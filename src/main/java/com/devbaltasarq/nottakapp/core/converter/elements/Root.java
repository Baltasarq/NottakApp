// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.elements;


import com.devbaltasarq.nottakapp.core.converter.Element;
import java.util.ArrayList;
import java.util.List;


/** The root of the document.
  * @author baltasarq
  */
public class Root extends Element {
    public Root()
    {
        this( new ArrayList<>() );
    }
    
    public Root(List<Element> subElements)
    {
        super( "", "", subElements );
    }
    
    @Override
    public String toString()
    {
        return String.format( "Root %s", super.toString() );
    }
}
