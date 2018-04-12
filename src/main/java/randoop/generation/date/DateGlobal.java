package randoop.generation.date;

/**
 * 从命令行参数转换到 GenInputsAbstract 里那些配置项，用到了作者热爱的 plume 库。懒得学，也不想动 GenInputsAbstract，就自己写一个记配置项的类。
 *
 * <p>TODO: 还是学学
 */
public class DateGlobal {

  /**
   * true: 用我们的 Generator；false: 用原来的 ForwardGenerator
   *
   * <p>设为 true 的方式：在命令行参数中出现 date-generator
   */
  public static boolean useDateGenerator = false;
}
