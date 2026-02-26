// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.html;


import com.devbaltasarq.nottakapp.core.converter.Element;
import com.devbaltasarq.nottakapp.core.converter.ElementDto;
import com.devbaltasarq.nottakapp.core.converter.ParseException;
import com.devbaltasarq.nottakapp.core.converter.Parser;
import com.devbaltasarq.nottakapp.core.converter.elements.Root;
import com.devbaltasarq.nottakapp.core.converter.elements.Text;

import java.util.Map;


/** Parses an HTML archive.
 * @author baltasarq
 */
public class HtmlParser extends Parser {
    private static final String NON_CLOSEABLE = " img input ";
    
    public HtmlParser(String text)
    {
        this( new HtmlScanner( text ) );
    }
    
    public HtmlParser(HtmlScanner scanner)
    {
        super( scanner );
        this.title = "";
    }

    /** @return the title of the head of the document,
      * or null if not yet parsed.
      */
    public String getTitle()
    {
        return this.title;
    }
    
    private static boolean isTagNotCloseable(final String TAG_NAME)
    {
        return NON_CLOSEABLE.contains( " " + TAG_NAME + " " );
    }
    
    @Override
    public HtmlScanner getScanner()
    {
        return (HtmlScanner) super.getScanner();
    }
    
    /** Reads a complete tag.
      * @return the tag info read, as a ElementDto object.
      * @throws ParseException if there is a syntax mismatch.
      */
    public ElementDto parseWholeTag() throws ParseException
    {
        final var SCANNER = this.getScanner();
        ElementDto toret = null;
        
        SCANNER.skipSpaces();
        
        if ( SCANNER.getTokenType() == HtmlScanner.TokenType.TAG ) {
            final Map<String, String> TAG_INFO = SCANNER.readTag();
            
            if ( TAG_INFO.containsKey( HtmlScanner.LBL_ERROR ) ) {
                throw new ParseException( TAG_INFO.get( HtmlScanner.LBL_ERROR ));
            }
            
            final String TAG_NAME = TAG_INFO.get( HtmlScanner.LBL_TAG_NAME );
            final var CONTENTS = new StringBuilder();
            
            if ( !isTagNotCloseable( TAG_NAME ) ) {
                final String CLOSING_TAG = "/" + TAG_NAME;

                while ( !SCANNER.isEod() ) {
                    String nextTag = SCANNER.getNextTagName();

                    if ( nextTag != null
                      && nextTag.equals( CLOSING_TAG ))
                    {
                        break;
                    }

                    CONTENTS.append( SCANNER.readChar() );
                }

                SCANNER.readTag();
            }

            toret = new ElementDto( TAG_NAME, CONTENTS.toString(), TAG_INFO );
        } else {
            throw new ParseException( 
                            "expecting tag, found: '"
                            + this.getScanner().getCurrentChar() + "'" );
        }
        
        return toret;
    }
    
    /** Pass the cursor after the next tag,
      * provided its name matches the given one.
      * @param tagName the given name of the tag to match.
      * @return true if the given tag is next, false otherwise.
      */
    public boolean matchTag(String tagName)
    {
        boolean toret = false;
        String nextTag = this.getScanner().getNextTagName();
        
        
        if ( nextTag != null
          && nextTag.equals( tagName ) )
        {
            // Pass tag
            this.getScanner().readTag();
            toret = true;
        }
        
        return toret;
    }
    
    /** Read the whole head of the HTML document.
      * @return the title of the document, or blank if not found.
      * @throws ParseException if there is a syntax mismatch.
      */
    public String parseHead() throws ParseException
    {
        final var SCANNER = this.getScanner();
        this.title = "";
        
        this.matchTag( "html" );

        if ( this.matchTag( "head" )) {
            // Pass all possible tags that are not the title.
            while ( !this.getScanner().isEod()
                 && !SCANNER.getNextTagName().equals( "title" )
                 && !SCANNER.getNextTagName().equals( "/head" ) )
            {
                this.parseWholeTag();
            }

            if ( SCANNER.getNextTagName().equals( "title" ) ) {
                this.title = this.parseWholeTag().getContents();
            }

            // Pass all possible tags up to the end of the head section.
            while ( !SCANNER.isEod()
                 && !SCANNER.getNextTagName().equals( "/head" ) )
            {
                this.parseWholeTag();
            }

            if ( !this.matchTag( "/head" ) ) {
                throw new ParseException( "missing /head" );
            }
        }
        
        return this.title;
    }
    
    /** Reads the body of the HTML document, and returns its root Element.
      * @return the root element of the body of the HTML.
      * @see HtmlParser::getRoot
      * @see HtmlParser::readHead
      * @throws ParseException if there is a syntax mismatch.
      */
    public Root parseBody() throws ParseException
    {
        final var SCANNER = this.getScanner();
        
        // Read the head of the HTML document
        if ( this.matchTag( "html" ) ) {
            this.parseHead();
        }
        
        if ( this.matchTag( "head" ) ) {
            this.parseHead();
        }
        
        // Now read the body
        if ( !this.matchTag( "body" ) ) {
            throw new ParseException( "missing: body" );
        }
        
        // Create the root element
        Element current = (Element) this.getRoot();
        
        // Read the document until reaching the end
        while ( !SCANNER.isEod() ) {
            if ( SCANNER.isNextTag( "/body" ) ) {
                break;
            }

            String nextTag = SCANNER.getNextTagName();
            
            if ( nextTag != null ) {
                var newElement
                        = Element.createFrom(this.parseWholeTag() );
                
                this.parseTagsIn( newElement );
                current.add( newElement );
            }
            else {
                final HtmlScanner.TokenType TTOKEN = SCANNER.getNextTokenType();
                
                if ( TTOKEN == HtmlScanner.TokenType.TEXT
                  || TTOKEN == HtmlScanner.TokenType.CHAR )
                {
                    final var TEXT = Element.createFrom(
                                                new ElementDto(
                                                        Text.NAME,
                                                        SCANNER.readText() ) );
                    current.add( TEXT );
                }
            }
        }
        
        // Finish the body
        if ( !this.matchTag( "/body" ) ) {
            throw new ParseException( "missing: /body" );
        }
        
        if ( !this.matchTag( "/html" ) ) {
            throw new ParseException( "missing: /html" );
        }
        
        return this.getRoot();
    }
    
    /** Parse tags in a given text. */
    private void parseTagsIn(Element element) throws ParseException
    {
        final HtmlParser PARSER = new HtmlParser( element.getContents() );
        final HtmlScanner SCANNER = PARSER.getScanner();
 
        element.clearContents();
        while ( !SCANNER.isEod() ) {
            switch( SCANNER.getNextTokenType() ) {
                case BLANK -> {
                    SCANNER.skipSpaces();
                }
                case CHAR, TEXT -> {
                    final String TEXT = SCANNER.readText();
                    final var ELTO = new ElementDto( Text.NAME, TEXT );
                    element.add( Element.createFrom( ELTO ));
                }
                case TAG -> {
                    var newElement = Element.createFrom(PARSER.parseWholeTag() );
                    element.add( newElement );
                    this.parseTagsIn( newElement );
                }
            }
        }
    }
    
    @Override
    public Root parse() throws ParseException
    {
        return this.parseBody();
    }
    
    private String title;
}
