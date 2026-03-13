package ltsa.lts;

import java.util.BitSet;
import java.util.Vector;

public interface Animator {

    /**
    * initialises the Animator with the list of
    * actions that are to be controlled
    * menu is a vector of strings
    * returns bitset of eligible menu actions
    */
    public BitSet initialise(Vector menu);

    /*
    * returns the alphabet of names from menu
    * that are contained in the model alphabet
    * indexes in this string array are consistent with
    * the bitsets returned by the step operations.
    * if menu was null then the complete model alphabet is returned
    */
    public String[] getMenuNames();

    /*
    * returns the model alphabet
    */
    public String[] getAllNames();

    /**
    * causes the Animator to take a single step
    * by choosing one of the eligible menu actions
    * returns a bitset of eligible menu actions
    */
    public BitSet menuStep(int choice);

    /**
    * causes the Animator to take a single step
    * by choosing one of the eligible non-menu actions
    */
    public BitSet singleStep();

    /**
    * return the action number in AllNames
    * of the last action chosen and executed
    */
    public int actionChosen();

    /**
    * return the action name
    * of the last action chosen and executed
    */
    public String actionNameChosen();


    /**
    * return true if error state has been reached
    */
    public boolean isError();
    
    /**
    * return true if END state has been reached
    */
    public boolean isEnd();

    /**
    * returns true if there is an eligible action
    * that is not a menu action
    */
    public boolean nonMenuChoice();


    /**
    * returns the bitset of menu actions with higher/lower priority
    */
    public BitSet getPriorityActions();

    /**
    * returns true if priority actions are low priority
    * flase if they are low priority
    */
    public boolean getPriority();

    /*
    * prints message on LTSA window
    */
    public void message(String msg);
    
    /*
    * -- these are used for replaying error traces
    */
    
    /* 
    * returns true if error trace exists
    */
    public boolean hasErrorTrace();
    
    /*
    *returns true if next element in the trace is eligible
    */
     public boolean traceChoice();
     
     /*
     *execute next step in error trace
     */
     public BitSet traceStep();
}