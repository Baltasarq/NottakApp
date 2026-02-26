// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter;


/** A generic scanner.
  * @author baltasarq
  */
public abstract class Scanner {
    public Scanner(String text)
    {
        if ( text == null ) {
            text = "";
        }
        
        this.text = text.trim();
    }
    
    /** @return the size of the input. */
    public int size()
    {
        return this.text.length();
    }
    
    /** @return the number of chars pending in input. */
    public int countPendingChars()
    {
        return this.size() - this.getPos();
    }
    
    /** @return the whole input text. */
    public String getInput()
    {
        return this.text;
    }
    
    /** @return the current pos. */
    public int getPos()
    {
        return this.pos;
    }
    
    /** Skips in the input the spaces ahead. */
    public abstract void skipSpaces();
    
    /** Moves the cursor to the given position.
      * Moves the cursor to 0 if pos is negative.
      * Moves the cursor to the last position if pos >= size.
      * @param pos the new position.
      */
    public void moveTo(int pos)
    {
        this.pos = Math.max( 0, Math.min( pos, this.size() - 1 ) );
    }
    
    /** Advance the cursor exactly one position. */
    public void skip()
    {
        this.skip( 1 );
    }
    
    /** Move the cursor
      * @param delta the movement from the current position.
      */
    public void skip(int delta)
    {
        if ( !this.isEod() ) {
            this.pos = Math.max( 0, pos + delta );
        }
    }
    
    /** @return true of we have reached the end of the document, false otherwise. */
    public boolean isEod()
    {
        return this.pos >= this.text.length();
    }
    
    /** @return the current char. */
    public char getCurrentChar()
    {
        char toret = '\0';
        
        if ( !this.isEod() ) {
            toret = this.text.charAt( pos );
        }
        
        return toret;
    }
    
    /** @return true when the next input matches the given char, false otherwise.
      * @param ch the char to match in the input.
      */
    public boolean match(char ch)
    {
        return this.match( Character.toString( ch ) );
    }
    
    /** @return true when the next input matches the given text, false otherwise.
      * @param text the text to match in the input.
      */
    public boolean match(String text)
    {
        boolean toret = false;
        
        if ( !this.isEod() ) {
            toret = true;
            for(char ch: text.toCharArray()) {
                if ( ch != getCurrentChar() ) {
                    toret = false;
                    break;
                }
                
                this.skip();
            }
        }
        
        return toret;
    }
    
    /** @return the current char and skips it. */
    public char readChar()
    {
        char toret = this.getCurrentChar();
        
        this.skip();
        return toret;
    }
    
    private int pos;
    private final String text;
}
