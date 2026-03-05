#!/bin/sh
# install-internal-deps.sh
#
# Installs internal/vendor JARs into the local Maven repository using the
# coordinate-to-file mapping in .github/internal-artifact-mapping.txt.
#
# If the JAR file listed in the mapping does not exist on disk, the script
# creates a TEMPORARY dummy JAR (containing only META-INF/README.txt) and
# installs that instead.  This allows the Maven build to resolve all declared
# dependencies in CI even when the real binaries are not yet committed.
#
# IMPORTANT: Dummy JARs are purely a CI convenience.  They MUST be replaced
# with the genuine artifacts before any production build or release.
# See CONTRIBUTING.md for instructions on replacing dummy JARs.
#
# The script exits non-zero only when an actual `mvn install:install-file`
# invocation fails; creating or installing a dummy JAR is considered a
# successful (though degraded) outcome.
#
# Mapping file format (pipe-separated, 6 fields per line):
#   relative-path|groupId|artifactId|version|packaging|classifier
# The classifier field is optional and may be left empty.

set -u

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
MAPPING_FILE="${SCRIPT_DIR}/../internal-artifact-mapping.txt"

REAL_COUNT=0
DUMMY_COUNT=0
FAIL_COUNT=0

if [ ! -f "${MAPPING_FILE}" ]; then
    echo "ERROR: Mapping file not found: ${MAPPING_FILE}" >&2
    exit 1
fi

# ---------------------------------------------------------------------------
# create_dummy_jar <output_jar> <groupId> <artifactId> <version>
#
# Writes a minimal JAR archive containing META-INF/README.txt to <output_jar>.
# Requires either the JDK `jar` tool or Python 3/2 to be available.
# ---------------------------------------------------------------------------
create_dummy_jar() {
    _out_jar="$1"
    _group="$2"
    _artifact="$3"
    _version="$4"

    _tmp_dir="$(mktemp -d)"
    mkdir -p "${_tmp_dir}/META-INF"

    # Write a human-readable notice into the dummy archive.
    cat > "${_tmp_dir}/META-INF/README.txt" << EOREADME
DUMMY JAR - TEMPORARY PLACEHOLDER
==================================
This archive was generated automatically by install-internal-deps.sh because
the real JAR was not found on disk.

Artifact : ${_group}:${_artifact}:${_version}
Generated: $(date -u '+%Y-%m-%dT%H:%M:%SZ')

This dummy MUST be replaced with the genuine JAR before production use.
See CONTRIBUTING.md for replacement instructions.
EOREADME

    # Try the JDK jar tool first, then fall back to Python.
    if command -v jar > /dev/null 2>&1; then
        jar cf "${_out_jar}" -C "${_tmp_dir}" META-INF
        _rc=$?
    elif command -v python3 > /dev/null 2>&1; then
        python3 - "${_out_jar}" "${_tmp_dir}/META-INF/README.txt" << 'EOPY'
import sys, zipfile
out_jar, readme = sys.argv[1], sys.argv[2]
with zipfile.ZipFile(out_jar, 'w', zipfile.ZIP_DEFLATED) as zf:
    zf.write(readme, 'META-INF/README.txt')
EOPY
        _rc=$?
    elif command -v python > /dev/null 2>&1; then
        python - "${_out_jar}" "${_tmp_dir}/META-INF/README.txt" << 'EOPY'
import sys, zipfile
out_jar, readme = sys.argv[1], sys.argv[2]
with zipfile.ZipFile(out_jar, 'w', zipfile.ZIP_DEFLATED) as zf:
    zf.write(readme, 'META-INF/README.txt')
EOPY
        _rc=$?
    else
        echo "  ERROR: Cannot create dummy JAR - neither 'jar' nor 'python3'/'python' found." >&2
        _rc=1
    fi

    rm -rf "${_tmp_dir}"
    return ${_rc}
}

# ---------------------------------------------------------------------------
# Main loop: read the mapping file and install each artifact.
# ---------------------------------------------------------------------------
echo "Installing internal/vendor JARs from ${MAPPING_FILE} ..."
echo ""

