// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.elements;


import com.devbaltasarq.nottakapp.core.converter.Element;
import com.devbaltasarq.nottakapp.core.converter.ElementDto;

import java.util.ArrayList;
import java.util.List;


/** Just text.
  * @author baltasarq
  */
public class Text extends Element {
    public static final String NAME = "txt";

    public Text(final ElementDto ELTO)
    {
        this( ELTO, new ArrayList<>() );
    }
    
    public Text(final ElementDto ELTO, List<Element> subElements)
    {
        super( ELTO, subElements );
    }
    
    /** This element should not be closed. */
    @Override
    public boolean needsClosing()
    {
        return false;
    }
    
    @Override
    public String toString()
    {
        return String.format( "Text %s", super.toString() );
    }
}
