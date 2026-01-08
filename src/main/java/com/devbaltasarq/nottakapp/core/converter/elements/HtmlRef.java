// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.elements;


import com.devbaltasarq.nottakapp.core.converter.Element;
import java.util.ArrayList;
import java.util.List;


/** An URL.
  * @author baltasarq
  */
public class HtmlRef extends Element {
    public final static String TAG_DESC = "a";
    
    public HtmlRef(String text)
    {
        this( text, new ArrayList<>() );
    }
    
    public HtmlRef(String text, List<Element> subElements)
    {
        super( TAG_DESC, text, subElements );
    }
    
    @Override
    public String toString()
    {
        return String.format( "HtmlRef %s", super.toString() );
    }
}
