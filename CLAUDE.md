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

Notes:
- CI (`.github/workflows/build.yml`) runs `mvn -B clean package` on Java 11 for pushes and PRs to `master`.
- Release-only checks live in the `release` profile, and the default build doesn't run them:
  - the `versions-maven-plugin` update reports (run them manually with e.g. `mvn versions:display-dependency-updates`)
  - the versioninfo tests (`XMLtoHTMLConversionTest`, `CurrentCondenserParseTest`). They run only when the system property
    `build.versioninfo` is set, which the release profile does via surefire. The `...Global` tests read
    `$PSD_DATA_DIR + "versioninfo.xml"` (the env var must end with `/`).
- If tests fail with `NoClassDefFoundError` for classes that exist, the IDE probably rebuilt `target/` during the run. Rerun.

The default build produces only a thin `target/psdiagram-<ver>.jar` with no Main-Class. To run the app:
```
mvn -B -q dependency:build-classpath -Dmdep.outputFile=cp.txt
java -cp "target/psdiagram-<ver>.jar:$(cat cp.txt)" cz.miroslavbartyzal.psdiagram.app.Main
```
At startup `MainWindow` pings the server (`TimeCollector`). When the network is blocked this logs a handled
`NoRouteToHostException`, which is harmless.

### Release profile (`-P release`): Windows only
It builds the shaded `-with_dependencies` jar with Main-Class and Build-Number (`buildnumber-maven-plugin` increments
`buildNumber.properties`). Using `versioninfo-maven-plugin` (the author's own plugin), it merges the release notes from the pom
into `$PSD_DATA_DIR/versioninfo.xml`. Antrun then packages `target/PSD_DATA/` from these parts:
- `PS_Diagram_<ver>.zip`, which bundles the JRE from a hard-coded `c:\Program Files (x86)\Eclipse Adoptium\...` path, plus
  `PS Diagram.exe` (a separate C# launcher)
- `PSD-update_<ver>.zip`, which contains `psdiagram.updater` from the local Maven repo (built from `../ps-diagram-updater`, which must be `mvn install`ed with the same version)
- the user manual PDF

Neither these plugins nor the installed updater artifact are available here, so this profile cannot run in the container. The release notes for the next
version are edited in the `versioninfo-maven-plugin` `<configuration>` in `pom.xml` (Czech HTML).

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
