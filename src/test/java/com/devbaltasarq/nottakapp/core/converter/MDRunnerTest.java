// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter;


import com.devbaltasarq.nottakapp.core.converter.runners.MarkDownRunner;
import com.devbaltasarq.nottakapp.core.converter.elements.Root;
import com.devbaltasarq.nottakapp.core.converter.html.HtmlParser;
import com.devbaltasarq.nottakapp.core.converter.markdown.MDParser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;



/** Test the plain text runner.
  * @author baltasarq
  */
public class MDRunnerTest {
    @Test
    public void testSimpleTextFromHtml()
    {
        final String TEXT = "This is a test.";
        final var PARSER = new HtmlParser( "<html><body>"
                                                    + TEXT
                                                    + "</body></html>" );
        
        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        this.testSimpleText( TEXT, PARSER.getRoot() );
    }
    
    @Test
    public void testSimpleTextFromMD()
    {
        final String TEXT = "This is a test.";
        final var PARSER = new MDParser( TEXT );
        
        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        this.testSimpleText( TEXT, PARSER.getRoot() );
    }

    private void testSimpleText(final String TEXT, final Root ROOT)
    {
        
        final var TEXT_RUNNER = new MarkDownRunner( ROOT );
        TEXT_RUNNER.run();
        
        // When there is only text, automatically a "p" is added.
        assertEquals( TEXT, TEXT_RUNNER.toString().trim() );
    }
    
    @Test
    public void testWithParsFromHtml()
    {
        final String TEXT = "This is a test.";
        final var PARSER = new HtmlParser( "<html><body>"
                                                    + TEXT
                                                    + "<p>"
                                                    + TEXT
                                                    + "</p></body></html>" );
        
        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        this.testTextWithPars( TEXT, PARSER.getRoot() );
    }
    
    @Test
    public void testWithParsFromMD()
    {
        final String TEXT = "This is a test.";
        final var PARSER = new MDParser( TEXT + "\n" + TEXT + "\n");
        
        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        this.testTextWithPars( TEXT, PARSER.getRoot() );
    }
    
    private void testTextWithPars(final String TEXT, final Root ROOT)
    {
        
        final var TEXT_RUNNER = new MarkDownRunner( ROOT );
        TEXT_RUNNER.run();
        
        final String RESULTING_TEXT = TEXT + "\n" + TEXT;
        assertEquals( RESULTING_TEXT, TEXT_RUNNER.toString().trim() );
    }
    
