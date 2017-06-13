package newbiosecupdater;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

public class Functions {

  
    public static boolean downloadUpdateInfoData() {

        File f = new File(Config.INSTALLATION_PATH);
        if (!f.exists()) {
            Config.setLOG("BioSec not installed on this system.");
            Config.setLOG("Contact with RITC");
            return false;
        }
        if (!new File(Config.UPDATE_INFO_FILE_PATH).exists()) {
            Config.setLOG("Configration File Not found:");
            Config.setLOG("Contact with RITC to ask update link and go to \"" + Config.UPDATE_INFO_FILE_PATH + "\" to update link");

            return false;
        }

        if (!createDirectory(Config.UPDATE_INFO_DIR)) {
            Config.setLOG("Creating Directory Faild\"" + Config.UPDATE_INFO_DIR + "\"");
            return false;
        }

        File file = new File(Config.UPDATE_INFO_FILE_PATH);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException ex) {

            return false;
        }

        //Construct BufferedReader from InputStreamReader to read url form Config file
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line = null;
        try {
            if ((line = br.readLine()) != null) {
                Config.setUPDATE_INFO_URL(line);
            }
            br.close();
        } catch (IOException ex) {
            Config.setLOG("File read error\"" + Config.UPDATE_INFO_FILE_PATH + "\"");

            return false;
        }
//Construct BufferedReader from InputStreamReader to read url form Config file
        String url = Config.getUPDATE_INFO_URL();

