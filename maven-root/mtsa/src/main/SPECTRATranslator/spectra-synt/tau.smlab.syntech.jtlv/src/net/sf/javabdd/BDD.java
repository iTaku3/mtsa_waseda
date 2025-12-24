/*
Copyright (c) since 2015, Tel Aviv University and Software Modeling Lab

All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of Tel Aviv University and Software Modeling Lab nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Tel Aviv University and Software Modeling Lab
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package net.sf.javabdd;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Stack;
import tau.smlab.syntech.jtlv.Env;

/** Binary Decision Diagrams (BDDs) {@code Interface} . */
public interface BDD {

  /**
   * Returns the factory that created this BDD.
   *
   * @return factory that created this BDD
   */
  public BDDFactory getFactory();

  /**
   * Returns true if this BDD is the zero (false) BDD.
   *
   * @return true if this BDD is the zero (false) BDD
   */
  public boolean isZero();

  /**
   * Returns true if this BDD is the one (true) BDD.
   *
   * @return true if this BDD is the one (true) BDD
   */
  public boolean isOne();

  /**
   * Returns true if this BDD is the universe BDD. The universal BDD differs from the one BDD in ZDD
   * mode.
   *
   * @return true if this BDD is the universe BDD
   */
  public boolean isUniverse();

  /**
   * Converts this BDD to a new BDDVarSet.
   *
   * <p>This BDD must be a boolean function that represents the all-true minterm of the BDD
   * variables of interest.
   *
   * @return the contents of this BDD as a new BDDVarSet
   */
  public BDDVarSet toVarSet();

  /**
   * Gets the variable labeling the BDD.
   *
   * <p>Compare to bdd_var.
   *
   * @return the index of the variable labeling the BDD
   */
  public abstract int var();

  /**
   * Returns true if this BDD is an ADD
   *
   * @return true if this BDD is an ADD
   */
  public boolean isADD();

  /**
   * Gets the level of this BDD.
   *
   * <p>Compare to LEVEL() macro.
   *
   * @return the level of this BDD
   */
  public int level();

  /**
   * Gets the true branch of this BDD. If this DD is a constant, then null is returned.
   *
   * <p>Compare to bdd_high.
   *
   * @return null if this is a constant DD; Otherwise,true branch of this BDD
   */
  public abstract BDD high();

  /**
   * Gets the false branch of this BDD. If this DD is a constant, then null is returned.
   *
   * <p>Compare to bdd_low.
   *
   * @return null if this is a constant DD; Otherwise, false branch of this BDD
   */
  public abstract BDD low();

  /**
   * Identity function. Returns a copy of this BDD. Use as the argument to the "xxxWith" style
   * operators when you do not want to have the argument consumed.
   *
   * <p>Compare to bdd_addref.
   *
   * @return copy of this BDD
   */
  public abstract BDD id();

  /**
   * Negates this BDD by exchanging all references to the zero-terminal with references to the
   * one-terminal and vice-versa.
   *
   * <p>Compare to bdd_not.
   *
   * @return the negated BDD
   */
  public abstract BDD not();

  /**
   * like not() but adds domain restrictions afterwards
   *
   * @return
   */
  public abstract BDD notWithDoms();

  /**
   * Returns the logical 'and' of two BDDs. This is a shortcut for calling "apply" with the "and"
   * operator.
   *
   * <p>Compare to bdd_and.
   *
   * @param that BDD to 'and' with
   * @return the logical 'and' of two BDDs
   */
  public BDD and(BDD that);

  /**
   * Makes this BDD be the logical 'and' of two BDDs. The "that" BDD is consumed, and can no longer
   * be used. This is a shortcut for calling "applyWith" with the "and" operator.
   *
   * <p>Compare to bdd_and and bdd_delref.
   *
   * @param that the BDD to 'and' with
   */
  public BDD andWith(BDD that);

