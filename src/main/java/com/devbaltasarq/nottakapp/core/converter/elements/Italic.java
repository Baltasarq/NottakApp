// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.elements;


import com.devbaltasarq.nottakapp.core.converter.Element;
import com.devbaltasarq.nottakapp.core.converter.ElementDto;

import java.util.ArrayList;
import java.util.List;


/** Italic effect.
  * @author baltasarq
  */
public class Italic extends Element {
    public final static String NAME = "i";
    
    public Italic(final ElementDto ELTO)
    {
        this( ELTO, new ArrayList<>() );
    }
    
    public Italic(final ElementDto ELTO, List<Element> subElements)
    {
        super( ELTO, subElements );
    }
    
    @Override
    public String toString()
    {
        return String.format( "Italic %s", super.toString() );
    }
}
