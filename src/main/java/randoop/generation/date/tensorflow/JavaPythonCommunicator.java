package randoop.generation.date.tensorflow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.Gson;

public class JavaPythonCommunicator {
	
	ServerSocket server = null;
	
	List<JavaPythonRemoteInvoke> rpis = Collections.synchronizedList(new LinkedList<JavaPythonRemoteInvoke>());
	
	Thread listening_thread = null;
	
	boolean running = true;

	public JavaPythonCommunicator() {
		try {
			server = new ServerSocket(41500);
			System.out.println("Socket Server starts successfully!");
		} catch (Exception e) {
			System.out.println("Wrong in Socket Server:" + e);
		}
		listening_thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (running) {
					try {
						Socket socket = server.accept();
						BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						String json_data = "";
						String line = null;
						while ((line = bufferedReader.readLine()) != null) {
							json_data += line;
			            }
						JavaPythonRemoteInvoke rpi = rpis.remove(0);
						rpi.SetResult(json_data);
						rpi.notify();
						Thread.sleep(50);
					} catch (InterruptedException | IOException e) {
						e.printStackTrace();
					}
				}
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		listening_thread.start();
	}
	
	public void StopServerRunning() {
		running = false;
	}
	
	public JavaPythonRemoteInvoke RemoteCallPython(Map<String, Object> feed_dict, String remote_command) {
		Map<String, Object> final_feed = new TreeMap<>();
		final_feed.put(remote_command, final_feed);
		Gson gson = new Gson();
		String json_data = gson.toJson(final_feed);
		JavaPythonRemoteInvoke rpi = new JavaPythonRemoteInvoke();
		try {
			rpis.add(rpi);
			Socket socket = new Socket("127.0.0.1", 31500);
			OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
			writer.write(json_data);
			writer.close();
			socket.close();
			rpi.wait();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return rpi;
	}

}
