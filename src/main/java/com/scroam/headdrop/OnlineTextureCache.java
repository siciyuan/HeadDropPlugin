package com.scroam.headdrop;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OnlineTextureCache {

    private static final Map<String, String> TEXTURE_CACHE = new HashMap<>();
    private static Path cacheDirectory;
    private static boolean initialized = false;
    private static JavaPlugin plugin;

    public static void initialize(JavaPlugin pluginInstance) {
        if (initialized) return;
        
        plugin = pluginInstance;
        cacheDirectory = Paths.get(plugin.getDataFolder().toString(), "texture_cache");
        
        try {
            if (!Files.exists(cacheDirectory)) {
                Files.createDirectories(cacheDirectory);
            }
            loadCachedTextures();
        } catch (IOException e) {
            plugin.getLogger().severe("无法创建纹理缓存目录: " + e.getMessage());
        }
        
        initialized = true;
        plugin.getLogger().info("在线纹理缓存管理器已初始化，缓存目录: " + cacheDirectory);
    }

    private static void loadCachedTextures() {
        try {
            if (!Files.exists(cacheDirectory)) return;
            
            Files.list(cacheDirectory).filter(path -> path.toString().endsWith(".txt")).forEach(path -> {
                try {
                    String fileName = path.getFileName().toString();
                    String entityType = fileName.substring(0, fileName.lastIndexOf('.'));
                    String base64Texture = Files.readString(path);
                    TEXTURE_CACHE.put(entityType, base64Texture);
                } catch (IOException e) {
                    plugin.getLogger().warning("无法加载缓存的纹理: " + path);
                }
            });
            
            plugin.getLogger().info("已从缓存加载 " + TEXTURE_CACHE.size() + " 个纹理");
        } catch (IOException e) {
            plugin.getLogger().warning("无法遍历缓存目录: " + e.getMessage());
        }
    }

    public static String getTextureBase64(String entityType, String textureId) {
        if (TEXTURE_CACHE.containsKey(entityType)) {
            return TEXTURE_CACHE.get(entityType);
        }
        
        String base64Texture = downloadTexture(textureId);
        if (base64Texture != null) {
            TEXTURE_CACHE.put(entityType, base64Texture);
            saveTextureToCache(entityType, base64Texture);
        }
        
        return base64Texture;
    }

    private static String downloadTexture(String textureId) {
        String urlString = "https://textures.minecraft.net/texture/" + textureId;
        
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                plugin.getLogger().warning("下载纹理失败，HTTP状态码: " + responseCode);
                return null;
            }
            
            try (InputStream inputStream = connection.getInputStream()) {
                byte[] data = inputStream.readAllBytes();
                return Base64.getEncoder().encodeToString(data);
            }
            
        } catch (IOException e) {
            plugin.getLogger().warning("下载纹理失败: " + e.getMessage());
            return null;
        }
    }

    private static void saveTextureToCache(String entityType, String base64Texture) {
        try {
            Path cacheFile = cacheDirectory.resolve(entityType + ".txt");
            Files.writeString(cacheFile, base64Texture);
        } catch (IOException e) {
            plugin.getLogger().warning("无法保存纹理到缓存: " + entityType);
        }
    }

    public static boolean hasCachedTexture(String entityType) {
        return TEXTURE_CACHE.containsKey(entityType);
    }

    public static int getCachedTextureCount() {
        return TEXTURE_CACHE.size();
    }

    public static void clearCache() {
        TEXTURE_CACHE.clear();
        try {
            if (Files.exists(cacheDirectory)) {
                Files.list(cacheDirectory).filter(path -> path.toString().endsWith(".txt")).forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        plugin.getLogger().warning("无法删除缓存文件: " + path);
                    }
                });
            }
            plugin.getLogger().info("纹理缓存已清空");
        } catch (IOException e) {
            plugin.getLogger().warning("无法清空缓存目录: " + e.getMessage());
        }
    }
}