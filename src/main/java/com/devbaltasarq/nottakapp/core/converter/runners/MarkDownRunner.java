// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.runners;


import com.devbaltasarq.nottakapp.core.converter.DOMRunner;
import com.devbaltasarq.nottakapp.core.converter.Element;
import com.devbaltasarq.nottakapp.core.converter.elements.Root;
import com.devbaltasarq.nottakapp.core.converter.elements.Text;
import com.devbaltasarq.nottakapp.core.converter.elements.Par;
import com.devbaltasarq.nottakapp.core.converter.elements.Bold;
import com.devbaltasarq.nottakapp.core.converter.elements.Chk;
import com.devbaltasarq.nottakapp.core.converter.elements.Entry;
import com.devbaltasarq.nottakapp.core.converter.elements.Head;
import com.devbaltasarq.nottakapp.core.converter.elements.HtmlRef;
import com.devbaltasarq.nottakapp.core.converter.elements.Img;
import com.devbaltasarq.nottakapp.core.converter.elements.Ref;
import com.devbaltasarq.nottakapp.core.converter.elements.Italic;
import com.devbaltasarq.nottakapp.core.converter.elements.OrdList;
import com.devbaltasarq.nottakapp.core.converter.elements.UnordList;


/** Converts the DOM to MarkDown.
  * @author baltasarq
  */
public class MarkDownRunner extends DOMRunner {
    public MarkDownRunner(Root root)
    {
        super( root );
        this.inUl = false;
        this.olLevel = Integer.MIN_VALUE;
        this.refUrl = "";
        this.setVisitor( e -> this.markDownFromElement( e ) );
    }
    
    public void markDownFromElement(final Element E)
    {
        if ( E == null ) {
            throw new Error( "PlainTextRunner.txtFromElement(e): e is null" );
        }
        
        if ( !E.isClosing() ) {
            this.doWhenOpening( E );
        } else {
            this.doWhenClosing( E );
        }
    }
    
    private void doImg(final Img IMG)
    {
        final String IMAGE_URL = IMG.getSrc();
        final String IMAGE_TITLE = IMG.getTitle();

        this.addToTextResult( "![" );
        this.addToTextResult( IMAGE_TITLE );
        this.addToTextResult( "](" );
        this.addToTextResult( IMAGE_URL );
        this.addToTextResult( ") " );
    }
    
    private void doWhenOpeningText(final Text TEXT)
    {
        this.addToTextResult( TEXT.getContents() );
        
        // Separate plain text with a space
        if ( TEXT.getParent() instanceof Root
          || TEXT.getParent() instanceof Par )
        {
            this.rtrimTextResult();
            this.addToTextResult( ' ' );
        }
    }
    
    private void doWhenOpeningChk(final Chk CHK)
    {
        String strChk = "[ ] ";
        
        if ( CHK.isActivated() ) {
            strChk = "[X] ";
        }
        
        this.addToTextResult( strChk );
    }
    
    private void doWhenOpeningHead(final Head HEAD)
    {
        this.addToTextResult( "#".repeat( HEAD.getLevel() ) );
        this.addToTextResult( ' ' );

        this.addToTextResult( HEAD.getContents() );
    }
    
    private void doWhenOpeningEntry()
    {
        if ( this.inUl ) {
            this.addToTextResult( " - " );
        }
        else
        if ( this.olLevel >= 0 ) {
            this.olLevel += 1;
            this.addToTextResult( String.format( " %d. ", this.olLevel ));
        }
    }
    
    /** This must be executed when the tags is closing.
      * @param E the element being visited.
      */
    private void doWhenOpening(final Element E)
    {
        switch( E ) {
            case final Text TEXT -> this.doWhenOpeningText( TEXT );
            case UnordList _     -> {
                this.inUl = true;
                this.appendCR();
            }
            case OrdList _       -> {
                this.olLevel = 0;
                this.appendCR();
            }
            case Entry _ -> this.doWhenOpeningEntry();
            case final Head HEAD -> this.doWhenOpeningHead( HEAD );
            case final Chk CHK   -> this.doWhenOpeningChk( CHK );
            case final Img IMG   -> this.doImg( IMG );
            case Ref _           -> {
                        this.refUrl = E.getAttributes().get( "href" );
                        this.addToTextResult( "[" );
            }
            case HtmlRef _       -> {
                        this.refUrl = E.getAttributes().get( "href" );
                        this.addToTextResult( "[" );
            }
            case Bold _          -> this.addToTextResult( "**" );
            case Italic _        -> this.addToTextResult( "__" );
            case Par _           -> this.appendCR();
            default              -> { break; }
        }
    }
    
    /** This must be executed when the tags is closing.
      * @param E the element being visited.
      */
    private void doWhenClosing(final Element E)
    {
        if ( E instanceof UnordList ) {
            this.inUl = false;
        }
        else
        if ( E instanceof OrdList ) {
            this.olLevel = Integer.MIN_VALUE;
        }
        else
        if ( E instanceof Par
          || E instanceof Entry
          || E instanceof Head )
        {
            this.appendCR();
        }
        else
        if ( E instanceof Ref) {
            this.addToTextResult( "|" );
            this.addToTextResult( this.refUrl );
            this.addToTextResult( "] " );
            this.refUrl = "";
        }
        else
        if ( E instanceof HtmlRef) {
            this.addToTextResult( "](" );
            this.addToTextResult( this.refUrl );
            this.addToTextResult( ") " );
            this.refUrl = "";
        }
        else
        if ( E instanceof Bold ) {
            this.addToTextResult( "** " );
        }
        else
        if ( E instanceof Italic) {
            this.addToTextResult( "__ " );
        }
    }
    
    /** Eliminates trailing spaces and appends a single CR. */
    protected void appendCR()
    {
        this.rtrimTextResult();
        this.addToTextResult( '\n' );
    }

    private boolean inUl;
    private int olLevel;
    private String refUrl;
}
