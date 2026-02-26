// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter;


import com.devbaltasarq.nottakapp.core.converter.elements.Chk;
import com.devbaltasarq.nottakapp.core.converter.elements.Root;


/** A generic parser.
  * @author baltasarq
  */
public abstract class Parser {
    public Parser(final Scanner SCAN)
    {
        this.SCANNER = SCAN;
        this.root = new Root();
    }
    
        
    /** @return the scanner in use. */
    public Scanner getScanner()
    {
        return this.SCANNER;
    }
    
    /** @return the root of the parsed input, empty if not yet parsed. */
    public Root getRoot()
    {
        return this.root;
    }
    
    /** Parses the input. This method is to be override by sub classes.
      * @return the root of the input parsed.
      * @throws ParseException when parsing something unexpected.
      */
    public abstract Root parse() throws ParseException;

    private final Root root;
    private final Scanner SCANNER;
}
