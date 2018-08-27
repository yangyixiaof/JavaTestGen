package randoop.generation.date.influence;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TraceReader {

//	private static final String default_trace_file = System.getProperty("user.home") + "/" + "trace.txt";
//	String specific_file = null;
	
//	static int enter = 0, exit = 0, branch_operand = 0; // debug to check whether enter and exit are matched.
//	static int currentLineFrom1 = 0; // start from 1
//	static String lastPop = null;

	/**
	 * Main entry of this class.
	 *
	 * @param specific_file
	 * @return
	 */
	public static TraceInfo ReadFromTraceFile(String specific_file) {
//		Stack<String> runtime_stack = new Stack<>();
//		Map<String, LinkedList<ValuesOfBranch>> branch_signature_to_info = new TreeMap<>();
		File file = new File(specific_file);
		String str = "";
		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();
			str = new String(data, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return HandleOneTrace(str);
	}
	
	public static TraceInfo HandleOneTrace(String str) {
		String[] lines = str.split("\\r?\\n");
		TraceInfo ti = new TraceInfo();
		try {
			for (String one_line : lines) {
//				currentLineFrom1++;
				one_line = one_line.trim();
				if (!one_line.equals("")) {
//					if (one_line.startsWith("@Method-Enter:")) {
//						String[] parts = one_line.split(":");
//						enter++;
//						ProcessMethodEnter(parts[1], runtime_stack);
//					}
//					if (one_line.startsWith("@Method-Exit:")) {
//						String[] parts = one_line.split(":");
//						exit++;
//						ProcessMethodExit(parts[1], runtime_stack);
//					}
					if (one_line.startsWith("@Branch-Operand")) {
						String[] parts = one_line.split("#");
//						branch_operand++;
						try {
							String operandSig = parts[1] + "#" + parts[2] + "#" + parts[3];
							int relativeOffset = Integer.parseInt(parts[4]);
							String cmpOperator = parts[5];
							double op1 = Double.parseDouble(parts[6]);
							double op2 = Double.parseDouble(parts[7]);
//							String enclosingMethod = runtime_stack.peek();
							ProcessBranchOperand(operandSig, relativeOffset, cmpOperator, op1, op2, ti);
						} catch (Exception e) {
							// System.out.println("lastPop: " + lastPop);
							// System.out.println("currentLineFrom1 " + currentLineFrom1);
							e.printStackTrace();
							System.exit(1);
						}
					}
//					if (one_line.startsWith("@Var")) {
//						String[] parts = one_line.split("#");
//						String var_type = parts[1];
//						String var_value = parts[2];
//						Class<?> var_class = Class.forName(var_type);
//						ti.AddOneReturnOfStatement(new StatementReturn(var_class, var_value));
//					}
					if (one_line.startsWith("@Object-Address")) {
						String[] parts = one_line.split("#");
//						branch_operand++;
						try {
							String operandSig = parts[1] + "#" + parts[2] + "#" + parts[3];
							int relativeOffset = Integer.parseInt(parts[4]);
							int object_address = Integer.parseInt(parts[5]);
//							String enclosingMethod = runtime_stack.peek();
							ProcessObjectAddress(operandSig, relativeOffset, object_address, ti);
						} catch (Exception e) {
							// System.out.println("lastPop: " + lastPop);
							// System.out.println("currentLineFrom1 " + currentLineFrom1);
							e.printStackTrace();
							System.exit(1);
						}
					}
				}
			}
			
			// TraceSerializer.SerializeByIdentification(current_sequence_identifier,
			// branch_signature_to_info);
			
			// @SuppressWarnings("unchecked")
			// Map<String, ValuesOfBranch> previous_branch_signature =
			// (Map<String, ValuesOfBranch>)
			// TraceSerializer.DeserializeByIdentification(previous_sequence_identifier);
			//
			// BuildGuidedModel(previous_branch_signature, branch_signature_to_info);

		} catch (Exception e) {
			e.printStackTrace();
		}
		ti.IdentifyStatesOfBranches();
		return ti;
	}

//	private void ProcessMethodEnter(String method_name, Stack<String> runtime_stack) {
//		runtime_stack.push(method_name);
//	}
//	
//	private void ProcessMethodExit(String method_name, Stack<String> runtime_stack) {
//		String mname = runtime_stack.pop();
//		// lastPop = mname;
//		if (!mname.equals(method_name)) {
//			System.err.println("very strange! stack not valid! Should be the same:");
//			System.err.println(mname);
//			System.err.println(method_name);
//			System.err.println("currentLineFrom1: " + currentLineFrom1);
//			System.exit(1);
//		}
//	}
	
	private static void ProcessBranchOperand(String operand_sig, int relative_offset, String cmp_optr,
			double branch_value1, double branch_value2, TraceInfo ti) {
		ValuesOfBranch vob = new ValuesOfBranch(operand_sig, relative_offset, cmp_optr, branch_value1,
				branch_value2);
		String catted = operand_sig + "#" + relative_offset;
		ti.AddOneValueOfBranch(catted, vob);
	}

	private static void ProcessObjectAddress(String operand_sig, int relative_offset, int object_address, TraceInfo ti) {
		String catted = operand_sig + "#" + relative_offset;
		ti.AddOneObjectAddress(catted, object_address);
	}
	
}
