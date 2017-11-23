package com.translator;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.*;

public class Settings {

    private boolean openOnStartup,
                    firstTime; //if this is the first time the user open the application
    private String src, to;
    private String shortcut;

    private String programPath;
    private static final String SETTINGS_FILE = "settings.t";
    private static final String LANGUAGES_FILE = "langs.t";

    private Map<String, String> languages;

    public Settings() {

        //this line gets the path where this class was loaded
        // its the same thing as the path for the whole program
        programPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        programPath = programPath.substring(1, programPath.lastIndexOf('/')+1);

        languages = new LinkedHashMap<>();
        initLanguages();
        getSettings();
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
            //default settings

            boolean isExist = Advapi32Util.registryValueExists(
                    WinReg.HKEY_CURRENT_USER,
                    "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run",
                    Display.APPLICATION_NAME
            );

            if (isExist)  {
                Advapi32Util.registryDeleteValue(
                        WinReg.HKEY_CURRENT_USER,
                        "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run",
                        Display.APPLICATION_NAME
                );
            }

            openOnStartup = false;

            src = "English";
            to = "Arabic";
            shortcut = "ctrl+T"; // default shortcut

            //if the settings file not found
            //then this is the first time the user
            //open the program
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


    //TODO implement startup for linux
    public void setOpenOnStartup(boolean openOnStartup) {


        if (System.getProperty("os.name").startsWith("Windows")) {

            //add a registry value to open the application on startup
            if (this.openOnStartup == false && openOnStartup == true) {

                String appPath = "\"" + programPath.replaceAll("/", "\\\\") + "Translator.jar\"";

                Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,
                        "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run",
                        Display.APPLICATION_NAME,
                        appPath
                );

                //delete the registry value so it doesn't open the application on start up
            } else if (this.openOnStartup == true && openOnStartup == false) {

                Advapi32Util.registryDeleteValue(
                        WinReg.HKEY_CURRENT_USER,
                        "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run",
                        Display.APPLICATION_NAME
                );
            }

            this.openOnStartup = openOnStartup;
        }
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
