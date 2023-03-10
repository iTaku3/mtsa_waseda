/**
 * generated by Xtext 2.25.0
 */
package tau.smlab.syntech.spectra.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import tau.smlab.syntech.spectra.Counter;
import tau.smlab.syntech.spectra.OverflowMethod;
import tau.smlab.syntech.spectra.Referrable;
import tau.smlab.syntech.spectra.SpectraPackage;
import tau.smlab.syntech.spectra.Subrange;
import tau.smlab.syntech.spectra.TemporalExpression;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Counter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link tau.smlab.syntech.spectra.impl.CounterImpl#getName <em>Name</em>}</li>
 *   <li>{@link tau.smlab.syntech.spectra.impl.CounterImpl#getRange <em>Range</em>}</li>
 *   <li>{@link tau.smlab.syntech.spectra.impl.CounterImpl#getInitial <em>Initial</em>}</li>
 *   <li>{@link tau.smlab.syntech.spectra.impl.CounterImpl#getResetPred <em>Reset Pred</em>}</li>
 *   <li>{@link tau.smlab.syntech.spectra.impl.CounterImpl#getIncPred <em>Inc Pred</em>}</li>
 *   <li>{@link tau.smlab.syntech.spectra.impl.CounterImpl#getDecPred <em>Dec Pred</em>}</li>
 *   <li>{@link tau.smlab.syntech.spectra.impl.CounterImpl#getOverflowMethod <em>Overflow Method</em>}</li>
 *   <li>{@link tau.smlab.syntech.spectra.impl.CounterImpl#getUnderflowMethod <em>Underflow Method</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CounterImpl extends DeclImpl implements Counter
{
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getRange() <em>Range</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRange()
   * @generated
   * @ordered
   */
  protected Subrange range;

  /**
   * The cached value of the '{@link #getInitial() <em>Initial</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInitial()
   * @generated
   * @ordered
   */
  protected EList<TemporalExpression> initial;

  /**
   * The cached value of the '{@link #getResetPred() <em>Reset Pred</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getResetPred()
   * @generated
   * @ordered
   */
  protected EList<TemporalExpression> resetPred;

  /**
   * The cached value of the '{@link #getIncPred() <em>Inc Pred</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getIncPred()
   * @generated
   * @ordered
   */
  protected EList<TemporalExpression> incPred;

  /**
   * The cached value of the '{@link #getDecPred() <em>Dec Pred</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDecPred()
   * @generated
   * @ordered
   */
  protected EList<TemporalExpression> decPred;

  /**
   * The cached value of the '{@link #getOverflowMethod() <em>Overflow Method</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOverflowMethod()
   * @generated
   * @ordered
   */
  protected EList<OverflowMethod> overflowMethod;

  /**
   * The cached value of the '{@link #getUnderflowMethod() <em>Underflow Method</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUnderflowMethod()
   * @generated
   * @ordered
   */
  protected EList<OverflowMethod> underflowMethod;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected CounterImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return SpectraPackage.Literals.COUNTER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SpectraPackage.COUNTER__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Subrange getRange()
  {
    return range;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetRange(Subrange newRange, NotificationChain msgs)
  {
    Subrange oldRange = range;
    range = newRange;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, SpectraPackage.COUNTER__RANGE, oldRange, newRange);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setRange(Subrange newRange)
  {
    if (newRange != range)
    {
      NotificationChain msgs = null;
      if (range != null)
        msgs = ((InternalEObject)range).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - SpectraPackage.COUNTER__RANGE, null, msgs);
      if (newRange != null)
        msgs = ((InternalEObject)newRange).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - SpectraPackage.COUNTER__RANGE, null, msgs);
      msgs = basicSetRange(newRange, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SpectraPackage.COUNTER__RANGE, newRange, newRange));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<TemporalExpression> getInitial()
  {
    if (initial == null)
    {
      initial = new EObjectContainmentEList<TemporalExpression>(TemporalExpression.class, this, SpectraPackage.COUNTER__INITIAL);
    }
    return initial;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<TemporalExpression> getResetPred()
  {
    if (resetPred == null)
    {
      resetPred = new EObjectContainmentEList<TemporalExpression>(TemporalExpression.class, this, SpectraPackage.COUNTER__RESET_PRED);
    }
    return resetPred;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<TemporalExpression> getIncPred()
  {
    if (incPred == null)
    {
      incPred = new EObjectContainmentEList<TemporalExpression>(TemporalExpression.class, this, SpectraPackage.COUNTER__INC_PRED);
    }
    return incPred;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<TemporalExpression> getDecPred()
  {
    if (decPred == null)
    {
      decPred = new EObjectContainmentEList<TemporalExpression>(TemporalExpression.class, this, SpectraPackage.COUNTER__DEC_PRED);
    }
    return decPred;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<OverflowMethod> getOverflowMethod()
  {
    if (overflowMethod == null)
    {
      overflowMethod = new EDataTypeEList<OverflowMethod>(OverflowMethod.class, this, SpectraPackage.COUNTER__OVERFLOW_METHOD);
    }
    return overflowMethod;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<OverflowMethod> getUnderflowMethod()
  {
    if (underflowMethod == null)
    {
      underflowMethod = new EDataTypeEList<OverflowMethod>(OverflowMethod.class, this, SpectraPackage.COUNTER__UNDERFLOW_METHOD);
    }
    return underflowMethod;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case SpectraPackage.COUNTER__RANGE:
        return basicSetRange(null, msgs);
      case SpectraPackage.COUNTER__INITIAL:
        return ((InternalEList<?>)getInitial()).basicRemove(otherEnd, msgs);
      case SpectraPackage.COUNTER__RESET_PRED:
        return ((InternalEList<?>)getResetPred()).basicRemove(otherEnd, msgs);
      case SpectraPackage.COUNTER__INC_PRED:
        return ((InternalEList<?>)getIncPred()).basicRemove(otherEnd, msgs);
      case SpectraPackage.COUNTER__DEC_PRED:
        return ((InternalEList<?>)getDecPred()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case SpectraPackage.COUNTER__NAME:
        return getName();
      case SpectraPackage.COUNTER__RANGE:
        return getRange();
      case SpectraPackage.COUNTER__INITIAL:
        return getInitial();
      case SpectraPackage.COUNTER__RESET_PRED:
        return getResetPred();
      case SpectraPackage.COUNTER__INC_PRED:
        return getIncPred();
      case SpectraPackage.COUNTER__DEC_PRED:
        return getDecPred();
      case SpectraPackage.COUNTER__OVERFLOW_METHOD:
        return getOverflowMethod();
      case SpectraPackage.COUNTER__UNDERFLOW_METHOD:
        return getUnderflowMethod();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case SpectraPackage.COUNTER__NAME:
        setName((String)newValue);
        return;
      case SpectraPackage.COUNTER__RANGE:
        setRange((Subrange)newValue);
        return;
      case SpectraPackage.COUNTER__INITIAL:
        getInitial().clear();
        getInitial().addAll((Collection<? extends TemporalExpression>)newValue);
        return;
      case SpectraPackage.COUNTER__RESET_PRED:
        getResetPred().clear();
        getResetPred().addAll((Collection<? extends TemporalExpression>)newValue);
        return;
      case SpectraPackage.COUNTER__INC_PRED:
        getIncPred().clear();
        getIncPred().addAll((Collection<? extends TemporalExpression>)newValue);
        return;
      case SpectraPackage.COUNTER__DEC_PRED:
        getDecPred().clear();
        getDecPred().addAll((Collection<? extends TemporalExpression>)newValue);
        return;
      case SpectraPackage.COUNTER__OVERFLOW_METHOD:
        getOverflowMethod().clear();
        getOverflowMethod().addAll((Collection<? extends OverflowMethod>)newValue);
        return;
      case SpectraPackage.COUNTER__UNDERFLOW_METHOD:
        getUnderflowMethod().clear();
        getUnderflowMethod().addAll((Collection<? extends OverflowMethod>)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case SpectraPackage.COUNTER__NAME:
        setName(NAME_EDEFAULT);
        return;
      case SpectraPackage.COUNTER__RANGE:
        setRange((Subrange)null);
        return;
      case SpectraPackage.COUNTER__INITIAL:
        getInitial().clear();
        return;
      case SpectraPackage.COUNTER__RESET_PRED:
        getResetPred().clear();
        return;
      case SpectraPackage.COUNTER__INC_PRED:
        getIncPred().clear();
        return;
      case SpectraPackage.COUNTER__DEC_PRED:
        getDecPred().clear();
        return;
      case SpectraPackage.COUNTER__OVERFLOW_METHOD:
        getOverflowMethod().clear();
        return;
      case SpectraPackage.COUNTER__UNDERFLOW_METHOD:
        getUnderflowMethod().clear();
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case SpectraPackage.COUNTER__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case SpectraPackage.COUNTER__RANGE:
        return range != null;
      case SpectraPackage.COUNTER__INITIAL:
        return initial != null && !initial.isEmpty();
      case SpectraPackage.COUNTER__RESET_PRED:
        return resetPred != null && !resetPred.isEmpty();
      case SpectraPackage.COUNTER__INC_PRED:
        return incPred != null && !incPred.isEmpty();
      case SpectraPackage.COUNTER__DEC_PRED:
        return decPred != null && !decPred.isEmpty();
      case SpectraPackage.COUNTER__OVERFLOW_METHOD:
        return overflowMethod != null && !overflowMethod.isEmpty();
      case SpectraPackage.COUNTER__UNDERFLOW_METHOD:
        return underflowMethod != null && !underflowMethod.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass)
  {
    if (baseClass == Referrable.class)
    {
      switch (derivedFeatureID)
      {
        case SpectraPackage.COUNTER__NAME: return SpectraPackage.REFERRABLE__NAME;
        default: return -1;
      }
    }
    return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass)
  {
    if (baseClass == Referrable.class)
    {
      switch (baseFeatureID)
      {
        case SpectraPackage.REFERRABLE__NAME: return SpectraPackage.COUNTER__NAME;
        default: return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(", overflowMethod: ");
    result.append(overflowMethod);
    result.append(", underflowMethod: ");
    result.append(underflowMethod);
    result.append(')');
    return result.toString();
  }

} //CounterImpl
