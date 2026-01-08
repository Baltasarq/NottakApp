// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.elements;


import com.devbaltasarq.nottakapp.core.converter.Element;
import java.util.ArrayList;
import java.util.List;


/** An ordered list.
  * @author baltasarq
  */
public class OrdList extends Element {
    public final static String TAG_DESC = "ol";
    
    public OrdList(String text)
    {
        this( text, new ArrayList<>() );
    }
    
    public OrdList(String text, List<Element> subElements)
    {
        super( TAG_DESC, text, subElements );
    }
    
    @Override
    public String toString()
    {
        return String.format( "OrdList %s", super.toString() );
    }
}
