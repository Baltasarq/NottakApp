// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.markdown;


import com.devbaltasarq.nottakapp.core.converter.ParseException;
import com.devbaltasarq.nottakapp.core.converter.Parser;
import com.devbaltasarq.nottakapp.core.converter.Element;
import com.devbaltasarq.nottakapp.core.converter.ElementDto;
import com.devbaltasarq.nottakapp.core.converter.elements.Root;
import com.devbaltasarq.nottakapp.core.converter.elements.Par;
import com.devbaltasarq.nottakapp.core.converter.elements.Chk;
import com.devbaltasarq.nottakapp.core.converter.elements.Text;
import com.devbaltasarq.nottakapp.core.converter.elements.Bold;
import com.devbaltasarq.nottakapp.core.converter.elements.Italic;
import com.devbaltasarq.nottakapp.core.converter.elements.Head;
import com.devbaltasarq.nottakapp.core.converter.elements.Entry;
import com.devbaltasarq.nottakapp.core.converter.elements.HtmlRef;
import com.devbaltasarq.nottakapp.core.converter.elements.UnordList;
import com.devbaltasarq.nottakapp.core.converter.elements.OrdList;
import com.devbaltasarq.nottakapp.core.converter.elements.Ref;
import com.devbaltasarq.nottakapp.core.converter.elements.Img;

import java.util.Map;
import java.util.Set;


/** An parser for MarkDown input.
  * @author baltasarq
  */
public class MDParser extends Parser {
    private final static char OPEN_CHK = '[';
    private final static char CLOSE_CHK = ']';

    public MDParser(String text)
    {
        this( new MDScanner( text ) );
    }
    
    public MDParser(MDScanner scan)
    {
        super( scan );
    }
    
    /** @return the scanner in use. */
    @Override
    public MDScanner getScanner()
    {
        return (MDScanner) super.getScanner();
    }
    
    @Override
    public Root parse() throws ParseException
    {
        Element current = this.getRoot();
        
        while ( !this.getScanner().isEod() ) {
            current = this.parseNext( current );
        }
        
        return this.getRoot();
    }
    
    /** Parses plain text.
      * @param current the current element.
      * @throws ParseException if the corresponding element cannot be created.
      */
    private Element parseText(Element current) throws ParseException
    {
        final var SCAN = this.getScanner();
        String text = SCAN.readPlainText();
                
        if ( !text.isEmpty() ) {
            current.add( Element.createFrom(
                                            Text.NAME,
                                            text ));
        }
        
        return current;
    }
    
    /** Parses an italic format mark.
      * @param current the current element.
      * @return the new current element.
      */
    private Element parseItalic(Element current) throws ParseException
    {
        if ( current instanceof Italic ) {
            current = current.getParent();
        } else {
            var italicElto = Element.createFrom( Italic.NAME, "" );
            current.add( italicElto );
            current = italicElto;
        }

        this.getScanner().match( MDScanner.ITALIC_FORMATTER );
        return current;
    }
    
    /** Parses a bold format mark.
      * @param current the current element.
      * @return the new current element.
      */
    private Element parseBold(Element current) throws ParseException
    {
        if ( current instanceof Bold ) {
            current = current.getParent();
        } else {
            var boldElt = Element.createFrom( Bold.NAME, "" );
            current.add( boldElt );
            current = boldElt;
        }

        this.getScanner().match( MDScanner.BOLD_FORMATTER );
        return current;
    }
    
    /** Parses a web reference. Is the URL what remains to be read.
      * @param LBL the label of the reference.
      * @throws ParseException if parsing goes wrong.
      * @return the element with the web ref.
      */
    private Element parseWebRef(final String LBL) throws ParseException
    {
        final var SCAN = this.getScanner();
        final String URL = SCAN.readURL();
        final var TORET = new HtmlRef(
                                new ElementDto(
                                        HtmlRef.NAME,
                                        "",
                                        Map.of( "href", URL ) ) );
        
        if ( !SCAN.match( MDScanner.CLOSE_HREF ) ) {
            throw new ParseException( "missing ')' to end the URL" );
        }
        
        TORET.add( new Text( new ElementDto( Text.NAME, LBL ) ));
        return TORET;
    }
    
    /** Parses a wiki reference. Is the URL what remains to be read.
      * @param LBL the label of the reference.
      * @throws ParseException if parsing goes wrong.
      * @return the element with the web ref.
      */
    private Element parseWikiRef(final String LBL) throws ParseException
    {
        final var SCAN = this.getScanner();
        
        final String FILE = SCAN.readFileName();        
        final var TORET = new Ref(
                                new ElementDto(
                                        Ref.NAME,
                                        "",
                                        Map.of( "href", FILE ) ) );
        
        if ( !SCAN.match( MDScanner.CLOSE_WIKI_REF ) ) {
            throw new ParseException( "missing ')' to end the URL" );
        }
        
        TORET.add( new Text( new ElementDto( Text.NAME, LBL ) ));
        return TORET;
    }
    
