// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.elements;


import com.devbaltasarq.nottakapp.core.converter.Element;
import com.devbaltasarq.nottakapp.core.converter.ElementDto;
import java.util.ArrayList;
import java.util.List;


/** Bold effect.
  * @author baltasarq
  */
public class Entry extends Element {
    public final static String NAME = "li";
    
    public Entry(final ElementDto ELTO)
    {
        this( ELTO, new ArrayList<>() );
    }
    
    public Entry(final ElementDto ELTO, List<Element> subElements)
    {
        super( ELTO, subElements );
    }
    
    @Override
    public String toString()
    {
        return String.format( "Entry %s", super.toString() );
    }
}
