package ltsa.lts.ltl;

/**
 * Visitor interface to Formula
 * @author gsibay
 *
 */
public interface FormulaVisitor {

	Formula visit(True t);

	Formula visit(False f);

	Formula visit(Proposition p);

	Formula visit(Not n);

	Formula visit(And a);

	Formula visit(Or o);

	Formula visit(Until u);

	Formula visit(Release r);

	Formula visit(Next n);
}
