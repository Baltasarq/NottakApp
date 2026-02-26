// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.elements;


import com.devbaltasarq.nottakapp.core.converter.Element;
import com.devbaltasarq.nottakapp.core.converter.ElementDto;

import java.util.ArrayList;
import java.util.List;


/** A heading.
  * @author baltasarq
  */
public class Head extends Element {
    public static final String NAME = "h";
    public static final String ETQ_LEVEL = "lvl";
    
    public Head(final ElementDto ELTO)
    {
        this( ELTO, new ArrayList<>() );
    }
    
    public Head(final ElementDto ELTO, List<Element> subElements)
    {
        super( ELTO, subElements );
    }
    
    @Override
    public String getName()
    {
        return NAME + ( (char) ( '0' + this.getLevel() ) );
    }
    
    /** @return the heading level. */
    public int getLevel()
    {
        String lvl = this.getAttributes().getOrDefault( ETQ_LEVEL, "2" );
        int toret;
        
        try {
            toret = Integer.parseInt( lvl.trim() );
        } catch(NumberFormatException exc) {
            toret = 2;
        }
        
        return toret;
    }
    
    @Override
    public String toString()
    {
        return String.format( "Heading (%d) %s",
                                this.getLevel(),
                                super.toString() );
    }
}
