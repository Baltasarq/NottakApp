// NottakApp (c) Baltasar 2025 MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.view;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;


/** Preferences window
  * @author baltasarq
  */
public class PreferencesView extends JDialog {
    private final Dimension SIZE = new Dimension( 300, 220 );
    
    public PreferencesView(JFrame owner)
    {
        this.build();
        this.setMinimumSize( SIZE );
        this.setSize( SIZE );
        this.setLocationRelativeTo( owner );
        this.setTitle( "Preferences" );
    }
    
    private void build()
    {
        var ly = new BorderLayout();
        var mainPanel = new JPanel( ly );
        
        ly.setHgap( 10 );
        ly.setVgap( 10 );
        
        mainPanel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
        this.add( this.buildPanelClose(), BorderLayout.PAGE_END );
        this.add( this.buildPanelNotesPath(), BorderLayout.PAGE_START );
        this.add( mainPanel );
    }
    
    private JPanel buildPanelNotesPath()
    {
        final Border TITLE_BORDER = BorderFactory.createEmptyBorder( 5, 5, 10, 5 );
        final Border MARGIN_BORDER = BorderFactory.createLineBorder( Color.BLACK );
        final var BORDER = new CompoundBorder( TITLE_BORDER, MARGIN_BORDER );
        var ly = new BorderLayout();
        var toret = new JPanel( ly );
        
        ly.setHgap( 5 );
        ly.setVgap( 5 );
        
        toret.setBorder(
            BorderFactory.createTitledBorder( BORDER, "Notes path" ) );
        this.edNotesPath = new JTextField();
        this.edNotesPath.setEditable( false );
        this.btEdNotesPath = new JButton( "..." );
        
        toret.add(this.edNotesPath, BorderLayout.CENTER );
        toret.add( this.btEdNotesPath, BorderLayout.LINE_END );
        return toret;
    }
    
    private JPanel buildPanelClose()
    {
        var ly = new BorderLayout();
        var toret = new JPanel( ly );
        
        ly.setHgap( 10 );
        ly.setVgap( 10 );
        
        this.btSave = new JButton( "Save" );
        toret.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
        toret.add( this.btSave, BorderLayout.LINE_END );
        
        return toret;
    }
   
    /** @return the text box edit for the config path. */
    public JTextField getEdNotesPath()
    {
        return this.edNotesPath;
    }
    
    /** @return the button to change the path of the config. */
    public JButton getBtEdNotesPath()
    {
        return this.btEdNotesPath;
    }
    
    /** @return the button to save the preferences. */
    public JButton getBtSave()
    {
        return this.btSave;
    }
    
    private JTextField edNotesPath;
    private JButton btEdNotesPath;
    private JButton btSave;
}
