// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter;


import com.devbaltasarq.nottakapp.core.converter.elements.Root;
import com.devbaltasarq.nottakapp.core.converter.elements.Text;


/** Converts the DOM to text.
  * @author baltasarq
  */
public class PlainTextRunner extends DOMRunner {
    public PlainTextRunner(Root root)
    {
        super( root );
        this.toret = new StringBuilder();
        this.setVisitor( e -> this.txtFromElement( e ) );
    }
    
    public void txtFromElement(Element e)
    {
        if ( e instanceof Text ) {
            this.toret.append( e.getText() );
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
