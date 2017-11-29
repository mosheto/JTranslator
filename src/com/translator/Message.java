package com.translator;

import javax.swing.*;
import java.awt.*;

public class Message extends JDialog {

    private JPanel contentPane;
    private JTextArea textArea;

    private Font messageFont;
    private final int maxWidth = 426;
    private final int offset = 20;

    public Message() {

        contentPane = new JPanel(new BorderLayout());
        textArea = new JTextArea();
        messageFont = new Font("Arial", Font.BOLD, 25);

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(messageFont);

        contentPane.add(textArea);

        setMinimumSize(new Dimension(200, 75));
        setTitle("Translation message");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setContentPane(contentPane);
        pack();
    }

    public void setText(String text) {
        textArea.setText(text);

        // 10 is dummy value it will be set internally to preferred height
        textArea.setSize(getTextWidth(text), 10);
        setSize(getTextWidth(text), 10);
    }

    private int getTextWidth(String text) {

        FontMetrics fm = getFontMetrics(messageFont);

        int widthOfText = SwingUtilities.computeStringWidth(fm, text);

        return widthOfText + offset <= maxWidth ? widthOfText + offset : maxWidth;
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public void setOrientation(ComponentOrientation o) {
        textArea.setComponentOrientation(o);
    }
}
