package com.translator;

import java.awt.*;
import javax.swing.*;

public class Display extends JFrame {

    public static final int WIDTH = 352, HEIGHT = 367;

    //handle switch between panels
    private JPanel layouts;

    public static final String APPLICATION_NAME = "Translator";
    public static final String SETTINGS_GUI = "SettingsGUI";
    public static final String ABOUT = "About";

    private Translator translator;

    private SettingsGUI settingsGUI;
    private About about;

    //get the state of the frame and save it
    //when clicking Save
    private Settings settings;

    public Display(Settings settings, Translator translator) {
        this.settings = settings;
        this.translator = translator;
        initComponents();
    }

    private void initComponents() {
        layouts = new JPanel(new CardLayout());
        settingsGUI = new SettingsGUI(this, settings, translator);
        about = new About(this);

        layouts.add(settingsGUI, SETTINGS_GUI);
        layouts.add(about, ABOUT);

        this.setContentPane(layouts);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle(APPLICATION_NAME);
        //setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        //center the program in the screen
        int x = Toolkit.getDefaultToolkit().getScreenSize().width / 2 - WIDTH / 2;
        int y = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - HEIGHT / 2;
        this.setLocation(x, y);

        this.setResizable(false);
        this.pack();
    }

    public void show(String name) {
        ((CardLayout) layouts.getLayout()).show(layouts, name);
    }
}
