// NottakApp (c) Baltasar 2025 MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.view;


import javax.swing.JPanel;
import javax.swing.BorderFactory;


/** A panel with a title.
  * @author baltasarq
  */
public class JTitlePanel extends JPanel {
    public JTitlePanel(String title)
    {
        this.setBorder(
            BorderFactory.createTitledBorder( title ) );
    }
    
    public void replaceTitle(String title)
    {
        this.setBorder(
            BorderFactory.createTitledBorder( title ) );
    }
}
