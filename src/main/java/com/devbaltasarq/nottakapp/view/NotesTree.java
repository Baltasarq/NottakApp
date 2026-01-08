// NottakApp (c) Baltasar 2025 MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.view;


import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.function.Consumer;

import com.devbaltasarq.nottakapp.core.NoteProxy;
import com.devbaltasarq.nottakapp.core.Notebook;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.logging.Logger;


/** A hierarchical view of available notes.
  * @author baltasarq
  */
public class NotesTree {
    private static final Logger LOG = Logger.getLogger( NotesTree.class.getName() );

    public NotesTree(Notebook nb)
    {
        this( new NotesTreeView( null ), nb );
    }
    
    public NotesTree(NotesTreeView notesTree, Notebook nb)
    {
        this.notebook = nb;
        this.notesTree = notesTree;
        this.notesTree.addTreeSelectionListener(
                        (evt) -> {
                            DefaultMutableTreeNode selectedNode = 
                                   (DefaultMutableTreeNode)
                                       this.notesTree.getLastSelectedPathComponent();

                            if ( selectedNode != null
                              && selectedNode != this.getRoot() )
                            {
                                this.selectedAction.accept(
                                        (NoteProxy)
                                            selectedNode.getUserObject() );
                            }
                        });
        this.notesTree.addFocusListener( this.focusListener );
    }
   
    /** @return the corresponding view. */
    public NotesTreeView getView()
    {
        return notesTree;
    }
    
    /** @return the root node of the tree. */
    public DefaultMutableTreeNode getRoot()
    {
        return this.notesTree.getTreeRoot();
    }
    
    public void expandAll()
    {
        this.notesTree.expandRow( 0 );
    }
    
    /** Removes all the nodes (except the root one). */
    public void removeAll()
    {
        this.getRoot().removeAllChildren();
    }
    
    /** @return the notebook shown. */
    public Notebook getNotebook()
    {
        return this.notebook;
    }
    
    /** Adds a new node to the tree root node.
      * @param note the new children note for root.
      */
    public void add(NoteProxy note)
    {
        final var MODEL = (DefaultTreeModel) this.getView().getModel();
        final var NEW_NODE = new DefaultMutableTreeNode( note );
        final var ROOT = this.getRoot();
        
        MODEL.insertNodeInto( NEW_NODE, ROOT, ROOT.getChildCount() );
        this.expandAll();
    }
    
    public void update()
    {
        final var MODEL = (DefaultTreeModel) this.getView().getModel();
        final var ROOT = this.getRoot();
        
        MODEL.nodeStructureChanged( ROOT );
        MODEL.reload();
    }
    
    public void refreshAll()
    {
        this.removeAll();

        for(NoteProxy np: this.notebook.getAllNotes()) {
            this.add( np );
        }
        
        this.update();
    }
    
    /** Replaces the selected action event.
      * @param action a lambda accepting a node.
      */
    public void setSelectedAction(Consumer<NoteProxy> action)
    {
        this.selectedAction = action;
    }
    
    private final Notebook notebook;
    private final NotesTreeView notesTree;
    private Consumer<NoteProxy> selectedAction = (n) -> {};
    private final FocusListener focusListener = new FocusListener() {
        @Override
        public void focusGained(FocusEvent e)
        {
            NotesTree.this.update();
        }
        @Override
        public void focusLost(FocusEvent e)
        {
        }
    };
}
