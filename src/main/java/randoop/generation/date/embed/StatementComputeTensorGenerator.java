package randoop.generation.date.embed;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.SimpleName;

public class StatementComputeTensorGenerator extends ASTVisitor {
	
	Set<ASTNode> skip_nodes = null;
	Map<IBinding, Integer> binding_statement_map = null;
	Map<String, Integer> basic_elements_id_backward = null;
	
	// the id of binding or basic element
	ArrayList<Integer> first_row = new ArrayList<Integer>();
	// shows which role the first row represents
	// 0 means the data in first row at this slot represents binding(statement) id
	// 1 means the data in first row at this slot represents basic_element id
	ArrayList<Integer> second_row = new ArrayList<Integer>();
	
	public StatementComputeTensorGenerator(Map<IBinding, Integer> binding_statement_map, Map<String, Integer> basic_elements_id_backward, Set<ASTNode> skip_nodes) {
		this.skip_nodes = skip_nodes != null ? skip_nodes : new HashSet<ASTNode>();
		this.binding_statement_map = binding_statement_map;
		this.basic_elements_id_backward = basic_elements_id_backward;
	}
	
	@Override
	public boolean preVisit2(ASTNode node) {
		if (skip_nodes.contains(node)) {
			return false;
		}
		boolean take_as_binding = false;
		if (node instanceof SimpleName) {
			IBinding ib = ((SimpleName)node).resolveBinding();
			if (ib != null) {
				if (ib instanceof ILocalVariable) {
					if (binding_statement_map.containsKey(ib)) {
						first_row.add(binding_statement_map.get(ib));
						second_row.add(0);
						take_as_binding = true;
					}
				}
			}
		}
		if (!take_as_binding) {
			String type_str = node.getClass().getSimpleName().toString();
			String node_str = node.toString();
			if (basic_elements_id_backward.containsKey(type_str)) {
				first_row.add(basic_elements_id_backward.get(type_str));
				second_row.add(1);
			} else if (basic_elements_id_backward.containsKey(node_str)) {
				first_row.add(basic_elements_id_backward.get(node_str));
				second_row.add(1);
			}
		}
		return super.preVisit2(node);
	}
	
	public int GetComputeTensorSize() {
		return first_row.size();
	}
	
	public int[][] ToNormalizedTensor(int tensor_size) {
		int[][] result = new int[2][tensor_size];
		int i_len = GetComputeTensorSize();
		for (int i=0; i<tensor_size; i++) {
			if (i < i_len) {
				result[0][i] = first_row.get(i);
				result[1][i] = second_row.get(i);
			} else {
				result[0][i] = -1;
				result[1][i] = -1;
			}
		}
		return result;
	}
	
}
