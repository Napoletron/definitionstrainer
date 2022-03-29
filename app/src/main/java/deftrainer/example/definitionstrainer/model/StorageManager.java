package deftrainer.example.definitionstrainer.model;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

// kann sein, dass er die lokale library nicht findet
// dann einfach die entsprechende .jar Datei extern irgendwo speichern und
// den absoluten Pfad in build.gradle zu der .jar Datei anpassen.
import com.fasterxml.jackson.databind.*;

public class StorageManager {

    // This path leads to the file where the Uri is stored.
    // The Uri leads to the JSON file of the Definitions
    public static final String PATHTODEFINITIONSOBJ= "definitions.obj";
    public static final String PATHTODEFINITIONSJSON= "definitions.json";
    public static final String PATH_TO_DEFAULT_OBJ_DEFINITIONS = "definitions.obj";
    public static final String PATH_TO_INTERNAL_SETTINGS = "settings.obj";
    //public static final String EXPORT_PATH = "/Download/definitions.json";
    public static final String EXPORT_PATH = "/Download/definitions.txt";

    public static void storeExternalFile(Activity activity, String path, String ... message) {
        try {
            // important!! ask for permission
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
            // get the file
            File dest = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + EXPORT_PATH);

            // open the streams and close them again
            FileOutputStream fos = new FileOutputStream(dest);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            //
            if (message.length != 0) {
                bos.write(message[0].getBytes());
            } else {
                String jsonString = getJSONString(DefinitionsManager.getDefinitionsManager().getAllDefinitions());
                bos.write(jsonString.getBytes());
            }

            bos.close();
            fos.close();

        } catch (FileNotFoundException fnfe) {
            Log.e("StorageManager", "storeExternalFile", fnfe);
        } catch (IOException e) {
            Log.e("StorageManager", "storeExternalFile", e);
        }
    }

    public static Object loadExternalFile(Activity activity, String path, boolean fileContentIsString) {
        Object o = null;
        try {
            // important!! ask for permission
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 123);

            File src = new File(path);

            if ( !fileContentIsString) {
                FileInputStream fis = new FileInputStream(src);
                ObjectInputStream ois = new ObjectInputStream(fis);
                o = ois.readObject();
                ois.close();
                fis.close();
            } else {
                BufferedReader bis = new BufferedReader(new FileReader(src));
                StringBuilder s = new StringBuilder();
                String tmp;
                while (( tmp = bis.readLine()) != null) {
                    s.append(tmp);
                }
                bis.close();
                o = s.toString();
            }

        } catch (ClassNotFoundException cnfe) {
            Log.e("StorageManager", "storeExternalFile", cnfe);
        } catch (IOException e) {
            Log.e("StorageManager", "storeExternalFile", e);
        }
        return o;
    }

    public static void storeInternalFile(String filename, Object objectToStore, Context c) {
        try {
            FileOutputStream fos = c.openFileOutput(filename, Context.MODE_PRIVATE);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            oos.writeObject(objectToStore);

            oos.close();
            bos.close();
            fos.close();

        } catch (IOException e) {
            Log.e("InternalStorageManager", "Irgend ein Problem mit den output Streams", e);
        }
    }

    /**
     * Try to load an internal File.
     * Might return null!
     */
    public static Object loadInternalFile(String filename, Context c){
        Object objectToLoad = null;
        try {
            FileInputStream fis = c.openFileInput(filename);
            BufferedInputStream bis = new BufferedInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(bis);

            objectToLoad = ois.readObject();

            ois.close();
            bis.close();
            fis.close();

        } catch (FileNotFoundException fnfe) {
            Log.e("ERROR", "InternalStorageManager: Could not load file");
        } catch (IOException e) {
            Log.e("ERROR", "InternalStorageManager: loadInternalFile - IOException");
        } catch (ClassNotFoundException cnfe) {
            Log.e("ERROR", "InternalStorageManager: loadInternalFile - ClassNotFoundException");
        }
        return objectToLoad;
    }


    /**
     * Loads an object from an asset file.
     * @param name the filename i.e. "definitions.obj"
     * @param c The context
     * @return the loaded object, or null if an exception occurs.
     */
    public static Object loadAssetObject(String name, Context c) throws ClassNotFoundException{
        Object objectToLoad = null;
        AssetManager assetManager = c.getAssets();

        try {
            InputStream is = assetManager.open(name);
            ObjectInputStream ois = new ObjectInputStream(is);

            objectToLoad = ois.readObject();

            ois.close();
            is.close();
        } catch (IOException ioe) {
            Log.e("InternalStorageManager", "loadAssetFile", ioe);
        } catch (ClassNotFoundException cnfe) {
            Log.e("InternalStorageManager", "loadAssetFile", cnfe);
            throw(cnfe);
        }

        return objectToLoad;
    }

    /**
     * This method tries to convert the Java-Object to a JSon-String
     * @param definitions the definition List to convert
     * @return hopefully the JSON-String
     */
    public static String getJSONString(List<Definition> definitions) {
        String jsonString = null;
        try {
            ObjectMapper mapper = new ObjectMapper();

            // Converting the Object to JSONString
            jsonString = mapper.writeValueAsString(definitions);

        } catch (IOException ioe){
            Log.e("derp", "derp", ioe);
        }
        return jsonString;
    }

/*
    private static List<Definition> convertDefinitionToNewDefinition(List<Definition> definitions) {
        int ID = 0;
        String jahrgang = "20S";
        List<String> jahrgaenge = new ArrayList<>();
        List<Definition> newDefinitions = new ArrayList<>();

        jahrgaenge.add(jahrgang);

        for(Definition d : definitions) {
            Definition newDef = new Definition(ID, d.getName(), d.getDefinition(), d.getSkill(), d.getFaecher(), jahrgaenge);
            newDefinitions.add(newDef);
            ID++;
        }

        return newDefinitions;
    }
 */
}
