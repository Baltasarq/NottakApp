// NottakApp (c) Baltasar 2025 MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.view;


import com.devbaltasarq.nottakapp.core.Config;
import com.devbaltasarq.nottakapp.core.Notebook;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


/** The preferences controller.
  * @author baltasarq
  */
public class Preferences {
    public Preferences(JFrame owner, Config cfg, Notebook notebook)
    {
        this.view = new PreferencesView( owner );
        this.cfg = cfg;
        this.notebook = notebook;

        this.cfg2intf();
        this.setListeners();
    }
    
    // Dumps all the config in the corresponding fields.
    private void cfg2intf()
    {
        String notesPath = this.cfg.get( Config.Key.DATA_DIR_PATH );
        
        this.getView().getEdNotesPath().setText( notesPath );
    }
    
    private void setListeners()
    {
        final PreferencesView PREFS_DLG = this.getView();
        
        PREFS_DLG.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Dialog is closing.");
            }
        });

        PREFS_DLG.getBtEdNotesPath().addActionListener(
                                            (evt) -> this.doChangeCfgPath() );
        PREFS_DLG.getBtSave().addActionListener(
                                            (evt) -> this.save() );
    }
    
    /** @return the preferences view, as a JDialog. */
    public PreferencesView getView()
    {
        return this.view;
    }
    
    /** Show the dialog. */
    public void run()
    {
        this.getView().setVisible( true );
    }
    
    private void save()
    {
        final String DATA_PATH = this.cfg.get( Config.Key.DATA_DIR_PATH );
        
        try {
            Notebook.moveTo( DATA_PATH, this.notebook );
        } catch(IOException exc) {
            JOptionPane.showMessageDialog(
                            this.getView(),
                            exc.getMessage() );
            
            // Reset the data dir to the old one
            this.cfg.add( Config.Key.DATA_DIR_PATH, this.notebook.getPath() );
            JOptionPane.showMessageDialog(
                            this.getView(),
                            "Old notes path restored." );
        } finally {
            this.cfg.save();
        }
        
        this.getView().setVisible( false );
    }
    
    public void doChangeCfgPath()
    {
        final String OLD_PATH = this.cfg.get( Config.Key.DATA_DIR_PATH );
        var dlg = new JFileChooser();
        
        dlg.setDialogTitle( "Notes path" );
        dlg.setSelectedFile( new File( OLD_PATH ) );
        dlg.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        if ( dlg.showOpenDialog( this.getView() )
                                               == JFileChooser.APPROVE_OPTION )
        {
            File dir = dlg.getSelectedFile();
            
            this.cfg.add( Config.Key.DATA_DIR_PATH, dir.getAbsolutePath() );
            this.getView().getEdNotesPath().setText( dir.getAbsolutePath() );
        }
    }
    
    final private PreferencesView view;
    final private Notebook notebook;
    final private Config cfg;
}