  /**
   * Returns the logical 'or' of two BDDs. This is a shortcut for calling "apply" with the "or"
   * operator.
   *
   * <p>Compare to bdd_or.
   *
   * @param that the BDD to 'or' with
   * @return the logical 'or' of two BDDs
   */
  public BDD or(BDD that);

  /**
   * Makes this BDD be the logical 'or' of two BDDs. The "that" BDD is consumed, and can no longer
   * be used. This is a shortcut for calling "applyWith" with the "or" operator.
   *
   * <p>Compare to bdd_or and bdd_delref.
   *
   * @param that the BDD to 'or' with
   */
  public BDD orWith(BDD that);

  /**
   * Returns the logical 'xor' of two BDDs. This is a shortcut for calling "apply" with the "xor"
   * operator.
   *
   * <p>Compare to bdd_xor.
   *
   * @param that the BDD to 'xor' with
   * @return the logical 'xor' of two BDDs
   */
  public BDD xor(BDD that);

  /**
   * Makes this BDD be the logical 'xor' of two BDDs. The "that" BDD is consumed, and can no longer
   * be used. This is a shortcut for calling "applyWith" with the "xor" operator.
   *
   * <p>Compare to bdd_xor and bdd_delref.
   *
   * @param that the BDD to 'xor' with
   */
  public BDD xorWith(BDD that);

  /**
   * Returns the logical 'implication' of two BDDs. This is a shortcut for calling "apply" with the
   * "imp" operator.
   *
   * <p>Compare to bdd_imp.
   *
   * @param that the BDD to 'implication' with
   * @return the logical 'implication' of two BDDs
   */
  public BDD imp(BDD that);

  /**
   * Makes this BDD be the logical 'implication' of two BDDs. The "that" BDD is consumed, and can no
   * longer be used. This is a shortcut for calling "applyWith" with the "imp" operator.
   *
   * <p>Compare to bdd_imp and bdd_delref.
   *
   * @param that the BDD to 'implication' with
   */
  public BDD impWith(BDD that);

  /**
   * Returns the logical 'bi-implication' of two BDDs. This is a shortcut for calling "apply" with
   * the "biimp" operator.
   *
   * <p>Compare to bdd_biimp.
   *
   * @param that the BDD to 'bi-implication' with
   * @return the logical 'bi-implication' of two BDDs
   */
  public BDD biimp(BDD that);

  /**
   * Makes this BDD be the logical 'bi-implication' of two BDDs. The "that" BDD is consumed, and can
   * no longer be used. This is a shortcut for calling "applyWith" with the "biimp" operator.
   *
   * <p>Compare to bdd_biimp and bdd_delref.
   *
   * @param that the BDD to 'bi-implication' with
   */
  public BDD biimpWith(BDD that);

  /**
   * if-then-else operator, i.e. computes: (this and then) or (not(this) and else)
   *
   * <p>Compare to bdd_ite.
   *
   * @param thenBDD the 'then' BDD
   * @param elseBDD the 'else' BDD
   * @return the result of the if-then-else operator on the three BDDs
   */
  public BDD ite(BDD thenBDD, BDD elseBDD);

  /**
   * Relational product. Calculates the relational product of the two BDDs as this AND that with the
   * variables in var quantified out afterwards. Identical to applyEx(that, and, var).
   *
   * <p>Compare to bdd_relprod.
   *
   * @param that the BDD to 'and' with
   * @param var the BDDVarSet to existentially quantify with
   * @return the result of the relational product
   * @see net.sf.javabdd.BDDDomain#set()
   */
  public BDD relprod(BDD that, BDDVarSet var);

  /**
   * Functional composition. Substitutes the variable var with the BDD that in this BDD: result =
   * f[g/var].
   *
   * <p>Compare to bdd_compose.
   *
   * @param g the function to use to replace
   * @param var the variable number to replace
   * @return the result of the functional composition
   */
  public BDD compose(BDD g, int var);

