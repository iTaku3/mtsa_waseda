package ltsa.lts;

public class LTSEvent {
    public int kind;
    public Object info;
    public String name;

    public final static int NEWSTATE = 0;
    public final static int INVALID = 1;
    public final static int KILL = 2;
    
    // >>> AMES: Enhanced Modularity
    public final static int NEWCOMPOSITES = 3;
    public final static int NEWPROCESSES = 4;
    public final static int NEWLABELSETS = 5;
    // <<< AMES

    public LTSEvent(int kind, Object info){
        this.kind = kind;
        this.info = info;
    }

    public LTSEvent(int kind, Object info, String name){
        this.kind = kind;
        this.info = info;
        this.name = name;
    }

}
