package newbiosecupdater;

public class Config {

////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////Windows/////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//    public static final boolean WINDOWS_OS = true;
//    public static final String INSTALLATION_PATH = "C:/BiometricPlugin/";
//    public static final String UPDATE_INFO_FILE_PATH = "C:/BiometricPlugin/Config.dat";
//    public static final String UPDATE_INFO_DIR = "C:/temp/abcd/";
//    public static final String CURRENT_INFO_FILE_PATH = "C:/BiometricPlugin/CurrentVersionInfo.xml";
//    public static final String UPDATE_DOWNLOAD_DIR = "C:/temp/BioSecUpdate/";
//    public static final String CURRENT_VERSION_BACKUP_DIR = "C:/temp/BioSecBackup/";
////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////Linux///////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static final boolean WINDOWS_OS = false;
    public static final String INSTALLATION_PATH = "/usr/local/etc/";
    public static final String UPDATE_INFO_FILE_PATH = "/usr/local/etc/Config.dat";
    public static final String UPDATE_INFO_DIR = "/usr/temp/info/";
    public static final String CURRENT_INFO_FILE_PATH = "/usr/local/etc/CurrentVersionInfo.xml";
    public static final String UPDATE_DOWNLOAD_DIR = "/usr/temp/BioSecUpdate/";
    public static final String CURRENT_VERSION_BACKUP_DIR = "/usr/temp/BioSecBackup/";
////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////Finals//////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static String SELECTED_VERSION_URL;
    private static String UPDATE_INFO_URL;
    private static String CURRENT_VERSION;
    private static long CURENT_ID;
    private static String LOG = "BioSecUtil Log File(Version 1.1.2)";
    private static String SELECTED_VERSION;
    private static boolean BACKUP;
    private static boolean SUCCESS = false;
    private static boolean SAME_VERSION = false;

    public static String getLOG() {
        return LOG;
    }

    public static void setLOG(String LOG) {
        Config.LOG = Config.LOG + "\n" + LOG;
    }

    public static boolean isSAME_VERSION() {
        return SAME_VERSION;
    }

    public static void setSAME_VERSION(boolean SAME_VERSION) {
        Config.SAME_VERSION = SAME_VERSION;
    }

    public static boolean isSUCCESS() {
        return SUCCESS;
    }

    public static void setSUCCESS(boolean SUCCESS) {
        Config.SUCCESS = SUCCESS;
    }

    public static boolean isBACKUP() {
        return BACKUP;
    }

    public static void setBACKUP(boolean BACKUP) {
        Config.BACKUP = BACKUP;
    }

    public static String getSELECTED_VERSION_URL() {
        return SELECTED_VERSION_URL;
    }

    public static void setSELECTED_VERSION_URL(String SELECTED_VERSION_URL) {
        Config.SELECTED_VERSION_URL = SELECTED_VERSION_URL;
    }

    public static String getCURRENT_VERSION() {
        return CURRENT_VERSION;
    }

    public static void setCURRENT_VERSION(String CURRENT_VERSION) {
        Config.CURRENT_VERSION = CURRENT_VERSION;
    }

    public static long getCURENT_ID() {
        return CURENT_ID;
    }

    public static void setCURENT_ID(long CURENT_ID) {
        Config.CURENT_ID = CURENT_ID;
    }

    public static String getSELECTED_VERSION() {
        return SELECTED_VERSION;
    }

    public static void setSELECTED_VERSION(String SELECTED_VERSION) {
        Config.SELECTED_VERSION = SELECTED_VERSION;
    }

    public static String getUPDATE_INFO_URL() {
        return UPDATE_INFO_URL;
    }

    public static void setUPDATE_INFO_URL(String UPDATE_INFO_URL) {
        Config.UPDATE_INFO_URL = UPDATE_INFO_URL;
    }
}
