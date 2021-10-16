package com.pandoaspen.leaderboards.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

public class ConfigLoader {

    private static final Gson GSON = new Gson();

    public static <T> T load(Plugin plugin, String config, Class<T> clazz) {
        File dataFolder = plugin.getDataFolder();

        File file = new File(dataFolder, config);
        if (!file.exists()) {
            plugin.saveResource(config, false);
        }

        return load(plugin.getLogger(), GSON, file, clazz);
    }

    public static <T> T load(Plugin plugin, Gson gson, String config, Class<T> clazz) {
        File dataFolder = plugin.getDataFolder();

        File file = new File(dataFolder, config);
        if (!file.exists()) {
            plugin.saveResource(config, false);
        }

        return load(plugin.getLogger(), gson, file, clazz);
    }

    public static <T> T load(Logger logger, Gson gson, File file, Class<T> clazz) {
        try (InputStream inputStream = new FileInputStream(file);) {
            Yaml yaml = new Yaml();
            Object obj = yaml.load(inputStream);
            JsonElement jsonElement = gson.toJsonTree(obj);
            return gson.fromJson(jsonElement, clazz);
        } catch (Exception e) {
            logger.severe("The plugin configuration couldn't be loaded!");
            e.printStackTrace();
        }
        return null;
    }

}