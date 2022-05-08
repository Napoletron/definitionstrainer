package deftrainer.example.definitionstrainer.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import deftrainer.example.definitionstrainer.R;
import deftrainer.example.definitionstrainer.model.DefinitionsManager;
import deftrainer.example.definitionstrainer.model.Fachbereich;

import java.util.Locale;
import java.util.Random;

public class FaecherauswahlActivity  extends AppCompatActivity {

    public static final String FACHBEREICH = "com.example.definitionstrainer:FACHBEREICH";

    private Fachbereich fachbereich;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faecherauswahl);
        setTitle(R.string.subject_selection);

        getFachbereich();
        addButtons();
    }

    /**
     * Diese Methode lädt den zugewiesenen Fachbereich
     */
    private void getFachbereich() {
        fachbereich = (Fachbereich) this.getIntent().getSerializableExtra(FACHBEREICH);
    }

    /**
     * Für jedes Fach im Fachbereich wird ein eigener Button kreiert
     */
    private void addButtons() {

        LinearLayout button_container = findViewById(R.id.button_container);

        for(Fachbereich.Fach fach : fachbereich.fachliste) {
            int total_defs = DefinitionsManager.getDefinitionsManager().getNumberOfDefinitionsOfThatSubject(fach.fachname);
            int mastered_def = DefinitionsManager.getDefinitionsManager().getNumberOfMasteredDefinitionsOfThatSubject(fach.fachname);


            Button btnTag = new Button(this);
            btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            btnTag.setText(String.format(Locale.getDefault(), "%s (%d/%d)", fach.fachname, mastered_def, total_defs));
            btnTag.setId(new Random().nextInt(100000));
            btnTag.setBackgroundColor(getResources().getColor(R.color.polizeihellblau));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 2, 8, 1);
            btnTag.setLayoutParams(params);
            button_container.addView(btnTag);

            if (total_defs > 0) {
                final Context context = this;
                final String fachname = fach.fachname;
                btnTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent abfrage = new Intent(context, Abfrage_Activity.class);
                        DefinitionsManager.getDefinitionsManager().setFilter_string(fachname, false); // nicht optimal... aber funktioniert
                        startActivityForResult(abfrage, 42); //42 is jsut a random number
                    }
                });
            } else {
                btnTag.setActivated(false);
                btnTag.setPaintFlags(btnTag.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                btnTag.setBackgroundColor(getResources().getColor(R.color.polizeihellhellblau));
            }
        }
    }

    /**
     * Wenn man von der Abfrage-Activity zurückkommt werden die Button-Texte aktualisiert
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 42) {// reload the activity to update the button_text
            finish();
            startActivity(getIntent());
        }
    }
}
