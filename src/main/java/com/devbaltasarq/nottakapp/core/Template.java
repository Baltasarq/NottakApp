// NottakApp (c) Baltasar 2025 MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core;


import java.util.Map;


/** A template for new text.
  * @author baltasarq
  */
public abstract class Template {
    public final String TEXT_VBLE = "$text";
    public final String DATE_VBLE = "$date";
    
    public Template(String format)
    {
        this.format = format;
    }
    
    public String getFormat()
    {
        return this.format;
    }
    
    /** Replaces the given format with true data.
      * @param data a map with pairs like "$texto", "Cool info."
      * @return the new text-
      */
    public String replace(Map<String, String> data)
    {
        String toret = this.format;
        
        for(String key: data.keySet()) {
            toret = toret.replace( key, data.get( key ) );
        }
        
        return toret;
    }
    
    private final String format;
}
