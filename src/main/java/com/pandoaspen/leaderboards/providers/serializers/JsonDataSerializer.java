package com.pandoaspen.leaderboards.providers.serializers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.pandoaspen.leaderboards.providers.registry.DataEntry;
import com.pandoaspen.leaderboards.providers.registry.PlayerData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class JsonDataSerializer implements IDataSerializer {

    @Getter private static final Gson GSON;


    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();


        gsonBuilder.registerTypeAdapter(DataEntry.class, new TypeAdapter<DataEntry>() {
            @Override
            public void write(JsonWriter jsonWriter, DataEntry dataEntry) throws IOException {
                jsonWriter.value(String.format("%d %f", dataEntry.getTime(), dataEntry.getValue()));
            }

            @Override
            public DataEntry read(JsonReader jsonReader) throws IOException {
                String[] parts = jsonReader.nextString().split(" ");
                long time = Long.parseLong(parts[0]);
                double value = Double.parseDouble(parts[1]);
                return new DataEntry(time, value);
            }
        });

        GSON = gsonBuilder.create();
    }

    @Getter private final JavaPlugin plugin;

    @Override
    public Map<UUID, PlayerData> readData() throws IOException {
        File file = getDataFile(false);
        if (!file.exists()) return new HashMap<>();
        String json = new String(Files.readAllBytes(file.toPath()));
        Type type = new TypeToken<Map<UUID, PlayerData>>() {
        }.getType();
        return GSON.fromJson(json, type);
    }

    @Override
    public void writeData(Map<UUID, PlayerData> data) throws IOException {
        byte[] bytes = GSON.toJson(data).getBytes();
        File file = getDataFile(true);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(bytes);
        fileOutputStream.close();

//        writeBinary(data);
    }

    public void writeBinary(Map<UUID, PlayerData> data) throws IOException {
        File file = new File(getDataFile(false).getParentFile(), getName() + ".bin");

        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        DataOutputStream out = new DataOutputStream(fileOutputStream);

        for (Map.Entry<UUID, PlayerData> entry : data.entrySet()) {
            UUID uuid = entry.getKey();
            PlayerData playerData = entry.getValue();
            out.writeLong(uuid.getLeastSignificantBits());
            out.writeLong(uuid.getMostSignificantBits());
            out.writeUTF(playerData.getPlayerName());
            out.writeInt(data.size());
            for (DataEntry dataEntry : playerData) {
                out.writeLong(dataEntry.getTime());
                out.writeDouble(dataEntry.getValue());
            }
        }
    }


    @Override
    public String getExtention() {
        return "json";
    }
}
