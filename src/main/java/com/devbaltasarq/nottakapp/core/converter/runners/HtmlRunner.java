// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.runners;


import com.devbaltasarq.nottakapp.core.converter.DOMRunner;
import com.devbaltasarq.nottakapp.core.converter.Element;
import com.devbaltasarq.nottakapp.core.converter.elements.Bold;
import com.devbaltasarq.nottakapp.core.converter.elements.Chk;
import com.devbaltasarq.nottakapp.core.converter.elements.Root;
import com.devbaltasarq.nottakapp.core.converter.elements.Text;
import com.devbaltasarq.nottakapp.core.converter.elements.Head;
import com.devbaltasarq.nottakapp.core.converter.elements.Link;
import com.devbaltasarq.nottakapp.core.converter.elements.Img;
import com.devbaltasarq.nottakapp.core.converter.elements.Italic;
import com.devbaltasarq.nottakapp.core.converter.elements.Par;
import java.util.Map;

/*
 --------------------------------------
| Needs of spacing label and contents  |
 --------------------------------------
|  Label   |  With space |  No space   |
|----------|-------------|-------------|
|    p     |             |      X      |
|    h     |             |      X      |
|    li    |             |      X      |
|    ul    |             |      X      |
|    ol    |             |      X      |
|    b     |      X      |             |
|    i     |      X      |             |
|    a     |      X      |             |
|   img    |      X      |             |
 --------------------------------------
*/


/** Converts the DOM to HTML.
  * @author baltasarq
  */
public class HtmlRunner extends DOMRunner {
    public HtmlRunner(Root root)
    {
        super( root );
        
        this.addToTextResult( this.htmlPreamble() );
        this.setVisitor( e -> this.htmlFromElement( e ) );
    }
    
    @Override
    public void run()
    {
        super.run();
        this.rtrimTextResult();
        this.addToTextResult( this.htmlEpilogue() );
    }
    
    /** @return the HTML preamble: <html><body>.... */
    private String htmlPreamble()
    {
        return "<html><body>";
    }
    
    /** @return the HTML epilogue: ...</body></html> */
    private String htmlEpilogue()
    {
        return "</body></html>";
    }
    
    /** Generates bold or italic.
      * @param E the element being visited.
      */
    public void doFontWeight(final Element E)
    {
        Map<String, String> tags = ELEM_TO_OPEN_HTML;
        
        if ( E.isClosing() ) {
            tags = ELEM_TO_CLOSE_HTML;
        }
        
        String txt = tags.get( E.getName() );
        
        if ( txt == null ) {
            throw new Error( "doFontWeight(E): no corresponding tag found" );
        }
        
        if ( !E.isClosing() ) {
            this.addToTextResult( ' ' );
        }
        
        this.addToTextResult( txt );
        
        if ( E.isClosing() ) {
            this.addToTextResult( ' ' );
        }
    }
    
    private void doCheckbox(final Chk CHK)
    {
        final var CHK_FMT = "<input type=\"checkbox\" %s/> ";
        String strValue = "";
        
        if ( CHK.isActivated() ) {
            strValue = Chk.ETQ_CHECKED;
        }

        this.addToTextResult( String.format( CHK_FMT, strValue ) );
    }
    
    private void doHead(final Head HEAD)
    {
        String tag = HEAD.getName();
        
        if ( HEAD.isClosing() ) {
            tag = "/" + tag;
        }
                
        this.addToTextResult( "<" );
        this.addToTextResult( tag );
        this.addToTextResult( ">" );
        
        if ( !HEAD.isClosing() ) {
            this.addToTextResult( HEAD.getContents() );
        }
    }
    
    private void doText(final Text TEXT)
    {
        this.addToTextResult( TEXT.getContents() );
    }
    
    private void doOpenLink(final Link LINK)
    {
        this.addToTextResult( " <a href=\"" );
        this.addToTextResult( LINK.getURL() );
        this.addToTextResult( "\">" );
    }
    
    private void doImg(final Img IMG)
    {
        this.addToTextResult( " <img src=\"" );
        this.addToTextResult( IMG.getSrc() );
        this.addToTextResult( "\" alt=\"" );
        this.addToTextResult( IMG.getTitle() );
        this.addToTextResult( "\"> " );
    }
    
    private void doParOpening(final Par PAR)
    {
        if ( !this.textResultEndsWith( "<p>" )
       /*   && !this.textResultEndsWith( "<p/>" )
          && !this.textResultEndsWith( "</p>" ) */)
        {
            this.addToTextResult( "<p>" );    
        }
    }
    
    private void doParClosing()
    {
        if ( !this.textResultEndsWith( "</p>" ) ) {
            this.addToTextResult( "</p>" );
        }
    }
    
    /** This must be executed when the tags is closing.
      * @param E the element being visited.
      */
    private void doWhenOpening(final Element E)
    {
        switch ( E ) {
            case final Text TEXT    -> this.doText( TEXT );
            case Bold _             -> this.doFontWeight( E );
            case Italic _           -> this.doFontWeight( E );
            case final Img IMG      -> this.doImg( IMG );
            case final Link LINK    -> this.doOpenLink( LINK );
            case final Head HEAD    -> this.doHead( HEAD );
            case final Chk CHK      -> this.doCheckbox( CHK );
            case final Par PAR      -> this.doParOpening( PAR );
            default -> {
                addToTextResult( ELEM_TO_OPEN_HTML.getOrDefault( E.getName(), "" ) );
            }
        }
        
        return;
    }
    
    /** This must be executed when the tags is closing.
      * @param E the element being visited.
      */
    private void doWhenClosing(final Element E)
    {
        switch( E ) {
            case final Head HEAD -> this.doHead( HEAD );
            case Bold _          -> this.doFontWeight( E );
            case Italic _        -> this.doFontWeight( E );
            case final Par _     -> this.doParClosing();
            default              -> this.addToTextResult(
                                        ELEM_TO_CLOSE_HTML.getOrDefault(
                                                            E.getName(), "" ));
        }
    }
    
    /** Writes to the internal builder the text generation for the given element.
      * @param E an element to generate HTML text for.
      */
    public void htmlFromElement(final Element E)
    {
        if ( E == null ) {
            throw new Error( "HtmlRunner.txtFromElement(e): e is null" );
        }
        
        if ( !E.isClosing() ) {
            this.doWhenOpening( E );
        } else {
            this.doWhenClosing( E );
        }
    }
       
    private static final Map<String, String> ELEM_TO_OPEN_HTML = Map.ofEntries(
                        Map.entry( "b", "<b>"),
                        Map.entry( "i", "<i>"),
                        Map.entry( "li", "<li>"),
                        Map.entry( "ul", "<ul>"),
                        Map.entry( "ol", "<ol>")
    );

    private static final Map<String, String> ELEM_TO_CLOSE_HTML = Map.ofEntries(
                        Map.entry( "b", "</b>"),
                        Map.entry( "i", "</i>"),
                        Map.entry( "li", "</li>"),
                        Map.entry( "a", "</a> " ),
                        Map.entry( "wref:a", "</a> " ),
                        Map.entry( "ol", "</ol>"),
                        Map.entry( "ul", "</ul>")
    );
}
