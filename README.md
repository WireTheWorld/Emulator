<div align="center">
  <img src="./Logo.png" alt="logo" width="100" />
  <h2>CircuitJS1</h2>
  <h3>运行在浏览器中的电子电路模拟器</h3>
</div>

<p align="center">🌐 <a href="./README.en.md">English</a> | <a href="./README.md">中文</a></p>

### 一、功能简介
- 纯浏览器运行：由 GWT 将 Java 编译为 JavaScript，打开网页即可仿真，无需安装
- 元件丰富：电阻、电容、电感、二极管、晶体管、运放、逻辑门、芯片、变压器、电机、扬声器等数百种元件
- 实时仿真：电路运行中可直接拖拽、改动元件，结果即时反馈
- 测量工具：内置示波器（含频谱）、电压表、电流表、功率表等
- 电路导入/导出：支持 URL 压缩数据、本地文件、Dropbox
- 多语言界面：支持中文、英文等

### 二、快速开始
直接访问托管版本：
* http://www.falstad.com/circuit/
* http://lushprojects.com/circuitjs/

本地运行（推荐开发方式，Maven + GWT）：
```bash
# 1. 编译 Java 源码（需要 JDK 8）
mvn compile

# 2. 编译 GWT JavaScript 包（输出到 war/circuitjs1/，需几分钟）
JAVA_HOME=<你的 JDK 8 路径> mvn gwt:compile

# 3. 托管 war/ 目录，浏览器打开
# http://localhost:<端口>/circuitjs.html
```

### 三、构建环境
| 工具 | 版本 | 说明 |
|------|------|------|
| JDK | 8 | GWT 2.7.0 编译器只能在 Java 8 上运行 |
| Maven | 3+ | 项目已转换为 Maven 构建（`pom.xml`） |
| IntelliJ IDEA | 任意 | 推荐 IDE，自动识别 Maven 项目 |

传统方式也可使用 Eclipse + GWT 插件构建，详见仓库内 `INTERNALS.md`。

### 四、嵌入与参数（API 一览）
应用支持 iframe 嵌入，并支持以下 URL 参数：

| 参数 | 说明 |
|------|------|
| `cct` | 从 URL 加载电路（类似 Java 版中的 `#`） |
| `ctz` | 从 URL 中的压缩数据加载电路 |
| `startCircuit` | 从 Circuits 目录加载名为 filename 的电路 |
| `startCircuitLink` | 从指定 URL 加载电路（需支持客户端 CORS） |
| `euroResistors` | `true` 强制欧式电阻风格 |
| `usResistors` | `true` 强制美式电阻风格 |
| `whiteBackground` | `true`/`false` 切换白底 |
| `conventionalCurrent` | `true`/`false` 切换电流方向 |
| `running` | `true`/`false` 是否以运行状态启动，默认 `true` |
| `hideSidebar` | `true`/`false` 隐藏侧边栏，默认 `false` |
| `hideMenu` | `true`/`false` 隐藏菜单，默认 `false` |
| `editable` | `true`/`false` 允许编辑电路，默认 `true` |
| `positiveColor` | 正电压颜色（`%23rrggbb`） |
| `negativeColor` | 负电压颜色（`%23rrggbb`） |
| `hideInfoBox` | `true`/`false` 隐藏信息框 |

示例：
```
.../circuitjs.html?startCircuit=4001.txt&whiteBackground=true
```

### 五、部署
1. 按上文执行 GWT 编译。
2. 将 `war/` 目录中除 `WEB-INF` 外的所有内容复制到 Web 服务器。
3. 可按需定制 `circuitjs1.html` 头部（统计代码、favicon 等）与 `iframe.html`（右侧面板品牌内容）。
4. 如需短链接功能，部署可选脚本 `shortrelay.php`；如需 Dropbox 存取，配置 Dropbox API 密钥。

目录结构参考：
```
-+ 首页所在目录（例如 "circuitjs"）
  +- circuitjs.html - 全页版应用
  +- iframe.html - 品牌内容
  +- shortrelay.php - 短链接中继（可选）
  ++ circuitjs1（目录）
   +- GWT 构建产物
   +- circuits（示例电路）
   +- setuplist.txt（示例电路索引）
```

### 六、构建 Electron 应用
1. 按上文使用 GWT 编译应用。
2. 下载并解压目标平台的 [Electron 预构建二进制](https://github.com/electron/electron/releases)（版本 9.3.2）。
3. 将仓库中的 `app` 目录复制到 Electron 二进制目录结构指定位置。
4. 将编译后的 `war` 目录复制到上述结构中的 `app` 目录内。
5. 运行 `Electron` 可执行文件，即可加载 CircuitJS1。

已知限制：
* "Export as URL" 中的 "Create short URL" 依赖服务器支持，无法工作。
* 二极管的 "Create Simple Model" 依赖不受支持的 JavaScript 特性，无法工作。

### 七、致谢
本项目基于上游仓库 [sharpie7/circuitjs1](https://github.com/sharpie7/circuitjs1) 进行本地化与改进，特此致谢。

### 八、许可证
本项目为自由软件，依据自由软件基金会发布的 GNU 通用公共许可证（GPL）第 2 版或（由你选择）任何更高版本分发。详见仓库内 `COPYING.txt`。