package service;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeAdapter extends TypeAdapter<ZonedDateTime> {

    private static final DateTimeFormatter formatterWriter = DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss Z");
    private static final DateTimeFormatter formatterReader = DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss Z");

    @Override
    public void write(final JsonWriter jsonWriter, final ZonedDateTime localDate) throws IOException {
        if (localDate != null) {
            jsonWriter.value(localDate.format(formatterWriter));
        }
    }

    @Override
    public ZonedDateTime read(final JsonReader jsonReader) throws IOException {
        return ZonedDateTime.parse(jsonReader.nextString(), formatterReader);
    }
}
