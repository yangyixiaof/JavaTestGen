package randoop.generation.date.embed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import randoop.generation.date.util.tensor.NPArrayHelper;

public class TCEASTTree {
	
	// depth information of ASTNode
	Map<ASTNode, Integer> depth_forward = new HashMap<ASTNode, Integer>();
	Map<Integer, LinkedList<ASTNode>> depth_backward = new TreeMap<Integer, LinkedList<ASTNode>>();
	
	// the following three are set up by decode and are used both by decode and encode
//	Map<ASTNode, Integer> node_encode_index_map = new HashMap<ASTNode, Integer>();
//	Map<ASTNode, Integer> node_encode_first_child_index_map = new HashMap<ASTNode, Integer>();
//	Map<ASTNode, Integer> node_encode_last_child_index_map = new HashMap<ASTNode, Integer>();
	
	ArrayList<StatementComputeTensorGenerator> statement_compute_tensor = new ArrayList<StatementComputeTensorGenerator>();
	ArrayList<IBinding> statement_binding_map = new ArrayList<IBinding>();
	Map<IBinding, Integer> binding_statement_map = new HashMap<IBinding, Integer>();
	
	ArrayList<ArrayList<Integer>> statement_first_row = new ArrayList<ArrayList<Integer>>();
	ArrayList<ArrayList<Integer>> statement_second_row = new ArrayList<ArrayList<Integer>>();
	
	Map<String, Integer> basic_elements_id_backward = null;
	
	public TCEASTTree(Map<String, Integer> basic_elements_id_backward) {// int[][] to_compute_embed_tensor
//		this.to_compute_embed_tensor = to_compute_embed_tensor;
		this.basic_elements_id_backward = basic_elements_id_backward;
	}
	
	private void AssertNodeHasSameLevel(ASTNode node, int depth, Map<Integer, LinkedList<ASTNode>> depth_backward) {
		ASTNode node_parent = node.getParent();
		LinkedList<ASTNode> same_depth_decode_nodes = depth_backward.get(depth);
		if (same_depth_decode_nodes != null && same_depth_decode_nodes.size() > 0) {
			if (node_parent == null) {
				Assert.isTrue(node_parent == same_depth_decode_nodes.getLast().getParent());
			} else {
				Assert.isTrue(node_parent.equals(same_depth_decode_nodes.getLast().getParent()), "node_parent:" + node_parent + "^###^another_parent:" + same_depth_decode_nodes.getLast().getParent() + "^###^another_parent_parent:" + same_depth_decode_nodes.getLast().getParent().getParent());
			}
		}
	}

	public int SetUpAndGetDepth(ASTNode node) {
		if (depth_forward.containsKey(node)) {
		} else {
			// set up depth info
			ASTNode parent = node.getParent();
			int node_depth = parent == null ? 0 : (depth_forward.get(parent) == null ? 0 : depth_forward.get(parent) + 1);
			if (node_depth == 0) {
				AssertNodeHasSameLevel(node, node_depth, depth_backward);
			}
			PutToDepthForwardBackward(depth_forward, depth_backward, node, node_depth);
		}
		return depth_forward.get(node);
	}
	
	private void PutToDepthForwardBackward(Map<ASTNode, Integer> depth_forward, Map<Integer, LinkedList<ASTNode>> depth_backward, ASTNode node, int depth) {
		depth_forward.put(node, depth);
		PutToDepthBackward(depth_backward, node, depth);
	}
	
	private void PutToDepthBackward(Map<Integer, LinkedList<ASTNode>> depth_backward, ASTNode node, int depth) {
		LinkedList<ASTNode> ll = depth_backward.get(depth);
		if (ll == null) {
			ll = new LinkedList<ASTNode>();
			depth_backward.put(depth, ll);
		}
		ll.add(node);
	}
	
	public int Size() {
		return depth_forward.size();
	}
	
	public boolean Contains(ASTNode node) {
		return depth_forward.containsKey(node);
	}
	
	public void GenerateEncodeDecodeData() {
		GenerateEncodeDataFromDepthInfo();
	}
	
