package ltsa.lts;

public class EmptyLTSOuput implements LTSOutput {

	@Override
	public void clearOutput() {}

	@Override
	public void out(String str) {}

	@Override
	public void outln(String str) {}
}
