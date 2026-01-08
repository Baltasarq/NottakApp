// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.devbaltasarq.nottakapp.core.converter.elements.Root;
import com.devbaltasarq.nottakapp.core.converter.elements.Par;
import com.devbaltasarq.nottakapp.core.converter.elements.UnordList;
import com.devbaltasarq.nottakapp.core.converter.elements.Entry;
import com.devbaltasarq.nottakapp.core.converter.elements.Text;
import com.devbaltasarq.nottakapp.core.converter.elements.Italic;
import com.devbaltasarq.nottakapp.core.converter.elements.Bold;
import com.devbaltasarq.nottakapp.core.converter.elements.Head;


/** Tests the Parser for HTML.
  * @author baltasarq
  */
public class HtmlParserTest {
    @BeforeEach
    public void setUp()
    {
        this.parser1 = new HtmlParser( new HtmlScanner(
                                        """
                                                <p>Hola</p>
                                            """ ));
        
        this.parserDocComplete = new HtmlParser( new HtmlScanner( """
                                       <html>
                                       <head>
                                           <title>Test</title>                                  
                                       </head>
                                       <body>
                                           <p>First <i>paragraph</i>.</p>
                                           <p><b>Second</b> paragraph.</p>
                                           <p>Third paragraph.</p> 
                                       </body>
                                       </html>
                                       """ ));
        
        this.parserDocWithList = new HtmlParser( new HtmlScanner( """
                                       <html>
                                       <head>
                                           <title>Test</title>                                  
                                       </head>
                                       <body>
                                        <ul>
                                           <li>First <i>item</i>.</li>
                                           <li><b>Second</b> item.</li>
                                           <li>Third paragraph.</li>
                                        </ul>                    
                                       </body>
                                       </html>
                                       """ ));
    }
    
    @Test
    public void testNoTags()
    {
        final String INPUT_TEXT = "This is a text.";
        final var PARSER = new HtmlParser( new HtmlScanner (
                                                "<html><body>"
                                                    + INPUT_TEXT
                                                    + "</body></html>" ) );
        try {
            final Root ROOT = PARSER.readBody();
            
            assertEquals( 1, ROOT.count() );
            assertTrue( ROOT.getLast() instanceof Par );
            assertTrue( ROOT.getLast().getLast() instanceof Text );
            assertEquals( INPUT_TEXT, ROOT.getLast().getLast().getText() );
            
        } catch(ParseException exc) {
            fail( "[ERR] " + exc.getMessage() );
        }
    }
    
    @Test
    public void testGetNextTag()
    {
        final var SCANNER = this.parser1.getScanner();
        
        assertEquals( "p", SCANNER.getNextTag() );
        assertEquals( 0, SCANNER.getPos() );
    }
    
    @Test
    public void testReadWholeTag()
    {
        final var SCANNER = this.parser1.getScanner();
        
        assertEquals( "p", SCANNER.getNextTag() );
        
        try {
            assertEquals( "Hola", this.parser1.readWholeTag() );
            assertTrue( SCANNER.isEod() );
        } catch(ParseException exc) {
            fail( "[ERR] " + exc.getMessage() );
        }
    }
    
    @Test
    public void testMatchTag()
    {
        assertTrue( this.parser1.matchTag( "p" ) );
        assertEquals( "Hola", this.parser1.getScanner().readText() );
        assertTrue( this.parser1.matchTag( "/p" ) );
        assertTrue( this.parser1.getScanner().isEod() );
    }
    
    @Test
    public void testReadHead()
    {
        final var SCANNER = this.parserDocComplete.getScanner();
        
        try {
            assertEquals( "Test", this.parserDocComplete.readHead() );
            assertEquals( "body", SCANNER.getNextTag() );
        } catch(ParseException exc) {
            fail( "Parse exception: " + exc );
        } catch(Exception exc) {
            fail( "General exception: " + exc );
        }
    }
    
