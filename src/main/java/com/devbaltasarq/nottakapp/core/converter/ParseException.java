// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter;


/** Exception thrown when a parsing mismatch is detected.
  * @author baltasarq
  */
public class ParseException extends Exception {
    public ParseException(String msg)
    {
        super( msg );
    }
}
