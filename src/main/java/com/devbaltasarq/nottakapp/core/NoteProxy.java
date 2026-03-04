// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core;


import java.io.File;
import java.util.Scanner;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


/** A proxy for any note.
  * The purpose is to avoid loading a note
  * before it is needed.
  * @author baltasarq
  */
public final class NoteProxy {
    private static final Logger LOG = Logger.getLogger( NoteProxy.class.getName() );
    public static final String FILE_EXT = ".md";
    public static final boolean MANDATORY_SAVE = true;
    
    private NoteProxy(
                Notebook notebook,
                Id id,
                long fileChangedTime,
                String title,
                Date creation,
                Date modification,
                TagSet tags)
    {
        this.id = id;
        this.notebook = notebook;
        this.fileChangedTime = fileChangedTime;
        this.title = title;
        this.tags = tags;
        this.creationDate = creation;
        this.modificationDate = modification;
        this.note = null;
    }
    
    private NoteProxy(Notebook notebook, Note note)
    {
        this.id = note.getId();
        this.fileChangedTime = System.currentTimeMillis();
        this.notebook = notebook;
        this.note = note;
        this.title = note.getTitle();
        this.tags = note.getTags();
        this.creationDate = note.getCreationDate();
        this.modificationDate = note.getModificationDate();
    }
    
    /** @return the path to the note. */
    public String getPath()
    {
        return this.buildPath();
    }
    
    /** @return the id from the file name. */
    public Id getId()
    {
        if ( this.id == null ) {
            this.id = new Id();
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
    
    /** @return the last time the note's file was changed. */
    public long getFileChangedTime()
    {
        return this.fileChangedTime;
    }
    
    /** @return the creation date. */
    public Date getCreationDate()
    {
        return this.creationDate;
    }
    
    /** @return the modification date. */
    public Date getModificationDate()
    {
        Date toret = this.modificationDate;
        
        if ( this.note != null ) {
            toret = this.note.getModificationDate();
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
        if ( this.note == null ) {
            try (final var FINPUT = new FileInputStream( this.getPath() ) ) {
                this.note = Note.retrieveFrom( this.getId(), FINPUT );
            } catch(NoSuchElementException | FileNotFoundException exc)
            {
                LOG.log( Level.WARNING, "no data found in note" );
            } catch(IOException exc) {
                LOG.log( Level.WARNING, "unable to load: " + exc );
            }
        }
        
        assert this.note != null: "INTERNAL: note is still null!!";
        return this.note;
    }
    
    /** @return the name of the file. */
    public String buildPath()
    {
        String toret =  this.getId().toString() + FILE_EXT;

        return new File( this.notebook.getPath(), toret ).getAbsolutePath();
    }
    
    /** Saves the note,  provided the note has been loaded and it is dirty.
      * Otherwise, it is ignored.
      * @return true if the note needs updating, false otherwise.
      * @throws IOException if writing goes wrong.
      */
    public boolean save() throws IOException
    {
        return this.save( this.notebook.getPath() );
    }
    
    /** Saves the note, provided the note has been loaded and it is dirty.
      * Otherwise, it is ignored.
      * @return true if the note needs updating, false otherwise.
      * @param pathToNotesDir the path for notes.
      * @throws IOException if writing goes wrong.
     */
    public boolean save(String pathToNotesDir) throws IOException
    {
        return this.save( pathToNotesDir, false );
    }
    
    /** Saves the note, provided the note has been loaded nd it is dirty.
      * Otherwise, it is ignored.
      * @return true if the note needs updating, false otherwise.
      * @param pathToNotesDir the path for notes.
      * @param ignoreDirty ignore the dirty mark and save anyway.
      * @throws IOException if writing goes wrong.
     */
    public boolean save(String pathToNotesDir, boolean ignoreDirty) throws IOException
    {
        boolean toret = false;
        boolean needsSave = ( ( this.note != null && this.note.isDirty() ) 
                              || ignoreDirty );
        
        if ( this.note != null ) {
            final String PATH = this.buildPath();
            long currentFileChangedTime = new File( PATH ).lastModified();
            
            // Chk file on disk
            if ( this.fileChangedTime < currentFileChangedTime ) {
                // The note has changed on disk
                try (final var FILE_IN = new FileInputStream( PATH ) ) {
                    final var DISK_NOTE =
                                NoteDto.retrieveFrom( this.getId(), FILE_IN );
                    final var THIS_NOTE = NoteDto.from( this.getNote() );
                    final var NEW_NOTE = NoteDto.merge( THIS_NOTE, DISK_NOTE );
                    this.note = NEW_NOTE.toNote();
                    toret = true;
                } catch(IOException | IllegalArgumentException exc) {
                    // The note on disk was changed but it is corrupted.
                    LOG.warning( "note on disk more updated but corrupted" );
                }
            }
            
            // Normal save
            if ( needsSave ) {
                try (final var FILE_OUT = new FileOutputStream( PATH )) {
                    this.note.save( FILE_OUT, needsSave );
                    this.fileChangedTime = new File( PATH ).lastModified();
                } catch(IOException exc) {
                    String errorMsg = "unable to save note: "
                                            + this.getId()
                                            + "\n" + exc.getMessage();
                    LOG.severe( errorMsg );
                    throw new IOException( errorMsg );
                }
            }
        }
        
        return toret;
    }
    
    /** Delete the note. */
    public void delete()
    {
        final Path PATH = Path.of( this.getPath() );
        try {
            if ( !Files.deleteIfExists( PATH ) ) {
                LOG.warning(
                    String.format( "unable to delete note at: '%s'", PATH ));
            }
        } catch(IOException exc) {
                LOG.warning(
                    String.format( "I/O deleting note at: '%s'", PATH ));
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
    
    /** Loads only part of a note, i.e., title, dates and tags.
      * @param notebook the notebook this proxy pertains to.
      * @param path the path to the note.
      * @return a new NoteProxy object.
      */
    public static NoteProxy semiLoad(Notebook notebook, String path)
    {
        NoteDto noteDto = new NoteDto();
        long fileChangedTime = new File( path ).lastModified();
        final Id ID = Id.from( Path.of( path ) );
        
        try (final var SCANNER = new Scanner( new FileInputStream( path ) ))
        {
            noteDto = NoteDto.metaDatafromScanner( SCANNER );
        } catch(IllegalArgumentException exc)
        {
            LOG.log( Level.SEVERE, "illegal format: " + exc );
        }
        catch(NoSuchElementException | FileNotFoundException exc)
        {
            LOG.log( Level.WARNING, "no data found in note" );
        }
        
        return new NoteProxy(
                        notebook,
                        ID,
                        fileChangedTime,
                        noteDto.title(),
                        noteDto.dateCreation(),
                        noteDto.dateModification(),
                        noteDto.tags() );
    }
    
    /** Creates a NoteProxy from an existing note.
      * @param notebook the notebook this proxy pertains to.
      * @param note the note to create the proxy from.
      * @return a new proxy.
      */
    public static NoteProxy fromNote(Notebook notebook, Note note)
    {
        return new NoteProxy( notebook, note );
    }
    
    private Id id;
    private long fileChangedTime;
    private final Notebook notebook;
    private final String title;
    private final TagSet tags;
    private final Date creationDate;
    private final Date modificationDate;
    private Note note;
}
