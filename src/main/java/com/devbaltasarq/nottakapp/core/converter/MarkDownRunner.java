// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter;


import com.devbaltasarq.nottakapp.core.converter.elements.Root;
import com.devbaltasarq.nottakapp.core.converter.elements.Text;
import com.devbaltasarq.nottakapp.core.converter.elements.Par;
import com.devbaltasarq.nottakapp.core.converter.elements.Bold;
import com.devbaltasarq.nottakapp.core.converter.elements.Italic;


/** Converts the DOM to MarkDown.
  * @author baltasarq
  */
public class MarkDownRunner extends DOMRunner {
    public MarkDownRunner(Root root)
    {
        super( root );
        this.toret = new StringBuilder();
        this.setVisitor( e -> this.markDownFromElement( e ) );
    }
    
    public void markDownFromElement(Element e)
    {
        if ( e instanceof Par ) {
            this.toret.append( '\n' );
            this.toret.append( '\n' );            
        }
        else
        if ( e instanceof Bold ) {
            this.toret.append( "**" );
        }
        else
        if ( e instanceof Italic ) {
            this.toret.append( "*" );
        }
    }
    
    @Override
    public String toString()
    {
        if ( this.toret.isEmpty() ) {
            this.run();
        }
        
        return this.toret.toString();
    }
    
    private final StringBuilder toret;
}
