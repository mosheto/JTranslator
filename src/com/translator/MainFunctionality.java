package com.translator;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class MainFunctionality implements HotKeyListener{

    private Message message;
    private TranslateManager translateManager;
    private Settings settings;

    public MainFunctionality(TranslateManager translateManager, Settings settings) {
        this.settings = settings;
        this.translateManager = translateManager;
        this.message = new Message();
    }

    @Override
    public void onHotKey(HotKey hotKey) {

        if (message.isVisible()) return;

        String translation = translateManager.translate(
                settings.getEncodedSrc(),
                settings.getEncodedTo(),
                getClipboardContents()
        );

        if (translation == null){
            JOptionPane.showMessageDialog(null, "Something went wrong\ncheck your internet and try again"
                    , "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        message.setText(translation);
        message.setOrientation(translateManager.getDir());
        message.pack();
        message.setVisible(true);
    }

    public String getClipboardContents() {

        String result = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        Transferable contents = clipboard.getContents(null);

        boolean hasTransferableText =
                (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);

        if (hasTransferableText) {
            try {
                result = (String)contents.getTransferData(DataFlavor.stringFlavor);
            }
            catch (UnsupportedFlavorException | IOException ex){
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }
        return result;
    }
}