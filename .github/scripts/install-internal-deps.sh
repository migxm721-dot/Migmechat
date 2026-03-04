#!/bin/sh
# install-internal-deps.sh
#
# Installs internal/private jar artifacts into the runner's local Maven
# repository so that `mvn clean package` can resolve them without access to
# a private Nexus instance.
#
# HOW TO USE:
#   1. Place each private jar under libs/ in the repository root, e.g.:
#        libs/com.projectgoth-configuration-1.0.5.jar
#   2. Add a corresponding line to .github/internal-artifact-mapping.txt
#      using the pipe-delimited format described in that file.
#   3. Commit both the jar and the updated mapping file.
#
# MAPPING FILE FORMAT (.github/internal-artifact-mapping.txt):
#   Each non-comment, non-blank line must have the form:
#     relative-jar-path|groupId|artifactId|version|packaging[|classifier]
#   The classifier field is optional; leave it empty or omit it.

set -e

REPO_ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
MAPPING_FILE="${REPO_ROOT}/.github/internal-artifact-mapping.txt"

if [ ! -f "${MAPPING_FILE}" ]; then
  echo "ERROR: Mapping file not found: ${MAPPING_FILE}" >&2
  exit 1
fi

echo "=== Installing internal artifacts into local Maven repository ==="
echo "    Mapping file : ${MAPPING_FILE}"
echo "    Repository root: ${REPO_ROOT}"
echo ""

installed=0
errors=0

while IFS= read -r line || [ -n "${line}" ]; do
  # Skip blank lines and comment lines (starting with #)
  case "${line}" in
    ''|\#*) continue ;;
  esac

  # Split on '|'
  jar_path="$(echo "${line}" | cut -d'|' -f1)"
  group_id="$(echo "${line}" | cut -d'|' -f2)"
  artifact_id="$(echo "${line}" | cut -d'|' -f3)"
  version="$(echo "${line}" | cut -d'|' -f4)"
  packaging="$(echo "${line}" | cut -d'|' -f5)"
  classifier="$(echo "${line}" | cut -d'|' -f6)"

  abs_jar_path="${REPO_ROOT}/${jar_path}"

  echo "---"
  echo "  Jar       : ${jar_path}"
  echo "  Group     : ${group_id}"
  echo "  Artifact  : ${artifact_id}"
  echo "  Version   : ${version}"
  echo "  Packaging : ${packaging}"
  if [ -n "${classifier}" ]; then
    echo "  Classifier: ${classifier}"
  fi

  if [ ! -f "${abs_jar_path}" ]; then
    echo "  WARNING: Jar file not found, skipping: ${abs_jar_path}" >&2
    errors=$((errors + 1))
    continue
  fi

  # Invoke mvn directly with properly quoted arguments to avoid eval and
  # prevent command injection from untrusted field values in the mapping file.
  if [ -n "${classifier}" ]; then
    echo "  Running: mvn install-file (with classifier)"
    mvn --batch-mode org.apache.maven.plugins:maven-install-plugin:3.1.0:install-file \
      "-Dfile=${abs_jar_path}" \
      "-DgroupId=${group_id}" \
      "-DartifactId=${artifact_id}" \
      "-Dversion=${version}" \
      "-Dpackaging=${packaging}" \
      "-Dclassifier=${classifier}" \
      && rc=0 || rc=$?
  else
    echo "  Running: mvn install-file"
    mvn --batch-mode org.apache.maven.plugins:maven-install-plugin:3.1.0:install-file \
      "-Dfile=${abs_jar_path}" \
      "-DgroupId=${group_id}" \
      "-DartifactId=${artifact_id}" \
      "-Dversion=${version}" \
      "-Dpackaging=${packaging}" \
      && rc=0 || rc=$?
  fi

  if [ "${rc}" -eq 0 ]; then
    echo "  SUCCESS: Installed ${group_id}:${artifact_id}:${version}"
    installed=$((installed + 1))
  else
    echo "  ERROR: Failed to install ${group_id}:${artifact_id}:${version}" >&2
    errors=$((errors + 1))
  fi

done < "${MAPPING_FILE}"

echo ""
echo "=== Install summary: ${installed} artifact(s) installed, ${errors} error(s) ==="

if [ "${errors}" -gt 0 ]; then
  echo "ERROR: One or more artifacts could not be installed." >&2
  exit 1
fi

exit 0
