package randoop.generation.date.tensorflow;

public class JavaPythonRemoteInvoke {
	
	String result = null;
	
	public JavaPythonRemoteInvoke() {
	}
	
	public void SetResult(String result) {
		this.result = result;
	}
	
	@Override
	public String toString() {
		return result.toString();
	}
	
	public void Wait() {
		while (result == null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
