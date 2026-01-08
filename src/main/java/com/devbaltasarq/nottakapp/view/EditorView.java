// NottakApp (c) Baltasar 2025 MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.view;


import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import javax.swing.JPanel;
import javax.swing.JEditorPane;
import javax.swing.JTextField;
import javax.swing.text.html.HTMLEditorKit;

import com.devbaltasarq.nottakapp.core.TagSet;
import javax.swing.JScrollPane;


/** The view for the editor.
  * @author baltasarq
  */
public class EditorView extends JPanel {
    private final String MSG_NO_NOTE = "No note.";

    public EditorView(Font font)
    {
        final BorderLayout BLAY = new BorderLayout();
        
        BLAY.setHgap( 5 );
        BLAY.setVgap( 5 );
        
        this.font = font;
        if ( font == null ) {
            this.font = new Font( Font.SANS_SERIF, Font.PLAIN, 16 );
        }
        
        this.setLayout( BLAY );
        this.add( this.buildEditor(), BorderLayout.CENTER );
        this.add( this.buildTitleEditor(), BorderLayout.PAGE_START );
        this.add( this.buildTagsEditor(), BorderLayout.PAGE_END );
    }
    
    private JScrollPane buildEditor()
    {
        final var KIT = new HTMLEditorKit();
        
        this.editor = new JEditorPane();
        this.editor.setContentType( "text/html" );
        this.editor.setEditorKitForContentType( "text/html", KIT );
        this.editor.putClientProperty(
                        JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE );
        this.editor.setFont( this.font );
        
        return new JScrollPane( this.editor );
    }
    
    private JTitlePanel buildTitleEditor()
    {
        final var LAY = new BorderLayout();
        
        this.edTitle = new JTextField();
        this.edTitle.setFont( this.font );
        
        this.titlePanel = new JTitlePanel( "Title" );
        this.titlePanel.setLayout( LAY );
        this.titlePanel.add( this.edTitle, BorderLayout.CENTER );
        return this.titlePanel;
    }
    
    private JTitlePanel buildTagsEditor()
    {
        final var LAY = new BorderLayout();
        final Font FONT = new Font(
                            this.font.getFamily(),
                            this.font.getStyle(),
                            18 );
        
        this.edTags = new JTextField();
        this.edTags.setFont( FONT );
        
        this.tagsPanel = new JTitlePanel( "Tags" );
        this.tagsPanel.setLayout( LAY );
        this.tagsPanel.setLayout( LAY );
        this.tagsPanel.add( this.edTags, BorderLayout.CENTER );
        return this.tagsPanel;
    }
    
    /** @return the editor inside. */
    public JEditorPane getEditor()
    {
        return this.editor;
    }
    
    /** @return the text field for the title editor. */
    public String getTitle()
    {
        return this.edTitle.getText();
    }
    
    /** Sets the title in the view.
      * @param title the title of the note.
      */
    public void setTitle(String title)
    {
        this.edTitle.setText( title );
    }
    
    /** @return the tags as edited by the user. */
    public TagSet getTags()
    {
        final var TORET = new TagSet();
        
        TORET.addAllFromString( this.edTags.getText() );
        return TORET;
    }
    
    /** Sets the tags in the editor.
      * @param ts the tags, as a TagSet object.
      * @see com.devbaltasarq.nottakapp.core.TagSet
      */
    public void setTags(TagSet ts)
    {
        this.edTags.setText( ts.toString() );
    }
    
    /** Hide nearly everything, show only the minimum. */
    public void setNoNoteStatus()
    {
        this.edTitle.setEditable( false );
        this.edTags.setEditable( false );
        this.editor.setEditable( false );
        
        this.edTitle.setText( MSG_NO_NOTE );
        this.edTags.setText( MSG_NO_NOTE );
        this.editor.setText( MSG_NO_NOTE );
        this.revalidate();
    }
    
    /** Show all sections. */
    public void setNoteStatus()
    {
        this.edTitle.setEditable( true );
        this.edTags.setEditable( true );
        this.editor.setEditable( true );
        this.revalidate();
    }
    
    /** Sets a focus listener so the data is saved.
      * @param action something to do when focus is lost.
      */
    public void onFocusLost(Runnable action)
    {
        final var ON_FOCUS_LOST = new FocusListener() {
            @Override
            public void focusGained(FocusEvent evt)
            {   
            }
            @Override
            public void focusLost(FocusEvent evt)
            {
                action.run();
            }
        };
        
        this.edTitle.addFocusListener( ON_FOCUS_LOST );
        this.edTags.addFocusListener( ON_FOCUS_LOST );
        this.editor.addFocusListener( ON_FOCUS_LOST );
    }
    
    private JTitlePanel titlePanel;
    private JTitlePanel tagsPanel;
    private JTextField edTitle;
    private JTextField edTags;
    private JEditorPane editor;
    private Font font;
}
