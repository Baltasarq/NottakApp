// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter;


import com.devbaltasarq.nottakapp.core.converter.elements.Bold;
import com.devbaltasarq.nottakapp.core.converter.elements.Entry;
import com.devbaltasarq.nottakapp.core.converter.elements.Head;
import com.devbaltasarq.nottakapp.core.converter.elements.Italic;
import com.devbaltasarq.nottakapp.core.converter.elements.Par;
import com.devbaltasarq.nottakapp.core.converter.elements.Chk;
import com.devbaltasarq.nottakapp.core.converter.elements.Ref;
import com.devbaltasarq.nottakapp.core.converter.elements.Text;
import com.devbaltasarq.nottakapp.core.converter.elements.HtmlRef;
import com.devbaltasarq.nottakapp.core.converter.elements.OrdList;
import com.devbaltasarq.nottakapp.core.converter.elements.Root;
import com.devbaltasarq.nottakapp.core.converter.elements.UnordList;
import com.devbaltasarq.nottakapp.core.converter.markdown.MDParser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/** Scanner for MarkDown.
  * @author baltasarq
  */
public class MDParserTest {
    @Test
    public void testSimpleText()
    {
        final String TEXT = "This is a test.";
        final var PARSER = new MDParser( TEXT );
        
        try {
            PARSER.parse();
        } catch(ParseException exc)
        {
            fail( exc.getMessage() );
        }
        
        final var ROOT = PARSER.getRoot();
        
        assertEquals( 1, ROOT.count() );
        assertEquals( Text.NAME, ROOT.getLast().getName() );
        assertEquals( TEXT, ROOT.getLast().getContents() );
    }
    
    @Test
    public void testSimpleFormattedText()
    {
        final String TEXT_THIS = "This";
        final String TEXT_IS = "is";
        final String TEXT_A  ="a";
        final String TEXT_TEST = "test";
        final String TEXT = TEXT_THIS + " **" + TEXT_IS + "** " + TEXT_A
                            + " __" + TEXT_TEST + "__.";
        final var PARSER = new MDParser( TEXT );
        
        try {
            PARSER.parse();
        } catch(ParseException exc)
        {
            fail( exc.getMessage() );
        }
        
        final var ROOT = PARSER.getRoot();        
        assertEquals( 5, ROOT.count() );
        
        var eltoThis = ROOT.get( 0 );
        assertEquals( Text.NAME, eltoThis.getName() );
        assertEquals( TEXT_THIS, eltoThis.getContents() );
        
        var eltoIs = ROOT.get( 1 );
        assertEquals( Bold.NAME, eltoIs.getName() );
        assertEquals( Text.NAME, eltoIs.getLast().getName() );
        assertEquals( TEXT_IS, eltoIs.getLast().getContents() );
        
        var eltoA = ROOT.get( 2 );
        assertEquals( Text.NAME, eltoA.getName() );
        assertEquals( TEXT_A, eltoA.getContents() );
        
        var eltoTest = ROOT.get( 3 );
        assertEquals( Italic.NAME, eltoTest.getName() );
        assertEquals( Text.NAME, eltoTest.getLast().getName() );
        assertEquals( TEXT_TEST, eltoTest.getLast().getContents() );
        
        var eltoDot = ROOT.get( 4 );
        assertEquals( Text.NAME, eltoDot.getName() );
        assertEquals( ".", eltoDot.getContents() );
    }
    
    @Test
    public void testMultiplePars()
    {
        final String TEXT1 = "This is a first test.";
        final String TEXT2 = "This is a second test.";
        final var PARSER = new MDParser( TEXT1 + "\n" + TEXT2 );
        
        try {
            PARSER.parse();
        } catch(ParseException exc)
        {
            fail( exc.getMessage() );
        }
        
        final var ROOT = PARSER.getRoot();
        
        assertEquals( 2, ROOT.count() );
        assertEquals( Text.NAME, ROOT.get( 0 ).getName() );
        assertEquals( Par.NAME, ROOT.getLast().getName() );
        assertEquals( Text.NAME, ROOT.getLast().getLast().getName() );
        assertEquals( TEXT1, ROOT.get( 0 ).getContents() );
        assertEquals( TEXT2, ROOT.getLast().getLast().getContents() );
    }
    