    @Test
    public void testUlFromHtml()
    {
        final String TEXT = "This is a test.";
        final var PARSER = new HtmlParser( "<html><body><ul><li>"
                                                    + TEXT
                                                    + "</li><li>"
                                                    + TEXT
                                                    + "</li></ul></body></html>" );
        
        try {
            PARSER.parseBody();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        this.testUl( TEXT, PARSER.getRoot() );
    }
    
    @Test
    public void testUlFromMD()
    {
        final String TEXT = "This is a test.";
        final var PARSER = new MDParser( " - " + TEXT
                                            + "\n - " + TEXT );
        
        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        this.testUl( TEXT, PARSER.getRoot() );
    }
    
    private void testUl(final String TEXT, final Root ROOT)
    {
        final var TEXT_RUNNER = new MarkDownRunner( ROOT );
        TEXT_RUNNER.run();
        
        final String RESULTING_TEXT = "- " + TEXT + "\n - " + TEXT;
        assertEquals( RESULTING_TEXT, TEXT_RUNNER.toString().trim() );
    }
    
    @Test
    public void testOlFromHtml()
    {
        final String TEXT = "This is a test.";
        final var PARSER = new HtmlParser( "<html><body><ol><li>"
                                                    + TEXT
                                                    + "</li><li>"
                                                    + TEXT
                                                    + "</li></ol></body></html>" );
        
        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        this.testOl( TEXT.trim(), PARSER.getRoot() );
    }    
    
    @Test
    public void testOlFromMD()
    {
        final String TEXT = "This is a test.";
        final var PARSER = new MDParser( "1. " + TEXT + "\n2. " + TEXT );
        
        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        this.testOl( TEXT.trim(), PARSER.getRoot() );
    }
    
    @Test
    private void testOl(final String TEXT, final Root ROOT)
    {        
        final var TEXT_RUNNER = new MarkDownRunner( ROOT );
        TEXT_RUNNER.run();
        
        final String RESULTING_TEXT = "1. " + TEXT + "\n 2. " + TEXT;
        assertEquals( RESULTING_TEXT, TEXT_RUNNER.toString().trim() );
    }
    
    @Test
    public void testHeadingsFromHtml()
    {
        final String TEXT = "This is a test heading.";
        final var PARSER = new HtmlParser( "<html><body><h2>"
                                                    + TEXT
                                                    + "</h2><h3>"
                                                    + TEXT
                                                    + "</h3></body></html>" );
        
        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        this.testHeadings( TEXT, PARSER.getRoot() );
    }
    
    @Test
    public void testHeadingsFromMD()
    {
        final String TEXT = "This is a test heading.";
        final var PARSER = new MDParser( "## "+ TEXT
                                            + "\n### " + TEXT );
        
        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        this.testHeadings( TEXT, PARSER.getRoot() );
    }
    
    @Test
    private void testHeadings(final String TEXT, final Root ROOT)
    {
        final var TEXT_RUNNER = new MarkDownRunner( ROOT );
        TEXT_RUNNER.run();
        
        final String RESULTING_TEXT = "## " + TEXT + "\n### " + TEXT;
        assertEquals( RESULTING_TEXT, TEXT_RUNNER.toString().trim() );
    }
    
    @Test
    public void testBoldItalicFromHtml()
    {
        final String TEXT = "This is a test.";
        final var PARSER = new HtmlParser( "<html><body><p><b>"
                                                    + TEXT
                                                    + "</b>"
                                                    + TEXT
                                                    + "<i>"
                                                    + TEXT
                                                    + "</i></p></body></html>" );
        
        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        this.testBoldItalic( TEXT, PARSER.getRoot() );
    }
    
     @Test
    public void testBoldItalicFromMD()
    {
        final String TEXT = "This is a test.";
        final var PARSER = new MDParser( "**" + TEXT + "** "
                                            + TEXT + " __" + TEXT + "__" );
        
        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        this.testBoldItalic( TEXT, PARSER.getRoot() );
    }
    
    private void testBoldItalic(final String TEXT, final Root ROOT)
    {
        final var TEXT_RUNNER = new MarkDownRunner( ROOT );
        TEXT_RUNNER.run();
        
        final String RESULTING_TEXT = "**" + TEXT + "** "
                                            + TEXT + " __" + TEXT + "__";
        assertEquals( RESULTING_TEXT, TEXT_RUNNER.toString().trim() );
    }
    
    @Test
    public void testImgFromHtml()
    {
        final String TEXT = "This is a test.";
        final var PARSER = new HtmlParser( "<html><body><p>"
                                                    + TEXT
                                                    + "<img src='a.jpg'"
                                                        + " alt='" + TEXT + "'>"
                                                    + TEXT
                                                    + "</p></body></html>" );
        
        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        this.testImg( TEXT, PARSER.getRoot() );
    }
    
    @Test
    public void testImgFromMD()
    {
        final String TEXT = "This is a test.";
        final var PARSER = new MDParser( TEXT + " ![" + TEXT + "](a.jpg) "
                                            + TEXT );
        
        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        this.testImg( TEXT, PARSER.getRoot() );
    }
    
    private void testImg(final String TEXT, final Root ROOT)
    {
        final var TEXT_RUNNER = new MarkDownRunner( ROOT );
        TEXT_RUNNER.run();
        
        final String RESULTING_TEXT = TEXT + " ![" + TEXT + "](a.jpg) "
                                            + TEXT;
        assertEquals( RESULTING_TEXT, TEXT_RUNNER.toString().trim() );
    }
    
    @Test
    public void testWikiRefFromHtml()
    {
        final String FILE = "a12345.md";
        final String TEXT = "This is a test.";
        final var PARSER = new HtmlParser( "<html><body>"
                                                    + TEXT
                                                    + "<a href='" + FILE + "'>"
                                                    + TEXT + "</a>"
                                                    + TEXT
                                                    + "</body></html>" );
        
        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        this.testWikiRef( FILE, TEXT, PARSER.getRoot() );
    }
    
    @Test
    public void testWikiRefFromMD()
    {
        final String FILE = "a12345.jpg";
        final String TEXT = "This is a test.";
        final var PARSER = new MDParser( TEXT
                                            + " [" + TEXT + "|"
                                            + FILE + "] " + TEXT );
        
        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        this.testWikiRef( FILE, TEXT, PARSER.getRoot() );
    }
    
    private void testWikiRef(final String FILE, final String TEXT, final Root ROOT)
    {
        final var TEXT_RUNNER = new MarkDownRunner( ROOT );
        TEXT_RUNNER.run();
        
        final String RESULTING_TEXT = TEXT
                                            + " [" + TEXT + "|"
                                            + FILE + "] " + TEXT;
        assertEquals( RESULTING_TEXT, TEXT_RUNNER.toString().trim() );
    }
    
    @Test
    public void testHtmlRefFromHtml()
    {
        final String URL = "http://www.wikipedia.es";
        final String TEXT = "This is a test.";
        final var PARSER = new HtmlParser( "<html><body><p>"
                                                    + TEXT
                                                    + "<a href='" + URL + "'>"
                                                    + TEXT + "</a>"
                                                    + TEXT
                                                    + "</p></body></html>" );
        
        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        this.testHtmlRef( URL, TEXT, PARSER.getRoot() );
    }
    
    @Test
    public void testHtmlRefFromMD()
    {
        final String URL = "http://www.wikipedia.es";
        final String TEXT = "This is a test.";
        final var PARSER = new MDParser( TEXT
                                            + " [" + TEXT + "]("
                                            + URL + ") "
                                            + TEXT );
        
        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        this.testHtmlRef( URL, TEXT, PARSER.getRoot() );
    }
    
    private void testHtmlRef(final String URL, final String TEXT, final Root ROOT)
    {
        
        final var TEXT_RUNNER = new MarkDownRunner( ROOT );
        TEXT_RUNNER.run();
        
        final String RESULTING_TEXT = TEXT
                                            + " [" + TEXT + "]("
                                            + URL + ") "
                                            + TEXT;
        assertEquals( RESULTING_TEXT, TEXT_RUNNER.toString().trim() );
    }
    
    @Test
    public void testChkFromHtml()
    {
        final String TEXT = "This is a test.";
        final var PARSER = new HtmlParser( "<html><body>"
                                            + "<input type='checkbox' > "
                                            + TEXT
                                            + "<p><input type='checkbox' checked/> "
                                            + TEXT
                                            + "</p></body></html>");
        
        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        this.testChk( TEXT, PARSER.getRoot() );
    }
    
    @Test
    public void testChkFromMD()
    {
        final String TEXT = "This is a test.";
        final var PARSER = new MDParser( "[ ] " + TEXT
                                         + "\n[X] " + TEXT );
        
        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        this.testChk( TEXT, PARSER.getRoot() );
    }
    
    private void testChk(final String TEXT, final Root ROOT)
    {
        final var TEXT_RUNNER = new MarkDownRunner( ROOT );
        TEXT_RUNNER.run();
        
        final String RESULTING_TEXT = "[ ] " + TEXT
                                      + "\n[X] " + TEXT;
        assertEquals( RESULTING_TEXT, TEXT_RUNNER.toString().trim() );
    }
}
