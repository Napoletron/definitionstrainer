package deftrainer.example.definitionstrainer.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import deftrainer.example.definitionstrainer.R;
import deftrainer.example.definitionstrainer.model.Definition;
import deftrainer.example.definitionstrainer.model.DefinitionsManager;
import deftrainer.example.definitionstrainer.model.JSONReader;
import deftrainer.example.definitionstrainer.model.Settings;
import deftrainer.example.definitionstrainer.model.StorageManager;

import java.io.File;
import java.util.List;

public class EinstellungenActivity extends AppCompatActivity {

    private static final int RESULTCODE_IMPORT = 43;

    private Button set_b_export;
    private Button set_b_import;
    private Button button_reset;
    private Button set_b_resetSkill;
    private Button set_b_save;
    private Button set_b_cheat;
    private EditText set_et_increase;
    private EditText set_et_decrease;
    private Spinner set_spin_class;
    private TextView set_tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.settings);

        getAllViews();
        initExportButton();
        initImportButton();
        initResetSkillButton();
        initResetButton();
        initSaveButton();
        initCheatButton();
        initClassSpinner();

        update_editTextFields();
        update_spinner();
    }

    private void initClassSpinner() {
        final Context context = this;
        List<String> klassen = DefinitionsManager.getDefinitionsManager().getAllClasses();
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, klassen);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        set_spin_class.setAdapter(aa);

        set_spin_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // stub
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // stub
            }
        });
    }

    private void getAllViews() {
        set_b_export = findViewById(R.id.set_b_export);
        set_b_import = findViewById(R.id.set_b_import);
        set_b_resetSkill = findViewById(R.id.set_b_resetSkill);
        set_b_cheat = findViewById(R.id.set_b_cheat);
        set_b_save = findViewById(R.id.set_b_save);
        button_reset = findViewById(R.id.button_reset2);

        set_et_increase = findViewById(R.id.set_et_increase);
        set_et_decrease = findViewById(R.id.set_et_decrease);

        set_tv_version = findViewById(R.id.set_tv_version);
        set_tv_version.setText("Version: "+Settings.getSettings().getVersion());

        set_spin_class = findViewById(R.id.set_spin_class);
    }

    /**
     * Diese Methode aktualisiert die zwei Zahlenfelder.
     * Die Einstellungen werden ausgelesen und in das Zahlenfeld eingefügt.
     */
    private void update_editTextFields() {
        set_et_increase.setText(String.valueOf(Settings.getSettings().getIncrease()));
        set_et_decrease.setText(String.valueOf(Settings.getSettings().getDecrease()));
    }

    private void update_spinner() {
        String default_klasse = Settings.getSettings().getKlasse();
        int pos = DefinitionsManager.getDefinitionsManager().getAllClasses().indexOf(default_klasse);
        set_spin_class.setSelection(pos);
    }
    /**
     * Wenn der Benutzer eine Datei exportieren will...
     * 1) Wird er darauf hingewiesen, wohin die Datei exportiert wird und dass diese ggf. überschrieben wird.
     * 2) Er wird nochmals nach der Berechtigung für den Zugriff gefragt.
     * 3) Die Datei wird exportiert
     * 4) Ein toast erscheint wenn alles gut lief.
     */
    private void initExportButton() {
        if ( !Settings.TESTING ) {
            set_b_export.setVisibility(View.INVISIBLE);
            set_b_export.setActivated(false);
            return;
        }
        final Activity activity = this;
        set_b_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder adb = new AlertDialog.Builder(activity);
                adb.setTitle(R.string.export);
                adb.setMessage(R.string.export_to_downloads);
                adb.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXPORT);
                    }
                });
                adb.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // stub
                    }
                });
                adb.create().show();
            }
        });
    }

    /**
     * Kreiert den "Importieren"-Knopf.
     * Wenn der Benutzer diesen drückt, wird ein File-Picker angezeigt, mit dem er die entsprechende Datei auswählen kann.
     * Danach wird er darauf hingewiesen, dass seine aktuelle Sammlung überschrieben wird.
     * Bestätigt er dies, wird die neue Sammlung wenn möglich importiert und gesichert.
     */
    private void initImportButton() {
        final Activity activity = this;
        if ( !Settings.TESTING ) {
            set_b_import.setVisibility(View.INVISIBLE);
            set_b_import.setActivated(false);
            return;
        }
        set_b_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, IMPORT);
            }
        });
    }

    private void evoke_store_external() {
        if (Settings.TESTING) {
            /*
            StringBuilder sb = new StringBuilder();
            for (Definition d : DefinitionsManager.getDefinitionsManager().getAllDefinitions()) {
                sb.append(d.getName());
                sb.append(" ");
                sb.append(d.getFaecher().toString());
                sb.append(":\n");
                sb.append(d.getDefinition());
                sb.append("\n\n");
            }
            StorageManager.storeExternalFile(this, StorageManager.EXPORT_PATH, sb.toString());
            */
            StorageManager.storeExternalFile(this, StorageManager.EXPORT_PATH);
        } else {
            StorageManager.storeExternalFile(this, StorageManager.EXPORT_PATH);
        }
        Toast.makeText(this, R.string.export_was_successful, Toast.LENGTH_SHORT).show();
    }

    private static final int EXPORT = 42;
    private static final int IMPORT = 41;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if (requestCode == EXPORT) {
            // run through all the permissions
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];
                // if one of them matches the WRITE_EXTERNAL_STORAGE ...
                if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // ... and the permission was granted
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        // ==> store the external file
                        evoke_store_external();
                    } else {
                        // otherwise promt the user, that he denied the storage access
                        Toast.makeText(this, R.string.you_need_to_grant_permission_to_access_the_external_storage, Toast.LENGTH_LONG).show();
                    }
                }
            }
        } else if (requestCode == IMPORT) {
            // run through all the permissions
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];
                // if one of them matches the WRITE_EXTERNAL_STORAGE ...
                if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // ... and the permission was granted
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        // ==> open the import dialog
                        Intent chooser = new Intent(Intent.ACTION_GET_CONTENT);
                        chooser.setType("*/*");
                        startActivityForResult(chooser, RESULTCODE_IMPORT);
                    } else {
                        // otherwise promt the user, that he denied the storage access
                        Toast.makeText(this, R.string.you_need_to_grant_permission_to_access_the_external_storage, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

    }


    /**
     * Dieser Knopf ermöglicht es, die App auf Werkseinstellungen zurückzusetzen.
     * Der Benutzer wird entsprechend davor nochmals auf den evt. Verlust seiner Sammlung hingewiesen.
     */
    private void initResetSkillButton() {
        final Context context = this;
        set_b_resetSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder adb = new AlertDialog.Builder(context);
                adb.setTitle(R.string.reset);
                adb.setMessage(R.string.do_you_want_to_reset_the_skill);
                adb.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        resetSkill();
                    }
                });
                adb.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // stub
                    }
                });
                adb.create().show();
            }
        });
    }

    private void initSaveButton() {
        set_b_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettings();
            }
        });
    }

    /**
     * This method inits the complete reset button
     */
    private void initResetButton() {
        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCompleteResetDialog();
            }
        });
    }

    /**
     * This method inits the cheat button.
     * The cheat button will set all skill levels to MAX
     */
    private void initCheatButton() {
        final Context context = this;
        set_b_cheat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder adb = new AlertDialog.Builder(context);
                adb.setTitle(R.string.cheat);
                adb.setMessage(R.string.set_skill_to_max);
                adb.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setSkillToMax();
                    }
                });
                adb.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // stub
                    }
                });
                adb.create().show();
            }
        });
    }

    /**
     * Diese Methode zeigt den Reset Dialog an
     */
    private void showCompleteResetDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(R.string.reset);
        adb.setMessage(R.string.do_you_want_to_reset_the_app);
        adb.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                completeReset();
            }
        });
        adb.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // stub
            }
        });
        adb.create().show();
    }

    /**
     * Diese Methode zeigt den ImportDialog an
     * @param data das Ergebnis aus dem File-Chooser Dialog
     */
    private void showImportDialog(final Intent data) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(R.string.importt);
        adb.setMessage(R.string.import_and_overwrite_definitions);
        adb.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                importDefinitions(data);
            }
        });
        adb.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // stub
            }
        });
        adb.create().show();
    }

    /**
     * Diese Methode setzt den Skill der Definitionen auf den Minimalen-Wert zurück.
     */
    private void resetSkill() {
        List<Definition> definitions = DefinitionsManager.getDefinitionsManager().getImportantDefinitions();
        for (Definition d : definitions) {
            d.resetSkill();
        }
        DefinitionsManager.getDefinitionsManager().writeDefinitionsToMemory(this);
        Toast.makeText(this, R.string.skill_reset_successful, Toast.LENGTH_SHORT).show();
    }

    /**
     * Diese Methode setzt den Skill der Definitionen auf den Maximalen-Wert.
     */
    private void setSkillToMax() {
        List<Definition> definitions = DefinitionsManager.getDefinitionsManager().getImportantDefinitions();
        for (Definition d : definitions) {
            d.setMaxSkill();
        }
        DefinitionsManager.getDefinitionsManager().writeDefinitionsToMemory(this);
        Toast.makeText(this, R.string.cheat_was_successful, Toast.LENGTH_SHORT).show();
    }

    /**
     * Diese Methode liest die beiden Zahlenfelder aus und speichert die Einstellungen
     */
    private void saveSettings() {
        int increase = Integer.parseInt(set_et_increase.getText().toString());
        int decrease = Integer.parseInt(set_et_decrease.getText().toString());
        List<String> klassen = DefinitionsManager.getDefinitionsManager().getAllClasses();
        Settings.getSettings().setKlasse(klassen.get(set_spin_class.getSelectedItemPosition()), this);
        DefinitionsManager.getDefinitionsManager().update_important_definitions();

        if (!checkCorrectnesOfValues(increase, decrease)) {
            return;
        }

        Settings.getSettings().setIncrease(increase, this);
        Settings.getSettings().setDecrease(decrease, this);

        Toast.makeText(this, R.string.settings_were_saved, Toast.LENGTH_SHORT).show();
    }

    /**
     * If input values are not correct -> return false
     */
    private boolean checkCorrectnesOfValues(int increase, int decrease) {
        if ( increase <= 0 || 10 < increase) {
            Toast.makeText(this, "Increase must have a value between 1 and 10", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (decrease < 0  || 10 < decrease) {
            Toast.makeText(this, "Decrease must have a value between 0 and 10", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Diese Methode setzt die gesamte App auf Werkeinstellungen zurück
     */
    private void completeReset() {
        File f = this.getFilesDir();
        File file_obj = new File(f, StorageManager.PATHTODEFINITIONSOBJ);
        file_obj.delete();
        Settings.reset(this);
        DefinitionsManager.getDefinitionsManager().reset(this);

        Settings.tryToLoadSettings(this);
        DefinitionsManager.getDefinitionsManager().tryToLoadDefinitions(this);
        Settings.makeUpdatesIfNecessary(this);

        DefinitionsManager.getDefinitionsManager().ask_to_update_definitions(this);
        //onBackPressed();
        Toast.makeText(this, R.string.reset_was_successful, Toast.LENGTH_SHORT).show();
    }

    /**
     * Diese Methode importiert die Definitionssammlung
     * @param data das Ergebnis aus dem File-Chooser Dialog
     */
    private void importDefinitions(final Intent data) {
        String import_path = data.getData().getPath();
        import_path = adjustPath(import_path);

        List<Definition> definitionList = null;
        if (import_path.endsWith("json")) {
            Object o = StorageManager.loadExternalFile(this, import_path, true);
            String JSONString = (String) o;
            definitionList = JSONReader.parseJsonString(JSONString);
        } else if (import_path.endsWith("obj")) {
            Object o = StorageManager.loadExternalFile(this, import_path, true);
            definitionList = (List<Definition>) o;
        } else {
            Log.e("EinstellungsActivity", "Unbekanntes Object geladen");
        }
        int definitionsAdded = DefinitionsManager.getDefinitionsManager().addDefinitions(definitionList);
        Toast.makeText(this, definitionsAdded + " " + getString(R.string.definitions_were_added), Toast.LENGTH_SHORT).show();
    }

    /**
     * Diese Methode wird aufgerufen, sobald die File-Chooser-Activity beendet wird
     * @param requestCode sollte RESULTCODE_IMPORT sein
     * @param resultCode egal
     * @param data das Ergebnis des Intend
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULTCODE_IMPORT) {
            showImportDialog(data);
        }
    }

    /**
     * Verwandelt:
     * /documents/primary:Download/derp.json
     * zu
     * /<externalDir>/Download/derp.json
     * @return Einen path, den man benutzen kann um einen File zu erzeugen
     */
    private String adjustPath(String path) {
        int idx = path.indexOf(":");
        path = path.substring(idx+1);
        path = Environment.getExternalStorageDirectory() + "/" + path;
        return path;
    }
}
