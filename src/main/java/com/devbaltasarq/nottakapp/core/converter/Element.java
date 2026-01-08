// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter;


import java.util.List;
import java.util.ArrayList;

import com.devbaltasarq.nottakapp.core.converter.elements.Par;
import com.devbaltasarq.nottakapp.core.converter.elements.Text;
import com.devbaltasarq.nottakapp.core.converter.elements.Bold;
import com.devbaltasarq.nottakapp.core.converter.elements.Italic;
import com.devbaltasarq.nottakapp.core.converter.elements.HtmlRef;
import com.devbaltasarq.nottakapp.core.converter.elements.Ref;
import com.devbaltasarq.nottakapp.core.converter.elements.OrdList;
import com.devbaltasarq.nottakapp.core.converter.elements.UnordList;
import com.devbaltasarq.nottakapp.core.converter.elements.Img;
import com.devbaltasarq.nottakapp.core.converter.elements.Entry;
import com.devbaltasarq.nottakapp.core.converter.elements.Head;


/** An element inside a document.
  * @author baltasarq
  */
public class Element {
    protected Element(String tagDesc, String text)
    {
        this( tagDesc, text, new ArrayList<>() );
    }
    
    protected Element(String tagDesc, String text, List<Element> subElements)
    {
        this.text = text;
        this.closing = false;
        this.parent = null;
        this.tagDesc = tagDesc;
        this.subElements = new ArrayList<>( subElements );
    }
    
    /** Whether this is a closing element, as in /i.
      * @return true if it is a closing element, false otherwise.
      */
    public boolean isClosing()
    {
        return closing;
    }
    
    /** Set this element as a closing, as in /i. */
    private void setClosing()
    {
        this.closing = true;
    }
    
    /** Creates a copy of this element, as a closing.
      * @return a new element, as a closing.
      */
    public Element copyAsClosing()
    {
        Element toret = null;
        
        try {
            toret = Element.createFor( this.tagDesc, "" );
        } catch(ParseException exc) {
            // this is a copy, it is impossible
            throw new Error( "[ERR] copy as a closing element: " + exc.getMessage() );
        }
            
        toret.setClosing();
        return toret;
    }
    
    @Override
    public boolean equals(Object o)
    {
        boolean toret = false;
        
        if ( o instanceof Element other ) {
            if ( this.getText().equals( other.getText() )
              && this.count() == other.count() )
            {
                toret = true;
                
                for(int i = 0; i < this.count(); ++i) {
                    if ( !this.get( i ).equals( other.get( i ) ) ) {
                        toret = false;
                        break;
                    }
                }
            }
        }
        
        return toret;
    }
    
    @Override
    public int hashCode()
    {
        return this.getText().hashCode() + ( 11 * this.count() );
    }
    
    /** @return the parent of this element, or null if it is root
      *         or it hasn't been added as a subelement of another.
      */
    public Element getParent()
    {
        return this.parent;
    }
    
    // Replaces the parent of this element.
    private void replaceParent(Element newParent)
    {
        this.parent = newParent;
    }
    
    /** Adds a new sub-element.
      * @param newElement the new element to add as a subelement.
      */
    public void add(Element newElement)
    {
        newElement.replaceParent( this );
        this.subElements.add( newElement );
        this.text = "";
    }
    
    /** @return the last element added. */
    public Element getLast()
    {
        Element toret = null;
        
        if ( !this.subElements.isEmpty() ) {
            toret = this.subElements.get( this.subElements.size() - 1 );
        }
        
        return toret;
    }
    
    /** @return All the subelements in this element. */
    public List<Element> getAll()
    {
        return new ArrayList<>( this.subElements );
    }
    
    /** @return the number of elements. */
    public int count()
    {
        return this.subElements.size();
    }
    
    /** @return the element at the given pos.
      * @param pos the index in the list.
      */
    public Element get(int pos)
    {
        return this.subElements.get( pos );
    }
    
    /** @return the text of the node. */
    public String getText()
    {
        return this.text;
    }
    
    @Override
    public String toString()
    {
        return String.format( "element with text \"%s\", and %d subelements:\n%s",
                this.getText(),
                this.count(),
                String.join(
                        "\n",
                        this.subElements.stream()
                                        .map( e -> e.toString() ).toList() ));
    }
    
    /** Creates the corresponding element for the given tag.
      * @param tagDesc the name of the tag.
      * @param text the text inside of the tag.
      * @return a new element, corresponding to the given tag.
      * @throws ParseException if there is no element for the given tag.
     */
    public static Element createFor(String tagDesc, String text)
                    throws ParseException
    {
        Element toret = null;
        
        // Chk
        if ( tagDesc == null ) {
            tagDesc = "";
        }
        
        if ( text == null ) {
            text = "";
        }
        
        // Remove front '/', if present.
        tagDesc = tagDesc.trim().toLowerCase();
        if ( !tagDesc.isEmpty()
           && tagDesc.charAt( 0 ) == '/' )
        {
            tagDesc = tagDesc.substring( 1 );
        }
        
        switch ( tagDesc ) {
            case Text.TAG_DESC      -> toret = new Text( text );
            case Par.TAG_DESC       -> toret = new Par( text );
            case Bold.TAG_DESC      -> toret = new Bold( text );
            case Italic.TAG_DESC    -> toret = new Italic( text );
            case HtmlRef.TAG_DESC   -> toret = new HtmlRef( text );
            case Entry.TAG_DESC     -> toret = new Entry( text );
            case Img.TAG_DESC       -> toret = new Img( text );
            case Ref.TAG_DESC       -> toret = new Ref( text );
            case UnordList.TAG_DESC -> toret = new UnordList( text );
            case OrdList.TAG_DESC   -> toret = new OrdList( text );
            case Head.TAG_DESC      -> toret = new Head( text );
            default    ->
                throw new ParseException( "tag not manageable: '" + tagDesc + "'" );
        }
        
        return toret;
    }
    
    private Element parent;
    private String text;
    private boolean closing;
    private final String tagDesc;
    private final List<Element> subElements;
}
