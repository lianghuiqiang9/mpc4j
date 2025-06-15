
# change the xml
<!--<module>mpc4j-work-femur</module>-->

# compile

mvn package

mvn clean install -DskipTests

# run

java -jar mpc4j-s2pc-pir/target/mpc4j-s2pc-pir-1.1.4-beta-jar-with-dependencies.jar mpc4j-s2pc-pir/src/test/resources/conf_single_cp_ks_pir_example.conf server