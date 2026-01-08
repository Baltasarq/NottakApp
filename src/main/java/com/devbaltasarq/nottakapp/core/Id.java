// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core;


import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.UUID;


/** An id for a note. It is just an UUID.
  * @author baltasarq
  */
public class Id {
    private final static int UUID_LENGTH = 8 + 4 + 4 + 4 + 12;
    
    public Id()
    {
        this( UUID.randomUUID() );
    }
    
    /** @creates a new id from a given UUID.
      * @param uuid the given uuid to store as an id.
      */
    public Id(UUID uuid)
    {
        this.id = uuid;
    }
    
    /** @return the uuid inside this id. */
    public UUID get()
    {
        return this.id;
    }
    
    @Override
    public String toString()
    {
        return this.id.toString().replaceAll( "-", "" ).trim();
    }
    
    public static Id from(String strUUID)
    {
        strUUID = strUUID.trim();
        
        if ( strUUID.length() < UUID_LENGTH ) {
                throw new NoSuchElementException(
                            "Id.from(): the given string is not an uuid: "
                            + strUUID );
        }

        strUUID = strUUID.substring( 0, 8 )
                + "-" + strUUID.substring( 8, 12 )
                + "-" + strUUID.substring( 12, 16 )
                + "-" + strUUID.substring( 16, 20 )
                + "-" + strUUID.substring( 20 );

        return new Id( UUID.fromString( strUUID ) );
    }
    
    public static Id from(Path path)
    {
        String strId = path.getFileName().toString();
        int pos = strId.lastIndexOf( '.' );

        if ( pos >= 0 ) {
            strId = strId.substring( 0, pos );            
        }
        
        return from( strId );
    }
    
    private UUID id;
}
