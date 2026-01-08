// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.elements;


import com.devbaltasarq.nottakapp.core.converter.Element;
import java.util.ArrayList;
import java.util.List;


/** Bold effect.
  * @author baltasarq
  */
public class Entry extends Element {
    public final static String TAG_DESC = "li";
    
    public Entry(String text)
    {
        this( text, new ArrayList<>() );
    }
    
    public Entry(String text, List<Element> subElements)
    {
        super( TAG_DESC, text, subElements );
    }
    
    @Override
    public String toString()
    {
        return String.format( "Entry %s", super.toString() );
    }
}
