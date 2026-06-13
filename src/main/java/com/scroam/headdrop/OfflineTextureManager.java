package com.scroam.headdrop;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class OfflineTextureManager {

    private static final Map<String, String> TEXTURE_MAP = new HashMap<>();
    private static boolean initialized = false;

    public static void initialize(JavaPlugin plugin) {
        if (initialized) return;
        
        String[] entities = {
            "ALLAY", "ARMADILLO", "AXOLOTL", "BAT", "BEE", "BLAZE", "BOGGED", "BREEZE",
            "CAMEL", "CAMEL_HUSK", "CAT", "CAVE_SPIDER", "CHICKEN", "COD", "COPPER_GOLEM",
            "COW", "CREAKING", "CREEPER", "DOLPHIN", "DONKEY", "DROWNED", "ELDER_GUARDIAN",
            "ENDERMAN", "ENDERMITE", "ENDER_DRAGON", "EVOKER", "FOX", "FROG", "GHAST",
            "GIANT", "GLOW_SQUID", "GOAT", "GUARDIAN", "HOGLIN", "HORSE", "HUSK",
            "ILLUSIONER", "IRON_GOLEM", "LLAMA", "MAGMA_CUBE", "MOOSHROOM", "MULE",
            "NAUTILUS", "OCELOT", "PANDA", "PARCHED", "PARROT", "PHANTOM", "PIG",
            "PIGLIN", "PIGLIN_BRUTE", "PILLAGER", "POLAR_BEAR", "PUFFERFISH", "RABBIT",
            "RAVAGER", "SALMON", "SHEEP", "SHULKER", "SILVERFISH", "SKELETON",
            "SKELETON_HORSE", "SLIME", "SNIFFER", "SNOW_GOLEM", "SPIDER", "SQUID",
            "STRAY", "STRIDER", "TADPOLE", "TRADER_LLAMA", "TROPICAL_FISH", "TURTLE",
            "VEX", "VILLAGER", "VINDICATOR", "WANDERING_TRADER", "WARDEN", "WITCH",
            "WITHER", "WITHER_SKELETON", "WOLF", "ZOGLIN", "ZOMBIE", "ZOMBIE_HORSE",
            "ZOMBIE_VILLAGER", "ZOMBIFIED_PIGLIN"
        };

        for (String entity : entities) {
            try {
                String path = "textures/" + entity + ".png";
                InputStream is = plugin.getResource(path);
                if (is != null) {
                    byte[] data = is.readAllBytes();
                    String base64 = Base64.getEncoder().encodeToString(data);
                    TEXTURE_MAP.put(entity, base64);
                    is.close();
                }
            } catch (IOException e) {
                plugin.getLogger().warning("无法加载纹理: " + entity);
            }
        }
        
        loadOnlineCache(plugin);
        
        initialized = true;
        plugin.getLogger().info("离线纹理管理器已初始化，加载了 " + TEXTURE_MAP.size() + " 种纹理");
    }
    
    private static void loadOnlineCache(JavaPlugin plugin) {
        Path cacheDir = plugin.getDataFolder().toPath().resolve("texture_cache");
        File cacheDirFile = cacheDir.toFile();
        
        if (!cacheDirFile.exists() || !cacheDirFile.isDirectory()) {
            return;
        }
        
        File[] cacheFiles = cacheDirFile.listFiles((dir, name) -> name.endsWith(".dat"));
        if (cacheFiles == null) {
            return;
        }
        
        int loaded = 0;
        for (File file : cacheFiles) {
            try {
                String entityType = file.getName().replace(".dat", "");
                String base64 = Files.readString(file.toPath());
                TEXTURE_MAP.put(entityType, base64);
                loaded++;
            } catch (IOException e) {
                plugin.getLogger().warning("无法读取缓存文件: " + file.getName());
            }
        }
        
        if (loaded > 0) {
            plugin.getLogger().info("从在线缓存加载了 " + loaded + " 种纹理");
        }
    }

    public static String getTextureBase64(String entityType) {
        return TEXTURE_MAP.get(entityType);
    }

    public static boolean hasTexture(String entityType) {
        return TEXTURE_MAP.containsKey(entityType);
    }

    public static int getTextureCount() {
        return TEXTURE_MAP.size();
    }
}
