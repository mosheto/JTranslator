package com.translator;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

class TranslateManager {

    private static final String GOOGLE_TRANSLATE_URL = "https://translate.google.com/#auto/";

    private WebDriver browser;
    private Settings settings;

    TranslateManager(WebDriver browser, Settings settings) {
        this.settings = settings;
        this.browser = browser;
    }

    String translate(String str) {
        String url = createURL(settings.getLanguage(), str);

        if (url == null) return null;

        System.out.println(url);

        browser.navigate().to(url);
        browser.navigate().refresh();

        WebDriverWait wait = new WebDriverWait(browser, 10);

        wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));

        try{
            return browser.findElement(By.id("result_box")).getText();
        } catch (NoSuchElementException e) {
            System.out.println("Error, check your connection!");
            return null;
        }
    }

    //language must be standard like en for english or ar for arabic
    private String createURL(String language, String stringToTranslate) {

        StringBuilder sb = new StringBuilder(GOOGLE_TRANSLATE_URL);

        try {
            String stringToTranslateEncoded = URLEncoder.encode(stringToTranslate, "UTF-8");
            sb.append(language).append("/").append(stringToTranslateEncoded);

            if (sb.toString().length() >= 5000)
                return null;
            else
                return sb.toString();

        } catch (UnsupportedEncodingException e) {
            System.out.println("error when encoding the string");
            e.printStackTrace();
            return null;
        }
    }
}
