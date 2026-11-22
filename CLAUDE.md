# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

PS Diagram is a Czech desktop app (Java 11 / Swing) for drawing, animating and debugging flowcharts, with conversion between
flowcharts and Pascal / Java source code. The working branch is `develop`, and releases are merged to `master`.
The website and update server live in the sibling repo `../ps-diagram-web` (see `../CLAUDE.md`).

## Build & test

```
mvn -B clean package                      # default profile = "development"
mvn -B test -Dtest=PSDToJavaScriptTest    # single test class
mvn -B test -Dtest=PSDToJavaScriptTest#methodName
```

Known build blockers (as of Nov 2026, not fixed in the pom):
- `versions-maven-plugin` 2.8.1 is bound to `validate`. Its `display-plugin-updates` goal throws an NPE on Maven 3.9.x, so
  **every** `mvn` invocation that goes through the lifecycle fails before compiling. It has no skip property. The fix is to bump
  the plugin version or remove the execution. Ask the user before you change the pom.
- `XMLtoHTMLConversionTest.conversionTestGlobal` reads `$PSD_DATA_DIR + "versioninfo.xml"`. The env var must end with `/`.
  It fails when the directory isn't available (e.g. in the devcontainer, where PSD_data on the NAS isn't reachable).
  `conversionTestBuild` runs only when `-Dbuild.versioninfo` is set.

The default build produces only a thin `target/psdiagram-<ver>.jar` with no Main-Class. To run the app:
```
mvn -B -q dependency:build-classpath -Dmdep.outputFile=cp.txt
java -cp "target/psdiagram-<ver>.jar:$(cat cp.txt)" cz.miroslavbartyzal.psdiagram.app.Main
```
At startup `MainWindow` pings the server (`TimeCollector`). When the network is blocked this logs a handled
`NoRouteToHostException`, which is harmless.

### Release profile (`-P deployment`): Windows only
It builds the shaded `-with_dependencies` jar with Main-Class and Build-Number (`buildnumber-maven-plugin` increments
`buildNumber.properties`). Using `versioninfo-maven-plugin` (the author's own plugin), it merges the release notes from the pom
into `$PSD_DATA_DIR/versioninfo.xml`. Antrun then packages `target/OPENSHIFT_DATA/` from these parts:
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

- Work on `feature/<name>` branches and open PRs into `develop` (ask before pushing). Always use the `feature/` prefix,
  because branches without it are protected.
- UI texts, comments and many identifiers are in Czech, and the commit messages use the prefixes `ADDED` / `CHANGED` / `FIXED` / `ENHANCED` /
  `RELEASING`.
- The client talks to `http://www.psdiagram.cz` over **plain HTTP**. Its HTTP client doesn't follow http→https redirects, so the
  server keeps these endpoints on HTTP. Don't switch the URL to https without changing the server side too.
