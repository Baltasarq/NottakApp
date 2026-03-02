// NottakApp (c) Baltasar 2025 MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.view;


import java.awt.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.View;
import javax.swing.text.html.FormView;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import com.devbaltasarq.nottakapp.core.Note;
import com.devbaltasarq.nottakapp.core.NoteFormatConverter;
import com.devbaltasarq.nottakapp.core.NoteProxy;
import com.devbaltasarq.nottakapp.core.converter.DOMRunner;
import com.devbaltasarq.nottakapp.core.converter.ParseException;
import com.devbaltasarq.nottakapp.core.converter.Element;
import com.devbaltasarq.nottakapp.core.converter.elements.Chk;
import com.devbaltasarq.nottakapp.core.converter.elements.Root;
import java.util.logging.Logger;


/** The text editor.
  * @author baltasarq
  */
public class Editor {
    private static final Logger LOG = Logger.getLogger( MainWindow.class.getName() );
    
    public Editor()
    {
        this( new EditorView( null ) );
    }
    
    public Editor(EditorView view)
    {
        this.noteProxy = null;
        this.dirty = false;
        this.editorView = view;
        this.loadTextFromNote();
        this.editorView.onFocusLost( () -> this.saveTextToNote() );
        this.editorView.onContentsChanged( () -> this.onContentsChanged() );
    }
    
    /** @return the view of the editor. */
    public EditorView getView()
    {
        return this.editorView;
    }
    
    /** @return the editor in the view. */
    public JEditorPane getEditor()
    {
        return this.getView().getEditor();
    }
    
    /** @return whether the note has been modified or not. */
    public boolean isDirty()
    {
        return this.dirty;
    }
    
    /** Replaces the contents of the editor with the given HTML.
      * @param newContents a String with new HTML.
      */
    public void replaceHtmlWith(String newContents)
    {
        final var ED = this.getEditor();
        final var KIT = (HTMLEditorKit) ED.getEditorKit();
        final var DOC = (HTMLDocument) ED.getDocument();
        
        ED.setText( "" );
        
        try {
            // Insert at the end of document
            KIT.insertHTML(
                            DOC,
                            0,
                            newContents,
                            0, 0, null );

        } catch (BadLocationException | IOException e) {
            ED.setText( "ERROR: " + e.getMessage() );
        }
    }
    
    private static String htmlFromMD(String text)
    {
        String toret = "";
        
        try {
            final var CONVERTER = NoteFormatConverter.fromMD( text );
            toret = CONVERTER.toHtml();
        } catch(ParseException exc)
        {
            toret = "ERROR converting from MD: " + exc.getMessage();
        }
        
        return toret;
    }
    
    private void loadTextFromNote()
    {
        if ( this.noteProxy != null ) {
            final Note NOTE = this.noteProxy.getNote();
            
            this.getView().setNoteStatus();
            this.getView().setTitle( NOTE.getTitle() );
            this.getView().setTags( NOTE.getTags() );
            this.getView().setDates(
                                NOTE.getCreationDate(),
                                NOTE.getModificationDate() );
            this.replaceHtmlWith( htmlFromMD( NOTE.get() ) );
            this.dirty = false;
        } else {
            this.getView().setNoNoteStatus();
        }
    }
    
    public void saveTextToNote()
    {
        if ( this.noteProxy != null ) {
            if ( this.isDirty() ) {
                final Map<Integer, Boolean> CHK_VALUES =
                        CheckBoxValuesExtraction
                                .extractCheckboxStates( this.getEditor() );
                final Note NOTE = this.noteProxy.getNote();
                final String CONTENTS = this.getEditor().getText();

                NOTE.replaceTitle( this.getView().getTitle() );
                NOTE.getTags().replaceWith( this.getView().getTags() );
                NOTE.replace( mdFromHtml( CONTENTS, CHK_VALUES ) );
                
                try {
                    if ( this.noteProxy.save() ) {
                        this.loadTextFromNote();
                    }
                    
                    this.dirty = false;
                } catch(IOException exc) {
                    LOG.warning( "saving note: " + exc.getMessage() );
                }
            }
        } else {
            this.loadTextFromNote();
        }
    }
    
