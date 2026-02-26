// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter;


import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import com.devbaltasarq.nottakapp.core.converter.elements.Root;
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
import com.devbaltasarq.nottakapp.core.converter.elements.Chk;


/** An element inside a document.
  * @author baltasarq
  */
public class Element {
    private static final String HTTP_PROTOCOL = "http://";
    private static final String HTTPS_PROTOCOL = "https://";
    private static final String MARKDOWN_EXTENSION = ".md";
    
    protected Element(final ElementDto ELTO)
    {
        this( ELTO, new ArrayList<>() );
    }
    
    protected Element(final ElementDto ELTO, List<Element> subElements)
    {
        this.contents = ELTO.getContents();
        this.closing = false;
        this.parent = null;
        this.name = ELTO.getName();
        this.attrs = ELTO.getAttributes();
        this.subElements = new ArrayList<>( subElements );
    }
    
    /** @return the id, if any, on this element, or an empty string. */
    public String getId()
    {
        String id = this.attrs.get( "id" );
        
        if ( id == null ) {
            id = "";
        }
        
        return id;
    }
    
    /** @return the name of the node. */
    public String getName()
    {
        return this.name;
    }
    
    /** @return the contents of the node. */
    public String getContents()
    {
        return this.contents;
    }
    
    /** @return the attributes. */
    public Map<String, String> getAttributes()
    {
        return new HashMap<>( this.attrs );
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
            toret = Element.createFrom( this );
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
            if ( this.getContents().equals( other.getContents() )
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
        return this.getContents().hashCode() + ( 11 * this.count() );
    }
    
    /** @return the parent of this element, or null if it is root
      *         or it hasn't been added as a subelement of another.
      */
    public Element getParent()
    {
        return this.parent;
    }
    
    // Replaces the parent of this element.
    protected void replaceParent(Element newParent)
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
    }
    
    /** Removes a subelement.
      * @param pos the position of the element to remove.
      * @return true if the position was correct and the element was removed.
      */
    public boolean removeAt(int pos)
    {
        boolean toret = false;
        
        if ( pos >= 0
          && pos < this.count() )
        {
            this.subElements.remove( pos );
        }
        
        return toret;
    }
    
