# Java Web API Service, Test Framework and Tests

The following is an example for bootstrapping a
framework for testing web APIs.  It is a work in 
progress.  This document explains the implemented
features and outlines the planned updates.

For questions, please email Ben at
bmmoskow@amath.washington.edu

## Quick Start

Prerequisites are [java](https://java.com/en/download/), 
[maven](https://maven.apache.org/download.cgi), 
[docker](https://docs.docker.com/install/) and 
[aws](https://aws.amazon.com/).  Then to build
 and run tests, execute on the command line

```mvn clean verify```

The integration tests are configured connect to an 
AWS S3 bucket with public access.  Please contact me
(contact info above) if you encounter any problems.

## Capabilities

This project demonstrates

* a basic web API service, programmatically using the Dropwizard API, 
demonstrating a simple computational numerics problem
* usage of the Maven lifecycle to build and test a service
* unit tests against the web service using TestNG
* usage of data-driven tests using TestNG
* acquiring test data from an AWS S3 bucket
* deployment of the service on a Docker container
* assignment of ports for the service according to
the ports available on the local machine
* integration tests against the service using TestNG

## Upcoming features

* extended comments and Javadocs
* a more seemless handling of data-driven tests
* integration with Jenkins
* use of DynamoDb to record history

## A Deeper Look

### The Service

The service takes advantage of the Web API framework provided by 
[Dropwizard](http://www.dropwizard.io/1.2.2/docs/).
It exposes one POST endpoint, ```/distribution/exponential/pdf```, which 
computes the 
[probability density function of the exponential distribution](https://en.wikipedia.org/wiki/Exponential_distribution).
This problem has a particular computational vagary that the program
attempts to fix.  

The formula, ```lambda*exp(-lambda*x)```, has a natural
underflow when the solution is less than ```2^-1074```, the limit of 
double precision.

The formula also has a spurious underflow where ```exp(-lambda*x)``` 
underflows but ```lambda*exp(-lambda*x)``` is still above 
the underflow level.  
In this case, when the exponential term underflows, it 
will multiply ```lambda``` and compute to zero.
An example of this spurious zero is ```lambda = x = 27.3```. 
To fix this issue, one needs to instead try the alternate but equivalent
formula ```exp(log(lambda) - lambda*x)```.

The service presented here
corrects this problem by using the alternate formula whenever the prior
formula returns zero.  

Matlab gets this wrong!  To see this, try the [matlab function 
```exppdf(x, 1/lambda)```](https://www.mathworks.com/help/stats/exppdf.html) for the 
above example and see that it incorrectly returns zero where a non-zero
answer is correct.  (Note that Matlab defines ```lambda``` as the reciprical of
our ```lambda```; otherwise the formula is identical.)

### The [Maven Build Lifecycle](http://maven.apache.org/ref/3.5.0/maven-core/lifecycles.html)

This Java project uses Maven to compile and run tests and one may define
various operations as part of the build process in the ```pom.xml``` file.  
On the command line, the user can run all or a subset of these commands as 
needed.

The following operations are defined in this package as a part of the 
Build Lifecyle:

* **test**:  run unit tests
* **compile**:  build the code
* **prepare-package**:  select unused ports on the local machine to be used
later for running the service.
* **package**:  
  * build the .jar file for the code
  * place the port numbers into files that will use them later
* **pre-integration-test**:  run ```docker-compose up``` on the Service
* **integration-test**:  run the integration tests
* **post-integration-test**:  run ```docker-compose down``` on the Service
* **verify**:  verify that the integration tests passed
