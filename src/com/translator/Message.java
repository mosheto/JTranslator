package com.translator;

import javax.swing.*;
import java.awt.*;

public class Message extends JDialog {

    private JPanel contentPane;
    private JTextArea textArea;

    public Message() {

        contentPane = new JPanel(new BorderLayout());
        textArea = new JTextArea();
        Font font = new Font("Arial", Font.BOLD, 25);


        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(font);

        contentPane.add(textArea);

        setMinimumSize(new Dimension(200, 75));
        setTitle("Translation message");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setContentPane(contentPane);
        pack();
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public void setOrientation(ComponentOrientation o) {
        textArea.setComponentOrientation(o);
    }

    public void setText(String text) {
        textArea.setText(text);
    }
}
