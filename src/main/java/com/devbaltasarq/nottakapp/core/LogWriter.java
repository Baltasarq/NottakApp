// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core;


import java.util.logging.StreamHandler;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import java.util.function.Consumer;
import java.util.Calendar;


/** A writer handler for a java.util.logging logger.
  * @author baltasarq
  */
public class LogWriter extends StreamHandler {
    public LogWriter(Logger log, Consumer<String> writer)
    {
        this.log = log;
        this.writer = writer;
    }
    
    @Override
    public void publish(LogRecord rec)
    {
        final var DATE = Calendar.getInstance();

        DATE.setTimeInMillis( rec.getMillis() );        
        String STR_DATE = String.format(
                "%4d-%02d-%02dT%02d:%02d:%02d",
                DATE.get( Calendar.YEAR ),
                DATE.get( Calendar.MONTH ),
                DATE.get( Calendar.DAY_OF_MONTH ),
                DATE.get( Calendar.HOUR_OF_DAY ),
                DATE.get( Calendar.MINUTE ),
                DATE.get( Calendar.SECOND )
        );
        
        this.writer.accept(
                STR_DATE
                + "/" + rec.getLevel().getName().toUpperCase()
                + ": " + rec.getMessage() );
    }
    
    /** @return the logger being used. */
    public Logger getLogger()
    {
        return this.log;
    }
    
    /** @return the writer being used. */
    public Consumer<String> getWriter()
    {
        return this.writer;
    }
    
    private final Logger log;
    private final Consumer<String> writer;
}
