// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.elements;


import com.devbaltasarq.nottakapp.core.converter.Element;
import com.devbaltasarq.nottakapp.core.converter.ElementDto;

import java.util.List;


/** Represents notes and web references.
  * @author baltasarq
  */
public abstract class Link extends Element {
    public final static String LBL_REF = "href";

    public Link(final ElementDto ELTO, List<Element> subElements)
    {
        super( ELTO, subElements );
    }
    
    /** @return the referenced URL inside the a tag. */
    public String getURL()
    {
        return this.getAttributes().getOrDefault( LBL_REF, "" );
    }
    
    @Override
    public String toString()
    {
        return String.format( "Ref %s", super.toString() );
    }
}
