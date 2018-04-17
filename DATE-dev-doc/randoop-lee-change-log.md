# Randoop 改动记录

*Dirty hack and clean hack…*



1. [src/main/java/randoop/operation/TypedTermOperation.java](https://github.com/SnowOnion/randoop-private/commit/0ba3d8260b94dcb0636cb4c07e613a70105a473a#diff-0229d43114eca9bff909f7552bf58158) 类和构造函数从包可见改成了 public! 因为在 [src/main/java/randoop/sequence/Sequence.java](https://github.com/SnowOnion/randoop-private/commit/0ba3d8260b94dcb0636cb4c07e613a70105a473a#diff-5923d03fa4fbfe11fcc5fe6455fa9d6b) 的 modifyPrimitive 里用到了。
   1. TODO 修复：在 operation 包内写包装方法……
      1. 先看 TypedTermOperation 的 usage！好像 apply Substitution 很有意思……

2. GenTests

   ```
       GenInputsAbstract.use_jdk_specifications = false; // fuck sky // I don't wanna find the option...(even if it exists
   ```

   ​


好多个 非public -> public 啊！