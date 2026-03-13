package ltsa.lts;

import static ltsa.lts.CompactStateUtils.convertToNonProbabilistic;
import static ltsa.lts.CompactStateUtils.isProbabilisticMachine;

import MTSTools.ac.ic.doc.mtstools.model.MTSConstants;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import ltsa.lts.util.MTSUtils;

public class Alphabet {

  PrefixTree root = null;
  String[] myAlpha;
  CompactState sm;
  public int maxLevel = 0;

  public Alphabet(CompactState sm, boolean removeMaybes) {
    this.sm = sm;
    myAlpha = sm.getTransitionsLabels().clone();
    //        myAlpha = new String[sm.alphabet.length];
    //        for (int i = 0; i < sm.alphabet.length; ++i)
    //            myAlpha[i] = sm.alphabet[i];
    int tausIndex = 1;
    if (sm.alphabet[Declaration.TAU_MAYBE] != null
        && sm.alphabet[Declaration.TAU_MAYBE].equals(MTSConstants.TAU_MAYBE)) {
      tausIndex = 2;
    }
    sort(myAlpha, tausIndex);
    for (int i = tausIndex; i < myAlpha.length; ++i)
      if (removeMaybes && !myAlpha[i].contains(MTSUtils.MAYBE_MARK))
        root = PrefixTree.addName(root, myAlpha[i]);
    if (root != null) maxLevel = root.maxDepth();
  }

  public Alphabet(CompactState sm) {
    this(sm, false);
  }

  public Alphabet(String[] inames) {
    String names[] = new String[inames.length];
    for (int i = 0; i < names.length; ++i) names[i] = inames[i];
    if (names.length > 1) sort(names, 0);
    for (int i = 0; i < names.length; ++i) root = PrefixTree.addName(root, names[i]);
  }

  public Alphabet(Vector names) {
    this((String[]) names.toArray(new String[names.size()]));
  }

  public String toString() {
    if (root == null) return "{}";
    else return root.toString();
  }

  public Set toSet() {
    if (root == null) return null;
    else return root.toSet();
  }

  public void print(LTSOutput output, int level) {
    output.outln("Process:\n\t" + sm.name);
    output.outln("Alphabet:\n");
    if (root == null) {
      output.outln("\t{}");
      return;
    }
    if (isProbabilisticMachine(sm)) {
      convertToNonProbabilistic(sm);
    }

    output.outln("\t" + printableAlphabet(sm.getAlphabet()));
  }

  public void printExpand(LTSOutput output, int level) {
    output.outln("Process:\n\t" + sm.name);
    output.outln("Alphabet:");
    if (root == null) {
      output.outln("\t{}");
      return;
    }
    if (level == 0) {
      output.outln("\t" + root.toString());
    } else {
      output.out("\t{ ");
      printExpandLabels(output, level);
      output.outln("\t}");
    }
  }

  public void printExpandLabels(LTSOutput output, int level) {
    printExpandLabels(output, level, false);
  }

  public void printExpandLabels(LTSOutput output, int level, boolean singleLine) {
    Vector v = new Vector();
    root.getStrings(v, level - 1, null);
    Enumeration e = v.elements();
    boolean first = true;
    while (e.hasMoreElements()) {
      String s = (String) e.nextElement();
      if (!first && !singleLine) output.out("\t  ");
      if (e.hasMoreElements()) {
        output.out(s + ",");
        if (!singleLine) output.outln("");
      } else {
        output.out(s);
        if (!singleLine) output.outln("");
      }
      first = false;
    }
  }

  private String printableAlphabet(String[] alphabet) {
    int size = alphabet.length;
    String result = "{";
    for (int i = 0; i < size - 1; i++) {
      result = result + alphabet[i] + ", ";
    }
    result = result + alphabet[size - 1] + "}";

    return result;
  }

  private void sort(String a[], int from) { // simple shell sort
    int min;
    for (int i = from; i < a.length - 1; i++) {
      min = i;
      for (int j = i + 1; j < a.length; j++) {
        if (a[j].compareTo(a[min]) < 0) min = j;
      }
      // swap
      String temp = a[i];
      a[i] = a[min];
      a[min] = temp;
    }
  }
}

/** class PrefixTree - list of subtrees */
class PrefixTree {
  String name;
  int value;
  boolean isInt = false;
  PrefixTree subname = null;
  PrefixTree list = null;
  boolean lastprefix = false;

  PrefixTree(String n) {
    name = n;
    checkInt();
  }

  static PrefixTree addName(PrefixTree pt, String s) {
    if (pt == null) {
      pt = new PrefixTree(prefix(s, 0));
    }
    pt.add(s, 0);
    return pt;
  }

  private void add(String s, int level) {
    String ps = prefix(s, level);
    if (ps == null) return;
    if (ps.equals(name) && !lastprefix) {
      String pps = prefix(s, level + 1);
      if (pps == null) {
        lastprefix = true;
        return;
      }
      if (subname == null) subname = new PrefixTree(pps);
      subname.add(s, level + 1);
    } else {
      if (list == null) list = new PrefixTree(ps);
      list.add(s, level);
    }
    return;
  }

