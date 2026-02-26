// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.elements;


import com.devbaltasarq.nottakapp.core.converter.Element;
import com.devbaltasarq.nottakapp.core.converter.ElementDto;
import java.util.ArrayList;
import java.util.List;


/** A check (activated or not).
  * @author baltasarq
  */
public class Chk extends Element {
    public static final String NAME = "input";
    public static final String ETQ_CHECKED = "checked";
    
    public Chk(final ElementDto ELTO)
    {
        this( ELTO, new ArrayList<>() );
    }
    
    public Chk(final ElementDto ELTO, List<Element> subElements)
    {
        super( ELTO, subElements );
    }
    
    /** Sets the activated attribute to true or false.
      * @param activated true activates it, false deactivates it.
      */
    public void setActivated(boolean activated)
    {
        if ( activated ) {
            this.setAttr( ETQ_CHECKED );
        } else {
            this.removeAttr( ETQ_CHECKED );
        }
    }
    
    /** When activated, it deactivates it, and vice versa. */
    public void flip()
    {
        this.setActivated( !this.isActivated() );
    }
    
    /** @return the activated attribute. */
    public boolean isActivated()
    {
        return this.containsAttr( ETQ_CHECKED );
    }
    
    @Override
    public String toString()
    {
        return String.format("Chk (%b) %s",
                                    this.isActivated(),
                                    super.toString() );
    }
    
    private int num;
}
