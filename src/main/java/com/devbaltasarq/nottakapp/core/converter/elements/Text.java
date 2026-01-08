// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.elements;


import com.devbaltasarq.nottakapp.core.converter.Element;
import java.util.ArrayList;
import java.util.List;


/** Just text.
  * @author baltasarq
  */
public class Text extends Element {
    public static final String TAG_DESC = "txt";

    public Text(String text)
    {
        this( text, new ArrayList<>() );
    }
    
    public Text(String text, List<Element> subElements)
    {
        super( TAG_DESC, text, subElements );
    }
    
    @Override
    public String toString()
    {
        return String.format( "Text %s", super.toString() );
    }
}
