// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;



/** Test the plain text runner.
  * @author baltasarq
  */
public class PlainTextRunnerTest {
    @Test
    public void testSimpleText()
    {
        final String TEXT = "This is a test.";
        final var PARSER = new HtmlParser(
                            new HtmlScanner( "<html><body>"
                                                    + TEXT
                                                    + "</body></html>" ));
        
        try {
            PARSER.readBody();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        final var TEXT_RUNNER = new PlainTextRunner( PARSER.getRoot() );
        TEXT_RUNNER.run();
        
        assertEquals( TEXT, TEXT_RUNNER.toString() );
    }
    
    @Test
    public void testOnePar()
    {
        final String TEXT = "This is a test.";
        final var PARSER = new HtmlParser(
                            new HtmlScanner( "<html><body><p>"
                                                    + TEXT
                                                    + "</p></body></html>" ));
        
        try {
            PARSER.readBody();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        final var TEXT_RUNNER = new PlainTextRunner( PARSER.getRoot() );
        TEXT_RUNNER.run();
        
        assertEquals( "\n" + TEXT, TEXT_RUNNER.toString() );
    }
}
