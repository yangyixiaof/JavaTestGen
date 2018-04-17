# Randoop 二次开发

查看 [Randoop 的 Readme](README-Randoop.md)。

## 版本管理 with git

Github 不允许把一个 public 项目 fork 后变 private，所以我 clone 了 [Randoop](https://github.com/randoop/randoop)、自己建了这个 private 项目并把 cloned Randoop 的 Git 仓库的 remote 指向[这里](https://github.com/SnowOnion/randoop-private)。

`master` 分支保留了 clone 时的样子。请不要更新 `master` 分支。可以拉取（`git fetch origin`）`origin/master`。

一般地，李的开发在 `lee` 分支上进行。杨的开发在 `yyx` 分支进行。

Fix bug 请新开分支（一个例子：`fixing-remove`），合并后删除分支。

目前的合并方式是 `merge`。

常用命令见子章节。

### push 时请指定分支

推到 date 这个远端（remote）的 lee 分支（我在本地，把指向 https://github.com/SnowOnion/randoop-private 的 remote 命名为 date。你可能采用了其他命名——很可能是 origin）

```
git push date lee
```

## commit 前请手动格式化代码

`./gradlew googleJavaFormat` 或将 IDE 的一键格式化代码配置为 googleJavaFormat。

否则由于 randoop 的项目里设置了 git commit 时自动 `./gradlew googleJavaFormat`，会产生双份的 commit。

## 更新 Javadoc

`./gradlew javadoc`

产生的 javadoc 还是中文的（根据系统 locale 来的？），但我直接在 Safari 打开 `build/docs/api/index.html` （并不是 `docs/api/index.html` ）会乱码。一个方法是用 用 [npm](https://www.npmjs.com) 安装的 http-server 来建一个微型 HTTP 服务器…… <!--META：链接不宜太多。链接是增加信息量的传送门，但也是「我需要点吗？」的疑惑来源-->

```
npm install -g http-server
cd build/docs/api/
http-server
# 然后访问它提示的 URL，如 http://101.5.214.142:8080
```



