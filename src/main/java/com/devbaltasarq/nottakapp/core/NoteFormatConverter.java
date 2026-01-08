// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core;

import com.devbaltasarq.nottakapp.core.converter.HtmlParser;
import com.devbaltasarq.nottakapp.core.converter.ParseException;
import com.devbaltasarq.nottakapp.core.converter.MarkDownRunner;
import com.devbaltasarq.nottakapp.core.converter.elements.Root;


/** Used to convert HTML to MarkDown and vice versa.
  * @author baltasarq
  */
public class NoteFormatConverter {
    private NoteFormatConverter(Root root)
    {
        this.root = root;
    }
    
    /** @return the text of the document, as markdown. */
    public String toMDText()
    {
        final var RUNNER = new MarkDownRunner( this.getRoot() );
        
        RUNNER.run();
        return RUNNER.toString();
    }
    
    /** @return the root element. */
    public Root getRoot()
    {
        return this.root;
    }
    
    @Override
    public String toString()
    {
        return this.toMDText();
    }
    
    /** Creates a new converter, given a string with HTML contents.
      * @param text a given string with HTML content.
      * @return a new converter.
      * @throws ParseException if there is a syntax mismatch.
     */
    public static NoteFormatConverter fromHtml(String text) throws ParseException
    {
        final var CONVERTER = new HtmlParser( text );
        
        CONVERTER.readBody();
        return new NoteFormatConverter( CONVERTER.getRoot() );
    }
    
    private final Root root;
}
