package com.scroam.headdrop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.net.URL;
import java.util.UUID;

public class HeadDropCommand implements CommandExecutor {

    private final HeadDropPlugin plugin;

    public HeadDropCommand(HeadDropPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "reload":
                return reloadConfig(sender);
            case "chance":
                return setChance(sender, args);
            case "give":
                return giveHead(sender, args);
            case "list":
                return listMobs(sender);
            case "info":
                return showInfo(sender);
            default:
                sendHelp(sender);
                return true;
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== HeadDropPlugin 命令帮助 ===");
        sender.sendMessage(ChatColor.YELLOW + "/headdrop reload" + ChatColor.WHITE + " - 重新加载配置");
        sender.sendMessage(ChatColor.YELLOW + "/headdrop chance <概率>" + ChatColor.WHITE + " - 设置掉落概率 (0.0-1.0)");
        sender.sendMessage(ChatColor.YELLOW + "/headdrop give <玩家> <生物类型>" + ChatColor.WHITE + " - 给予玩家头颅");
        sender.sendMessage(ChatColor.YELLOW + "/headdrop list" + ChatColor.WHITE + " - 列出所有支持的生物");
        sender.sendMessage(ChatColor.YELLOW + "/headdrop info" + ChatColor.WHITE + " - 显示插件信息");
        sender.sendMessage(ChatColor.GOLD + "==============================");
    }

