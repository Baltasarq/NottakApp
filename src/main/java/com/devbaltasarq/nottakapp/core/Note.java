// NottakApp (c) Baltasar 2025 MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/** Represents a simple note.
  * @author baltasarq
  */
public class Note {
    /** Creates an empty note with a title.
      * @param title the title for the note.
      */
    public Note(String title)
    {
        this( title, "" );
    }
    
    /** Creates a note with an initial text.
      * @param title the title for the note. 
      * @param initialText the initial contents for the note.
      */
    public Note(String title, String initialText)
    {
        this( new Id(),
                Date.fromSystem(),
                Date.fromSystem(),
                title,
                null,
                initialText );
    }
    
    /** Creates a note with all its components.
      * @param id the id of the note.
      * @param fileChangedTime the last time it was modified on storage.
      * @param creation the creation date of the note.
      * @param modification the modification date of the note.
      * @param title the title of the note.
      * @param tags the tags associated with the note.
      * @param text the whole text for the note.
      */
    Note(
            Id id,
            Date creation,
            Date modification,
            String title,
            TagSet tags,
            String text)
    {
        this.id = id;
        this.dirty = false;
        this.title = title;
        this.text = text;
        this.dateCreation = creation;
        this.dateModification = modification;
        
        if ( tags == null ) {
            this.tags = new TagSet();
        } else {
            this.tags = tags;
        }
    }
        
    /** @return the id of this note. */
    public Id getId()
    {
        return this.id;
    }
    
    /** @return the title of this note. */
    public String getTitle()
    {
        return this.title;
    }
        
    /** @return the creation date. */
    public Date getCreationDate()
    {
        return this.dateCreation;
    }
    
    /** @return the modification date. */
    public Date getModificationDate()
    {
        return this.dateModification;
    }
    
    /** Changes the title of this note.
      * @param newTitle the text for the new title.
      */
    public void replaceTitle(String newTitle)
    {
        this.title = newTitle;
        this.dirty = true;
    }
    
    /** @return the contents of the note. */
    public String get()
    {
        return this.text;
    }
    
    /** Changes the text in the note.
      * @param newText the new contents of the note.
      */
    public void replace(String newText)
    {
        this.text = newText;
        this.dirty = true;
    }
    
    /** Appends text to the note.
      * @param newText the new contents at the end of the note.
      */
    public void append(String newText)
    {
        this.text += newText;
        this.dirty = true;
    }
        
    @Override
    public int hashCode()
    {
        return this.getId().hashCode();
    }
    
    @Override
    public boolean equals(Object o)
    {
        boolean toret = false;
        
        if ( o instanceof Note n) {
            toret = this.getId().equals( n.getId() );
        }
        
        return toret;
    }
    
    /** @return the tags collection. */
    public TagSet getTags()
    {
        return this.tags;
    }
    
    /** @return true if it needs saving, false otherwise. */
    public boolean isDirty()
    {
        return ( this.dirty || this.tags.isDirty() );
    }
    
    /** Resets the dirty mark, signaling all changed have been saved. */
    public void resetDirty()
    {
        this.dirty = false;
        this.tags.resetDirty();
    }
    
    /** Writes the note to a stream.
      * @param OUT the stream to write the note to.
      * @param ignoreDirty ignore the dirty mark and save the note.
      * @throws IOException if writing goes wrong.
      */
    public void save(final OutputStream OUT, boolean ignoreDirty) 
            throws IOException
    {
        boolean needsSaving = ( this.isDirty() || ignoreDirty );
        
        if ( needsSaving ) {
            this.dateModification = Date.fromSystem();
            NoteDto.from( this ).save( OUT );
            this.resetDirty();
        }
    }
    
    /** @return the title of the note. */
    @Override
    public String toString()
    {
        return this.getTitle();
    }
    
    /** Retrieves a note from an InputStream.
      * @param ID the id, previously extracted from the path of the note.
      * @param INPUT the InputStream to read from.
      * @return a new note, with the data retrieved.
      * @throws IllegalArgumentException if parsing dates goes wrong.
      */
    public static Note retrieveFrom(
                            final Id ID,
                            final InputStream INPUT)
            throws IllegalArgumentException
    {
        return NoteDto.retrieveFrom( ID, INPUT ).toNote();
    }
    
    private final Id id;
    private String title;
    private final TagSet tags;
    private final Date dateCreation;
    private Date dateModification;
    private String text;
    private boolean dirty;
}
