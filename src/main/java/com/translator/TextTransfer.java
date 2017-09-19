package com.translator;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.Toolkit;
import java.io.*;

public class TextTransfer implements ClipboardOwner{

    private Clipboard clipboard;

    public TextTransfer() {
        this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    }


    public void setClipboardContents(String aString){
        StringSelection stringSelection = new StringSelection(aString);
        clipboard.setContents(stringSelection, this);
    }

    /**
     * Get the String residing on the clipboard.
     *
     * @return any text found on the Clipboard; if none found, return an
     * empty String.
     */
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

    /**
     * if you lost the Ownership you will get the clipboard and the old contents of it
     */
    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {}
}