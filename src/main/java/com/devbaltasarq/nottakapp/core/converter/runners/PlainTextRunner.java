// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.runners;


import com.devbaltasarq.nottakapp.core.converter.DOMRunner;
import com.devbaltasarq.nottakapp.core.converter.Element;
import com.devbaltasarq.nottakapp.core.converter.elements.Chk;
import com.devbaltasarq.nottakapp.core.converter.elements.Root;
import com.devbaltasarq.nottakapp.core.converter.elements.Par;
import com.devbaltasarq.nottakapp.core.converter.elements.Head;
import com.devbaltasarq.nottakapp.core.converter.elements.Text;
import com.devbaltasarq.nottakapp.core.converter.elements.Entry;
import com.devbaltasarq.nottakapp.core.converter.elements.OrdList;
import com.devbaltasarq.nottakapp.core.converter.elements.UnordList;


/** Converts the DOM to text.
  * @author baltasarq
  */
public class PlainTextRunner extends DOMRunner {
    public PlainTextRunner(Root root)
    {
        super( root );
        this.olLevel = Integer.MIN_VALUE;
        this.inUl = false;
        this.setVisitor( e -> this.txtFromElement( e ) );
    }
    
    private void txtFromText(final Text TEXT)
    {
        if ( this.inUl ) {
            this.addToTextResult( " - " );
        }
        else
        if ( this.olLevel >= 0 ) {
            this.olLevel += 1;
            this.addToTextResult( String.format( " %d. ", this.olLevel ));
        }

        this.addToTextResult( TEXT.getContents() );
    }
    
    private void txtFromChk(final Chk CHK)
    {
        String strChk = "[ ] ";
        
        if ( CHK.isActivated() ) {
            strChk = "[X] ";
        }
        
        this.addToTextResult( strChk );
    }
    
    private void txtFromHead(final Head HEAD)
    {
        int heading = HEAD.getLevel();
        
        this.addToTextResult( HEAD.getContents() );

        if ( heading == 1 ) {
            this.addToTextResult(  "\n===\n\n" );
        }
        else
        if ( heading > 1 ) {
            this.addToTextResult(  "\n---\n\n" );
        }
    }
    
    public void txtFromElement(Element e)
    {
        if ( e == null ) {
            throw new Error( "PlainTextRunner.txtFromElement(e): e is null" );
        }
        
        if ( !e.isClosing() ) {
            if ( e instanceof final Text TEXT) {
                this.txtFromText( TEXT );
            }
            else
            if ( e instanceof UnordList ) {
                this.inUl = true;
            }
            else
            if ( e instanceof OrdList ) {
                this.olLevel = 0;
            }
            else
            if ( e instanceof Par ) {
                this.addToTextResult( '\n' );
            }
            else
            if ( e instanceof final Chk CHK) {
                this.txtFromChk( CHK );
            }
        } else {
            if ( e instanceof UnordList ) {
                this.inUl = false;
            }
            else
            if ( e instanceof OrdList ) {
                this.olLevel = Integer.MIN_VALUE;
            }           
            else
            if ( e instanceof Par
              || e instanceof Entry )
            {
                this.addToTextResult( '\n' );
            }
            else
            if ( e instanceof final Head HEAD) {
                this.txtFromHead( HEAD );
            }
        }
    }
    
    private boolean inUl;
    private int olLevel;
}
