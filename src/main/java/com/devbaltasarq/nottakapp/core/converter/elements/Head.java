// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.elements;


import com.devbaltasarq.nottakapp.core.converter.Element;
import java.util.ArrayList;
import java.util.List;


/** A heading.
  * @author baltasarq
  */
public class Head extends Element {
    public static final String TAG_DESC = "h";
    
    public Head(int level, String text)
    {
        this( level, text, new ArrayList<>() );
    }
    
    public Head(int level, String text, List<Element> subElements)
    {
        super( TAG_DESC, text, subElements );
        this.level = level;
    }
    
    /** @return the heading level. */
    public int getLevel()
    {
        return this.level;
    }
    
    @Override
    public String toString()
    {
        return String.format( "Heading %s", super.toString() );
    }
    
    private int level;
}
