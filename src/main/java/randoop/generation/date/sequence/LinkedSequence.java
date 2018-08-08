package randoop.generation.date.sequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.Assert;

import cern.colt.matrix.ObjectFactory2D;
import cern.colt.matrix.ObjectMatrix2D;
import cern.colt.matrix.impl.DenseObjectMatrix2D;
import cn.yyx.labtask.test_agent_trace_reader.TraceInfo;
import randoop.Globals;
import randoop.generation.date.embed.StringIDAssigner;
import randoop.generation.date.embed.TypedOperationIDAssigner;
import randoop.generation.date.mutation.operation.MutationOperation;
import randoop.generation.date.mutation.operation.PrimitiveBooleanModify;
import randoop.generation.date.mutation.operation.PrimitiveIntegralModify;
import randoop.generation.date.mutation.operation.PrimitiveRealModify;
import randoop.generation.date.mutation.operation.Remove;
import randoop.generation.date.mutation.operation.StringAlterModify;
import randoop.generation.date.mutation.operation.StringInsertModify;
import randoop.generation.date.mutation.operation.StringRemoveModify;
import randoop.generation.date.runtime.DateRuntime;
import randoop.generation.date.tensorflow.QTransition;
import randoop.operation.TypedOperation;
import randoop.sequence.Sequence;
import randoop.sequence.Statement;
import randoop.sequence.Variable;
import randoop.types.PrimitiveType;
import randoop.types.Type;
import randoop.util.SimpleArrayList;
import randoop.util.SimpleList;

public class LinkedSequence extends Sequence {

	QTransition input_transition = null;
	// the key is the action indexes
	Map<Integer, QTransition> output_transitions = new TreeMap<Integer, QTransition>();
	
	LinkedSequence last_sequence = null;
//	Map<Statement, Integer> curr_statement_in_last_sequence_index_map = new HashMap<>();
	
	TraceInfo trace_info = null;
	MutationOperation mo = null;
	
	// public TraceableSequence(Sequence curr_sequence, Sequence last_sequence) {
	// this.curr_sequence = curr_sequence;
	// this.last_sequence = last_sequence;
	// }

	public LinkedSequence() {
		super();
	}

	public LinkedSequence(Sequence sequence) {
		super(sequence.statements, computeHashcode(sequence.statements), computeNetSize(sequence.statements));
	}

	public LinkedSequence(SimpleList<Statement> statements, // Map<Statement, Integer> curr_statement_in_last_sequence_index_map, 
			LinkedSequence last_sequence, MutationOperation mo) {
		super(statements, computeHashcode(statements), computeNetSize(statements));
		this.last_sequence = last_sequence;
		this.mo = mo;
		if (last_sequence.mo == null) {
			if (last_sequence.last_sequence != null) {
				this.last_sequence = last_sequence.last_sequence;
				Assert.isTrue(last_sequence.trace_info == null);
			}
		}
//		if (curr_statement_in_last_sequence_index_map != null) {
//			this.curr_statement_in_last_sequence_index_map.putAll(curr_statement_in_last_sequence_index_map);
//		}
	}

