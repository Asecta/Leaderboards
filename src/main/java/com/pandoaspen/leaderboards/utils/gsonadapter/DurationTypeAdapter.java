package com.pandoaspen.leaderboards.utils.gsonadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.pandoaspen.leaderboards.utils.Duration;

import java.io.IOException;

public class DurationTypeAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        jsonWriter.value(duration.getDurationString());
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        return new Duration(jsonReader.nextString());
    }
}
