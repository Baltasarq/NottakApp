// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.elements;


import com.devbaltasarq.nottakapp.core.converter.Element;
import com.devbaltasarq.nottakapp.core.converter.ElementDto;

import java.util.ArrayList;
import java.util.List;


/** Reference to another note.
  * @author baltasarq
  */
public class Ref extends Link {
    public final static String NAME = "wref:a";
    
    public Ref(final ElementDto ELTO)
    {
        this( ELTO, new ArrayList<>() );
    }
    
    public Ref(final ElementDto ELTO, List<Element> subElements)
    {
        super( ELTO, subElements );
    }
    
    @Override
    public String toString()
    {
        return String.format( "Wiki%s", super.toString() );
    }
}
