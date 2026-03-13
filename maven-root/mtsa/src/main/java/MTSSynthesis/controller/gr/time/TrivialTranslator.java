package MTSSynthesis.controller.gr.time;


public class TrivialTranslator<S> implements Translator<S, S> {

	@Override
	public S translate(S s) {
		return s;
	}

}
