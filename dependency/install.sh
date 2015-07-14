#!/bin/sh

# based on Pivotal-Field-Engineering/pxf-field

tar -zxvf dependencies.tgz

#GROUP=com.gopivotal

#mvn install:install-file -Dfile=./gemfirexd.jar -DgroupId=$GROUP -DartifactId=gemfirexd -Dversion=1.0 -Dpackaging=jar
#mvn install:install-file -Dfile=./gemfirexd-client.jar -DgroupId=$GROUP -DartifactId=gemfirexd-client -Dversion=1.0 -Dpackaging=jar
#mvn install:install-file -Dfile=./postgresql-9.3-1101.jdbc41.jar -DgroupId=$GROUP -DartifactId=postgresql -Dversion=9.3 -Dpackaging=jar

#rm gemfirexd.jar
#rm gemfirexd-client.jar
#rm postgresql-9.3-1101.jdbc41.jar

#cd ../pxf-test
#mvn clean install

exit 0
