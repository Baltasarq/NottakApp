// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter.markdown;


import com.devbaltasarq.nottakapp.core.converter.Scanner;
import java.util.Locale;
import java.util.Set;


/** An scanner for MarkDown input.
  * @author baltasarq
  */
public class MDScanner extends Scanner {
    private final static String SPACES = " \t";
    public final static char CR = '\n';
    public final static char HEADING = '#';
    public final static char ITALIC_FORMATTER_CHAR = '_';
    public final static String ITALIC_FORMATTER = "" + ITALIC_FORMATTER_CHAR
                                                    + ITALIC_FORMATTER_CHAR;
    public final static char BOLD_FORMATTER_CHAR = '*';
    public final static String BOLD_FORMATTER = "" + BOLD_FORMATTER_CHAR
                                                    + BOLD_FORMATTER_CHAR;
    public final static char UNORDERED_LIST_ENTRY = '-';
    public final static String OPEN_IMG = "![";
    public final static char OPEN_REF = '[';
    public final static String CLOSE_HREF_LABEL = "](";
    public final static char CLOSE_WIKI_REF = ']';
    public final static char CLOSE_HREF = ')';
    public final static char WIKI_REF_SEPARATOR = '|';
    public final static String CHK_BOX_FALSE = "[ ]";
    public final static String CHK_BOX_TRUE = "[X]";
    
    public enum TokenType {
        TEXT,
        SPACE,
        CR,
        CHECK_BOX_FALSE,
        CHECK_BOX_TRUE,
        HEADING,
        BOLD_FORMATTER,
        ITALIC_FORMATTER,
        UNORDERED_LIST_ENTRY,
        ORDERED_LIST_ENTRY,
        OPEN_IMG,
        OPEN_REF,
        CLOSE_WIKI_REF,
        CLOSE_HREF_LABEL,
        WIKI_REF_SEPARATOR,
        END
    }
    
    public MDScanner(String text)
    {
        super( text );
    }
    
    /** Decides whether the current char is a space.
      * Only chars in SPACES are considered spaces.
      * @see MDScanner::SPACES
      * @return true if the current char is a space, false otherwise.
      */
    public boolean isSpace()
    {
        return SPACES.contains( Character.toString( this.getCurrentChar() ) );
    }
    
    /** Skips the next spaces. 
      * @see MDScanner::isSpace
      */
    @Override
    public void skipSpaces()
    {
        while( !this.isEod()
            && this.isSpace() )
        {
            this.readChar();
        }
    }
    
    private TokenType determineRefOrCheckbox()
    {
        TokenType toret = TokenType.OPEN_REF;
        int oldPos = this.getPos();
                                
        if ( this.match( CHK_BOX_FALSE ) ) {
            toret = TokenType.CHECK_BOX_FALSE;
        }
        else {
            this.moveTo( oldPos );
            if ( this.match( CHK_BOX_TRUE ) ) {
                toret = TokenType.CHECK_BOX_TRUE;
            }
            
            this.moveTo( oldPos );
            if ( this.match( CHK_BOX_TRUE.toLowerCase( Locale.US ) ) ) {
                toret = TokenType.CHECK_BOX_TRUE;
            }
        }

        this.moveTo( oldPos );
        return toret;
    }
    
    /** @return whether there is one of the tokens ahead that depend on
      *         the start of the line.
      */
    private TokenType getDependentTokenTypeAhead()
    {
        TokenType toret = null;
        
        if ( this.isStartOfLine() ) {
            if ( this.isOrdListEntryPrefix() )
            {
                toret = TokenType.ORDERED_LIST_ENTRY;
            } else {
                switch ( this.getCurrentChar() ) {
                    case HEADING ->
                                toret = TokenType.HEADING;
                    case UNORDERED_LIST_ENTRY -> 
                                toret = TokenType.UNORDERED_LIST_ENTRY;
                    case OPEN_REF ->
                                toret = this.determineRefOrCheckbox();
                }
            }
        }
        
        return toret;
    }
    
