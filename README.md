<div align="center">
<a href="https://github.com/UniversalExporters/UniversalExporter">
<img src="common/src/main/resources/icon.png" />
</a>

适用于 mcmod.cn 的通用多模组加载器的模组内容数据输出器

<img alt="fabric" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/fabric_vector.svg">
<img alt="forge" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/forge_vector.svg">

</div>

## 项目结构

| 模块      | 功能            |
|:-------:|:-------------:|
| adapter |  导出的json配置格式  |
| common  | 多模组加载器之间的通用代码 |
| fabric  | fabric侧的专用代码  |
| forge   |  forge侧的专用代码  |

## 导出格式 (TODO)

|  项目   |         功能         |
|:-----:|:------------------:|
| 物品和方块 | 导出物品或者方块的贴图，属性或者其他 |
|  进度   |  导出进度和进度显示器进度抓钩等等  |
|       |                    |

## 构建
### adapter
- 使用java8环境下运行
- 且gson版本须是2.10.*
- 自动构建，编译
### 构建环境
- adapter需要构建一次后再构建方能显示
- Java 17及以上版本(某些版本使用java 8)
- IntelliJ IDEA
### 编译
直接执行 `./gradlew build` 即可。

## 许可证
本项目使用AGPLv3许可协议。
