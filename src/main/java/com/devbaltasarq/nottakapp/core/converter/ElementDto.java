// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter;


import com.devbaltasarq.nottakapp.core.converter.html.HtmlScanner;
import java.util.HashMap;
import java.util.Map;


/** Represents a future element, like a Tag such as <b>test</b>, or **test**.
  * @author baltasarq
  */
public class ElementDto {
    public ElementDto(String tagName, String contents)
    {
        this( tagName, contents, new HashMap<>() );
    }

    public ElementDto(String tagName, String contents, Map<String, String> attrs)
    {
        this.name = tagName;
        this.contents = contents.trim();
        this.attrs = new HashMap<>( attrs );
        this.attrs.remove( HtmlScanner.LBL_TAG_NAME );
    }

    /** @return true if this tag is valid, false otherwise. */
    public boolean isValid()
    {
        return this.attrs.containsKey( HtmlScanner.LBL_ERROR );
    }

    /** @return the name of the tag. */
    public String getName()
    {
        return this.name;
    }

    /** @return the error message if this tag is invalid, "" otherwise. */
    public String getErrorMessage()
    {
        return this.attrs.getOrDefault( HtmlScanner.LBL_ERROR, "" );
    }

    /** @return a map with all the attributes of the tag. */
    public Map<String, String> getAttributes()
    {
        return new HashMap( this.attrs );
    }

    /** @return the contents between the opening and closing tag. */
    public String getContents()
    {
        return this.contents;
    }
    
    /** Sets a given attribute, given its name and value.
      * @param key the name of the attribute.
      * @param value the value of the attribute.
      */
    public void setAttr(String key, String value)
    {
        this.attrs.put( key, value );
    }
    
    /** Sets a given attribute, given its name.
      * This is given as a support for the attribute 'checked' in checkboxes,
      * for instance.
      * @param key the name of the attribute.
      */
    public void setAttr(String key)
    {
        this.attrs.put( key, null );
    }

    private final String name;
    private final String contents;
    private final Map<String, String> attrs;
}
