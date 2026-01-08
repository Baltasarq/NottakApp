// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/** Test the HTML Scanner class.
  * @author baltasarq
  */
public class HtmlScannerTest {
    @BeforeEach
    public void setUp()
    {
        this.scanner1 = new HtmlScanner( """
                            <html><head><title>Hola, mundo!</title></head>
                            <body>Hola!</body></html>
                            """ );
        this.scanner2 = new HtmlScanner( """
                            <html>           
                                <head>
                                    <title>Hola, mundo!</title>
                                </head>
                            <body>
                                Hola!
                            </body>
                            </html>
                            """ );
        this.scanner3 = new HtmlScanner( "text  \t\n  text" );
        this.scanner4 = new HtmlScanner( "&lt;mensaje&gt;" );
    }

    /** Test basic cursor behaviour. */
    @Test
    public void testCursor()
    {
        final String TEXT = this.scanner1.getWholeText();
        
        for(int i = 0; i < this.scanner1.size(); ++i) {
            assertEquals( TEXT.charAt( i ), this.scanner1.getCurrentChar() );
            this.scanner1.skip();
        }
        
        assertEquals( this.scanner1.isEod(), true );
        assertEquals( this.scanner1.countPendingChars(), 0 );
    }

    /** Test HtmlScanner::readText & getNextTokenType. */
    @Test
    public void testSkipSpaces()
    {
        assertEquals( HtmlScanner.TokenType.TEXT, this.scanner3.getTokenType() );
        assertEquals( HtmlScanner.TokenType.TEXT, this.scanner3.getNextTokenType() );
        assertEquals( "text text", this.scanner3.readText() );
    }

    /** Test of HtmlScanner::match(ch) & skipSpaces(). */
    @Test
    public void testMatchChar()
    {
        while( !this.scanner3.isEod() ) {
            this.scanner3.match( 't' );
            this.scanner3.match( 'e' );
            this.scanner3.match( 'x' );
            this.scanner3.match( 't' );
            this.scanner3.skipSpaces();
        }
    }

    /** Test of HtmlScanner::match(String) & skipSpaces(). */
    @Test
    public void testMatchString()
    {
        this.scanner3.match( "text" );
        this.scanner3.skipSpaces();
        this.scanner3.match( "text" );
    }
    
    private void testReadTagFor(HtmlScanner scanner)
    {
        assertEquals( HtmlScanner.TokenType.TAG, scanner.getTokenType() );
        assertEquals( "html", scanner.readTag() );
        assertEquals( "head", scanner.readTag() );
        assertEquals( "title", scanner.readTag() );
        
        assertEquals( "Hola, mundo!", scanner.readText() );
        
        assertEquals( "/title", scanner.readTag() );
        assertEquals( "/head", scanner.readTag() );
        assertEquals( "body", scanner.readTag() );
        
        assertEquals( "Hola!", scanner.readText() );
        
        assertEquals( "/body", scanner.readTag() );
        assertEquals( "/html", scanner.readTag() );
    }

    /** Test of HtmlScanner::readTag(). */
    @Test
    public void testReadTag()
    {
        testReadTagFor( this.scanner1 );
        testReadTagFor( this.scanner2 );
    }

    /** Test of HtmlScanner::readSpecialChar(). */
    @Test
    public void testReadSpecialChar()
    {
        assertEquals( "<mensaje>", this.scanner4.readText() );
    }

    /** Test of HtmlScanner::charFromSpecialName(). */
    @Test
    public void testCharFromSpecialName()
    {
        var specialChars = new String[]{
                "gt", "lt", "ntilde",
                "aacute", "eacute", "iacute", "oacute", "uacute"
        };
        String normalChars = "><ñáéíóú";
        
        assertEquals( specialChars.length, normalChars.length() );
        for(int i = 0; i < normalChars.length(); ++i) {
            assertEquals(
                    normalChars.charAt( i ),
                    HtmlScanner.charFromSpecialName( specialChars[ i ] ));
        }           
    }
    
    @Test
    public void testComplexTag()
    {
        final var SCANNER = new HtmlScanner( "<body><p style=\"margin-top: 0\">test</p></body>" );
        
        assertEquals( "body", SCANNER.readTag() );
        assertEquals( "p", SCANNER.readTag() );
        assertEquals( "test", SCANNER.readText() );
        assertEquals( "/p", SCANNER.readTag() );
        assertEquals( "/body", SCANNER.readTag() );
    }
    
    private HtmlScanner scanner1;
    private HtmlScanner scanner2;
    private HtmlScanner scanner3;
    private HtmlScanner scanner4;
}
