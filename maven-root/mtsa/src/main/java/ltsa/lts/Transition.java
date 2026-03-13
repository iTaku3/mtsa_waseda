package ltsa.lts;

public class Transition {
    int from;
    int to;
    Symbol event;

    Transition() {}

    Transition(int from) {this.from = from;}

    Transition(int from, Symbol event, int to) {
        this.from =from;
        this.to = to;
        this.event = event;
    }

    public String toString() {
        return ""+from+" "+event+" "+to;
    }
}
