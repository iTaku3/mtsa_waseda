/**
 * generated by Xtext 2.25.0
 */
package tau.smlab.syntech.spectra;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Temporal Additive Expr</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tau.smlab.syntech.spectra.TemporalAdditiveExpr#getElements <em>Elements</em>}</li>
 *   <li>{@link tau.smlab.syntech.spectra.TemporalAdditiveExpr#getOperator <em>Operator</em>}</li>
 * </ul>
 *
 * @see tau.smlab.syntech.spectra.SpectraPackage#getTemporalAdditiveExpr()
 * @model
 * @generated
 */
public interface TemporalAdditiveExpr extends TemporalExpression
{
  /**
   * Returns the value of the '<em><b>Elements</b></em>' containment reference list.
   * The list contents are of type {@link tau.smlab.syntech.spectra.TemporalExpression}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Elements</em>' containment reference list.
   * @see tau.smlab.syntech.spectra.SpectraPackage#getTemporalAdditiveExpr_Elements()
   * @model containment="true"
   * @generated
   */
  EList<TemporalExpression> getElements();

  /**
   * Returns the value of the '<em><b>Operator</b></em>' attribute list.
   * The list contents are of type {@link java.lang.String}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Operator</em>' attribute list.
   * @see tau.smlab.syntech.spectra.SpectraPackage#getTemporalAdditiveExpr_Operator()
   * @model unique="false"
   * @generated
   */
  EList<String> getOperator();

} // TemporalAdditiveExpr