    /** @return whether there is one of the independent tokens. */
    private TokenType getIndependentTokenTypeAhead()
    {
        final char CH = this.getCurrentChar();
        TokenType toret = TokenType.TEXT;

        if ( this.isSpace() ) {
            toret = TokenType.SPACE;
        } else {
            switch ( CH ) {
                case CR -> toret = TokenType.CR;
                case BOLD_FORMATTER_CHAR -> {
                    if ( this.match( BOLD_FORMATTER ) ) {
                        toret = TokenType.BOLD_FORMATTER;
                    } else {
                        toret = TokenType.TEXT;
                    }
                }
                case ITALIC_FORMATTER_CHAR -> {
                    if ( this.match( ITALIC_FORMATTER ) ) {
                        toret = TokenType.ITALIC_FORMATTER;
                    } else {
                        toret = TokenType.TEXT;
                    }
                }
                case OPEN_REF -> toret = this.determineRefOrCheckbox();
                case WIKI_REF_SEPARATOR -> toret = TokenType.WIKI_REF_SEPARATOR;
                case CLOSE_WIKI_REF -> {
                    if ( this.match( CLOSE_HREF_LABEL ) ) {
                        toret = TokenType.CLOSE_HREF_LABEL;
                    } else {
                        toret = TokenType.CLOSE_WIKI_REF;
                    }
                }
                case '!' -> {
                    if ( this.match( OPEN_IMG ) ) {
                        toret = TokenType.OPEN_IMG;
                    } else {
                        toret = TokenType.TEXT;
                    }
                }
            }
        }
        
        return toret;
    }
    
    /** @return the next token type, based on the next char. **/
    public TokenType getNextTokenType()
    {
        TokenType toret = TokenType.END;
        
        if ( !this.isEod() ) {
            int oldPos = this.getPos();

            // Marks that do only make sense at the start of the line
            toret = this.getDependentTokenTypeAhead();

            if ( toret == null ) {
                // These can happen at any position
                toret = this.getIndependentTokenTypeAhead();
            }

            this.moveTo( oldPos );
        }
        
        return toret;
    }
    
    /** Reads the plain text ahead, ending with any other kind of input.
      * @return the plain text ahead.
      */
    public String readPlainText()
    {
        final StringBuilder TORET = new StringBuilder();
        TokenType nextToken = this.getNextTokenType();
        
        while( !this.isEod()
            && ( nextToken == TokenType.TEXT
              || nextToken == TokenType.SPACE ))
        {
            char ch = this.getCurrentChar();
            
            // Convert sequences of spaces to a single space.
            if ( this.isSpace() ) {
                ch = ' ';
                this.skipSpaces();
                this.skip( -1 );
            }
            
            TORET.append( ch );
            this.skip();
            nextToken = this.getNextTokenType();
        }
        
        return TORET.toString().trim();
    }
    
    /** Reads the text ahead, char by char, until the delimiter is found.
      * @param delimiter
      * @return the input until the char given is found.
      */
    public String readVerbatimUntil(char delimiter)
    {
        return this.readVerbatimUntil( Set.of( delimiter ) );
    }
    
    /** Reads the text ahead, char by char, until the delimiter is found.
      * @param delimiters a set of delimiters.
      * @return the input until the char given is found.
      */
    public String readVerbatimUntil(Set<Character> delimiters)
    {
        final StringBuilder TORET = new StringBuilder();
        
        while( !this.isEod()
            && !delimiters.contains( this.getCurrentChar() ) )
        {
            TORET.append( this.readChar() );
        }
        
        return TORET.toString().trim();
    }
    
    /** @return the file name ahead, as in the last part of [file1|file.txt] */
    public String readFileName()
    {
        return this.readVerbatimUntil( CLOSE_WIKI_REF );
    }
    
    /** @return the URL ahead,
      * as in the last part of [Wikipedia](http://wikipedia.es)
      */
    public String readURL()
    {
        return this.readVerbatimUntil( CLOSE_HREF );
    }
    
    /** Determines if there is an ordered list entry prefix ahead.
      * It does not change the position of the scanner.
      * @return true if there is an ordered list entry ahead, as in "1. xxx"
      */
    public boolean isOrdListEntryPrefix()
    {
        int pos = this.getPos();
        boolean toret = this.matchOrdListEntryPrefix();
        
        this.moveTo( pos );
        return toret;
    }
    
    /** Determines if there is an ordered list entry prefix ahead.
      * @return true if there is an ordered list entry ahead, as in "1. xxx"
      */
    public boolean matchOrdListEntryPrefix()
    {
        int digits = 0;
        
        while( !this.isEod()
            && Character.isDigit( this.getCurrentChar() ) )
        {
            this.skip();
            digits += 1;
        }
        
        boolean toret = ( digits > 0 ) && this.match( '.' );
        return toret;
    }
    
    /** @return true if the current pos is the first token after the last CR. */
    public boolean isStartOfLine()
    {
        int pos = this.getPos();
        
        this.skip( -1 );
        while ( this.getPos() >= 0
              && this.isSpace() )
        {
            this.skip( -1 );
        }
        
        boolean toret = ( this.getPos() <= 0
                       || this.getCurrentChar() == CR );
        this.moveTo( pos );
        return toret;
    }
}
