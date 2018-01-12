package com.jtranslator;

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

    //previous clipboard content;
    private String prevClipContent;

    public MainFunctionality(TranslateManager translateManager, Settings settings) {
        this.settings = settings;
        this.translateManager = translateManager;
        this.message = new Message();
        this.prevClipContent = "";
    }

    @Override
    public void onHotKey(HotKey hotKey) {

        String clipboardContents = getClipboardContents().trim();

        //to protect from multiple shortcut presses
        if (message.isVisible() && prevClipContent.equals(clipboardContents)) return;

        prevClipContent = clipboardContents;

        String translation = translateManager.translate(
                settings.getEncodedSrc(),
                settings.getEncodedTo(),
                clipboardContents
        );

        if (translation == null){
            JOptionPane.showMessageDialog(null, "Something went wrong\ncheck your internet and try again"
                    , "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (message.isVisible()) message.setVisible(false);
        message.setOrientation(translateManager.getDir());
        message.setText(translation);
        message.pack();
        message.setVisible(true);
    }

    private String getClipboardContents() {

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