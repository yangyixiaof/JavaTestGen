package randoop.generation.date.execution;

import java.io.File;
import java.io.IOException;
import java.util.*;
import randoop.generation.date.util.FileUtil;

public class ProcessExecutor {

  public static final String test_case_name = "YYX_RDQ_Test";
  public static final String over_all_working_directory =
      System.getProperty("user.home") + "/" + "TestCaseExecutionDirectory";
  public static final String instrument_agent =
      System.getProperty("user.home") + "/" + "TestGen_Library" + "/" + "test_agent.jar";

  public static final String jar_path_to_test =
      System.getProperty("user.home") + "/" + "TestGen_Library" + "/" + "ToTest.jar";
  public String importStr = "import xyz.sonion.cut.AES;";

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
    StringBuilder testCaseSb = new StringBuilder();
    testCaseSb.append(importStr);
    testCaseSb.append(System.lineSeparator());
    testCaseSb.append("public class " + test_case_name + " { public static void main(String[] args) {");
    testCaseSb.append(System.lineSeparator());
    testCaseSb.append(test_case);
    testCaseSb.append(System.lineSeparator());
    testCaseSb.append("} }");
    String test_case_string = testCaseSb.toString();
    FileUtil.WriteToFile(test_case_file, test_case_string);
    String[] javac_cmds =
        new String[] {
          "javac",
          "-cp",
          System.getProperty("java.class.path")
              + System.getProperty("path.separator")
              + jar_path_to_test,
          test_case_name + ".java"
        };
    System.out.println("!!!!!javac_cmds:");
    for (String s : javac_cmds) {
      System.out.print(s + " ");
    }
    System.out.println("");
    System.out.println("before 鎵ц javac_cmds 浜� " + dir);
    ExecuteOneCmdForTestCase(javac_cmds, dir);
    String[] java_agent_cmds =
        new String[] {
          "java",
          "-Xbootclasspath/p:" + instrument_agent,
          "-javaagent:" + instrument_agent + "=" + directory,
          "-cp",
          "."
              + System.getProperty("path.separator")
              + System.getProperty("java.class.path")
              + System.getProperty("path.separator")
              + jar_path_to_test,
          test_case_name
        };
    System.out.println("!!!!!java_agent_cmds:");
    for (String s : java_agent_cmds) {
      System.out.print(s + " ");
    }
    System.out.println("");
    System.out.println("before 鎵ц java_agent_cmds 浜� " + dir);
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