    /** Reads the next '[' (and the following text) as text.
      * @param current the current element from which to hang the new text.
      * @throws ParseException if anything goes wrong.
     */
    private void parseOpenRefAsText(Element current) throws ParseException
    {
        final StringBuilder TORET = new StringBuilder();
        final MDScanner SCAN = this.getScanner();
        
        TORET.append( SCAN.readChar() );
        
        if ( SCAN.getNextTokenType() == MDScanner.TokenType.SPACE ) {
            TORET.append( ' ' );
        }
        
        SCAN.skipSpaces();
        TORET.append( SCAN.readPlainText() );
        current.add( Element.createFrom( Text.NAME, TORET.toString() ) );
    }
    
    /** Parse a wiki or a web ref.
      * @param current the current element.
      * @return the new current element.
      */
    private Element parseRef(Element current) throws ParseException
    {
        final var DELIMS = Set.of( MDScanner.CLOSE_HREF_LABEL.charAt( 0 ),
                           MDScanner.WIKI_REF_SEPARATOR );
        final var SCAN = this.getScanner();
        final int OLD_POS = SCAN.getPos();
        
        // Pass over the '['
        SCAN.skip();
        
        final String LBL = SCAN.readVerbatimUntil( DELIMS );
        
        // Parse both kinds of refs
        if ( SCAN.match( MDScanner.CLOSE_HREF_LABEL ) ) {
            current.add( this.parseWebRef( LBL ) );
        }
        else
        if ( SCAN.match( MDScanner.WIKI_REF_SEPARATOR ) ) {
            current.add( this.parseWikiRef( LBL ) );
        } else {
            SCAN.moveTo( OLD_POS );
            this.parseOpenRefAsText( current );
        }
        
        return current;
    }
    
    /** Main function for each possible input.
      * @param current the current element.
      * @return the new current element.
      */
    private Element parseNext(Element current) throws ParseException
    {
        final var SCAN = this.getScanner();
        final var NEXT_TOKEN = SCAN.getNextTokenType();
        
        SCAN.skipSpaces();

        switch ( NEXT_TOKEN ) {
            case TEXT -> {
                current = this.parseText( current );
            }
            case CR -> {
                current = this.parseCR( current );
            }
            case SPACE -> {
                SCAN.skipSpaces();
            }
            case BOLD_FORMATTER -> {
                current = this.parseBold( current );
            }
            case ITALIC_FORMATTER -> {
                current = this.parseItalic( current );
            }
            case HEADING -> {
                current = this.parseHeading( current );
            }
            case UNORDERED_LIST_ENTRY -> {
                current = this.parseUnordListEntry( current );
            }
            case ORDERED_LIST_ENTRY -> {
                current = this.parseOrdListEntry( current );
            }
            case CHECK_BOX_FALSE -> {
                current = this.parseCheckbox( current );
            }
            case CHECK_BOX_TRUE -> {
                current = this.parseCheckbox( current );
            }   
            case OPEN_REF -> {
                current = this.parseRef( current );
            }
            case OPEN_IMG -> {
                this.parseImg( current );
            }
        }
        
        return current;
    }
    
    /** Reads the next checkbox, of the form [x] or [X].
      * @return true if it is [x] or [X], false if it is [ ]
      * @throws ParseException if does not comply.
      */
    public boolean readCheckbox() throws ParseException
    {
        final var SCAN = this.getScanner();
        boolean toret = false;
        
        SCAN.skipSpaces();
        if ( SCAN.match( OPEN_CHK ) ) {
            SCAN.skipSpaces();

            if ( SCAN.match( CLOSE_CHK ) ) {
                toret = false;
            } else {
                if ( Character.toLowerCase( SCAN.readChar() ) == 'x' ) {
                    toret = true;
                }
                
                SCAN.skipSpaces();
                if ( !SCAN.match( CLOSE_CHK ) ) {
                    throw new ParseException( "expecting ']'" );
                }
            }
        } else {
            throw new ParseException( "expecting ']'" );
        }
        
        return toret;
    }
    
    /** Parses a checkbox, of the form [ ], [x] or [X].
      * @param current the current element.
      * @return the new current element.
      */
    private Element parseCheckbox(Element current) throws ParseException
    {
        boolean activated = this.readCheckbox();
        var chk = new ElementDto( Chk.NAME, "" );
        
        if ( activated ) {
            chk.setAttr( Chk.ETQ_CHECKED );
        }
        
        current.add( Element.createFrom( chk ) );
        return current;
    }
    
