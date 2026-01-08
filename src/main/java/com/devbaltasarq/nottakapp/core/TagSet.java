// NottakApp (c) Baltasar 2025 MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core;


import com.devbaltasarq.nottakapp.view.MainWindow;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;


/** A collection of tags.
  * @author baltasarq
  */
public class TagSet {
    public static final String DELIMITER = ",";
    private static final Logger LOG = Logger.getLogger( MainWindow.class.getName() );
    
    public TagSet()
    {
        this.tags = new HashSet();
    }
    
    /** Adds a new tag, unless it is already in the set.
      * @param tag the new tag to add.
      */
    public void add(Tag tag)
    {
        this.tags.add( tag );
    }
    
    /** Adds all tags present in the string.
      * @param strTags a comma-separated string with tags.
      */
    public void addAllFromString(String strTags)
    {
        String[] strTagList = strTags.trim().split( DELIMITER );
        
        if ( strTagList.length != 1
          || !strTagList[ 0 ].isBlank() )
        {
            for(String strTag: strTagList) {
                try {
                    this.add( new Tag( strTag ) );
                } catch(IllegalArgumentException exc) {
                    LOG.log( Level.SEVERE, exc.getMessage() );
                }
            }
        }
    }
    
    /** Replace all the tags with the tags in ts,
      * @param ts a tag set with tags which will be copied here.
      */
    public void replaceWith(TagSet ts)
    {
        this.clear();
        
        for(Tag tag: ts.getAll()) {
            this.add( tag );
        }
    }
    
    /** @return all the tags contained. */
    public List<Tag> getAll()
    {
        return new ArrayList<>( this.tags );
    }
    
    /** Remove all tags. */
    public void clear()
    {
        this.tags.clear();
    }
    
    /** @return the number of tags. */
    public int count()
    {
        return this.tags.size();
    }
    
    /** @return true if the given tag is already in the set, false otherwise. */
    public boolean contains(Tag t)
    {
        return this.tags.contains( t );
    }
    
    @Override
    public String toString()
    {
        return String.join( ", ",
                            this.getAll().stream()
                                            .map( (t) -> t.get() ).toList() );
    }
    
    private final Set<Tag> tags;
}
