package deftrainer.example.definitionstrainer.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import deftrainer.example.definitionstrainer.R;
import deftrainer.example.definitionstrainer.model.Definition;
import deftrainer.example.definitionstrainer.model.DefinitionsManager;
import deftrainer.example.definitionstrainer.model.RecyclerViewAdapter;
import deftrainer.example.definitionstrainer.model.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SuchenActivity extends AppCompatActivity {

    private EditText editText_suchen;
    private Button button_suchen;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private TextView s_tv_results;
    private TextView s_tv_showOnly;
    private CheckBox s_box_showOnly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suchen);
        setTitle(R.string.search);

        getViews();
        initRecylclerview();
        initSuchenButton();
        initSuchenEditText();
        initTextViewShowOnly();
        initChipShowOnly();
    }

    private void initChipShowOnly() {
        s_box_showOnly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                search();
            }
        });
    }

    private void initTextViewShowOnly() {
        String klasse = Settings.getSettings().getKlasse();
        s_tv_showOnly.setText("Zeige nur "+klasse);
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerViewAdapter.notifyDataSetChanged();
        search();
    }

    private void getViews() {
        editText_suchen = findViewById(R.id.s_tv_suchen);
        button_suchen = findViewById(R.id.s_b_suchen);
        recyclerView = findViewById(R.id.s_rv);
        s_tv_results = findViewById(R.id.s_tv_results);
        s_tv_showOnly = findViewById(R.id.s_tv_showOnly);
        s_box_showOnly = findViewById(R.id.s_box_OnlyJahrgang);
    }

    private void initSuchenButton() {
        button_suchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
    }

    private void initSuchenEditText() {
        editText_suchen.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // stub
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // stub
            }

            @Override
            public void afterTextChanged(Editable editable) {
                search();
            }
        });
    }

    private void initRecylclerview() {

        recyclerViewAdapter = new RecyclerViewAdapter(DefinitionsManager.getDefinitionsManager().getAllDefinitions(), this);

        // Add the following lines to create RecyclerView
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void search() {
        String searchText = editText_suchen.getText().toString();

        List<Definition> allDefinitions = DefinitionsManager.getDefinitionsManager().getAllDefinitions();
        List<Definition> definitionList = new ArrayList<>();

        if (s_box_showOnly.isChecked()) {
            String klasse = Settings.getSettings().getKlasse();
            for (Definition d : allDefinitions) {
                String s1 = d.getJahrgaengeString().toLowerCase();
                String s2 = klasse.toLowerCase();
                if (s1.contains(s2)) {
                    definitionList.add(d);
                }
            }
        } else {
            definitionList = allDefinitions;
        }

        Set<Definition> relevant_def = new TreeSet<>();

        // first, add all Definitions with the matching name
        for (Definition d : definitionList) {
            if (d.getName().toLowerCase().contains(searchText.toLowerCase()))
                relevant_def.add(d);
        }

        // first, add all Definitions with the matching subject
        for (Definition d : definitionList) {
            String faecher = d.getFeacherString();
            if(faecher.toLowerCase().contains(searchText.toLowerCase())) {
                relevant_def.add(d);
            }
        }

        // first, add all Definitions with the matching jahrgang
        for (Definition d : definitionList) {
            String jahrgaenge = d.getJahrgaengeString();
            if(jahrgaenge.toLowerCase().contains(searchText.toLowerCase())) {
                relevant_def.add(d);
            }
        }

        s_tv_results.setText(relevant_def.size() + " Treffer");

        recyclerViewAdapter.setVisibleDefinitions(new ArrayList<>(relevant_def));
    }
}