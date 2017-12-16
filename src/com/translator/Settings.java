package com.translator;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

class Settings {

    private boolean openOnStartup,
                    firstTime; //if this is the first time the user open the application
    private String src, to;
    private String shortcut;

    private static final String SETTINGS_FILE = "settings.t";
    private static final String LANGUAGES_FILE = "/langs.t";

    //name and path of the jar file
    private String programName;
    private String programPath;

    private Map<String, String> languages;

    Settings() {
        initProgramPathAndName();
        initLanguages();
        getSettings();
    }

    private void initProgramPathAndName() {

        //this line gets the path where this class was loaded
        //its the same thing as the path for the whole program
        programPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        programName = programPath.substring(programPath.lastIndexOf('/')+1); // it's like name.jar

        //this is for Windows looks like C://path/to/program/
        if (System.getProperty("os.name").startsWith("Windows")) {
            programPath = programPath.substring(1, programPath.lastIndexOf('/')+1);
            programPath = programPath.replaceAll("/", "\\\\");

            //this is for Linux looks like /path/to/program/
        } else if (System.getProperty("os.name").startsWith("Linux")) {
            programPath = programPath.substring(0, programPath.lastIndexOf('/')+1);
        }
    }

    //get the settings from the file
    private void getSettings() {

        try(BufferedReader br = new BufferedReader(new FileReader(programPath + SETTINGS_FILE))){

            br.readLine(); //get heading not needed
            StringTokenizer st = new StringTokenizer(br.readLine());

            openOnStartup = st.nextToken().equals("on");
            src = st.nextToken();
            to = st.nextToken();
            shortcut = st.nextToken();
            firstTime = false;

        } catch (FileNotFoundException e) {

            // if settings file not found
            // then get the default settings
            defaultSettings();

            // if the settings file not found
            // then this is the first time the user
            // open the program
            // this used to show the application
            // so that the user edit it
            firstTime = true;

            saveSettings();

        } catch (IOException e) {
            System.out.println("Error retrieving the settings");
            e.printStackTrace();
        }
    }

    private void defaultSettings() {

        //delete any registry value or startup file if exist from previous version
        if (System.getProperty("os.name").startsWith("Windows")) {
            //delete previous registry value if it exist
            deleteRegValue(Translator.APPLICATION_NAME);
        } else if (System.getProperty("os.name").startsWith("Linux")) {
            deleteStartupFile();
        }

        // default settings
        openOnStartup = false;
        src = "English";
        to = "Arabic";
        shortcut = "ctrl+T";
    }

    void saveSettings() {
        String heading = String.format("%-10s%-15s%-15s%s", "Startup", "src", "to", "shortcut");
        String options = String.format("%-10s%-15s%-15s%s", openOnStartup ? "on" : "off", src,  to, shortcut);

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(programPath + SETTINGS_FILE))){

            bw.write(heading);
            bw.newLine();
            bw.write(options);

        } catch (IOException e) {
            System.out.println("Error while saving the settings");
            e.printStackTrace();
        }
    }

    //build languages map from a file
    private void initLanguages() {

        languages = new LinkedHashMap<>();

        try(BufferedReader br = new BufferedReader(new InputStreamReader(
                Settings.class.getResourceAsStream(LANGUAGES_FILE)))
        ) {

            String line;
            StringTokenizer tk;

            //initializing languages map
            while ((line = br.readLine())!=null) {
                tk = new StringTokenizer(line, ",");
                languages.put(tk.nextToken(), tk.nextToken());
            }

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error while loading languages\n" +
                            "make sure that " + LANGUAGES_FILE + " file exist.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    //SETTERS ANS GETTERS

    void setOpenOnStartup(boolean openOnStartup) {


        if (System.getProperty("os.name").startsWith("Windows")) {

            //add a registry value to open the application on startup
            if (openOnStartup) {

                String val = "\"" + programPath + programName + "\"";
                setRegValue(Translator.APPLICATION_NAME, val);

            //delete the registry value so it doesn't open the application on start up
            } else {
                deleteRegValue(Translator.APPLICATION_NAME);
                System.out.println("registry value Deleted.");
            }

        } else if (System.getProperty("os.name").startsWith("Linux")) {

            if (openOnStartup) {
                addStartupFile();
            } else {
                deleteStartupFile();
            }
        }

        this.openOnStartup = openOnStartup;
    }

    //Linux related function
    private void deleteStartupFile() {

        String path = System.getProperty("user.home") + "/.config/autostart/" + Translator.APPLICATION_NAME + ".desktop";

        try {
            Files.deleteIfExists(Paths.get(path));
        } catch (IOException e) {
            System.out.println("Something went wrong when trying to delete startup file");
            e.printStackTrace();
        }
    }

    //Linux related function
    private void addStartupFile() {

        String path = System.getProperty("user.home") + "/.config/autostart/" + Translator.APPLICATION_NAME + ".desktop";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {

            bw.write("[Desktop Entry]");
            bw.newLine();
            bw.write("Type=Application");
            bw.newLine();
            bw.write("Exec=java -jar " + programPath + programName);
            bw.newLine();
            bw.write("Name=Translator");
            bw.newLine();
            bw.write("Comment=translator");
            bw.close();

        } catch (IOException e){
            System.out.println("Something went wrong while writing " + Translator.APPLICATION_NAME + ".desktop");
            e.printStackTrace();
        }
    }

    // Windows related function to write value to start the program on startup
    private void setRegValue(String name, String value) {

        //32bit machines don't have the key path in current user
        WinReg.HKEY root = System.getProperty("os.arch").contains("64") ? WinReg.HKEY_CURRENT_USER
                                                                        : WinReg.HKEY_LOCAL_MACHINE;

         Advapi32Util.registrySetStringValue(root,
                "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run",
                name,
                value
        );
    }

    // Windows related function to delete the value
    private void deleteRegValue(String name) {

        //32bit machines don't have the key path in current user
        WinReg.HKEY root = System.getProperty("os.arch").contains("64") ? WinReg.HKEY_CURRENT_USER
                                                                        : WinReg.HKEY_LOCAL_MACHINE;

        boolean isValueExist = Advapi32Util.registryValueExists(
                root,
                "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run",
                name);

        //there is nothing to delete
        if (!isValueExist) return;

        Advapi32Util.registryDeleteValue(
                root,
                "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run",
                name
        );
    }


    boolean isOpenOnStartup() {
        return openOnStartup;
    }

    boolean isFirstTime() {
        return firstTime;
    }


    String getShortcut() {
        return shortcut;
    }

    void setShortcut(String shortcut) {
        this.shortcut = shortcut;
    }


    void setSrc(String src) {
        this.src = src;
    }

    String getSrc() {
        return src;
    }

    String getEncodedSrc() {
        return languages.get(src);
    }


    void setTo(String to) {
        this.to = to;
    }

    String getTo() {
        return to;
    }

    String getEncodedTo() {
        return languages.get(to);
   }


    String[] getLanguages() {
        Object[] keys = languages.keySet().toArray();
        return Arrays.copyOf(keys, keys.length, String[].class);
    }
}