  public static boolean equals(PrefixTree one, PrefixTree two) {
    if (one == two) return true;
    if (one == null || two == null) return false;
    if (!one.name.equals(two.name)) return false;
    return equals(one.subname, two.subname) && equals(one.list, two.list);
  }

  // get sub lists of names with the same suffix
  PrefixTree[] getSubLists() {
    Vector subs = new Vector();
    PrefixTree pt = this;
    PrefixTree ptt = list;
    subs.addElement(pt);
    while (ptt != null) {
      if (!equals(pt.subname, ptt.subname) || pt.isInt != ptt.isInt) {
        subs.addElement(ptt);
        pt = ptt;
      }
      ptt = ptt.list;
    }
    subs.addElement(null); // sentinel
    PrefixTree[] sl = new PrefixTree[subs.size()];
    subs.copyInto(sl);
    return sl;
  }

  void checkInt() {
    try {
      value = Integer.parseInt(name);
      isInt = true;
    } catch (NumberFormatException e) {
    }
  }

  static String prefix(String s, int level) {
    int start = 0;
    for (int i = 0; i < level; i++) {
      start = s.indexOf('.', start);
      if (start < 0) return null;
      ++start;
    }
    int finish = s.indexOf('.', start);
    if (finish < 0) return s.substring(start);
    else return s.substring(start, finish);
  }

  public void getStrings(Vector v, int level, String prefix) {
    PrefixTree pt = this;
    while (pt != null) {
      String pre;
      if (prefix == null) pre = pt.item();
      else pre = prefix + dotted(pt.item());
      if (pt.subname == null) v.addElement(pre);
      else if (level > 0) pt.subname.getStrings(v, level - 1, pre);
      else v.addElement(pre + dotted(pt.subname.toString()));
      pt = pt.list;
    }
  }

  public int maxDepth() {
    PrefixTree pt = this;
    int max = 0;
    while (pt != null) {
      if (pt.subname == null) max = Math.max(max, 1);
      else max = Math.max(1 + pt.subname.maxDepth(), max);
      pt = pt.list;
    }
    return max;
  }

  // routines to stringify PrefixTree

  public String toString() {
    PrefixTree[] subs = getSubLists();
    String s;
    if (subs.length > 2) s = "{";
    else s = "";
    for (int i = 0; i < subs.length - 1; ++i) {
      if (i < subs.length - 2) s = s + listString(subs[i], subs[i + 1]) + ", ";
      else s = s + listString(subs[i], subs[i + 1]);
    }
    if (subs.length > 2) return s + "}";
    else return s;
  }

  public Set<String> toSet() {
    PrefixTree[] subs = getSubLists();
    Set<String> s = new HashSet<>();
    if (subs != null) {
      for (int i = 0; i < subs.length - 1; i++) {
        s.add(subs[i].name);
        if (subs[i].list != null) {
          s.addAll(subs[i].list.toSet());
        }
      }
    }
    return s;
  }

  static String listString(PrefixTree start, PrefixTree end) {
    String s;
    if (start.list == end) {
      s = start.item();
    } else {
      if (intRange(start, end)) s = rangeString(start, end);
      else {
        s = "{" + start.item();
        PrefixTree pt = start.list;
        while (pt != end) {
          s = s + ", " + pt.item();
          pt = pt.list;
        }
        s = s + "}";
      }
    }
    if (start.subname != null) return s + dotted(start.subname.toString());
    else return s;
  }

  private static String dotted(String suffix) { // decide whether dot needed
    if (suffix.charAt(0) == '[') return suffix;
    else return "." + suffix;
  }

  String item() {
    if (isInt) return "[" + name + "]";
    else return name;
  }

  static boolean intRange(PrefixTree start, PrefixTree end) {
    PrefixTree pt = start;
    while (pt != end) {
      if (!pt.isInt) return false;
      pt = pt.list;
    }
    return true;
  }

  static String rangeString(PrefixTree start, PrefixTree end) {
    PrefixTree pt = start;
    int n = 0;
    while (pt != end) {
      pt = pt.list;
      ++n;
    }
    int a[] = new int[n];
    pt = start;
    for (int i = 0; i < a.length; ++i) {
      a[i] = pt.value;
      pt = pt.list;
    }
    sort(a);
    if (isOneRange(a)) {
      return "[" + a[0] + ".." + a[a.length - 1] + "]";
    } else {
      int j = 0;
      String s = "{";
      while (j < a.length) {
        int i;
        for (i = j; i < a.length - 1 && a[i + 1] - a[i] == 1; ++i)
          ;
        if (i == j) s = s + "[" + a[j] + "]";
        else s = s + "[" + a[j] + ".." + a[i] + "]";
        j = i + 1;
        if (j < a.length) s = s + ", ";
      }
      s = s + "}";
      return s;
    }
  }

  private static boolean isOneRange(int a[]) {
    for (int i = 0; i < a.length - 1; ++i) {
      if (a[i + 1] - a[i] != 1) return false;
    }
    return true;
  }

  private static void sort(int a[]) { // simple shell sort
    int min;
    for (int i = 0; i < a.length - 1; i++) {
      min = i;
      for (int j = i + 1; j < a.length; j++) {
        if (a[j] < a[min]) min = j;
      }
      // swap
      int temp = a[i];
      a[i] = a[min];
      a[min] = temp;
    }
  }
}
