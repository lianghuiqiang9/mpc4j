
# install mpc4j-naive-tool

which java
/usr/bin/java
readlink -f /usr/bin/java
/usr/lib/jvm/java-17-openjdk-amd64/bin/java
/usr/lib/jvm/java-17-openjdk-amd64
临时
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
永久
touch ~/.bashrc
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
source ~/.bashrc

测试
echo $JAVA_HOME

cmake .. -DLIBSODIUM_LIBRARY=/lib/x86_64-linux-gnu/libsodium.so
sudo cp ~/workspace/mpc4j/mpc4j-native-tool/build/libmpc4j-native-tool.so /usr/lib
不推荐但是有用

# install mpc4j-naive-fourq


# compile

mvn package


# run


java --add-modules jdk.incubator.vector -jar target/mpc4j-s2pc-pir-1.1.4-beta-jar-with-dependencies.jar conf_lhq.conf server

java --add-modules jdk.incubator.vector -jar target/mpc4j-s2pc-pir-1.1.4-beta-jar-with-dependencies.jar conf_lhq.conf client


# 坑
1. 真TM的坑多，config配置不清楚
2. naive要用，然后fhe估计还要安装。
3. cp代表client preprocessing
4. kw代表keyword


# 代码位置
SimpleNaive* → KPIR^{kvs}

SimpleBin* → KPIR^{hash}

SimplePgm* → KPIR^{index}

cppir/ks/chalamet/ Chalamet


# 符号

Party ID	
Server Set Size	
Query Num	
Is Parallel	
Thread Num	
Init Time(ms)	
Init DataPacket Num	
Init Payload Bytes(B)	
Init Send Bytes(B)	
Pto  Time(ms)	
Pto  DataPacket Num	
Pto  Payload Bytes(B)	
Pto  Send Bytes(B)

// initTime 包括bff构建时间和A*DB的时间, 要减去一个
// initSendByteLength 是server发送给client的hint大小
// ptoTime 协议执行时间， 可以在日志中看到server 响应的时间
// ptoSendByteLength 是server发送给client的rep大小

# 

lognumkeys	&query nums	&init hint times	&hint size	&query gen times	&query gen comm.	&	answer gen times	&	answer gen comm.	&	reconstruct times

20    &  any    &  bff1428ms+hints13125ms   &   17264765B   &  & & 766ms  &  506820B   & 