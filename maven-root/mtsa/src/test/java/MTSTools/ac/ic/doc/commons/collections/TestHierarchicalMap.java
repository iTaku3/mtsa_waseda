package MTSTools.ac.ic.doc.commons.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @author jallik Tests for the HierarchicalMap util class.
 */
public class TestHierarchicalMap extends TestCase {

    HierarchicalMap duplicatesHierarchicalMap;
    HierarchicalMap emptyHierarchicalMap;
    HierarchicalMap emptyLocalHierarchicalMap;
    HierarchicalMap emptyParentHierarchicalMap;
    HierarchicalMap noDuplicatesHierarchicalMap;

    public TestHierarchicalMap(String name) {
        super(name);
    }

    public void testBasicMethodsOnEmptyMap() {
        assertTrue(emptyHierarchicalMap.containsKey("1") == false);
        assertTrue(emptyHierarchicalMap.containsValue("a") == false);
        assertEquals(emptyHierarchicalMap.get("1"), null);

        // verifica que no se haga throw de alguna exception
        emptyHierarchicalMap.remove("1");

        assertTrue(emptyHierarchicalMap.values().isEmpty());
    }

    public void testBasicMethodsOnEmptyParentMap() {
        assertTrue(emptyParentHierarchicalMap.containsKey("1") == true);
        assertTrue(emptyParentHierarchicalMap.containsKey("2") == false);

        assertTrue(emptyParentHierarchicalMap.containsValue("a"));
        assertTrue(!emptyParentHierarchicalMap.containsValue("f"));

        assertTrue(emptyParentHierarchicalMap.get("1").equals("a"));
        assertTrue(emptyParentHierarchicalMap.get("6") == null);

        assertTrue(!emptyParentHierarchicalMap.values().isEmpty());
    }

    public void testBasicMethodsOnMapWithDuplicates() {
        // acceso a key local
        assertTrue(duplicatesHierarchicalMap.containsKey("1") == true);
        // acceso a key del parent
        assertTrue(duplicatesHierarchicalMap.containsKey("2") == true);

        // acceso a value de local
        assertTrue(duplicatesHierarchicalMap.containsValue("a") == true);

        // TODO definir bien
        // acceso a value de parent con override de key
        assertTrue(!duplicatesHierarchicalMap.containsValue("A") == true);

        // acceso a value de parent sin override de key
        assertTrue(duplicatesHierarchicalMap.containsValue("b") == true);
        // acceso a value de local identico en parent
        assertTrue(duplicatesHierarchicalMap.containsValue("c") == true);

        // acceso por get a value con override
        assertTrue(duplicatesHierarchicalMap.get("1").equals("a"));
        // acceso por get a value del parent
        assertTrue(duplicatesHierarchicalMap.get("2").equals("b"));
        // acceso por get a value con override de local identico a value de parent.
        assertTrue(duplicatesHierarchicalMap.get("3").equals("c"));

        // remove para acceso por get a value del parent
        duplicatesHierarchicalMap.remove("1");
        assertTrue(duplicatesHierarchicalMap.get("1").equals("A"));
        // adicion para inhibir acceso a parent
        duplicatesHierarchicalMap.put("1", "ccc");
        assertTrue(duplicatesHierarchicalMap.get("1").equals("ccc"));

        // remove de elemento inexistente en local pero existente en parent
        duplicatesHierarchicalMap.remove("2");
        // acceso al valor de parent
        assertTrue(duplicatesHierarchicalMap.get("2").equals("b"));
        // adicion para inhibir acceso a parent
        duplicatesHierarchicalMap.put("2", "bbb");
        assertTrue(duplicatesHierarchicalMap.get("2").equals("bbb"));

        // eliminacion de elemento en local identico a en parent. acceso posterior a parent.
        duplicatesHierarchicalMap.remove("3");
        assertTrue(duplicatesHierarchicalMap.get("3").equals("c"));

    }

    public void testEmptyMap() {
        HierarchicalMap hm = new HierarchicalMap();

        assertTrue(hm.get("1") == null);
    }

