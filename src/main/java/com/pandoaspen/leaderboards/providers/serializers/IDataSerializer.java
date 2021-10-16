package com.pandoaspen.leaderboards.providers.serializers;

import com.pandoaspen.leaderboards.providers.registry.DataRegistry;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public interface IDataSerializer {

    String getName();

    Map<UUID, DataRegistry> readData() throws IOException;

    void writeData(Map<UUID, DataRegistry> data) throws IOException;

    String getExtention();

    JavaPlugin getPlugin();

    default File getDataFile(boolean create) throws IOException {
        File parent = new File(getPlugin().getDataFolder(), "data");

        String extention = getExtention();
        if (!extention.startsWith(".")) {
            extention = "." + extention;
        }

        String name = getName();
        if (!name.toUpperCase().endsWith(extention.toUpperCase())) {
            name = name + extention;
        }

        if (!parent.exists()) {
            parent.mkdirs();
        }

        File file = new File(parent, name);
        if (!file.exists()) {
            if (create) {
                file.createNewFile();
            }
        }

        return file;
    }
}
