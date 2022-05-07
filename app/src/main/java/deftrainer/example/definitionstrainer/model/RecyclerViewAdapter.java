package deftrainer.example.definitionstrainer.model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import deftrainer.example.definitionstrainer.Activities.DefinitionHinzufuegenActivity;
import deftrainer.example.definitionstrainer.Activities.RecyclerViewHolder;
import deftrainer.example.definitionstrainer.R;

import java.util.List;

public class RecyclerViewAdapter  extends RecyclerView.Adapter<RecyclerViewHolder> {

    private List<Definition> visibleDefinitions;
    private Context context;

    public RecyclerViewAdapter(List<Definition> visibleDefinitions, Context context) {
        this.visibleDefinitions = visibleDefinitions;
        this.context = context;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.activity_suchen;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frame_layout, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        final Definition def_to_bind = visibleDefinitions.get(position);

        holder.getTitleView().setText(def_to_bind.getName());
        holder.getDefinitionView().setText(def_to_bind.getDefinition());
        holder.getFachView().setText(def_to_bind.getFeacherString());
        holder.getJahrgangView().setText(def_to_bind.getJahrgaengeString());
        holder.getFavoritCheckBox().setChecked(def_to_bind.getFavorit());
        holder.getFavoritCheckBox().setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               boolean isChecked = holder.getFavoritCheckBox().isChecked();
               if (def_to_bind.getFavorit() != isChecked) {
                   def_to_bind.setFavorit(holder.getFavoritCheckBox().isChecked());
                   DefinitionsManager.getDefinitionsManager().writeDefinitionsToMemory(context);
               }
           }
        });
        /*
        holder.getFavoritCheckBox().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            }
        });
        */

        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if the user clicks on a Definition, ask him whether he wants to delete it.
                // askToDeleteDefinition(def_to_bind);

                // start the DefinitionHinzufügenActivity and pass the matching ID of the Definition
                Intent intent = new Intent(context, DefinitionHinzufuegenActivity.class);
                intent.putExtra(DefinitionHinzufuegenActivity.EXTRA_EDIT_DEFINITION, "1");
                intent.putExtra(DefinitionHinzufuegenActivity.EXTRA_DEFINITION_ID, ""+def_to_bind.getID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return visibleDefinitions.size();
    }

    public void setVisibleDefinitions(List<Definition> visibleDefinitions) {
        this.visibleDefinitions = visibleDefinitions;
        notifyDataSetChanged();
    }

}