while IFS= read -r _line; do
    # Strip leading whitespace and skip blank lines and comments.
    _line="$(printf '%s' "${_line}" | sed 's/^[[:space:]]*//')"
    case "${_line}" in
        ''|'#'*) continue ;;
    esac

    # Parse the pipe-separated fields:
    #   relative-path|groupId|artifactId|version|packaging|classifier
    _jar_rel="$(    printf '%s' "${_line}" | cut -d'|' -f1)"
    _group_id="$(   printf '%s' "${_line}" | cut -d'|' -f2)"
    _artifact_id="$(printf '%s' "${_line}" | cut -d'|' -f3)"
    _version="$(    printf '%s' "${_line}" | cut -d'|' -f4)"
    _packaging="$(  printf '%s' "${_line}" | cut -d'|' -f5)"
    _classifier="$( printf '%s' "${_line}" | cut -d'|' -f6)"

    # Default packaging to "jar" if not specified.
    if [ -z "${_packaging}" ]; then
        _packaging="jar"
    fi

    if [ -z "${_jar_rel}" ] || [ -z "${_group_id}" ] || \
       [ -z "${_artifact_id}" ] || [ -z "${_version}" ]; then
        echo "  WARN: Skipping malformed mapping line: ${_line}" >&2
        continue
    fi

    _jar_abs="${REPO_ROOT}/${_jar_rel}"
    _install_file=""
    _is_dummy=0

    if [ -f "${_jar_abs}" ]; then
        # Real JAR found - install it directly.
        echo "[REAL ] ${_group_id}:${_artifact_id}:${_version}  <- ${_jar_rel}"
        _install_file="${_jar_abs}"
        REAL_COUNT=$((REAL_COUNT + 1))
    else
        # JAR missing - create and install a temporary dummy.
        # This is intentional CI behaviour; see file header for details.
        echo "[DUMMY] ${_group_id}:${_artifact_id}:${_version}  (${_jar_rel} not found - using dummy)"
        _dummy_jar="${REPO_ROOT}/libs/${_artifact_id}-${_version}-dummy.jar"

        if ! create_dummy_jar "${_dummy_jar}" \
                "${_group_id}" "${_artifact_id}" "${_version}"; then
            echo "  ERROR: Could not create dummy JAR for ${_group_id}:${_artifact_id}:${_version}" >&2
            FAIL_COUNT=$((FAIL_COUNT + 1))
            continue
        fi

        _install_file="${_dummy_jar}"
        _is_dummy=1
        DUMMY_COUNT=$((DUMMY_COUNT + 1))
    fi

    # Install into the local Maven repository.
    # Classifier is passed as an extra argument only when set to avoid eval.
    if [ -n "${_classifier}" ]; then
        if mvn --batch-mode org.apache.maven.plugins:maven-install-plugin:3.1.0:install-file \
                -DgroupId="${_group_id}" \
                -DartifactId="${_artifact_id}" \
                -Dversion="${_version}" \
                -Dpackaging="${_packaging}" \
                -Dfile="${_install_file}" \
                -DgeneratePom=true \
                -Dclassifier="${_classifier}" \
                -q; then
            echo "  OK: Installed ${_group_id}:${_artifact_id}:${_version}:${_classifier}"
        else
            echo "  ERROR: mvn install-file failed for ${_group_id}:${_artifact_id}:${_version}" >&2
            FAIL_COUNT=$((FAIL_COUNT + 1))
        fi
    else
        if mvn --batch-mode org.apache.maven.plugins:maven-install-plugin:3.1.0:install-file \
                -DgroupId="${_group_id}" \
                -DartifactId="${_artifact_id}" \
                -Dversion="${_version}" \
                -Dpackaging="${_packaging}" \
                -Dfile="${_install_file}" \
                -DgeneratePom=true \
                -q; then
            echo "  OK: Installed ${_group_id}:${_artifact_id}:${_version}"
        else
            echo "  ERROR: mvn install-file failed for ${_group_id}:${_artifact_id}:${_version}" >&2
            FAIL_COUNT=$((FAIL_COUNT + 1))
        fi
    fi

    # Remove the temporary dummy JAR from disk after installation.
    if [ "${_is_dummy}" -eq 1 ]; then
        rm -f "${_install_file}"
    fi

done < "${MAPPING_FILE}"

echo ""
echo "-------------------------------------------------"
echo "Summary: ${REAL_COUNT} real, ${DUMMY_COUNT} dummy (temporary), ${FAIL_COUNT} failed"
echo "-------------------------------------------------"

if [ "${DUMMY_COUNT}" -gt 0 ]; then
    echo "WARNING: ${DUMMY_COUNT} artifact(s) installed as dummy JARs." \
         "Replace them with real JARs before production use." >&2
fi

if [ "${FAIL_COUNT}" -gt 0 ]; then
    echo "ERROR: ${FAIL_COUNT} artifact(s) failed to install." >&2
    exit 1
fi

exit 0