	private void GenerateEncodeDataFromDepthInfo() {
		// start generating tensor which is used for computing embed
		LinkedList<ASTNode> statements_nodes = depth_backward.get(0);
		for (ASTNode statement_root : statements_nodes) {
			HandleASTNode(statement_root);
		}
//		List<Integer> keys = new ArrayList<Integer>(depth_backward.keySet());
//		Collections.reverse(keys);
//		Iterator<Integer> kitr = keys.iterator();
//		int node_count = 0;
//		while (kitr.hasNext()) {
//			Integer key = kitr.next();
//			LinkedList<ASTNode> all_nodes = depth_backward.get(key);
//			Iterator<ASTNode> aitr = all_nodes.iterator();
//			while (aitr.hasNext()) {
//				ASTNode anode = aitr.next();
//				Integer child_start = node_encode_child_start_map.get(anode);
//				Integer child_end = node_encode_child_end_map.get(anode);
//				int child_start_idx = child_start == null ? -1 : child_start;
//				int child_end_idx = child_end == null ? -1 : child_end;
//				List<ASTNode> anode_children = JDTSearchForChildrenOfASTNode.GetChildren(anode);
//				boolean is_leaf_node = (anode_children == null) || (anode_children.size() == 0);
//				TypeContentID type_content_id = TypeContentIDFetcher.FetchTypeContentID(anode, is_leaf_node, im);
//				if (!is_root_tree || key > 0) {
////				if (key > 0) {
//					ASTNode parent = anode.getParent();
//					List<ASTNode> children = JDTSearchForChildrenOfASTNode.GetChildren(parent);
//					assert (children != null && children.size() > 0);
//					int aidx = children.indexOf(anode);
//					if (aidx == 0) {
//						int first_index = tensor.StoreOneEncodeNode(-1, -1,
//								im.GetTypeID(IDManager.InitialLeafASTType), im.GetContentID(IDManager.Default),
//								happened_type.get(im.GetTypeID(IDManager.InitialLeafASTType)), happened_content.get(im.GetContentID(IDManager.Default)), IDManager.InitialLeafASTType, IDManager.Default);
//						node_encode_first_child_index_map.put(parent, first_index);
//						node_count++;
//						node_encode_child_start_map.put(parent, first_index);
//					}
//					int anode_index = tensor.StoreOneEncodeNode(child_start_idx, child_end_idx, type_content_id.GetTypeID(),
//							type_content_id.GetContentID(), happened_type.get(type_content_id.GetTypeID()), happened_content.get(type_content_id.GetContentID()), type_content_id.GetType(), type_content_id.GetContent());
//					node_encode_index_map.put(anode, anode_index);
//					node_count++;
//					if (aidx == children.size() - 1) {
//						int last_index = tensor.StoreOneEncodeNode(-1, -1,
//								im.GetTypeID(IDManager.TerminalLeafASTType), im.GetContentID(IDManager.Default),
//								happened_type.get(im.GetTypeID(IDManager.TerminalLeafASTType)), happened_content.get(im.GetContentID(IDManager.Default)), IDManager.TerminalLeafASTType, IDManager.Default);
//						node_encode_last_child_index_map.put(parent, last_index);
//						node_count++;
//						node_encode_child_end_map.put(parent, last_index);
//					}
//				} else {
//					int anode_index = tensor.StoreOneEncodeNode(child_start_idx, child_end_idx, type_content_id.GetTypeID(),
//							type_content_id.GetContentID(), happened_type.get(type_content_id.GetTypeID()), happened_content.get(type_content_id.GetContentID()), type_content_id.GetType(), type_content_id.GetContent());
//					node_encode_index_map.put(anode, anode_index);
//					node_count++;
//				}
//			}
//			tensor.StoreOneParallelEncodePhase(node_count);
//		}
	}
	
