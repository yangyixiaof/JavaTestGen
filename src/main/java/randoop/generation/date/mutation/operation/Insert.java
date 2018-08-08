package randoop.generation.date.mutation.operation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cern.colt.matrix.impl.DenseObjectMatrix2D;
import randoop.generation.date.embed.StringIDAssigner;
import randoop.generation.date.embed.TypedOperationIDAssigner;
import randoop.generation.date.sequence.LinkedSequence;
import randoop.operation.TypedOperation;
import randoop.sequence.Variable;

public class Insert extends MutationOperation {

	int index = -1;
	TypedOperation operation = null;
	List<Variable> inputVariables = new LinkedList<Variable>();

	public Insert(LinkedSequence sequence, int index, TypedOperation operation, List<Variable> inputVariables) {
		super(sequence);
		this.index = index;
		this.operation = operation;
		this.inputVariables.addAll(inputVariables);
	}

	public LinkedSequence ApplyMutation() {
		return sequence.insert(this, index, operation, inputVariables);
	}

	@Override
	public String toString() {
		return "Insert at:" + index + "#operation:" + operation + "#inputs:" + inputVariables;
	}

	@Override
	public DenseObjectMatrix2D toComputeTensor(TypedOperationIDAssigner operation_id_assigner, StringIDAssigner string_id_assigner) {
		int length = inputVariables.size() + 2;
		DenseObjectMatrix2D dom2d = new DenseObjectMatrix2D(2, length);
//		int[][] result = new int[2][length];
		dom2d.set(0, 0, index);
		dom2d.set(1, 0, 2);
//		result[0][0] = index;
//		result[1][0] = 2;
//		Assert.isTrue(operation_id_map.containsKey(operation));
		dom2d.set(0, 1, operation_id_assigner.AssignID(operation));
		dom2d.set(1, 1, 1);
//		result[0][1] = operation_id_map.get(operation);
//		result[1][1] = 1;
		Iterator<Variable> iitr = inputVariables.iterator();
		int idx = 2;
		while (iitr.hasNext()) {
			Variable v = iitr.next();
			dom2d.set(0, idx, v.getDeclIndex());
			dom2d.set(1, idx, 0);
//			result[0][idx] = v.getDeclIndex();
//			result[1][idx] = 0;
			idx++;
		}
		return dom2d;
	}

}
