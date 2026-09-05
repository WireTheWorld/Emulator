<div align="center">
  <img src="./Logo.png" alt="logo" width="100" />
  <h2>CircuitJS1</h2>
  <h3>Electronic circuit simulator that runs in the browser</h3>
</div>

<p align="center">🌐 <a href="./README.en.md">English</a> | <a href="./README.md">中文</a></p>

### 1. Features
- Runs entirely in the browser: Java compiled to JavaScript via GWT, no installation required
- Rich component library: resistors, capacitors, inductors, diodes, transistors, op-amps, logic gates, chips, transformers, motors, speakers, and hundreds more
- Live simulation: drag and modify components while the circuit is running, with instant feedback
- Measurement tools: built-in oscilloscope (with spectrum), voltmeter, ammeter, wattmeter, etc.
- Circuit import/export: supports URL-compressed data, local files, and Dropbox
- Multi-language UI: Chinese, English, and more

### 2. Quick Start
Visit the hosted versions:
* http://www.falstad.com/circuit/
* http://lushprojects.com/circuitjs/

Run locally (recommended for development, Maven + GWT):
```bash
# 1. Compile Java sources (requires JDK 8)
mvn compile

# 2. Compile the GWT JavaScript bundle (outputs to war/circuitjs1/, takes a few minutes)
JAVA_HOME=<path-to-your-jdk-8> mvn gwt:compile

# 3. Serve the war/ directory and open in a browser
# http://localhost:<port>/circuitjs.html
```

### 3. Build Environment
| Tool | Version | Notes |
|------|---------|-------|
| JDK | 8 | The GWT 2.7.0 compiler only runs on Java 8 |
| Maven | 3+ | Project converted to Maven build (`pom.xml`) |
| IntelliJ IDEA | any | Recommended IDE, auto-detects Maven projects |

The classic workflow with Eclipse + the GWT plugin is also supported; see `INTERNALS.md` in this repository.

### 4. Embedding & Parameters (API Overview)
The app can be embedded via iframe and supports the following URL parameters:

| Parameter | Description |
|-----------|-------------|
| `cct` | Load a circuit from the URL (like `#` in the Java version) |
| `ctz` | Load a circuit from compressed data in the URL |
| `startCircuit` | Load circuit "filename" from the Circuits directory |
| `startCircuitLink` | Load a circuit from the given URL (must support client-side CORS) |
| `euroResistors` | `true` to force European-style resistors |
| `usResistors` | `true` to force US-style resistors |
| `whiteBackground` | `true`/`false` to toggle white background |
| `conventionalCurrent` | `true`/`false` to toggle current direction |
| `running` | `true`/`false` start with simulation running, default `true` |
| `hideSidebar` | `true`/`false` hide the sidebar, default `false` |
| `hideMenu` | `true`/`false` hide the menu, default `false` |
| `editable` | `true`/`false` allow circuit editing, default `true` |
| `positiveColor` | Positive voltage color (`%23rrggbb`) |
| `negativeColor` | Negative voltage color (`%23rrggbb`) |
| `hideInfoBox` | `true`/`false` hide the info box |

Example:
```
.../circuitjs.html?startCircuit=4001.txt&whiteBackground=true
```

### 5. Deployment
1. Run the GWT compile as described above.
2. Copy everything under `war/` except the `WEB-INF` directory to your web server.
3. Optionally customize the `circuitjs1.html` header (analytics, favicon, etc.) and `iframe.html` (branding in the right panel).
4. For short URLs, deploy the optional `shortrelay.php` script; for Dropbox, configure a Dropbox API key.

Directory layout:
```
-+ directory containing the homepage (e.g. "circuitjs")
  +- circuitjs.html - full-page app
  +- iframe.html - branding
  +- shortrelay.php - short-link relay (optional)
  ++ circuitjs1 (directory)
   +- GWT build output
   +- circuits (sample circuits)
   +- setuplist.txt (index of sample circuits)
```

### 6. Building the Electron App
1. Compile the app with GWT as described above.
2. Download and unpack the [pre-built Electron binaries](https://github.com/electron/electron/releases) (version 9.3.2) for your target platform.
3. Copy the `app` directory from this repository to the location specified in the Electron distribution structure.
4. Copy the compiled `war` directory into the `app` directory in the Electron structure.
5. Run the `Electron` executable; it should load CircuitJS1 automatically.

Known limitations:
* "Create short URL" in "Export as URL" does not work because it depends on server support.
* "Create Simple Model" for diodes does not work because it depends on an unsupported JavaScript feature.

### 7. Acknowledgements
This project is localized and improved on top of the upstream repository [sharpie7/circuitjs1](https://github.com/sharpie7/circuitjs1).

### 8. License
This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any later version. See `COPYING.txt` in this repository.