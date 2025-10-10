package com.erp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {
    private static final LanguageManager instance = new LanguageManager();
    private Locale currentLocale;
    private ResourceBundle messages;
    private final List<LanguageObserver> observers = new ArrayList<>();

    private LanguageManager() {
        setLocale(new Locale("pt", "BR"));
    }

    public static LanguageManager getInstance() {
        return instance;
    }

    public void setLocale(Locale locale) {
        this.currentLocale = locale;
        this.messages = ResourceBundle.getBundle("messages", currentLocale);
        notifyObservers();
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }
    public ResourceBundle getMessages() {
        return messages;
    }
    public void addObserver(LanguageObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(LanguageObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (LanguageObserver observer : observers) {
            observer.updateLanguage();
        }
    }
}