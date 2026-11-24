# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

PS Diagram is a Czech desktop app (Java 11 / Swing) for drawing, animating and debugging flowcharts, with conversion between
flowcharts and Pascal / Java source code. Development is trunk-based on `master` (protected), and releases are tags `vX.Y.Z` on `master`.
The website and update server live in the sibling repo `../ps-diagram-web` (see `../CLAUDE.md`).

## Build & test

```
mvn -B clean package                      # default profile = "development"
mvn -B test -Dtest=PSDToJavaScriptTest    # single test class
mvn -B test -Dtest=PSDToJavaScriptTest#methodName
```
Maven comes from the Maven Wrapper (`mvnw` / `mvnw.cmd`, pinned to 3.9.9 with a SHA-256). On the dev PC, `mvn` means
`..\tools\bin\mvn.cmd` (JDK 11 + the repo's `mvnw.cmd`; see `../CLAUDE.md`). Elsewhere, use `./mvnw` with a JDK 11
and `unzip` (without `unzip`, `mvnw` fetches the `.tar.gz`, which fails the SHA-256 pinned for the `.zip`).

Notes:
- CI (`.github/workflows/build.yml`) runs `./mvnw -B clean package` on Java 11 for pushes and PRs to `master`.
- Release-only checks live in the `release` profile, and the default build doesn't run them:
  - the `versions-maven-plugin` update reports (run them manually with e.g. `mvn versions:display-dependency-updates`)
  - the versioninfo tests (`XMLtoHTMLConversionTest`, `CurrentCondenserParseTest`). They run only when the system property
    `build.versioninfo` is set, which the release profile does via surefire. The `...Global` tests read
    `$PSD_DATA_DIR + "versioninfo.xml"` (the env var must end with `/`).
- If tests fail with `NoClassDefFoundError` for classes that exist, the IDE probably rebuilt `target/` during the run. Rerun.

The default build produces only a thin `target/psdiagram-<ver>.jar` with no Main-Class. To run the app:
```
mvn -B -q dependency:build-classpath -Dmdep.outputFile=cp.txt
java -cp "target/psdiagram-<ver>.jar;$(Get-Content cp.txt)" cz.miroslavbartyzal.psdiagram.app.Main   # PowerShell
```
(On Linux, use `:` as the separator and `$(cat cp.txt)`.)
At startup `MainWindow` pings the server (`TimeCollector`). When the network is blocked this logs a handled
`NoRouteToHostException`, which is harmless.

### Release profile (`-P release`): Windows only
It builds the shaded `-with_dependencies` jar with Main-Class and Build-Number (`buildnumber-maven-plugin` increments
`buildNumber.properties`). Using `versioninfo-maven-plugin` (the author's own plugin), it merges the release notes from the pom
into `$PSD_DATA_DIR/versioninfo.xml`. Antrun then packages `target/PSD_DATA/` from these parts:
- `PS_Diagram_<ver>.zip`, which bundles the JRE given by `-Dpsd.jre.dir=<unpacked JRE>` (the build fails without it),
  plus the launcher `tools/launcher/dist/PS Diagram.exe`. The bundled JRE is a 32-bit Java 11 (so it also runs on 32-bit
  Windows); since Temurin has no 32-bit Windows builds after 11.0.29, it is Azul Zulu (pinned on the dev PC, see
  `../CLAUDE.md`)
- `PSD-update_<ver>.zip`, which contains `psdiagram.updater` from the local Maven repo (built from `../ps-diagram-updater`, which must be `mvn install`ed with the same version)
- the user manual PDF

Before a release build, `mvn -B -f tools install` the plugins (see "Build tools" below) and `mvn install` the updater. The
release notes for the next version are edited in the `versioninfo-maven-plugin` `<configuration>` in `pom.xml` (Czech
HTML), written only at release time. On the dev PC the whole release (build, local rehearsal, publishing) is scripted:
see `../RELEASING.md`.

## Build tools (`tools/`)
Separate builds, not modules of the root pom, so the default build ignores them. They were imported in 2026-11 from the
author's old plain folders (no earlier git history).
- `versioninfo-maven-plugin` (1.2) and `directoryfootprint-maven-plugin` (1.3): the author's own Maven plugins used by the
  release profile. `mvn -B -f tools install` builds both and installs them into `~/.m2`. CI builds them too.
  `versioninfo-maven-plugin` has its own copy of the `ChangesCondenser` JAXB classes, which must stay compatible with
  `update/` here and with the web repo.
- `launcher/`: `PS Diagram.exe`, a small native Win32 C program (it replaced a C# .NET 2.0 launcher, which made users install
  .NET 3.5; that version is in the git history).
  - It finds `psdiagram.jar` in the current dir, then next to the exe, and starts it with `jre\bin\javaw.exe`, else
    `%JAVA_HOME%\bin\javaw.exe`, else (after a warning) through the shell's `.jar` association. The arguments are passed
    through as the raw command line. Keep this behaviour and the Czech messages.
  - Keep the exe name: `JFrameSettings` file associations point at `PS Diagram.exe`.
  - `tools/launcher/build.ps1` builds it on Windows with the pinned WinLibs MinGW-w64 i686 (gcc 16.2.0, mingw-w64 14.0.0,
    msvcrt; version, URL and SHA-256 in the script header). On the dev PC, `setup.ps1` installs it to `..\tools\mingw32`,
    the script's default; elsewhere pass `-MingwBin` or set `PSD_MINGW_BIN`. The build is deterministic (same bytes in
    any folder, checked 2026-11-24), and `dist/PS Diagram.exe` is committed. Rebuild and commit the exe **only when its
    source changes**, so the hash stays the same and keeps its antivirus/SmartScreen reputation. `build.ps1 -Check`
    rebuilds into `build\` and fails if the result differs from `dist\`; run it after any change under `launcher/` or a
    toolchain change. There is no CI check (by the user's decision, the exe doesn't depend on GitHub).
  - The toolchain is part of the exe's identity: upgrading WinLibs changes the bytes, so do it only together with a
    launcher change, and repeat the checks below. WinLibs adds a default manifest (`default-manifest.o`) to every exe;
    `build.ps1` replaces it with an empty object (`-B build/nomanifest/`), so only `launcher.manifest` is embedded.
  - The exe is **not code-signed**, by the user's decision (no budget for paid signing; free SignPath Foundation signing
    would need an OSI license for the repo). Keep it plain to avoid false positives: no packer/UPX, keep the version
    resource and manifest.
  - **After building a new exe** (whenever `dist/PS Diagram.exe` changes), before it goes into a release:
    1. Windows test (Claude on the dev PC, or the user): put it into an unpacked release zip and check these cases: bundled JRE,
       `JAVA_HOME` only, no JRE (warning), no jar (error, exit code 1), a file argument with spaces/diacritics. Also run a
       Microsoft Defender scan. Run the cases with `JAVA_TOOL_OPTIONS=-Duser.home=<a temp dir>` set (the launcher passes
       the environment on to javaw). Otherwise the app writes to the real `%USERPROFILE%\.psdiagram`: `settings.xml`
       remembers the test file as the last flowchart. Each force-killed instance also leaves a crash-recovery
       `backup_N.xml`, which the next start reopens in an extra "(zotaveno)" window (seen 2026-11-24).
    2. Upload the exe to [VirusTotal](https://www.virustotal.com/gui/home/upload) (the user agreed this is a standard
       step; uploading publishes the file, which is in this public repo anyway). Check that the SHA-256 matches the build.
    3. For every engine that flags it, check the vendor's own scanner first. For Kaspersky that's
       https://opentip.kaspersky.com/: upload the exe and wait for the dynamic analysis (~3 min). If the vendor says it's
       clean, the hit is only from that engine's VirusTotal configuration: re-run "Reanalyze" on VirusTotal after a few
       days. If the vendor's own scanner detects it, send a false-positive report through it (Kaspersky shows a "send
       for research" option with an e-mail field, which the user fills in). Once whitelisted, the hash stays clean
       across releases until the source changes.
    4. Note the result (hash, detections, reports sent) in the commit message or PR description.
  - History: the 2026-11-22 exe `3e9adfe7…e031` scored 1/71 on VirusTotal: only Kaspersky flagged it, with the heuristic
    `VHO:Trojan-Downloader.Win32.Upatre.gen` (a tiny exe that starts another program). Kaspersky's own OpenTIP rated it
    Clean the same day (0 detections, no suspicious activity), so no report was needed.
    The old C# exe scored 1/69 (SecureAge). On 2026-11-24 the reference toolchain moved from Debian bookworm MinGW to
    WinLibs on Windows (same flags), and the JRE warning's line break was fixed (break between the sentences, a
    non-breaking space in "PS Diagram"). The new exe is `af16e289…8b78` (Windows test cases and Defender passed the same
    day; VirusTotal 2026-11-24: 0/69, Kaspersky included). Neither the old exe nor this one had been released yet. Its
    imports are the same DLLs as before (KERNEL32, msvcrt, SHELL32, USER32); a few msvcrt entry points changed, all of
    which exist on Windows 7.

## Architecture (package `cz.miroslavbartyzal.psdiagram.app`)

- `flowchart/`: the model. A `Flowchart` is a tree of `FlowchartSegment`s holding `FlowchartElement`s, and each element wraps a
  symbol from `symbols/` (process, decision, loops, I/O, …). `layouts/` positions the symbols, and there are several
  layout styles.
- `gui/`: Swing UI. `MainWindow` is the central class (the NetBeans GUI-builder form, so don't hand-edit the generated
  `initComponents` blocks). `managers/` hold edit/undo (`undoableEdits/`) and other mode logic. `symbolFunctionForms/` are
  the per-symbol editors, and `documentFilters/` validate the expression syntax that users type.
- Expression language: users write commands in a "PSD" pseudo-language, defined by the ANTLR4 grammars in
  `src/main/antlr4/.../parser/psd/`. Parsers are generated at build time (`antlr4-maven-plugin`). Don't commit the generated `gen/`.
  - `parser/psd` validates the expressions and translates them to JavaScript (`parser/javascript`).
  - `debug/` runs the flowchart step by step: `DebugAnimator` drives the animation, and `debug/function` evaluates the translated
    JavaScript with Rhino (`rhino-runtime`) and tracks variables and scopes.
- Code import/export: `codeImportExport/` (`Pascal`, `Java`) and `parser/` (`CodeToFlowchartConvertor`,
  `FlowchartGenerator`, `SourceCodeGenerator`). Java import uses the `parser/java` grammar. The `parser/c` grammar exists but isn't wired up
  as a feature.
- `persistence/`: XML (JAXB / EclipseLink MOXy) save/load, crash recovery, and `collector/FlowchartCollector`, which
  POSTs the user's flowcharts to the server (`/c`).
- `update/`: `Updater` fetches `/versioninfo` and downloads updates via `/download`, then hands off to the external
  `updater.jar` (Windows `cmd`). `ChangesCondenser` and friends are the JAXB model of `versioninfo.xml`, which must stay in sync
  with the web repo's copy of the same classes.
- `global/SettingsHolder`: global constants and user settings. `PSDIAGRAM_SERVER`, version, build number and build profile come from
  the Maven-filtered `src/main/resources/application.properties` (`psdiagram.web.url`, `build.profile`).
- `src/main/resources/intozip/` holds files shipped next to the jar in the release zip (examples, user manual), and isn't part of the jar.

## Conventions

- Work on short-lived `feature/<name>` branches and open PRs into `master` (ask before pushing). Always use the `feature/`
  prefix, because branches without it are protected. Outside contributors open PRs from their forks.
- Releases: one commit sets the release version (`RELEASING new version X.Y.Z`), it gets the tag `vX.Y.Z` on `master`, and the
  next commit bumps the version back to `-SNAPSHOT`. Older tags are named `version_X.Y.Z`. If an already released version needs
  a hotfix while `master` holds unreleased work, branch `release/X.Y.x` from its tag at that point, tag the fix there, and
  cherry-pick it to `master`.
- UI texts, comments and many identifiers are in Czech, and the commit messages use the prefixes `ADDED` / `CHANGED` / `FIXED` / `ENHANCED` /
  `RELEASING`.
- The client talks to `http://www.psdiagram.cz` over **plain HTTP**. Its HTTP client doesn't follow http→https redirects, so the
  server keeps these endpoints on HTTP. Don't switch the URL to https without changing the server side too.
