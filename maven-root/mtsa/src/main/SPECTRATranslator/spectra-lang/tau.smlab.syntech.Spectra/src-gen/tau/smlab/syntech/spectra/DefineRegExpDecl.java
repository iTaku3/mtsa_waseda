/**
 * generated by Xtext 2.25.0
 */
package tau.smlab.syntech.spectra;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Define Reg Exp Decl</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link tau.smlab.syntech.spectra.DefineRegExpDecl#getExp <em>Exp</em>}</li>
 * </ul>
 *
 * @see tau.smlab.syntech.spectra.SpectraPackage#getDefineRegExpDecl()
 * @model
 * @generated
 */
public interface DefineRegExpDecl extends Referrable
{
  /**
   * Returns the value of the '<em><b>Exp</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Exp</em>' containment reference.
   * @see #setExp(RegExp)
   * @see tau.smlab.syntech.spectra.SpectraPackage#getDefineRegExpDecl_Exp()
   * @model containment="true"
   * @generated
   */
  RegExp getExp();

  /**
   * Sets the value of the '{@link tau.smlab.syntech.spectra.DefineRegExpDecl#getExp <em>Exp</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Exp</em>' containment reference.
   * @see #getExp()
   * @generated
   */
  void setExp(RegExp value);

} // DefineRegExpDecl
