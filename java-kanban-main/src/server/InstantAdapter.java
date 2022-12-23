package server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;

public class InstantAdapter extends TypeAdapter<Instant> {

    @Override
    public void write(final JsonWriter jsonWriter, final Instant instant) throws IOException {
        if (instant != null) {
            jsonWriter.value(instant.toEpochMilli());
        } else
            jsonWriter.nullValue();
    }

    @Override
    public Instant read(final JsonReader jsonReader) throws IOException {

        String timeAsString = jsonReader.nextString();
        if (timeAsString != null){
            long timeAsLong = Long.parseLong(timeAsString);
            return Instant.ofEpochMilli(timeAsLong);
        }
        return null;
    }
}