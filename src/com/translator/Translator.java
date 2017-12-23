package com.translator;

import com.tulskiy.keymaster.common.Provider;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Translator {

    public static final Image APPLICATION_ICON = loadImage("/t.png");;
    public static final String APPLICATION_NAME = "JTranslator";
    public static final String APPLICATION_VERSION = "v1.5";

    //for global keyListening
    private Provider p;
    private MainFunctionality listener;

    //vars if SystemTray is supported
    private SystemTray systemTray;
    private TrayIcon trayIcon;

    //application display and Icon
    private Display display;

    //Settings
    private Settings settings;

    //translate Manager
    private TranslateManager translateManager;

    public Translator() {

        init();

        if (SystemTray.isSupported())
            showTray();
    }

    //initialize the application
    private void init() {

        settings = new Settings();
        translateManager = new TranslateManager();

        //the keyListener
        listener = new MainFunctionality(translateManager, settings);

        //set up the display
        SwingUtilities.invokeLater(() ->{

            display = new Display(settings, this);
            display.setIconImage(APPLICATION_ICON);

            //is set visible to false on start up and the tray is supported
            //or true its the first time
            if (!settings.isFirstTime() && SystemTray.isSupported())
                display.setVisible(false);
            else if (!settings.isFirstTime()) {
                display.setVisible(true);
                display.setState(Frame.ICONIFIED);
            } else
                display.setVisible(true);

            display.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    onClosing(e);
                }
            });
        });

        p = Provider.getCurrentProvider(false);
        resetAndRegProvider();
    }


    //method if SystemTray is supported shoe it
    private void showTray() {

        systemTray = SystemTray.getSystemTray();
        trayIcon = new TrayIcon(APPLICATION_ICON);
        trayIcon.setImageAutoSize(true);

        PopupMenu popupMenu = new PopupMenu();

        MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(e -> stop());

        MenuItem open = new MenuItem("Open");
        open.addActionListener(e -> display.setVisible(true));

        popupMenu.add(open);
        popupMenu.addSeparator();
        popupMenu.add(exit);

        trayIcon.setPopupMenu(popupMenu);
        trayIcon.setToolTip(Translator.APPLICATION_NAME);
        trayIcon.addActionListener(e -> display.setVisible(true));

        try {
            systemTray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("Error, when adding the tray icon to the system tray");
            e.printStackTrace();
        }
    }

    public static BufferedImage loadImage(String path){
        try {

            return ImageIO.read(Translator.class.getResource(path));

        } catch (IOException e) {

            System.out.println("Something wrong while loading the icon image");
            e.printStackTrace();
            System.exit(1);
        }

        return null;
    }

    //when closing the app
    private void onClosing(WindowEvent e) {

        int option = JOptionPane.showConfirmDialog(display, "Do you really want to close?",
                 "Quit?", JOptionPane.YES_NO_OPTION);

        switch(option) {
            case JOptionPane.YES_OPTION:
                stop();
                break;
        }
    }

    //this method should be called if the shortcut changed
    //so i will going to call it in saveButton listener
    //after the saveButton clicked
    public void resetAndRegProvider() {
        p.reset();
        p.register(         // getKeyStroke accept shortcut without plus
                KeyStroke.getKeyStroke(settings.getShortcut().replaceAll("\\+", " ")),
                listener
        );
    }

    //to stop the application probably
    private void stop() {

        //stop listening on the keyboard
        p.reset();
        p.stop();

        //remove the tray properly
        if(SystemTray.isSupported())
            systemTray.remove(trayIcon);

        System.exit(0);
    }

    public Image getImgIcon() {
        return APPLICATION_ICON;
    }
}
