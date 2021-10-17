package com.pandoaspen.leaderboards.utils.gsonadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.ChatColor;

import java.io.IOException;

public class ColoredStringAdapter extends TypeAdapter<String> {

    private static final String COLOR_CHAR = Character.toString(ChatColor.COLOR_CHAR);

    @Override
    public void write(JsonWriter jsonWriter, String s) throws IOException {
        jsonWriter.value(s.replaceAll(COLOR_CHAR, "&"));
    }

    @Override
    public String read(JsonReader jsonReader) throws IOException {
        return ChatColor.translateAlternateColorCodes('&', jsonReader.nextString());
    }
}
