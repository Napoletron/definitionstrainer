package deftrainer.example.definitionstrainer.model;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class Definition implements Serializable, Comparable{

    static final long serialVersionUID =2169084276548728155L;
    private String name;
    private String definition;
    // skill ist eine Zahl von 0-10, wobei 0 bedeutet, dass der Nutzer die Definition noch nicht beherrscht
    private int skill;
    // ID ist eine Identifikations-numemr
    private int ID;
    // Faecher ist eine Liste von Fächern, die diese Definition benutzen. Z.B. "Strafprozessrecht"
    private List<String> faecher;
    // Jahrgang ist eine von Jahrgängen / Klassen für welche diese Definition relevant ist. Z.B. "20S", "19H" etc.
    private List<String> jahrgaenge;
    // gibt an, ob diese Definition vom Nutzer modifiziert wurde oder vom Nutzer erstellt wurde.
    // ist dies der Fall, soll die Definition beim Updaten nicht überschrieben werden.
    private boolean userModified = false;
    private boolean favorit = false;

    public Definition(int ID, String name, String definition, int skill, List<String> faecher, List<String> jahrgaenge, boolean userModified, boolean favorit) {
        this.ID = ID;
        this.name = name;
        this.definition = definition;
        this.skill = skill;
        this.faecher = faecher;
        this.jahrgaenge = jahrgaenge;
        this.userModified = userModified;
        this.favorit = favorit;
    }

    public boolean istWichtigFuerFach(String gesuchter_fachname) {
        for (String fach : faecher) {
            if (fach.toLowerCase().contains(gesuchter_fachname.toLowerCase()))
                return true;
        }
        return false;
    }

    public int getSkill() {
        return skill;
    }

    public int getID() {
        return ID;
    }

    public void resetSkill() {
        skill = Settings.getSettings().getMinSkill();
    }

    public void setMaxSkill() {
        skill = Settings.getSettings().getMaxSkill();
    }

    public void setSkill(int skill) {
        this.skill = skill;
    }

    public String getName() {
        return name;
    }

    public String getDefinition() {
        return definition;
    }

    public List<String> getFaecher() {
        return faecher;
    }

    public void setUserModified(boolean newUserModified) {
        this.userModified = newUserModified;
    }

    public boolean isUserModified() {
        return userModified;
    }

    public List<String> getJahrgaenge() {return jahrgaenge;}

    public String getFeacherString() {
        StringBuilder sb = new StringBuilder();
        if(faecher.size() > 0) {
            sb.append(faecher.get(0));
            for (int i=1; i<faecher.size(); i++) {
                sb.append(", "+faecher.get(i));
            }
        }
        return sb.toString();
    }

    public String getJahrgaengeString() {
        StringBuilder sb = new StringBuilder();
        if(jahrgaenge.size() > 0) {
            sb.append(jahrgaenge.get(0));
            for (int i=1; i<jahrgaenge.size(); i++) {
                sb.append(", "+jahrgaenge.get(i));
            }
        }
        return sb.toString();
    }

    public void dekrementSkillLevel() {
        skill -= Settings.getSettings().getDecrease();
        if (skill < Settings.STANDARD_MIN_SKILL) {
            skill = Settings.STANDARD_MIN_SKILL;
        }
    }

    public void incrementSkillLevel() {
        skill += Settings.getSettings().getIncrease();
        if (skill > Settings.STANDARD_MAX_SKILL) {
            skill = Settings.STANDARD_MAX_SKILL;
        }
    }

    public void update_from(Definition new_def) {
        this.name = new_def.getName();
        this.definition = new_def.getDefinition();
        this.jahrgaenge = new_def.getJahrgaenge();
        this.faecher = new_def.getFaecher();
        this.userModified = new_def.isUserModified();
    }

    public boolean getFavorit() {
        return favorit;
    }

    public void setFavorit(boolean newFavorit) {
        favorit = newFavorit;
    }

    @Override
    public int compareTo(Object o) {
        Definition d2 = (Definition)o;
        String name1 = this.getName().toLowerCase(Locale.ROOT);
        String name2 = d2.getName().toLowerCase(Locale.ROOT);
        return name1.compareTo(name2);
    }
}
