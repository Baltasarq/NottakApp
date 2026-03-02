// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter;


import java.util.function.Consumer;

import com.devbaltasarq.nottakapp.core.converter.elements.Root;


/** The visitor pattern.
  * @author baltasarq
  */
public class DOMRunner {
    public DOMRunner(Root root)
    {
        this.root = root;
        this.visitor = e -> {};
        this.textResult = new StringBuilder();
    }
    
    /** Change the lambda for the visitor.
      * @param r a new consumer for the element.
      */
    public void setVisitor(Consumer<Element> r)
    {
        this.visitor = r;
    }
    
    /** Execute the running all over the elements. */
    public void run()
    {
        this.runOver( this.getRoot() );
    }
    
    private void runOver(Element elto)
    {
        for(final Element SUB_ELTO: elto.getAll()) {
            this.visitor.accept( SUB_ELTO );
            runOver( SUB_ELTO );
            
            if ( SUB_ELTO.needsClosing() ) {
                this.visitor.accept( SUB_ELTO.copyAsClosing() );
            }
        }
    }
    
    /** @return the last char of the resulting text. */
    protected char getResultTextLastChar()
    {
        char toret = '\0';
        int size = this.textResult.length();
        
        if ( size > 0 ) {
            toret = this.textResult.charAt( size - 1 );
        }
        
        return toret;
    }
    
    /** Eliminates the ending spaces at the end of the result text. */
    protected void rtrimTextResult()
    {
        int last = this.textResult.length() - 1;
     
        while ( last >= 0
             && Character.isSpaceChar( this.textResult.charAt( last ) ) )
        {
            last -= 1;
        }
        
        this.textResult.delete( last + 1, this.textResult.length() );
    }
    
    /** Appends text to the result.
      * @param txt the text to append to the result.
      */
    protected void addToTextResult(String txt)
    {
        this.textResult.append( txt );
    }
    
    /** Appends a char to the result.
      * @param ch the char to append to the result.
      */
    protected void addToTextResult(char ch)
    {
        this.textResult.append( ch );
    }
    
    /** @return true if text ends with the given postfix.
      * @param POSTFIX a given string to check the ending of the text with.
      */
    protected boolean textResultEndsWith(final String POSTFIX)
    {
        return ( POSTFIX.length() <= this.textResult.length()
                && this.textResult
                    .substring( this.textResult.length() - POSTFIX.length() )
                        .equals( POSTFIX ) );
    }
    
    /** @return the root element of the DOM. */
    public Root getRoot()
    {
        return this.root;
    }
    
    /** @return the result text. */
    public String getResultText()
    {
        return this.textResult.toString();
    }
    
    @Override
    public String toString()
    {
        return this.textResult.toString();
    }
    
    private Consumer<Element> visitor;
    private final StringBuilder textResult;
    private final Root root;
}
