package randoop.generation.date.execution;

import java.io.File;
import java.io.IOException;
import java.util.*;
import randoop.generation.date.util.FileUtil;

public class ProcessExecutor {

  public static final String test_case_name = "Test";
  public static final String over_all_working_directory =
      System.getProperty("user.home") + "/" + "TestCaseExecutionDirectory";
  public static final String instrument_agent =
      System.getProperty("user.home") + "/" + "TestGen_Library" + "/" + "test_agent.jar";

  static {
    File dir = new File(over_all_working_directory);
    if (dir.exists()) {
      FileUtil.DeleteFile(dir);
    }
    dir.mkdirs();
    System.err.println("TestCaseExecutionDirectory created!");
  }

  List<String> test_cases = null;
  Map<String, Process> thread_pool = new TreeMap<String, Process>();

  public ProcessExecutor(List<String> test_cases) {
    this.test_cases = test_cases;
  }

  public void ExecuteTestCases() {
    long curr_millis = System.currentTimeMillis();
    String id_prefix = curr_millis + "#" + this.hashCode() + "#";
    int tc_index = 0;
    Iterator<String> tc_itr = test_cases.iterator();
    List<Thread> all_threads = new LinkedList<Thread>();
    while (tc_itr.hasNext()) {
      tc_index++;
      final String test_case = tc_itr.next();
      final String test_case_id = id_prefix + tc_index;
      Thread t =
          new Thread(
              new Runnable() {
                @Override
                public void run() {
                  ExecuteOneTestCase(test_case, test_case_id);
                }
              });
      t.start();
      all_threads.add(t);
    }
    Iterator<Thread> aitr = all_threads.iterator();
    while (aitr.hasNext()) {
      Thread t = aitr.next();
      try {
        t.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private void ExecuteOneTestCase(String test_case, String test_case_id) {
    String directory = over_all_working_directory + "/" + test_case_id;
    File dir = FileUtil.EnsureDirectoryExist(directory);
    File test_case_file = new File(directory + "/" + test_case_name + ".java");
    String test_case_string =
        "public class Test { public static void main(String[] args) {" + test_case + "} }";
    FileUtil.WriteToFile(test_case_file, test_case_string);
    String[] javac_cmds =
        new String[] {
          "javac", "-cp", System.getProperty("java.class.path"), test_case_name + ".java"
        };
    ExecuteOneCmdForTestCase(javac_cmds, dir);
    String[] java_agent_cmds =
        new String[] {
          "java",
          "-Xbootclasspath/p:" + instrument_agent,
          "-javaagent:" + instrument_agent + "=" + directory,
          "-cp",
          "." + System.getProperty("path.separator") + System.getProperty("java.class.path"),
          test_case_name
        };
    ExecuteOneCmdForTestCase(java_agent_cmds, dir);
  }

  private void ExecuteOneCmdForTestCase(String[] cmds, File directory) {
    ProcessBuilder pb = new ProcessBuilder(cmds);
    pb.directory(directory);
    pb.inheritIO();
    try {
      Process process = pb.start();
      process.waitFor();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
