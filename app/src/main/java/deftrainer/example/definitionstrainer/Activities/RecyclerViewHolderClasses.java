package deftrainer.example.definitionstrainer.Activities;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import deftrainer.example.definitionstrainer.R;
import deftrainer.example.definitionstrainer.model.DefinitionsManager;
import deftrainer.example.definitionstrainer.model.RecyclerViewAdapterClasses;

public class RecyclerViewHolderClasses extends RecyclerView.ViewHolder{

    private Button butt;
    private Spinner spinn;
    private int spinnerPosition;
    private Context context;
    private List<String> allClasses;

    public RecyclerViewHolderClasses(@NonNull View itemView) {
        super(itemView);
        this.butt = itemView.findViewById(R.id.fl_butt);
        this.spinn = itemView.findViewById(R.id.fl_spinn);
    }

    public Button getButt () {
        return butt;
    }

    public Spinner getSpinn() {
        return spinn;
    }

    public void initHolder(Context context, List<String> allClasses, int spinnerPosition) {
        this.context = context;
        this.allClasses = allClasses;
        this.spinnerPosition = spinnerPosition;
    }

    public void initSpinner(String classToSelect) {
        List<String> classes = DefinitionsManager.getDefinitionsManager().getAllClasses();
        ArrayAdapter<String> aa = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, classes);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinn.setAdapter(aa);
        spinn.setSelection(allClasses.indexOf(classToSelect));

        spinn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RecyclerViewAdapterClasses rvac = (RecyclerViewAdapterClasses)getBindingAdapter();
                String cl = parent.getItemAtPosition(position).toString();
                rvac.updateClassAtPos(cl, spinnerPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //nothing
            }
        });

    }

    public void initButton() {
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerViewAdapterClasses rvac = (RecyclerViewAdapterClasses)getBindingAdapter();
                rvac.deleteClassAtPos(spinnerPosition);
            }
        });
    }
}
