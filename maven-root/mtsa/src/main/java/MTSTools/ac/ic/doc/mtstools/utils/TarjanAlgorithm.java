/*
 * Copyright ďż˝ 2011 Jens Dietrich. All Rights Reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 * following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY JENS DIETRICH "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
  * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package MTSTools.ac.ic.doc.mtstools.utils;

/*
 * Copyright 2011 Jens Dietrich Licensed under the GNU AFFERO GENERAL PUBLIC LICENSE, Version 3
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.gnu.org/licenses/agpl.html Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import org.apache.commons.collections15.Predicate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Implementation of Tarjan's algorithm.
 * Complexity is O(|V|+|E|).
 * Tarjan, R. E. (1972), "Depth-first search and linear graph algorithms", SIAM Journal on Computing 1 (2):
 * 146ďż˝160, doi:10.1137/0201010.
 * {@link http://algowiki.net/wiki/index.php?title=Tarjan's_algorithm}
 * <p>
 * Adapted to work with MTS interface (Victor Wjugow)
 *
 * @param <V>
 * @param <E>
 * @author jens dietrich
 */
public class TarjanAlgorithm<V, E> {
	static final Predicate NULL_FILTER = new Predicate() {
		@Override
		public boolean evaluate(Object e) {
			return true;
		}
	};

	private int index = 0;
	private Stack<V> stack = new Stack<V>();
	private Map<V, Integer> indices = new HashMap<V, Integer>();
	private Map<V, Integer> lowLinks = new HashMap<V, Integer>();
	private Map<V, Set<V>> componentMembership = new HashMap<V, Set<V>>();
	// by default, use null filter
	private Predicate<E> edgeFilter = NULL_FILTER;
	private DirectedGraph<Set<V>, Integer> componentGraph = null;

	public void buildComponentGraph(MTS<V, E> graph, Predicate<E> edgeFilter) {
		this.componentGraph = new DirectedSparseGraph<Set<V>, Integer>();
		if (edgeFilter != null) {
			this.edgeFilter = edgeFilter;
		}
		for (V v : graph.getStates()) {
			if (!indices.containsKey(v)) {
				buildComponent(graph, v);
			}
		}
		int id = 0;
		// add edges
		for (V state : graph.getStates()) {
			for (Pair<E, V> transition : graph.getTransitions(state, MTS.TransitionType.REQUIRED)) {
				if (this.edgeFilter.evaluate(transition.getFirst())) {
					// note that the graph implementation class used will check for and reject parallel edges
					// as a consequence, their may be gaps in the range of assigned ids
					componentGraph.addEdge(id++, componentMembership.get(state), componentMembership.get(transition
							.getSecond()));
				}
			}
		}
	}

	public DirectedGraph<Set<V>, Integer> getComponentGraph() {
		return this.componentGraph;
	}

	public Map<V, Set<V>> getComponentMembership() {
		return this.componentMembership;
	}

	private void buildComponent(MTS<V, E> graph, V v) {
		indices.put(v, index);
		lowLinks.put(v, index);
		index = index + 1;
		stack.push(v);
		for (Pair<E, V> transition : graph.getTransitions(v, MTS.TransitionType.REQUIRED)) {
			if (edgeFilter.evaluate(transition.getFirst())) {
				V next = transition.getSecond();
				if (!indices.containsKey(next)) {
					buildComponent(graph, next);
					lowLinks.put(v, Math.min(lowLinks.get(v), lowLinks.get(next)));
				} else if (stack.contains(next)) {
					lowLinks.put(v, Math.min(lowLinks.get(v), indices.get(next)));
				}
			}
		}

		// build new component
		if (lowLinks.get(v).equals(indices.get(v))) {
			Set<V> component = new HashSet<V>();
			V v2;
			do {
				v2 = stack.pop();
				component.add(v2);
				componentMembership.put(v2, component); // look up faster later than searching components!
			} while (v2 != v);
			componentGraph.addVertex(component);
		}
	}
}