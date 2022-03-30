package deftrainer.example.definitionstrainer.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import deftrainer.example.definitionstrainer.R;
import deftrainer.example.definitionstrainer.model.DefinitionsManager;
import deftrainer.example.definitionstrainer.model.Settings;

public class MainActivity extends AppCompatActivity {

    private Button button_about;
    private Button button_definitionHinzufuegen;
    private Button button_einstellungen;
    private Button button_trainieren;
    private Button button_suchen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getAllViews();
        initTrainierenButton();
        initDefinitionHinzufuegenButton();
        initEinstellungenButton();
        initButtonSuchen();
        initButtonAbout();

        // Don't change the order of the following (3) functions!
        Settings.tryToLoadSettings(this); // (1)

        if (Settings.getSettings().getFIRST_INSTALL() || Settings.TESTING) {
            showRestrictedContentDialog();
        }

        DefinitionsManager.getDefinitionsManager().tryToLoadDefinitions(this); // (2)
        Settings.makeUpdatesIfNecessary(this); // (3)

    }

    private void showRestrictedContentDialog() {

        final Context context = this;
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setTitle(R.string.restricted_content);
        adb.setMessage(R.string.restricted_content_may_not_be_inserted);
        adb.setCancelable(false);
        adb.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // stub
            }
        });
        adb.create().show();
    }

    /**
     * this method finds all views for this activity and assigns them
     */
    private void getAllViews() {
        button_definitionHinzufuegen = findViewById(R.id.ma_b_hinzufuegen);
        button_einstellungen = findViewById(R.id.button_einstellungen);
        button_trainieren = findViewById(R.id.fba_b_alleDefinitionen);
        button_suchen = findViewById(R.id.ma_b_suchen);
        button_about = findViewById(R.id.button_about);
    }

    private void initTrainierenButton() {
        final Context context = this;
        button_trainieren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fba_activity = new Intent(context, FachbereichauswahlActivity.class);
                startActivity(fba_activity);
            }
        });
    }

    private void initDefinitionHinzufuegenButton() {
        final Context context = this;
        button_definitionHinzufuegen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent defhinz = new Intent(context, DefinitionHinzufuegenActivity.class);
                startActivity(defhinz);
            }
        });
    }

    private void initEinstellungenButton() {
        final Context context = this;
        button_einstellungen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent facherauswahl = new Intent(context, EinstellungenActivity.class);
                startActivity(facherauswahl);
            }
        });
    }

    private void initButtonSuchen() {
        final Context context = this;
        button_suchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fba_activity = new Intent(context, SuchenActivity.class);
                startActivity(fba_activity);
            }
        });
    }

    private void initButtonAbout() {
        final Context context = this;
        button_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fba_activity = new Intent(context, AboutActivity.class);
                startActivity(fba_activity);
            }
        });
    }
}