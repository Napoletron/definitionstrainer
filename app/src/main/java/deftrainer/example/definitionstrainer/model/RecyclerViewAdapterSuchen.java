package deftrainer.example.definitionstrainer.model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import deftrainer.example.definitionstrainer.Activities.DefinitionHinzufuegenActivity;
import deftrainer.example.definitionstrainer.Activities.RecyclerViewHolderSuchen;
import deftrainer.example.definitionstrainer.R;

import java.util.List;

public class RecyclerViewAdapterSuchen extends RecyclerView.Adapter<RecyclerViewHolderSuchen> {

    private List<Definition> visibleDefinitions;
    private Context definitionHinzufuegenActivity;

    public RecyclerViewAdapterSuchen(List<Definition> visibleDefinitions, Context definitionHinzufuegenActivity) {
        this.visibleDefinitions = visibleDefinitions;
        this.definitionHinzufuegenActivity = definitionHinzufuegenActivity;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.activity_suchen;
    }

    @NonNull
    @Override
    public RecyclerViewHolderSuchen onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frame_layout, parent, false);
        return new RecyclerViewHolderSuchen(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolderSuchen holder, int position) {
        final Definition def_to_bind = visibleDefinitions.get(position);

        holder.getTitleView().setText(def_to_bind.getName());
        holder.getDefinitionView().setText(android.text.Html.fromHtml(def_to_bind.getDefinition().replace("li>", "lx>"), null, new MyTagHandler()));
        holder.getFachView().setText(def_to_bind.getFeacherString());
        holder.getJahrgangView().setText(def_to_bind.getJahrgaengeString());
        holder.getFavoritCheckBox().setChecked(def_to_bind.getFavorit());
        holder.getFavoritCheckBox().setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               boolean isChecked = holder.getFavoritCheckBox().isChecked();
               if (def_to_bind.getFavorit() != isChecked) {
                   def_to_bind.setFavorit(holder.getFavoritCheckBox().isChecked());
                   DefinitionsManager.getDefinitionsManager().writeDefinitionsToMemory(definitionHinzufuegenActivity);
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
                Intent intent = new Intent(definitionHinzufuegenActivity, DefinitionHinzufuegenActivity.class);
                intent.putExtra(DefinitionHinzufuegenActivity.EXTRA_EDIT_DEFINITION, "1");
                intent.putExtra(DefinitionHinzufuegenActivity.EXTRA_DEFINITION_ID, ""+def_to_bind.getID());
                definitionHinzufuegenActivity.startActivity(intent);
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