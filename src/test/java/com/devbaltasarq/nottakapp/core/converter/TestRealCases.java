// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter;

import com.devbaltasarq.nottakapp.core.converter.elements.Chk;
import com.devbaltasarq.nottakapp.core.converter.elements.Entry;
import com.devbaltasarq.nottakapp.core.converter.elements.Root;
import com.devbaltasarq.nottakapp.core.converter.elements.Text;
import com.devbaltasarq.nottakapp.core.converter.elements.UnordList;
import com.devbaltasarq.nottakapp.core.converter.html.HtmlParser;
import com.devbaltasarq.nottakapp.core.converter.markdown.MDParser;
import com.devbaltasarq.nottakapp.core.converter.runners.HtmlRunner;
import com.devbaltasarq.nottakapp.core.converter.runners.MarkDownRunner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;


/** A few real cases.
  * @author baltasarq
  */
public class TestRealCases {
    private void chkTestElementsListOfChks(
                            final String TEXT,
                            final String[] TEXTS,
                            final Root ROOT)
    {
        assertEquals( 2, ROOT.count() );
        assertTrue( ROOT.get( 0 ) instanceof Text );
        assertEquals( TEXTS[ 0 ], ROOT.get( 0 ).getContents() );
        assertTrue( ROOT.getLast() instanceof UnordList );
        
        final var LIST = (UnordList) ROOT.getLast();
        
        assertTrue( LIST.get( 0 ) instanceof Entry );
        assertTrue( LIST.getLast() instanceof Entry );
        
        assertTrue( LIST.get( 0 ).get( 0 ) instanceof Chk );
        assertTrue( LIST.get( 0 ).getLast() instanceof Text );
        
        assertTrue( LIST.get( 1 ).get( 0 ) instanceof Chk );
        assertTrue( LIST.get( 1 ).getLast() instanceof Text );
        
        assertEquals( TEXTS[ 1 ], LIST.get( 0 ).getLast().getContents() );
        assertEquals( TEXTS[ 2 ], LIST.get( 1 ).getLast().getContents() );
        
        final var RUNNER = new MarkDownRunner( ROOT );
        RUNNER.run();
        
        final String RESULT_TEXT = RUNNER.toString().trim();
        assertEquals( TEXT, RESULT_TEXT );
    }
    
    @Test
    public void testRealCaseListOfChks()
    {
        final String[] TEXTS = {
                            "Tareas:",
                            "Ropa del club.",
                            "Crear el HtmlRunner de NottakApp." };
        final String TEXT_MD_FMT = "%s\n - [X] %s\n - [ ] %s";
        final String TEXT_HTML_FMT =
                        "<html><body>%s<ul>"
                        + "<li><input type=\"checkbox\" checked/> %s</li>"
                        + "<li><input type=\"checkbox\" /> %s</li>"
                        + "</ul></body></html>";
        final String TEXT = String.format( TEXT_MD_FMT, (Object[]) TEXTS );
        
        // From Markdown to HTML
        final var MD_PARSER = new MDParser( TEXT );

        try {
            MD_PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        this.chkTestElementsListOfChks( TEXT, TEXTS, MD_PARSER.getRoot() );

        final var HTML_RUNNER = new HtmlRunner( MD_PARSER.getRoot() );
        HTML_RUNNER.run();
        final var HTML_RESULTING_TEXT = HTML_RUNNER.toString();
        final var HTML_TEXT = String.format( TEXT_HTML_FMT, (Object[]) TEXTS );
        
        assertEquals(  HTML_TEXT, HTML_RESULTING_TEXT );
        
        // From HTML to Markdown        
        final var HTML_RES_PARSER = new HtmlParser( HTML_RESULTING_TEXT );
        
        try {
            HTML_RES_PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        this.chkTestElementsListOfChks( TEXT, TEXTS, HTML_RES_PARSER.getRoot() );
        
        final var MD_RES_RUNNER = new MarkDownRunner( HTML_RES_PARSER.getRoot() );
        MD_RES_RUNNER.run();
        final var MD_RES_RUNNER_TEXT = MD_RES_RUNNER.toString();
        assertEquals(  TEXT + "\n", MD_RES_RUNNER_TEXT );
    }
}
