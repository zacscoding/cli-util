## Cli util project  

> #### Index

- <a href="#git">Git cli</a>

---  

> Getting started  

```
app@app:~$ git clone https://github.com/zacscoding/cli-util.git
```  

```
app@app:~$ cd cli-util  
```  

```
app@app:~/cli-util$ mvn clean package  
```  

```
app@app:~/cli-util$ java -jar target cli-util.git

Commands : [ git-status | git-diff ]

```  


<div id="git"></div>

### Git cli util

> #### Index  

- <a href="#git-status">Status</a>  
- <a href="#git-diff">Diff</a>

---  

<div id="git-status"></div>  

#### Git status  
; Checks added files & untracked files from path with recursive depth

> Options

```
usage: java -jar cli-util git-status

Example of Using cli-util :)
 -d,--depth <arg>   Will check with depth. default value is 1
 -h,--help          Display help messages
 -p,--path <arg>    trace from this path
 -s,--show <arg>    show added & untracked files. default false
```  

> Example  


```
$ java -jar target\cli-util.jar git-status -p C:\git\zaccoding -d 1 -s false

----------------------------------------------------------------------------
>> Check repository [ blockchain ] > remain state & un tracked file. added : 1 | un tracked : 2
----------------------------------------------------------------------------
>> Check repository [ blockchain-collector ] > empty stage files
```  

---  

<div id="git-diff"></div>  

#### Git diff
; Compare the commits in local repository and remote repositories

> Options

```
usage: java -jar cli-util git-diff

Example of Using cli-util :)
 -h,--help          display help messages
 -d,--depth <arg>   will check with depth. default value is 1
 -p,--path <arg>    trace from this path
```  

> Example  

```
$ java -jar target\cli-util.jar git-diff -p C:\git\zaccoding -d 1

----------------------------------------------------------------------------
>> Check repository [ blockchain-collector ] > synchronized
----------------------------------------------------------------------------
>> Check repository [ kafka-learn ]
>>> diff ref refs/heads/master. local : 7ff1c30da1471aff7b61b8264f2d81b42c911655 | remote origin-https://github.com/zacscoding/kafka-learn.git : f104f49d6e2ede8d355701e9e508e04db5ca8fd6
----------------------------------------------------------------------------
>> Check repository [ blockchain-jsonrpc ]> empty remotes
----------------------------------------------------------------------------
>> Check repository [ git-temp ]
>>> Cannot connect repository. url : https://github.com/zacscoding/git-temp.git
----------------------------------------------------------------------------
>> Check repository [ ethereum-explorer ]
>>> Not exist 2 branches at local compared with https://github.com/zacscoding/ethereum-explorer.git
```    
