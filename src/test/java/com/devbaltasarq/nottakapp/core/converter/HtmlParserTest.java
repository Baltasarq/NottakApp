// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter;


import com.devbaltasarq.nottakapp.core.converter.html.HtmlScanner;
import com.devbaltasarq.nottakapp.core.converter.html.HtmlParser;
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
import com.devbaltasarq.nottakapp.core.converter.elements.Chk;
import com.devbaltasarq.nottakapp.core.converter.elements.Head;
import java.util.Map;


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
                                           <li><i>This is a test item.</i></li>
                                           <li><i>Another test item.</i></li>
                                           <li><i>Yet another test item.</i></li>
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
            final Root ROOT = PARSER.parseBody();
            
            assertEquals( 1, ROOT.count() );
            assertTrue( ROOT.get( 0 ) instanceof Text );
            assertEquals( INPUT_TEXT, ROOT.get( 0 ).getContents() );
            
        } catch(ParseException exc) {
            fail( "[ERR] " + exc.getMessage() );
        }
    }
    
    @Test
    public void testGetNextTag()
    {
        final var SCANNER = this.parser1.getScanner();
        
        assertEquals( "p", SCANNER.getNextTagName() );
        assertEquals( 0, SCANNER.getPos() );
    }
    
    @Test
    public void testReadWholeTag()
    {
        final var SCANNER = this.parser1.getScanner();
        
        assertEquals( "p", SCANNER.getNextTagName() );
        
        try {
            assertEquals( "Hola",
                            this.parser1.parseWholeTag().getContents() );
            assertTrue( SCANNER.isEod() );
        } catch(ParseException exc) {
            fail( "[ERR] " + exc.getMessage() );
        }
    }
    
    @Test
    public void testComplexTag()
    {
        final String ATTR_NAME = "Style";
        final String ATTR_VALUE = "margin-top: 0";
        final String ATTR = ATTR_NAME + " = " + '"' + ATTR_VALUE + '"';
        final var SCANNER = new HtmlScanner( "<p " + ATTR + ">test</p>" );
        final var PARSER = new HtmlParser( SCANNER );
        
        try {
            final var TAG = PARSER.parseWholeTag();
            final Map<String, String> ATTRS = TAG.getAttributes();

            assertEquals( "p", TAG.getName() );
            assertEquals( "test", TAG.getContents() );
            assertEquals( ATTR_VALUE, ATTRS.get( ATTR_NAME.toLowerCase() ) );
            assertFalse( ATTRS.containsKey( HtmlScanner.LBL_ERROR ) );
            assertFalse( ATTRS.containsKey( HtmlScanner.LBL_TAG_NAME ) );
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
            assertEquals( "Test", this.parserDocComplete.parseHead() );
            assertEquals( "body", SCANNER.getNextTagName() );
        } catch(ParseException exc) {
            fail( "[ERR] Parse exception: " + exc );
        } catch(Exception exc) {
            fail( "[ERR] General exception: " + exc );
        }
    }
    
    @Test
    public void testReadBodyDocComplete()
    {
        try {
            Root root = this.parserDocComplete.parseBody();
            
            assertNotNull( root );
            assertEquals( 3, root.count() );
            
            for(int i = 0; i < root.count(); ++i) {
                final var ELTO = root.get( i );
                
                assertTrue( ELTO instanceof Par );
                
                if ( ELTO.isClosing() ) {
                    continue;
                }
                
                switch ( i ) {
                    case 0 -> {
                        // Paragraph with italics at the end.
                        assertEquals( 3, ELTO.count() );

                        // "First"
                        assertTrue( ELTO.get( 0 ) instanceof Text );
                        assertEquals( "First", ELTO.get( 0 ).getContents() );
                        
                        // "<i>paragraph</i>"
                        assertTrue( ELTO.get( 1 ) instanceof Italic );
                        assertEquals( 1, ELTO.get( 1 ).count() );
                        assertTrue( ELTO.get( 1 ).getLast() instanceof Text );
                        assertEquals( "paragraph", ELTO.get( 1 ).getLast().getContents() );
                        
                        // "."
                        assertTrue( ELTO.getLast() instanceof Text );
                        assertEquals( ".", ELTO.getLast().getContents() );
                    }
                    case 1 -> {
                        // Paragraph with bold at the beginning.
                        assertEquals( 2, ELTO.count() );
                        
                        // <b>Second</b>
                        assertTrue( ELTO.get( 0 ) instanceof Bold );
                        assertEquals( 1, ELTO.get(  0 ).count() );
                        assertTrue( ELTO.get( 0 ).getLast() instanceof Text );
                        assertEquals( "Second", ELTO.get( 0 ).getLast().getContents() );
                        
                        assertTrue( ELTO.get( 1 ) instanceof Text );
                        assertEquals( "paragraph.", ELTO.get( 1 ).getContents() );
                    }
                    case 2 -> {
                        // Paragraph with bold at the beginning.
                        assertEquals( 1, ELTO.count() );
                        assertTrue( ELTO.getLast() instanceof Text );
                        assertEquals( "Third paragraph.", ELTO.getLast().getContents() );
                    }
                    default -> {
                        fail( "[ERR] unexpected subelement" );
                    }
                }
            }
        } catch(ParseException exc) {
            fail( "[ERR] Parse exception: " + exc );
        } catch(Exception exc) {
            fail( "[ERR] General exception: " + exc );
        }
    }
    
    @Test
    public void testReadBodyDocWithList()
    {
        try {
            final Root ROOT = this.parserDocWithList.parseBody();
            
            assertNotNull( ROOT );
            assertEquals( 1, ROOT.count() );
            
            for(Element e: ROOT.getAll()) {
                assertTrue( e instanceof UnordList );
                assertEquals( 3, e.count() );

                for(Element li_e: e.getAll()) {
                    assertTrue( li_e instanceof Entry );
                    assertEquals( 1, li_e.count() );
                    assertEquals( "i", li_e.getLast().getName() );
                    final var TEXT = li_e.getLast().getLast();
                    assertTrue( TEXT.getContents().contains( "test") );
                    assertTrue( TEXT.getContents().contains( "item") );
                }
            }
        } catch(ParseException exc) {
            fail( "[ERR] Parse exception: " + exc );
        } catch(Exception exc) {
            fail( "[ERR] General exception: " + exc );
        }
    }
    
    @Test
    public void testImg()
    {
        final String IMG_PATH = "test_foto.jpg";
        final String IMG_TAG = "<img src=\"" + IMG_PATH + "\">";
        final String IMG_TAG2 = "<img src=\"" + IMG_PATH + "\"/>";
        final String HTML1 = "<html><head></head><body>" + IMG_TAG + "</body></html>";
        final String HTML2 = "<html><head></head><body>" + IMG_TAG2 + "</body></html>";
        final var PARSER1 = new HtmlParser( new HtmlScanner ( HTML1 ) );
        final var PARSER2 = new HtmlParser( new HtmlScanner ( HTML2 ) );
        
        try {
            final var ROOT1 = PARSER1.parseBody();
            final var ROOT2 = PARSER2.parseBody();
            
            var elt1 = ROOT1.getLast();
            var elt2 = ROOT2.getLast();
            
            assertEquals( "img", elt1.getName() );
            assertTrue( elt1.getAttributes().containsKey( "src" ) );
            assertEquals( IMG_PATH, elt1.getAttributes().get( "src" ) );
            
            assertEquals( "img", elt2.getName() );
            assertTrue( elt2.getAttributes().containsKey( "src" ) );
            assertEquals( IMG_PATH, elt2.getAttributes().get( "src" ) );
        } catch(ParseException exc) {
            fail( "[ERR] " + exc.getMessage() );
        }
    }
    
    @Test
    public void testWikiRef()
    {
        final String FILE = "abcdef123456.md";
        final String TITLE = "test title";
        final String A_TAG = "<a href=\"" + FILE + "\" target=\"_blank\">" + TITLE + "</a>";
        final String HTML = "<html><head></head><body>" + A_TAG + "</body></html>";
        final var PARSER = new HtmlParser( new HtmlScanner ( HTML ) );
        
        try {
            final var ROOT = PARSER.parseBody();
            
            var elt = ROOT.getLast();
            
            assertEquals( "a", elt.getName() );
            assertEquals( FILE, elt.getAttributes().get( "href" ) );
            assertEquals( TITLE, elt.getLast().getContents() );
        } catch(ParseException exc) {
            fail( "[ERR] " + exc.getMessage() );
        }
    }
    
    @Test
    public void testHRef()
    {
        final String URL = "http://wikipedia.es";
        final String TITLE = "Wikipedia";
        final String A_TAG = "<a href=\"" + URL + "\" target=\"_blank\">" + TITLE + "</a>";
        final String HTML = "<html><head></head><body>" + A_TAG + "</body></html>";
        final var PARSER = new HtmlParser( new HtmlScanner ( HTML ) );
        
        try {
            final var ROOT = PARSER.parseBody();
            
            var elt = ROOT.getLast();
            
            assertEquals( "a", elt.getName() );
            assertEquals( URL, elt.getAttributes().get( "href" ) );
            assertEquals( TITLE, elt.getLast().getContents() );
        } catch(ParseException exc) {
            fail( "[ERR] " + exc.getMessage() );
        }
    }
    
    @Test
    public void testCheckbox()
    {
        final String TEXT = "Do the chores";
        final String CHK_TAG1 = "<input type=\"checkbox\"/> " + TEXT;
        final String CHK_TAG2 = "<input type=\"checkbox\" checked> " + TEXT;
        final String HTML = "<html><head></head><body>"
                                + CHK_TAG1
                                + CHK_TAG2
                                + "</body></html>";
        final var PARSER = new HtmlParser( new HtmlScanner ( HTML ) );
        
        try {
            final var ROOT = PARSER.parseBody();
            
            var elt1 = ROOT.get( 0 );
            
            assertEquals( "input", elt1.getName() );
            assertEquals( "checkbox", elt1.getAttributes().get( "type" ) );
            
            var chk1 = (Chk) elt1;
            assertFalse( chk1.isActivated() );
            assertFalse( chk1.containsAttr( Chk.ETQ_CHECKED ) );
            
            assertEquals( TEXT, ROOT.get( 1 ).getContents() );

            var elt2 = ROOT.get( 2 );
            assertEquals( "input", elt2.getName() );
            assertEquals( "checkbox", elt2.getAttributes().get( "type" ) );
            
            var chk2 = (Chk) elt2;
            assertTrue( chk2.isActivated() );
            assertTrue( chk2.containsAttr( Chk.ETQ_CHECKED ) );
            
            assertEquals( TEXT, ROOT.getLast().getContents() );
        } catch(ParseException exc) {
            fail( "[ERR] " + exc.getMessage() );
        }
    }
    
    @Test
    public void testHeading()
    {
        String html = "<html><body><h2>Test title</h2>"
                        + "<p></p><h3>Test sub</h3></body></html>";
        final var PARSER = new HtmlParser( new HtmlScanner( html ) );
        
        try {
            PARSER.parseBody();
        } catch(ParseException exc) {
            fail( "[ERR] " + exc.getMessage() );
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
        assertEquals( 2, H1.getLevel() );
        assertEquals( 1, H1.count() );
        assertTrue( H1.get( 0 ) instanceof Text );
        assertEquals( "Test title", H1.get( 0 ).getContents() );
        
        // P
        assertEquals( 0, P.count() );
        
        // H2
        assertEquals( 3, H2.getLevel() );
        assertEquals( 1, H2.count() );
        assertTrue( H2.get( 0 ) instanceof Text );
        assertEquals( "Test sub", H2.get( 0 ).getContents() );
    }
    
    private HtmlParser parser1;
    private HtmlParser parserDocComplete;
    private HtmlParser parserDocWithList;
}