  /**
   * Simultaneous functional composition. Uses the pairs of variables and BDDs in pair to make the
   * simultaneous substitution: f [g1/V1, ... gn/Vn]. In this way one or more BDDs may be
   * substituted in one step. The BDDs in pair may depend on the variables they are substituting.
   * BDD.compose() may be used instead of BDD.replace() but is not as efficient when gi is a single
   * variable, the same applies to BDD.restrict(). Note that simultaneous substitution is not
   * necessarily the same as repeated substitution.
   *
   * <p>Compare to bdd_veccompose.
   *
   * @param pair the pairing of variables to functions
   * @return BDD the result of the simultaneous functional composition
   */
  public BDD veccompose(BDDPairing pair);

  /**
   * Generalized cofactor. Computes the generalized cofactor of this BDD with respect to the given
   * BDD.
   *
   * <p>Compare to bdd_constrain.
   *
   * @param that the BDD with which to compute the generalized cofactor
   * @return the result of the generalized cofactor
   */
  public BDD constrain(BDD that);

  /**
   * Existential quantification of variables. Removes all occurrences of this BDD in variables in
   * the set var by existential quantification.
   *
   * <p>Compare to bdd_exist.
   *
   * @param var BDDVarSet containing the variables to be existentially quantified
   * @return the result of the existential quantification
   * @see net.sf.javabdd.BDDDomain#set()
   */
  public BDD exist(BDDVarSet var);

  /**
   * Universal quantification of variables. Removes all occurrences of this BDD in variables in the
   * set var by universal quantification.
   *
   * <p>Compare to bdd_forall.
   *
   * @param var BDDVarSet containing the variables to be universally quantified
   * @return the result of the universal quantification
   * @see net.sf.javabdd.BDDDomain#set()
   */
  public BDD forAll(BDDVarSet var);

  /**
   * Unique quantification of variables. This type of quantification uses a XOR operator instead of
   * an OR operator as in the existential quantification.
   *
   * <p>Compare to bdd_unique.
   *
   * @param var BDDVarSet containing the variables to be uniquely quantified
   * @return the result of the unique quantification
   * @see net.sf.javabdd.BDDDomain#set()
   */
  public BDD unique(BDDVarSet var);

  /**
   * Restrict a set of variables to constant values. Restricts the variables in this BDD to constant
   * true if they are included in their positive form in var, and constant false if they are
   * included in their negative form.
   *
   * <p><i>Note that this is quite different than Coudert and Madre's restrict function.</i>
   *
   * <p>Compare to bdd_restrict.
   *
   * @param var BDD containing the variables to be restricted
   * @return the result of the restrict operation
   * @see net.sf.javabdd.BDD#simplify(BDD)
   */
  public BDD restrict(BDD var);

  /**
   * Mutates this BDD to restrict a set of variables to constant values. Restricts the variables in
   * this BDD to constant true if they are included in their positive form in var, and constant
   * false if they are included in their negative form. The "that" BDD is consumed, and can no
   * longer be used.
   *
   * <p><i>Note that this is quite different than Coudert and Madre's restrict function.</i>
   *
   * <p>Compare to bdd_restrict and bdd_delref.
   *
   * @param var BDD containing the variables to be restricted
   * @see net.sf.javabdd.BDDDomain#set()
   */
  public BDD restrictWith(BDD var);

  /**
   * Coudert and Madre's restrict function. Tries to simplify the BDD f by restricting it to the
   * domain covered by d. No checks are done to see if the result is actually smaller than the
   * input. This can be done by the user with a call to nodeCount().
   *
   * <p>Compare to bdd_simplify.
   *
   * @param d BDDVarSet containing the variables in the domain
   * @return the result of the simplify operation
   */
  public BDD simplify(BDDVarSet d);

  /**
   * Returns the variable support of this BDD. The support is all the variables that this BDD
   * depends on.
   *
   * <p>Compare to bdd_support.
   *
   * @return the variable support of this BDD
   */
  public BDDVarSet support();

