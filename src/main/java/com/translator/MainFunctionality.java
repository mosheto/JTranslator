package com.translator;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;

import javax.swing.*;
import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainFunctionality implements HotKeyListener{

    private JTextArea textArea;
    private TextTransfer clipboard;
    private TranslateManager translateManager;

    public MainFunctionality(TranslateManager translateManager) {
        clipboard = new TextTransfer();

        textArea = new JTextArea(10, 40);
        textArea.setLineWrap(true);
        textArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        //test will make code to handle verity of languages
        textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        this.translateManager = translateManager;
    }

    @Override
    public void onHotKey(HotKey hotKey) {

        String str = translateManager.translate(clipboard.getClipboardContents());

        if (str == null) {
            JOptionPane.showMessageDialog(null, "There something wrong", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        textArea.setText(str);
        new Message(null, "the translating is", textArea);
    }
}