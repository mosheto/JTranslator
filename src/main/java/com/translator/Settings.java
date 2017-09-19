package com.translator;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Settings {

    private boolean openOnStartup;
    private String translateTo;
    private String chromeDriverPath;

    private Map<String, String> supportedLanguages;

    public Settings() {
        supportedLanguages = new HashMap<>();
        initSupportedLanguages();
        getSettings();
    }

    //get the settings from the a file
    private void getSettings() {

        try {
            BufferedReader br = new BufferedReader(new FileReader("settings.t"));
            br.readLine(); //not needed here

            StringTokenizer st = new StringTokenizer(br.readLine());

            openOnStartup = st.nextToken().equals("on");
            translateTo = st.nextToken();
            chromeDriverPath = br.readLine();

            br.close();

        } catch (FileNotFoundException e) {
            //default settings
            openOnStartup = false;
            translateTo = "Arabic";
            saveSettings();

        } catch (IOException e) {
            System.out.println("Error retrieving the settings");
            e.printStackTrace();
        }
    }

    public void saveSettings() {
        String s1 = String.format("%-10s%-15s\n", "Startup", "Translate to");
        String s2 = String.format("%-10s%-15s", openOnStartup ? "on" : "off", translateTo);

        try {
            FileWriter fileWriter = new FileWriter("settings.t");

            fileWriter.write(s1);
            fileWriter.write(s2);
            fileWriter.write(chromeDriverPath);
            fileWriter.close();

        } catch (IOException e) {
            System.out.println("Error writing the settings");
            e.printStackTrace();
        }
    }

    //TODO in future build the map from a file and add more languages
    private void initSupportedLanguages() {
        supportedLanguages.put("Arabic", "ar");
        supportedLanguages.put("English", "en");
    }

    //SETTERS ANS GETTERS


    public String getChromeDriverPath() {
        return chromeDriverPath;
    }

    public void setOpenOnStartup(boolean openOnStartup) {
        this.openOnStartup = openOnStartup;
    }

    public void setTranslateTo(String translateTo) {
        this.translateTo = translateTo;
    }

    public boolean isOpenOnStartup() {
        return openOnStartup;
    }

    public String getTranslateTo() {
        return translateTo;
    }

   public String getLanguage() {
        return supportedLanguages.get(translateTo);
   }
}
