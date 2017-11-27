package com.translator;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Settings {

    private boolean openOnStartup,
                    firstTime; //if this is the first time the user open the application
    private String src, to;
    private String shortcut;

    private static final String SETTINGS_FILE = "settings.t";
    private static final String LANGUAGES_FILE = "langs.t";

    //name and path of the jar file
    private String programName;
    private String programPath;

    private Map<String, String> languages;

    public Settings() {
        initProgramPathAndName();
        initLanguages();
        getSettings();
    }

    private void initProgramPathAndName() {

        //this line gets the path where this class was loaded
        // its the same thing as the path for the whole program
        programPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        programName = programPath.substring(programPath.lastIndexOf('/')+1);

        if (System.getProperty("os.name").startsWith("Windows")) {
            programPath = programPath.substring(1, programPath.lastIndexOf('/')+1);
            programPath = programPath.replaceAll("/", "\\\\");

        } else if (System.getProperty("os.name").startsWith("Linux")) {
            programPath = programPath.substring(0, programPath.lastIndexOf('/')+1);
        }

        System.out.println("Program path and name");
        System.out.println(programName);
        System.out.println(programPath);
    }

    //get the settings from the a file
    private void getSettings() {

        try {
            BufferedReader br = new BufferedReader(new FileReader(programPath + SETTINGS_FILE));

            br.readLine(); //get heading not needed
            StringTokenizer st = new StringTokenizer(br.readLine());

            openOnStartup = st.nextToken().equals("on");
            src = st.nextToken();
            to = st.nextToken();
            shortcut = st.nextToken();
            firstTime = false;

            br.close();

        } catch (FileNotFoundException e) {

            // if settings file not found
            // then get the default settings

            if (System.getProperty("os.name").startsWith("Windows")) {
                //delete previous registry value if it exist
                deleteRegValue(Display.APPLICATION_NAME);
            } else if (System.getProperty("os.name").startsWith("Linux")) {
                deleteStartupFile();
            }

            // default settings
            openOnStartup = false;
            src = "English";
            to = "Arabic";
            shortcut = "ctrl+T";

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


    public void saveSettings() {
        String heading = String.format("%-10s%-15s%-15s%s", "Startup", "src", "to", "shortcut");
        String options = String.format("%-10s%-15s%-15s%s", openOnStartup ? "on" : "off", src,  to, shortcut);

        try {

            BufferedWriter bw = new BufferedWriter(new FileWriter(programPath + SETTINGS_FILE));

            bw.write(heading);
            bw.newLine();
            bw.write(options);
            bw.close();

        } catch (IOException e) {
            System.out.println("Error while saving the settings");
            e.printStackTrace();
        }
    }

    //build languages map from a file
    private void initLanguages() {

        languages = new LinkedHashMap<>();

        try {

            BufferedReader br = new BufferedReader(new FileReader(programPath + LANGUAGES_FILE));

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

    public boolean isFirstTime() {
        return firstTime;
    }


    public void setOpenOnStartup(boolean openOnStartup) {


        if (System.getProperty("os.name").startsWith("Windows")) {

            //add a registry value to open the application on startup
            if (openOnStartup) {

                String val = "\"" + programPath + programName + "\"";

                setRegValue(Display.APPLICATION_NAME, val);

            //delete the registry value so it doesn't open the application on start up
            } else {
                deleteRegValue(Display.APPLICATION_NAME);
                System.out.println("Deleted registry value.");
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

    private void deleteStartupFile() {

        String path = System.getProperty("user.home") + "/.config/autostart/" + Display.APPLICATION_NAME + ".desktop";

        File startupFile = new File(path);

        try {
            Files.deleteIfExists(startupFile.toPath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addStartupFile() {

        String path = System.getProperty("user.home") + "/.config/autostart/" + Display.APPLICATION_NAME + ".desktop";

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
            System.out.println("Something went wrong while writing Translator.desktop");
            e.printStackTrace();
        }
    }


    // Windows related function
    private void setRegValue(String name, String value) {
         Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,
                "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run",
                name,
                value
        );
    }

    // Windows related function
    private void deleteRegValue(String name) {

        boolean isValueExist = Advapi32Util.registryValueExists(
                WinReg.HKEY_CURRENT_USER,
                "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run",
                name);

        //there is nothing to delete
        if (!isValueExist) return;

        Advapi32Util.registryDeleteValue(
                WinReg.HKEY_CURRENT_USER,
                "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run",
                name
        );
    }

    public boolean isOpenOnStartup() {
        return openOnStartup;
    }


    public String getShortcut() {
        return shortcut;
    }

    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
    }


    public void setSrc(String src) {
        this.src = src;
    }

    public String getSrc() {
        return src;
    }

    public String getEncodedSrc() {
        return languages.get(src);
    }


    public void setTo(String to) {
        this.to = to;
    }

    public String getTo() {
        return to;
    }

    public String getEncodedTo() {
        return languages.get(to);
   }


    public String[] getLanguages() {
        Object[] keys = languages.keySet().toArray();
        return Arrays.copyOf(keys, keys.length, String[].class);
    }
}
