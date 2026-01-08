// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.elements;


import com.devbaltasarq.nottakapp.core.converter.Element;
import java.util.ArrayList;
import java.util.List;


/** Reference to another note.
  * @author baltasarq
  */
public class Ref extends Element {
    public final static String TAG_DESC = "wikiref";
    
    public Ref(String text)
    {
        this( text, new ArrayList<>() );
    }
    
    public Ref(String text, List<Element> subElements)
    {
        super( TAG_DESC, text, subElements );
    }
    
    @Override
    public String toString()
    {
        return String.format( "Ref %s", super.toString() );
    }
}
