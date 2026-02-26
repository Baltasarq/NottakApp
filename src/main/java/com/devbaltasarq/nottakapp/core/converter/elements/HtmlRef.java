// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.elements;


import com.devbaltasarq.nottakapp.core.converter.Element;
import com.devbaltasarq.nottakapp.core.converter.ElementDto;

import java.util.ArrayList;
import java.util.List;


/** A hypertext link.
  * @author baltasarq
  */
public class HtmlRef extends Link {
    public final static String NAME = "a";
    
    public HtmlRef(final ElementDto ELTO)
    {
        this( ELTO, new ArrayList<>() );
    }
    
    public HtmlRef(final ElementDto ELTO, List<Element> subElements)
    {
        super( ELTO, subElements );
    }
    
    @Override
    public String toString()
    {
        return String.format( "Html%s", super.toString() );
    }
}
