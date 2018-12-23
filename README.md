## Cli util project  

> #### Index

- <a href="#git">Git cli</a>

---  

<div id="git"></div>

### Git cli util

> #### Index  

- <a href="#git-status">Status</a>  

---  

<div id="git-status"></div>  

#### Git status  
; Checks added files & untracked files from path with recursive depth

> Options

```
usage: java -jar cli-util status

Example of Using cli-util :)
 -d,--depth <arg>   Will check with depth. default value is 1
 -h,--help          Display help messages
 -p,--path <arg>    trace from this path
 -s,--show <arg>    show added & untracked files. default false
```  

> Sample command

```
$ java -jar target\cli-util.jar status -p C:\git\zaccoding -d 1 -s false
```  

> Sample result  

```
----------------------------------------------------------------------------
>> Check repository [ blockchain ]> remain state & un tracked file. added : 1 | un tracked : 2
----------------------------------------------------------------------------
>> Check repository [ blockchain-collector ]> empty stage files
```  








