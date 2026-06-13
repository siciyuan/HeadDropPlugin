# HeadDropPlugin

万物皆可掉头颅插件 - 支持 Paper/Spigot 1.21.11

## 功能特性

- 🎯 击杀任何生物都有概率掉落对应的头颅
- 📊 可自定义每种生物的掉落概率
- 🎨 支持自定义头颅纹理
- ⚙️ 支持热重载配置文件
- 📦 提供完整的管理命令

## 命令

| 命令 | 描述 | 权限 |
|------|------|------|
| `/headdrop reload` | 重新加载配置文件 | `headdrop.admin` |
| `/headdrop chance <entity> <probability>` | 设置指定生物的掉落概率 | `headdrop.admin` |
| `/headdrop give <player> <entity> [amount]` | 给予玩家指定头颅 | `headdrop.admin` |
| `/headdrop list` | 列出所有支持的生物 | `headdrop.admin` |
| `/headdrop info <entity>` | 查看指定生物的掉落信息 | `headdrop.admin` |

## 权限

| 权限节点 | 描述 | 默认 |
|----------|------|------|
| `headdrop.admin` | 允许管理 HeadDropPlugin | OP |

## 配置文件

配置文件位于 `plugins/HeadDropPlugin/config.yml`

```yaml
# 全局掉落概率 (0.0 - 1.0)
global-chance: 0.1

# 单独配置各生物的掉落概率
mobs:
  CREEPER: 0.15
  SKELETON: 0.1
  ZOMBIE: 0.1
  SPIDER: 0.1
  # ... 更多生物配置
```

## 支持的生物

- ALLAY, ARMADILLO, AXOLOTL, BAT, BEE, BLAZE
- BOGGED, BREEZE, CAMEL, CAT, CAVE_SPIDER, CHICKEN
- COD, COPPER_GOLEM, COW, CREAKING, CREEPER
- DOLPHIN, DONKEY, DROWNED, ELDER_GUARDIAN, ENDERMAN
- ENDERMITE, ENDER_DRAGON, EVOKER, FOX, FROG
- GHAST, GIANT, GLOW_SQUID, GOAT, GUARDIAN
- HOGLIN, HORSE, HUSK, ILLUSIONER, IRON_GOLEM
- LLAMA, MAGMA_CUBE, MOOSHROOM, MULE, NAUTILUS
- OCELOT, PANDA, PARCHED, PARROT, PHANTOM
- PIG, PIGLIN, PIGLIN_BRUTE, PILLAGER, POLAR_BEAR
- PUFFERFISH, RABBIT, RAVAGER, SALMON, SHEEP
- SHULKER, SILVERFISH, SKELETON, SKELETON_HORSE, SLIME
- SNIFFER, SNOW_GOLEM, SPIDER, SQUID, STRAY
- STRIDER, TADPOLE, TRADER_LLAMA, TROPICAL_FISH, TURTLE
- VEX, VILLAGER, VINDICATOR, WANDERING_TRADER, WARDEN
- WITCH, WITHER, WITHER_SKELETON, WOLF, ZOGLIN
- ZOMBIE, ZOMBIE_HORSE, ZOMBIE_VILLAGER, ZOMBIFIED_PIGLIN

## 安装

1. 下载 `HeadDropPlugin-1.0.0.jar`
2. 将 JAR 文件放入服务器的 `plugins/` 目录
3. 重启服务器或使用 `/reload` 命令

## 构建

```bash
mvn clean package
```

构建后的 JAR 文件位于 `target/HeadDropPlugin-1.0.0.jar`

## 作者

- **SCROAM** - [blog.world123.top](https://blog.world123.top)

## 版本

- 1.0.0 - 初始版本

## 许可证

MIT License
