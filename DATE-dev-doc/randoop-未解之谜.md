# Randoop 未解之谜

## 重要吧

###有 `randoop/src/main/resources/conditions/jdk/java-lang-Math.json` 等很多针对 JDK 里类和方法的手写模型…… 干嘛呢？

find in path（cmd shift f）`conditions/jdk`

一处使用：`randoop.main.GenTests` `private Collection<? extends File> getJDKSpecificationFiles()`

是 "JDK specification files"

---



### immutable

```
RelativeNegativeIndex 的设计简史里提到：
```

(2) create new statements that represent the adjusted indices, which breaks the "reuse statements" idea.

难道 `Statement` 的设计理念也是 immutable 的（besides `Sequence`）？！这么情怀



###Sequence 不是 Serializable 的，为啥几个字段还加 transient？



###4. activeFlags 是？问杨》不知道



 ###5. SimpleList 还是个接口，就很皮。设计意图……？

```
ArrayListSimpleList 像包皮一样薄……
```



###6. ./gradlew 后面的参数 不是在gradle项目配置文件里定义的「任务名」？？？而是都定好的？

https://randoop.github.io/randoop/manual/dev.html#compiling

能跳过测试去打包的是 `./gradlew assemble` √

带编译 49 秒……



### 重复代码？

还有 methodCall 构造。

```
EnumReflectionTest

  private TypedClassOperation createConstructorCall(Constructor<?> con)
      throws RandoopTypeException {
    ConstructorCall op = new ConstructorCall(con);
    ClassOrInterfaceType declaringType = ClassOrInterfaceType.forClass(con.getDeclaringClass());
    List<Type> paramTypes = new ArrayList<>();
    for (java.lang.reflect.Type pc : con.getGenericParameterTypes()) {
      paramTypes.add(Type.forType(pc));
    }
    return new TypedClassOperation(op, declaringType, new TypeTuple(paramTypes), declaringType);
  }
```



```
TypedOperation

  public static TypedClassOperation forConstructor(Constructor<?> constructor) {
    ConstructorCall op = new ConstructorCall(constructor);
    ClassOrInterfaceType declaringType =
        ClassOrInterfaceType.forClass(constructor.getDeclaringClass());
    List<Type> paramTypes = new ArrayList<>();
    for (java.lang.reflect.Type t : constructor.getGenericParameterTypes()) {
      paramTypes.add(Type.forType(t));
    }
    TypeTuple inputTypes = new TypeTuple(paramTypes);
    return new TypedClassOperation(op, declaringType, inputTypes, declaringType);
  }
```





##  不重要

1. 我根本没有 daikon.jar 啊：

```
➜  randoop git:(master) git remote add lee git@github.com:SnowOnion/randoop-private.git
➜  randoop git:(master) git push lee master
Counting objects: 46914, done.
Delta compression using up to 8 threads.
Compressing objects: 100% (8838/8838), done.
Writing objects: 100% (46914/46914), 399.94 MiB | 1.99 MiB/s, done.
Total 46914 (delta 33855), reused 46881 (delta 33833)
remote: Resolving deltas: 100% (33855/33855), done.
remote: warning: GH001: Large files detected. You may want to try Git Large File Storage - https://git-lfs.github.com.
remote: warning: See http://git.io/iEPt8g for more information.
remote: warning: File lib/daikon.jar is 53.24 MB; this is larger than GitHub's recommended maximum file size of 50.00 MB
remote: warning: File lib/daikon.jar is 53.88 MB; this is larger than GitHub's recommended maximum file size of 50.00 MB
remote: warning: File lib/daikon.jar is 53.93 MB; this is larger than GitHub's recommended maximum file size of 50.00 MB
To git@github.com:SnowOnion/randoop-private.git
 * [new branch]      master -> master
➜  randoop git:(master) tree lib 
lib
├── README
└── plume.jar

0 directories, 2 files

```

