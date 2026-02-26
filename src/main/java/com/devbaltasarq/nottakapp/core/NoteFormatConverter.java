// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core;

import com.devbaltasarq.nottakapp.core.converter.html.HtmlParser;
import com.devbaltasarq.nottakapp.core.converter.markdown.MDParser;
import com.devbaltasarq.nottakapp.core.converter.ParseException;
import com.devbaltasarq.nottakapp.core.converter.Parser;
import com.devbaltasarq.nottakapp.core.converter.runners.MarkDownRunner;
import com.devbaltasarq.nottakapp.core.converter.runners.PlainTextRunner;
import com.devbaltasarq.nottakapp.core.converter.runners.HtmlRunner;
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
    
    /** @return the text of the document, as plain text. */
    public String toPlainText()
    {
        final var RUNNER = new PlainTextRunner( this.getRoot() );
        
        RUNNER.run();
        return RUNNER.toString();
    }
    
    /** @return the text of the document, as html. */
    public String toHtml()
    {
        final var RUNNER = new HtmlRunner( this.getRoot() );
        
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
        return fromParser( new HtmlParser( text ) );
    }
    
    /** Creates a new converter, given a string with MarkDown contents.
      * @param text a given string with HTML content.
      * @return a new converter.
      * @throws ParseException if there is a syntax mismatch.
     */
    public static NoteFormatConverter fromMD(String text) throws ParseException
    {
        return fromParser( new MDParser( text ) );
    }
    
    /** Creates a new converter from any parser.
      * @param PARSER the parser from which to well... parse.
      * @return a new NoteFormatConverter.
      * @throws ParseException if parsing goes wrong.
      */
    public static NoteFormatConverter fromParser(final Parser PARSER)
                                                          throws ParseException
    {
        PARSER.parse();
        return new NoteFormatConverter( PARSER.getRoot() );
    }
    
    private final Root root;
}