    @Test
    public void testHeadings()
    {
        final String TEXT1 = "Heading 1";
        final String TEXT2 = "This is a first test.";
        final String TEXT3 = "Heading 2";
        final String TEXT4 = "This is a second test.";
        final var PARSER = new MDParser(
                                    "## " + TEXT1
                                    + "\n" + TEXT2
                                    + "\n### " + TEXT3
                                    + "\n" + TEXT4 );
        
        try {
            PARSER.parse();
        } catch(ParseException exc)
        {
            fail( exc.getMessage() );
        }
        
        final var ROOT = PARSER.getRoot();
        assertEquals( 2, ROOT.count() );
        
        final var H2_1 = ROOT.get( 0 );
        assertEquals( Head.NAME + "2", H2_1.getName() );
        assertEquals( TEXT1, H2_1.getContents() );
        
        final var H2_2 = ROOT.get( 1 );
        assertEquals( Head.NAME + "3", H2_2.getName() );
        assertEquals( TEXT3, H2_2.getContents() );
        
        final var P1 = H2_1.getLast();
        final var P2 = H2_2.getLast();

        assertEquals( Text.NAME, P1.getName() );
        assertEquals( TEXT2, P1.getContents() );
        assertEquals( Text.NAME, P2.getName() );
        assertEquals( TEXT4, P2.getContents() );
    }
    
    @Test
    public void testFalseHeading()
    {
        final String TEXT = "This is a test of #1 priority.";
        final var PARSER = new MDParser( TEXT );

        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        final var ROOT = PARSER.getRoot();

        assertEquals( 1, ROOT.count() );
        assertEquals( Text.NAME, ROOT.getLast().getName() );
        assertEquals( TEXT, ROOT.getLast().getContents() );
    }
    
    @Test
    public void testUnorderedList()
    {
        final String TEXT0 = "This is a list of texts";
        final String TEXT1 = "This is a first test.";
        final String TEXT2 = "This is a second test.";
        final String TEXT3 = "This is a third test.";
        final String[] LI_TEXTS = new String[]{ TEXT1, TEXT2, TEXT3 };
        final var PARSER = new MDParser(
                                    TEXT0
                                    + "\n - " + TEXT1
                                    + "\n - " + TEXT2
                                    + "\n - " + TEXT3 );
        
        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        final var ROOT = PARSER.getRoot();

        assertEquals( 2, ROOT.count() );
        assertEquals( TEXT0, ROOT.get( 0 ).getContents() );
        assertEquals( UnordList.NAME, ROOT.getLast().getName());
        
        final var UL = ROOT.getLast();
        
        assertEquals( 3, UL.count() );
        assertEquals( Entry.NAME, UL.get( 0 ).getName() );
        assertEquals( Entry.NAME, UL.get( 1 ).getName() );
        assertEquals( Entry.NAME, UL.get( 2 ).getName() );
        
        for(int i = 0; i < LI_TEXTS.length; ++i) {
            var li = UL.get( i );
            
            assertEquals( Text.NAME, li.getLast().getName() );
            assertEquals( LI_TEXTS[ i ], li.getLast().getContents() );
        }
    }
    
    @Test
    public void testFalseUnorderedList()
    {
        final String TEXT = "This: 5 - 1 is a test of a false UL.";
        final var PARSER = new MDParser( TEXT );

        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        final var ROOT = PARSER.getRoot();

        assertEquals( 1, ROOT.count() );
        assertEquals( Text.NAME, ROOT.getLast().getName() );
        assertEquals( TEXT, ROOT.getLast().getContents() );
    }
    
    @Test
    public void testOrderedList()
    {
        final String TEXT0 = "This is a list of texts";
        final String TEXT1 = "This is a first test.";
        final String TEXT2 = "This is a second test.";
        final String TEXT3 = "This is a third test.";
        final String[] LI_TEXTS = new String[]{ TEXT1, TEXT2, TEXT3 };
        final var PARSER = new MDParser(
                                    TEXT0
                                    + "\n 1. " + TEXT1
                                    + "\n 2. " + TEXT2
                                    + "\n 3. " + TEXT3 );
        
        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
              
        final var ROOT = PARSER.getRoot();
        assertEquals( 2, ROOT.count() );
        assertEquals( TEXT0, ROOT.get( 0 ).getContents() );
        assertEquals( OrdList.NAME, ROOT.getLast().getName());
        
        final var UL = ROOT.getLast();
        
        assertEquals( 3, UL.count() );
        assertEquals( Entry.NAME, UL.get( 0 ).getName() );
        assertEquals( Entry.NAME, UL.get( 1 ).getName() );
        assertEquals( Entry.NAME, UL.get( 2 ).getName() );
        
        for(int i = 0; i < LI_TEXTS.length; ++i) {
            var li = UL.get( i );
            
            assertEquals( Text.NAME, li.getLast().getName() );
            assertEquals( LI_TEXTS[ i ], li.getLast().getContents() );
        }
    }
    
