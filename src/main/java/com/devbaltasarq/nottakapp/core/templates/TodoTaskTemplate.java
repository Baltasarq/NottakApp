// NottakApp (c) Baltasar 2025 MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.templates;


import com.devbaltasarq.nottakapp.core.Template;


/** A template for the new TO-DO task.
  * @author baltasarq
  */
public class TodoTaskTemplate extends Template {
    public TodoTaskTemplate()
    {
        super( "- [ ] $text" );
    }
}
