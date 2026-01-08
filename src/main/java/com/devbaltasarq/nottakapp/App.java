// NottakApp (c) Baltasar 2025 MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp;


import com.devbaltasarq.nottakapp.view.MainWindow;

import java.awt.EventQueue;
import javax.swing.UIManager;


public class App {
    public static void main(String[] args)
    {
        // Prepare look & feel, if possible
        try {
            System.setProperty( "swing.aatext", "true" );
            System.setProperty("awt.useSystemAAFontSettings", "on" );
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        } catch (Exception ignored) {
        }
        
        EventQueue.invokeLater( () -> new MainWindow().show());
    }
}
