// NottakApp (c) Baltasar 2025 MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.view;


import java.util.logging.Logger;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.Point;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.devbaltasarq.nottakapp.core.LogWriter;
import com.devbaltasarq.nottakapp.core.AppInfo;
import com.devbaltasarq.nottakapp.core.Config;
import com.devbaltasarq.nottakapp.core.Notebook;
import com.devbaltasarq.nottakapp.core.NoteProxy;
import com.devbaltasarq.nottakapp.core.Note;
import java.awt.Font;


/** The main window for the application.
  * @author baltasarq
  */
public class MainWindow {
    private static final Logger LOG = Logger.getLogger( MainWindow.class.getName() );
    
    public MainWindow()
    {
        final var FONT = new Font( Font.SANS_SERIF, Font.PLAIN, 16 );
        
        this.working = false;
        this.config = Config.restore( AppInfo.NAME, AppInfo.NOTES_DIR_NAME );
        this.notebook = Notebook.restoreFrom( this.config.get( Config.Key.DATA_DIR_PATH ) );
        this.currentNote = null;
        
        this.view = new MainWindowView( FONT );
        this.editor = new Editor( this.view.getEditorView() );
        this.notesTree = new NotesTree( this.view.getNotesTreeView(), this.notebook );
        this.notesTree.setSelectedAction( (note) -> this.selectedTreeNode( note ) );
        this.logViewer = new LogWriter(
                                LOG, 
                                   (str) ->
                                    this.view.getLogViewerView().append( str + "\n" ));
        LOG.addHandler( this.logViewer );
        
        this.view.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE  );
        this.view.getOpQuit().addActionListener( (o) -> this.quit() );
        this.view.getOpAbout().addActionListener( (o) -> this.about() );
        this.view.getOpNewNote().addActionListener( (o) -> this.newNote() );
        this.view.getOpViewLog().addActionListener(
                                        (o) -> this.getView().showLog() );
        this.view.getOpDeleteNote().addActionListener(
                                        (o) -> this.deleteCurrentNote() );
        
        this.view.addWindowListener( this.mainWindowListener );
        this.applyConfig();
    }
    
    /** Makes the window visible. */
    public void show()
    {
        LOG.info( String.format( "View shown: %s",  AppInfo.TITLE ));
        this.getView().setVisible( true );
    }
    
    /** Unique end point for the app. */
    public void quit()
    {
        this.getEditor().saveTextToNote();
        this.saveConfig();
        
        this.getView().setVisible( false );
        this.getView().dispose();
                
        this.notebook.saveAll( this.config.get( Config.Key.DATA_DIR_PATH ) );
        LOG.info( String.format( "View hidden: %s", AppInfo.TITLE ));
    }
    
    /** On creating a new note. */
    public void newNote()
    {
        if ( !this.working ) {
            this.working = true;
            
            final Note NOTE = new Note( "New note" );
            final NoteProxy PROXY = NoteProxy.fromNote( NOTE );

            this.notebook.add( PROXY );
            this.notesTree.add( PROXY );
            LOG.info(
                    String.format(
                            "New note created: '%s'" + NOTE.getIdAsString() ));
            this.selectedTreeNode( PROXY );
            this.working = false;
        }
    }
    
    public void deleteCurrentNote()
    {
        if ( !this.working ) {
            this.working = true;
            this.notebook.delete( this.currentNote );
            this.notesTree.refreshAll();
            this.editor.setNote( null );
            this.working = false;
        }
    }

    public void selectedTreeNode(NoteProxy note)
    {
        this.updateEditor( note );
        this.currentNote = note;
    }
    
    /** Shows app's info. */
    public void about()
    {
        JOptionPane.showMessageDialog(
                        this.view,
                        AppInfo.TITLE,
                        "About",
                        JOptionPane.INFORMATION_MESSAGE );
    }
    
    /** @return the view of this window. */
    public MainWindowView getView()
    {
        return this.view;
    }
    
    /** Sets the status bar message to "Ready". */
    public void setStatus()
    {
        this.setStatus( "Ready" );
    }
    
    /** Sets the new status at the bottom of the window.
      * @param newText the new status.
      */
    public void setStatus(String newText)
    {
        this.view.getStatusBar().setText( newText );
    }
    
    /** @return the editor. */
    public Editor getEditor()
    {
        return this.editor;
    }
    
    /** Update the whole view. */
    public void update()
    {
        final String MSG_UPDATING = "Updating view...";
        
        LOG.info( MSG_UPDATING );
        this.setStatus( MSG_UPDATING );
        LOG.entering( "MainWindow", "update" );
        
        this.notesTree.removeAll();
        
        for(NoteProxy note: this.notebook.getAllNotes()) {
            this.notesTree.add( note );
        }
        
        this.notesTree.expandAll();
        this.updateEditor( this.currentNote );
        
        if ( this.currentNote == null ) {
            LOG.info( "no node selected." );
        }
        
        this.setStatus();
        LOG.exiting( "MainWindow", "update" );
    }
    
    /** Updates the editor with the contents of the note.
      * @param noteProxy the proxy for this note.
      */
    public void updateEditor(NoteProxy noteProxy)
    {
        if ( noteProxy != null ) {
            final Note NOTE = noteProxy.getNote();
        
            this.editor.setNote( NOTE );
            LOG.info( "note shown in editor: " + NOTE.getIdAsString() );
        }
    }
    
    /** Applies the configuration to the app. */
    private void applyConfig()
    {
        String strWidth = this.config.get( Config.Key.WIDTH );
        String strHeight = this.config.get( Config.Key.HEIGHT );
        String strLeft = this.config.get( Config.Key.LEFT );
        String strTop = this.config.get( Config.Key.TOP );
        
        if ( strWidth != null
          && strHeight != null )
        {
            this.view.setSize(
                Integer.parseInt( strWidth ),
                Integer.parseInt( strHeight ));
        }
        
        if ( strLeft != null
          && strTop != null )
        {
            this.view.setLocation( new Point( 
                Integer.parseInt( strLeft ),
                Integer.parseInt( strTop )));
        }
    }
    
    /** Saves the settings to the config. */
    private void saveConfig()
    {
        final Point POINT = this.view.getLocation();
        
        this.config.add( Config.Key.WIDTH, "" + this.view.getWidth() );
        this.config.add( Config.Key.HEIGHT, "" + this.view.getHeight() );
        this.config.add( Config.Key.LEFT, "" + POINT.x );
        this.config.add( Config.Key.TOP, "" + POINT.y );
        
        new Thread( () -> this.config.save() ).start();
    }
    
    private boolean working;
    private NoteProxy currentNote;
    private final MainWindowView view;
    private final Editor editor;
    private final LogWriter logViewer;
    private final NotesTree notesTree;
    private final Notebook notebook;
    private final Config config;
    private final WindowListener mainWindowListener = new WindowListener() {
        @Override
        public void windowClosing(WindowEvent evt)
        {
            MainWindow.this.quit();
        }

        @Override
        public void windowDeactivated(WindowEvent evt)
        {
        }

        @Override
        public void windowActivated(WindowEvent evt)
        {
            MainWindow.this.update();
        }

        @Override
        public void windowDeiconified(WindowEvent evt)
        {
        }

        @Override
        public void windowIconified(WindowEvent evt)
        {
        }

        @Override
        public void windowClosed(WindowEvent evt)
        {
        }

        @Override
        public void windowOpened(WindowEvent evt)
        {
        }
    };
}
