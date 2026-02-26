// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.elements;


import com.devbaltasarq.nottakapp.core.converter.Element;
import com.devbaltasarq.nottakapp.core.converter.ElementDto;

import java.util.ArrayList;
import java.util.List;


/** An image.
  * @author baltasarq
  */
public class Img extends Element {
    public final static String NAME = "img";
    public final static String LBL_SRC = "src";
    public final static String LBL_TITLE = "alt";
    
    public Img(final ElementDto ELTO)
    {
        this( ELTO, new ArrayList<>() );
    }
    
    public Img(final ElementDto ELTO, List<Element> subElements)
    {
        super( ELTO, subElements );
    }
    
    /** @return the src tag of the image. */
    public String getSrc()
    {
        return this.getAttributes().getOrDefault( LBL_SRC, "" );
    }
    
    /** @return the title of the image. */
    public String getTitle()
    {
        return this.getAttributes().getOrDefault( LBL_TITLE, "" );
    }
    
    @Override
    public String toString()
    {
        return String.format( "Img %s", super.toString() );
    }
}
