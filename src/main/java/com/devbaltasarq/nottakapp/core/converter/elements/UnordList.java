// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.elements;


import com.devbaltasarq.nottakapp.core.converter.Element;
import com.devbaltasarq.nottakapp.core.converter.ElementDto;
import java.util.ArrayList;
import java.util.List;


/** An unordered list.
  * @author baltasarq
  */
public class UnordList extends Element {
    public final static String NAME = "ul";
    
    public UnordList(final ElementDto ELTO)
    {
        this( ELTO, new ArrayList<>() );
    }
    
    public UnordList(final ElementDto ELTO, List<Element> subElements)
    {
        super( ELTO, subElements );
    }
    
    @Override
    public String toString()
    {
        return String.format( "UnordList %s", super.toString() );
    }
}
