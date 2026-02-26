// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.elements;


import com.devbaltasarq.nottakapp.core.converter.Element;
import com.devbaltasarq.nottakapp.core.converter.ElementDto;

import java.util.ArrayList;
import java.util.List;


/** An ordered list.
  * @author baltasarq
  */
public class OrdList extends Element {
    public final static String NAME = "ol";
    
    public OrdList(final ElementDto ELTO)
    {
        this( ELTO, new ArrayList<>() );
    }
    
    public OrdList(final ElementDto ELTO, List<Element> subElements)
    {
        super( ELTO, subElements );
    }
    
    @Override
    public String toString()
    {
        return String.format( "OrdList %s", super.toString() );
    }
}
