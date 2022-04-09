package deftrainer.example.definitionstrainer.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import deftrainer.example.definitionstrainer.R;
import deftrainer.example.definitionstrainer.model.DefinitionsManager;
import deftrainer.example.definitionstrainer.model.Fachbereich;
import deftrainer.example.definitionstrainer.model.Settings;

public class FachbereichauswahlActivity extends AppCompatActivity  {


    private Button button_facherunterricht;
    private Button button_streife;
    private Button button_verkehr;
    private Button button_kriminalitaet;
    private Button button_alle_Definitionen;

    private TextView fba_tv_entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fachbereichauswahl);
        setTitle(R.string.area_selection);

        getAllViews();
        initFachbereichButtons();
        initAlleDefinitionenButton();
        initEntryText();
    }

    @Override
    public void onResume() {
        super.onResume();
        initEntryText();
    }


    /**
     * this method finds all views for this activity and assigns them
     */
    private void getAllViews() {
        button_facherunterricht = findViewById(R.id.fba_b_facherunterricht);
        button_streife = findViewById(R.id.fba_b_streife);
        button_verkehr = findViewById(R.id.fba_b_verkehr);
        button_kriminalitaet = findViewById(R.id.fba_b_kriminalitaet);
        button_alle_Definitionen = findViewById(R.id.fba_b_alleDefinitionen);
        fba_tv_entry = findViewById(R.id.fba_tv_entry);
    }

    /**
     * This method initializes all the button from this activity
     */
    private void initFachbereichButtons() {

        class MyOnClickListener implements View.OnClickListener {
            Fachbereich fachbereich;
            Context context;

            public MyOnClickListener(Fachbereich fachbereich, Context context) {
                super();
                this.fachbereich = fachbereich;
                this.context = context;
            }

            @Override
            public void onClick(View view) {
                Intent facherauswahl = new Intent(context, FaecherauswahlActivity.class);
                facherauswahl.putExtra(FaecherauswahlActivity.FACHBEREICH, fachbereich);
                startActivity(facherauswahl);
            }
        }

        button_facherunterricht.setOnClickListener(new MyOnClickListener(Fachbereich.EINSATZUNDFUEHRUNGSWISS, this));
        button_streife.setOnClickListener(new MyOnClickListener(Fachbereich.KRIMINALITAET, this));
        button_verkehr.setOnClickListener(new MyOnClickListener(Fachbereich.RECHT, this));
        button_kriminalitaet.setOnClickListener(new MyOnClickListener(Fachbereich.SOZIALWISS, this));

    }

    private void initAlleDefinitionenButton() {
        if (DefinitionsManager.getDefinitionsManager().getImportantDefinitions().size() == 0) {
            button_alle_Definitionen.setActivated(false);
            return;
        }
        final Context context = this;
        button_alle_Definitionen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent abfrage = new Intent(context, Abfrage_Activity.class);
                DefinitionsManager.getDefinitionsManager().setFilter_string(""); // filter="" sorgt dafür, dass alle Fächer abgefragt werden
                startActivity(abfrage);
            }
        });
    }

    private void initEntryText() {
        int number_masterd_defs = DefinitionsManager.getDefinitionsManager().getTotalNumberOfMasteredDefs();
        int number_total_defs = DefinitionsManager.getDefinitionsManager().getImportantDefinitions().size();
        String s1 = getResources().getString(R.string.you_have_mastered);
        String s2 = getResources().getString(R.string.of);
        String s3 = getResources().getString(R.string.definitions_in_the_class);
        String s4 = getResources().getString(R.string.gemeistert);
        String fach = Settings.getSettings().getKlasse();

        String entry_text_string = String.format("%s %d %s %d %s %s %s", s1, number_masterd_defs, s2, number_total_defs, s3, fach, s4);
        fba_tv_entry.setText(entry_text_string);
    }
}
