package randoop.generation.date.execution;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TracePrintController {
	
	public static void OpenPrintFlag() {
		try {
			Class<?> c = Class.forName("cn.yyx.research.trace_recorder.TraceRecorder");
			Field f = c.getDeclaredField("now_record");
			f.set(null, Boolean.TRUE);
			/**
			 * print operation start
			 */
			Method m = c.getMethod("PrintOperationStartUp");
			m.invoke(null);
//			Boolean f_v = (Boolean)f.get(null);
//			System.out.println("open setted f_v:" + f_v);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void ClosePrintFlag() {
		try {
			Class<?> c = Class.forName("cn.yyx.research.trace_recorder.TraceRecorder");
			Field f = c.getDeclaredField("now_record");
			f.set(null, Boolean.FALSE);
//			Boolean f_v = (Boolean)f.get(null);
//			System.out.println("close setted f_v:" + f_v);
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
