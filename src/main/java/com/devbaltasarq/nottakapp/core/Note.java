// NottakApp (c) Baltasar 2025 MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;


/** Represents a simple note.
  * @author baltasarq
  */
public class Note {
    public static final String FILE_EXT = ".md";
    
    /** Creates an empty note.
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
        this( new Id(), title, null, initialText );
    }
    
    /** Creates a note with all its components.
      * @param id the id of the note.
      * @param title the title of the note.
      * @param text the whole text for the note.
      */
    Note(Id id, String title, TagSet tags, String text)
    {
        this.id = id;
        this.title = title;
        this.text = text;
        
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
    
    /** @return the id as an string. */
    public String getIdAsString()
    {
        return this.id.toString().replaceAll( "-", "" );
    }
    
    /** @return the file name for this note. */
    public String getFileName()
    {
        return this.getIdAsString() + FILE_EXT;
    }
       
    /** @return the title of this note. */
    public String getTitle()
    {
        return this.title;
    }
    
    /** Changes the title of this note.
      * @param newTitle the text for the new title.
      */
    public void replaceTitle(String newTitle)
    {
        this.title = newTitle;
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
    }
    
    /** Appends text to the note.
      * @param newText the new contents at the end of the note.
      */
    public void append(String newText)
    {
        this.text += newText;
    }
    
    /** @return the name of the file.
      * @param notesDir the directory for the notes.
      */
    public String buildFileName(String notesDir)
    {
        String toret =  this.getId().toString().toLowerCase();
        
        toret += FILE_EXT;
        return new File( notesDir, toret ).getAbsolutePath();
    }
    
    /** Saves the note to secondary memory
      * @param path the path to the notes dir.
      * @throws IOException if saving goes wrong.
      */
    public void save(String path) throws IOException
    {
        String noteText =
                "# " + this.getTitle()
                + "\n" + this.getTags().toString()
                + "\n" + this.get();
        
        Files.writeString( Path.of( this.buildFileName( path ) ),
                            noteText,
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING,
                            StandardOpenOption.WRITE );
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
    
    @Override
    public String toString()
    {
        return this.getTitle();
    }
    
    // This does not belong here.
    // Missing the path.
    public static String createPathFor(Note note)
    {
        return note.getFileName();
    }
    
    private final Id id;
    private final TagSet tags;
    private String title;
    private String text;
}
