package deftrainer.example.definitionstrainer.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import deftrainer.example.definitionstrainer.R;
import deftrainer.example.definitionstrainer.model.Definition;
import deftrainer.example.definitionstrainer.model.DefinitionsManager;
import deftrainer.example.definitionstrainer.model.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SuchenActivity extends AppCompatActivity {

    private EditText editText_suchen;
    private Button button_suchen;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suchen);
        setTitle(R.string.search);

        getViews();
        initRecylclerview();
        initSuchenButton();
        initSuchenEditText();
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

        List<Definition> definitionList = DefinitionsManager.getDefinitionsManager().getAllDefinitions();
        List<Definition> relevant_def = new ArrayList<>();
        for (Definition d : definitionList) {
            if (d.getName().toLowerCase().contains(searchText.toLowerCase()))
                relevant_def.add(d);
        }

        recyclerViewAdapter.setVisibleDefinitions(relevant_def);
    }
}