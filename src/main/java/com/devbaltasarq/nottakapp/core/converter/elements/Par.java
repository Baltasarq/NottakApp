// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.elements;


import com.devbaltasarq.nottakapp.core.converter.Element;
import com.devbaltasarq.nottakapp.core.converter.ElementDto;
import java.util.ArrayList;
import java.util.List;


/** Paragraph
  * @author baltasarq
  */
public class Par extends Element {
    public final static String NAME = "p";
    
    public Par(final ElementDto ELTO)
    {
        this( ELTO, new ArrayList<>() );
    }
    
    public Par(final ElementDto ELTO, List<Element> subElements)
    {
        super( ELTO, subElements );
    }
    
    @Override
    public String toString()
    {
        return String.format( "Par %s", super.toString() );
    }
}
