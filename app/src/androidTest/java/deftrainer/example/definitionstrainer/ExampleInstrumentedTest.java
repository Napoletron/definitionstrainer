package deftrainer.example.definitionstrainer;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import deftrainer.example.definitionstrainer.model.Definition;
import deftrainer.example.definitionstrainer.model.JSONReader;
import deftrainer.example.definitionstrainer.model.StorageManager;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    Context appContext;
    List<Definition> definitions_json;
    List<Definition> definitions_internal;

    @Before
    public void initTest() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Try to load internal definitions
        Object o = StorageManager.loadInternalFile(StorageManager.PATHTODEFINITIONSOBJ, appContext);
        if (o instanceof List) {
            definitions_internal = (List<Definition>) o;
        } else {
            fail("Unable to load internal definitions!");
        }

        // try to load external json definitions
        try {
            definitions_json = JSONReader.readJsonStream(appContext.getAssets().open(StorageManager.PATHTODEFINITIONSJSON));
        } catch (IOException ioe2) {
            fail("Could not read JSON");
        }
    }

    @Test
    public void assertIdsAreOnyUsedOnce() {
        Set<Integer> ids = new HashSet<>();

        for(Definition d : definitions_json) {
            Integer id = d.getID();
            if(ids.contains(id)) {
                fail("Definition-ID *"+id+"* was used at least twice!");
            } else {
                ids.add(id);
            }
        }
    }
}