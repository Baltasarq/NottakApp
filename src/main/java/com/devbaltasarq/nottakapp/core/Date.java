// NottakApp (c) Baltasar 2025 MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core;

import java.util.Calendar;
import java.util.Locale;


/** Represents a date in ISO-8601
  * @author baltasarq
  */
public class Date {
    private final String[] STR_MONTHS_EN = {
                            "Ene", "Feb", "Mar", "Abr", "May", "Jun",
                            "Jul", "Ago", "Sep", "Oct", "Nov", "Dic" };
    private final String[] STR_MONTHS_ES = {
                            "Ene", "Feb", "Mar", "Abr", "May", "Jun",
                            "Jul", "Ago", "Sep", "Oct", "Nov", "Dic" };
    private final String STR_DATE_FORMAT_US = "%s %02d, %04d";
    private final String STR_DATE_FORMAT = "%02d %s %04d";
    
    // Create a new Date
    public Date(int year, int month, int day)
    {
        this.year = year;
        this.month = month;
        this.day = day;
    }
    
    /** @return the year of the date. */
    public int getYear()
    {
        return this.year;
    }
    
    /** @return the month of the date. */
    public int getMonth()
    {
        return this.month;
    }
    
    /** @return the day of the date. */
    public int getDay()
    {
        return this.day;
    }
    
    /** @return a locale-sensitive long date. */
    public String toLongDateString()
    {
        final var LOCALE_ES = Locale.forLanguageTag( "ES" );
        var strMonths = STR_MONTHS_EN;
        String monthName;
        String toret;
        
        if ( Locale.getDefault() == LOCALE_ES ) {
            strMonths = STR_MONTHS_ES;
        }
        
        monthName = strMonths[ this.getMonth() - 1 ];
        
        if ( Locale.getDefault() == Locale.US ) {
            toret = String.format( STR_DATE_FORMAT_US,
                                            monthName,
                                            this.getDay(),
                                            this.getYear() );
        } else {
            toret = String.format( STR_DATE_FORMAT,
                                                this.getDay(),
                                                monthName,
                                                this.getYear() );
        }
        
        return toret;
    }
    
    /** @return true if this is a date before OTHER, false otherwise.
      * @param OTHER another Date.
      */
    public boolean isLessThan(final Date OTHER)
    {
        boolean toret = true;
        
        if ( this.getYear() > OTHER.getYear() ) {
            toret = false;
        }
        else
        if ( this.getYear() == OTHER.getYear() ) {
            if( this.getMonth() > OTHER.getMonth() ) {
                toret = false;
            }
            else
            if( this.getMonth() == OTHER.getMonth() ) {
                toret = ( this.getDay() < OTHER.getDay() );
            }
        }
        
        return toret;
    }
    
    @Override
    public int hashCode()
    {
        return ( 11 * this.getYear() )
                + ( 7 * this.getMonth() )
                + ( 5 * this.getDay() );
    }

    @Override
    public boolean equals(Object otherObj)
    {
        if ( this == otherObj ) {
            return true;
        }
        
        boolean toret = false;
        
        if ( otherObj instanceof final Date OTHER ) {
            toret = this.getDay() == OTHER.getDay()
                    && this.getMonth() == OTHER.getMonth()
                    && this.getDay() == OTHER.getDay();
        }
        
        return toret;
    }
    
    /** @return the date in ISO format. */
    @Override
    public String toString()
    {
        return String.format("%04d-%02d-%02d",
                                this.getYear(),
                                this.getMonth(),
                                this.getDay() );
    }
    
    /** @return the date given from the system. */
    public static Date fromSystem()
    {
        final Calendar CALENDAR = Calendar.getInstance();
        
        int day = CALENDAR.get( Calendar.DAY_OF_MONTH );
        int month = CALENDAR.get( Calendar.MONTH ) + 1;
        int year = CALENDAR.get( Calendar.YEAR );
        
        return new Date( year, month, day );
    }
    
    /** @return the date, or throw if incorrect.
      * @param strDate a date, in the ISO format: yyyy-mm-dd.
      * @throws IllegalArgumentException if the date format is incorrect.
      */
    public static Date fromString(String strDate) throws IllegalArgumentException
    {
        final var EXCEPTION
                    = new IllegalArgumentException(
                                    strDate + "does not comply: yyyy-mm-dd" );
        String[] parts = strDate.split( "-" );
        int day;
        int month;
        int year;
        
        if ( parts.length != 3 ) {
            throw EXCEPTION;
        }
        
        try {
            day = Integer.parseInt( parts[ 2 ] );
            month = Integer.parseInt( parts[ 1 ] );
            year = Integer.parseInt( parts[ 0 ] );
        } catch(NumberFormatException exc) {
            throw EXCEPTION;
        }
        
        return new Date( year, month, day );
    }
    
    private final int day;
    private final int month;
    private final int year;
}
