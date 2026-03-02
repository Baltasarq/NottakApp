// NottakApp (c) Baltasar 2025 MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;


/** Used to pass raw data while I/O.
  * @author baltasarq
  */
public record NoteDto(
                    Id id,
                    String title,
                    TagSet tags,
                    Date dateCreation,
                    Date dateModification,
                    String text)
{
    private static final String NOTE_TEXT_FORMAT =
                                        "# %s"                  // Title
                                        + "\n%s"                // Tags
                                        + "\n%s"                // Date created
                                        + "\n%s"                // Date edited
                                        + "\n%s";               // Text
        
    public NoteDto()
    {
        this(
            new Id(),
            "note",
            new TagSet(),
            Date.fromSystem(), 
            Date.fromSystem(), 
            "" );
    }
    
    /** Saves all the data in the note to the OutputStream.
      * @param OUT the output to write to.
      * @throws IOException if writing goes wrong.
      */
    public void save(final OutputStream OUT) throws IOException
    {
        OUT.write( this.toString().getBytes() );
    }
    
    /** @return a note with the data inside this. */
    public Note toNote()
    {
        return new Note(
                        this.id(),
                        this.dateCreation(),
                        this.dateModification(),
                        this.title(),
                        this.tags(),
                        this.text() );
    }
    
    /** @return the whole note as a string. */
    @Override
    public String toString()
    {
        return String.format(NOTE_TEXT_FORMAT,
                                    this.title(),
                                    this.tags().toString(),
                                    this.dateCreation().toString(),
                                    this.dateModification().toString(),
                                    this.text() );
    }
    
    /** Retrieves a note from an InputStream.
      * @param ID the id, previously extracted from the path of the note.
      * @param INPUT the InputStream to read from.
      * @return a new note, with the data retrieved.
      * @throws IllegalArgumentException if parsing dates goes wrong.
      */
    public static NoteDto retrieveFrom(
                            final Id ID,
                            final InputStream INPUT)
            throws IllegalArgumentException
    {
        final var SCANNER = new Scanner( INPUT );
        final NoteDto META_DATA_DTO = metaDatafromScanner( SCANNER );
        String text = "";
        
        // Read body
        while ( SCANNER.hasNext() ) {
            text += SCANNER.nextLine() + "\n";
        }

        // Create the note
        return new NoteDto(
                    ID,
                    META_DATA_DTO.title(),
                    META_DATA_DTO.tags(),
                    META_DATA_DTO.dateCreation(),
                    META_DATA_DTO.dateModification(),
                    text );
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
    
    public static NoteDto metaDatafromScanner(final Scanner SCANNER)
    {
        final TagSet TAGS = new TagSet();
        String title = cleanTitle( SCANNER.nextLine() );
        TAGS.addAllFrom( SCANNER.nextLine() );
        Date dateCreation = Date.fromString( SCANNER.nextLine() );
        Date dateModification = Date.fromString( SCANNER.nextLine() );
        
        return new NoteDto(
                        null,
                        title,
                        TAGS,
                        dateCreation,
                        dateModification,
                        "" );
    }
    
    public static NoteDto from(final Note NOTE)
    {
        return new NoteDto(
                        NOTE.getId(),
                        NOTE.getTitle(),
                        NOTE.getTags(),
                        NOTE.getCreationDate(),
                        NOTE.getModificationDate(),
                        NOTE.get() );
    }
    
    /** This is used when there's the need to merge two notes.
      * @param nd1 the first note data to merge.
      * @param nd2 the second note data to merge.
      * @return a resulting NoteDto with all data from both sources.
      */
    public static NoteDto merge(NoteDto nd1, NoteDto nd2)
    {
        Id id = nd1.id();
        final var TAGS = new TagSet();
        String title = nd1.title();
        Date created = nd1.dateCreation();
        Date modified = nd1.dateModification();
        String text = nd1.text().trim();
        
        // The resulting id
        if ( !id.equals( nd2.id() ) ) {
            id = new Id();
        }
        
        // The resulting title
        if ( !title.equals( nd2.title() ) ) {
            title += " // " + nd2.title();
        }
        
        // The resulting creation date
        if ( nd2.dateCreation().isLessThan( created ) ) {
            created = nd2.dateCreation();
        }
        
        // The resulting modification date
        if ( nd2.dateModification().isLessThan( modified ) ) {
            created = nd2.dateModification();
        }
        
        // The tags
        TAGS.addAllFrom( nd1.tags() );
        TAGS.addAllFrom( nd2.tags() );
        
        // The contents
        if ( !text.equals( nd2.text().trim() ) ) {
            NoteDto first = nd1;
            NoteDto second = nd2;
            
            if ( nd2.dateModification().isLessThan( nd1.dateModification() )) {
                first = nd2;
                second = nd1;
            }
            
            text = "**======= " + first.dateModification().toString()
                    + "**\n\n"
                    + first.text()
                    + "\n\n**======= " + second.dateModification().toString()
                    + "**\n\n"
                    + second.text();
        }
        
        return new NoteDto(
                        id,
                        title,
                        TAGS,
                        created,
                        modified,
                        text );
    }

}
