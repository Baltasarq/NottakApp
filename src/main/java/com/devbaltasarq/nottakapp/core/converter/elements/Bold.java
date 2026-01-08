// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.elements;


import com.devbaltasarq.nottakapp.core.converter.Element;
import java.util.ArrayList;
import java.util.List;


/** Bold effect.
  * @author baltasarq
  */
public class Bold extends Element {
    public static final String TAG_DESC = "b";
    
    public Bold(String text)
    {
        this( text, new ArrayList<>() );
    }
    
    public Bold(String text, List<Element> subElements)
    {
        super( TAG_DESC, text, subElements );
    }
    
    @Override
    public String toString()
    {
        return String.format( "Bold %s", super.toString() );
    }
}
