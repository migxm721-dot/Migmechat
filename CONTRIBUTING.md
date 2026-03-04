# Migmechat (Fusion) – Contributing Guide

## Building locally

### Prerequisites

| Tool | Version |
|------|---------|
| JDK  | 17 (Temurin recommended) |
| Maven | 3.9+ |
| Access to internal Nexus | Required for normal local builds |

---

## Vendoring internal/private artifacts for CI builds

Several dependencies in `pom.xml` are hosted on the internal Projectgoth Nexus
server and are therefore not accessible from GitHub Actions runners. To enable
CI builds without modifying the POM or the ICE dependency, private jars are
committed into the `libs/` directory and installed into the runner's local
Maven repository before the build runs.

### Step-by-step: adding a new private jar

1. **Copy the jar** into `libs/` at the repository root:
   ```
   libs/my-private-lib-1.2.3.jar
   ```

2. **Add a mapping line** to `.github/internal-artifact-mapping.txt`:
   ```
   libs/my-private-lib-1.2.3.jar|com.example|my-private-lib|1.2.3|jar|
   ```
   The format is pipe-delimited:
   ```
   relative-jar-path|groupId|artifactId|version|packaging|classifier
   ```
   The `classifier` field is optional — leave it empty or omit it.

3. **Commit** both the jar file and the updated mapping file:
   ```bash
   git add libs/my-private-lib-1.2.3.jar .github/internal-artifact-mapping.txt
   git commit -m "vendor: add my-private-lib 1.2.3"
   ```

The CI workflow (`.github/workflows/ci-vendor-jars.yml`) will pick up the new
mapping automatically on the next run.

### How the CI workflow works

```
push / pull_request → main
  └─ checkout
  └─ setup-java (Temurin 17, Maven cache)
  └─ chmod +x .github/scripts/install-internal-deps.sh
  └─ .github/scripts/install-internal-deps.sh
       reads .github/internal-artifact-mapping.txt
       runs `mvn install-file` for each entry whose jar exists in libs/
  └─ mvn -B -U -DskipTests clean package
  └─ upload build logs (on failure)
```

### Important notes

* **Do not change the ICE dependency** in `pom.xml` or any ICE-related source
  files. The `pom.xml` uses profile-specific groupIds (`com.zeroc.mac` on
  macOS, `com.zeroc.linux` on Linux) with version `3.3.1`. The mapping file
  has entries for both; activate the relevant profile or ensure the correct
  platform jar is placed in `libs/`.
* The `libs/` directory is intentionally tracked by Git so that the vendor
  jars are available on fresh checkouts in CI.
* Do not upgrade dependency versions in this mechanism; its purpose is to
  enable CI testing of the current branch, not to perform upgrades.
* If a jar listed in the mapping file is missing from `libs/`, the install
  script will print a warning and exit with a non-zero status, failing the
  build so the gap is visible immediately.

### Mapping file reference

```
# .github/internal-artifact-mapping.txt
# Lines starting with '#' and blank lines are ignored.
relative-jar-path|groupId|artifactId|version|packaging|classifier
```

| Field | Description |
|-------|-------------|
| `relative-jar-path` | Path from repo root, e.g. `libs/foo-1.0.jar` |
| `groupId` | Maven groupId |
| `artifactId` | Maven artifactId |
| `version` | Maven version |
| `packaging` | Usually `jar` |
| `classifier` | Optional; leave empty if not needed |
