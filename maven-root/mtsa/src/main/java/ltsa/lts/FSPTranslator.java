package ltsa.lts;

public class FSPTranslator extends AbstractTranslator {


    @Override
    protected void doTranslate() {
        CompactState composition = base_composite.composition;
        PrintTransitions transitions = new PrintTransitions(composition);
        String fsp = transitions.getFSP(9000);
        add(fsp);
    }
}
