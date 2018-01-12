package com.jtranslator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.*;
import java.net.URLEncoder;

class TranslateManager {

    private static final String GOOGLE_TRANSLATE_URL = "https://translate.google.com/m?hl=en&sl=%s&tl=%s&ie=UTF-8&prev=_m&q=%s";

    private ComponentOrientation dir;

    String translate(String src, String to, String content) {

        try {
            String url = String.format(GOOGLE_TRANSLATE_URL, src, to, URLEncoder.encode(content, "UTF-8"));

            Document doc = Jsoup.connect(url).get();
            Elements divs = doc.select("div");

            //set the direction of the text
            dir = divs.get(2).attr("dir").equals("rtl") ? ComponentOrientation.RIGHT_TO_LEFT :
                                                                     ComponentOrientation.LEFT_TO_RIGHT;

            //return the translation
            return divs.get(2).text();

        } catch (IOException e) {
            System.out.println("Error while getting the translation");
            System.out.println(e.getMessage());
            return null;
        }
    }

    //this method should be called after calling the translate method
    //to get the direction of the translated text
    ComponentOrientation getDir() {
        return dir;
    }
}
