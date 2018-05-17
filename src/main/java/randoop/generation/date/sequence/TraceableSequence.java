package randoop.generation.date.sequence;

import java.util.*;
import randoop.Globals;
import randoop.generation.date.runtime.DateRuntime;
import randoop.operation.TypedOperation;
import randoop.sequence.Sequence;
import randoop.sequence.Statement;
import randoop.sequence.Variable;
import randoop.types.PrimitiveType;
import randoop.types.Type;
import randoop.util.SimpleArrayList;
import randoop.util.SimpleList;

public class TraceableSequence extends Sequence implements Comparable<TraceableSequence> {

	final TraceableSequence last_sequence;
	final Map<Statement, Integer> curr_statement_in_last_sequence_index_map = new HashMap<>();

	// public TraceableSequence(Sequence curr_sequence, Sequence last_sequence) {
	// this.curr_sequence = curr_sequence;
	// this.last_sequence = last_sequence;
	// }

	public TraceableSequence() {
		super();
		last_sequence = null;
	}

	public TraceableSequence(Sequence sequence) {
		super(sequence.statements, computeHashcode(sequence.statements), computeNetSize(sequence.statements));
		this.last_sequence = null;
	}

	public TraceableSequence(SimpleList<Statement> statements,
			Map<Statement, Integer> curr_statement_in_last_sequence_index_map, TraceableSequence last_sequence) {
		super(statements, computeHashcode(statements), computeNetSize(statements));
		this.last_sequence = last_sequence;
		if (curr_statement_in_last_sequence_index_map != null) {
			this.curr_statement_in_last_sequence_index_map.putAll(curr_statement_in_last_sequence_index_map);
		}
	}

	/**
	 * 鍦� this Sequence 涓彃鍏ヤ竴涓� Statement锛屼娇杩欎釜 Statement 鎴愪负璇� Sequence 鐨勭 index
	 * 涓鍙ワ紝鍏朵粬璇彞鍚庢帹銆�0 璧疯鏁般��
	 *
	 * <p>
	 * 鐩告瘮涔嬩笅锛宔xtend 鏄湪鏈熬鎻掑叆涓�涓� Statement銆傚綋 index==this.size() 鏃讹紝insert 閫�鍖栦负
	 * extend銆�
	 *
	 * @param index
	 *            瑕佹妸璇彞鎻掑埌鍝効
	 * @param operation
	 * @param inputVariables
	 * @return 鎻掑叆璇彞鍚庣殑鏂� sequence
	 */
	public final TraceableSequence insert(int index, TypedOperation operation, List<Variable> inputVariables) {
		// 1 妫�鏌ュ弬鏁�
		if (index < 0 || this.size() < index) {
			String msg = "this.size():" + this.size() + " but index:" + index;
			throw new IllegalArgumentException(msg);
		}
		checkInputs(operation, inputVariables);

		// 1.1 纭繚鍙紩鐢� index 涔嬪墠鐨勫彉閲忋��
		// TODO
		// 鍙湪杩欓噷纭繚鏄笉澶熺殑銆傚彲浠ュ湪杩欏効杩囨护锛屼絾瑕佷繚璇佹晥鐜囪繕寰楀湪璋冪敤澶勪粠婧愬ご灏卞彧閫夊墠闈㈢殑鍙橀噺銆傦紙浣嗘槸鎴戜滑鍏�"瀹屾垚鐩爣"
		// 锛氾級
		for (Variable v : inputVariables) {
			if (v.index >= index) {
				// 涓嶈兘鎶� RE銆傚洜涓哄湪鐩墠瀹炵幇鏂瑰紡涓嬭繖鏄竴涓�屼笟鍔″垎鏀�嶈�岄潪寮傚父銆�
				return this; // TODO 鎴栬瀵艰嚧澶氬寮曠敤鍚屼竴 Sequence锛岃繖浼氬嚭浜嬪悧锛�
			}
		}

		// 2 鏋勯�犺鎻掑叆鐨� statement
		List<Sequence.RelativeNegativeIndex> indexListOfNewStatment = new ArrayList<>(1);
		for (Variable v : inputVariables) {
			indexListOfNewStatment.add(getRelativeIndexForVariable(index, v));
		}
		Statement newStatement = new Statement(operation, indexListOfNewStatment);

		HashMap<Statement, Integer> curr_statement_in_last_sequence_index_map_inner = new HashMap<>();

		// 3 鏋勯�犳柊 sequence銆備緷娆℃坊鍔狅細鎻掑叆鐐逛箣鍓嶇殑璇彞锛屾彃鍏ョ殑璇彞锛屾彃鍏ョ偣涔嬪悗鐨勮鍙�
		SimpleArrayList<Statement> newStatements = new SimpleArrayList<>();
		for (int i = 0; i < index; i++) {
			Statement stmt = this.getStatement(i);
			curr_statement_in_last_sequence_index_map_inner.put(stmt, i);
			newStatements.add(stmt);
		}
		newStatements.add(newStatement);
		// 3.1 鏋勯�犳椂瑕佷慨鏀硅鎺ㄥ悗鐨� statements 鐨� RelativeNegativeIndex
		/*
		 * 杩欐槸涓�涓� RelativeNegativeIndex 璋冩暣绀轰緥銆� 鎴戜滑缁欏嚭姣忎釜璇彞鐨� 搴忓彿 鍜�
		 * List<RelativeNegativeIndex>
		 *
		 * 鎻掍箣鍓嶏細 0 {} 1 {-1} 2 {-1} 3 {-3,-1}
		 *
		 * insert(2,...) 涔嬪悗锛� 0 {} 1 {-1} 2 {...} // 鏂版彃鍏ョ殑 statement 3 {-2} // -1
		 * 鍙楀奖鍝嶅彉鎴� -2 4 {-4,-1} // -3 鍙楀奖鍝嶅彉鎴� -4锛�-1 涓嶅彈褰卞搷
		 */
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
			newStatements.add(new Statement(stmt.getOperation(), newInputs)); // 鏃ф搷浣滐紝鏂板弬鏁帮紙鍙湁 RNI 鏄柊鐨�= =銆侊級
		}