    private static String mdFromHtml(
            final String TEXT,
            final Map<Integer, Boolean> CHK_VALUES)
    {
        String toret = "";
        
        try {
            final var CONVERTER = NoteFormatConverter.fromHtml( TEXT );
            
            if ( !CHK_VALUES.isEmpty() ) {
                final var CHK_ASSIGN = new CheckBoxValuesAssigner(
                                                    CONVERTER.getRoot(),
                                                        CHK_VALUES );

                CHK_ASSIGN.run();
            }
            
            toret = CONVERTER.toMDText();
        } catch(ParseException exc)
        {
            toret = "ERROR converting from HTML: " + exc.getMessage();
        }
        
        return toret;
    }
    
    /** Change the text in the editor.
      * @param note the note to show in the editor.
      */
    public void setNote(NoteProxy note)
    {
        this.saveTextToNote();
        this.noteProxy = note;
        this.loadTextFromNote();
    }
    
    /** @return the note in the editor, or null if none is shown. */
    public NoteProxy getNote()
    {
        return this.noteProxy;
    }
    
    /** Called when the note has been changed. */
    private void onContentsChanged()
    {
        this.dirty = true;
    }
    
    private NoteProxy noteProxy;
    private boolean dirty;
    private final EditorView editorView;
    
    private static class CheckBoxValuesExtraction {
        /** This should not be needed... but it is.
          * The authors of SWING, when writing JEditorText.getText(), "connected"
          * the value of the TextFields of the view with the `<input type="text">`
          * tags, while they didn't for CheckBoxes (nor probably for RadioButtons).
          * That's while I have to fish for the real values of the CheckBoxes.
          * Don't get me started about this. One must be really lazy
          * to do the connection for text fields and "forgot"
          * about check boxes and radio buttons. Come on...
          * Don't forget to call this before calling JTextEditor::getText.
          * @param editor the editor pane to analyze.
          * @return a mapping between checkbox tag names and their actual values.
          */
        public static Map<Integer, Boolean> extractCheckboxStates(JEditorPane editor)
        {
            Map<Integer, Boolean> states = new HashMap<>();

            // Get the root view
            View rootView = editor.getUI().getRootView( editor );

            // Traverse the view hierarchy to find checkbox components
            numChk = 0;
            traverseViews( rootView, states );

            return states;
        }

        /** Traverses all visual components of the JEditorPane's HtmlDocument,
          * looking for those that are inputs (FormView class), and then trying to
          * cast to JCheckBox, which means we are in the right track.
          * @param VIEW the view to verify at this moment.
          * @param CHK_STATES the map storing the name of the input and its state.
          */
        private static void traverseViews(
                            final View VIEW,
                            final Map<Integer, Boolean> CHK_STATES)
        {
            if ( VIEW instanceof final FormView formView) {
                // The associated AWT widget
                Component component = formView.getComponent();
                if ( component instanceof final JCheckBox CHK ) {
                    CHK_STATES.put( ++numChk, CHK.isSelected() );
                }
            }

            // Recursively traverse child views
            for (int i = 0; i < VIEW.getViewCount(); i++) {
                traverseViews( VIEW.getView( i ), CHK_STATES );
            }
        }
        
        private static int numChk;
    }
    
    private static class CheckBoxValuesAssigner extends DOMRunner {
        public CheckBoxValuesAssigner(
                Root ROOT,
                final Map<Integer, Boolean> CHK_VALUES)
        {
            super( ROOT );
            this.setVisitor( e -> this.assignValues( e ) );
            this.numChks = 0;
            this.chkValues = CHK_VALUES;
        }
        
        private void assignValues(Element e)
        {
            if ( e instanceof final Chk CHK
              && !CHK.isClosing() )
            {
                ++numChks;
                
                Boolean value = this.chkValues.get( numChks );
                
                if ( value != null ) {
                    CHK.setActivated( value );
                }
            }
        }
        
        private int numChks;
        private final Map<Integer, Boolean> chkValues;
    }
}
