# Randoop TODO List

## 变异操作

- [x] 设计实现 Trackable Sequence

- [x] 分析可行 Mutation 时，不 modify 自定义辅助函数 和 NonReceiverTerm

      （能被其他改动 cover；导致 action 爆炸）

- [ ] 用 MethodCall，自定义辅助函数 实现 ModifyPrimitive 新格式

      - [x] 写出来

      - [x] 支持Boolean

      - [ ] 支持各种数值类型、
            - [x] 数值类型的overloading实验
            - [ ] 设计合适的接口。咔嚓全部一类型一函数地实现 = 把麻烦扔给给概率模型设计
            - [ ] 实现
            - [ ] 测试

      - [x] 支持字符串类型
            - [x] String 的自定义辅助函数

      - [x] 2018-01-31 讨论：有必要细分接口daze
            - [ ] modifyPrimitive 分裂成：
                  - [x] modifyIntegral, 
                        - [ ] 传入类型自动转换
                        - [x] 又分裂成多个小的
                  - [x] modifyReal, 
                        - [ ] 传入类型自动转换
                  - [x] modifyBoolean
            - [x] 写三个：
                  - [x] modifyStringInsert, 
                  - [x] modifyStringRemove, 
                  - [x] modifyStringModify 233333

      - [ ] 测好

      - [ ] ```
            下面写法太二了，改成yyx的 Type#getRuntimeClass()
            Class<?> classOfVarToModify = DateRuntime.realTypeToClass.getOrDefault(typeOfVarToModify, null);
            ```

- [x] 讨论：变异操作和值修改操作，参数不合法时返回什么？大概不要放任崩溃，但返回什么东西还是要商量

      原对象？同值的新对象？null？

      - [ ] `TraceableSequence#insert modify remove` 的参数
      - [ ] `DateRuntime.insert remove modify` 的参数
      - [x] 2018-01-31 结论：fail loudly。因为是我们传进去的参数。

## 随机框架

- [ ] 随机框架完全调起
      - [ ] 概率模型
- [x] DateGenerator 的初始 Sequence 集合（现在使用了 GrulSeed）

## 泛型处理

- [ ] `TreeSet<T>` Randoop 最新版也处理不了 :( 但我们要处理

2018-03-09，杨观察到以前版本的 Randoop 曾经：

```
LinkedList<String> l=..;
Linked<LinkedList<String>> ll=..;
```

就是以前出现过的类型，当做后面的填类型参数的材料。

（？？？如果以前有这功能，何至于 Randoop 4.0 还不能测 TreeSet 呢……奇怪）


杨：在 `MutationAnalyzer` 里，

```
public void GenerateInsertOperations(
      List<MutationOperation> mutates, Set<TypedOperation> candidates)
```
“找了 TypedOperation 但是没填类型参数”

改这里。

1. 先在 DateGenerator 里试
2. 把相似方式在 ForwardGenerator 试试，看 InstancialtionError 能不能消除。能消除则说明方法OK。

Randoop does not add elements to generic collections #105 https://github.com/randoop/randoop/issues/105 也许相关？

## 影响分析

- [ ] 以一个独立 Java 进程的方式整合插桩模块
- [ ] Branch 分析

2018-03-09 读代码：`ExecutableSequence#execute` 执行测试用例的方式是，通过反射把存在 Sequence 数据结构里的「程序」给拉到 Randoop 进程的同一层，然后用执行 Randoop 的 Java 解释器来执行该 Sequence……

杨：它们没搞？这样被测程序里有全局状态（static 变量 etc）时就有问题。

学到：`ClassLoader` 就是个类。多个 `ClassLoader` 实例里就可以存同一个类的多份 static 变量。常用的实现类是 `URLClassLoader`……（芝士力量！）

## 理论基础

- [ ] ……考虑“不同 State 下，可行的 Action 集合迥异”

- [ ] ……考虑“不同 State 下，「同一 Action」的效果迥异”

      “慢慢来，不一定一上来就 RL”


## 为了开发、使用和评价的便利

- [x] yyx 写了 TraceableSequence#toLongFormString()、人間の鑑（かがみ）です。
      - [x] 不过，toString 之前先 `Sequence#disableShortForm()` 就可以……
- [ ] 命令行灵活切换 ForwardGenerator 和 DateGenerator
      - [x] naive dirty hack 法大失败
- [ ] IDEA 里看 branch coverage
- [ ] 准备 Benchmark……
- [ ] 跟 evosuite 对比……evosuite-1.0.5.jar 请

## Meta：软件过程改进

- [ ] 配一下 Eclipse 的 Google Java Format…… 避免一批改动被迫 commit 两次，导致 commit 记录不好读
      - [ ] maybe 问题在于 Eclipse 里的 Git GUI 忽略了 git hook? ``
- [ ] IDEA 里的 Google Java Format 插件的严格程度，与 `./gradlew googleJavaFormat` 有微妙不同 ( ´_ゝ`) 再碰到且有闲心再说吧

