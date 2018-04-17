# Randoop Developer's Manual 理解和翻译

母语 = 烙印 = 不流失不蒸发。

初版来自 2018-01-02 访问 https://randoop.github.io/randoop/manual/dev.html

​	大概容易从 Git log 看到文档修订历史，毕竟 Randoop 把文档也用 Git 管起来啦。

------

- Getting started
  - 准备工作 [Prerequisites](https://randoop.github.io/randoop/manual/dev.html#prerequisites)
- Building Randoop
  - [The Gradle wrapper](https://randoop.github.io/randoop/manual/dev.html#gradlew)
  - [The Randoop build script](https://randoop.github.io/randoop/manual/dev.html#buildgradle)
- Running Randoop
  - [Randoop classpath](https://randoop.github.io/randoop/manual/dev.html#classpath)
- 测试 Randoop 本身 Testing Randoop
  - Adding tests
    - [Unit tests](https://randoop.github.io/randoop/manual/dev.html#unit-tests)
    - [System tests](https://randoop.github.io/randoop/manual/dev.html#system-tests)
- 开发 Randoop | Editing Randoop
  - 在 IDE 里开发 Randoop [Using an IDE](https://randoop.github.io/randoop/manual/dev.html#usinganide)
  - 把改动记到文档里！[Documenting changes](https://randoop.github.io/randoop/manual/dev.html#documentingchanges)
  - 代码格式化 [Formatting Randoop source code](https://randoop.github.io/randoop/manual/dev.html#codeformatting)
    - 操作：`alias goJF='./gradlew googleJavaFormat' ` `verGJF`
    - 我去安装了 google-java-format 的 IDEA 插件。启用后，cmd+shift+L 会按 google java 风格来格式化。但和 goJF 搞的有些区别。可用的操作：平时 cmd+shift+L，commit 前 goJF。
  - [Updating libraries](https://randoop.github.io/randoop/manual/dev.html#maintaininglibraries)
  - 修改 Randoop 手册 [Modifying the manual](https://randoop.github.io/randoop/manual/dev.html#modmanual)
  - 修改 Randoop 网站 [Modifying the website](https://randoop.github.io/randoop/manual/dev.html#github-site)
- [Releasing a new version of Randoop](https://randoop.github.io/randoop/manual/dev.html#making_new_dist)
- Randoop internals
  - [Unit test concepts](https://randoop.github.io/randoop/manual/dev.html#unit_tests)
  - 序列 Sequences
    - [Creating sequences](https://randoop.github.io/randoop/manual/dev.html#creating_sequence)
  - 可执行序列 Executable sequences
    - [Executing a sequence](https://randoop.github.io/randoop/manual/dev.html#executing-a-sequence)
    - [Miscellaneous notes](https://randoop.github.io/randoop/manual/dev.html#miscellaneous-notes)
  - 从文件读序列，向文件写序列 Writing/reading sequences to file
    - [Writing a sequence as a JUnit test](https://randoop.github.io/randoop/manual/dev.html#writing_sequence_as_junit)
  - Checks
    - [Statements vs. checks](https://randoop.github.io/randoop/manual/dev.html#distinction)
    - [Executing Checks](https://randoop.github.io/randoop/manual/dev.html#checks_)
  - 工程的几个入口 [Main entry points](https://randoop.github.io/randoop/manual/dev.html#code-entry) TODO
    - [randoop.main.GenTests](https://randoop.github.io/randoop/api/randoop/main/GenTests.html) is the main class for Randoop as it is normally used. There are other mains for other purposes.
    - Method `handle` is the main [GenTests](https://randoop.github.io/randoop/api/randoop/main/GenTests.html) entrypoint for Randoop. (This is not strictly true, as Randoop's true entrypoint is class [randoop.main.Main](https://randoop.github.io/randoop/api/randoop/main/Main.html). But `GenTests` is where all the action starts with test generation.) The `handle` method is long and mostly deals with setting up things before the generation process, and doing things like outputting tests after generation.
    - Most command line options are specified in [GenInputsAbstract](https://randoop.github.io/randoop/api/randoop/main/GenInputsAbstract.html).
  - [Test generator classes](https://randoop.github.io/randoop/manual/dev.html#code-generator)

(Also see the [Randoop Manual](https://randoop.github.io/randoop/manual/index.html).)