    /** Parses a heading, as in `# heading`, for instance.
      * @param current the current element.
      */
    private Element parseHeading(Element current)
    {
        final var SCAN = this.getScanner();
        int headingLevel = 1;

        // '#' must be the first token in line
        if ( !SCAN.isStartOfLine() ) {
            return current;
        }

        // Parse heading level
        SCAN.skip();
        while( SCAN.getCurrentChar() == MDScanner.HEADING ) {
            headingLevel += 1;
            SCAN.skip();
        }

        // Are we in an unneeded par?
        if ( current instanceof Par
          && current.count() == 0 )
        {
            var p = current;
            current = current.getParent();
            current.remove( p );
        }
        
        // Go up
        if ( current != this.getRoot() ) {
            current = current.getParent();
            
            if ( current instanceof Head ) {
                current = current.getParent();
            }
        }
        
        // Parse the heading text
        final String HEADING_TEXT = SCAN.readVerbatimUntil( MDScanner.CR );
        SCAN.skip();

        // Create the heading
        final var ELTO = new ElementDto( Head.NAME, HEADING_TEXT );
        ELTO.setAttr( Head.ETQ_LEVEL, "" + headingLevel );
        
        final var TORET = new Head( ELTO );
        
        current.add( TORET );
        current = TORET;
        return current;
    }
    
    /** Parses an entry of an unordered list.
      * @param current the current element.
      * @return the new current element, probably a new Entry.
      * @throws ParseException if creating the new element goes wrong.
     */
    private Element parseUnordListEntry(Element current)
                        throws ParseException
    {
        final var SCAN = this.getScanner();
        
        // '-' must be the first token in line
        if ( !SCAN.isStartOfLine() ) {
            return current;
        }
        
        // Pass over '-'
        SCAN.match( MDScanner.UNORDERED_LIST_ENTRY );
        
        // Are we under an 'ul'?
        if ( ! ( current instanceof UnordList ) ) {
            final var UL = Element.createFrom( UnordList.NAME, "" );
            
            if ( current instanceof Par
              && current.count() == 0 )
            {
                Element par = current;
                current = current.getParent();
                current.remove( par );
            }
            
            current.add( UL );
            current = UL;
        }
        
        // Add the entry element
        final var LI = Element.createFrom( Entry.NAME, "" );
        current.add( LI );
        current = LI;
        return current;
    }
    
    /** Parses an entry of an ordered list.
      * @param current the current element.
      * @return the new current element, probably a new Entry.
      * @throws ParseException if creating the new element goes wrong.
     */
    private Element parseOrdListEntry(Element current)
                        throws ParseException
    {
        final var SCAN = this.getScanner();
        
        // "1." must be the first token in line
        if ( !SCAN.isStartOfLine() ) {
            return current;
        }
        
        // Pass over "1."
        if ( !SCAN.matchOrdListEntryPrefix() ) {
            throw new ParseException( "expected '1.' or similar entry" );
        }
        
        // Are we under an 'ol'?
        if ( !current.getName().equals( OrdList.NAME ) ) {
            final var OL = Element.createFrom( OrdList.NAME, "" );
            
            if ( current instanceof Par
              && current.count() == 0 )
            {
                Element par = current;
                current = current.getParent();
                current.remove( par );
            }
            
            current.add( OL );
            current = OL;
        }
        
        // Add the entry element
        final var LI = Element.createFrom( Entry.NAME, "" );
        current.add( LI );
        current = LI;
        return current;
    }
    
    /** Parses a CR. This involves "ending" (going up) current Par,
      * and creating another one.
      * @param current the current element, probably a Par.
      *                but it could be and Entry, so in that case
      *                we just "end" it going up.
      * @return the new current element.
      * @throws if createForm fails.
      */
    private Element parseCR(Element current)
                        throws ParseException
    {
        final var SCAN = this.getScanner();
        
        if ( !( current instanceof Entry ) ) {
            // Create another par
            var par = Element.createFrom( Par.NAME, "" );
            
            if ( !( current instanceof Root ) ) {
                current = current.getParent();
            }

            current.add( par );
            current = par;
        } else {
            // This entry is done, go up to ul or ol.
            current = current.getParent();
        }

        SCAN.skip();        
        return current;
    }
    
    /** Parses an imag, like ![test image](image.jpg)
      * @throws if parsing fails.
      */
    private void parseImg(Element current) throws ParseException
    {
        final var SCAN = this.getScanner();
        
        if ( !SCAN.match( MDScanner.OPEN_IMG ) ) {
            throw new ParseException( "missing '!['" );
        }
        
        final String TITLE = SCAN.readVerbatimUntil(
                                MDScanner.CLOSE_WIKI_REF );
        
        if ( !SCAN.match( MDScanner.CLOSE_HREF_LABEL ) ) {
            throw new ParseException( "missing ']('" );
        }
        
        final String URL = SCAN.readURL();
        final var TORET = new Img(
                                new ElementDto(
                                        Img.NAME,
                                        TITLE,
                                        Map.of( Img.LBL_SRC, URL,
                                                    Img.LBL_TITLE, TITLE ) ) );
        
        if ( !SCAN.match( MDScanner.CLOSE_HREF ) ) {
            throw new ParseException( "missing ')' to end the URL" );
        }
        
        current.add( TORET );
    }
}
