package com.translator;

import com.tulskiy.keymaster.common.Provider;
import jdk.nashorn.internal.scripts.JD;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class Translator {

    //for global keyListening
    private Provider p;
    private String shortCut = "control shift T";

    //vars if SystemTray is supported
    private SystemTray systemTray;
    private TrayIcon trayIcon;

    //application display and Icon
    private Display display;
    private Image imgIcon;

    //Settings
    private Settings settings;

    //the hidden browser
    private WebDriver browser;
    private ChromeOptions options;

    //translate Manager
    private TranslateManager translateManager;

    public Translator() {

        init();

        if (SystemTray.isSupported())
            showTray();
    }

    private void init() {

        settings = new Settings();

        System.setProperty("webdriver.chrome.driver", settings.getChromeDriverPath());

        options = new ChromeOptions();
        options.addArguments("--headless");
        browser = new ChromeDriver(options);

        translateManager = new TranslateManager(browser, settings);

        //the keyListener
        p = Provider.getCurrentProvider(false);
        p.register(KeyStroke.getKeyStroke(shortCut), new MainFunctionality(translateManager));

        display = new Display(settings);
        display.setIconImage(imgIcon);

        //is set visible to false on start up and the tray is supported
        //or true its the first time
        if (settings.isOpenOnStartup() && SystemTray.isSupported())
            display.setVisible(false);
        else
            display.setVisible(true);

        display.setLocationRelativeTo(null);
        display.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onClosing(e);
            }
        });
    }

    //methods if SystemTray is supported
    private void showTray() {

        systemTray = SystemTray.getSystemTray();
        imgIcon = ImageLoader.loadImage("/res/t.png");
        trayIcon = new TrayIcon(imgIcon);
        trayIcon.setImageAutoSize(true);

        PopupMenu popupMenu = new PopupMenu();

        MenuItem exit = new MenuItem("Exit");
        exit.addActionListener((e) -> stop());

        MenuItem open = new MenuItem("Open");
        open.addActionListener((e) -> display.setVisible(true));

        popupMenu.add(open);
        popupMenu.addSeparator();
        popupMenu.add(exit);

        trayIcon.setPopupMenu(popupMenu);
        trayIcon.setToolTip("Translator");
        trayIcon.addActionListener((e) -> display.setVisible(true));

        try {
            systemTray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("Error, when adding the tray icon to the system tray");
            e.printStackTrace();
        }
    }

    //when close the app
    private void onClosing(WindowEvent e) {
        new CloseDialog();
    }

    private void stop() {

        p.reset();
        p.stop();

        browser.close();

        if(SystemTray.isSupported())
            systemTray.remove(trayIcon);

        System.exit(0);
    }

    private class CloseDialog extends JDialog {

        public CloseDialog() {
            super();

            JButton minimize = new JButton("Minimize");
            minimize.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    display.setVisible(false);
                    dispose();
                }
            });

            if (!SystemTray.isSupported()) {
                minimize.setEnabled(false);
                minimize.setToolTipText("Your system doesn't\nsupport system tray");
            }

            JButton close = new JButton("Close");
            close.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {

                    if (JOptionPane.showConfirmDialog(null,
                            "Are you sure you want to close the translator?", "Really Closing?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){

                        stop(); //stop the application :(
                    }
                    dispose();
                }
            });


            System.out.println("creating the window..");

            // set the position of the window
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            setLocation(dim.width/2 - 375/2 + 8, dim.height/2-101/2-6);

            // Create a message
            JPanel messagePane = new JPanel();
            messagePane.add(new JLabel("Click close to close the app or minimize to put it in the tray."));
            // get content pane, which is usually the
            // Container of all the dialog's components.
            getContentPane().add(messagePane);

            //Create a button
            JPanel buttonPane = new JPanel();
            buttonPane.add(minimize);
            buttonPane.add(close);
            // set action listener on the button
            getContentPane().add(buttonPane, BorderLayout.PAGE_END);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            pack();
            setVisible(true);
        }
    }
}