  /**
   * Returns the result of applying the binary operator <tt>opr</tt> to the two BDDs.
   *
   * <p>Note that some operators might not be valid for every type of decision diagram (BDD, ADD,
   * 0-1 ADD). It is the caller's responsibility to check for the binary operator's validity.
   *
   * <p>Compare to bdd_apply.
   *
   * @see {@link net.sf.javabdd.BDDFactory.BDDOp}
   * @param that the BDD to apply the operator on
   * @param opr the operator to apply
   * @return the result of applying the operator
   */
  public BDD apply(BDD that, BDDFactory.BDDOp opr);

  /**
   * Makes this BDD be the result of the binary operator <tt>opr</tt> of two BDDs. The "that" BDD is
   * consumed, and can no longer be used. Attempting to use the passed in BDD again will result in
   * an exception being thrown.
   *
   * <p>Note that some operators might not be valid for every type of decision diagram (BDD, ADD,
   * 0-1 ADD). It is the caller's responsibility to check for the binary operator's validity.
   *
   * @see {@link net.sf.javabdd.BDDFactory.BDDOp}
   * @param that the BDD to apply the operator on
   * @param opr the operator to apply
   */
  public BDD applyWith(BDD that, BDDFactory.BDDOp opr);

  /**
   * Applies the binary operator <tt>opr</tt> to two BDDs and then performs a universal
   * quantification of the variables from the variable set <tt>var</tt>.
   *
   * <p>Compare to bdd_appall.
   *
   * @param that the BDD to apply the operator on
   * @param opr the operator to apply
   * @param var BDDVarSet containing the variables to quantify
   * @return the result
   * @see net.sf.javabdd.BDDDomain#set()
   */
  public BDD applyAll(BDD that, BDDFactory.BDDOp opr, BDDVarSet var);

  /**
   * Applies the binary operator <tt>opr</tt> to two BDDs and then performs an existential
   * quantification of the variables from the variable set <tt>var</tt>.
   *
   * <p>Compare to bdd_appex.
   *
   * @param that the BDD to apply the operator on
   * @param opr the operator to apply
   * @param var BDDVarSet containing the variables to quantify
   * @return the result
   * @see net.sf.javabdd.BDDDomain#set()
   */
  public BDD applyEx(BDD that, BDDFactory.BDDOp opr, BDDVarSet var);

  /**
   * Applies the binary operator <tt>opr</tt> to two BDDs and then performs a unique quantification
   * of the variables from the variable set <tt>var</tt>.
   *
   * <p>Compare to bdd_appuni.
   *
   * @param that the BDD to apply the operator on
   * @param opr the operator to apply
   * @param var BDDVarSet containing the variables to quantify
   * @return the result
   * @see net.sf.javabdd.BDDDomain#set()
   */
  public BDD applyUni(BDD that, BDDFactory.BDDOp opr, BDDVarSet var);

  /**
   * Finds one satisfying variable assignment of this BDD which restricts each variable of the set
   * <tt>var</tt>. The only variables mentioned in the result are those of <tt>var</tt>. All other
   * variables would be undefined, i.e., don't cares, in the resulting BDD.
   *
   * @param var BDDVarSet the set of variables that are mentioned in the result
   * @return one satisfying variable assignment
   * @see net.sf.javabdd.BDDDomain#set()
   */
  public BDD satOne(BDDVarSet var);

  /**
   * Finds all satisfying variable assignments.
   *
   * <p>Compare to bdd_allsat.
   *
   * @return all satisfying variable assignments
   */
  public AllSatIterator allsat();

  /**
   * Iterator that returns all satisfying assignments as byte arrays. In the byte arrays, -1 means
   * dont-care, 0 means 0, and 1 means 1.
   */
  public static class AllSatIterator implements Iterator<byte[]> {

    protected final BDDFactory f;
    protected BDD original;
    protected byte[] allsatProfile;
    protected Stack<Integer> choices;
    protected LinkedList<Integer> freeVars;