    public void testEntrySetSizeEqualsGettableElements() {
        HierarchicalMap hm = new HierarchicalMap();
        HierarchicalMap parent = new HierarchicalMap();

        parent.put("1", "AAAA");
        hm.setParent(parent);

        hm.put("1", "a");

        Iterator iter = hm.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry element = (Map.Entry)iter.next();
            if (element.getValue().equals("AAAA")) {
                fail("El entrySet tiene un elemento del parent que esta sobreescrito en el local.");
            }
        }
    }

    public void testEntrySetWithDuplicates() {

        HierarchicalMap expectedMap = new HierarchicalMap();
        expectedMap.put("1", "a");
        expectedMap.put("3", "c");
        expectedMap.put("4", "d");
        HierarchicalMap expectedParentMap = new HierarchicalMap();
        expectedParentMap.put("1", "A");
        expectedParentMap.put("2", "b");
        expectedParentMap.put("3", "c");
        expectedParentMap.put("5", "e");
        expectedParentMap.put("6", "f");
        expectedParentMap.put("7", "g");
        expectedMap.setParent(expectedParentMap);

        Set resultSet = duplicatesHierarchicalMap.entrySet();

        assertEquals(expectedMap.entrySet().size(), resultSet.size());
        assertTrue(expectedMap.entrySet().equals(resultSet));

        expectedMap.remove("1");
        assertTrue(!expectedMap.entrySet().equals(resultSet));
        expectedMap.put("1", "aaa");
        assertTrue(!expectedMap.entrySet().equals(resultSet));
        expectedMap.put("1", "a");
        assertTrue(expectedMap.entrySet().equals(resultSet));

        // la adicion de un elemento duplicado entre local y parent:
        // - no genera un cambio en el entryset
        // - no modifica el acceso al elemento por key
        assertTrue(noDuplicatesHierarchicalMap.get("4").equals("d"));
        expectedParentMap.put("4", "AAAAAA");
        expectedMap.setParent(expectedParentMap);
        assertTrue(expectedMap.entrySet().equals(noDuplicatesHierarchicalMap.entrySet()));
        assertTrue(noDuplicatesHierarchicalMap.get("4").equals("d"));

        // la adicion de un elemento local:
        // - tiene prioridad por sobre uno del parent
        // - su eliminacion permite acceder nuevamente al parent
        // - su eliminacion implica lograr un entryset igual al previo a la adicion
        assertTrue(noDuplicatesHierarchicalMap.get("5").equals("e"));
        noDuplicatesHierarchicalMap.put("5", "TTTT");
        assertTrue(noDuplicatesHierarchicalMap.get("5").equals("TTTT"));
        noDuplicatesHierarchicalMap.remove("5");
        assertTrue(noDuplicatesHierarchicalMap.get("5").equals("e"));
        assertTrue(expectedMap.entrySet().equals(noDuplicatesHierarchicalMap.entrySet()));

        // el entryset luego de la eliminacion de todos los elementos locales es igual al entryset del parent
        expectedMap.clear();
        assertTrue(expectedMap.entrySet().equals(expectedParentMap.entrySet()));

    }

    public void testEntrySetWithNoDuplicates() {

        HierarchicalMap expectedParentMap = new HierarchicalMap();
        expectedParentMap.put("2", "b");
        expectedParentMap.put("5", "e");
        expectedParentMap.put("6", "f");
        expectedParentMap.put("7", "g");
        HierarchicalMap expectedMap = new HierarchicalMap();
        expectedMap.put("1", "a");
        expectedMap.put("3", "c");
        expectedMap.put("4", "d");
        expectedMap.setParent(expectedParentMap);

        assertEquals(expectedMap.entrySet().size(), noDuplicatesHierarchicalMap.entrySet().size());
        assertTrue(expectedMap.entrySet().equals(noDuplicatesHierarchicalMap.entrySet()));

        // que el entryset refleje adicion de elementos
        noDuplicatesHierarchicalMap.put("50", "ZZ");
        noDuplicatesHierarchicalMap.put("52", "ZZ");
        assertEquals(2 + expectedMap.entrySet().size(), noDuplicatesHierarchicalMap.entrySet().size());

        // que el entryset refleje elimnacion de elementos
        noDuplicatesHierarchicalMap.remove("52");
        assertEquals(1 + expectedMap.entrySet().size(), noDuplicatesHierarchicalMap.entrySet().size());

        // que elementos de key duplicado no cambien el tama�o del entryset
        noDuplicatesHierarchicalMap.put("50", "RRR");
        assertEquals(1 + expectedMap.entrySet().size(), noDuplicatesHierarchicalMap.entrySet().size());
        noDuplicatesHierarchicalMap.remove("50");

        // que elementos de key duplicado impliquen un cambio de contenido del entryset
        assertTrue(expectedMap.entrySet().equals(noDuplicatesHierarchicalMap.entrySet()));
        noDuplicatesHierarchicalMap.put("1", "ZZZZ");
        assertTrue(!expectedMap.entrySet().equals(noDuplicatesHierarchicalMap.entrySet()));
        noDuplicatesHierarchicalMap.put("1", "a");
        assertTrue(expectedMap.entrySet().equals(noDuplicatesHierarchicalMap.entrySet()));

    }

    public void testKeySetAndValuesAreUnmodifiable() {
        HierarchicalMap hm = new HierarchicalMap();
        HierarchicalMap parent = new HierarchicalMap();

        parent.put("1", "AAAA");
        hm.setParent(parent);

        hm.put("1", "a");

        Collection c = hm.values();
        try {
            c.remove("a");
            fail("The Map values should be unmodifiable.");
        } catch (UnsupportedOperationException e) {
        }

        Set s = hm.keySet();
        try {
            s.remove("1");
            fail("The Map keySet should be unmodifiable.");
        } catch (UnsupportedOperationException e) {
        }
    }

    public void testLocalParentEntryRelationships() {
        // que no se pueda heredar si un key solo esta en local
        assertTrue(!noDuplicatesHierarchicalMap.isKeyInheritanceAllowed("1"));
        // que se pueda heredar si un key esta en parent
        assertTrue(noDuplicatesHierarchicalMap.isKeyInheritanceAllowed("2"));

        // que un elemento de local no sea heredado
        assertTrue(!noDuplicatesHierarchicalMap.isKeyInherited("1"));
        // que un elemento de parent sea heredado
        assertTrue(noDuplicatesHierarchicalMap.isKeyInherited("2"));

        // pruebas de override falso con elementos no duplicados entre local y parent
        assertTrue(!noDuplicatesHierarchicalMap.isKeyOverridden("1"));
        assertTrue(!noDuplicatesHierarchicalMap.isKeyOverridden("2"));

        // elemento heredado con override local
        assertTrue(duplicatesHierarchicalMap.isKeyInheritanceAllowed("1"));
        // elemento heredado sin override local
        assertTrue(duplicatesHierarchicalMap.isKeyInheritanceAllowed("2"));
        // elemento heredado con override local
        assertTrue(duplicatesHierarchicalMap.isKeyInheritanceAllowed("3"));
        // elemento no heredado
        assertTrue(!duplicatesHierarchicalMap.isKeyInheritanceAllowed("4"));

        // elemento heredado con override local
        assertTrue(!duplicatesHierarchicalMap.isKeyInherited("1"));
        // elemento heredado sin override local
        assertTrue(duplicatesHierarchicalMap.isKeyInherited("2"));
        // elemento heredado con override local
        assertTrue(!duplicatesHierarchicalMap.isKeyInherited("3"));
        // elemento no heredado
        assertTrue(!duplicatesHierarchicalMap.isKeyInherited("4"));

        // elemento heredado con override local
        assertTrue(duplicatesHierarchicalMap.isKeyOverridden("1"));
        // elemento heredado sin override local
        assertTrue(!duplicatesHierarchicalMap.isKeyOverridden("2"));
        // elemento heredado con override local
        assertTrue(duplicatesHierarchicalMap.isKeyOverridden("3"));
        // elemento no heredado
        assertTrue(!duplicatesHierarchicalMap.isKeyOverridden("4"));

    }

    public void testRemovalOfDuplicateForParentElementAccess() {

        assertTrue(duplicatesHierarchicalMap.get("1").equals("a"));
        duplicatesHierarchicalMap.remove("1");
        assertTrue(!duplicatesHierarchicalMap.get("1").equals("a"));
        assertTrue(duplicatesHierarchicalMap.get("1").equals("A"));
        duplicatesHierarchicalMap.put("1", "a");
        assertTrue(duplicatesHierarchicalMap.get("1").equals("a"));

    }

    public void testRepeatedPutsMaintainsEntrySet() {

        HierarchicalMap m = new HierarchicalMap();

        m.put("1", "a");
        m.put("2", "b");

        Set firstSet = m.entrySet();

        m.put("2", "b");
        assertTrue(m.entrySet().size() == firstSet.size());
        assertEquals(firstSet, m.entrySet());

        m.put("2", "bbbb");
        assertTrue(m.entrySet().size() == firstSet.size());
        assertTrue(!firstSet.equals(m.entrySet()));

        m.remove("2");

        assertTrue(m.entrySet().size() != firstSet.size());
    }

    public void testValueAccessOnKeyOverride() {

        HierarchicalMap hm = new HierarchicalMap();
        HierarchicalMap parent = new HierarchicalMap();

        // no se puede obtener un valor del parent si hay key overriden y no existe en local map
        parent.put("1", "AAAA");
        hm.setParent(parent);
        hm.put("1", "a");
        assertTrue(!hm.containsValue("AAAA"));

        // si existe en el localmap tiene que encontrarlo siempre independientemente de si hay o no key override
        hm.put("1", "AAAA");
        assertTrue(hm.containsValue("AAAA"));
        hm.put("2", "AAAA");
        assertTrue(hm.containsValue("AAAA"));
        hm.remove("2");

        // que efectivamente un valor que no exista no sea detectado
        assertTrue(!hm.containsValue("zzz"));

        // si existe un key sin override con el valor entonces se puede obtener
        parent.put("5", "BBBB");
        assertTrue(hm.containsValue("BBBB"));

        // si agrego override ya no es accesible
        hm.put("5", "z");
        assertTrue(!hm.containsValue("BBBB"));
    }

    protected void setUp() throws Exception {
        super.setUp();

        HierarchicalMap baseLocalMap = new HierarchicalMap();
        HierarchicalMap baseParentMap = new HierarchicalMap();
        HierarchicalMap extendedParentMap = new HierarchicalMap();

        baseLocalMap.put("1", "a");
        baseLocalMap.put("3", "c");
        baseLocalMap.put("4", "d");

        baseParentMap.put("2", "b");
        baseParentMap.put("5", "e");
        baseParentMap.put("6", "f");
        baseParentMap.put("7", "g");

        extendedParentMap.put("1", "A"); // key duplicado, value distinto
        extendedParentMap.put("2", "b");
        extendedParentMap.put("3", "c"); // key duplicado, value duplicado
        extendedParentMap.put("5", "e");
        extendedParentMap.put("6", "f");
        extendedParentMap.put("7", "g");

        noDuplicatesHierarchicalMap = new HierarchicalMap();
        noDuplicatesHierarchicalMap.setLocalMap(baseLocalMap);
        noDuplicatesHierarchicalMap.setParent(baseParentMap);

        duplicatesHierarchicalMap = new HierarchicalMap();
        duplicatesHierarchicalMap.setLocalMap(baseLocalMap);
        duplicatesHierarchicalMap.setParent(extendedParentMap);

        emptyHierarchicalMap = new HierarchicalMap();
        emptyHierarchicalMap.setParent(new HierarchicalMap());

        emptyParentHierarchicalMap = new HierarchicalMap();
        emptyParentHierarchicalMap.setLocalMap(baseLocalMap);
        emptyParentHierarchicalMap.setParent(new HierarchicalMap());

        emptyLocalHierarchicalMap = new HierarchicalMap();
        emptyLocalHierarchicalMap.setParent(baseParentMap);

    }
}