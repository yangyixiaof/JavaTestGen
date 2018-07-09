package randoop.generation.date.execution;

import java.lang.reflect.Field;

public class TracePrintController {
	
	public static void OpenPrintFlag() {
		try {
			Class<?> c = Class.forName("cn.yyx.research.trace_recorder.TraceRecorder");
			Field f = c.getDeclaredField("now_record");
//			Boolean f_v1 = (Boolean)f.get(null);
			f.set(null, Boolean.TRUE);
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static void ClosePrintFlag() {
		try {
			Class<?> c = Class.forName("cn.yyx.research.trace_recorder.TraceRecorder");
			Field f = c.getDeclaredField("now_record");
//			Boolean f_v2 = (Boolean)f.get(null);
			f.set(null, Boolean.FALSE);
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static String GetPrintedTrace() {
		String trace = "";
		try {
			Class<?> c = Class.forName("cn.yyx.research.trace_recorder.TraceRecorder");
			Field f = c.getDeclaredField("buffer");
			StringBuffer buffer = (StringBuffer)f.get(null);
			trace = buffer.toString();
			buffer.delete(0, buffer.length());
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return trace;
	}
	
}