    private boolean reloadConfig(CommandSender sender) {
        if (!sender.hasPermission("headdrop.admin")) {
            sender.sendMessage(ChatColor.RED + "没有权限执行此命令！");
            return true;
        }
        plugin.reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "配置文件已重新加载！");
        return true;
    }

    private boolean setChance(CommandSender sender, String[] args) {
        if (!sender.hasPermission("headdrop.admin")) {
            sender.sendMessage(ChatColor.RED + "没有权限执行此命令！");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "用法: /headdrop chance <概率>");
            return true;
        }
        try {
            double chance = Double.parseDouble(args[1]);
            if (chance < 0 || chance > 1) {
                sender.sendMessage(ChatColor.RED + "概率必须在 0.0-1.0 之间！");
                return true;
            }
            plugin.getConfig().set("drop-chance", chance);
            plugin.saveConfig();
            sender.sendMessage(ChatColor.GREEN + "掉落概率已设置为: " + (chance * 100) + "%");
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "无效的概率值！");
        }
        return true;
    }

    private boolean giveHead(CommandSender sender, String[] args) {
        if (!sender.hasPermission("headdrop.admin")) {
            sender.sendMessage(ChatColor.RED + "没有权限执行此命令！");
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "用法: /headdrop give <玩家> <生物类型>");
            sender.sendMessage(ChatColor.GRAY + "使用 /headdrop list 查看所有支持的生物类型");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "玩家 " + args[1] + " 不在线！");
            return true;
        }

        String entityType = args[2].toUpperCase();
        try {
            EntityType type = EntityType.valueOf(entityType);
            ItemStack head = createHead(type);
            if (head != null) {
                target.getInventory().addItem(head);
                sender.sendMessage(ChatColor.GREEN + "已给予 " + target.getName() + " " + type.name() + " 的头颅！");
            } else {
                sender.sendMessage(ChatColor.RED + "无法创建 " + entityType + " 的头颅！");
            }
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "未知的生物类型: " + entityType);
            sender.sendMessage(ChatColor.GRAY + "使用 /headdrop list 查看所有支持的生物类型");
        }
        return true;
    }

    private boolean listMobs(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== 支持的生物类型 ===");
        
        sender.sendMessage(ChatColor.YELLOW + "\n怪物:");
        String[] mobs = {"CREEPER", "SKELETON", "WITHER_SKELETON", "STRAY", "SPIDER", "CAVE_SPIDER",
                "ENDERMAN", "WITHER", "ENDER_DRAGON", "GUARDIAN", "ELDER_GUARDIAN", "BLAZE",
                "GHAST", "MAGMA_CUBE", "SILVERFISH", "ENDERMITE", "VINDICATOR", "EVOKER",
                "ILLUSIONER", "RAVAGER", "HOGLIN", "PIGLIN_BRUTE", "ZOMBIE", "DROWNED",
                "HUSK", "ZOMBIE_VILLAGER", "PIGLIN", "ZOMBIFIED_PIGLIN", "PHANTOM", "SHULKER",
                "WITCH", "WARDEN", "BREEZE", "BOGGED", "CREAKING", "PILLAGER", "VEX", "ZOGLIN"};
        sender.sendMessage(ChatColor.WHITE + String.join(", ", mobs));

        sender.sendMessage(ChatColor.YELLOW + "\n动物:");
        String[] animals = {"CHICKEN", "COW", "PIG", "SHEEP", "WOLF", "OCELOT", "CAT", "HORSE",
                "DONKEY", "MULE", "LLAMA", "TRADER_LLAMA", "FOX", "BEE", "ALLAY", "CAMEL",
                "FROG", "GOAT", "MOOSHROOM", "PARROT", "RABBIT", "TURTLE", "POLAR_BEAR",
                "PANDA", "AXOLOTL", "BAT", "DOLPHIN", "COD", "SALMON", "PUFFERFISH",
                "TROPICAL_FISH", "SQUID", "GLOW_SQUID", "NAUTILUS", "SNIFFER", "TADPOLE",
                "IRON_GOLEM", "SNOW_GOLEM", "STRIDER", "WANDERING_TRADER", "VILLAGER",
                "SKELETON_HORSE", "ZOMBIE_HORSE", "SLIME", "GIANT", "COPPER_GOLEM",
                "ARMADILLO", "PARCHED", "CAMEL_HUSK"};
        sender.sendMessage(ChatColor.WHITE + String.join(", ", animals));

        sender.sendMessage(ChatColor.GOLD + "======================");
        return true;
    }

    private boolean showInfo(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== HeadDropPlugin 信息 ===");
        sender.sendMessage(ChatColor.YELLOW + "版本: " + ChatColor.WHITE + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.YELLOW + "作者: " + ChatColor.WHITE + "SCROAM");
        sender.sendMessage(ChatColor.YELLOW + "网站: " + ChatColor.WHITE + "blog.world123.top");
        sender.sendMessage(ChatColor.YELLOW + "离线模式: " + (plugin.isOfflineMode() ? ChatColor.GREEN + "开启" : ChatColor.RED + "关闭"));
        sender.sendMessage(ChatColor.YELLOW + "掉落概率: " + ChatColor.WHITE + (plugin.getDropChance() * 100) + "%");
        sender.sendMessage(ChatColor.YELLOW + "支持生物: " + ChatColor.WHITE + "87种");
        sender.sendMessage(ChatColor.GOLD + "==========================");
        return true;
    }

    private ItemStack createHead(EntityType type) {
        Material material = getMobHeadMaterial(type);
        if (material != null) {
            ItemStack head = new ItemStack(material);
            ItemMeta meta = head.getItemMeta();
            String name = capitalize(type.name().toLowerCase().replace("_", " "));
            meta.setDisplayName(ChatColor.GOLD + name + " 的头颅");
            head.setItemMeta(meta);
            return head;
        }

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        
        String typeName = type.name();
        String displayName = capitalize(typeName.toLowerCase().replace("_", " ")) + " 的头颅";
        meta.setDisplayName(ChatColor.GOLD + displayName);

        try {
            UUID uuid = UUID.nameUUIDFromBytes(typeName.getBytes());
            PlayerProfile profile = Bukkit.createPlayerProfile(uuid, typeName);
            
            String textureId = getTextureId(typeName);
            if (textureId != null) {
                profile.getTextures().setSkin(new URL("https://textures.minecraft.net/texture/" + textureId));
            }
            
            meta.setOwnerProfile(profile);
        } catch (Exception e) {
            plugin.getLogger().warning("无法创建 " + typeName + " 的头颅: " + e.getMessage());
            return null;
        }

        head.setItemMeta(meta);
        return head;
    }

    private Material getMobHeadMaterial(EntityType type) {
        return switch (type) {
            case CREEPER -> Material.CREEPER_HEAD;
            case SKELETON -> Material.SKELETON_SKULL;
            case WITHER_SKELETON -> Material.WITHER_SKELETON_SKULL;
            case ZOMBIE -> Material.ZOMBIE_HEAD;
            case PLAYER -> Material.PLAYER_HEAD;
            case WITHER -> Material.WITHER_SKELETON_SKULL;
            case ENDER_DRAGON -> Material.DRAGON_HEAD;
            default -> null;
        };
    }

    private String getTextureId(String typeName) {
        return switch (typeName) {
            case "CREEPER" -> "f2e30e303a54fd4261b9554453101f16d246b013c3398b444ed22ed263a6d58d";
            case "SKELETON" -> "8be6593381f1b60999bf79d269e0c82b23ea22dd0ca692aeaaeec0ac2101e8e";
            case "WITHER_SKELETON" -> "4172d85261902aa2da7f7843beea57fe775f5c8927e24ced0c9f39b377110f8d";
            case "STRAY" -> "c2ada9159293e3f5a0cbe509b16662ce4b85e44097bfc714a61909fdc515b3da";
            case "SPIDER" -> "c87a96a8c23b83b32a73df051f6b84c2ef24d25ba4190dbe74f11138629b5aef";
            case "CAVE_SPIDER" -> "604d5fcb289fe65b6786682e1c736c3f7b16f39d940e3d2f41cf0040704c6282";
            case "ENDERMAN" -> "96c0b36d53fff69a49c7d6f3932f2b0fe948e032226d5e8045ec58408a36e951";
            case "WITHER" -> "ee280cefe946911ea90e87ded1b3e18330c63a23af5129dfcfe9a8e166588041";
            case "ENDER_DRAGON" -> "6f3606ff94dd5275a7aa4e72473e6aafb1fe6b104338a051f067fbbcccc1a263";
            case "GUARDIAN" -> "a0bf34a71e7715b6ba52d5dd1bae5cb85f773dc9b0d457b4bfc5f9dd3cc7c94";
            case "ELDER_GUARDIAN" -> "610e80a014165bade0634f7f96b4784a8b4d802dacc8866534a2b0fb2b655433";
            case "BLAZE" -> "f30c14d1aadffe8d92f9bd8f938e220a15321ec5f39343d5f61e670aa7958631";
            case "GHAST" -> "de8a38e9afbd3da10d19b577c55c7bfd6b4f2e407e44d4017b23be9167abff02";
            case "MAGMA_CUBE" -> "a1c97a06efde04d00287bf20416404ab2103e10f08623087e1b0c1264a1c0f0c";
            case "SILVERFISH" -> "84a21beeca0744784b8f6e6fbbbabffc8f8a14172a1fa5a8e472c0e0285c3b93";
            case "ENDERMITE" -> "5bc7b9d36fb92b6bf292be73d32c6c5b0ecc25b44323a541fae1f1e67e393a3e";
            case "VINDICATOR" -> "4f6fb89d1c631bd7e79fe185ba1a6705425f5c31a5ff626521e395d4a6f7e2";
            case "EVOKER" -> "630ce775edb65db8c2741bdfae84f3c0d0285aba93afadc74900d55dfd9504a5";
            case "ILLUSIONER" -> "4639d325f4494258a473a93a3b47f34a0c51b3fceaf59fee87205a5e7ff31f68";
            case "RAVAGER" -> "cd20bf52ec390a0799299184fc678bf84cf732bb1bd78fd1c4b441858f0235a8";
            case "HOGLIN" -> "9bb9bc0f01dbd762a08d9e77c08069ed7c95364aa30ca1072208561b730e8d75";
            case "PIGLIN_BRUTE" -> "3e300e9027349c4907497438bac29e3a4c87a848c50b34c21242727b57f4e1cf";
            case "ZOMBIE" -> "79715c68b781c0ad4eb16f9d7cf4590c38503490c9054d1508e232d24b0baba9";
            case "DROWNED" -> "c3f7ccf61dbc3f9fe9a6333cde0c0e14399eb2eea71d34cf223b3ace22051";
            case "HUSK" -> "269b9734d0e7bf060fedc6bf7fec64e1f7ad6fc80b0fd8441ad0c7508c850d73";
            case "ZOMBIE_VILLAGER" -> "37e838ccc26776a217c678386f6a65791fe8cdab8ce9ca4ac6b28397a4d81c22";
            case "PIGLIN" -> "c79da5fbf2ae1bd0ffda2b7d5a6d8107772303d246667309ab452032a11f249";
            case "ZOMBIFIED_PIGLIN" -> "7eabaecc5fae5a8a49c8863ff4831aaa284198f1a2398890c765e0a8de18da8c";
            case "PHANTOM" -> "746830da5f83a3aaed838a99156ad781a789cfcf13e25beef7f54a86e4fa4";
            case "SHULKER" -> "1433a4b73273a64c8ab2830b0fff777a61a488c92f60f83bfb3e421f428a44";
            case "WITCH" -> "20e13d18474fc94ed55aeb7069566e4687d773dac16f4c3f8722fc95bf9f2dfa";
            case "WARDEN" -> "cf6481c7c435c34f21dff1043a4c7034c445a383a5435fa1f2a503a348afd62f";
            case "BREEZE" -> "a275728af7e6a29c88125b675a39d88ae9919bb61fdc200337fed6ab0c49d65c";
            case "BOGGED" -> "a3b9003ba2d05562c75119b8a62185c67130e9282f7acbac4bc2824c21eb95d9";
            case "CREAKING" -> "77b5be72769ccff1a6cb77c5848e01d7e5704a3d349c0737ff93cb54d02380ac";
            case "PILLAGER" -> "4aee6bb37cbfc9b0d86db5ada4790c64ff4468d68b84942fde04405e8ef5333";
            case "VEX" -> "869a7c6f0a7358c594c29d3d42cf7b69638ef3b5b3ec1f9d38150c8b2bff7813";
            case "ZOGLIN" -> "e67e18602e03035ad68967ce090235d8996663fb9ea47578d3a7ebbc42a5ccf9";
            case "CHICKEN" -> "de8a410582d1db0f1607eb4d07322deca21df39cb9c33e37034d0dcfe3fc73a5";
            case "COW" -> "c9ba90526d6d60f1cf0cfbac018b0cb0b30307be0e5ccfbefb6984f706b2632";
            case "PIG" -> "7cdc454eeaa457cef34a89e8b98c28667f81321ee478411773613476236afbe2";
            case "SHEEP" -> "b600b92d210e49ff4a10aeb1e411a96e327baaf93d025909e3bf5a8cea768c04";
            case "WOLF" -> "72ce161e3205d89e7e4d3ec04d25abfea6231a2277a2bd76f4693f4ce6189a2d";
            case "OCELOT" -> "de440058c7e9b57f441c5e2e9538135bc7e42ce5ea039d8c5cdb85a4c2c3a5aa";
            case "CAT" -> "d5b3f8ca4b3a555ccb3d194449808b4c9d783327197800d4d65974cc685af2ea";
            case "HORSE" -> "628d1ab4be1e28b7b461fdea46381ac363a7e5c3591c9e5d2683fbe1ec9fcd3";
            case "DONKEY" -> "63a976c047f412ebc5cb197131ebef30c004c0faf49d8dd4105fca1207edaff3";
            case "MULE" -> "a0486a742e7dda0bae61ce2f55fa13527f1c3b334c57c034bb4cf132fb5f5f";
            case "LLAMA" -> "7f832466dcc7d5e7702cdee4cd555dbd39637d20adf9367fb03cfd6888baaae7";
            case "TRADER_LLAMA" -> "8424780b3c5c5351cf49fb5bf41fcb289491df6c430683c84d7846188db4f84d";
            case "FOX" -> "d8954a42e69e0881ae6d24d4281459c144a0d5a968aed35d6d3d73a3c65d26a";
            case "BEE" -> "59ac16f296b461d05ea0785d477033e527358b4f30c266aa02f020157ffca736";
            case "ALLAY" -> "e50294a1747310f104124c6373cc639b712baa57b7d926297b645188b7bb9ab9";
            case "CAMEL" -> "74b8a333dfa92e7e5a95ad4ae2d84b1bafa33dc28c054925277f60e79dafc8c4";
            case "FROG" -> "ce62e8a048d040eb0533ba26a866cd9c2d0928c931c50b4482ac3a3261fab6f0";
            case "GOAT" -> "7b0ee70b42c77265b040ba7fb2e5b890cd420e0b81c93a052b8cfb0d74014bf0";
            case "MOOSHROOM" -> "2b52841f2fd589e0bc84cbabf9e1c27cb70cac98f8d6b3dd065e55a4dcb70d77";
            case "PARROT" -> "2b94f236c4a642eb2bcdc3589b9c3c4a0b5bd5df9cd5d68f37f8c83f8e3f1";
            case "RABBIT" -> "ffecc6b5e6ea5ced74c46e7627be3f0826327fba26386c6cc7863372e9bc";
            case "TURTLE" -> "0a4050e7aacc4539202658fdc339dd182d7e322f9fbcc4d5f99b5718a";
            case "POLAR_BEAR" -> "c4fe926922fbb406f343b34a10bb98992cee4410137d3f88099427b22de3ab90";
            case "PANDA" -> "dca096eea506301bea6d4b17ee1605625a6f5082c71f74a639cc940439f47166";
            case "AXOLOTL" -> "5c138f401c67fc2e1e387d9c90a9691772ee486e8ddbf2ed375fc8348746f936";
            case "BAT" -> "6de75a2cc1c950e82f62abe20d42754379dfad6f5ff546e58f1c09061862bb92";
            case "DOLPHIN" -> "8e9688b950d880b55b7aa2cfcd76e5a0fa94aac6d16f78e833f7443ea29fed3";
            case "COD" -> "7892d7dd6aadf35f86da27fb63da4edda211df96d2829f691462a4fb1cab0";
            case "SALMON" -> "8aeb21a25e46806ce8537fbd6668281cf176ceafe95af90e94a5fd84924878";
            case "PUFFERFISH" -> "17152876bc3a96dd2a2299245edb3beef647c8a56ac8853a687c3e7b5d8bb";
            case "TROPICAL_FISH" -> "34a0c84dc3c090df7bafc4367a9fc6c8520da2f73efffb80e934d1189eadac41";
            case "SQUID" -> "49c2c9ce67eb5971cc5958463e6c9abab8e599adc295f4d4249936b0095769dd";
            case "GLOW_SQUID" -> "55e2b46e52ac92d419a2ddbcc9cdce7b451cb48ae739d85d607db0502a008ce0";
            case "NAUTILUS" -> "b53d63eb175b00fb35285f4330d0be68a0713db94e00e6fdd85832d41e3a08b";
            case "SNIFFER" -> "87ad920a66e38cc3426a5bff084667e8772116915e298098567c139f222e2c42";
            case "TADPOLE" -> "b23ebf26b7a441e10a86fb5c2a5f3b519258a5c5dddd6a1a75549f517332815b";
            case "IRON_GOLEM" -> "e13f34227283796bc017244cb46557d64bd562fa9dab0e12af5d23ad699cf697";
            case "SNOW_GOLEM" -> "e6f20aec528c3968dd8164f9d9336b081b3a2c7ecf189cf73df6f925e5a4ed14";
            case "STRIDER" -> "18a9adf780ec7dd4625c9c0779052e6a15a451866623511e4c82e9655714b3c1";
            case "WANDERING_TRADER" -> "5f1379a82290d7abe1efaabbc70710ff2ec02dd34ade386bc00c930c461cf932";
            case "VILLAGER" -> "d14bff1a38c9154e5ec84ce5cf00c58768e068eb42b2d89a6bbd29787590106b";
            case "SKELETON_HORSE" -> "2704a814f1f50349c2aafc6fbff9e1a26e39ed8bf881bbe9310ce6486059f0";
            case "ZOMBIE_HORSE" -> "171ce469cba4426c811f69be5d958a09bfb9b1b2bb649d3577a0c2161ad2f524";
            case "SLIME" -> "895aeec6b842ada8669f846d65bc49762597824ab944f22f45bf3bbb941abe6c";
            case "GIANT" -> "c09e16bccd9a4822fa75416ae38a7a98786e219dda679c5b127e3a2efcfc";
            case "COPPER_GOLEM" -> "d998651718b318a2ca6a4a21ed201f0c65a14d0fcde7dd044bad33116cd5e026";
            case "ARMADILLO" -> "c9c1e96ce985725e22ed6ccf0f4c4810c729a2538b97bda06faeb3b92799c878";
            case "PARCHED" -> "24aeceff5f26dd8413c5c03547c234ac03108d187af0b9cd834a8ce12598591c";
            case "CAMEL_HUSK" -> "750bfc9b2cc40f4d8d0224ccbabac26b338aa947d99dcde769f859b59b8d0b0e";
            default -> null;
        };
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        String[] words = str.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)));
                result.append(word.substring(1).toLowerCase());
                result.append(" ");
            }
        }
        return result.toString().trim();
    }
}