    @Test
    public void testReadBodyDocComplete()
    {
        try {
            Root root = this.parserDocComplete.readBody();
            
            assertNotNull( root );
            assertEquals( 3, root.count() );
            
            for(int i = 0; i < root.count(); ++i) {
                final var ELTO = root.get( i );
                
                assertTrue( ELTO instanceof Par );
                
                switch (i) {
                    case 0 -> {
                        // Paragraph with italics at the end.
                        assertEquals( 3, ELTO.count() );

                        // "First"
                        assertTrue( ELTO.get( 0 ) instanceof Text );
                        assertEquals( "First", ELTO.get( 0 ).getText() );
                        
                        // "<i>paragraph</i>"
                        assertTrue( ELTO.get( 1 ) instanceof Italic );
                        assertEquals( 1, ELTO.get(  1 ).count() );
                        assertTrue( ELTO.get( 1 ).getLast() instanceof Text );
                        assertEquals( "paragraph", ELTO.get( 1 ).getLast().getText() );
                        
                        // "."
                        assertTrue( ELTO.getLast() instanceof Text );
                        assertEquals( ".", ELTO.getLast().getText() );
                    }
                    case 1 -> {
                        // Paragraph with bold at the beginning.
                        assertEquals( 2, ELTO.count() );
                        
                        // <b>Second</b>
                        assertTrue( ELTO.get( 0 ) instanceof Bold );
                        assertEquals( 1, ELTO.get( 0 ).count() );
                        assertTrue( ELTO.get( 0 ).getLast() instanceof Text );
                        assertEquals( "Second", ELTO.get( 0 ).getLast().getText() );
                        
                        // paragraph.
                        assertTrue( ELTO.get( 1 ) instanceof Text );
                        assertEquals( "paragraph.", ELTO.get( 1 ).getText() );
                    }
                    case 2 -> {
                        // Paragraph with bold at the beginning.
                        assertEquals( 1, ELTO.count() );
                        assertTrue( ELTO.getLast() instanceof Text );
                        assertEquals( "Third paragraph.", ELTO.getLast().getText() );
                    }
                    default -> {
                        fail();
                    }
                }
            }
        } catch(ParseException exc) {
            fail( "Parse exception: " + exc );
        } catch(Exception exc) {
            fail( "General exception: " + exc );
        }
    }
    
    @Test
    public void testReadBodyDocWithList()
    {
        try {
            Root root = this.parserDocWithList.readBody();
            
            assertNotNull( root );
            assertEquals( 1, root.count() );
            
            for(Element e: root.getAll()) {
                assertTrue( e instanceof UnordList );
                assertEquals( 3, e.count() );
                for(Element li_e: e.getAll()) {
                    assertTrue( li_e instanceof Entry );
                }
            }
        } catch(ParseException exc) {
            fail( "Parse exception: " + exc );
        } catch(Exception exc) {
            fail( "General exception: " + exc );
        }
    }
    
    @Test
    public void testHeading()
    {
        String html = "<html><body><h1>Test title</h1>"
                        + "<p></p><h2>Test sub</h2></body></html>";
        final var PARSER = new HtmlParser( new HtmlScanner( html ) );
        
        try {
            PARSER.readBody();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        final Root ROOT = PARSER.getRoot();
        
        assertEquals( 3, ROOT.count() );
        assertTrue( ROOT.get( 0 ) instanceof Head );
        assertTrue( ROOT.get( 1 ) instanceof Par );
        assertTrue( ROOT.get( 2 ) instanceof Head );
        
        final var H1 = (Head) ROOT.get( 0 );
        final var P = (Par) ROOT.get( 1 );
        final var H2 = (Head) ROOT.get( 2 );
        
        // H1
        assertEquals( 1, H1.getLevel() );
        assertEquals( 1, H1.count() );
        assertTrue( H1.get( 0 ) instanceof Text );
        assertEquals( "Test title", H1.get( 0 ).getText() );
        
        // P
        assertEquals( 0, P.count() );
        
        // H2
        assertEquals( 2, H2.getLevel() );
        assertEquals( 1, H2.count() );
        assertTrue( H2.get( 0 ) instanceof Text );
        assertEquals( "Test sub", H2.get( 0 ).getText() );
    }
    
    private HtmlParser parser1;
    private HtmlParser parserDocComplete;
    private HtmlParser parserDocWithList;
}
