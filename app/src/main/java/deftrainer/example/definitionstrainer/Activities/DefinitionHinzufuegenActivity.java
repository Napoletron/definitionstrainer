package deftrainer.example.definitionstrainer.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import deftrainer.example.definitionstrainer.R;
import deftrainer.example.definitionstrainer.model.Definition;
import deftrainer.example.definitionstrainer.model.DefinitionsManager;
import deftrainer.example.definitionstrainer.model.Fachbereich;
import deftrainer.example.definitionstrainer.model.RecyclerViewAdapterClasses;
import deftrainer.example.definitionstrainer.model.RecyclerViewAdapterSuchen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DefinitionHinzufuegenActivity extends AppCompatActivity{

    public static String EXTRA_EDIT_DEFINITION = "EXTRA_EDIT_DEFINITION";
    public static String EXTRA_DEFINITION_ID = "EXTRA_DEFINITION_ID";

    private EditText editText_title;
    private EditText editText_definition;
    private RecyclerView recycle_view_class;
    private RecyclerViewAdapterClasses recyclerViewAdapterClasses;
    private Spinner spinner_fach;
    private Button button_fertig;
    private Button button_delete;
    private Button button_add_class;

    private String title;
    private String definition;
    private String fach;

    // It is possible to acces the activity in two ways:
    // 1) via the main page to add a new Definition
    // 2) via the search page to edit a new Definition
    // if the activity is initiated by the 2. option => edit_definition will be 1, otherwise 0
    // if edit_definition equals to 1 => definition_id will be the id of the Definition to be edited
    private int edit_definition;
    private int definition_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definitionhinzufuegen);
        setTitle(R.string.add);

        try {
            String s1 = getIntent().getStringExtra(EXTRA_EDIT_DEFINITION);
            String s2 = getIntent().getStringExtra(EXTRA_DEFINITION_ID);
            edit_definition = Integer.parseInt(s1);
            definition_id = Integer.parseInt(s2);
        } catch (NullPointerException npe) {
            Log.e("ERROR", "DefinitionHinzufuegenActivity - onCreate - NullPointerException", npe);
        } catch ( NumberFormatException npe) {
            Log.e("ERROR", "DefinitionHinzufuegenActivity - onCreate - NumberFormatException", npe);
        }

        getAllViews();
        initSpinnerFach();
        initRecycleViewClasses();
        initFertigButton();
        initDeleteButton();
        initAddClassButton();

        insert_values_if_definition_is_edited();
    }

    private void getAllViews() {
        editText_title = findViewById(R.id.dhf_et_title);
        editText_definition = findViewById(R.id.dhf_met_definition);
        spinner_fach = findViewById(R.id.dhf_spin_fach);
        recycle_view_class = findViewById(R.id.dhf_rv_classes);
        button_fertig = findViewById(R.id.dhf_b_fertig);
        button_delete = findViewById(R.id.dhf_b_delete);
        button_add_class = findViewById(R.id.dhf_b_addClass);
    }

    private void initAddClassButton() {
        button_add_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addClassField(DefinitionsManager.getDefinitionsManager().getAllClasses().get(0));
            }
        });
    }

    private void initSpinnerFach() {

        List<String> faecher = Fachbereich.getAllFaecherOfAllFields();
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, faecher);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_fach.setAdapter(aa);

        spinner_fach.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fach = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // stub
            }
        });
    }

    private void initRecycleViewClasses() {
        recyclerViewAdapterClasses = new RecyclerViewAdapterClasses(this);
        // Add the following lines to create RecyclerView
        recycle_view_class.setAdapter(recyclerViewAdapterClasses);
        recycle_view_class.setLayoutManager(new LinearLayoutManager(this));

        if (edit_definition != 1 ) {
            addClassField(DefinitionsManager.getDefinitionsManager().getAllClasses().get(0));
        }
    }

    private void initFertigButton() {
        final Context context = this;
        button_fertig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = editText_title.getText().toString();
                definition = editText_definition.getText().toString();
                createDefinition();
                onBackPressed();
                DefinitionsManager.getDefinitionsManager().writeDefinitionsToMemory(context);
            }
        });
    }

    private void initDeleteButton() {
        if ( edit_definition == 1 ) {
            // If the 'Delete'-Button shall be active:
            final Context context = this;
            final Definition definitionToDelete = DefinitionsManager.getDefinitionsManager().getDefinitionByID(definition_id);

            button_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(context);
                    adb.setTitle(R.string.delete);
                    adb.setMessage(String.format("%s '%s' ?", context.getString(R.string.do_you_want_to_delete), definitionToDelete.getName()));
                    adb.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            removeDefinition(definitionToDelete);
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
        } else {
            button_delete.setVisibility(View.INVISIBLE);
            button_delete.setActivated(false);
        }
    }


    /**
     * This method removes a definition from the collection
     */
    private void removeDefinition(Definition definitionToDelete) {
        DefinitionsManager.getDefinitionsManager().removeDefinition(this, definitionToDelete);
        Toast.makeText(this, R.string.deleting_successful, Toast.LENGTH_SHORT).show();
        onBackPressed();
        DefinitionsManager.getDefinitionsManager().writeDefinitionsToMemory(this);
    }

    private void createDefinition() {
        //TODO: Spinner anpassen
        if (edit_definition == 0){
            int id = DefinitionsManager.getDefinitionsManager().getNextFreeDefinitionID();
            Definition d = new Definition(id, title, definition, 0, Arrays.asList(fach), getSelectedClasses(), true, false);
            DefinitionsManager.getDefinitionsManager().addDefinition(d, this);
        } else {
            Definition definition_to_update =  DefinitionsManager.getDefinitionsManager().getDefinitionByID(definition_id);
            Definition tmp = new Definition(new Random().nextInt(), title, definition, 0, Arrays.asList(fach), getSelectedClasses(), true, definition_to_update.getFavorit());
            definition_to_update.update_from(tmp);
        }
        Toast.makeText(this, title + " " +getResources().getString(R.string.has_been_added), Toast.LENGTH_SHORT).show();
        DefinitionsManager.getDefinitionsManager().writeDefinitionsToMemory(this);
    }

    private void insert_values_if_definition_is_edited() {
        if ( edit_definition == 1 ){
            Definition definition = DefinitionsManager.getDefinitionsManager().getDefinitionByID(definition_id);
            editText_title.setText(definition.getName());
            editText_definition.setText(definition.getDefinition());

            // init classes spinners
            for (String klasse : definition.getJahrgaenge()) {
                addClassField(klasse);
            }

            // init fach spinner
            int pos_fach = Fachbereich.getAllFaecherOfAllFields().indexOf(definition.getFaecher().get(0));
            spinner_fach.setSelection(pos_fach);

            button_fertig.setText(getResources().getString(R.string.update));
        }
    }

    public List<String> getSelectedClasses() {
        return recyclerViewAdapterClasses.getClasses();
    }

    public void addClassField(String className) {
        recyclerViewAdapterClasses.addClass(className);
    }

    public void removeClassField() {

    }
}
