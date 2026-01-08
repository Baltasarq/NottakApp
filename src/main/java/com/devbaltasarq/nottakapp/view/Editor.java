// NottakApp (c) Baltasar 2025 MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.view;


import com.devbaltasarq.nottakapp.core.Note;
import com.devbaltasarq.nottakapp.core.NoteFormatConverter;
import com.devbaltasarq.nottakapp.core.converter.ParseException;

import javax.swing.JPanel;


/** The text editor.
  * @author baltasarq
  */
public class Editor {   
    public Editor()
    {
        this( new EditorView( null ) );
    }
    
    public Editor(EditorView view)
    {
        this.note = null;
        this.editorView = view;
        this.loadTextFromNote();
        this.editorView.onFocusLost( () -> this.saveTextToNote() );
    }
    
    /** @return the view of the editor. */
    public JPanel getView()
    {
        return this.editorView;
    }
    
    private void loadTextFromNote()
    {
        if ( this.note != null ) {
            this.editorView.setNoteStatus();
            this.editorView.setTitle( this.note.getTitle() );
            this.editorView.setTags( this.note.getTags() );
            this.editorView.getEditor().setText( this.getNote().get() );
        } else {
            this.editorView.setNoNoteStatus();
        }
    }
    
    private String mdFromHtml()
    {
        final String DOC = this.editorView.getEditor().getText();
        String toret = "";
        
        try {
            final var CONVERTER = NoteFormatConverter.fromHtml( DOC );
            toret = CONVERTER.toMDText();
        } catch(ParseException exc)
        {
            toret = "ERROR converting from HTML: " + exc.getMessage();
        }
        
        return toret;
    }
    
    public void saveTextToNote()
    {
        if ( this.note != null ) {
            this.note.replaceTitle( this.editorView.getTitle() );
            this.note.getTags().replaceWith( this.editorView.getTags() );
            this.note.replace( this.mdFromHtml() );
        } else {
            this.loadTextFromNote();
        }
    }
    
    /** Change the text in the editor.
      * @param note the note to show in the editor.
      */
    public void setNote(Note note)
    {
        this.saveTextToNote();
        this.note = note;
        this.loadTextFromNote();
    }
    
    /** @return the note in the editor, or null if none is shown. */
    public Note getNote()
    {
        return this.note;
    }
    
    
    private Note note;
    private final EditorView editorView;
}