	private void HandleASTNode(ASTNode node) {
		boolean node_handled = false;
		if (node instanceof VariableDeclarationStatement) {
			@SuppressWarnings("unchecked")
			List<VariableDeclarationFragment> vdfs = ((VariableDeclarationStatement)node).fragments();
			Assert.isTrue(vdfs.size() == 1);
			HandleVariableFragment(vdfs.get(0));
			node_handled = true;
		}
		if (node instanceof ExpressionStatement) {
			Expression expr = ((ExpressionStatement)node).getExpression();
			HandleASTNode(expr);
			node_handled = true;
		}
		if (node instanceof VariableDeclarationExpression) {
			VariableDeclarationExpression svd_expr = (VariableDeclarationExpression)node;
			@SuppressWarnings("unchecked")
			List<VariableDeclarationFragment> vdfs = svd_expr.fragments();
			Assert.isTrue(vdfs.size() == 1);
			HandleVariableFragment(vdfs.get(0));
			node_handled = true;
		}
		if (node instanceof Assignment) {
			Assignment as_expr = (Assignment)node;
			if (as_expr.getParent() instanceof Statement) {
				Expression left_as_expr = as_expr.getLeftHandSide();
				if (left_as_expr instanceof SimpleName) {
					SimpleName sn = (SimpleName)left_as_expr;
					IBinding binding = sn.resolveBinding();
					binding_statement_map.put(binding, statement_binding_map.size());
					statement_binding_map.add(binding);
					Expression right_as_expr = as_expr.getRightHandSide();
					StatementComputeTensorGenerator t_gen = new StatementComputeTensorGenerator(binding_statement_map, basic_elements_id_backward, null);
					right_as_expr.accept(t_gen);
					statement_compute_tensor.add(t_gen);
					node_handled = true;
				}
			}
		}
		if (node instanceof MethodInvocation) {
			MethodInvocation mi_expr = (MethodInvocation)node;
			if (mi_expr.getParent() instanceof Statement) {
				Expression expr = mi_expr.getExpression();
				if (expr instanceof SimpleName) {
					SimpleName sn = (SimpleName)expr;
					IBinding binding = sn.resolveBinding();
					binding_statement_map.put(binding, statement_binding_map.size());
					statement_binding_map.add(binding);
					Set<ASTNode> skip_nodes = new HashSet<ASTNode>();
					skip_nodes.add(expr);
					StatementComputeTensorGenerator t_gen = new StatementComputeTensorGenerator(binding_statement_map, basic_elements_id_backward, skip_nodes);
					mi_expr.accept(t_gen);
					statement_compute_tensor.add(t_gen);
					node_handled = true;
				}
			}
		}
		if (!node_handled) {
			new Exception("Some strange node not in handling list #node_type:" + node.getClass() + "#node_content:" + node.toString()).printStackTrace();
			System.exit(1);
		}
	}
	
	private void HandleVariableFragment(VariableDeclarationFragment vdf) {
		IBinding binding = vdf.getName().resolveBinding();
		Assert.isTrue(binding != null);
		Assert.isTrue((vdf.getParent() instanceof Statement) || (vdf.getParent() instanceof VariableDeclarationExpression && vdf.getParent() instanceof Statement));
		binding_statement_map.put(binding, statement_binding_map.size());
		statement_binding_map.add(binding);
		StatementComputeTensorGenerator t_gen = new StatementComputeTensorGenerator(binding_statement_map, basic_elements_id_backward, null);
		vdf.getInitializer().accept(t_gen);
		statement_compute_tensor.add(t_gen);
	}
	
	public int[][][] ToNormalizedTensor() {
		int s_len = statement_compute_tensor.size();
		int max_last_dimension_size = 0;
		for (int s = 0; s < s_len; s++) {
			StatementComputeTensorGenerator sctg = statement_compute_tensor.get(s);
			int s_size = sctg.GetComputeTensorSize();
			if (max_last_dimension_size < s_size) {
				max_last_dimension_size = s_size;
			}
		}
		int[][][] result = new int[s_len][2][max_last_dimension_size];
		for (int s = 0; s < s_len; s++) {
			StatementComputeTensorGenerator sctg = statement_compute_tensor.get(s);
			int[][] one_statement_tensor = sctg.ToNormalizedTensor(max_last_dimension_size);
			NPArrayHelper.CopyArray(one_statement_tensor, result[s]);
		}
		return result;
	}
	
}