    /** Removes a subelement.
      * @param sub the subelement to remove.
      * @return true if the subelement was found and removed. 
      */
    public boolean remove(Element sub)
    {
        return this.subElements.remove( sub );
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
    
    public void clearContents()
    {
        this.contents = "";
    }
    
    /** Determines whether this element should be closed,
      * (i.e., a p, or h...)
      * or not (i.e., the phony ones, Root or Text)
      * @return true if it must be closed, false otherwise.
      */
    public boolean needsClosing()
    {
        return true;
    }
    
    /** Sets a given attribute, given only its name.
      * Internally, the attribute has value null.
      * This must be supported for the infamous "checked" attribute,
      * that is not assigned to anything.
      * @param key the name of the attribute.
      */
    protected void setAttr(String key)
    {
        this.attrs.put( key, null );
    }
    
    /** Sets a given attribute, given its name and value.
      * @param key the name of the attribute.
      * @param value the value of the attribute.
      */
    protected void setAttr(String key, String value)
    {
        this.attrs.put( key, value );
    }

    /** Removes a given attributes, given its name.
      * @param key the name of the attribute.
      */
    protected void removeAttr(String key)
    {
        this.attrs.remove( key );
    }
    
    /** @return true if an attribute with the given key is stored,
      *               false otherwise.
      * @param key the name of the attribute.
      */
    protected boolean containsAttr(String key)
    {
        return this.attrs.containsKey( key );
    }
    
    /** Finds those elements that are represented by the given ids.
      * @param IDS A given set of id's that are going to be looked for.
      * @return a list of elements, found by their id's.
      */
    public List<Element> lookForSubElementsWith(final Set<String> IDS)
    {
        final var TORET = new ArrayList<Element>( IDS.size() );
        final var ELEMS_TO_LOOK = new ArrayList<Element>( this.count() );
        
        ELEMS_TO_LOOK.add( this );
        while( !IDS.isEmpty()
            && !ELEMS_TO_LOOK.isEmpty() )
        {
            final Element ELEM = ELEMS_TO_LOOK.get( 0 );
            
            ELEMS_TO_LOOK.remove( 0 );

            // Check all names
            for(final String ID: IDS) {
                if ( ELEM.getId().equals( ID ) ) {
                    IDS.remove( ID );
                    TORET.add( ELEM );
                    break;
                }
            }

            // Now, store the subelements
            for(final Element SUB_ELEM: ELEM.subElements) {
                ELEMS_TO_LOOK.add( SUB_ELEM );
            }
        }
        
        ELEMS_TO_LOOK.clear();
        return TORET;
    }
    
    @Override
    public String toString()
    {
        return String.format("element with text \"%s\", attributes \"%s\", and %d subelements:\n%s",
                this.getContents(),
                String.join(
                        "\n",
                        this.attrs.entrySet().stream()
                                        .map( p -> p.toString() ).toList() ),
                this.count(),
                String.join(
                        "\n",
                        this.subElements.stream()
                                        .map( e -> e.toString() ).toList() ));
    }
    
    /** Creates a new element.
      * @param name the name of the future element.
      * @param contents the contents of the future element.
      * @return the corresponding element.
      * @throws ParseException if something goes wrong.
      */
    public static Element createFrom(String name, String contents)
                    throws ParseException
    {
        return Element.createFrom(
                new ElementDto(
                            name,
                            contents,
                            new HashMap<>() ));
    }
    
    /** Creates the corresponding element for the given element.
      * @param element the element to copy.
      * @return a new element, corresponding to the given element.
      * @throws ParseException if there is no element for the given element.
     */
    public static Element createFrom(Element element)
                    throws ParseException
    {
        return Element.createFrom(
                new ElementDto(
                        element.getName(),
                        element.getContents(),
                        element.getAttributes()));
    }
    
    /** Creates the appropriate element from a link element.
      * It can be a HTML ref, or a Wiki ref.
      * @param HREF a ElementDto object.
      * @return an HtmlRef or Ref object.
      */
    private static Element createFromRef(final ElementDto ELTO)
    {
        final String URL = ELTO.getAttributes().get( "href" );
        Element toret = null;
        
        if ( URL == null ) {
            throw new Error( "createElementFromRef(): URL is null" );
        }
        
        final String URL_LOWER = URL.trim().toLowerCase();

        if ( ( URL_LOWER.startsWith( HTTP_PROTOCOL )
          || URL_LOWER.startsWith( HTTPS_PROTOCOL ) )
          && !URL_LOWER.endsWith( MARKDOWN_EXTENSION ) )
        {
            toret = new HtmlRef( ELTO );
        } else {
            toret = new Ref( ELTO );
        }
        
        return toret;
    }
    
    /** Creates the corresponding element for the given element.
      * @param ELTO a ElementDto object with the element's description.
      * @return a new element, corresponding to the given element.
      * @throws ParseException if there is no element for the given element.
     */
    public static Element createFrom(final ElementDto ELTO)
                    throws ParseException
    {
        if ( ELTO == null ) {
            throw new Error( "Element.createFor: missing dto" );
        }
        
        Element toret = null;
        String name = ELTO.getName();
        char headingLevel = '2';
        
        // Chk
        if ( name == null ) {
            throw new Error( "Element.createFor: missing the element's name" );
        }
        
        if ( !name.isEmpty() ) {
            // Remove front '/', if present.
            name = name.trim().toLowerCase( Locale.US );
            
            if ( name.charAt( 0 ) == '/' ) {
                name = name.substring( 1 );
            }

            // Is it the heading element?
            if ( name.charAt( 0 ) == 'h' ) {
                if ( name.length() > 1 ) {
                    headingLevel = name.charAt( 1 );
                } else {
                    throw new Error( "heading missing the heading level" );
                }

                name = "h";
                ELTO.setAttr(
                        Head.ETQ_LEVEL,
                        Character.toString( headingLevel ) );
            }
        }
        
        switch ( name ) {
            case ""             -> toret = new Root();
            case Text.NAME      -> toret = new Text( ELTO );
            case Par.NAME       -> toret = new Par( ELTO );
            case Bold.NAME      -> toret = new Bold( ELTO );
            case Italic.NAME    -> toret = new Italic( ELTO );
            case HtmlRef.NAME   -> toret = createFromRef( ELTO );
            case Ref.NAME       -> toret = new Ref( ELTO );
            case Entry.NAME     -> toret = new Entry( ELTO );
            case Img.NAME       -> toret = new Img( ELTO );
            case UnordList.NAME -> toret = new UnordList( ELTO );
            case OrdList.NAME   -> toret = new OrdList( ELTO );
            case Head.NAME      -> toret = new Head( ELTO );
            case Chk.NAME       -> toret = new Chk( ELTO );
            default             ->
                throw new ParseException( "element not manageable: '" + name + "'" );
        }
        
        return toret;
    }
    
    private Element parent;
    private String contents;
    private boolean closing;
    private final String name;
    private final List<Element> subElements;
    private final Map<String, String> attrs;
}
