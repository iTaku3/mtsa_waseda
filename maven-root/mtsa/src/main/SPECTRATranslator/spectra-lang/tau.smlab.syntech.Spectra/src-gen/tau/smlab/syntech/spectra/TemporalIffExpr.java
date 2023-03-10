/**
 * generated by Xtext 2.25.0
 */
package tau.smlab.syntech.spectra;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Temporal Iff Expr</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tau.smlab.syntech.spectra.TemporalIffExpr#getElements <em>Elements</em>}</li>
 *   <li>{@link tau.smlab.syntech.spectra.TemporalIffExpr#getOperator <em>Operator</em>}</li>
 * </ul>
 *
 * @see tau.smlab.syntech.spectra.SpectraPackage#getTemporalIffExpr()
 * @model
 * @generated
 */
public interface TemporalIffExpr extends TemporalExpression
{
  /**
   * Returns the value of the '<em><b>Elements</b></em>' containment reference list.
   * The list contents are of type {@link tau.smlab.syntech.spectra.TemporalExpression}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Elements</em>' containment reference list.
   * @see tau.smlab.syntech.spectra.SpectraPackage#getTemporalIffExpr_Elements()
   * @model containment="true"
   * @generated
   */
  EList<TemporalExpression> getElements();

  /**
   * Returns the value of the '<em><b>Operator</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Operator</em>' attribute.
   * @see #setOperator(String)
   * @see tau.smlab.syntech.spectra.SpectraPackage#getTemporalIffExpr_Operator()
   * @model
   * @generated
   */
  String getOperator();

  /**
   * Sets the value of the '{@link tau.smlab.syntech.spectra.TemporalIffExpr#getOperator <em>Operator</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Operator</em>' attribute.
   * @see #getOperator()
   * @generated
   */
  void setOperator(String value);

} // TemporalIffExpr
