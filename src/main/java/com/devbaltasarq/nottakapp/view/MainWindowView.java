// NottakApp (c) Baltasar 2025 MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.view;


import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import javax.swing.KeyStroke;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTree;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.BorderFactory;

import com.devbaltasarq.nottakapp.core.AppInfo;
import java.awt.Color;
import java.awt.Font;


/** The Swing view for this window.
  * @author baltasarq
  */
public class MainWindowView extends JFrame {
    private final Dimension SIZE = new Dimension( 600, 440 );
    
    public MainWindowView(Font font)
    {
        this.setMinimumSize( SIZE );
        this.setTitle( AppInfo.TITLE );
        this.setLocationByPlatform( true );
        
        this.font = font;
        if ( font == null ) {
            this.font = new Font( Font.SANS_SERIF, Font.PLAIN, 16 );
        }

        this.build();
        this.pack();
        this.revalidate();
        this.mainSplitPanel.setDividerLocation( 0.3 );
    }
    
    private void build()
    {
        final var MAIN_BLAY = new BorderLayout();
        final var BLAY_FOR_MAIN_PANEL = new BorderLayout();
        final var PNL_MAIN = new JPanel( BLAY_FOR_MAIN_PANEL );
        
        MAIN_BLAY.setHgap( 5 );
        MAIN_BLAY.setVgap( 5 );

        BLAY_FOR_MAIN_PANEL.setHgap( 5 );
        BLAY_FOR_MAIN_PANEL.setVgap( 5 );
        
        PNL_MAIN.add( this.buildSplitPanel(), BorderLayout.CENTER );
        PNL_MAIN.add( this.buildLogViewer(), BorderLayout.PAGE_END );       
        
        this.setJMenuBar( this.buildMainMenu() );
        this.setLayout( MAIN_BLAY );
        this.add( PNL_MAIN, BorderLayout.CENTER );
        this.add( this.buildStatusBar(), BorderLayout.PAGE_END );
    }
    
    private JPanel buildLogViewer()
    {
        final var BLAY_FOR_LOG = new BorderLayout();
        final var FONT = new Font( Font.MONOSPACED, Font.PLAIN, 16 );
        
        BLAY_FOR_LOG.setHgap( 5 );
        BLAY_FOR_LOG.setVgap( 5 );
        
        this.logViewerView = new JTextArea( 5, 80 );        
        this.logViewerView.setBackground( Color.BLACK );
        this.logViewerView.setForeground( Color.WHITE );
        this.logViewerView.setFont( FONT );
        this.logViewerView.setEditable( false );
        
        this.pnlLog = new JPanel( BLAY_FOR_LOG );
        this.pnlLog.add( new JScrollPane( this.logViewerView ),
                            BorderLayout.CENTER );
        this.pnlLog.setVisible( false );

        return this.pnlLog;
    }
    
    private JSplitPane buildSplitPanel()
    {
        this.mainSplitPanel = new JSplitPane();

        this.setMargin( this.mainSplitPanel, 5 );
        this.mainSplitPanel.setLeftComponent( this.buildNotesTree() );
        this.mainSplitPanel.setRightComponent( this.buildEditor() );
        return this.mainSplitPanel;
    }
    
    private JTree buildNotesTree()
    {
        this.notesTree = new NotesTreeView( this.font );
        return this.notesTree;
    }
    
    private EditorView buildEditor()
    {
        this.editorView = new EditorView( this.font );
        return this.editorView;
    }
    
    private JMenuBar buildMainMenu()
    {
        final var TORET = new JMenuBar();
        final var FILE = new JMenu( "File" );
        final var EDIT = new JMenu( "Edit" );
        final var VIEW = new JMenu( "View" );
        final var HELP = new JMenu( "Help" );
        
        this.opQuit = new JMenuItem( "Quit" );
        this.opQuit.setMnemonic( 'q' );
        this.opQuit.setAccelerator(
                KeyStroke.getKeyStroke( KeyEvent.VK_Q,
                                            InputEvent.CTRL_DOWN_MASK ));
        FILE.setMnemonic( 'f' );
        FILE.add( this.opQuit );
        
        this.opAbout = new JMenuItem( "About" );
        this.opAbout.setMnemonic( 'a' );
        HELP.setMnemonic( 'h' );
        HELP.add( this.opAbout );
        
        this.opNewNote = new JMenuItem( "New note" );
        this.opNewNote.setMnemonic( 'n' );
        this.opNewNote.setAccelerator(
                KeyStroke.getKeyStroke( KeyEvent.VK_N,
                                            InputEvent.CTRL_DOWN_MASK ));
        
        this.opDeleteNote = new JMenuItem( "Delete note" );
        this.opDeleteNote.setMnemonic( 'n' );
        this.opDeleteNote.setAccelerator(
                KeyStroke.getKeyStroke( KeyEvent.VK_DELETE,
                                            InputEvent.CTRL_DOWN_MASK ));

        EDIT.setMnemonic( 'e' );
        EDIT.add( this.opNewNote );
        EDIT.add( this.opDeleteNote );
        
        this.opViewLog = new JMenuItem( "View log" );
        this.opViewLog.setMnemonic( 'l' );
        this.opViewLog.setAccelerator(
                KeyStroke.getKeyStroke( KeyEvent.VK_F2,
                                            InputEvent.CTRL_DOWN_MASK ));
        VIEW.setMnemonic( 'v' );
        VIEW.add( this.opViewLog );
        
        TORET.add( FILE );
        TORET.add( EDIT );
        TORET.add( VIEW );
        TORET.add( HELP );
        return TORET;
    }
    
    private JTextField buildStatusBar()
    {
        this.statusBar = new JTextField();
        
        this.statusBar.setText( "Ready" );
        this.statusBar.setEditable( false );
        return this.statusBar;
    }
    
    private void setMargin(JComponent widget, int size)
    {
        widget.setBorder(
                    BorderFactory.createEmptyBorder(
                                    size, size, size, size ) );
    }
    
    public void showLog()
    {
        boolean isLogVisible = this.pnlLog.isVisible();
        
        this.pnlLog.setVisible( !isLogVisible );
        this.revalidate();
    }
    
    /** @return the quit option. */
    public JMenuItem getOpQuit()
    {
        return this.opQuit;
    }
    
    /** @return the new note option. */
    public JMenuItem getOpNewNote()
    {
        return this.opNewNote;
    }
    
    /** @return the delete note option. */
    public JMenuItem getOpDeleteNote()
    {
        return this.opDeleteNote;
    }
    
    /** @return the view log option.. */
    public JMenuItem getOpViewLog()
    {
        return this.opViewLog;
    }
    
    /** @return the about option. */
    public JMenuItem getOpAbout()
    {
        return this.opAbout;
    }
    
    /** @return the tree of notes. */
    public NotesTreeView getNotesTreeView()
    {
        return this.notesTree;
    }
    
    /** @return the status bar. */
    public JTextField getStatusBar()
    {
        return this.statusBar;
    }
    
    /** @return the editor. */
    public EditorView getEditorView()
    {
        return this.editorView;
    }
    
    /** @return the log viewer. */
    public JTextArea getLogViewerView()
    {
        return this.logViewerView;
    }
    
    private NotesTreeView notesTree;
    private JMenuItem opQuit;
    private JMenuItem opAbout;
    private JMenuItem opNewNote;
    private JMenuItem opDeleteNote;
    private JMenuItem opViewLog;
    private EditorView editorView;
    private JSplitPane mainSplitPanel;
    private JTextField statusBar;
    private JTextArea logViewerView;
    private JPanel pnlLog;
    private Font font;
}
