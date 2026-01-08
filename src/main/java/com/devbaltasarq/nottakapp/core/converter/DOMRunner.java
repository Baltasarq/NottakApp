// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core.converter;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

import com.devbaltasarq.nottakapp.core.converter.elements.Root;


/** The visitor pattern.
  * @author baltasarq
  */
public class DOMRunner {
    public DOMRunner(Root root)
    {
        this.root = root;
        this.visitor = e -> {};
        this.pending = new ArrayList<>( root.count() * 2 );
    }
    
    /** Change the lambda for the visitor.
      * @param r a new consumer for the element.
      */
    public void setVisitor(Consumer<Element> r)
    {
        this.visitor = r;
    }
    
    /** Execute the running all over the elements. */
    public void run()
    {
        // Iterative deep first
        this.pending.clear();
        this.pending.add( this.root );
        
        while( !this.pending.isEmpty() ) {
            this.current = this.pending.getLast();

            if ( !this.current.isClosing() ) {
                if ( this.current != this.root ) {
                    this.pending.add( this.current.copyAsClosing() );
                }

                this.pending.addAll( this.current.getAll().reversed() );
            }
            
            this.visitor.accept(this.current );
            this.pending.remove( this.current );
        }
    }
    
    /** @return the root element of the DOM. */
    public Root getRoot()
    {
        return this.root;
    }
    
    private Consumer<Element> visitor;
    private Element current;
    private final Root root;
    private final List<Element> pending;
}
