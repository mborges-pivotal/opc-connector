[![Build Status](https://travis-ci.org/mborges-pivotal/opc-connector.svg?branch=master)](https://travis-ci.org/mborges-pivotal/opc-connector.svg?branch=master)

# OPC Connector
The purpose of this project is to provide connectivity to machines (broadly speaking) via the OPC standard. The initial OPC standard was restricted to the Windows platform (OLE for Process Control), but since has evolved to support an more universal approach (OPC UA - Unified Architecture)

See [What is OPC](https://opcfoundation.org/about/what-is-opc/)

## Projects

We have 2 projects: 

* opc - core project with Spring Boot OPC client application
* opc-xd - Spring XD OPC DA Source

The idea is to use the opc client to explorer the OPC server and the Spring XD source to setup data ingestion pipelines. 

## Building

**Note:** We have a *dependency* folder with a package of libraries and script to install on your local maven repository. However, this since has been replace by remote repository. Still, I'm concern about the evolution, or lack of it, of the open-scada and its ancillary projects - e.g. Utgard

1. Build the *opc* project and install in local maven repository
  1. ```mvn install```
2. Build single jar Spring boot App
  2. ```mvn package spring-boot:repackage```
3. Build the *opc-xd* project and upload the module to Spring XD. We tested with 1.1.2 and 1.2.0.
  3. ```mvn package -DskipTests``` - We don't want to run the tests

## Running the opc client

You can run from maven or use the Spring Boot single jar

1. ```mvn spring-boot:run``` Or
2. ```java -jar target/opc-0.0.1-SNAPSHOT.jar```
  2. You can provide your own *application.yml* using the --spring.config.location=application.yml 

Once the client is running you can connect using ssh. The default port is 2222 and user/password is admin/admin.

```ssh -p 2222 admin@localhost```

Then type **opc** to see the available commands. 

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::  (v1.3.0.M2) on mborgesalmacpro
> opc
usage: opc [-h | --help] COMMAND [ARGS]

The most commonly used opc commands are:
   groups           List groups
   connect          Connect to OPC Server
   listen           Listen tag updates from OPC Server
   list             List OPC Server definitions and Connections
   servers          list OPC Servers from existing server connection
   disconnect       Disconnect from OPC Server
   atag             Add tag to listen
   quiesce          Quiesce OPC Server
   tags             list OPC Server tags
   agroup           Add group item
   rgroup           Remove group item
   rtag             Remove tag to listen
   lgroup           Read (Listen) group items once
   server           create OPC Server
```
You can get quick help by using the *--help* and detail using *man*. For example:

```
> opc connect --help
usage: opc [-h | --help] connect <name> <server> <password>

   [-h | --help] this help
```
And

```
> man opc connect
NAME
       opc connect - Connect to OPC Server

SYNOPSIS
       opc [-h | --help] connect <name> <server> <password>

STREAM
       opc connect <java.lang.Void, java.lang.Object>

PARAMETERS
       [-h | --help]
           Display this help message

       <name>
           The name of the Connection to create

       <server>
           The name server to use

       <password>
           password to use

```
## Typical opc client flows

1. ```opc list``` - to see available servers
2. ```opc server``` - create a server definition based on existing servers
3. ```opc connect``` - connect to opc server
  3. ```opc servers``` - list all opc servers from the current connected server 
4. ```opc tags``` - list server tags. You may want to use flat browsing and filters for production servers.
5. Now you have 2 ways to retrieve data
  5. EXPERIMENTAL - Using groups
    5. ```opc agroup```, ```opc groups```,```opc lgroups``` and ```opc rgroup``` - add, list, listen/read, remove group items
  6. Async read (used by Spring XD)
    6. ```opc atag```, ```opc listen```, ```opc quiesce``` and ```opc rtag``` - add, listen, pause, remove items
7. ```opc disconnect``` 

## Using the Spring XD opc source module

1. Upload the module and check
  1. ```module upload --file [OPX-XD_TARGET]/opc.xd-0.0.1-SNAPSHOT.jar --name opc --type source```
  1. ```module info source:opc```
2. Create stream and deploy
  2. ```stream create --name opc-stream --definition "opc --progId=Matrikon.OPC.Simulation.1 --host=192.168.9.192 --domain=CORP --user=borgem --password=PASSWORD --tags=Random.Boolean,Random.Int1,Random.ArrayOfString --clsId=OR-PROGID | log "``` --deploy
   
Once the stream is deployed the opc source will start receiving the update events for the configured tags in a json format. 

```
{"id":"Random.Int1","value":"84","timestamp":"07/24/2015 02:15:45.347 AM","quality":"good"}
{"id":"Random.Boolean","value":"true","timestamp":"07/24/2015 02:15:45.347 AM","quality":"good"}
{"id":"Random.ArrayOfString","value":"like,never,before,,Matrikon's","timestamp":"07/24/2015 02:15:45.347 AM","quality":"good"}
```
Undeploy the stream to stop receiving the events. 
  

## Configuration

All the configuration is done via *application.yml* file and normal Spring boot capabilities for overwriting the configuration from the command line applies

See [Project Wiki for Details](https://github.com/mborges-pivotal/opc-connector/wiki)

