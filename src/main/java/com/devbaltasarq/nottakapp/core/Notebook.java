// NottakApp (c) Baltasar 2025 MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core;


import java.io.File;
import java.io.IOException;
import java.io.FileFilter;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;


/** Represents the collection of notes in secondary memory.
  * @author baltasarq
  */
public final class Notebook {
    private static final Logger LOG = Logger.getLogger( Notebook.class.getName() );
    private Notebook(String path)
    {
        this.path = path;
        this.notesIndexed = new HashMap<>();
    }
    
    /** Adds a new note.
      * @param note the note object to add.
      */
    public void add(NoteProxy note)
    {
        this.notesIndexed.put( note.getId(), note );
    }
    
    /** Removes a given note.
      * @param note the note to remove.
     */
    public void delete(NoteProxy note)
    {
        this.notesIndexed.remove( note.getId(), note );
        note.delete();
    }
    
    /** @return a list containing all the notes. */
    public List<NoteProxy> getAllNotes()
    {
        return new ArrayList<>( this.notesIndexed.values() );
    }
    
    /** @return a set containing the ids for all notes. */
    public Set<Id> getAllIds()
    {
        return this.notesIndexed.keySet();
    }
    
    /** Gets the corresponding proxy for the id.
      * @param id the id for the note.
      * @return the note for the given id, or null.
      */
    public NoteProxy lookUp(Id id)
    {
        return this.notesIndexed.get( id );
    }
    
    /** @return the path to the directory in which the note files live. */
    public String getPath()
    {
        return this.path;
    }
    
    /** Saves all the notes in the notebook.
      * @param pathToNotesDir the directory in which to save notes.
      */
    public void saveAll(String pathToNotesDir)
    {
        for(NoteProxy noteProxy: this.notesIndexed.values()) {
            try {
                noteProxy.save( pathToNotesDir );
            } catch(IOException exc) {
                LOG.warning( String.format( "[IO][ERR] skipping: %s",
                                                noteProxy.getId() ) );
            }
        }
    }
    
    public static Notebook restoreFrom(String path)
    {
        final var TORET = new Notebook( path );
        final FileFilter FF = (file) -> file.getName().endsWith( Note.FILE_EXT );
        final File DIR = new File( path );
        
        // Get the actual directory, if needed
        if ( !DIR.isDirectory() ) {
            LOG.severe( "Notebook.restoreFrom(): notes path is not a directory" );
        } else {
            for (final File fileEntry : DIR.listFiles( FF )) {
                try {
                    TORET.add( NoteProxy.load( fileEntry.getCanonicalPath() ) );
                } catch(IOException exc) {
                    LOG.warning( "error loading: " + fileEntry );
                }
            }
        }
        
        return TORET;
    }
    
    private final Map<Id, NoteProxy> notesIndexed;
    private final String path;
}
