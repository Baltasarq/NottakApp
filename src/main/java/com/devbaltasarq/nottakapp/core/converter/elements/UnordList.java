// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.elements;


import com.devbaltasarq.nottakapp.core.converter.Element;
import java.util.ArrayList;
import java.util.List;


/** An unordered list.
  * @author baltasarq
  */
public class UnordList extends Element {
    public final static String TAG_DESC = "ul";
    
    public UnordList(String text)
    {
        this( text, new ArrayList<>() );
    }
    
    public UnordList(String text, List<Element> subElements)
    {
        super( TAG_DESC, text, subElements );
    }
    
    @Override
    public String toString()
    {
        return String.format( "UnordList %s", super.toString() );
    }
}
