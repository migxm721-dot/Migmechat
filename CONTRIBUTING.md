# Contributing to Migmechat

Thank you for your interest in contributing!

## Internal / Vendor JAR Dependencies

Several dependencies used by this project are internal artifacts that are not
available in any public Maven repository.  They are sourced from the `libs/`
directory and installed into the local Maven repository during CI by the script
`.github/scripts/install-internal-deps.sh`.

### Temporary Dummy JARs

When a real JAR is absent from `libs/` (for example `configuration-1.0.5.jar`
or `mig33rabbitmq-client-0.5.0.jar`), the install script automatically creates
a **temporary dummy JAR** — a minimal ZIP archive containing only
`META-INF/README.txt` — and installs that in its place.

> **Important:** Dummy JARs are a CI convenience only.  Code that depends on
> classes from these artifacts will not compile or run correctly against a
> dummy.  They **must** be replaced with the genuine binaries before any
> production build or release.

### Replacing a Dummy with the Real JAR

1. Obtain the real JAR for the missing artifact.
2. Copy it to `libs/` using the filename shown in
   `.github/internal-artifact-mapping.txt`
   (e.g. `libs/configuration-1.0.5.jar`).
3. In `.github/internal-artifact-mapping.txt`, uncomment the corresponding
   mapping line (remove the leading `# `).
4. Open a pull request with the updated mapping file.
   **Do not commit binary JARs** unless they are small and genuinely required;
   prefer linking to an internal artifact registry instead.

### Adding a New Internal Artifact

1. Add the JAR to `libs/`.
2. Add a new line to `.github/internal-artifact-mapping.txt` in the format:
   ```
   groupId  artifactId  version  libs/filename.jar
   ```
3. Re-run `.github/scripts/install-internal-deps.sh` locally to verify the
   installation succeeds.
