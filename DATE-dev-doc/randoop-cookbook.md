# Randoop Cookbook

## Lee 常用命令 

*我们必须优雅，我们必将优雅*。 ——希尔伯特（雾

```
➜  src pwd
/Users/sonion/dev/Graduate/SBST/randoop-expr-0/src
➜  src echo $RANDOOP_JAR
/Users/sonion/dev/Graduate/SBST/randoop/build/libs/randoop-all-3.1.5-lee-1.jar
➜  src alias rdpgen 
rdpgen='java -cp .:/Users/sonion/dev/Graduate/SBST/randoop/build/libs/randoop-all-3.1.5-lee-1.jar randoop.main.Main gentests'

java -cp .:$RANDOOP_JAR randoop.main.Main gentests --testclass=xyz.sonion.randoop.CutWithIntToVoid --time-limit=10

rdpgen --testclass=xyz.sonion.randoop.CutWithIntToVoid --time-limit=10


cd /Users/sonion/dev/Graduate/SBST/randoop
# 编译出 randoop-all 的 jar：
./gradlew assemble

# 产生TC
cd /Users/sonion/dev/Graduate/SBST/randoop-expr-0/src
javac -d . yyx/*
## 用原版最新版randoop
rdpOrigen --testclass=yyx.ToTestGeneration --junit-package-name yyx --time-limit=10

rdpgen --testclass=xyz.sonion.randoop.CutWithIntToVoid --time-limit=5

# 全量构建：
./gradlew build manual

# 产生 IDEA 所需文件：
./gradlew idea
然后用 IDEA 打开。


# 至少在 gradle 编译过后，在 IDEA 可以运行。
参考参数：
gentests --use-jdk-specifications --testclass=xyz.sonion.randoop.CutWithIntToVoid --time-limit=4 --junit-output-dir src/main/java
被测类放在 randoop 项目里一起编译了…… 反正是知道如何用 jar 包方式来测其它被测类的。

```

## 坑坑

### --timelimit → --time-limit

在当今的开发版本中，`The --timelimit command-line option has been renamed to --time-limit.`

​	没发版本所以没改manual吧……

### ConcreteTypes → JavaTypes

devManual 里（https://randoop.github.io/randoop/manual/dev.html#creating_sequence）没改！你们读者真是……

https://github.com/randoop/randoop/search?o=desc&q=ConcreteTypes&s=committer-date&type=Commits&utf8=✓

Change *ConcreteTypes* to JavaTypes and void to own class

Ben Keller committed to [randoop/randoop](https://github.com/randoop/randoop) on 25 Aug 2016



日啊，devManual 里的[建立新序列](https://randoop.github.io/randoop/manual/dev.html#creating_sequence)改成 JavaTypes 依然不可用？！TODO



### Randoop 的 Gradle 配置里写的，本身用 Java7

```
sourceCompatibility = 1.7
targetCompatibility = 1.7
```
在 Java 8 跑会有幺蛾子否？



### 在 Eclipse/IDEA 里直接跑 Main 函数时需要在选项里加：

`--use-jdk-specifications=false`

否则提示路径 path 之类的错误。

这个选项和相关功能（GenTests.java 里 `if (GenInputsAbstract.use_jdk_specifications)`）是最近（[2017-11-23](https://github.com/randoop/randoop/commit/5ae656171baf17170e3b582f3b83af9efc6fcdcb)）加到 randoop 的，还没写进开发者手册和用户手册。

