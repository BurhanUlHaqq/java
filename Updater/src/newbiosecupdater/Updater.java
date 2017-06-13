package newbiosecupdater;

public class Updater {

    public static void main(String args[]) {
        if (!Functions.downloadUpdateInfoData()) {
            Functions.removeTemporaryFiles();
            Functions.createLogFile();
            return;
        }
        Config.setLOG("Download Info Successfull");
        if (!Functions.loadVersionsInfo()) {
            Functions.removeTemporaryFiles();
            Functions.createLogFile();
            return;
        }
        Config.setLOG("Available Version info loaded");
        if (!Functions.setSelectVersionURL()) {
            Functions.removeTemporaryFiles();
            Functions.createLogFile();
            return;
        }
        Config.setLOG("Latest Version Url Set");

        if (!Functions.downloadSelectedVersion()) {
            Functions.removeTemporaryFiles();
            Functions.createLogFile();
            return;
        }
        Config.setLOG("Download Data Successfull");
        if (!Functions.extractUpdates()) {
            Functions.removeTemporaryFiles();
            Functions.createLogFile();
            return;
        }
        if (!Functions.takeBackupOfCurrentVersion()) {            
            Config.setBACKUP(false);
            Functions.createLogFile();
           // return;
        }
        if (!Functions.installSelectedUpdates()) {
            if (!Functions.rollBackCurrentVersion()) {
                Config.setLOG("\nRollback also failed:");
                Config.setLOG("Note:\nNow try to mark attendance and check login on T24 using BioMetric attempt if it works fine then OK.\nOtherwise contact your RITC to resolve your problem by reinstalling BioSec on this computer.");
            }
            if (Config.isBACKUP()) {//it decide that backup is created or not
            } else {
                Functions.removeTemporaryFiles();
            }
            Config.setLOG("Installing Update Failed");
        }
        Functions.removeTemporaryFiles();
        Functions.createLogFile();
    }
}
