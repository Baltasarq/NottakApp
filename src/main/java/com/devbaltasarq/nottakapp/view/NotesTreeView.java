// NottakApp (c) Baltasar 2025 MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.view;

import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;


/** The view of the notes tree.
  * @author baltasarq
  */
public class NotesTreeView extends JTree {
    public NotesTreeView(Font font)
    {
        this.font = font;
        if ( font == null ) {
            this.font = new Font( Font.SANS_SERIF, Font.PLAIN, 16 );
        }
        
        this.font = new Font(
                        this.font.getFamily(),
                        this.font.getStyle(),
                        this.font.getSize() - 2 );
        this.setFont( this.font );
        this.build();
    }
    
    private void build()
    {
        this.treeRoot = new DefaultMutableTreeNode( "Notes" );
        final var MODEL = new DefaultTreeModel( this.treeRoot );
        this.setModel( MODEL );
        
        this.treeRoot.setAllowsChildren( true );
        this.setEditable( false );
        this.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
    }
    
    /** @return the root node. */
    public DefaultMutableTreeNode getTreeRoot()
    {
        return this.treeRoot;
    }
    
    private Font font;
    private DefaultMutableTreeNode treeRoot;
}