		return new TraceableSequence(newStatements, curr_statement_in_last_sequence_index_map_inner, this); // 鐢ㄨ繖涓瀯閫犲嚱鏁帮紝璁�
																											// hashcode
																											// 鍜�
																											// netsize
																											// 琚噸鏂拌绠�
	}

	/**
	 * @param index
	 * @param operation
	 * @return
	 */
	public final TraceableSequence insert(int index, TypedOperation operation) {
		return insert(index, operation, new ArrayList<Variable>());
	}

	/**
	 * 鎶� this Sequence 鐨勭 stmtIndex 鍙ョ殑绗� varIndex 涓緭鍏ュ彉閲忔敼涓�
	 * targetVariable銆俰ndex 鍧囦粠 0 璧疯鏁般��
	 *
	 * @param stmtIndex
	 * @param varIndex
	 * @param targetVariable
	 * @return
	 */
	public final TraceableSequence modifyReference(int stmtIndex, int varIndex, Variable targetVariable) {
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

		// 杩囨护銆俆ODO 浠庢簮澶村杩囨护
		if (!opOfTheModified.getInputTypes().get(varIndex).isAssignableFrom(targetVarType)) {
			return this; // TODO 鎴栬瀵艰嚧澶氬寮曠敤鍚屼竴 Sequence锛岃繖浼氬嚭浜嬪悧锛� fail loudly!
		}
		// 纭繚鍙紩鐢� index 涔嬪墠鐨勫彉閲忋�俆ODO 浠庢簮澶村杩囨护
		if (targetVariable.index >= stmtIndex) {
			// 涓嶈兘鎶� RE銆傚洜涓哄湪鐩墠瀹炵幇鏂瑰紡涓嬭繖鏄竴涓�屼笟鍔″垎鏀�嶈�岄潪寮傚父銆�
			return this; // TODO 鎴栬瀵艰嚧澶氬寮曠敤鍚屼竴 Sequence锛岃繖浼氬嚭浜嬪悧锛�
		}

		// 鏋勯��
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
						newInputs.add(new Sequence.RelativeNegativeIndex(rni.index)); // wanna
						// RelativeNegativeIndex#clone()
						// TODO
					} else {
						newInputs.add(new Sequence.RelativeNegativeIndex(targetVariable.index - stmtIndex));
					}
				}
				// 鏃ф搷浣滐紝鏂板弬鏁帮紙鍙湁涓�涓� RNI 鏄柊鐨�= =銆侊級
				Statement modifiedStatement = new Statement(statementToModify.getOperation(), newInputs);
				newStatements.add(modifiedStatement);
			}
		}
		return new TraceableSequence(newStatements, curr_statement_in_last_sequence_index_map_inner, this); // 鐢ㄨ繖涓瀯閫犲嚱鏁帮紝璁�
																											// hashcode
																											// 鍜�
																											// netsize
																											// 琚噸鏂拌绠�
	}

	/**
	 * @param stmtIndex
	 * @param varIndex
	 * @return
	 */
	public final TraceableSequence modifyBoolean(int stmtIndex, int varIndex) {
		Statement stmtToModify = this.getStatement(stmtIndex); // 鏄惁瑕佹妸瓒婄晫 Exception 鍖呮垚鏇翠笟鍔＄殑 Exception锛�
		// Type typeOfVarToModify = stmtToModify.getInputTypes().get(varIndex); //
		// 鏈夊己杞椂锛岄瀷瀛愬舰鐘讹紒=鑴氱殑褰㈢姸
		// 鎵惧埌 浜х敓鍑鸿鏀瑰彉閲� 鐨勯偅涓�鍙�
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
			/*
			 * (浠ュ皬 boolean 涓轰緥銆傚ぇ Boolean 涓�鏍枫��)
			 * 
			 * 鍙樺紓鍓嶏細 boolean a = ... ... o.f(a);
			 * 
			 * 1. 鎻掑叆 boolean a1 = DateRuntime.not(a) 鍦� a 澹版槑鍚� boolean a = ... boolean a1
			 * = DateRuntime.not(a); ... o.f(a);
			 * 
			 * 2. 鎶� o.f(a) 缁� modifyReference 鎴� o.f(a1) boolean a = ... boolean a1 =
			 * DateRuntime.not(a); ... o.f(a1);
			 */
			TypedOperation methodCallNot;
			if (isBoxed) {
				methodCallNot = TypedOperation.forMethod(DateRuntime.class.getMethod("not", Boolean.class));
			} else {
				methodCallNot = TypedOperation.forMethod(DateRuntime.class.getMethod("not", boolean.class));
			}

			TraceableSequence insertedFlip = this.insert(varSourceStmtIndex + 1, methodCallNot,
					Arrays.asList(new Variable(this, varSourceStmtIndex)));

			// 瑕佷慨鏀圭殑閭ｅ彞宸茬粡琚尋涓嬫潵 1 琛屼簡
			return insertedFlip.modifyReference(stmtIndex + 1, varIndex,
					new Variable(insertedFlip, varSourceStmtIndex + 1));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
			// TODO 纭鍋滀笅
		}
	}

	/**
	 * 璁╄皟鐢ㄨ�呮柟渚匡細deltaValue 鍙互鏄� Float 鎴� Double锛堜互鍙� float 鎴� double锛屾劅璋㈣嚜鍔�
	 * boxing锛夈��
	 *
	 * <p>
	 * 鐜板湪浠嶇劧闇�瑕佸湪浼犲弬鏃跺尯鍒� float 鍜� double锛屽 1.2f 鍜� 1.2 TODO 鎶瑰钩gap
	 *
	 * <p>
	 * 琚敼鐨勫彉閲忥紝绫诲瀷淇棫濡傛棫
	 *
	 * @param stmtIndex
	 * @param varIndex
	 * @return
	 */
	public final TraceableSequence modifyReal(int stmtIndex, int varIndex, Object deltaValue) {
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

		// TODO 鎬庝箞妫�鏌� delta 濂斤紵

		try {
			/*
			 * 鍘熸潵鏄細 float a = ... ... o.f(a);
			 * 
			 * 1. 鎻掑叆 float delta = 2.0 杩欑 2. 鎻掑叆 float a1 = DateRuntime.add(a, delta) 杩欑
			 * 3. 鎶� o.f(a) 缁� modifyReference 鎴� o.f(a1)
			 * 
			 * float a = ... float delta = 2.0 float a1 = DateRuntime.add(a, delta) ...
			 * o.f(a1);
			 */

			TypedOperation deltaInit = TypedOperation.createPrimitiveInitialization(Type.forClass(classOfVarToModify),
					deltaValue
			// ensurePrimitive.cast(deltaValue)
			); // cast 鐩殑锛氳浼犲叆鐨� delta 寮鸿浆鎴愯鏀瑰彉閲忕殑绫诲瀷锛屼互渚胯皟鐢ㄥ悎閫傜殑 add 鍑芥暟銆� TODO Float
				// 涓嶈兘寮鸿浆 Double 鍙兘鏋勯�犲嚱鏁扳�︹��
			TraceableSequence insertedDelta = this.insert(varSourceStmtIndex + 1, deltaInit);

			TypedOperation addMethodCall = TypedOperation
					.forMethod(DateRuntime.class.getMethod("add", classOfVarToModify, Object.class));
			TraceableSequence insertedAdd = insertedDelta.insert(varSourceStmtIndex + 2, addMethodCall,
					Arrays.asList(new Variable(insertedDelta, varSourceStmtIndex),
							new Variable(insertedDelta, varSourceStmtIndex + 1)));

			// 瑕佷慨鏀圭殑閭ｅ彞宸茬粡琚尋涓嬫潵涓よ浜嗏�︹��
			return insertedAdd.modifyReference(stmtIndex + 2, varIndex,
					new Variable(insertedAdd, varSourceStmtIndex + 2));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
			// TODO 纭鑳� fail
		}
	}

	/**
	 * 鏁堟灉瀹炴祴锛�
	 *
	 * <p>
	 * 缁檌nt integer long浼� 42
	 *
	 * <p>
	 * 缁橪ong浼� 42L
	 *
	 * <p>
	 * 缂洪櫡锛氱幇鍦ㄨ閽堝涓嶅悓绫诲瀷鐨勮鏀瑰彉閲忥紝浼犱笉鍚岀被鍨嬬殑 deltaValue銆傚叿浣撴槸
	 * 1,1L,(short)1,(byte)1,(char)1 浜旂銆�
	 *
	 * <p>
	 * 涔熸槸鏈夐亾鐞嗙殑鍚э細涓嶅悓鐨勬暟鍊肩被鍨嬫湁涓嶅悓鐨勮寖鍥达紝褰撶劧鏈変笉鍚岀殑鍙敤 delta 鑼冨洿銆傝兘鎸夌被鍨嬪幓鍖哄垎 delta
	 * 鑼冨洿锛屼篃灏辩煡閬撲簡绫诲瀷锛屼篃灏变笉濡ㄤ紶涓嶅悓绫诲瀷鐨勩�備笉杩囷紝杩欎篃娌℃硶闈欐�佸畬鍏ㄥ垎鏋愬嚭銆係ad
	 *
	 * @param stmtIndex
	 * @param varIndex
	 * @param deltaValue
	 * @return
	 */
	public final TraceableSequence modifyIntegral(int stmtIndex, int varIndex, Object deltaValue) {
		Statement stmtToModify = this.getStatement(stmtIndex); // 鏄惁瑕佹妸瓒婄晫 Exception 鍖呮垚鏇翠笟鍔＄殑 Exception锛�
		// Type typeOfVarToModify = stmtToModify.getInputTypes().get(varIndex); //
		// 鏈夊己杞椂锛岄瀷瀛愬舰鐘� != 鑴氱殑褰㈢姸
		// 鎵惧埌 浜х敓鍑鸿鏀瑰彉閲� 鐨勯偅涓�鍙�
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

		// TODO 鎬庝箞妫�鏌� delta 濂斤紵

		try {
			/*
			 * 鍘熸潵鏄細 float a = ... ... o.f(a);
			 * 
			 * 1. 鎻掑叆 float delta = 2.0 杩欑 2. 鎻掑叆 float a1 = DateRuntime.add(a, delta) 杩欑
			 * 3. 鎶� o.f(a) 缁� modifyReference 鎴� o.f(a1)
			 * 
			 * float a = ... float delta = 2.0 float a1 = DateRuntime.add(a, delta) ...
			 * o.f(a1);
			 */

			TypedOperation deltaInit = TypedOperation.createPrimitiveInitialization(Type.forClass(classOfVarToModify),
					deltaValue
			// ensurePrimitive.cast(deltaValue)
			); // cast 鐩殑锛氳浼犲叆鐨� delta 寮鸿浆鎴愯鏀瑰彉閲忕殑绫诲瀷锛屼互渚胯皟鐢ㄥ悎閫傜殑 add 鍑芥暟銆� TODO Float
				// 涓嶈兘寮鸿浆 Double 鍙兘鏋勯�犲嚱鏁扳�︹��
			TraceableSequence insertedDelta = this.insert(varSourceStmtIndex + 1, deltaInit);

			TypedOperation addMethodCall = TypedOperation
					.forMethod(DateRuntime.class.getMethod("add", classOfVarToModify, Object.class));
			TraceableSequence insertedAdd = insertedDelta.insert(varSourceStmtIndex + 2, addMethodCall,
					Arrays.asList(new Variable(insertedDelta, varSourceStmtIndex),
							new Variable(insertedDelta, varSourceStmtIndex + 1)));

			// 瑕佷慨鏀圭殑閭ｅ彞宸茬粡琚尋涓嬫潵涓よ浜嗏�︹��
			return insertedAdd.modifyReference(stmtIndex + 2, varIndex,
					new Variable(insertedAdd, varSourceStmtIndex + 2));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
			// TODO 纭鑳� fail
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

	public final TraceableSequence modifyStringInsert(int stmtIndex, int varIndex, int charIndex) {
		/*
		 * String s = ...; ... o.f(s);
		 * 
		 * 鈫�
		 * 
		 * String s = ...; int charIndex=...; String s1 =
		 * DateRuntime.insert(s,charIndex); ... o.f(s1);
		 * 
		 * 璺� modifyReal 鍜� modifyIntegral 宸笉澶�
		 */
		try {
			Statement stmtToModify = this.getStatement(stmtIndex);
			// 鎵惧埌 浜х敓鍑鸿鏀瑰彉閲� 鐨勯偅涓�鍙�
			int varSourceStmtIndex = stmtIndex + stmtToModify.getInputs().get(varIndex).index;
			Type typeOfVarToModify = this.getStatement(varSourceStmtIndex).getOutputType();

			if (!typeOfVarToModify.getRuntimeClass().equals(String.class)) {
				throw new IllegalArgumentException(String.format(
						"modifyStringInsert-ing a non-Sting variable. statement index: %d, input index: %d", stmtIndex,
						varIndex));
			}

			TypedOperation charIndexInit = TypedOperation
					.createPrimitiveInitialization(PrimitiveType.forClass(int.class), charIndex);
			TraceableSequence insertedDelta = this.insert(varSourceStmtIndex + 1, charIndexInit);

			TypedOperation insertMethodCall = TypedOperation
					.forMethod(DateRuntime.class.getMethod("insert", String.class, int.class));
			TraceableSequence insertedInsert = insertedDelta.insert(varSourceStmtIndex + 2, insertMethodCall,
					Arrays.asList(new Variable(insertedDelta, varSourceStmtIndex),
							new Variable(insertedDelta, varSourceStmtIndex + 1)));

			// 瑕佷慨鏀圭殑閭ｅ彞宸茬粡琚尋涓嬫潵涓よ浜嗏�︹��
			return insertedInsert.modifyReference(stmtIndex + 2, varIndex,
					new Variable(insertedInsert, varSourceStmtIndex + 2));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
			// TODO 纭鑳� fail
		}
	}

	public final TraceableSequence modifyStringRemove(int stmtIndex, int varIndex, int charIndex) {
		/*
		 * String s = ...; ... o.f(s);
		 * 
		 * 鈫�
		 * 
		 * String s = ...; int charIndex=...; String s1 =
		 * DateRuntime.remove(s,charIndex); ... o.f(s1);
		 * 
		 * 璺� modifyReal 鍜� modifyIntegral 宸笉澶�
		 */
		try {
			Statement stmtToModify = this.getStatement(stmtIndex);
			// 鎵惧埌 浜х敓鍑鸿鏀瑰彉閲� 鐨勯偅涓�鍙�
			int varSourceStmtIndex = stmtIndex + stmtToModify.getInputs().get(varIndex).index;
			Type typeOfVarToModify = this.getStatement(varSourceStmtIndex).getOutputType();

			if (!typeOfVarToModify.getRuntimeClass().equals(String.class)) {
				throw new IllegalArgumentException(String.format(
						"modifyStringRemove-ing a non-Sting variable. statement index: %d, input index: %d", stmtIndex,
						varIndex));
			}

			TypedOperation charIndexInit = TypedOperation
					.createPrimitiveInitialization(PrimitiveType.forClass(int.class), charIndex);
			TraceableSequence insertedDelta = this.insert(varSourceStmtIndex + 1, charIndexInit);

			TypedOperation removeMethodCall = TypedOperation
					.forMethod(DateRuntime.class.getMethod("remove", String.class, int.class));
			TraceableSequence insertedInsert = insertedDelta.insert(varSourceStmtIndex + 2, removeMethodCall,
					Arrays.asList(new Variable(insertedDelta, varSourceStmtIndex),
							new Variable(insertedDelta, varSourceStmtIndex + 1)));

			// 瑕佷慨鏀圭殑閭ｅ彞宸茬粡琚尋涓嬫潵涓よ浜嗏�︹��
			return insertedInsert.modifyReference(stmtIndex + 2, varIndex,
					new Variable(insertedInsert, varSourceStmtIndex + 2));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
			// TODO 纭鑳� fail
		}
	}

	public final TraceableSequence modifyStringModify(int stmtIndex, int varIndex, int charIndex, int deltaValue) {
		/*
		 * String s = ...; ... o.f(s);
		 * 
		 * 鈫�
		 * 
		 * String s = ...; int charIndex=...; int deltaValue=...; String s1 =
		 * DateRuntime.modify(s,charIndex,deltaValue); ... o.f(s1);
		 * 
		 * 姣� modifyReal 鍜� modifyIntegral 瑕佸鎸や笅鏉ヤ竴琛岋紒锛侊紒
		 */
		try {
			Statement stmtToModify = this.getStatement(stmtIndex);
			// 鎵惧埌 浜х敓鍑鸿鏀瑰彉閲� 鐨勯偅涓�鍙�
			int varSourceStmtIndex = stmtIndex + stmtToModify.getInputs().get(varIndex).index;
			Type typeOfVarToModify = this.getStatement(varSourceStmtIndex).getOutputType();

			if (!typeOfVarToModify.getRuntimeClass().equals(String.class)) {
				throw new IllegalArgumentException(String.format(
						"modifyStringModify-ing a non-Sting variable. statement index: %d, input index: %d", stmtIndex,
						varIndex));
			}

			TypedOperation charIndexInit = TypedOperation
					.createPrimitiveInitialization(PrimitiveType.forClass(int.class), charIndex);
			TraceableSequence insertedDelta = this.insert(varSourceStmtIndex + 1, charIndexInit);

			TypedOperation deltaValueInit = TypedOperation
					.createPrimitiveInitialization(PrimitiveType.forClass(int.class), deltaValue);
			TraceableSequence insertedDelta2 = insertedDelta.insert(varSourceStmtIndex + 2, deltaValueInit);

			TypedOperation modifyMethodCall = TypedOperation
					.forMethod(DateRuntime.class.getMethod("modify", String.class, int.class, int.class));
			TraceableSequence insertedModify = insertedDelta2.insert(varSourceStmtIndex + 3, modifyMethodCall,
					Arrays.asList(new Variable(insertedDelta2, varSourceStmtIndex),
							new Variable(insertedDelta2, varSourceStmtIndex + 1),
							new Variable(insertedDelta2, varSourceStmtIndex + 2)));

			// 瑕佷慨鏀圭殑閭ｅ彞宸茬粡琚尋涓嬫潵 3 琛屼簡鈥︹��
			return insertedModify.modifyReference(stmtIndex + 3, varIndex,
					new Variable(insertedModify, varSourceStmtIndex + 3));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
			// TODO 纭鑳� fail
		}
	}

	/**
	 * TODO 搴熼櫎 鎶� this Sequence 鐨勭 stmtIndex 鍙ョ殑绗� varIndex 涓緭鍏ュ彉閲�"鐨勫��"鏍规嵁
	 * deltaValue 鍋氫慨鏀癸紝鏀规硶瑙� deltaValue 鍙傛暟璇存槑銆俰ndex 鍧囦粠 0 璧疯鏁般��
	 *
	 * <p>
	 * 锛堜骇鐢熻嫢骞蹭复鏃� Sequence銆俆ODO锛熸�ц兘浼樺寲鐐癸級
	 *
	 * <p>
	 * ModifyPrimitive 娈嬬暀鏂囨。锛� // // TODO 纭甯告暟涓嶈鐩存帴鐢ㄤ簬 input //
	 * NO锛佹湁浜х敓鐩存帴浼犲瓧闈㈤噺鐨勭敤渚嬶紙缁忚繃cast锛� // 鈥斺�� 鎬�鐤戞槸缁忚繃 shorten 鐨勶紵鍘熸潵鏄�
	 * NonreceiverTerm + UncheckedCast 璇彞锛熴�� // 鐩存帴浼犲瓧闈㈤噺杩欑鏄� ShortForm锛佸ぇ鍠�
	 *
	 * @param stmtIndex
	 * @param varIndex
	 * @param deltaValue
	 *            鍙樻洿閲忋�傚浜庢暟鍊硷紝鎬绘槸浼犳潵 Double锛屽彉杩欎釜閲忋�傚浜� Boolean锛屼笉绠′紶鏉ヤ粈涔堬紝鎬� not
	 * @return
	 */
	@Deprecated
	public final TraceableSequence modifyPrimitive(int stmtIndex, int varIndex, Object deltaValue) {
		return null;
	}

	/**
	 * 鍒犻櫎 this Sequence 鐨勭 stmtIndex 鍙ャ�傜骇鑱斿垹闄や緷璧栨湰鍙ョ殑璇彞锛涜皟鏁村墿涓嬭鍙ョ殑杈撳叆鐨�
	 * RelativeNegativeIndex銆�
	 *
	 * <pre>
	 * 杩欐槸涓�涓� RelativeNegativeIndex 璋冩暣绀轰緥銆�
	 * 鎴戜滑缁欏嚭姣忎釜璇彞鐨� 搴忓彿 鍜� List&lt;RelativeNegativeIndex&gt;
	 *
	 * 鍒犱箣鍓嶏細
	 * 0 {}
	 * 1 {-1}
	 * 2 {...} // 瑕佸垹闄ょ殑
	 * 3 {-1,-2} // 鍥� -1 绾ц仈鍒犻櫎
	 * 4 {-4,-3} // 涓嶈绾ц仈鍒犻櫎
	 * 5 {-2} // 3 琚垹闄ゅ悗锛岀骇鑱斿垹闄�
	 *
	 * remove(2) 涔嬪悗锛�
	 * 0 {}
	 * 1 {-1}
	 * 2 {-2,-1} // 涓嶈绾ц仈鍒犻櫎锛屼絾瑕佹敼 RNI锛�
	 * </pre>
	 *
	 * <p>
	 * 瀹炵幇锛�
	 *
	 * <p>
	 * 鏋勯�犱互 Statement 涓虹偣锛屼互銆岃渚濊禆銆嶅叧绯讳负杈圭殑鏈夊悜鏃犵幆绠�鍗曞浘銆�
	 *
	 * <p>
	 * 浠� stmtIndex 涓鸿捣鐐� traverse 璇ュ浘锛堝氨 dfs 鍚э級锛岃涓嬭兘璁块棶鍒扮殑
	 * Statements锛屼綔涓烘灙姣欏悕鍗曘��
	 *
	 * <p>
	 * 鎸夋灙姣欏悕鍗曡皟鏁村墿涓� Statements 鐨� input锛堢殑 RelativeNegativeIndex锛夈�傛瀯閫犳柊
	 * Sequence銆�
	 *
	 * @param stmtIndex
	 * @return
	 */
	public final TraceableSequence remove(int stmtIndex) {
		// 鍙傛暟妫�鏌�, easy peasy
		if (stmtIndex < 0 || this.size() <= stmtIndex) {
			String msg = "this.size():" + this.size() + " but stmtIndex:" + stmtIndex
					+ ". Expected 0 <= stmtIndex < this.size().";
			throw new IllegalArgumentException(msg);
		}

		// 琚緷璧栧叧绯诲浘 鐨� 閭绘帴琛�
		ArrayList<ArrayList<Integer>> dependedRelationAdj = calculateDependedRelationAdjacencyList();
		// 涓嶆兂娉勯湶鍒� class member 閭ｉ噷鈥︹�� 灏辩箒鐞愪紶鍙傚惂銆傚氨浠ユ敼鍙備唬杩斿洖鍚с�俀AQ
		int stmtNum = dependedRelationAdj.size();
		boolean[] visited = new boolean[stmtNum];
		Arrays.fill(visited, false);
		boolean[] toRemove = new boolean[stmtNum];
		Arrays.fill(toRemove, false);
		dfsForRemoval(dependedRelationAdj, stmtIndex, visited, toRemove);

		// 鏍规嵁鏋瘷鍚嶅崟锛坱oRemove锛夋瀯閫犳柊 TraceableSequence
		// TODO 鐢� HashMap锛孲tatement#equals 鏄惁瓒冲涓嶉噸涓嶆紡鍒ょ瓑锛�#hashcode 鏁堢巼锛堝皬浜嬶級
		HashMap<Statement, Integer> curr_statement_in_last_sequence_index_map_inner = new HashMap<>();
		SimpleArrayList<Statement> newStatements = new SimpleArrayList<>();
		// 鐢ㄤ簬璁＄畻鍖洪棿鍐呰鍒犺鍙ユ暟锛岀户鑰岀敤浜庤绠楁柊鐨勮 RelativeNegativeIndex
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
		return new TraceableSequence(newStatements, curr_statement_in_last_sequence_index_map_inner, this);
	}

	/**
	 * Calculate "depended" relation adjacency list for this Sequence.
	 *
	 * <p>
	 * 澶嶆潅搴︾◢楂橈紵绾挎�ф椂闂村幓閲�
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
	 * 娣卞害浼樺厛锛堝埆鐨勬柟寮忎篃琛岋級閬嶅巻閭绘帴琛� dependedRelationAdj 浠ｈ〃鐨勬湁鍚戝浘銆傚璁块棶鍒扮殑缁撶偣 i锛岃缃�
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

	@Override
	public int compareTo(TraceableSequence o) {
		return toLongFormString().compareTo(o.toLongFormString());
	}
}
