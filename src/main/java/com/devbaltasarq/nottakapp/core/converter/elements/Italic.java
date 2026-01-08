// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.elements;


import com.devbaltasarq.nottakapp.core.converter.Element;
import java.util.ArrayList;
import java.util.List;


/** Italic effect.
  * @author baltasarq
  */
public class Italic extends Element {
    public final static String TAG_DESC = "i";
    
    public Italic(String text)
    {
        this( text, new ArrayList<>() );
    }
    
    public Italic(String text, List<Element> subElements)
    {
        super( TAG_DESC, text, subElements );
    }
    
    @Override
    public String toString()
    {
        return String.format( "Italic %s", super.toString() );
    }
}
