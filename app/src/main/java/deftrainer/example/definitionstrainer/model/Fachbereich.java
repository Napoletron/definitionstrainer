package deftrainer.example.definitionstrainer.model;

import android.os.Build;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Fachbereich implements Serializable {

    public String fachbereichname;
    public List<Fach> fachliste;

    // Standardfächer
    public static final Fachbereich EINSATZUNDFUEHRUNGSWISS = new Fachbereich("Einsatz- und Führungswissenschaften",
            "Einsatztraining",
            "Einsatzwissenschaften",
            "Führungswissenschaften",
            "Sport",
            "Verkehrswissenschaften");

    public static final Fachbereich KRIMINALITAET =  new Fachbereich("Kriminalitätswissenschaften",
            "Kriminaltaktik",
            "Kriminaltechnik",
            "Kriminologie und Soziologie",
            "Kriminalistik");

    public static final Fachbereich RECHT = new Fachbereich("Rechtswissenschaften",
            "Öffentliches Dienstrecht",
            "Europarecht",
            "Polizei- und Verwaltungsrecht",
            "Staats- und Verfassungsrecht",
            "Strafrecht",
            "Strafprozessrecht",
            "Versammlungsrecht",
            "PolR - BtmG",
            "Europarecht");

    public static final Fachbereich SOZIALWISS = new Fachbereich("Sozialwissenschaften",
            "Informatik",
            "Berufsethik",
            "BWL",
            "Politikwissenschaft",
            "Psychologie",
            "Englisch",
            "Französisch",
            "Methodenlehre");

    public Fachbereich(String fachbereichname, String ... faecher){
        this.fachbereichname = fachbereichname;
        this.fachliste = new ArrayList<>();

        for(String fachname : faecher) {
            fachliste.add(new Fach(fachname));
        }
    }

    /**
     * Diese Methode gibt alle Fächer von allen Fachbereichen zurück.
     */
    public static List<String> getAllFaecherOfAllFields() {
        List<String> faecher = new ArrayList<>();
        faecher.addAll(EINSATZUNDFUEHRUNGSWISS.getAllFaecher());
        faecher.addAll(KRIMINALITAET.getAllFaecher());
        faecher.addAll(RECHT.getAllFaecher());
        faecher.addAll(SOZIALWISS.getAllFaecher());

        // remove duplicates
        List<String> distinct = new ArrayList<>();
        for (String fach : faecher) {
            boolean duplicate = false;
            for (String dist_fach : distinct) {
                duplicate = duplicate | fach.equals(dist_fach);
            }
            if (!duplicate) {
                distinct.add(fach);
            }
        }

        // sort by name
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            faecher = distinct.stream().sorted().collect(Collectors.<String>toList());
        } else {
            faecher = sortList(distinct, new Comparator<String>() {
                @Override
                public int compare(String s, String s2) {
                    return s.compareTo(s2);
                }
            });
        }

        return faecher;
    }

    /**
     *  Diese Methode gibt alle zugehörigen Fächer des Fachbereichs zurück
     */
    public List<String> getAllFaecher() {
        List<String> faecher = new ArrayList<>();
        for (Fach f : fachliste) {
            faecher.add(f.fachname);
        }
        return faecher;
    }

    public class Fach implements Serializable{
        public String fachname;

        public Fach(String fachname) {
            this.fachname = fachname;
        }
    }

    /**
     * Sort a list the old fashion style!
     */
    private static <T> List<T>  sortList(List<T> listToSort, Comparator<T> comp) {
        List<T> new_list = new ArrayList<>();
        for (T o : listToSort) {
            int insert_position = 0;
            for (T o2 : new_list) {
                if (comp.compare(o, o2) < 0) {
                    break;
                } else {
                    insert_position++;
                }
            }
            new_list.add(insert_position, o);
        }
        return new_list;
    }
}
