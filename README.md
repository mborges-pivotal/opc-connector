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

## Running

You can run from maven or use the Spring Boot single jar

1. ```mvn spring-boot:run``` Or
2. ```java -jar target/opc-0.0.1-SNAPSHOT.jar```

Once the client is running you can connect using ssh. The default port is 2222 and user/password is admin/admin.

```ssh -p 2222 admin@localhost```

Then type **opc** to see the command options.

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
   list             List OPC Server definitions and Connections
   connect          Connect to OPC Server
   tags             list OPC Server tags
   atag             Add tag to listen
   rtag             Remove tag to listen
   listen           Listen tag updates from OPC Server
   server           create OPC Server
   disconnect       Disconnect from OPC Server
   quiesce          Quiesce OPC Server
```

## Configuration

All the configuration is done via *application.yml* file and normal Spring boot capabilities for overwriting the configuration from the command line applies

See [Project Wiki for Details](https://github.com/mborges-pivotal/opc-connector/wiki)