	/**
	 * 
	 * @param index
	 * @param operation
	 * @param inputVariables
	 * @return a new sequence
	 */
	public final LinkedSequence insert(MutationOperation mo, int index, TypedOperation operation, List<Variable> inputVariables) {
		if (index < 0 || this.size() < index) {
			String msg = "this.size():" + this.size() + " but index:" + index;
			throw new IllegalArgumentException(msg);
		}
		checkInputs(operation, inputVariables);
		
		for (Variable v : inputVariables) {
			if (v.index >= index) {
				return this;
			}
		}

		List<Sequence.RelativeNegativeIndex> indexListOfNewStatment = new ArrayList<>(1);
		for (Variable v : inputVariables) {
			indexListOfNewStatment.add(getRelativeIndexForVariable(index, v));
		}
		Statement newStatement = new Statement(operation, indexListOfNewStatment);

		HashMap<Statement, Integer> curr_statement_in_last_sequence_index_map_inner = new HashMap<>();

		SimpleArrayList<Statement> newStatements = new SimpleArrayList<>();
		for (int i = 0; i < index; i++) {
			Statement stmt = this.getStatement(i);
			curr_statement_in_last_sequence_index_map_inner.put(stmt, i);
			newStatements.add(stmt);
		}
		newStatements.add(newStatement);
		
		for (int i = index; i < this.size(); i++) {
			Statement stmt = this.getStatement(i);
			curr_statement_in_last_sequence_index_map_inner.put(stmt, i);
			List<Sequence.RelativeNegativeIndex> newInputs = new ArrayList<>();
			for (Sequence.RelativeNegativeIndex rni : stmt.getInputs()) {
				if (rni.index + i < index) {
					newInputs.add(new Sequence.RelativeNegativeIndex(rni.index - 1));
				} else {
					newInputs.add(new Sequence.RelativeNegativeIndex(rni.index));
				}
			}
			newStatements.add(new Statement(stmt.getOperation(), newInputs));
		}
		// curr_statement_in_last_sequence_index_map_inner, 
		return new LinkedSequence(newStatements, this, mo);
	}

	/**
	 * @param index
	 * @param operation
	 * @return
	 */
	public final LinkedSequence insert(MutationOperation mo, int index, TypedOperation operation) {
		return insert(mo, index, operation, new ArrayList<Variable>());
	}

	/**
	 *
	 * @param stmtIndex
	 * @param varIndex
	 * @param targetVariable
	 * @return
	 */
	public final LinkedSequence modifyReference(MutationOperation mo, int stmtIndex, int varIndex, Variable targetVariable) {
		// checkInputsForModifyReference
		if (stmtIndex < 0 || this.size() < stmtIndex) {
			String msg = "this.size():" + this.size() + " but stmtIndex:" + stmtIndex;
			throw new IllegalArgumentException(msg);
		}
		Statement statementToModify = statements.get(stmtIndex);
		TypedOperation opOfTheModified = statementToModify.getOperation();
		if (varIndex < 0 || opOfTheModified.getInputTypes().size() < varIndex) {
			String msg = "opOfTheModified.getInputTypes().size():" + opOfTheModified.getInputTypes().size()
					+ " but varIndex:" + varIndex;
			throw new IllegalArgumentException(msg);
		}
		if (targetVariable.sequence != this) {
			String msg = "targetVariable.owner != this for" + Globals.lineSep + "sequence: " + toString()
					+ Globals.lineSep + "targetVariable:" + targetVariable;
			throw new IllegalArgumentException(msg);
		}

		Type targetVarType = statements.get(targetVariable.index).getOutputType();
		if (targetVarType == null) {
			String msg = "targetVarType == null for" + Globals.lineSep + "sequence: " + toString() + Globals.lineSep
					+ "targetVariable:" + targetVariable;
			throw new IllegalArgumentException(msg);
		}

		if (!opOfTheModified.getInputTypes().get(varIndex).isAssignableFrom(targetVarType)) {
			return this;
		}
		
		if (targetVariable.index >= stmtIndex) {
			return this;
		}

		HashMap<Statement, Integer> curr_statement_in_last_sequence_index_map_inner = new HashMap<>();
		SimpleArrayList<Statement> newStatements = new SimpleArrayList<>();
		for (int i = 0; i < this.size(); i++) {
			if (i != stmtIndex) {
				Statement stmt = this.getStatement(i);
				curr_statement_in_last_sequence_index_map_inner.put(stmt, i);
				newStatements.add(stmt);
			} else {
				List<Sequence.RelativeNegativeIndex> newInputs = new ArrayList<>();
				for (int j = 0; j < statementToModify.getInputs().size(); j++) {
					if (j != varIndex) {
						Sequence.RelativeNegativeIndex rni = statementToModify.getInputs().get(j);
						newInputs.add(new Sequence.RelativeNegativeIndex(rni.index));
					} else {
						newInputs.add(new Sequence.RelativeNegativeIndex(targetVariable.index - stmtIndex));
					}
				}
				Statement modifiedStatement = new Statement(statementToModify.getOperation(), newInputs);
				newStatements.add(modifiedStatement);
			}
		}
		// curr_statement_in_last_sequence_index_map_inner, 
		return new LinkedSequence(newStatements, this, mo);
	}

	/**
	 * @param stmtIndex
	 * @param varIndex
	 * @return
	 */
	public final LinkedSequence modifyBoolean(PrimitiveBooleanModify pbm, int stmtIndex, int varIndex) {
		Statement stmtToModify = this.getStatement(stmtIndex);
		// Type typeOfVarToModify = stmtToModify.getInputTypes().get(varIndex); //
		int varSourceStmtIndex = stmtIndex + stmtToModify.getInputs().get(varIndex).index;
		Type typeOfVarToModify = this.getStatement(varSourceStmtIndex).getOutputType();

		Boolean isBool = false, isBoxed = null;
		if (typeOfVarToModify.equals(PrimitiveType.forClass(boolean.class))) {
			isBool = true;
			isBoxed = false;
		} else if (typeOfVarToModify.equals(PrimitiveType.forClass(Boolean.class))) {
			isBool = true;
			isBoxed = true;
		}

		if (!isBool) {
			throw new IllegalArgumentException(
					String.format("modifyBoolean-ing a non-boolean variable. statement index: %d, input index: %d",
							stmtIndex, varIndex));
		}

		try {
			TypedOperation methodCallNot;
			if (isBoxed) {
				methodCallNot = TypedOperation.forMethod(DateRuntime.class.getMethod("not", Boolean.class));
			} else {
				methodCallNot = TypedOperation.forMethod(DateRuntime.class.getMethod("not", boolean.class));
			}

			LinkedSequence insertedFlip = this.insert(null, varSourceStmtIndex + 1, methodCallNot,
					Arrays.asList(new Variable(this, varSourceStmtIndex)));

			return insertedFlip.modifyReference(pbm, stmtIndex + 1, varIndex,
					new Variable(insertedFlip, varSourceStmtIndex + 1));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param stmtIndex
	 * @param varIndex
	 * @return
	 */
	public final LinkedSequence modifyReal(PrimitiveRealModify prm, int stmtIndex, int varIndex, Object deltaValue) {
		Statement stmtToModify = this.getStatement(stmtIndex); // 鏄惁瑕佹妸瓒婄晫 Exception 鍖呮垚鏇翠笟鍔＄殑 Exception锛�
		// Type typeOfVarToModify = stmtToModify.getInputTypes().get(varIndex); //
		// 鏈夊己杞椂锛岄瀷瀛愬舰鐘� != 鑴氱殑褰㈢姸
		// 鎵惧埌 浜х敓鍑鸿鏀瑰彉閲� 鐨勯偅涓�鍙�
		int varSourceStmtIndex = stmtIndex + stmtToModify.getInputs().get(varIndex).index;
		Type typeOfVarToModify = this.getStatement(varSourceStmtIndex).getOutputType();

		Class<?> classOfVarToModify = DateRuntime.realTypeToClass.getOrDefault(typeOfVarToModify, null);
		if (classOfVarToModify == null) {
			throw new IllegalArgumentException(String.format(
					"modifyReal-ing a non-(float, Float, double, Doublle) variable. statement index: %d, input index: %d",
					stmtIndex, varIndex));
		}
		// Class<?> ensurePrimitive =
		// classOfVarToModify.isPrimitive()?classOfVarToModify:
		// PrimitiveTypes.boxedToPrimitive.get(classOfVarToModify);

		try {
			/*W
			 * 1. float delta = 2.0
			 * 2. float a1 = DateRuntime.add(a, delta)
			 * 3. o.f(a) o.f(a1)
			 * 
			 * float a = ... float delta = 2.0 float a1 = DateRuntime.add(a, delta) ...
			 * o.f(a1);
			 */
			TypedOperation deltaInit = TypedOperation.createPrimitiveInitialization(Type.forClass(classOfVarToModify),
					deltaValue
			// ensurePrimitive.cast(deltaValue)
			);
			LinkedSequence insertedDelta = this.insert(null, varSourceStmtIndex + 1, deltaInit);

			TypedOperation addMethodCall = TypedOperation
					.forMethod(DateRuntime.class.getMethod("add", classOfVarToModify, Object.class));
			LinkedSequence insertedAdd = insertedDelta.insert(null, varSourceStmtIndex + 2, addMethodCall,
					Arrays.asList(new Variable(insertedDelta, varSourceStmtIndex),
							new Variable(insertedDelta, varSourceStmtIndex + 1)));

			return insertedAdd.modifyReference(prm, stmtIndex + 2, varIndex,
					new Variable(insertedAdd, varSourceStmtIndex + 2));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 1,1L,(short)1,(byte)1,(char)1
	 *
	 * @param stmtIndex
	 * @param varIndex
	 * @param deltaValue
	 * @return
	 */
	public final LinkedSequence modifyIntegral(PrimitiveIntegralModify pim, int stmtIndex, int varIndex, Object deltaValue) {
		Statement stmtToModify = this.getStatement(stmtIndex);
		// Type typeOfVarToModify = stmtToModify.getInputTypes().get(varIndex);
		int varSourceStmtIndex = stmtIndex + stmtToModify.getInputs().get(varIndex).index;
		Type typeOfVarToModify = this.getStatement(varSourceStmtIndex).getOutputType();

		Class<?> classOfVarToModify = DateRuntime.integralTypeToClass.getOrDefault(typeOfVarToModify, null);
		if (classOfVarToModify == null) {
			throw new IllegalArgumentException(String.format(
					"modifyIntegral-ing a non-(byte, Byte, short, Short, int, Integer, long, Long) variable. statement index: %d, input index: %d",
					stmtIndex, varIndex));
		}
		// Class<?> ensurePrimitive =
		// classOfVarToModify.isPrimitive()?classOfVarToModify:
		// PrimitiveTypes.boxedToPrimitive.get(classOfVarToModify);

		try {
			/*
			 * float a = ... ... o.f(a);
			 * 1. float delta = 2.0
			 * 2. float a1 = DateRuntime.add(a, delta)
			 * 3. o.f(a) o.f(a1)
			 * 
			 * float a = ... float delta = 2.0 float a1 = DateRuntime.add(a, delta) ...
			 * o.f(a1);
			 */

			TypedOperation deltaInit = TypedOperation.createPrimitiveInitialization(Type.forClass(classOfVarToModify),
					deltaValue
			// ensurePrimitive.cast(deltaValue)
			);
			LinkedSequence insertedDelta = this.insert(null, varSourceStmtIndex + 1, deltaInit);

			TypedOperation addMethodCall = TypedOperation
					.forMethod(DateRuntime.class.getMethod("add", classOfVarToModify, Object.class));
			LinkedSequence insertedAdd = insertedDelta.insert(null, varSourceStmtIndex + 2, addMethodCall,
					Arrays.asList(new Variable(insertedDelta, varSourceStmtIndex),
							new Variable(insertedDelta, varSourceStmtIndex + 1)));

			return insertedAdd.modifyReference(pim, stmtIndex + 2, varIndex,
					new Variable(insertedAdd, varSourceStmtIndex + 2));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}

	// public final TraceableSequence modifyIntegral(int stmtIndex, int varIndex,
	// long deltaValue) {
	// return modifyIntegral(stmtIndex, varIndex, Long.valueOf(deltaValue));
	// }
	//
	// public final TraceableSequence modifyIntegral(int stmtIndex, int varIndex,
	// int deltaValue) {
	// return modifyIntegral(stmtIndex, varIndex, Integer.valueOf(deltaValue));
	// }

	public final LinkedSequence modifyStringInsert(StringInsertModify sim, int stmtIndex, int varIndex, int charIndex) {
		/*
		 * String s = ...; ... o.f(s);
		 * 
		 * String s = ...; int charIndex=...; String s1 =
		 * DateRuntime.insert(s,charIndex); ... o.f(s1);
		 */
		try {
			Statement stmtToModify = this.getStatement(stmtIndex);
			int varSourceStmtIndex = stmtIndex + stmtToModify.getInputs().get(varIndex).index;
			Type typeOfVarToModify = this.getStatement(varSourceStmtIndex).getOutputType();

			if (!typeOfVarToModify.getRuntimeClass().equals(String.class)) {
				throw new IllegalArgumentException(String.format(
						"modifyStringInsert-ing a non-Sting variable. statement index: %d, input index: %d", stmtIndex,
						varIndex));
			}

			TypedOperation charIndexInit = TypedOperation
					.createPrimitiveInitialization(PrimitiveType.forClass(int.class), charIndex);
			LinkedSequence insertedDelta = this.insert(null, varSourceStmtIndex + 1, charIndexInit);

			TypedOperation insertMethodCall = TypedOperation
					.forMethod(DateRuntime.class.getMethod("insert", String.class, int.class));
			LinkedSequence insertedInsert = insertedDelta.insert(null, varSourceStmtIndex + 2, insertMethodCall,
					Arrays.asList(new Variable(insertedDelta, varSourceStmtIndex),
							new Variable(insertedDelta, varSourceStmtIndex + 1)));

			return insertedInsert.modifyReference(sim, stmtIndex + 2, varIndex,
					new Variable(insertedInsert, varSourceStmtIndex + 2));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}

	public final LinkedSequence modifyStringRemove(StringRemoveModify srm, int stmtIndex, int varIndex, int charIndex) {
		/*
		 * String s = ...; ... o.f(s);
		 * 
		 * String s = ...; int charIndex=...; String s1 =
		 * DateRuntime.remove(s,charIndex); ... o.f(s1);
		 * 
		 */
		try {
			Statement stmtToModify = this.getStatement(stmtIndex);
			int varSourceStmtIndex = stmtIndex + stmtToModify.getInputs().get(varIndex).index;
			Type typeOfVarToModify = this.getStatement(varSourceStmtIndex).getOutputType();

			if (!typeOfVarToModify.getRuntimeClass().equals(String.class)) {
				throw new IllegalArgumentException(String.format(
						"modifyStringRemove-ing a non-Sting variable. statement index: %d, input index: %d", stmtIndex,
						varIndex));
			}

			TypedOperation charIndexInit = TypedOperation
					.createPrimitiveInitialization(PrimitiveType.forClass(int.class), charIndex);
			LinkedSequence insertedDelta = this.insert(null, varSourceStmtIndex + 1, charIndexInit);

			TypedOperation removeMethodCall = TypedOperation
					.forMethod(DateRuntime.class.getMethod("remove", String.class, int.class));
			LinkedSequence insertedInsert = insertedDelta.insert(null, varSourceStmtIndex + 2, removeMethodCall,
					Arrays.asList(new Variable(insertedDelta, varSourceStmtIndex),
							new Variable(insertedDelta, varSourceStmtIndex + 1)));

			return insertedInsert.modifyReference(srm, stmtIndex + 2, varIndex,
					new Variable(insertedInsert, varSourceStmtIndex + 2));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}

	public final LinkedSequence modifyStringModify(StringAlterModify sam, int stmtIndex, int varIndex, int charIndex, int deltaValue) {
		/*
		 * String s = ...; ... o.f(s);
		 * 
		 * String s = ...; int charIndex=...; int deltaValue=...; String s1 =
		 * DateRuntime.modify(s,charIndex,deltaValue); ... o.f(s1);
		 * 
		 */
		try {
			Statement stmtToModify = this.getStatement(stmtIndex);
			int varSourceStmtIndex = stmtIndex + stmtToModify.getInputs().get(varIndex).index;
			Type typeOfVarToModify = this.getStatement(varSourceStmtIndex).getOutputType();

			if (!typeOfVarToModify.getRuntimeClass().equals(String.class)) {
				throw new IllegalArgumentException(String.format(
						"modifyStringModify-ing a non-Sting variable. statement index: %d, input index: %d", stmtIndex,
						varIndex));
			}

			TypedOperation charIndexInit = TypedOperation
					.createPrimitiveInitialization(PrimitiveType.forClass(int.class), charIndex);
			LinkedSequence insertedDelta = this.insert(null, varSourceStmtIndex + 1, charIndexInit);

			TypedOperation deltaValueInit = TypedOperation
					.createPrimitiveInitialization(PrimitiveType.forClass(int.class), deltaValue);
			LinkedSequence insertedDelta2 = insertedDelta.insert(null, varSourceStmtIndex + 2, deltaValueInit);

			TypedOperation modifyMethodCall = TypedOperation
					.forMethod(DateRuntime.class.getMethod("modify", String.class, int.class, int.class));
			LinkedSequence insertedModify = insertedDelta2.insert(null, varSourceStmtIndex + 3, modifyMethodCall,
					Arrays.asList(new Variable(insertedDelta2, varSourceStmtIndex),
							new Variable(insertedDelta2, varSourceStmtIndex + 1),
							new Variable(insertedDelta2, varSourceStmtIndex + 2)));

			return insertedModify.modifyReference(sam, stmtIndex + 3, varIndex,
					new Variable(insertedModify, varSourceStmtIndex + 3));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param stmtIndex
	 * @param varIndex
	 * @param deltaValue
	 * 
	 * @return
	 */
	@Deprecated
	public final LinkedSequence modifyPrimitive(int stmtIndex, int varIndex, Object deltaValue) {
		return null;
	}

	/**
	 * 0 {}
	 * 1 {-1}
	 * 2 {...}
	 * 3 {-1,-2}
	 * 4 {-4,-3}
	 * 5 {-2}
	 *
	 * remove(2)
	 * 0 {}
	 * 1 {-1}
	 * 2 {-2,-1}
	 * 
	 * @param stmtIndex
	 * @return
	 */
	public final LinkedSequence remove(Remove rmv, int stmtIndex) {
		if (stmtIndex < 0 || this.size() <= stmtIndex) {
			String msg = "this.size():" + this.size() + " but stmtIndex:" + stmtIndex
					+ ". Expected 0 <= stmtIndex < this.size().";
			throw new IllegalArgumentException(msg);
		}

		ArrayList<ArrayList<Integer>> dependedRelationAdj = calculateDependedRelationAdjacencyList();

		int stmtNum = dependedRelationAdj.size();
		boolean[] visited = new boolean[stmtNum];
		Arrays.fill(visited, false);
		boolean[] toRemove = new boolean[stmtNum];
		Arrays.fill(toRemove, false);
		dfsForRemoval(dependedRelationAdj, stmtIndex, visited, toRemove);

		HashMap<Statement, Integer> curr_statement_in_last_sequence_index_map_inner = new HashMap<>();
		SimpleArrayList<Statement> newStatements = new SimpleArrayList<>();

		int[] howManyRemovedBetween0And = new int[stmtNum];
		howManyRemovedBetween0And[0] = toRemove[0] ? 1 : 0;
		for (int i = 1; i < stmtNum; i++) {
			howManyRemovedBetween0And[i] = howManyRemovedBetween0And[i - 1] + (toRemove[i] ? 1 : 0);
		}

		for (int i = 0; i < size(); ++i) {
			if (toRemove[i]) {
				// howManyRemovedBeforeMe++;
			} else {
				Statement stmt = this.getStatement(i);
				curr_statement_in_last_sequence_index_map_inner.put(stmt, i);
				List<Sequence.RelativeNegativeIndex> newInputs = new ArrayList<>();
				for (Sequence.RelativeNegativeIndex rni : stmt.getInputs()) {
					int argStmtIndex = i + rni.index; // 璇ヨ緭鍏ユ槸绗嚑鍙ヤ骇鐢熺殑
					int howManyRemovedBetweenArgAndCall = argStmtIndex == 0 ? howManyRemovedBetween0And[i]
							: howManyRemovedBetween0And[i] - howManyRemovedBetween0And[argStmtIndex - 1];
					newInputs.add(new Sequence.RelativeNegativeIndex(rni.index + howManyRemovedBetweenArgAndCall));
				}
				newStatements.add(new Statement(stmt.getOperation(), newInputs));
			}
		}
		// curr_statement_in_last_sequence_index_map_inner, 
		return new LinkedSequence(newStatements, this, rmv);
	}

	/**
	 * Calculate "depended" relation adjacency list for this Sequence.
	 *
	 * @return
	 */
	final ArrayList<ArrayList<Integer>> calculateDependedRelationAdjacencyList() {
		ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
		for (int i = 0; i < this.size(); i++) {
			adj.add(new ArrayList<Integer>());
			for (Sequence.RelativeNegativeIndex rni : this.getStatement(i).getInputs()) {
				int dependedStmt = i + rni.index;
				if (!adj.get(dependedStmt).contains(i)) {
					adj.get(dependedStmt).add(i);
				}
			}
		}
		return adj;
	}

	/**
	 * toRemove[i] = true銆�
	 *
	 * <p>
	 * Side effect: update visited and toRemove!!!
	 *
	 * @param dependedRelationAdj
	 * @param stmtIndexToRemove
	 * @param visited
	 * @param toRemove
	 */
	final void dfsForRemoval(ArrayList<ArrayList<Integer>> dependedRelationAdj, int stmtIndexToRemove,
			boolean[] visited, boolean[] toRemove) {
		if (!visited[stmtIndexToRemove]) {
			toRemove[stmtIndexToRemove] = true;
			visited[stmtIndexToRemove] = true;
			for (Integer neighbor : dependedRelationAdj.get(stmtIndexToRemove)) {
				dfsForRemoval(dependedRelationAdj, neighbor, visited, toRemove);
			}
		}
	}

	public String toLongFormString() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < size(); i++) {
			// Don't dump primitive initializations, if using literals.
			// But do print them if they are the last statement;
			// otherwise, the sequence might print as the empty string.
			// if (i != size() - 1) {
			// if (canUseShortForm() && getStatement(i).getShortForm() != null) {
			// continue;
			// }
			// }
			appendCode(b, i);
			b.append(Globals.lineSep);
		}
		return b.toString();
	}
	
	public DenseObjectMatrix2D toComputeTensor(TypedOperationIDAssigner operation_id_assigner, StringIDAssigner string_id_assigner) {
		DenseObjectMatrix2D matrix = new DenseObjectMatrix2D(2,0);
		int stmt_size = this.size();
		for (int i=0;i<stmt_size;i++) {
			Statement stmt = this.getStatement(i);
			int id = operation_id_assigner.AssignID(stmt.getOperation());
			List<RelativeNegativeIndex> rnis = stmt.getInputs();
			int one_statement_size = (rnis == null ? 0 : rnis.size()) + 1;
			ObjectMatrix2D one_statement_matrix = new DenseObjectMatrix2D(2, one_statement_size);
			one_statement_matrix.set(0, 0, id);
			one_statement_matrix.set(1, 0, 2);
			Iterator<RelativeNegativeIndex> rni_itr = rnis.iterator();
			int j=1;
			while (rni_itr.hasNext()) {
				RelativeNegativeIndex rni = rni_itr.next();
				int real_index = i+rni.index;
				one_statement_matrix.set(0, j, real_index);
				one_statement_matrix.set(1, j, 0);
				j++;
			}
			matrix = (DenseObjectMatrix2D) ObjectFactory2D.dense.appendColumns(matrix, one_statement_matrix);
		}
		return matrix;
	}

//	@Override
//	public int compareTo(LinkedSequence o) {
//		return toLongFormString().compareTo(o.toLongFormString());
//	}

	public void SetExecutionTrace(TraceInfo ti) {
		this.trace_info = ti;
	}

	public TraceInfo GetTraceInfo() {
		return this.trace_info;
	}
	
	public void SetInputQTransition(QTransition transition) {
		input_transition = transition;
	}

	public QTransition GetInputQTransition() {
		return input_transition;
	}
	
	public void SetOutputQTransition(int action_index, QTransition transition) {
		output_transitions.put(action_index, transition);
	}
	
	public QTransition GetOutputQTransition(int action_index) {
		return output_transitions.get(action_index);
	}
	
	public Map<Integer, QTransition> GetOutputQTransitions() {
		return output_transitions;
	}
	
//	public static void main(String[] args) {
//		DenseObjectMatrix2D matrix = new DenseObjectMatrix2D(2,0);
//		System.out.println("matrix:" + matrix);
//	}
	
}