    /**
     * Constructs a satisfying-assignment iterator on the given BDD. next() will returns a byte
     * array, the byte array will be indexed by BDD variable number.
     *
     * @param r BDD to iterate over
     */
    public AllSatIterator(BDD r) {
      f = r.getFactory();
      if (f.isZDD()) {
        throw new RuntimeException("Sorry, lost ZDD compatibility.");
      }
      original = r.id();
      if (original.isZero()) {
        freeVars = null;
        return;
      }
      allsatProfile = new byte[f.varNum()];
      Arrays.fill(allsatProfile, (byte) -1);
      choices = new Stack<Integer>();
      // find relevant variables (not don't cares)
      freeVars = new LinkedList<>();
      for (int v : original.support().toArray()) {
        freeVars.add(v);
      }
      Collections.sort(freeVars);
      if (!r.isOne()) {
        if (!gotoNext()) {
          allsatProfile = null;
          original.free();
        }
      }
    }

    public void free() {
      if (!original.isFree()) {
        original.free();
      }
    }

    /**
     * compute next assignment of allsatProfile
     *
     * @return true if there is a next assignment false otherwise
     */
    private boolean gotoNext() {
      if (original.isZero() || original.isOne()) {
        return false;
      }

      BDD next = Env.FALSE();
      do {
        if (next.isZero() && !choices.isEmpty()) {
          // pop all dead-ends
          int var = choices.pop();
          while (var > 0 && !choices.isEmpty()) {
            // remember to later set this variable
            freeVars.addFirst(var - 1);
            var = choices.pop();
          }

          if (var > 0 && choices.isEmpty()) {
            return false;
          }

          // now we have a negaive var and can try alternative
          var = -var - 1;
          choices.push(var + 1);
        } else if (!freeVars.isEmpty()) {
          // add negated var choice
          choices.push(-(freeVars.removeFirst() + 1));
        }
        // prepare next BDD based on choices
        next.free();
        next = original.id();
        for (int i : choices) {
          if (i < 0) {
            next.andWith(f.nithVar(-i - 1));
          } else {
            next.andWith(f.ithVar(i - 1));
          }
        }
      } while (!freeVars.isEmpty() || next.isZero());
      next.free();

      // assign value to every variable
      for (int v = 0; v < allsatProfile.length; v++) {
        if (choices.contains(v + 1)) {
          allsatProfile[v] = 1;
        } else if (choices.contains(-(v + 1))) {
          allsatProfile[v] = 0;
        } else {
          // v is don't care
          allsatProfile[v] = -1;
        }
      }
      return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
      return allsatProfile != null;
    }

