// NottakApp (c) Baltasar 2025 MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core;


import java.io.File;
import java.io.IOException;
import java.io.FileFilter;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
    
    /** Saves all the notes in the notebook. */
    public void saveAll()
    {
        this.saveAll( this.getPath() );
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
        final File DIR = new File( path );
        final FileFilter FF = 
                    (file) -> file.getName().endsWith( NoteProxy.FILE_EXT );
        
        // Chk the actual directory
        assert DIR.isDirectory(): "Notebook.restoreFrom(): notes path is not a directory";
        
        for (final File fileEntry : DIR.listFiles( FF )) {
            try {
                TORET.add( NoteProxy.semiLoad(
                                            TORET,
                                            fileEntry.getCanonicalPath()) );
            } catch(IOException exc) {
                LOG.warning( "error loading: " + fileEntry );
            }
        }
        
        return TORET;
    }
    
    /** Moves all notes to a new path.
      * @param newPath
      * @param notebook
      * @throws IOException if writing goes wrong.
      */
    public static void moveTo(String newPath, Notebook notebook) throws IOException
    {
        final String FILE_FILTER = "*{" + NoteProxy.FILE_EXT + "}";
        final Path OLD_PATH = Path.of( notebook.getPath() );
        
        if ( !OLD_PATH.toString().equals( newPath ) ) {
            for(NoteProxy proxy: notebook.notesIndexed.values()) {
                proxy.save( OLD_PATH.toString(), NoteProxy.MANDATORY_SAVE );
            }
            
            Files.createDirectories( Path.of( newPath ) );
            
            try (var stream = Files.newDirectoryStream( OLD_PATH, FILE_FILTER ))
            {
                for (Path noteFile: stream) {
                    String fileName = noteFile.getFileName().toString();
                    Files.move(
                            noteFile,
                            Path.of( newPath , fileName ),
                            StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.ATOMIC_MOVE );
                }
            } catch (DirectoryIteratorException exc) {
                throw new IOException( exc.getMessage() );
            }
            
            Files.deleteIfExists( OLD_PATH );
        }
    }
    
    private final Map<Id, NoteProxy> notesIndexed;
    private final String path;
}
