# CircuitJS1

## 简介

CircuitJS1 是一个运行在浏览器中的电子电路模拟器。它最初由 Paul Falstad 以 Java Applet 的形式编写，后来由 Iain Sharp 使用 GWT 移植为浏览器版本。

托管版本的应用程序参见：

* Paul 的页面：[http://www.falstad.com/circuit/](http://www.falstad.com/circuit/)
* Iain 的页面：[http://lushprojects.com/circuitjs/](http://lushprojects.com/circuitjs/)

致谢：Edward Calver 提供了 15 个新元件及其他改进；Rodrigo Hausen 提供了文件导入/导出和许多其他 UI 改进；J. Mike Rollins 提供了齐纳二极管代码；Julius Schmidt 提供了火花隙代码和一些示例；Dustin Soodak 帮助改进了用户界面；Jacob Calvert 提供了 T 触发器；Ben Hayden 提供了示波器频谱；Thomas Reitinger、Krystian Sławiński、Usevalad Khatkevich、Lucio Sciamanna、Mauro Hemerly Gazzani、J. Miguel Silva 和 Franck Viard 提供了翻译；Andre Adrian 改进了发射极耦合振荡器；Felthry 提供了许多示例；Colin Howell 改进了代码。LZString (c) 2013 pieroxy。

## 构建 Web 应用程序

### IntelliJ IDEA + Maven（推荐）

项目已转换为 Maven 构建（参见 `pom.xml`）。要在 IntelliJ IDEA 中开发：

1. 安装 JDK 8（项目使用 GWT 2.7.0，其编译器只能在 Java 8 上运行）。
2. 在 IntelliJ IDEA 中打开项目文件夹（File -> Open -> 选择 `Emulator` 文件夹）。
   IDEA 会自动将 `pom.xml` 识别为 Maven 项目。`.idea/` 文件夹和
   `circuitjs1.iml` 已包含在内，但 IDEA 在首次打开时会从 Maven 重新导入。
3. 将项目 SDK 设置为 JDK 8：File -> Project Structure -> Project SDK -> 1.8。
4. 使用 Maven 工具窗口或终端构建 Java 源码：
   ```
   mvn compile
   ```
5. 编译 GWT JavaScript 包（这才是实际在浏览器中运行的部分）：
   ```
   JAVA_HOME=<你的 JDK 8 路径> mvn gwt:compile
   ```
   输出位于 `war/circuitjs1/`。GWT 编译很慢（需要几分钟），因此
   它没有绑定到正常的 Maven 生命周期——每次修改 Java 源码后需显式执行。
6. 托管 `war/` 目录（任意静态文件服务器均可），然后打开
   `http://localhost:<端口>/circuitjs.html`。或者运行 Electron 应用（见下文），
   它会直接加载 `war/circuitjs.html`。

### 传统方式：Eclipse + GWT 插件

构建该项目需要的工具：

* Eclipse，Oxygen 版本。
* 用于 Eclipse 的 GWT 插件。

从[这里](https://www.eclipse.org/downloads/packages/)安装 "Eclipse for Java developers"。要为 Eclipse 添加 GWT 插件，请按照[这里](https://gwt-plugins.github.io/documentation/gwt-eclipse-plugin/Download.html)的说明操作。

本仓库是 Eclipse 项目空间的一个项目文件夹。拿到本地副本后，你可以在开发模式下构建并运行，也可以为部署而构建。在超级开发模式下运行：点击工具栏上的 "run" 图标，然后从出现的 "Development Mode" 选项卡中选择 http://127.0.0.1:8888/circuitjs.html。为部署而构建：选择项目根节点，使用 Eclipse 任务栏上的 GWT 按钮，选择 "GWT Compile Project..."。

GWT 将构建输出放在 "war" 目录中。在 "war" 目录中，文件 "iframe.html" 作为 iFrame 被加载到右侧面板底部的空白区域中。可用于品牌定制等。

## Web 应用程序的部署

* 如上所述执行 "GWT Compile Project..."。这会将输出放入 Eclipse 项目文件夹中的 "war" 目录。然后你需要将 "war" 目录中的所有内容（"WEB-INF" 目录除外）复制到你的 Web 服务器上。
* 自定义文件 "circuitjs1.html" 的头部，加入你的统计代码、favicon 等。
* 自定义 "iframe.html" 文件，添加你希望在应用程序右侧面板中显示的任何品牌内容。
* 可选文件 "shortrelay.php" 是一个服务端脚本，用于作为 URL 缩短服务的中继，以避免纯客户端方案的跨域问题。你可能需要为此自定义站点。如果不想使用此功能，请在编译前编辑 circuitjs1.java 文件。
* 如需启用 dropbox 加载和保存，需要 dropbox API 应用密钥。应在需要处将其编辑到 circuitjs.html 文件中。如果未包含，相关功能将被禁用。

全页版本应用程序的链接现在是：
`http://<你的主机>/<你的路径>/circuitjs1.html`
（如果你愿意，也可以重命名 "circuitjs1.html" 文件，但同时也应更新 "shortrelay.php"）。

仅供参考，文件结构应如下所示：

```
-+ 包含首页的目录（例如 "circuitjs"）
  +- circuitjs.html - 应用程序的全页版本
  +- iframe.html - 参见上面的说明
  +- shortrelay.php - 参见上面的说明
  ++ circuitjs1（目录）
   +- GWT 构建生成的各种文件
   +- circuits（目录，包含示例电路）
   +- setuplist.txt（示例电路目录的索引）
```

## 嵌入

你可以使用上面显示的链接链接到应用程序的全页版本。

如果你想在其他页面中嵌入该应用程序，请使用 src 为全页版本的 iframe。

你可以添加查询参数来改变应用程序的启动行为。支持以下参数：
```
.../circuitjs.html?cct=<字符串> // 从 URL 加载电路（类似于 Java 版本中的 #）
.../circuitjs.html?ctz=<字符串> // 从 URL 中的压缩数据加载电路
.../circuitjs.html?startCircuit=<文件名> // 从 "Circuits" 目录加载名为 "filename" 的电路
.../circuitjs.html?startCircuitLink=<URL> // 从指定的 URL 加载电路。目前该 URL 必须是 DROPBOX 共享文件或其他支持客户端 CORS 访问的 URL
.../circuitjs.html?euroResistors=true // 设置为 true 以强制使用 "欧式" 风格电阻。如果未指定，电阻风格将基于用户浏览器的语言偏好
.../circuitjs.html?usResistors=true // 设置为 true 以强制使用 "美式" 风格电阻。如果未指定，电阻风格将基于用户浏览器的语言偏好
.../circuitjs.html?whiteBackground=<true|false>
.../circuitjs.html?conventionalCurrent=<true|false>
.../circuitjs.html?running=<true|false> // 不以运行状态启动应用程序，默认为 true
.../circuitjs.html?hideSidebar=<true|false> // 隐藏侧边栏，默认为 false
.../circuitjs.html?hideMenu=<true|false> // 隐藏菜单，默认为 false
.../circuitjs.html?editable=<true|false> // 允许编辑电路，默认为 true
.../circuitjs.html?positiveColor=%2300ff00 // 更改正电压颜色 (rrggbb)
.../circuitjs.html?negativeColor=%23ff0000 // 更改负电压颜色
.../circuitjs.html?hideInfoBox=<true|false>
```
## 构建 Electron 应用程序

[Electron](https://electronjs.org/) 项目允许将 Web 应用程序作为适用于各种平台的本地可执行文件分发。本仓库包含构建 circuitJS1 为 Electron 应用程序所需的额外文件。

构建针对特定平台的 Electron 应用程序的通用方法在[这里](https://electronjs.org/docs/tutorial/application-distribution)有文档说明。以下说明将该方法应用于 circuit JS。

构建 Electron 应用程序：
* 如上所述，使用 GWT 编译应用程序。
* 为目标平台下载并解压[预构建的 Electron 二进制目录](https://github.com/electron/electron/releases)版本 9.3.2。
* 按照[这里](https://electronjs.org/docs/tutorial/application-distribution)的说明，将本仓库中的 "app" 目录复制到 Electron 二进制目录结构中指定的位置。
* 将包含已编译 CircuitJS1 应用程序的 "war" 目录复制到 Electron 二进制目录结构中的 "app" 目录中。
* 运行 "Electron" 可执行文件。它应自动加载 CircuitJS1。

Electron 应用程序的已知限制：
* 在 "Export as URL" 中 "Create short URL" 不起作用，因为它依赖服务器支持。
* 对于二极管，"Create Simple Model" 不起作用，因为它依赖一个不受支持的 JavaScript 特性。

感谢 @Immortalin 对将 Electron 应用于 CircuitJS1 的初步工作。

## 许可证

本程序是自由软件；你可以根据自由软件基金会发布的 GNU 通用公共许可证的条款（许可证第 2 版，或（由你选择）任何更高版本）重新分发和/或修改它。

分发本程序是希望它有用，但不提供任何保证；甚至没有对适销性或特定用途适用性的默示保证。参见 GNU 通用公共许可证了解更多详情。

你应该已经收到一份 GNU 通用公共许可证的副本，随本程序一同分发；如果没有，请写信给自由软件基金会，地址：51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA。