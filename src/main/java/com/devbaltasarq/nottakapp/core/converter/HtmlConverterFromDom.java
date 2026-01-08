// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter;


/** Converts DOM to MarkDown.
  * @author baltasarq
  */
public class HtmlConverterFromDom {
    public HtmlConverterFromDom(Element root)
    {
        this.text = null;
        this.root = root;
    }
    
    /** @return the root element. */
    public Element getRoot()
    {
        return this.root;
    }
    
    public String fromDomToHtml()
    {
        final var TORET = new StringBuilder( 120 );
        
        
        
        return TORET.toString();
    }
    
    @Override
    public String toString()
    {
        if ( this.text == null ) {
            this.text = this.fromDomToHtml();
        }
        
        return this.text;
    }
    
    private String text;
    private final Element root;
}
