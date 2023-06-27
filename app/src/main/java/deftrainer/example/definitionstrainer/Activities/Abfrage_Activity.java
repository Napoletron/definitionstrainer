package deftrainer.example.definitionstrainer.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import deftrainer.example.definitionstrainer.R;
import deftrainer.example.definitionstrainer.model.Definition;
import deftrainer.example.definitionstrainer.model.DefinitionsManager;
import deftrainer.example.definitionstrainer.model.MyTagHandler;
import deftrainer.example.definitionstrainer.model.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Abfrage_Activity extends AppCompatActivity {

    private int progress = 0;
    private boolean nextDefinition = true;
    private List<TextView> checkboxes = new ArrayList<>();
    private Button button_aufloesen;
    private Button button_konnteIch;
    private Button button_besserNochmal;
    private TextView textView_definition_name;
    private TextView textview_definition_definition;
    private TextView textView_skillLevel;
    private TextView textView_fach;
    private Definition currentDefinition = null;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abfrage);
        setTitle(R.string.app_name);

        getViews();
        initButtonAufloesen();
        initButtonBesserNochmal();
        initButtonKonnteIch();
        getNextDefinition();
    }

    /**
     * Finde alle Views der Activity und initialisiere die entsprechenden Variablen
     */
    private void getViews() {
        button_aufloesen = findViewById(R.id.button_aufloesen);
        button_besserNochmal = findViewById(R.id.button_bessernochmal);
        button_konnteIch = findViewById(R.id.button_konnteIch);
        textView_definition_name = findViewById(R.id.textView_definition_name);
        textview_definition_definition = findViewById(R.id.tetView_definition_definition);
        textView_skillLevel = findViewById(R.id.textView_skillLevel);
        textView_fach = findViewById(R.id.af_tv_fach);

        // ist nicht hübsch, aber es funktioniert
        checkboxes.add((TextView)findViewById(R.id.check1));
        checkboxes.add((TextView)findViewById(R.id.check2));
        checkboxes.add((TextView)findViewById(R.id.check3));
        checkboxes.add((TextView)findViewById(R.id.check4));
        checkboxes.add((TextView)findViewById(R.id.check5));
        checkboxes.add((TextView)findViewById(R.id.check6));
        checkboxes.add((TextView)findViewById(R.id.check7));
    }

    /**
     * Wenn der benutzer auf den Auflösen-knopf drückt, wird ihm die komplette Definition angezeigt.
     * Der Knopf wird dann unsichtbar.
     * Anschließend werden die Knöpfe "Besser nochmal" und "Kann ich" angezeigt.
     */
    private void initButtonAufloesen() {
        button_aufloesen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(currentDefinition.getDefinition());
                textview_definition_definition.setText(android.text.Html.fromHtml(currentDefinition.getDefinition(), null, new MyTagHandler()));
                button_aufloesen.setVisibility(View.INVISIBLE);
                button_konnteIch.setVisibility(View.VISIBLE);
                button_besserNochmal.setVisibility(View.VISIBLE);

            }
        });
    }

    /**
     * Wenn der Benutzer den "Besser nochmal"-Knopf drückt, wird der Skill um 3 Punkte dekrementiert.
     * Danach wird die nächste Definition angezeigt.
     */
    private void initButtonBesserNochmal() {
        button_besserNochmal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDefinition.dekrementSkillLevel();
                storeDefinitions();
                makeNegativeProgress();
                getNextDefinition();
            }
        });
    }

    /**
     * Wenn der Benutzer den "Konnte ich"-Kopf drückt, wird der Skill um 1 inkrementiert.
     * Danach wird die nächste Definition angezeigt.
     */
    private void initButtonKonnteIch() {
        button_konnteIch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDefinition.incrementSkillLevel();
                storeDefinitions();
                makePositiveProgress();
                getNextDefinition();
            }
        });
    }

    /**
     * Ruft den Definitonsmanager auf und lässt die Definitionen auf das Gerät schreiben
     */
    private void storeDefinitions() {
        DefinitionsManager.getDefinitionsManager().writeDefinitionsToMemory(this);
    }

    /**
     * Diese Funktion holt sich die nächste Definiton, welche gelernt werden soll und passt entsprechend die Views an.
     */
    private void getNextDefinition() {
        if( !nextDefinition ) {
            return;
        }
        currentDefinition = DefinitionsManager.getDefinitionsManager().getNextDefinition();
        textView_definition_name.setText(currentDefinition.getName());
        textview_definition_definition.setText("");
        textView_skillLevel.setText(String.format(Locale.getDefault(), "Skill: %d/"+ Settings.getSettings().getMaxSkill(), currentDefinition.getSkill()));
        textView_fach.setText(currentDefinition.getFaecher().get(0));
        button_aufloesen.setVisibility(View.VISIBLE);
        button_konnteIch.setVisibility(View.INVISIBLE);
        button_besserNochmal.setVisibility(View.INVISIBLE);
    }

    /**
     * Diese Funktion aktuallisiert den Fortschrittsbalken um einen negativen Eintrag.
     * Dabei wird ein rotes Kreuz in den Fortschrittsbalken eingetragen.
     * Falls der Fortschrittsbalken voll ist, wird der Benutzer zurück zur Fächerauswahl gebracht
     * (so als ob er "zurück" auf dem Handy drücken würde").
     */
    private void makeNegativeProgress() {
        // Suche die Checkbox die von "O" zu "X" geändert werden soll.
        TextView tv = checkboxes.get(progress);
        //tv.setText("✘");
        //tv.setTextColor(getResources().getColor(R.color.falseRed));
        tv.setBackgroundColor(getResources().getColor(R.color.ButtonRed));
        progress++;
        checkForReturnBack();
    }

    /**
     * Diese Funktion aktuallisiert den Fortschrittsbalken um einen positiven Eintrag.
     * Dabei wird ein grüner Haken in den Fortschrittsbalken eingetragen.
     * Falls der Fortschrittsbalken voll ist, wird der Benutzer zurück zur Fächerauswahl gebracht
     * (so als ob er "zurück" auf dem Handy drücken würde").
     */
    private void makePositiveProgress() {
        // Suche die Checkbox die von "O" zu "X" geändert werden soll.
        TextView tv = checkboxes.get(progress);
        //tv.setText("✓"); //🗸
        //tv.setTextColor(getResources().getColor(R.color.correctGreen));
        tv.setBackgroundColor(getResources().getColor(R.color.ButtonGreen));
        progress++;
        checkForReturnBack();
    }

    /**
     * Falls der Fortschrittsbalken voll ist, wird der Benutzer zurück zum Fächermenü gebracht.
     */
    private void checkForReturnBack() {
        if (progress >= 7) {
            nextDefinition = false;
            button_konnteIch.setEnabled(false);
            button_besserNochmal.setEnabled(false);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    onBackPressed();
                }
            }, 1000);   //5 seconds
        }
    }
}