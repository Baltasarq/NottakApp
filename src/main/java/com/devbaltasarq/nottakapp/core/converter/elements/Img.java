// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.elements;


import com.devbaltasarq.nottakapp.core.converter.Element;
import java.util.ArrayList;
import java.util.List;


/** An image.
  * @author baltasarq
  */
public class Img extends Element {
    public final static String TAG_DESC = "img";
    
    public Img(String text)
    {
        this( text, new ArrayList<>() );
    }
    
    public Img(String text, List<Element> subElements)
    {
        super( TAG_DESC, text, subElements );
    }
    
    @Override
    public String toString()
    {
        return String.format( "Img %s", super.toString() );
    }
}
