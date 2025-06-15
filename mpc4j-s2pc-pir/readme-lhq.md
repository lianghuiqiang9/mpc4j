
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

# install mpc4j-naive-fourq


# compile

mvn package


# run


java --add-modules jdk.incubator.vector -jar target/mpc4j-s2pc-pir-1.1.4-beta-jar-with-dependencies.jar conf_single_cp_ks_pir_example.conf server

java --add-modules jdk.incubator.vector -jar target/mpc4j-s2pc-pir-1.1.4-beta-jar-with-dependencies.jar conf_single_cp_ks_pir_example.conf client


# 坑
1. 真TM的坑多，config配置不清楚
2. naive要用，然后fhe估计还要安装。