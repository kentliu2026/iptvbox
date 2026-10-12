# IPTV 组播播放器（机顶盒版）

## 一句话说明
一个为安卓机顶盒设计的 IPTV **组播**播放器：自己管理频道列表、遥控器上下键选台、确认键播放；真正的组播解码由本机已安装的 **VLC** 完成。

## 它能做什么
- 频道列表（ListView），遥控器方向键移动焦点、确认键播放，无需触屏
- 频道数据来自 `app/src/main/res/raw/channels.txt`，改文件即可增删频道，不用改代码
- 支持 `udp://@` 与 `rtp://@` 组播地址（运营商 IPTV 内网直播最常见格式）

## ⚠️ 重要边界（务必先读）
1. **本机无法直接出 APK，但已配好云端构建**：开发机（当前工作环境）没有 JDK / Android SDK / Gradle，无法直接编译。两种出包方式任选：
   - **(a) GitHub Actions 自动构建（推荐）**：本项目已含 `.github/workflows/build.yml`，把代码 push 到 GitHub 即云端 runner 自动出包（见下「GitHub Actions 自动构建」）。
   - **(b) 本地 Android Studio 编译**：在有 Android Studio 的电脑上打开本目录 `D:\iptv` 构建（见下「构建」）。
2. **播放依赖 VLC**：本 APP 调起系统里已安装的 VLC（包名 `org.videolan.vlc`）播放组播。盒子需先装 VLC——藏宝阁 `WPS-lingxi/IPTV/` 里已备：
   - `VLC-Android-3.7.1-arm64-v8a.apk` —— **64 位，本机顶盒（53U1M，S905L2 第三方固件为 64 位系统）用这个**
   - `VLC-Android-3.7.1-armeabi-v7a.apk` —— 32 位，仅其他 32 位固件用（本盒不适用）
   - 没装 VLC 时 APP 会提示去装。
3. **组播地址要真实且在内网有效**：示例里的 `239.1.1.1:1234` 是占位，不会出画面。必须换成你运营商/内网真实的组播地址（怎么找见下「获取真实组播地址」）。

## GitHub Actions 自动构建（推荐，无需本机 SDK）
前置：一个 GitHub 账号 + 一个空仓库（没有就让 KENT 帮你建）。

步骤：
1. 在 GitHub 新建一个空仓库（如 `iptvbox`）。
2. 在本机（装了 git 的机器，或直接在 D:\iptv 目录）：
   ```bash
   cd D:\iptv
   git init
   git add .
   git commit -m "init iptv box app"
   git branch -M main
   git remote add origin https://github.com/<你的用户名>/iptvbox.git
   git push -u origin main
   ```
3. 进仓库 → **Actions** 标签页 → 看到 `Build IPTV Box APK` 工作流自动运行（首次可能需在 Actions 页点一下 `I understand my workflows` 启用）。
4. 等待绿色 ✓ → 进该次运行 → **Artifacts** → 下载 `app-debug-apk`（里面是 `app-debug.apk`）。
5. 把 `app-debug.apk` 装到盒子（先装 64 位 VLC）。

> 也可在仓库 Actions 页手动点 `Run workflow` 触发（对应工作流里的 `workflow_dispatch`）。
> 工作流固定用 AGP 8.1.4 + Gradle 8.4 + compileSdk 33 + JDK 17，已在 `.github/workflows/build.yml` 写死，无需本地配环境。

## 构建（本地 Android Studio，备选）
前置：JDK 17、Android Studio（含 SDK Platform 33）、能联网拉依赖（首次会下载 Gradle 8.4 与 AndroidX 库）。

步骤：
1. 把 `D:\iptv` 整目录拷到那台机器。
2. Android Studio → `File → Open` → 选 `D:\iptv`。
3. 等待 Gradle 同步完成（自动下载依赖）。
4. `Build → Build Bundle(s) / APK(s) → Build APK(s)`。
5. 产物：`app/build/outputs/apk/debug/app-debug.apk`。

