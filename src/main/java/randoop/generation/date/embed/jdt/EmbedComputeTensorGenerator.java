package randoop.generation.date.embed.jdt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;


public class EmbedComputeTensorGenerator extends ASTVisitor {
	
	boolean begin_generation = false;
	int need_to_generate_node_num = -1;
	int[][][] to_compute_embed_tensor = null;
	
	ArrayList<String> basic_elements_id_forward = null;
	Map<String, Integer> basic_elements_id_backward = null;
	
	TCEASTTree tce_ast = null;
	
	public EmbedComputeTensorGenerator(ArrayList<String> basic_elements_id_forward, Map<String, Integer> basic_elements_id_backward) {
		this.basic_elements_id_forward = basic_elements_id_forward;
		this.basic_elements_id_backward = basic_elements_id_backward;
		this.tce_ast = new TCEASTTree(basic_elements_id_backward);
	}
	
	@Override
	public void preVisit(ASTNode node) {
		if (begin_generation) {
			tce_ast.SetUpAndGetDepth(node);
		}
		if (node instanceof Block && (node.getParent() instanceof MethodDeclaration) && (node.getParent().getParent() instanceof TypeDeclaration)) {
			begin_generation = true;
			Map<ASTNode, Integer> node_count = new HashMap<ASTNode, Integer>();
			node.accept(new NodeCountVisitor(node_count));
		}
		super.preVisit(node);
	}
	
	@Override
	public void postVisit(ASTNode node) {
		if (node instanceof Block && (node.getParent() instanceof MethodDeclaration)) {
			to_compute_embed_tensor = tce_ast.ToNormalizedTensor();
			begin_generation = false;
		}
		super.postVisit(node);
	}
	
	public int[][][] GetToComputeEmbedTensor() {
		return to_compute_embed_tensor;
	}
	
}

class NodeCountVisitor extends ASTVisitor {

	// count of nodes of ASTNode (include itself and all its descendant nodes)
	Map<ASTNode, Integer> node_count = null;

	public NodeCountVisitor(Map<ASTNode, Integer> node_count) {
		this.node_count = node_count;
	}

	@Override
	public void postVisit(ASTNode node) {
		int count = 1;
		List<ASTNode> node_children = JDTSearchForChildrenOfASTNode.GetChildren(node);
		if (node_children != null && node_children.size() > 0) {
			for (ASTNode child : node_children) {
				count += node_count.get(child);
			}
		}
		node_count.put(node, count);
		super.postVisit(node);
	}

}