    @Test
    public void testFalseOrderedList()
    {
        final String TEXT = "This: 5.1 is a test of a false OL.";
        final var PARSER = new MDParser( TEXT );

        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        final var ROOT = PARSER.getRoot();

        assertEquals( 1, ROOT.count() );
        assertEquals( Text.NAME, ROOT.getLast().getName() );
        assertEquals( TEXT, ROOT.getLast().getContents() );
    }
    
    @Test
    public void testWikiRef()
    {
        final String TEXT0 = "This is a";
        final String TEXT1 = "test";
        final String TEXT2 = "of a web ref.";
        final String FILE = "a1212112.md";
        final String TEXT = TEXT0 + " [" + TEXT1 + "|" + FILE + "] " + TEXT2;
        final var PARSER = new MDParser( TEXT );

        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        final var ROOT = PARSER.getRoot();
        
        assertEquals( Text.NAME, ROOT.get( 0 ).getName() );
        assertEquals( TEXT0, ROOT.get( 0 ).getContents() );
        
        final var REF = ROOT.get( 1 );
        
        assertEquals( Ref.NAME, REF.getName() );
        assertEquals( Text.NAME, REF.getLast().getName() );
        assertEquals( TEXT1, REF.getLast().getContents() );
        assertEquals( FILE,
                        REF.getAttributes().get( Ref.LBL_REF ) );
        
        assertEquals( Text.NAME, ROOT.get( 2 ).getName() );
        assertEquals( TEXT2, ROOT.get( 2 ).getContents() );
    }
    
    @Test
    public void testHRef()
    {
        final String TEXT0 = "This is a";
        final String TEXT1 = "test";
        final String TEXT2 = "of a web ref.";
        final String URL = "http://wikipedia.es";
        final String TEXT = TEXT0 + " [" + TEXT1 + "](" + URL + ") " + TEXT2;
        final var PARSER = new MDParser( TEXT );

        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        final var ROOT = PARSER.getRoot();

        assertEquals( Text.NAME, ROOT.get( 0 ).getName() );
        assertEquals( TEXT0, ROOT.get( 0 ).getContents() );
        
        final var HREF = ROOT.get( 1 );
        assertEquals( HtmlRef.NAME, HREF.getName() );
        assertEquals( 1, HREF.count() );
        assertEquals( Text.NAME, HREF.getLast().getName() );
        assertEquals( TEXT1, HREF.getLast().getContents() );
        assertEquals( URL, HREF.getAttributes().get( HtmlRef.LBL_REF ) );
        
        assertEquals( Text.NAME, ROOT.get( 2 ).getName() );
        assertEquals( TEXT2, ROOT.get( 2 ).getContents() );
    }
    
    @Test
    public void testFalseRef()
    {
        final String TEXT = "[ This is a test.";
        final var PARSER = new MDParser( TEXT );

        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        final var ROOT = PARSER.getRoot();

        assertEquals( 1, ROOT.count() );
        assertEquals( TEXT, ROOT.getLast().getContents() );
    }
    
    @Test
    public void testCheckboxLowercase()
    {
        final String TEXT = "This is a test.";
        final var PARSER = new MDParser( "[ ] " + TEXT
                                         + "\n[x] " + TEXT );

        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        this.chkCheckBox( TEXT, PARSER.getRoot() );
    }
    
    @Test
    public void testCheckboxUppercase()
    {
        final String TEXT = "This is a test.";
        final var PARSER = new MDParser( "[ ] " + TEXT
                                         + "\n[X] " + TEXT );

        try {
            PARSER.parse();
        } catch(ParseException exc) {
            fail( exc.getMessage() );
        }
        
        this.chkCheckBox( TEXT, PARSER.getRoot() );
    }
    
    private void chkCheckBox(final String TEXT, final Root ROOT)
    {
        assertEquals( 3, ROOT.count() );
        assertTrue( ROOT.get( 0 ) instanceof Chk );
        final Chk CHK1 = (Chk) ROOT.get( 0 );
        assertFalse( CHK1.isActivated() );
        assertFalse( CHK1.containsAttr( Chk.ETQ_CHECKED ) );
        assertTrue( ROOT.get( 1 ) instanceof Text );
        assertEquals( TEXT, ROOT.get( 1 ).getContents() );
        assertTrue( ROOT.getLast() instanceof Par );
        
        final var PAR = ROOT.getLast();
        assertEquals( 2, PAR.count() );
        assertTrue( PAR.get( 0 ) instanceof Chk );
        final Chk CHK2 = (Chk) PAR.get( 0 );
        assertTrue( CHK2.isActivated() );
        assertTrue( CHK2.containsAttr( Chk.ETQ_CHECKED ) );
        assertTrue( PAR.getLast() instanceof Text );
        assertEquals( TEXT, PAR.getLast().getContents() );
    }
}
