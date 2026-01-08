// NottakApp (c) Baltasar 2025 MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core;


/** This represents a tag for a note.
  * @author baltasarq
  */
public class Tag {
    /** Creates a new tag from the given tag string.
      * @param tag the given tag. This may or may not be the exact final tag,
      *            since the following transformations are carried out:
      *            - tag is trimmed and capitalized,
      *            - commas are eliminated.
      * @throws IllegalArgumentException if the tag is empty.
      */
    public Tag(String tag) throws IllegalArgumentException
    {
        tag = format( tag );
        
        if ( tag.isEmpty() ) {
            throw new Illegal­Argument­Exception( "Tag: cannot create empty tags" );
        }
        
        this.tag = tag;
    }
    
    /** Formats a string so it can be a suitable tag.
      * @param strTag the tag-to-be string.
      * @return the formatted tag, as a string.
      */
    private String format(String strTag)
    {
        strTag = strTag.trim().toLowerCase();
        
        if ( !strTag.isEmpty() ) {
            strTag = strTag.substring( 0, 1 ).toUpperCase()
                       + strTag.substring( 1 );
            strTag = strTag.replaceAll( ",", "" );
        }
        
        return strTag;
    }
    
    /** @return the stored tag. */
    public String get()
    {
        return this.tag;
    }
    
    @Override
    public int hashCode()
    {
        return this.tag.hashCode();
    }
    
    @Override
    public boolean equals(Object other)
    {
        boolean toret = false;
        
        if ( other instanceof Tag otherTag ) {
            toret = this.get().equals( otherTag.get() );
        }
        
        return toret;
    }
    
    @Override
    public String toString()
    {
        return this.get();
    }
    
    private final String tag;
}