命令行（已配置 Gradle 环境）：`./gradlew assembleDebug`（Windows：`gradlew.bat assembleDebug`）。

> 兼容性：当前 `build.gradle` 用 AGP 8.1.4 + Gradle 8.4，需 JDK 17。若你的环境是 JDK 11，把 `app/build.gradle` 的 `compileOptions` 与根 `build.gradle` 的 classpath 降到 AGP 7.4.x + Gradle 7.5（自行调整即可）。

## 安装到盒子
- 命令行（盒子已开 USB 调试、与电脑同网）：`adb connect 盒子IP:5555` → `adb install app-debug.apk`
- 或把 apk 拷到 U 盘，盒子文件管理器里点装（需允许未知来源）。
- 装好后**先装 VLC**（从藏宝阁拿的 64 位 apk），再装本 APP。

## 使用
- 打开 APP，看到频道列表。
- 遥控器：上/下键移动焦点（高亮行），确认键（OK/中间键）播放当前频道 → 自动跳到 VLC 播放组播。
- 返回键回到列表。

## 编辑频道（channels.txt）
- 打开 `app/src/main/res/raw/channels.txt`。
- 每行一条：`频道名|组播地址`，`#` 开头为注释。
- 改完**重新构建**（Actions 重新 push / 本地重新 Build）即可。
- 示例：
  ```
  CCTV-1|udp://@239.x.x.x:端口
  本地台|rtp://@239.x.x.x:端口
  ```

## 获取真实组播地址
组播地址通常来自运营商 IPTV 专网，公开难找。常见获取方式：
- 同地区网友分享的组播列表（论坛/群），按你所在城市搜「城市名 IPTV 组播 地址」。
- 自己抓包：在路由器或电脑上抓 IPTV 机顶盒的 IGMP/RTSP 流量，提取 `udp://@239...` 地址。
- 注意版权与合规，自用评估风险。

## 想做成「不依赖外部 VLC」的独立版？
进阶：把播放改为内嵌 `org.videolan.android:libvlc-all`（VideoLAN 官方 Maven 工件，含预编译 .so，不必自己编 VLC），用 `LibVLC` + `VideoView` 自绘播放界面。需给 `app/build.gradle` 加 VideoLAN Maven 仓库与该依赖，并改写 `MainActivity` 播放逻辑。需要的话我可以直接给你改一版。

## 文件结构
```
D:\iptv\
├─ settings.gradle / build.gradle / gradle.properties
├─ gradle/wrapper/gradle-wrapper.properties
├─ .github/workflows/build.yml   ← GitHub Actions 自动构建工作流
├─ app/
│  ├─ build.gradle
│  └─ src/main/
│     ├─ AndroidManifest.xml
│     ├─ java/com/example/iptvbox/
│     │  ├─ MainActivity.java    频道列表 + 调起 VLC
│     │  ├─ Channel.java         频道模型
│     │  ├─ ChannelAdapter.java  列表适配器(焦点高亮)
│     │  └─ ChannelStore.java    读取 channels.txt
│     └─ res/
│        ├─ layout/  activity_main.xml, item_channel.xml
│        ├─ values/ strings.xml, colors.xml, themes.xml
│        ├─ drawable/ list_selector.xml
│        └─ raw/ channels.txt    频道列表(可编辑)
└─ README.md
```

## 风险与排错
- **组播仅在对应内网可达**：家里普通宽带通常收不到运营商 IPTV 专网组播（需 IPTV 拨号/专用线路）。收不到时 VLC 会一直转圈或黑屏，不是 APP 的问题。
- **云端构建已配置**：若无 Android Studio，直接 push 到 GitHub 触发 Actions 出包；若构建报依赖或版本错，把 Actions 日志发我，我据此修正。
- **调起 VLC 的 Intent 依赖 VLC 安卓版（org.videolan.vlc）**：若你装的是其他品牌播放器，需改 `MainActivity` 里的 `VLC_PACKAGE` 常量。
