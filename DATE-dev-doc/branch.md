# 被测程序的分支、路径、循环、嵌套调用、递归

```
boolean leap(int y){
    return y%4!=0 ? false : y%4!=100 ? true : y%400==0 ? true : false;
}
```

先写成最 verbose 形式

```
boolean leap(int y){
    if(y%4!=0){
        return false;
    }else{
        if(y%4!=100){
            return true;
        }
        else{
            if(y%400==0){
                return true;
            }else{
                return false;
            }
        }
    }
}
```
