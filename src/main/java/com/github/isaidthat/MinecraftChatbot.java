package com.github.isaidthat;
import org.bukkit.plugin.java.JavaPlugin;

public class MinecraftChatbot extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Plugin enabled!");

        saveDefaultConfig();

        getCommand("chat").setExecutor(new Chat(this));
        getCommand("apikey").setExecutor(new ApiKey(this));
        getCommand("model").setExecutor(new Model(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled!");
    }
}