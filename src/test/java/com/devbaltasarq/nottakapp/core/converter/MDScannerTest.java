// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter;


import com.devbaltasarq.nottakapp.core.converter.markdown.MDScanner;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/** Scanner for MarkDown.
  * @author baltasarq
  */
public class MDScannerTest {
    private static final String TEXT_THIS = "This";
    private static final String TEXT_IS = "is";
    private static final String TEXT_A = "a";
    private static final String TEXT_TEST = "test";

    
    @Test
    public void testSimpleText()
    {
        final String TEXT = "This is a text.";
        final var SCAN = new MDScanner( TEXT );
        
        assertEquals( TEXT, SCAN.readPlainText() );
    }
    
    @Test
    public void testWithSpaces()
    {
        final String TEXT_WITH_SPACES = TEXT_THIS
                                            + "    "
                                            + TEXT_IS
                                            + "\t"
                                            + TEXT_A
                                            + "    \t   " + TEXT_TEST;
        final String TEXT = TEXT_THIS + " " + TEXT_IS + " " + TEXT_A + " " + TEXT_TEST;
        final var SCAN = new MDScanner( TEXT_WITH_SPACES );
        
        assertEquals( TEXT, SCAN.readPlainText() );
    }
    
    @Test
    public void testFormatters()
    {
        final String TEXT = TEXT_THIS
                            + " __" + TEXT_IS + "__"
                            + TEXT_A
                            + " **" + TEXT_TEST + "**";
        final var SCAN = new MDScanner( TEXT );

        assertEquals( TEXT_THIS, SCAN.readPlainText() );
        assertEquals( MDScanner.TokenType.ITALIC_FORMATTER,
                        SCAN.getNextTokenType() );
        
        if ( !SCAN.match( "__" ) ) {
            fail( "scanner should be at the beginning '__'" );
        }
        
        assertEquals( TEXT_IS, SCAN.readPlainText() );
        
        if ( !SCAN.match( "__" ) ) {
            fail( "scanner should be at the ending '__'" );
        }
        
        assertEquals( TEXT_A, SCAN.readPlainText() );
        
        assertEquals( MDScanner.TokenType.BOLD_FORMATTER, SCAN.getNextTokenType() );
        if ( !SCAN.match( "**" ) ) {
            fail( "scanner should be at the beginning '**'" );
        }
        
        assertEquals( TEXT_TEST, SCAN.readPlainText() );
        
        if ( !SCAN.match( "**" ) ) {
            fail( "scanner should be at the ending '**'" );
        }
    }
    
    @Test
    public void testWithPars()
    {
        final String TEXT = TEXT_THIS
                            + "\n" + TEXT_IS + " " + TEXT_A + " "
                            + "\n" + TEXT_TEST;
        final var SCAN = new MDScanner( TEXT );
        
        assertEquals( TEXT_THIS, SCAN.readPlainText() );
        
        assertEquals( MDScanner.TokenType.CR, SCAN.getNextTokenType() );
        SCAN.readChar();

        assertEquals( TEXT_IS + " " + TEXT_A, SCAN.readPlainText() );
        
        assertEquals( MDScanner.TokenType.CR, SCAN.getNextTokenType() );
        SCAN.readChar();
        
        assertEquals( TEXT_TEST, SCAN.readPlainText() );
    }
    
    @Test
    public void testWikiRef()
    {
        final String FILE = "a123456.md";
        final String TEXT = TEXT_THIS + " " + TEXT_IS + " " + TEXT_A
                            + " [" + TEXT_TEST + "|" + FILE + "].";
        final var SCAN = new MDScanner( TEXT );

        assertEquals( TEXT_THIS + " " + TEXT_IS + " " + TEXT_A, SCAN.readPlainText() );
        assertEquals(MDScanner.TokenType.OPEN_REF, SCAN.getNextTokenType() );
        SCAN.skip();
        assertEquals( TEXT_TEST, SCAN.readPlainText() );
        assertEquals( MDScanner.TokenType.WIKI_REF_SEPARATOR, SCAN.getNextTokenType() );
        SCAN.skip();
        assertEquals( FILE, SCAN.readFileName() );
        assertEquals(MDScanner.TokenType.CLOSE_WIKI_REF, SCAN.getNextTokenType() );
        SCAN.skip();
        assertEquals( ".", SCAN.readPlainText() );
    }
    
    @Test
    public void testHRef()
    {
        final String URL = "http://www.wkikipedia.es";
        final String TEXT = TEXT_THIS + " " + TEXT_IS + " " + TEXT_A
                            + " [" + TEXT_TEST + "](" + URL + ").";
        final var SCAN = new MDScanner( TEXT );

        assertEquals( TEXT_THIS + " " + TEXT_IS + " " + TEXT_A, SCAN.readPlainText() );
        assertEquals(MDScanner.TokenType.OPEN_REF, SCAN.getNextTokenType() );
        SCAN.skip();
        assertEquals( TEXT_TEST, SCAN.readPlainText() );
        assertEquals( MDScanner.TokenType.CLOSE_HREF_LABEL, SCAN.getNextTokenType() );
        
        if ( !SCAN.match( "](" ) ) {
            fail( "expected reading HREF: ']('" );
        }
        
        assertEquals( URL, SCAN.readURL() );
        assertEquals(MDScanner.TokenType.TEXT, SCAN.getNextTokenType() );
        assertEquals( ").", SCAN.readPlainText() );
    }
    
    @Test
    public void testUl()
    {
        final String TEXT = "This is a test.";
        final var SCAN = new MDScanner( " - " + TEXT );
        
        assertEquals( MDScanner.TokenType.UNORDERED_LIST_ENTRY, SCAN.getNextTokenType() );
        assertTrue( SCAN.match( '-' ) );
        assertEquals( TEXT, SCAN.readPlainText() );
    }
    
    @Test
    public void testFalseUl()
    {
        final String TEXT = "This: 5 - 4, is a test.";
        final var SCAN = new MDScanner( TEXT );
        
        assertEquals( MDScanner.TokenType.TEXT, SCAN.getNextTokenType() );
        assertEquals( TEXT, SCAN.readPlainText() );
    }
    
    @Test
    public void testOl()
    {
        final String TEXT = "This is a test.";
        final var SCAN = new MDScanner( " 1. " + TEXT );
        
        assertEquals( MDScanner.TokenType.ORDERED_LIST_ENTRY, SCAN.getNextTokenType() );
        assertTrue( SCAN.match( "1." ) );
        assertEquals( TEXT, SCAN.readPlainText() );
    }
    
    @Test
    public void testFalseOl()
    {
        final String TEXT = "This: 5.4, is a test.";
        final var SCAN = new MDScanner( TEXT );
        
        assertEquals( MDScanner.TokenType.TEXT, SCAN.getNextTokenType() );
        assertEquals( TEXT, SCAN.readPlainText() );
    }
    
    @Test
    public void testHeading()
    {
        final String TEXT = "This is a test heading";
        final var SCAN = new MDScanner( " # " + TEXT );
        
        assertEquals( MDScanner.TokenType.HEADING, SCAN.getNextTokenType() );
        
        if ( !SCAN.match( MDScanner.HEADING ) ) {
            fail( "missing heading mark" );
        }
        
        assertEquals( TEXT, SCAN.readPlainText() );
    }
    
    @Test
    public void testFalseHeading()
    {
        final String TEXT = "This is priority #1 when testing a heading";
        final var SCAN = new MDScanner( TEXT );
        
        assertEquals( MDScanner.TokenType.TEXT, SCAN.getNextTokenType() );        
        assertEquals( TEXT, SCAN.readPlainText() );
    }
}
