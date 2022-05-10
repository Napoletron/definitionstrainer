package deftrainer.example.definitionstrainer.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import deftrainer.example.definitionstrainer.Activities.RecyclerViewHolderClasses;
import deftrainer.example.definitionstrainer.R;

public class RecyclerViewAdapterClasses extends RecyclerView.Adapter<RecyclerViewHolderClasses>{

    private List<String> selectedClasses = new ArrayList<>();
    private Context context;

    public RecyclerViewAdapterClasses(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolderClasses onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.container_class, parent, false);
        return new RecyclerViewHolderClasses(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolderClasses holder, int position) {
        List<String> classes = DefinitionsManager.getDefinitionsManager().getAllClasses();
        holder.initHolder(context, classes, position);
        holder.initButton();
        String cl = selectedClasses.get(position);
        holder.initSpinner(cl);
    }

    @Override
    public int getItemCount() {
        return selectedClasses.size();
    }

    public List<String> getClasses() {
        return selectedClasses;
    }

    public void addClass(String className) {
        selectedClasses.add(className);
        notifyDataSetChanged();
    }

    public void updateClassAtPos(String cl, int position) {
        selectedClasses.remove(position);
        selectedClasses.add(position, cl);
        //notifyDataSetChanged();
    }

    public void deleteClassAtPos(int spinnerPosition) {
        selectedClasses.remove(spinnerPosition);
        notifyDataSetChanged();
    }
}
