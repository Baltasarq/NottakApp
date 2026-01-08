// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter;


import com.devbaltasarq.nottakapp.core.converter.elements.Par;
import com.devbaltasarq.nottakapp.core.converter.elements.Root;
import com.devbaltasarq.nottakapp.core.converter.elements.Text;


/** Parses an HTML archive.
 * @author baltasarq
 */
public class HtmlParser {
    public HtmlParser(String text)
    {
        this( new HtmlScanner( text ) );
    }
    
    public HtmlParser(HtmlScanner scanner)
    {
        this.scanner = scanner;
        this.root = null;
        this.title = "";
    }
    
    /** @return the scanner in use. */
    public HtmlScanner getScanner()
    {
        return this.scanner;
    }
    
    /** @return the root of the parsed body, or null if not yet parsed. */
    public Root getRoot()
    {
        return this.root;
    }
    
    /** @return the title of the head of the document,
      * or null if not yet parsed.
      */
    public String getTitle()
    {
        return this.title;
    }
    
    /** Reads a complete tag.
      * @return the text read.
      * @throws ParseException if there is a syntax mismatch.
      */
    public String readWholeTag() throws ParseException
    {
        final var SCANNER = this.getScanner();
        String toret = "";
        
        SCANNER.skipSpaces();
        
        if ( SCANNER.getTokenType() == HtmlScanner.TokenType.TAG ) {
            final var CONTENTS = new StringBuilder();
            String tag = SCANNER.readTag();
            String closingTag = "/" + tag;
            
            while ( !SCANNER.isEod() ) {
                String nextTag = SCANNER.getNextTag();
                
                if ( nextTag != null
                  && nextTag.equals( closingTag ))
                {
                    break;
                }
                
                CONTENTS.append( SCANNER.getCurrentChar() );
                SCANNER.skip();
            }
            
            SCANNER.readTag();
            toret = CONTENTS.toString();
        } else {
            throw new ParseException( 
                            "expecting tag, found: '"
                            + this.getScanner().getCurrentChar() + "'" );
        }
        
        return toret;
    }
    
    /** Read the whole head of the HTML document.
      * @return the title of the document, or blank if not found.
      * @throws ParseException if there is a syntax mismatch.
      */
    public String readHead() throws ParseException
    {
        final var SCANNER = this.getScanner();
        this.title = "";
        
        this.matchTag( "html" );

        if ( this.matchTag( "head" )) {
            // Pass all possible tags that are not the title.
            while ( !this.getScanner().isEod()
                 && !SCANNER.getNextTag().equals( "title" )
                 && !SCANNER.getNextTag().equals( "/head" ) )
            {
                this.readWholeTag();
            }

            if ( SCANNER.getNextTag().equals( "title" ) ) {
                this.title = this.readWholeTag();
            }

            // Pass all possible tags up to the end of the head section.
            while ( !SCANNER.isEod()
                 && !SCANNER.getNextTag().equals( "/head" ) )
            {
                this.readWholeTag();
            }

            if ( !this.matchTag( "/head" ) ) {
                throw new ParseException( "missing /head" );
            }
        }
        
        return this.title;
    }
    
    /** Pass the cursor after the next tag,
      * provided its name matches the given one.
      * @param tagName the given name of the tag to match.
      * @return true if the given tag is next, false otherwise.
      */
    public boolean matchTag(String tagName)
    {
        boolean toret = false;
        String nextTag = this.getScanner().getNextTag();
        
        
        if ( nextTag != null
          && nextTag.equals( tagName ) )
        {
            // Pass tag
            this.getScanner().readTag();
            toret = true;
        }
        
        return toret;
    }
    
    /** Reads the body of the HTML document, and returns its root Element.
      * @return the root element of the body of the HTML.
      * @see HtmlParser::getRoot
      * @see HtmlParser::readHead
      * @throws ParseException if there is a syntax mismatch.
      */
    public Root readBody() throws ParseException
    {
        final var SCANNER = this.getScanner();
        
        // Read the head of the HTML document
        if ( this.matchTag( "html" ) ) {
            this.readHead();
        }
        
        if ( this.matchTag( "head" ) ) {
            this.readHead();
        }
        
        // Now read the body
        if ( !this.matchTag( "body" ) ) {
            throw new ParseException( "missing: body" );
        }
        
        // Create the root element
        this.root = new Root();
        Element current = (Element) this.root;
        
        // Read the document until reaching the end
        while ( !SCANNER.isEod() ) {
            if ( SCANNER.isNextTag( "/body" ) ) {
                break;
            }

            String nextTag = SCANNER.getNextTag();
            
            if ( nextTag != null ) {
                var newElement
                        = Element.createFor( nextTag, this.readWholeTag() );
                
                this.parseTagsIn( newElement );
                current.add( newElement );
            }
            else
            if ( SCANNER.getNextTokenType()
                                                == HtmlScanner.TokenType.TEXT )
            {
                final var PAR = Element.createFor( Par.TAG_DESC, "" );
                
                PAR.add( Element.createFor( Text.TAG_DESC,
                                                    SCANNER.readText() ) );
                current.add( PAR );
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
        final HtmlParser PARSER = new HtmlParser( element.getText() );
        final HtmlScanner SCANNER = PARSER.getScanner();
 
        while ( !SCANNER.isEod() ) {
            switch( SCANNER.getNextTokenType() ) {
                case BLANK -> {
                    SCANNER.skipSpaces();
                }
                case CHAR, TEXT -> {
                    element.add( Element.createFor( Text.TAG_DESC, SCANNER.readText() ) );
                }
                case TAG -> {
                    String nextTag = SCANNER.getNextTag();
                    
                    var newElement
                        = Element.createFor( nextTag, PARSER.readWholeTag() );
                    element.add( newElement );
                    this.parseTagsIn( newElement );
                }

            }
        }
    }
    
    private final HtmlScanner scanner;
    private Root root;
    private String title;
}
