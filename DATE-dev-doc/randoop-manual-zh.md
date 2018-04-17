# Randoop Manual 理解和翻译

母语 = 烙印 = 不流失不蒸发。

初版来自 2018-01-02 访问 https://randoop.github.io/randoop/manual/index.html

​	大概容易从 Git log 看到文档修订历史，毕竟 Randoop 把文档也用 Git 管起来啦。

TODO 看下那哥们儿的 Randoop 记录

---

This is the manual for Randoop version 3.1.5, released April 28, 2017. The Randoop homepage is <https://randoop.github.io/randoop/>.

Contents:

- [Introduction](https://randoop.github.io/randoop/manual/index.html#Introduction)
- [Installing Randoop](https://randoop.github.io/randoop/manual/index.html#getting_randoop)
- [Running Randoop](https://randoop.github.io/randoop/manual/index.html#running_randoop)
- Generating tests
  - [Example: Generating tests for `java.util.Collections`](https://randoop.github.io/randoop/manual/index.html#example_collections)
  - [Specifying methods and constructors that may appear in a test](https://randoop.github.io/randoop/manual/index.html#specifying-methods)
  - [Classifying tests](https://randoop.github.io/randoop/manual/index.html#classifying_tests)
  - [Error-revealing tests](https://randoop.github.io/randoop/manual/index.html#error_revealing_tests)
  - Regression tests
    - [Regression test failures](https://randoop.github.io/randoop/manual/index.html#regression_test_failures)
- Customizing Randoop's behavior to your application
  - [Command-line options](https://randoop.github.io/randoop/manual/index.html#command-line-options)
  - Avoiding calls to specific methods
    - [Determining which calls to place in the map_calls file](https://randoop.github.io/randoop/manual/index.html#determining-calls-to-map)
  - [Specifying representation invariant methods (such as `checkRep`)](https://randoop.github.io/randoop/manual/index.html#checkrep)
  - [Instrumenting classes for filtering tests on exercised-classes](https://randoop.github.io/randoop/manual/index.html#exercised-filter)
  - [Specifying additional primitive values](https://randoop.github.io/randoop/manual/index.html#primitives)
  - [Speeding up test generation](https://randoop.github.io/randoop/manual/index.html#usethreads)
- Getting help
  - Troubleshooting
    - [Randoop does not run](https://randoop.github.io/randoop/manual/index.html#cannot-find-main)
    - Randoop 找不到被测类 [Randoop cannot find class-under-test](https://randoop.github.io/randoop/manual/index.html#no-class-found)
    - [Randoop does not create tests](https://randoop.github.io/randoop/manual/index.html#no-tests-created)
    - [Randoop does not create enough tests](https://randoop.github.io/randoop/manual/index.html#not-enough-tests-created)
    - [Randoop does not terminate](https://randoop.github.io/randoop/manual/index.html#nontermination)
    - [Randoop produces different output on different runs](https://randoop.github.io/randoop/manual/index.html#nondeterminism)
    - [Randoop stopped because of a flaky test](https://randoop.github.io/randoop/manual/index.html#flaky-tests)
    - [Tests behave differently in isolation or when reordered](https://randoop.github.io/randoop/manual/index.html#dependent-tests)
- [Credits](https://randoop.github.io/randoop/manual/index.html#credits)