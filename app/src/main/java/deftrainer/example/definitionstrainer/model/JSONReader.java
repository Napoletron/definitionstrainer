package deftrainer.example.definitionstrainer.model;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse ist weitestgehenst kopiert von
 * https://developer.android.com/reference/android/util/JsonReader
 */
public class JSONReader {

    public static List<Definition> parseJsonString(String jsonString) {
        List<Definition> definitions = null;
        try {
            definitions = readDefinitionsArray(new JsonReader(new StringReader(jsonString)));
        } catch (IOException e) {
            Log.e("JSONREADER", "could not read from file", e);
        }
        return definitions;
    }

    public static List<Definition> readJsonStream(InputStream in) throws IOException {
        List<Definition> definitions = null;
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            definitions = readDefinitionsArray(reader);
        } catch (IOException e) {
            Log.e("JSONREADER", "could not read from file", e);
        } finally {
            reader.close();
        }
        return definitions;
    }

    private static List<Definition> readDefinitionsArray(JsonReader reader) throws IOException {
        List<Definition> definitions = new ArrayList<Definition>();

        reader.beginArray();
        while (reader.hasNext()) {
            definitions.add(readDefinition(reader));
        }
        reader.endArray();
        return definitions;
    }

    private static Definition readDefinition(JsonReader reader) throws IOException {
        int skill = -1;
        int id = 0;
        String name = null;
        String definition = null;
        List<String> faecher = null;
        List<String> jahrgaenge = null;
        boolean userModified = false;

        reader.beginObject();
        while (reader.hasNext()) {
            String entry_name = reader.nextName();
            if (entry_name.equals("skill")) {
                skill = reader.nextInt();
            } else if (entry_name.equals("definition")) {
                definition = reader.nextString();
            } else if (entry_name.equals("id")) {
                id = reader.nextInt();
            } else if (entry_name.equals("faecher")) {
                faecher = readFaecher(reader);
            } else if (entry_name.equals("jahrgaenge")) {
                jahrgaenge = readJahrgaenge(reader);
            } else if (entry_name.equals("name")) {
                name = reader.nextString();
            } else if (entry_name.equals("userModified")) {
                userModified = reader.nextBoolean();
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Definition(id, name, definition, skill, faecher, jahrgaenge, userModified);
    }

    private static List<String> readFaecher(JsonReader reader) throws IOException {
        List<String> faecher = new ArrayList<String>();

        reader.beginArray();
        while (reader.hasNext()) {
            /*
            reader.beginObject();
            String name = reader.nextName();
            String fachName = reader.nextString();
            faecher.add(fachName);
            reader.endObject();
             */
            faecher.add(reader.nextString());

        }
        reader.endArray();
        return faecher;
    }

    private static List<String> readJahrgaenge(JsonReader reader) throws IOException {
        List<String> faecher = new ArrayList<String>();

        reader.beginArray();
        while (reader.hasNext()) {
            /*
            reader.beginObject();
            String name = reader.nextName();
            String fachName = reader.nextString();
            faecher.add(fachName);
            reader.endObject();
             */
            faecher.add(reader.nextString());

        }
        reader.endArray();
        return faecher;
    }
}
