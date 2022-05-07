package deftrainer.example.definitionstrainer.Activities;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import deftrainer.example.definitionstrainer.R;

public class RecyclerViewHolder  extends RecyclerView.ViewHolder {

    private TextView view_title;
    private TextView view_definition;
    private TextView view_fach;
    private TextView view_jahrgang;
    private View view;
    private CheckBox checkbox_favorit;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        view_title = itemView.findViewById(R.id.frameLayout_title);
        view_fach = itemView.findViewById(R.id.frameLayout_fach);
        view_jahrgang = itemView.findViewById(R.id.frameLayout_jahrgang);
        view_definition = itemView.findViewById(R.id.frameLayout_definition);
        checkbox_favorit = itemView.findViewById(R.id.frameLayout_favorit);
        view = itemView;
    }

    public TextView getTitleView(){
        return view_title;
    }

    public TextView getDefinitionView() {
        return view_definition;
    }

    public TextView getFachView(){
        return view_fach;
    }

    public TextView getJahrgangView(){
        return view_jahrgang;
    }

    public CheckBox getFavoritCheckBox() {
        return checkbox_favorit;
    }

    /**
     * Gebe den gesamten View zurück
     */
    public View getView() {
        return view;
    }
}