        try {
            downloadUsingStream(url, Config.UPDATE_INFO_DIR + "UpdateInfo.xml");
        } catch (IOException e) {
            Config.setLOG("Downloading Info file Failed\"" + url + "\n" + e.getMessage());

            return false;
        }
        Config.setLOG("Info file found");
        return true;
    }

    static boolean downloadSelectedVersion() {

        if (!createDirectory(Config.UPDATE_DOWNLOAD_DIR)) {
            Config.setLOG("Creating Directory Faild\"" + Config.UPDATE_DOWNLOAD_DIR + "\"");

            return false;
        }

        String url = Config.getSELECTED_VERSION_URL();

        try {
            downloadUsingStream(url, Config.UPDATE_DOWNLOAD_DIR + "setup.cab");
        } catch (IOException e) {
            Config.setLOG(e.getMessage());

            return false;
        }
        Config.setLOG("Download completed!");

        return true;
    }

    public static boolean loadVersionsInfo() {
        ////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////Reading Current Version File/////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////
        File file = new File(Config.CURRENT_INFO_FILE_PATH);
        long CurrentID = 0;
        String CurrentVersion = "Unknown";
        Config.setCURENT_ID(CurrentID);
        Config.setCURRENT_VERSION(CurrentVersion);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Config.setLOG(ex.getMessage());
            return false;
        }
        
        org.w3c.dom.Document document = null;
        try {
            document = documentBuilder.parse(file);
        } catch (SAXException ex) {
            Config.setLOG(ex.getMessage());
            return false;
        } catch (IOException ex) {
            Config.setBACKUP(false);
            Config.setLOG(ex.getMessage());
            // return false;
        }

        if (document != null) {
            CurrentID = Long.parseLong(document.getElementsByTagName("CurrentVersion").item(0).getAttributes().getNamedItem("id").getTextContent());
            CurrentVersion = document.getElementsByTagName("CurrentVersion").item(0).getAttributes().getNamedItem("version").getTextContent();
        }
        Config.setCURENT_ID(CurrentID);
        Config.setCURRENT_VERSION(CurrentVersion);

        ////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////Reading Update Version File/////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////
        File fileUp = new File(Config.UPDATE_INFO_DIR + "UpdateInfo.xml");
        DocumentBuilderFactory documentBuilderFactoryfileUp = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder documentBuilderfileUp = null;
        try {
            documentBuilderfileUp = documentBuilderFactoryfileUp.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Config.setLOG(ex.getMessage());
            return false;
        }
        
        org.w3c.dom.Document documentfileUp = null;
        try {
            documentfileUp = documentBuilder.parse(fileUp);
        } catch (SAXException ex) {
            Config.setLOG(ex.getMessage());
            return false;
        } catch (IOException ex) {
            Config.setLOG(ex.getMessage());
            return false;
        }
        long UpdateId = 0;
        boolean check=false;
        String UpdateVersion = null;
        for (int i = 0; i < documentfileUp.getElementsByTagName("version").getLength(); i++) {
            UpdateId = Long.parseLong(documentfileUp.getElementsByTagName("version").item(i).getAttributes().getNamedItem("id").getTextContent());
            UpdateVersion = documentfileUp.getElementsByTagName("version").item(i).getAttributes().getNamedItem("version").getTextContent();
            if (CurrentID < UpdateId) {
                Config.setSELECTED_VERSION(UpdateVersion);
                check=true;
            }
        }
        System.out.println("Current ID: "+CurrentID+"Latest ID: "+ UpdateId);
        if (CurrentID < UpdateId) {
        } else if (CurrentID == UpdateId) {
            Config.setSAME_VERSION(true);
            Config.setLOG("Already Updated Version!");
            return false;
        }
        if (!check) {
            Config.setLOG("Version XML having issue!");
            return false;
        }
        
        return true;
    }

    private static void downloadUsingStream(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count = 0;
        while ((count = bis.read(buffer, 0, 1024)) != -1) {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }

    private static void downloadUsingNIO(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }

    private static boolean createDirectory(String file) {
        File theDir = new File(file);
        boolean result = true;
// if the directory does not exist, create it
        if (!theDir.exists()) {
            try {
                theDir.mkdirs();
                // theDir.getParentFile().mkdirs();
                result = true;
            } catch (SecurityException se) {
                Config.setLOG("While Creating Directories: \n\"" + se.getMessage());
                result = false;
            }
            if (result) {
            }
        }
        return result;
    }

    static boolean setSelectVersionURL() {
        ////////////////////////////////////////////////////////////////////////////////////
        /////////////////////Reading Update Version File For Update Path////////////////////
        ////////////////////////////////////////////////////////////////////////////////////
        File file = new File(Config.UPDATE_INFO_DIR + "UpdateInfo.xml");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Config.setLOG(ex.getMessage());
            return false;
        }
        org.w3c.dom.Document document = null;
        try {
            document = documentBuilder.parse(file);
        } catch (SAXException ex) {
            Config.setLOG(ex.getMessage());
            return false;
        } catch (IOException ex) {
            Config.setLOG(ex.getMessage());
            return false;
        }
        for (int i = 0; i < document.getElementsByTagName("version").getLength(); i++) {
            if (Config.getSELECTED_VERSION().equals(document.getElementsByTagName("version").item(i).getAttributes().getNamedItem("version").getTextContent().toString())) {
                if (Config.WINDOWS_OS) {
                    Config.setSELECTED_VERSION_URL(document.getElementsByTagName("Windows").item(i).getAttributes().getNamedItem("path").getTextContent());
                } else {
                    Config.setSELECTED_VERSION_URL(document.getElementsByTagName("Linux").item(i).getAttributes().getNamedItem("path").getTextContent());
                }
                break;
            }
        }
        Config.setLOG("Version URL is Set");
        return true;
    }

    static boolean extractUpdates() {
        String source = Config.UPDATE_DOWNLOAD_DIR + "setup.cab";
        String destination = Config.UPDATE_DOWNLOAD_DIR + "/setup/";
        String password = "password";

        if (!createDirectory(Config.UPDATE_DOWNLOAD_DIR + "/setup/")) {

            return false;
        }

        try {
            ZipFile zipFile = new ZipFile(source);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password);
            }
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            Config.setLOG(e.getMessage());
            e.printStackTrace();
            return false;
        }
        Config.setLOG("Extrecting Data done..");

        return true;
    }

    static boolean takeBackupOfCurrentVersion() {
        ////////////////////////////////////////////////////////////////////////////////////
        /////////////////////Reading Current Version File For Backup////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////

        if (!createDirectory(Config.CURRENT_VERSION_BACKUP_DIR)) {
            Config.setLOG("Creating Directory Faild\"" + Config.CURRENT_VERSION_BACKUP_DIR + "\"");

            return false;
        }
        if (!copyFile(Config.CURRENT_INFO_FILE_PATH, Config.CURRENT_VERSION_BACKUP_DIR + new File(Config.CURRENT_INFO_FILE_PATH).getName())) {
            Config.setLOG("Failed to copy main current info file: \"" + Config.CURRENT_INFO_FILE_PATH + "\"");

            return false;
        }

        File file = new File(Config.CURRENT_INFO_FILE_PATH);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Config.setLOG(ex.getMessage());
            return false;
        }
        org.w3c.dom.Document document = null;
        try {
            document = documentBuilder.parse(file);
        } catch (SAXException ex) {
            Config.setLOG(ex.getMessage());
            return false;
        } catch (IOException ex) {
            Config.setLOG(ex.getMessage());
            return false;
        }

        int inc = 80 % document.getElementsByTagName("file").getLength();
        for (int i = 0; i < document.getElementsByTagName("file").getLength(); i++) {
            String name = document.getElementsByTagName("file").item(i).getAttributes().getNamedItem("name").getTextContent();
            String source = document.getElementsByTagName("file").item(i).getAttributes().getNamedItem("source").getTextContent();
            String destination = document.getElementsByTagName("file").item(i).getAttributes().getNamedItem("destination").getTextContent();
            String past_file_name = document.getElementsByTagName("file").item(i).getAttributes().getNamedItem("past_file_name").getTextContent();
            String required = document.getElementsByTagName("file").item(i).getAttributes().getNamedItem("required").getTextContent();
            String action = document.getElementsByTagName("file").item(i).getAttributes().getNamedItem("action").getTextContent();

            if (!copyFile(destination + past_file_name, Config.CURRENT_VERSION_BACKUP_DIR + source + name)) {
                Config.setLOG("Backup File Miss: " + destination + past_file_name + " to " + Config.CURRENT_VERSION_BACKUP_DIR + source + name);
                int reply = JOptionPane.showConfirmDialog(null, "File missd backup while taking backup\n Do you want to update on risk?", "Backup Failed!", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    Config.setBACKUP(false);
                    return false;
                }
            }
        }
        Config.setLOG("Backup Successfull");
        Config.setBACKUP(true);
        return true;
    }

    private static boolean copyFile(String src, String dst) {
        File source = new File(src);
        File sr = new File(source.getAbsolutePath());
        File dest = new File(dst);
        File ds = new File(dest.getAbsolutePath());
        if (!createDirectory(ds.getPath())) {
            return false;
        }
        try {
            Files.copy(sr.toPath(), ds.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Config.setLOG(ex.getMessage());
            return false;
        }
        return true;
    }

    static boolean installSelectedUpdates() {

        if (!copyFile(Config.UPDATE_DOWNLOAD_DIR + "setup/CurrentVersionInfo.xml", Config.CURRENT_INFO_FILE_PATH)) {
            Config.setLOG("Current Version updated");

            return false;
        }
        ////////////////////////////////////////////////////////////////////////////////////
        /////////////////////Reading Current Version File For Setup////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////

        File file = new File(Config.CURRENT_INFO_FILE_PATH);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder documentBuilder = null;
        try {

            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Config.setLOG(ex.getMessage());
            return false;
        }
        org.w3c.dom.Document document = null;
        try {
            document = documentBuilder.parse(file);
        } catch (SAXException ex) {
            Config.setLOG(ex.getMessage());
            return false;
        } catch (IOException ex) {
            Config.setLOG(ex.getMessage());
            return false;
        }

        int inc = 65 % document.getElementsByTagName("file").getLength();
        for (int i = 0; i < document.getElementsByTagName("file").getLength(); i++) {
            String name = document.getElementsByTagName("file").item(i).getAttributes().getNamedItem("name").getTextContent();
            String source = document.getElementsByTagName("file").item(i).getAttributes().getNamedItem("source").getTextContent();
            String destination = document.getElementsByTagName("file").item(i).getAttributes().getNamedItem("destination").getTextContent();
            String past_file_name = document.getElementsByTagName("file").item(i).getAttributes().getNamedItem("past_file_name").getTextContent();
            String required = document.getElementsByTagName("file").item(i).getAttributes().getNamedItem("required").getTextContent();
            String action = document.getElementsByTagName("file").item(i).getAttributes().getNamedItem("action").getTextContent();

            switch (action) {
                case "COPY": {
                    if (!copyFile(Config.UPDATE_DOWNLOAD_DIR + "setup/" + source + name, destination + past_file_name)) {
                        Config.setLOG("Failed Copy: " + destination + past_file_name);
                        if (required.equalsIgnoreCase("true")) {
                            return false;
                        }
                    }
                    break;
                }
                case "RUN": {
                    if (!runFile(Config.UPDATE_DOWNLOAD_DIR + "setup/" + source + name)) {
                        if (required.equalsIgnoreCase("true")) {
                            return false;
                        }
                    }
                    break;
                }
                default: {
                    Config.setLOG("Undefined Action!");
                    break;
                }
            }
        }
        Config.setLOG("Installation Successfull!");
        return true;
    }

    static boolean removeTemporaryFiles() {
        boolean state = true;
        if (!deleteDir(Config.UPDATE_INFO_DIR)) {
            Config.setLOG("Failed Temporary Data: " + Config.UPDATE_INFO_DIR);
            state = false;
        }
        if (!deleteDir(Config.CURRENT_VERSION_BACKUP_DIR)) {
            Config.setLOG("Failed Temporary Data: " + Config.CURRENT_VERSION_BACKUP_DIR);
            state = false;
        }
        if (!deleteDir(Config.UPDATE_DOWNLOAD_DIR)) {
            Config.setLOG("Failed Temporary Data: " + Config.UPDATE_DOWNLOAD_DIR);
            state = false;
        }
        if (state) {
            Config.setLOG("Temporary Data Removed Successfully");
        }
        return state;
    }

    static boolean deleteDir(String dir) {
        try {
            FileUtils.deleteDirectory(new File(dir));
        } catch (IOException ex) {
            Config.setLOG(ex.getMessage());
            return false;
        }
        return true;
    }

    static boolean loadSelectedVersionInfo() {
        File file = new File(Config.UPDATE_DOWNLOAD_DIR + "setup/CurrentVersionInfo.xml");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Config.setLOG(ex.getMessage());
            return false;
        }
        org.w3c.dom.Document document = null;
        try {
            document = documentBuilder.parse(file);
        } catch (SAXException ex) {
            Config.setLOG(ex.getMessage());
            return false;
        } catch (IOException ex) {
            Config.setLOG(ex.getMessage());
            return false;
        }

//        for (int i = 0; i < document.getElementsByTagName("device").getLength(); i++) {
//            model.addElement(document.getElementsByTagName("device").item(i).getAttributes().getNamedItem("name").getTextContent());
//        }
        Config.setLOG("Selected Version: " + Config.getSELECTED_VERSION());

        return true;
    }

    public static void slp(int MILLISECONDS) {
        try {
            Thread.sleep(MILLISECONDS);
        } catch (InterruptedException ex) {
            Config.setLOG(ex.getMessage());
        }
    }

    static boolean rollBackCurrentVersion() {

        File file = new File(Config.CURRENT_VERSION_BACKUP_DIR + new File(Config.CURRENT_INFO_FILE_PATH).getName());
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Config.setLOG(ex.getMessage());
            return false;
        }
        org.w3c.dom.Document document = null;
        try {
            document = documentBuilder.parse(file);
        } catch (SAXException ex) {
            Config.setLOG(ex.getMessage());
            return false;
        } catch (IOException ex) {
            Config.setLOG(ex.getMessage());
            return false;
        }
        for (int i = 0; i < document.getElementsByTagName("file").getLength(); i++) {
            String name = document.getElementsByTagName("file").item(i).getAttributes().getNamedItem("name").getTextContent();
            String source = document.getElementsByTagName("file").item(i).getAttributes().getNamedItem("source").getTextContent();
            String destination = document.getElementsByTagName("file").item(i).getAttributes().getNamedItem("destination").getTextContent();
            String past_file_name = document.getElementsByTagName("file").item(i).getAttributes().getNamedItem("past_file_name").getTextContent();
            String required = document.getElementsByTagName("file").item(i).getAttributes().getNamedItem("required").getTextContent();
            String action = document.getElementsByTagName("file").item(i).getAttributes().getNamedItem("action").getTextContent();
            switch (action) {
                case "COPY": {
                    if (!copyFile(Config.UPDATE_DOWNLOAD_DIR + "setup/" + source + name, destination + past_file_name)) {
                        Config.setLOG("Failed Copy: " + destination + past_file_name);
                        if (required.equalsIgnoreCase("true")) {
                            return false;
                        }
                    }
                    break;
                }
                case "RUN": {
                    if (!runFile(Config.UPDATE_DOWNLOAD_DIR + "setup/" + source + name)) {
                        if (required.equalsIgnoreCase("true")) {
                            return false;
                        }
                    }
                    break;
                }
                default: {
                    Config.setLOG("Undefined Action!");
                    break;
                }
            }
        }
        Config.setLOG("Rollback Successfull:");
        Config.setLOG("Note:\nNow try to mark attendance and check login on T24 using BioMetric attempt if it works fine then OK. \nOtherwise contact your RITC to resolve your problem by reinstalling BioSec on this computer.");
        return false;
    }

    static boolean createLogFile() {
        try {
            FileUtils.writeStringToFile(new File(Config.INSTALLATION_PATH + "BioSecUpdateLog.txt"), Config.getLOG());
        } catch (IOException ex) {
            Config.setLOG(ex.getMessage());
            return false;
        }
        return true;
    }

    private static boolean runFile(String path) {
        if (Config.WINDOWS_OS) {
            try {
                File file = new File(path);
                String pathh = file.getParent();
                String f = file.getName();
                Process p = null;
                try {
                    p = Runtime.getRuntime().exec("cmd /c start /wait " + pathh + "\"\\" + f + "\"");

                } catch (IOException ex) {
                    Config.setLOG(ex.getMessage());
                    return false;
                }
                p.waitFor();
            } catch (InterruptedException ex) {
                Config.setLOG(ex.getMessage());
                return false;
            }
        } else {
            try {
                Runtime.getRuntime().exec("chmod +x " + path);
                Runtime.getRuntime().exec(path).waitFor();
//                String[] cmdArray = {"xterm", "-e", path + " ; BioSecUpdater Installer"};
//                r.exec(cmdArray).waitFor();
            } catch (InterruptedException ex) {
                Config.setLOG(ex.getMessage());
                return false;
            } catch (IOException ex) {
                Config.setLOG(ex.getMessage());
                return false;
            }
        }
        return true;
    }
}
