package deftrainer.example.definitionstrainer.model;

import android.content.Context;
import android.util.Log;

import java.io.Serializable;

public class Settings implements Serializable {

    static final long serialVersionUID = 31415962123123L;

    public static final int STANDARD_INCREASE = 2;
    public static final int STANDARD_DECREASE = 4;
    public static final int STANDARD_MAX_SKILL = 10;
    public static final int STANDARD_MIN_SKILL = 0;
    // Version number = XXYYZZ where
    // XX = Version
    // YY = Feature
    // ZZ = Bug-fix
    public static final int APP_VERSION = 11300;
    public static final boolean TESTING = true;

    // The value at which a definitions skill is increased or decreased if guessed correct or wrong
    private int INCREASE;
    private int DECREASE;
    // This boolean stores the information, if the user will be asked again to add the default json-definitions
    private boolean ASK_AGAIN = true;
    private boolean FIRST_INSTALL = true;
    // the current version number
    private int THIS_VERSION = 0;
    // a list of all classes
    private String klasse;


    private static Settings settings = new Settings(STANDARD_INCREASE, STANDARD_DECREASE, 0, "20S");

    private Settings(int increase, int decrease, int version, String klasse) {
        this.INCREASE = increase;
        this.DECREASE = decrease;
        this.THIS_VERSION = version;
        this.klasse = klasse;
    }

    public static Settings getSettings() {
        return settings;
    }

    public static void tryToSaveSettings(Context c) {
        StorageManager.storeInternalFile(StorageManager.PATH_TO_INTERNAL_SETTINGS, settings, c);
    }

    public static void tryToLoadSettings(Context c) {
        try {
            Object o = StorageManager.loadInternalFile(StorageManager.PATH_TO_INTERNAL_SETTINGS, c);
            if(o instanceof Settings) {
                settings = (Settings) o;
            } else {
                throw new ClassNotFoundException();
            }
        } catch (ClassNotFoundException cnfe) {
            Log.e("ClassNotFoundException", "loading settings failed");
        }
    }

    public static void reset(Context c) {
        settings.ASK_AGAIN = true;
        settings.INCREASE = STANDARD_INCREASE;
        settings.DECREASE = STANDARD_DECREASE;
        settings.THIS_VERSION = APP_VERSION;
        settings.FIRST_INSTALL = true;
        tryToSaveSettings(c);
    }

    /*
    Makes all necessary updates.
    FIRST_INSTALL will be false, after this method was called.
     */
    public static void makeUpdatesIfNecessary(Context c) {
        if (TESTING || Settings.getSettings().getVersion() < APP_VERSION && Settings.getSettings().askMeAgain()) {

            DefinitionsManager.getDefinitionsManager().ask_to_update_definitions(c);

            Settings.getSettings().updateVersionNumber(c);
        }

        if (Settings.getSettings().getFIRST_INSTALL()) {
            Settings.getSettings().app_installed(c);
        }
    }

    public void updateVersionNumber(Context c) {
        THIS_VERSION = APP_VERSION;
        tryToSaveSettings(c);
    }

    public int getVersion() {
        return THIS_VERSION;
    }

    public int getMaxSkill() {
        return STANDARD_MAX_SKILL;
    }

    public int getMinSkill() {
        return STANDARD_MIN_SKILL;
    }

    public int getIncrease() {
        return INCREASE;
    }

    public int getDecrease() {
        return DECREASE;
    }

    /**
     * @return returns true, if its the first time this app runs on the device
     */
    public boolean getFIRST_INSTALL() {
        return FIRST_INSTALL;
    }

    public String getKlasse() {
        return klasse;
    }

    public void setIncrease(int increase, Context c) {
        INCREASE = increase;
        tryToSaveSettings(c);
    }

    public void setDecrease(int decrease, Context c) {
        DECREASE = decrease;
        tryToSaveSettings(c);
    }

    public void setKlasse(String klasse, Context c) {
        this.klasse = klasse;
        tryToSaveSettings(c);
    }

    public void app_installed(Context c) {
        FIRST_INSTALL = false;
        tryToSaveSettings(c);
    }

    public void dontAskMeAgainToAddDefaultDefinitions(Context c) {
        ASK_AGAIN = false;
        tryToSaveSettings(c);
    }

    public boolean askMeAgain() {
        return ASK_AGAIN;
    }
}
