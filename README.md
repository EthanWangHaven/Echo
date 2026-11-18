# Echo

黑白极简风格的 Android 音乐播放器。Kotlin + Jetpack Compose + Hilt + Media3/ExoPlayer。

音源托管在个人网站（GitHub Pages：`EthanWangHaven.github.io`），App 通过 GitHub Contents API 提交音源与歌单，网页端与 App 端歌单双向一致。

[![Release](https://img.shields.io/badge/release-v1.0.0-blue)](../../releases)

## 功能总览

### 曲库与播放
- **内置歌单 + 网站同步**：内置曲库随包发布，右上角「同步」拉取网站 `data/playlist.json` 增量合并，冷启动自动恢复
- **播放控制**：播放/暂停、上一首/下一首、进度拖动、列表循环/单曲循环、定时关闭（30/60 分钟循环切换）
- **播放内核 Media3 / ExoPlayer**：
  - 渐进式流式缓冲，加载期间 UI 显示转圈过渡态
  - **网络卡顿自愈**：短暂断流进入 `STATE_BUFFERING` 不中断播放会话
  - **断网自动续播**：加载错误按 1s→2s→4s 指数退避自动重试（最多 3 次），恢复后从出错断点继续播放；重试耗尽才提示失败，手动点播放即整曲重载
- **后台播放**：前台服务保活 + `WAKE_MODE_LOCAL` 防休眠；通知栏磨砂媒体卡片（网易云风格：封面模糊背景 + 三键遥控）；独立 `MediaSession` 驱动锁屏/快捷设置系统媒体卡（A13+），支持「喜欢」「词」自定义操作

### 添加音乐（对标网页端弹窗，双模式）
- **网易云 id 模式**：粘贴歌曲 id 或链接 → Meting 解析歌名/歌手/音源 → 三通道下载（直连 → corsproxy → allorigins 兜底）→ Meting 取歌词
- **本地文件模式**：SAF 选择音频（≤60MB）→ LRCLIB 按歌名+歌手自动匹配歌词（简体未命中自动转繁体重试，失败不阻塞提交）
- **提交**：GitHub Contents API 写入网站仓库——音源进 `public/audio/`，歌单更新 `data/playlist.json` 与 `public/data/playlist.json`（App「同步」拉取的线上副本）
- 提交成功后**本地立即加歌可试听**，网站 1-2 分钟部署完成后两端一致

### 应用内 GitHub Token（免重编译换 Token）
- 「添加音乐」弹窗标题右侧 ⓘ → 粘贴新 Token 保存即生效，无需改代码重新打包
- 优先级：应用内自定义 Token > 编译时 `BuildConfig.GH_TOKEN`；「清除」回退内置
- Token 保存在应用私有 SharedPreferences，不出设备

### 批量移除（本地 + 仓库同步删除）
- 操作行垃圾桶胶囊 → 进入选择模式：点击曲目勾选（支持单选/多选/全选），工具行显示已选数量
- 确认弹窗二次防误触；删除动作：
  - 仓库：逐个删除 `public/audio/` 音源（幂等），两份歌单过滤对应条目后重新提交
  - 本地：列表移除 + 持久化「已移除」标记（内置曲目也会真删，冷启动不回归）
  - 正在播放被删曲目时立即停止并复位
- **一致性保障**：仓库任一步失败则整体失败、本地不动（错误原样提示）；成功才动本地

### 界面
- 黑白极简设计语言：玻璃拟态弹层（真窗口模糊）、squircle 连续圆角、hairline 描边
- 自适应图标：白底黑纹「声波」glyph（连续圆角竖条），支持 Android 13+ 主题图标（monochrome）
- 歌单头：120dp 封面 + 「歌单 · N 首」+ **镜像波形律动条**（31 根细条上下对称，播放时错相跳动、暂停时定格剪影，颜色可在代码中一键配置）
- 深色模式全量适配

## 构建

要求：JDK 17+、Android SDK（platforms;android-35、build-tools;35.0.0）、Gradle 8.11.1（wrapper 自带）

```bash
git clone https://github.com/EthanWangHaven/Echo.git
cd Echo

# （可选）配置上传用 GitHub Token —— 也可装好后在 App 内 ⓘ 设置
echo "GH_TOKEN=ghp_xxx" >> ~/.gradle/gradle.properties

./gradlew assembleDebug
# 产物：app/build/outputs/apk/debug/Echo-1.0.0-debug.apk
```

Token 要求：classic PAT 含 `repo` 权限，或 fine-grained PAT 仅授权 `EthanWangHaven.github.io` 的 Contents 读写（推荐，粒度最小）。

> 项目根目录的 `gradle.properties` / `local.properties` 已在 `.gitignore` 中排除，Token 不会入库。

## 技术栈

| 项 | 值 |
|---|---|
| 语言 | Kotlin 2.0.21 |
| UI | Jetpack Compose（BOM 2024.12.01）+ Material3 |
| 依赖注入 | Hilt 2.52（KSP） |
| 播放内核 | Media3 / ExoPlayer 1.5.1 |
| 构建 | AGP 8.7.3 / Gradle 8.11.1 / compileSdk 35 / minSdk 26 |
| 数据 | DataStore Preferences（同步曲目 / 已移除标记 / 封面映射）+ SharedPreferences（喜欢 / Token） |

## 近期更新（v1.0.0）

- **播放内核升级 Media3/ExoPlayer**：网络卡顿自愈 + 断点自动续播（原 MediaPlayer 遇网络错误即停播需手动重试）
- **新增：应用内 GitHub Token 配置**——添加音乐弹窗标题 ⓘ 入口，换 Token 免重编译
- **新增：批量移除**——本地与仓库同步删除，含内置曲目防回归标记
- **新增：歌单头镜像波形律动条**，歌单封面放大至 120dp，布局上下分布
- **重绘 App 图标**：白底黑纹声波 glyph + monochrome 主题图标支持
- 修复：`GlassCard` 组件缺失导致的编译失败；`gradlew` 行尾统一为 LF

## License

仅供个人学习与使用。