    /**
     * Return the next satisfying var setting.
     *
     * @return byte[]
     */
    public byte[] nextSat() {
      if (allsatProfile == null) throw new NoSuchElementException();
      byte[] b = new byte[allsatProfile.length];
      System.arraycopy(allsatProfile, 0, b, 0, b.length);
      if (!gotoNext()) {
        allsatProfile = null;
        original.free();
      }
      return b;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Iterator#next()
     */
    public byte[] next() {
      return nextSat();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Iterator#remove()
     */
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * Finds one satisfying assignment of the domain <tt>d</tt> in this BDD and returns that value.
   *
   * <p>Compare to fdd_scanvar.
   *
   * @param d domain to scan
   * @return one satisfying assignment for that domain
   */
  public BigInteger scanVar(BDDDomain d);

  /**
   * Finds one satisfying assignment in this BDD of all the defined BDDDomain's. Each value is
   * stored in an array which is returned. The size of this array is exactly the number of
   * BDDDomain's defined.
   *
   * <p>Compare to fdd_scanallvar.
   *
   * @return array containing one satisfying assignment of all the defined domains
   */
  public BigInteger[] scanAllVar();

  /**
   * Returns an iteration of the satisfying assignments of this BDD. Returns an iteration of
   * minterms. The <tt>var</tt> argument is the set of variables that will be mentioned in the
   * result.
   *
   * @param var set of variables to mention in result
   * @return an iteration of minterms
   * @see net.sf.javabdd.BDDDomain#set()
   */
  public BDDIterator iterator(final BDDVarSet var);

  public static class BDDIterator implements Iterator<BDD> {
    final BDDFactory f;
    AllSatIterator i;
    // Reference to the initial BDD object, used to support the remove() operation.
    final BDD initialBDD;
    BDD remainder;

    /** relevant variables numbers */
    final int[] vars;

    /** Current bit assignmentof variables in vars. */
    final boolean[] varVals;

    /**
     * Latest result from allsat iterator. Here, array indexed by variable number. Can contain don't
     * cares (-1).
     */
    byte[] allSatRes;

    /** Last BDD returned. Used to support the remove() operation. */
    BDD lastReturned;

    /**
     * Construct a new BDDIterator on the given BDD. The var argument is the set of variables that
     * will be mentioned in the result.
     *
     * @param bdd BDD to iterate over
     * @param var variable set to mention in result
     */
    public BDDIterator(BDD bdd, BDDVarSet var) {
      initialBDD = bdd;
      remainder = bdd.id();
      f = bdd.getFactory();
      i = new AllSatIterator(bdd);

      vars = var.toArray();

      varVals = new boolean[vars.length];
      gotoNext();
    }

    protected void gotoNext() {
      if (i.hasNext()) {
        // here we need to remove everything we have seen before for the current VarSet (by removing
        // lastReturned)
        i.free();
        i = new AllSatIterator(remainder);
        if (!i.hasNext()) {
          allSatRes = null;
          return;
        }
        allSatRes = (byte[]) i.next();
      } else {
        allSatRes = null;
        return;
      }
      for (int i = 0; i < vars.length; ++i) {
        if (allSatRes[vars[i]] == 1) {
          varVals[i] = true;
        } else {
          varVals[i] = false;
        }
      }
    }

    protected boolean gotoNextA() {
      for (int i = vars.length - 1; i >= 0; --i) {
        if (allSatRes[vars[i]] != -1) continue;
        if (varVals[i] == false) {
          varVals[i] = true;
          return true;
        }
        varVals[i] = false;
      }
      return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
      return allSatRes != null;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Iterator#next()
     */
    public BDD next() {
      return nextBDD();
    }

    /**
     * next value in the given domain (encoding of the bools in varVals into a number)
     *
     * @param dom
     * @return null if the domain was not in the varset when the iterator was created
     */
    public BigInteger nextValue(BDDDomain dom) {
      if (allSatRes == null) {
        throw new NoSuchElementException();
      }
      if (lastReturned != null) {
        lastReturned.free();
      }
      lastReturned = null;
      BigInteger val = BigInteger.ZERO;
      int[] ivar = dom.vars();
      for (int m = dom.varNum() - 1; m >= 0; m--) {
        val = val.shiftLeft(1);
        int varPos = Arrays.binarySearch(vars, ivar[m]);
        if (varPos < 0) {
          val = null;
          break;
        }
        if (varVals[varPos]) {
          val = val.add(BigInteger.ONE);
        }
      }
      if (!gotoNextA()) {
        gotoNext();
      }
      return val;
    }

    /**
     * Return the next BDD in the iteration.
     *
     * @return the next BDD in the iteration
     */
    public BDD nextBDD() {
      if (allSatRes == null) {
        throw new NoSuchElementException();
      }
      if (lastReturned != null) {
        lastReturned.free();
      }
      lastReturned = f.universe();
      for (int i = 0; i < varVals.length; i++) {
        if (varVals[i] == true) {
          lastReturned.andWith(f.ithVar(vars[i]));
        } else {
          lastReturned.andWith(f.nithVar(vars[i]));
        }
      }
      remainder.andWith(lastReturned.not());
      if (!gotoNextA()) {
        gotoNext();
      }
      return lastReturned.id();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Iterator#remove()
     */
    public void remove() {
      if (lastReturned == null) throw new IllegalStateException();
      initialBDD.applyWith(lastReturned, BDDFactory.diff);
      lastReturned = null;
    }

    /**
     * Returns true if the given BDD variable number is a dont-care. <tt>var</tt> must be a variable
     * in the iteration set.
     *
     * @param var variable number to check
     * @return if the given variable is a dont-care
     */
    public boolean isDontCare(int var) {
      if (allSatRes == null) {
        return false;
      }
      return allSatRes[var] == -1;
    }

    /**
     * Returns true if the BDD variables in the given BDD domain are all dont-care's.
     *
     * <p>
     *
     * @param d domain to check
     * @return if the variables are all dont-cares
     * @throws BDDException if d is not in the iteration set
     */
    public boolean isDontCare(BDDDomain d) {
      if (allSatRes == null) {
        return false;
      }
      int[] vars = d.vars();
      for (int i = 0; i < vars.length; ++i) {
        if (!isDontCare(vars[i])) return false;
      }
      return true;
    }

    /**
     * Fast-forward the iteration such that the given variable number is true.
     *
     * @param var number of variable
     */
    public void fastForward(int var) {
      if (allSatRes == null) {
        throw new BDDException();
      }
      int varPos = Arrays.binarySearch(vars, var);
      if (varPos < 0 || allSatRes[var] != -1) {
        throw new BDDException();
      }
      varVals[varPos] = true;
    }

    /**
     * Fast-forward the iteration such that the given set of variables are true.
     *
     * @param vars set of variable indices
     */
    public void fastForward(int[] vars) {
      for (int i = 0; i < vars.length; ++i) {
        fastForward(vars[i]);
      }
    }

    /**
     * Assuming <tt>d</tt> is a dont-care, skip to the end of the iteration for <tt>d</tt>
     *
     * @param d BDD domain to fast-forward past
     */
    public void skipDontCare(BDDDomain d) {
      int[] vars = d.vars();
      fastForward(vars);
      if (!gotoNextA()) {
        gotoNext();
      }
    }

    public void free() {
      i.free();
      if (lastReturned != null) {
        lastReturned.free();
      }
    }
  }

  /**
   * Returns a BDD where all variables are replaced with the variables defined by pair. Each entry
   * in pair consists of a old and a new variable. Whenever the old variable is found in this BDD
   * then a new node with the new variable is inserted instead.
   *
   * <p>Compare to bdd_replace.
   *
   * @param pair pairing of variables to the BDDs that replace those variables
   * @return result of replace
   */
  public BDD replace(BDDPairing pair);

  /**
   * Replaces all variables in this BDD with the variables defined by pair. Each entry in pair
   * consists of a old and a new variable. Whenever the old variable is found in this BDD then a new
   * node with the new variable is inserted instead. Mutates the current BDD.
   *
   * <p>Compare to bdd_replace and bdd_delref.
   *
   * @param pair pairing of variables to the BDDs that replace those variables
   */
  public BDD replaceWith(BDDPairing pair);

  /**
   * Prints the set of truth assignments specified by this BDD.
   *
   * <p>Compare to bdd_printset.
   */
  public void printSet();

  /**
   * Prints this BDD in dot graph notation.
   *
   * <p>Compare to bdd_printdot.
   */
  public void printDot();

  /**
   * Counts the number of distinct nodes used for this BDD.
   *
   * <p>Compare to bdd_nodecount.
   *
   * @return the number of distinct nodes used for this BDD
   */
  public int nodeCount();

  /**
   * Counts the number of paths leading to the true terminal.
   *
   * <p>Compare to bdd_pathcount.
   *
   * @return the number of paths leading to the true terminal
   */
  public double pathCount();

  /**
   * Calculates the number of satisfying variable assignments.
   *
   * <p>Compare to bdd_satcount.
   *
   * @return the number of satisfying variable assignments
   */
  public double satCount();

  /**
   * Calculates the number of satisfying variable assignments to the variables in the given varset.
   * ASSUMES THAT THE BDD DOES NOT HAVE ANY ASSIGNMENTS TO VARIABLES THAT ARE NOT IN VARSET. You
   * will need to quantify out the other variables first.
   *
   * <p>Compare to bdd_satcountset.
   *
   * @return the number of satisfying variable assignments
   */
  public double satCount(BDDVarSet varset);

  /**
   * Calculates the logarithm of the number of satisfying variable assignments.
   *
   * <p>Compare to bdd_satcount.
   *
   * @return the logarithm of the number of satisfying variable assignments
   */
  public double logSatCount();

  /**
   * Calculates the logarithm of the number of satisfying variable assignments to the variables in
   * the given varset.
   *
   * <p>Compare to bdd_satcountset.
   *
   * @return the logarithm of the number of satisfying variable assignments
   */
  public double logSatCount(BDDVarSet varset);

  /**
   * Counts the number of times each variable occurs in this BDD. The result is stored and returned
   * in an integer array where the i'th position stores the number of times the i'th printing
   * variable occurred in the BDD.
   *
   * <p>Compare to bdd_varprofile.
   */
  public abstract int[] varProfile();

  /**
   * Returns true if this BDD equals that BDD, false otherwise.
   *
   * @param that the BDD to compare with
   * @return true iff the two BDDs are equal
   */
  public boolean equals(BDD that);

  /**
   * Returns a string representation of this BDD on the defined domains, using the given BDDToString
   * converter.
   *
   * @see net.sf.javabdd.BDD.BDDToString
   * @return string representation of this BDD using the given BDDToString converter
   */
  public String toStringWithDomains(BDDToString ts);

  /**
   * BDDToString is used to specify the printing behavior of BDDs with domains. Subclass this type
   * and pass it as an argument to toStringWithDomains to have the toStringWithDomains function use
   * your domain names and element names, instead of just numbers.
   */
  public static class BDDToString {
    /**
     * Singleton instance that does the default behavior: domains and elements are printed as their
     * numbers.
     */
    public static final BDDToString INSTANCE = new BDDToString();

    /** Protected constructor. */
    protected BDDToString() {}

    /**
     * Given a domain index and an element index, return the element's name. Called by the
     * toStringWithDomains() function.
     *
     * @param i the domain number
     * @param j the element number
     * @return the string representation of that element
     */
    public String elementName(int i, BigInteger j) {
      return j.toString();
    }

    /**
     * Given a domain index and an inclusive range of element indices, return the names of the
     * elements in that range. Called by the toStringWithDomains() function.
     *
     * @param i the domain number
     * @param lo the low range of element numbers, inclusive
     * @param hi the high range of element numbers, inclusive
     * @return the string representation of the elements in the range
     */
    public String elementNames(int i, BigInteger lo, BigInteger hi) {
      return lo.toString() + "-" + hi.toString();
    }
  }

  /**
   * Converts this BDD into a 0-1 ADD.<br>
   * If this is an ADD then it is equivalent to {@link #id() id}.
   *
   * @return the converted 0-1 ADD
   */
  public ADD toADD();

  /** Frees this BDD. Further use of this BDD will result in an exception being thrown. */
  public abstract void free();

  /**
   * Checks whether this BDD has already been freed.
   *
   * @return true if this BDD is freed
   */
  public abstract boolean isFree();

  public long rootId();

  /**
   * Returns a deterministic version of this BDD. It is assumed that this BDD represents a
   * controller or a strategy.<br>
   * The controller is deterministic such that for every combination of variable assignments that
   * are not in d, there is at most one assignment of variables in d that goes to 1 constant, i.e
   * there is at most one valid choice for the module that controls the variables that appear in d.
   *
   * @param d BDD containing the variables of the module whose choices should be deterministic
   * @return A deterministic version of this controller
   */
  public BDD determinizeController(BDDVarSet d);
}
