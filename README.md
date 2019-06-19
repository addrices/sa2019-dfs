# 微服务架构的分布式文件系统设计与实现
161220151 许嘉帆

## 命令说明
我主要使用命令行下的curl命令来对系统进行测试和实现。

### 启动
1. 设置更改
，在 namenode/src/main/resources/namenodeApplication.properties 中更改datanodeManager.replicas和datanodeManager.size分别对应副本数，块大小
```
server.port=8761

eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false

logging.level.com.netflix.eureka=OFF
logging.level.com.netflix.discovery=OF

datanodeManager.replicas=2
datanodeManager.size=8196
```

2. 在namenode目录下运行
```
mvn spring-boot:run
```
3. 在datanode目录下运行
```
mvn spring-boot:run -Dserver.port=8080
mvn spring-boot:run -Dserver.port=8081
mvn spring-boot:run -Dserver.port=8082
```
这样我们就有了3个datanode的结点

### 命令测试
1. 上传文件,进入需要上传的文件目录(上传本地的lab3.pdf文件到存储系统的lab3.pdf)
```
[addrice:~/Ducument/SAtest]$ ls
a  lab3.pdf
[addrice:~/Ducument/SAtest]$ curl -X PUT -F "file=@lab3.pdf" localhost:8761/upload/lab3.pdf
upload success%     
```
2. 查看目录(查看整个目录)
```
[addrice:~/Ducument/SAtest]$ curl -X GET localhost:8761/dir/          
home
    lab3.pdf
```
3. 下载(下载lab3.pdf文件到a.pdf)
```
[addrice:~/Ducument/SAtest]$ curl -X GET localhost:8761/download/lab3.pdf > a.pdf
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100  750k    0  750k    0     0  1773k      0 --:--:-- --:--:-- --:--:-- 1773k

```
4. 创建目录(创建目录txt)
```
[addrice:~/Ducument/SAtest]$ curl -X PUT localhost:8761/newdir/txt
addDirtxt% 
```
5. 上传文件到特定目录下(a上传到txt/a)
```
[addrice:~/Ducument/SAtest]$ curl -X PUT -F "file=@a" localhost:8761/upload/txt/a
upload success%
[addrice:~/Ducument/SAtest]$ curl -X GET localhost:8761/dir/  
home
    lab3.pdf
    txt
        a
```
6. 删除文件
```
[addrice:~/Ducument/SAtest]$ curl -X GET localhost:8761/dir/           
gongzuohome
    txt
        a
```

### 实现
#### namenode
我使用fileManager和datanodeManager2个类进行管理。

fileManager负责管理上传的文件(file类描述)，file类中存放这file在系统中的路径，子文件，以及其数据块存放在哪个datanode中的哪个block等信息。

datanodeManager负责管理datanode以及相关的数据传输工作， namenode和datanode之间使用Eureka服务器的注册功能互相发现，当namenode发现一个新的datanode注册信息的时候，将其信息加入datanodeManager中。而在传输block的时候，datanodeManager会选择负载最小的结点进行传输。

#### datanode
datanode有一个DataManager负责管理传输来的数据块，会将数据块存放到block/{port}目录下。