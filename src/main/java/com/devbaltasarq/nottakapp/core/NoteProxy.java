// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core;


import java.util.Scanner;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


/** A proxy for any note.
  * The purpose is to avoid loading a note
  * before it is needed.
  * @author baltasarq
  */
public final class NoteProxy {
    private static final Logger LOG = Logger.getLogger( NoteProxy.class.getName() );
    
    private NoteProxy(String path, String title, TagSet tags)
    {
        this.id = null;
        this.path = path;
        this.title = title;
        this.tags = tags;
        this.note = null;
    }
    
    private NoteProxy(Note note)
    {
        this.id = note.getId();
        this.path = "";
        this.note = note;
        this.title = note.getTitle();
        this.tags = note.getTags();
    }
    
    /** @return the path to the note. */
    public String getPath()
    {
        String toret = this.path;
        
        if ( toret.isBlank() ) {
            toret = this.note.getFileName();
        }
        
        return toret;
    }
    
    /** @return the id from the file name. */
    public Id getId()
    {
        if ( this.id == null ) {
            this.id = Id.from( Path.of( this.getPath() ) );
        }
        
        return this.id;
    }
    
    /** @return the title of the note. */
    public String getTitle()
    {
        String toret = this.title;
        
        if ( this.note != null ) {
            toret = this.note.getTitle();
        }
        
        return toret;
    }
    
    /** @return the tags of the note. */
    public TagSet getTags()
    {
        TagSet toret = this.tags;
        
        if ( this.note != null ) {
            toret = this.note.getTags();
        }
        
        return toret;
    }
    
    /** @return the real note. */
    public Note getNote()
    {
        return this.retrieve();
    }
    
    /** @return the note, loaded from storage, if needed. */
    private Note retrieve()
    {   
        String text = "";
        
        if ( this.note == null ) {
            try (final var SCAN = new Scanner( new FileInputStream( this.path ))) {
                SCAN.nextLine();                            // Ignore the title.
                SCAN.nextLine();                            // Ignore the tags.

                
                while ( SCAN.hasNext() ) {
                    text += SCAN.nextLine();
                }
            } catch(NoSuchElementException | FileNotFoundException exc) {
                LOG.log( Level.WARNING, "no data found in note" );
            }
            
            this.note = new Note(   this.getId(),
                                    this.getTitle(),
                                    this.getTags(),
                                    text );
        }
        
        return this.note;
    }
    
    public void save(String pathToNotesDir) throws IOException
    {
        if ( this.note != null ) {
            try {
                this.note.save( pathToNotesDir );
            } catch(IOException exc) {
                String errorMsg = "unable to save note: "
                                        + this.getId()
                                        + "\n" + exc.getMessage();
                LOG.severe( errorMsg );
                throw new IOException( errorMsg );
            }
        }
    }
    
    public void delete()
    {
        try {
            if ( !Files.deleteIfExists( Path.of( this.path ) ) ) {
                LOG.warning(
                        String.format( "unable to delete note at: '%s'",
                                          this.path ));
            }
        } catch(IOException exc) {
                LOG.warning(
                        String.format( "I/O deleting note at: '%s'",
                                          this.path ));
        }
    }
        
    @Override
    public int hashCode()
    {
        return this.getId().hashCode();
    }
    
    @Override
    public boolean equals(Object other)
    {
        boolean toret = false;
        
        if ( other instanceof NoteProxy otherProxy ) {
            toret = this.getId() == otherProxy.getId();
        }
        
        return toret;
    }
    
    @Override
    public String toString()
    {
        return this.getTitle();
    }
    
    /** Loads the relevant part of a note.
      * @param path the path to the note.
      * @return a new NoteProxy object.
      */
    public static NoteProxy load(String path)
    {
        TagSet tags = new TagSet();
        String title = "";
        
        
        try (final var SCAN = new Scanner( new FileInputStream( path ) )) {
            title = cleanTitle( SCAN.nextLine() );
            tags.addAllFromString( SCAN.nextLine() );
        } catch(NoSuchElementException | FileNotFoundException exc) {
            LOG.log( Level.WARNING, "no data found in note" );
        }

        return new NoteProxy( path, title, tags );
    }
    
    /** Remove the MD title prefix: "#" for title.
      * @param title the title, as in "# Title"
      * @return the given title, as in "Title"
      */
    private static String cleanTitle(String title)
    {
        if ( title == null ) {
            title = "";
        }
        
        title = title.trim();
        
        if ( !title.isEmpty() ) {
            if ( title.charAt( 0 ) == '#' ) {
                title = title.substring( 1 ).trim();
            }
        }
        
        return title;
    }
    
    /** Creates a NoteProxy from an existing note.
      * @param note the note to create the proxy from.
      * @return a new proxy.
      */
    public static NoteProxy fromNote(Note note)
    {
        return new NoteProxy( note );
    }
    
    private Id id;
    private final String path;
    private final String title;
    private final TagSet tags;
    private Note note;
}
