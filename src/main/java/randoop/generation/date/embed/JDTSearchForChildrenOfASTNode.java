package randoop.generation.date.embed;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;

public class JDTSearchForChildrenOfASTNode {

	@SuppressWarnings("unchecked")
	public static List<ASTNode> GetChildren(ASTNode node) {
		List<ASTNode> children = new LinkedList<ASTNode>();
		List<StructuralPropertyDescriptor> list = node.structuralPropertiesForType();
		for (int i = 0; i < list.size(); i++) {
			StructuralPropertyDescriptor curr = (StructuralPropertyDescriptor) list.get(i);
			Object child = node.getStructuralProperty(curr);
			if (child instanceof List) {
				List<ASTNode> child_nodes = (List<ASTNode>) child;
				children.addAll(child_nodes);
			} else if (child instanceof ASTNode) {
				// return new ASTNode[] { (ASTNode) child };
				children.add((ASTNode) child);
			}
		}
		return children;
	}

}
