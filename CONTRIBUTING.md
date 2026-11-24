# Contributing to PS Diagram

Thanks for your interest! PS Diagram uses trunk-based development:

1. Fork the repository and create a branch from `master`.
2. Make your change. The build must pass: `./mvnw -B clean package` (`mvnw.cmd` on Windows). It needs only a JDK 11;
   the Maven Wrapper downloads the right Maven (3.9.9). On Linux/macOS it also needs `unzip`: without it, `mvnw`
   fetches the `.tar.gz` instead, which fails the pinned SHA-256 check ("Maven distribution might be compromised").
3. Open a pull request into `master`. CI builds and tests it automatically.

Please write commit messages with one of the prefixes `ADDED` / `CHANGED` / `FIXED` / `ENHANCED`. The UI texts are in Czech.
Releases are made by the maintainer and tagged `vX.Y.Z`.
