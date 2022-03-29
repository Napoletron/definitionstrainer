package deftrainer.example.definitionstrainer.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import deftrainer.example.definitionstrainer.R;

public class AboutActivity extends AppCompatActivity {

    private static String text = "Diese Anwendung soll dem Benutzer helfen, die Definitionen zu lernen," +
            "welche Ihm in der Grundausbildung als Polizeikommissaranwärter in Baden-Württemberg begegnen.\n" +
            "Alle Definitionen sind nach Fachbereich und Fach sortiert wie sie zum Stand " +
            "2022 existieren. Weiterhin beitzt jede Definition ein ‘Skill-Level’ um " +
            "gezielter Definitionen abzufragen, welche der Benutzer weniger gut beherrscht.\n" +
            "Diese Anwendung hebt keinen Anspruch auf Vollstandigkeit. Deswegen ist es " +
            "möglich, eigene Definitionen der persönlichen Sammlung hinzuzufügen oder ggf. wieder zu löschen.\n" +
            "Desweiteren erhebt die Anwendung keinen Anspruch auf Korrektheit.\n" +
            "\n" +
            "Falls grobe Fehler, Bugs oder Fragen auftreten benutzen Sie folgende Mail-Adresse:\n" +
            "davidlangsam512@gmail.com\n" +
            "\n" +
            "Entwickler:\n" +
            "Paul David Langsam\n" +
            "\n" +
            "\n" +
            "== Datenschutzerklärung: ==\n" +
            "- Verantwortlicher -\n" +
            "Paul David Langsam\n" +
            "4421 K-IT\n" +
            "Sturmbühlstraße 250\n" +
            "78054 Villingen-Schwenningen\n" +
            "\n" +
            "- Erhobene Daten -\n" +
            "Speicherzugriff: Die Anwendung greift auf den Speicher des Gerätes zu. Dies dient dem Abspeichern und Aufrufen von Definitionen und Einstellungen. Dies Speicherung besteht dauerhaft bis zur Deinstallation der Anwendung.\n" +
            "Weiteres: Es werden keine weiteren persönlichen Daten erhoben. Eine Weitergabe von Daten an Dritte findet nicht statt.\n" +
            "Siehe auch: https://datenschutzerklarung-definitionstrainer.webflow.io/" +
            "\n" +
            "\n" +
            "Anmerkungen zum Zufallsalgorithmus:" +
            "Es wird unterschieden zwischen erlernten, zu erlernenden und unbekannten Definitionen. Bei jeder Abfrage nimmt der Algorithmus zwei erlernte (zufällig) und fünf zu erlernende Definitionen (fest). Hiervon nimmt er dann eine zufällig." +
            "Wird eine Definition beherrscht (Skill = 10). Dann wechselt sie von 'zu erlernen' zu 'erlernt' und die nächste unbekannte Definition rückt in den 'zu erlernenden'-Status nach." +
            "Der Nutzer wird also zeitgleich maximal mit 5 zu erlernenden Definitionen konfrontiert und 'gräbt' sich beim Üben durch den Stapel." +
            "Wenn am Ende alle Definitionen einen Skill von 10 haben, ist die Selektion echt zufällig (Wiederholungen sind möglich).";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView about_text = findViewById(R.id.tV_about);
        about_text.setText(text);
    }
}