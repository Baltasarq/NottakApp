// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter;


import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Logger;


/** A scanner for HTML documents.
  * @author baltasarq
  */
public class HtmlScanner {
    final private static Logger LOG = Logger.getLogger( HtmlScanner.class.getSimpleName() );
    private final char CH_OPEN_TAG = '<';
    private final char CH_CLOSE_TAG = '>';
    private final char CH_SPECIAL_CHAR = '&';
    private final char CH_SEMICOLON = ';';
    
    public enum TokenType {
        TEXT,
        BLANK,
        TAG,
        CHAR,
        END
    };
    
    public HtmlScanner(String text)
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
    public String getWholeText()
    {
        return this.text;
    }
    
    /** @return the current pos. */
    public int getPos()
    {
        return this.pos;
    }
    
    /** Skips all spaces, including CR and LF. */
    public void skipSpaces()
    {
        while( !this.isEod()
            && Character.isWhitespace( this.text.charAt( pos ) ) )
        {
            ++pos;
        }
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
    
    /** Get the current token type at cursor.
      * @return the type of the token, as a TokenType.
      */ 
    public TokenType getTokenType()
    {
        TokenType toret = TokenType.END;
        
        if ( !this.isEod() ) {
            final var CH = this.getCurrentChar();
            
            if ( Character.isSpaceChar( CH )) {
                toret = TokenType.BLANK;
            }
            else {
                switch ( this.getCurrentChar() ) {
                    case CH_OPEN_TAG -> toret = TokenType.TAG;
                    case CH_SPECIAL_CHAR -> toret = TokenType.CHAR;
                    default -> toret = TokenType.TEXT;
                }
            }
        }
        
        return toret;
    }

    /** @return the token type of the next token. */
    public TokenType getNextTokenType()
    {
        TokenType toret = TokenType.END;
               
        if ( !this.isEod() ) {
            int oldPos = this.getPos();
            
            this.skipSpaces();
            toret = this.getTokenType();
            this.pos = oldPos;
        }
        
        return toret;
    }
    
    /** @return the name of the tag if there is a tag ahead, null otherwise. */
    public String getNextTag()
    {
        String toret = null;
        
        if ( this.getNextTokenType() == HtmlScanner.TokenType.TAG ) {
            int oldPos = this.getPos();
            
            this.skipSpaces();
            toret = this.readTag();
            this.moveTo( oldPos );
        }
        
        return toret;
    }
    
    /** @return true if the next tag is the given tagName, false otherwise.
      * @param tagName the given tag name to compare.
      */
    public boolean isNextTag(String tagName)
    {
        String nextTag = this.getNextTag();
        
        tagName = tagName.trim().toLowerCase();
        return nextTag != null && nextTag.equals( tagName );
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
    
    /** @return the text at current cursor, or blank. */
    public String readText()
    {
        final var TORET = new StringBuilder();
        boolean tagFound = false;
        
        this.skipSpaces();
        while( !this.isEod()
            && !tagFound )
        {
            switch ( this.getTokenType() ) {
                case TEXT -> {
                    TORET.append( this.getCurrentChar() );
                    this.skip();
                }
                case BLANK -> {
                    TORET.append( ' ' );
                    this.skipSpaces();
                }
                case CHAR -> {
                    TORET.append( this.readSpecialChar() );
                }
                default -> {
                    tagFound = true;
                }
            }
        }
        
        return TORET.toString().trim();
    }
    
    /** @return the tag at cursor, in lower case, or blank. */
    public String readTag()
    {
        final var TORET = new StringBuilder();
        
        this.skipSpaces();
        if ( this.match( CH_OPEN_TAG ) ) {
            // Read tag label
            if ( !this.isEod() ) {
                boolean inAttrs = false;
                TORET.append( this.getCurrentChar() );
                this.skip();

                while ( !this.isEod()
                     && this.getCurrentChar() != CH_CLOSE_TAG )
                {
                    final char CH = this.getCurrentChar();

                    this.skip();
                    
                    if ( inAttrs ) {
                        continue;
                    }
                    
                    if ( !inAttrs
                       && CH == ' ' )
                    {
                        inAttrs = true;
                    }
                    
                    TORET.append( CH );
                }
            }
                
            // Ensure we are past the closing tag char.
            if ( !this.match( CH_CLOSE_TAG ) ) {
                // The input ended before ending the tag
                TORET.setLength( 0 );
            }
        }
        
        return TORET.toString().trim().toLowerCase();
    }
    
    /** @return the special char at current cursor pos, or null char. */
    public char readSpecialChar()
    {
        final var TOKEN = new StringBuilder( 10 );
        char toret = '\0';
        
        this.skipSpaces();
        
        if ( this.match( CH_SPECIAL_CHAR ) ) {
            // Read special char name or code.
            while ( !this.isEod()
                 && this.getCurrentChar() != CH_SEMICOLON )
            {
                TOKEN.append( this.getCurrentChar() );
                this.skip();
            }
            
            // Make sure we read past the end of the special char.
            if ( !this.match( CH_SEMICOLON ) ) {
                TOKEN.setLength( 0 );
            }
            
            // The end result
            var result = charFromSpecialName( TOKEN.toString() );
            
            if ( result != null ) {
                toret = result;
            } else {
                throw new NoSuchElementException( "no special symbol: " + TOKEN );
            }
        }
        
        return toret;
    }
    
    /** Moves the cursor to the given position.
      * Moves the cursor to 0 if pos is negative.
      * Moves the cursor to the last position if pos >= size.
      * @param pos the new position.
      */
    public void moveTo(int pos)
    {
        this.pos = Math.max( 0, Math.min( pos, this.size() - 1 ) );
    }
    
    /** Gets the corresponding char for this special name, or null.
      * for instance, 'euro' -> €.
      * @param token the string containing the name of the special char.
      * @return the corresponding char code, or null if not found.
      */
    public static Character charFromSpecialName(String token)
    {
        Character toret = null;
        token = token.trim();
        
        if ( token.charAt( 0 ) == '#' ) {
            int radix = 10;
            token = token.substring( 1 );
            char ch = Character.toLowerCase( token.charAt( 0 ) );
            
            if ( ch == 'x' ) {
                radix = 16;
                token = token.substring( 1 );
            }
            
            try {
                toret = Character.forDigit(
                            Integer.parseInt( token.substring( 1 ) ),
                                                    radix );
            } catch(NumberFormatException exc) {
                LOG.warning( String.format(
                                        "does not contain a number: '%s'",
                                        token ));
            }
        } else {
            toret = SPECIAL_CHAR_NAMES.get( token );
        }

        return toret;
    }

    private int pos;
    private final String text;
    private final static Map<String, Character> SPECIAL_CHAR_NAMES =
                                    Map.ofEntries(
                                        Map.entry( "Tab", '\u0009' ),
                                        Map.entry( "NewLine", '\r' ),
                                        Map.entry( "excl", '\u0021' ),
                                        Map.entry( "num", '\u0023' ),
                                        Map.entry( "dollar", '\u0024' ),
                                        Map.entry( "apos", '\'' ),
                                        Map.entry( "lpar", '\u0028' ),
                                        Map.entry( "rpar", '\u0029' ),
                                        Map.entry( "plus", '\u002B' ),
                                        Map.entry( "comma", '\u002C' ),
                                        Map.entry( "period", '\u002E' ),
                                        Map.entry( "sol", '\u002F' ),
                                        Map.entry( "colon", '\u003A' ),
                                        Map.entry( "semi", '\u003B' ),
                                        Map.entry( "equals", '\u003D' ),
                                        Map.entry( "quest", '\u003F' ),
                                        Map.entry( "commat", '\u0040' ),
                                        Map.entry( "lsqb", '\u005B' ),
                                        Map.entry( "bsol", '\\' ),
                                        Map.entry( "rsqb", '\u005D' ),
                                        Map.entry( "Hat", '\u005E' ),
                                        Map.entry( "lowbar", '\u005F' ),
                                        Map.entry( "underbar", '\u005F' ),
                                        Map.entry( "lcub", '\u007B' ),
                                        Map.entry( "lbrace", '\u007B' ),
                                        Map.entry( "rcub", '\u007D' ),
                                        Map.entry( "rbrace", '\u007D' ),
                                        Map.entry( "verbar", '\u007C' ),
                                        Map.entry( "vert", '\u007C' ),
                                        Map.entry( "VerticalBar", '\u007C' ),
                                        Map.entry( "dash", '\u2010' ),
                                        Map.entry( "ndash", '\u2013' ),
                                        Map.entry( "mdash", '\u2014' ),
                                        Map.entry( "horbar", '\u2015' ),
                                        Map.entry( "minus", '\u2212' ),                                    
                                        Map.entry( "quot", '\u0022' ),
                                        Map.entry( "amp", '\u0026' ),
                                        Map.entry( "lt", '\u003C' ),
                                        Map.entry( "gt", '\u003E' ),
                                        Map.entry( "nbsp", '\u00A0' ),
                                        Map.entry( "iexcl", '\u00A1' ),
                                        Map.entry( "cent", '\u00A2' ),
                                        Map.entry( "pound", '\u00A3' ),
                                        Map.entry( "curren", '\u00A4' ),
                                        Map.entry( "yen", '\u00A5' ),
                                        Map.entry( "brvbar", '\u00A6' ),
                                        Map.entry( "sect", '\u00A7' ),
                                        Map.entry( "uml", '\u00A8' ),
                                        Map.entry( "copy", '\u00A9' ),
                                        Map.entry( "ordf", '\u00AA' ),
                                        Map.entry( "laquo", '\u00AB' ),
                                        Map.entry( "not", '\u00AC' ),
                                        Map.entry( "shy", '\u00AD' ),
                                        Map.entry( "reg", '\u00AE' ),
                                        Map.entry( "trade", '\u00AE' ),
                                        Map.entry( "macr", '\u00AF' ),
                                        Map.entry( "deg", '\u00B0' ),
                                        Map.entry( "plusmn", '\u00B1' ),
                                        Map.entry( "sup2", '\u00B2' ),
                                        Map.entry( "sup3", '\u00B3' ),
                                        Map.entry( "acute", '\u00B4' ),
                                        Map.entry( "micro", '\u00B5' ),
                                        Map.entry( "para", '\u00B6' ),
                                        Map.entry( "middot", '\u00B7' ),
                                        Map.entry( "cedil", '\u00B8' ),
                                        Map.entry( "sup1", '\u00B9' ),
                                        Map.entry( "ordm", '\u00BA' ),
                                        Map.entry( "raquo", '\u00BB' ),
                                        Map.entry( "frac14", '\u00BC' ),
                                        Map.entry( "frac12", '\u00BD' ),
                                        Map.entry( "frac34", '\u00BE' ),
                                        Map.entry( "iquest", '\u00BF' ),
                                        Map.entry( "Agrave", '\u00C0' ),
                                        Map.entry( "Aacute", '\u00C1' ),
                                        Map.entry( "Acirc", '\u00C2' ),
                                        Map.entry( "Atilde", '\u00C3' ),
                                        Map.entry( "Auml", '\u00C4' ),
                                        Map.entry( "Aring", '\u00C5' ),
                                        Map.entry( "AElig", '\u00C6' ),
                                        Map.entry( "Ccedil", '\u00C7' ),
                                        Map.entry( "Egrave", '\u00C8' ),
                                        Map.entry( "Eacute", '\u00C9' ),
                                        Map.entry( "Ecirc", '\u00CA' ),
                                        Map.entry( "Euml", '\u00CB' ),
                                        Map.entry( "Igrave", '\u00CC' ),
                                        Map.entry( "Iacute", '\u00CD' ),
                                        Map.entry( "Icirc", '\u00CE' ),
                                        Map.entry( "Iuml", '\u00CF' ),
                                        Map.entry( "ETH", '\u00D0' ),
                                        Map.entry( "Ntilde", '\u00D1' ),
                                        Map.entry( "Ograve", '\u00D2' ),
                                        Map.entry( "Oacute", '\u00D3' ),
                                        Map.entry( "Ocirc", '\u00D4' ),
                                        Map.entry( "Otilde", '\u00D5' ),
                                        Map.entry( "Ouml", '\u00D6' ),
                                        Map.entry( "times", '\u00D7' ),
                                        Map.entry( "Oslash", '\u00D8' ),
                                        Map.entry( "Ugrave", '\u00D9' ),
                                        Map.entry( "Uacute", '\u00DA' ),
                                        Map.entry( "Ucirc", '\u00DB' ),
                                        Map.entry( "Uuml", '\u00DC' ),
                                        Map.entry( "Yacute", '\u00DD' ),
                                        Map.entry( "THORN", '\u00DE' ),
                                        Map.entry( "szlig", '\u00DF' ),
                                        Map.entry( "agrave", '\u00E0' ),
                                        Map.entry( "aacute", '\u00E1' ),
                                        Map.entry( "acirc", '\u00E2' ),
                                        Map.entry( "atilde", '\u00E3' ),
                                        Map.entry( "auml", '\u00E4' ),
                                        Map.entry( "aring", '\u00E5' ),
                                        Map.entry( "aelig", '\u00E6' ),
                                        Map.entry( "ccedil", '\u00E7' ),
                                        Map.entry( "egrave", '\u00E8' ),
                                        Map.entry( "eacute", '\u00E9' ),
                                        Map.entry( "ecirc", '\u00EA' ),
                                        Map.entry( "euml", '\u00EB' ),
                                        Map.entry( "igrave", '\u00EC' ),
                                        Map.entry( "iacute", '\u00ED' ),
                                        Map.entry( "icirc", '\u00EE' ),
                                        Map.entry( "iuml", '\u00EF' ),
                                        Map.entry( "eth", '\u00F0' ),
                                        Map.entry( "ntilde", '\u00F1' ),
                                        Map.entry( "ograve", '\u00F2' ),
                                        Map.entry( "oacute", '\u00F3' ),
                                        Map.entry( "ocirc", '\u00F4' ),
                                        Map.entry( "otilde", '\u00F5' ),
                                        Map.entry( "ouml", '\u00F6' ),
                                        Map.entry( "divide", '\u00F7' ),
                                        Map.entry( "oslash", '\u00F8' ),
                                        Map.entry( "ugrave", '\u00F9' ),
                                        Map.entry( "uacute", '\u00FA' ),
                                        Map.entry( "ucirc", '\u00FB' ),
                                        Map.entry( "uuml", '\u00FC' ),
                                        Map.entry( "yacute", '\u00FD' ),
                                        Map.entry( "thorn", '\u00FE' ),
                                        Map.entry( "yuml", '\u00FF' ),
                                        Map.entry( "euro", '\u20AC' ),
                                        Map.entry( "fnof", '\u0192' ),
                                        Map.entry( "alpha", '\u03B1' ),
                                        Map.entry( "beta", '\u03B2' ),
                                        Map.entry( "gamma", '\u03B3' ),
                                        Map.entry( "delta", '\u03B4' ),
                                        Map.entry( "epsilon", '\u03B5' ),
                                        Map.entry( "hellip", '\u2026' ),
                                        Map.entry( "larr", '\u2190' ),
                                        Map.entry( "rarr", '\u2192' ),
                                        Map.entry( "uarr", '\u2191' ),
                                        Map.entry( "darr", '\u2193' ),
                                        Map.entry( "harr", '\u2194' ),
                                        Map.entry( "ctdot", '\u22EF' ),
                                        Map.entry( "bull", '\u2022' ),
                                        Map.entry( "prime", '\u2032' ),
                                        Map.entry( "spade", '\u2660' ),
                                        Map.entry( "club", '\u2663' ),
                                        Map.entry( "heart", '\u2665' ),
                                        Map.entry( "diamond", '\u2666' ),
                                        Map.entry( "zeta", '\u03B6' ),
                                        Map.entry( "eta", '\u03B7' ),
                                        Map.entry( "theta", '\u03B8' ),
                                        Map.entry( "iota", '\u03B9' ),
                                        Map.entry( "kappa", '\u03BA' ),
                                        Map.entry( "lambda", '\u03BB' ),
                                        Map.entry( "mu", '\u03BC' ),
                                        Map.entry( "nu", '\u03BD' ),
                                        Map.entry( "xi", '\u03BE' ),
                                        Map.entry( "omicron", '\u03BF' ),
                                        Map.entry( "pi", '\u03C0' ),
                                        Map.entry( "rho", '\u03C1' ),
                                        Map.entry( "sigma", '\u03C3' ),
                                        Map.entry( "tau", '\u03C4' ),
                                        Map.entry( "upsilon", '\u03C5' ),
                                        Map.entry( "phi", '\u03C6' ),
                                        Map.entry( "chi", '\u03C7' ),
                                        Map.entry( "psi", '\u03C8' ),
                                        Map.entry( "omega", '\u03C9' ),
                                        Map.entry( "thetasym", '\u03D1' ));
}
