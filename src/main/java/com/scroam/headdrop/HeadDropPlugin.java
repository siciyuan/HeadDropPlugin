package com.scroam.headdrop;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class HeadDropPlugin extends JavaPlugin {

    private FileConfiguration config;
    private HeadDropListener listener;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();
        
        if (isOfflineMode()) {
            OfflineTextureManager.initialize(this);
        } else {
            OnlineTextureCache.initialize(this);
        }
        
        listener = new HeadDropListener(this);
        getServer().getPluginManager().registerEvents(listener, this);
        
        getCommand("headdrop").setExecutor(new HeadDropCommand(this));
        
        getLogger().info("HeadDropPlugin 已启用 - 万物皆可掉头颅!");
        
        if (isOfflineMode()) {
            getLogger().info("离线模式已启用，共支持 " + OfflineTextureManager.getTextureCount() + " 种生物纹理");
        } else {
            getLogger().info("在线模式已启用，已缓存 " + OnlineTextureCache.getCachedTextureCount() + " 个纹理");
        }
        
        if (useCustomSkinServer()) {
            getLogger().info("自定义皮肤服务器已启用: " + getSkinServerUrl());
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("HeadDropPlugin 已禁用");
    }

    public double getDropChance() {
        return config.getDouble("drop-chance", 0.1);
    }

    public boolean shouldDropPlayerHeads() {
        return config.getBoolean("drop-player-heads", true);
    }

    public boolean shouldDropMobHeads() {
        return config.getBoolean("drop-mob-heads", true);
    }

    public boolean shouldDropAnimalHeads() {
        return config.getBoolean("drop-animal-heads", true);
    }

    public boolean isMobEnabled(String type) {
        if (!config.getBoolean("mobs.enabled", true)) return false;
        if (!config.getStringList("mobs.blacklist").contains(type)) {
            if (!config.getStringList("mobs.whitelist").isEmpty()) {
                return config.getStringList("mobs.whitelist").contains(type);
            }
            return true;
        }
        return false;
    }

    public boolean isAnimalEnabled(String type) {
        if (!config.getBoolean("animals.enabled", true)) return false;
        if (!config.getStringList("animals.blacklist").contains(type)) {
            if (!config.getStringList("animals.whitelist").isEmpty()) {
                return config.getStringList("animals.whitelist").contains(type);
            }
            return true;
        }
        return false;
    }

    public boolean showHeadNames() {
        return config.getBoolean("show-head-names", true);
    }

    public String getHeadNamePrefix() {
        return config.getString("head-name-prefix", "");
    }

    public String getHeadNameSuffix() {
        return config.getString("head-name-suffix", " 的头颅");
    }

    public boolean showDropMessages() {
        return config.getBoolean("show-drop-messages", true);
    }

    public String getMessage(String key) {
        String message = config.getString("messages." + key, "");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public boolean isOfflineMode() {
        return config.getBoolean("offline-mode", false);
    }

    public boolean useCustomSkinServer() {
        return config.getBoolean("skin-server.enabled", true);
    }

    public String getSkinServerUrl() {
        return config.getString("skin-server.url", "https://littleskin.cn/api/yggdrasil/sessionserver/session/minecraft/profile/");
    }

    public int getSkinServerTimeout() {
        return config.getInt("skin-server.timeout", 5000);
    }
}