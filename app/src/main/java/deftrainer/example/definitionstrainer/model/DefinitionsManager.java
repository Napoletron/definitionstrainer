package deftrainer.example.definitionstrainer.model;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import deftrainer.example.definitionstrainer.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class DefinitionsManager {

    private static DefinitionsManager DEFINITIONSMANAGER = null;
    private List<Definition> all_definitions = null;
    private List<Definition> important_definitions = null;
    private String filter_string = "<empty>";
    private Random RANDOM = new Random();
    private int lastDefinitionId;

    /**
     * Diese Methode gibt den Definitionsmanager zurück.
     * Zu jedem Zeitpunkt kann es maximal einen Manager geben.
     * @return Der einzig wahre Definitionsmanager
     */
    public static DefinitionsManager getDefinitionsManager() {
        if (DEFINITIONSMANAGER == null) {
            DEFINITIONSMANAGER = new DefinitionsManager();
        }
        return DEFINITIONSMANAGER;
    }

    /**
     * create a list of canidates that might be added to the list
     * this speeds up the adding process
     */
    public List<Definition> getCandidatesToAdd(List<Definition> newDefinitions) {

        List<Definition> candidates = new ArrayList<>();
        for (Definition candidate_def : newDefinitions) {
            boolean exists = false;
            for (Definition old_def : all_definitions) {
                //if (d2.getName().equals(d.getName())) {
                if(candidate_def.getID() == old_def.getID()) {
                    exists = true;
                    break;
                }
            }
            if ( !exists ) {
                candidates.add(candidate_def);
            }
        }
        return candidates;
    }

    /**
     * Diese Methode fügt der Definitionssammlung nicht enthaltene Definitionen hinzu
     */
    public int addDefinitions(List<Definition> newDefinitions) {

        // Search for the Definitions to add (Definitions to add are those, who are not yet contained in the users list).
        // And add them to the users list.
        newDefinitions = getCandidatesToAdd(newDefinitions);
        int definitionsAdded = newDefinitions.size();
        all_definitions.addAll(newDefinitions);
        return definitionsAdded;

    }

    public void removeDefinition(Context c, Definition definition) {
        all_definitions.remove(definition);
        writeDefinitionsToMemory(c);
    }

    /**
     * This method updates 'important_definitions'
     * A Definition is considered important, iff its class (eg "Grundausbilgung") matches
     * the selected class in the settings.
     */
    public void update_important_definitions() {
        if (all_definitions == null) {
            Log.e("ERROR", "all_definitions was null!");
            return;
        }
        important_definitions = new ArrayList<>();
        String current_class = Settings.getSettings().getKlasse();
        for (Definition def : all_definitions) {
            if (def.getJahrgaenge().contains(current_class)) {
                important_definitions.add(def);
            }
        }
    }

    /**
     * getter für die Definitionenliste
     * @return Die Definitionenliste
     */
    public List<Definition> getImportantDefinitions() {
        return important_definitions;
    }

    public List<Definition> getAllDefinitions() {
        return all_definitions;
    }

    /**
     * Diese Methode setzt den Filter, z.B. "Verkehrsrecht" oder "Strafprozessrecht", ...
     * @param filter_string Ein Filter für die Fächerauswahl
     */
    public void setFilter_string(String filter_string) {
        this.filter_string = filter_string;
    }

    /**
     * Diese Funktion filtert zuerst alle Definitionen heraus die gesucht sind. Z.B.
     * Verkehrsrecht wenn filter="Verkehrsrecht". Danach gibt es eine Definition zurück,
     * welche der Benutzer noch nicht so gut auswendig kann. Dies wird mithilfe des skills
     * festgestellt.
     * @return Defintion oder null, falls die Definitionen noch nicht geladen wurden.
     */
    public Definition getNextDefinition() {
        List<Definition> relevant_definitions = filterDefinitions();

        Definition candidateDefinition = getDefinitionToLearn(relevant_definitions);

        // make sure, there are not direct repetitions
        if (relevant_definitions.size() > 1) {
            int candidateId = candidateDefinition.getID();
            while (candidateId == lastDefinitionId) {
                candidateDefinition = getDefinitionToLearn(relevant_definitions);
                candidateId = candidateDefinition.getID();
            }
        }

        lastDefinitionId = candidateDefinition.getID();

        return candidateDefinition;
    }

    /**
     * Setter für die Definitionenliste
     * @param definitions Die neue Definitionenliste
     */
    private void setAllDefinitions(List<Definition> definitions) {
        this.all_definitions = definitions;
        update_important_definitions();
    }

    /**
     * Filtert die Definitionsliste mithilfe der Methode Definition.istWichtigFuer(...)
     * @return Die gefilterte Definitionsliste
     */
    private List<Definition> filterDefinitions() {
        List<Definition> relevant_definitions = new ArrayList<>();

        // das mit den stream sorgt für Ärger... deswegen der längere Weg
        for (Definition definition : important_definitions) {
            if (definition.istWichtigFuerFach(filter_string)) {
                relevant_definitions.add(definition);
            }
        }
        return relevant_definitions;
    }

    /**
     * @return returns all Definitions that have a skill S with min_skill <= S <= max_skill
     */
    private List<Definition> filterDefinitionsWithSkillLevel(List<Definition> definitions_to_filter, int min_skill, int max_skill) {

        List<Definition> filteredDefinitions = new ArrayList<>();
        for (Definition d : definitions_to_filter) {
            if (d.getSkill() <= max_skill && min_skill <= d.getSkill()) {
                filteredDefinitions.add(d);
            }
        }

        return filteredDefinitions;
    }

    /**
     * @param defs The list to select from
     * @param N the number of Definitions to select
     * @return N Random elements of the list
     */
    private List<Definition> getNRandomDefs(List<Definition> defs, int N) {
        List<Definition> defs_to_learn = new ArrayList<>();

        if(defs.size() != 0) {

            for (int i = 0; i < N; i++) {
                int tmp = RANDOM.nextInt(defs.size());
                defs_to_learn.add(defs.get(tmp));
            }
        }

        return defs_to_learn;
    }

    /**
     * Returns the first N elements of the given List.
     * If the list size M is smaller then N -> return only the first M elements.
     */
    private List<Definition> getNFirstDefs(List<Definition> defs, int N) {
        List<Definition> defs_to_learn = new ArrayList<>();

        for (int i=0; i<N && defs_to_learn.size() < defs.size(); i++) {
            defs_to_learn.add(defs.get(i++));
        }

        return defs_to_learn;
    }

    /**
     *
     * @param def_list Die Liste, aus der eine Definition mit wenig skill gesucht werden soll.
     * @return Die Definition mit dem niedrigsten Definitionsskill
     */
    private Definition getDefinitionToLearn(List<Definition> def_list) {

        List<Definition> definitionsWithMaxSkill = filterDefinitionsWithSkillLevel(def_list, Settings.getSettings().getMaxSkill(), Settings.getSettings().getMaxSkill());
        List<Definition> definitionsWithNoSkill = filterDefinitionsWithSkillLevel(def_list, 0, 0);
        List<Definition> definitionsWithSomeSkill = filterDefinitionsWithSkillLevel(def_list, 1, Settings.getSettings().getMaxSkill()-1);

        int num_max_skill_defs = Math.min(2, definitionsWithMaxSkill.size()); // Try to take 2 defs with max skill
        int number_some_skill_defs = Math.min(definitionsWithSomeSkill.size(), 7-num_max_skill_defs); // try to take 5 defst with some skill
        int number_no_skill_defs = Math.min(definitionsWithNoSkill.size(), 7-num_max_skill_defs - number_some_skill_defs); // try to fill the rest of the 7 defs with no skill

        definitionsWithMaxSkill = getNRandomDefs(definitionsWithMaxSkill, num_max_skill_defs);
        definitionsWithNoSkill = getNFirstDefs(definitionsWithNoSkill, number_no_skill_defs);
        definitionsWithSomeSkill = getNRandomDefs(definitionsWithSomeSkill, number_some_skill_defs);

        List<Definition> all_defs = new ArrayList<>();
        all_defs.addAll(definitionsWithMaxSkill);
        all_defs.addAll(definitionsWithNoSkill);
        all_defs.addAll(definitionsWithSomeSkill);

        return getNRandomDefs(all_defs, 1).get(0);
    }

    /**
     * Diese Methode fügt der Sammlung eine Definition hinzu und speichert diese gleich auf dem Gerät sofern möglich.
     */
    public void addDefinition(Definition definition, Context c) {
        all_definitions.add(definition);
        writeDefinitionsToMemory(c);
    }

    public void writeDefinitionsToMemory(Context c) {
        StorageManager.storeInternalFile(StorageManager.PATHTODEFINITIONSOBJ, all_definitions, c);
    }

    public int getNumberOfDefinitionsOfThatSubject(final String str) {
        List<String> l = new ArrayList<>();
        l.add(str);
        return getNumberOfDefinitionsOfThatSubject(l);
    }

    public int getNumberOfDefinitionsOfThatSubject(List<String> subjects) {
        int n = 0;
        for (Definition d : important_definitions) {
            for (String subject: subjects) {
                if (d.istWichtigFuerFach(subject)) {
                    n++;
                    break;
                }
            }
        }
        return n;
    }

    public int getTotalNumberOfMasteredDefs() {
        int n = 0;
        for (Definition d : important_definitions) {
            if (d.getSkill() == Settings.getSettings().getMaxSkill()) {
                n++;
            }
        }
        return n;
    }

    public int getNumberOfMasteredDefinitionsOfThatSubject(final String str) {
        List<String> l = new ArrayList<>();
        l.add(str);
        return getNumberOfMasteredDefinitionsOfThatSubject(l);
    }

    public int getNumberOfMasteredDefinitionsOfThatSubject(List<String> subjects) {
        int n = 0;
        for (Definition d : important_definitions) {
            for (String subject : subjects) {
                if (d.istWichtigFuerFach(subject) && d.getSkill() == Settings.getSettings().getMaxSkill()) {
                    n++;
                }
            }
        }
        return n;
    }

    /**
     * This method searches in all Definitions for all classes and returns a distinct set
     */
    public List<String> getAllClasses() {
        List<String> classes = new ArrayList<>();

        //classes.add("GS 44"); "GS 44" --> "GS I K-IT", "GS I VOS", "GS II K-IT", "GS II VOS"
        //classes.add("HS 44"); "HS 44" --> "HS I K-IT", "HS I VOS", "HS II K-IT", "HS II VOS"
        //classes.add("20S"); "20 S" --> "Grundausbildung"

        classes.add("Grundausbildung");

        classes.add("GS I VOS");
        classes.add("GS II VOS");
        classes.add("HS I VOS");
        classes.add("HS II VOS");

        classes.add("GS I K-IT");
        classes.add("GS II K-IT");
        classes.add("HS I K-IT");
        classes.add("HS II K-IT");

        for (Definition d : all_definitions) {
            for (String s : d.getJahrgaenge()) {
                boolean is_contained = false;
                for (String c : classes) {
                    if (c.equals(s)) {
                        is_contained = true;
                        break;
                    }
                }
                if (!is_contained) {
                    classes.add(s);
                }
            }
        }
        return classes;
    }

    /**
     * Calculate the Definitions to update.
     * A Definitions needs to be updated if:
     * 1) Name is not equal anymore
     * 2) definition-text is not equal anymore
     * 3) The Faecher-List is not equal anymore
     * 4) The Jahrgaenge-List is not equal anymore
     * A definition is not to be updated, if the user once modified it.
     * @param new_defs The newest list of definitions
     * @param old_defs The old list of definitions, that needs to be updated
     * @return A list of Definitions (copied from "new_defs"), which needs to be updated
     */
    private static List<Definition> get_Defs_to_update(List<Definition> new_defs,List<Definition>  old_defs) {
        List<Definition> defs_to_update = new ArrayList<>();

        for(Definition new_def : new_defs) {
            for (Definition old_def : old_defs) {

                if (!old_def.isUserModified() && (new_def.getID() == old_def.getID())) {
                    boolean update_due_to_name = !old_def.getName().equals(new_def.getName());
                    boolean update_due_to_definitiontext = !old_def.getDefinition().equals(new_def.getDefinition());
                    // TODO: den Vergleich von den String-Listen richtig machen!
                    boolean update_due_to_faecher = !(old_def.getFaecher().size() == new_def.getFaecher().size());
                    boolean update_due_to_jahrgang = false;

                    if(old_def.getJahrgaenge() == null) {
                        update_due_to_jahrgang = true;
                    } else if ( new_def.getJahrgaenge() != null && (old_def.getJahrgaenge().size() != new_def.getJahrgaenge().size())) {
                        update_due_to_jahrgang = true;
                    }

                    if ( update_due_to_name || update_due_to_definitiontext || update_due_to_faecher || update_due_to_jahrgang) {
                        defs_to_update.add(new_def);
                    }
                    break;
                }

            }
        }

        return defs_to_update;
    }

    /**
     * This Method calculates the Definitions to add.
     * A Definition may be added if:
     *  - it is not contained in the old list
     * @param new_defs The newest list of definitions
     * @param old_defs The old list of definitions, that needs to be updated
     * @return A list of Definitions (copied from "new_defs"), which needs to be added
     */
    private static List<Definition> get_Defs_to_add(List<Definition> new_defs, List<Definition>  old_defs) {
        List<Definition> defs_to_add = new ArrayList<>();

        for(Definition new_def : new_defs) {
            boolean is_contained = false;
            for (Definition old_def : old_defs) {
                if (old_def.getID() == new_def.getID()) {
                    is_contained = true;
                    break;
                }
            }

            if ( !is_contained ) {
                defs_to_add.add(new_def);
            }
        }

        return  defs_to_add;
    }

    /**
     * This method updates the Definitions
     * @param new_defs The Definitions which need to be updated.
     */
    private void update_definitions(List<Definition> new_defs) {
        for(Definition new_def : new_defs) {
            for (Definition old_def : all_definitions) {
                if (new_def.getID() == old_def.getID()) {
                    old_def.update_from(new_def);
                }
            }
        }
    }

    /**
     * This method adds all given Definitions
     * @param new_defs The Definitions to add
     */
    private void add_definitions(List<Definition> new_defs) {
        if (all_definitions == null) {
            Log.e("Error", "all_defintions was null!");
        } else {
            all_definitions.addAll(new_defs);
        }
        update_important_definitions();
    }

    /**
     * Diese Methode versucht die Definitionssammlung zu laden.
     * Dies geschieht in folgender Priorität:
     * 1) die Definitionssammlung (OBJ) im privaten Speicher der eigenen App
     * 2) (deaktiviert) die Definitionssammlung (OBJ) im assets Ordner
     * 3) die Definitionssammlung (JSON) im assets Ordner
     */
    public void tryToLoadDefinitions(Activity c) {
        List<Definition> definitions_internal = new ArrayList<>();

        // first, try to load the .obj file
        Object o = StorageManager.loadInternalFile(StorageManager.PATHTODEFINITIONSOBJ, c);
        if (o instanceof List) {
            definitions_internal = (List<Definition>) o;
        } else {
            Log.e("ERROR", "Unable to load Definitions!");
        }
        DefinitionsManager.getDefinitionsManager().setAllDefinitions(definitions_internal);
    }

    public void reset(Context c) {
        setAllDefinitions(new ArrayList<Definition>());
        writeDefinitionsToMemory(c);
    }

    private Set<Integer> getAllDefinitionIDs() {
        Set<Integer> ids = new HashSet<>();
        for (Definition d : all_definitions) {
            ids.add(d.getID());
        }
        return ids;
    }

    private int getMaxDefinitionID() {
        int max = 0;
        for (Definition d : all_definitions) {
            max = Math.max(max, d.getID());
        }
        return max;
    }

    public int getNextFreeDefinitionID() {
        int min_id = 0;
        Set<Integer> ids = getAllDefinitionIDs();
        for (int i=0; i<=getMaxDefinitionID()+1; i++) {
            min_id = i;
            if (! ids.contains(i)) {
                break;
            }
        }
        return min_id;
    }

    /**
     * This is a simple Callable-class to outsource some code of the dialog.
     */
    class Callable {
        private List<Definition> defs_to_add;
        private List<Definition> defs_to_update;
        private Context c;

        public Callable (List<Definition> defs_to_update, List<Definition> defs_to_add, Context c) {
            this.defs_to_add = defs_to_add;
            this.defs_to_update = defs_to_update;
            this.c = c;
        }
        public void call(){
            // add definitions
            DefinitionsManager.getDefinitionsManager().update_definitions(defs_to_update);
            DefinitionsManager.getDefinitionsManager().add_definitions(defs_to_add);
            // write them also to the memory
            DefinitionsManager.getDefinitionsManager().writeDefinitionsToMemory(c);
            Toast.makeText(c, R.string.import_was_successful, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method calculates a list of Definitions to add and to update.
     * If the users wants, he/she can then update the list.
     * @param c
     */
    public void ask_to_update_definitions(final Context c) {
        List<Definition> definitions_json = new ArrayList<>();

        try {
            definitions_json = JSONReader.readJsonStream(c.getAssets().open(StorageManager.PATHTODEFINITIONSJSON));
        } catch (IOException ioe2) {
            Log.e("ERROR", "Could not load Definitions from JSON!", ioe2);
        }

        final List<Definition> defs_to_update = DefinitionsManager.get_Defs_to_update(definitions_json, all_definitions);
        final List<Definition> defs_to_add = DefinitionsManager.get_Defs_to_add(definitions_json, all_definitions);

        int number_to_updates = defs_to_update.size();
        int number_to_add = defs_to_add.size();

        if (number_to_updates == 0 && number_to_add == 0) {
            return;
        }

        Callable callable = new Callable(defs_to_update, defs_to_add, c);
        if ( !Settings.getSettings().getFIRST_INSTALL() ) {
            show_asking_dialog_to_update_defs(callable, number_to_updates, number_to_add, c);
        } else {
            callable.call();
        }

    }

    /**
     * Diese Methode wird einen Dialog anzeigen und den benutzer fragen, ob er die internen JSON-Definitionen
     * zu der Sammlung hinzufügen möchte.
     * Außerdem hat er die möglichkeit "Nicht mehr nachfragen" zu aktivieren.
     * Dies wird in den Settings gespeichert.
     * @param c The context (current activity)
     */
    private void show_asking_dialog_to_update_defs(final Callable callable, int num_of_defs_to_update, int num_of_defs_to_add, final Context c) {
        AlertDialog.Builder adb = new AlertDialog.Builder(c);

        String question_string = String.format(Locale.getDefault(), "%d Definitions to update and %d Definitions to add were found. Do you want to add and update them?", num_of_defs_to_update, num_of_defs_to_add);
        adb.setMessage(question_string);
        adb.setTitle(R.string.add);

        adb.setCancelable(false);
        adb.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // add definitions
                callable.call();
            }
        });
        adb.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                show_dont_ask_me_again_dialog(c);
            }
        });

        adb.create().show();
    }

    private void show_dont_ask_me_again_dialog(final Context c) {
        AlertDialog.Builder adb = new AlertDialog.Builder(c);

        adb.setTitle(R.string.add);
        adb.setMessage(R.string.dont_ask_me_again);

        adb.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Settings.getSettings().dontAskMeAgainToAddDefaultDefinitions(c);
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

    public Definition getDefinitionByID(int id) {
        for (Definition d : all_definitions) {
            if (d.getID() == id) {
                return d;
            }
        }
        return null;
    }
}
