
# Contents
[Microservice vs service oriented architectures (SOA)](#Microservice-vs-service-oriented-architectures-SOA) | [RestTemplate](#RestTemplate) | [WebClient](#WebClient) | [Service Discovery](#Service-Discovery) | 
[Issues With Microservices](#Issues) | [Hystrix](#Hystrix) | [BulkHead Pattern](#BulkHead-Pattern)

## Microservice-vs-service-oriented-architectures-SOA
SOA is an older architecture style where services are typically larger, coarse-grained, and rely on a central Enterprise Service Bus (ESB) for communication and orchestration. This often leads to bottlenecks and tighter coupling.

Microservices take the concept further by breaking applications into smaller, fine-grained services, each owning its data and deployed independently. They communicate using lightweight protocols (REST, gRPC, messaging) and are decentralized.

SOA is more suitable for legacy, enterprise-wide integration, while microservices are designed for cloud-native, scalable, CI/CD-friendly systems.

In short: SOA = centralized, coarse-grained, ESB-driven; Microservices = decentralized, fine-grained, independently deployable.
## RestTemplate
- By default, available with spring boot web
- It is synchronous by default.
```java
RestTemplate restTemplate = new RestTemplate();
Movie movie = restTemplate.getForObject("http://localhost:8082/movie/", Movie.class);
```
## WebClient
- Comes with spring boot webflux maven dependency
- It is asynchronous by default.
```java
WebClient.Builder builder = WebClient.builder();
Movie movie = builder.build()
        .get()
        .uri("http://localhost:8082/movie/")
        .retrieve()
        .bodyToMono(Movie.class)
        .block();
```
### Note
- .bodyToMono() → for a single object. 
- .bodyToFlux() → for a list/stream of objects. 
- You can add .onStatus() to handle error responses gracefully. 
- You can also set headers (like auth tokens) with .header("Authorization", "Bearer xyz").
## Service-Discovery
### Client Side
![clientSideDiscovery.png](assets%2FclientSideDiscovery.png)
### Server Side
![serverSideDiscovery.png](assets%2FserverSideDiscovery.png)
### Eureka Configuration
#### Server Configuration
   - Add @EnableEurekaServer in app class
   - Add pom dependency
        ```json
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        ```
   - Add property
       ```json
         server.port=8761
         eureka.client.register-with-eureka=false
         eureka.client.fetch-registry=false
        ```
![runningEureka.png](assets%2FrunningEureka.png)
#### Client configuration
   - @EnableEurekaClient,  with newer version not mandatory for client
   - If server is running on default port no need any prop in property file 
     else we have to add below in prop file
     - spring.application.name=movie-service
     - eureka.client.service-url.defaultZone=http://localhost:9090/eureka/
#### Calling
```java
    @Bean
    @LoadBalanced //Add this while creating RestTemplate or WebClient.Builder builder = WebClient.builder();
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
```
- while calling use service name in url insted of hostname
  http://RATING-DATA-SERVICE/rating/users/
## Issues
### Instance of microservice is down
### Microservice is slow
## Hystrix
1. Add maven dep
2. Add annotation in spring class @EnableCircuitBreaker
3. Add Hystrix command to method which want to enable hystrix 
   @HystrixCommand(fallbacMethod="methodName")
4. Configure behavior 
![hystrixParam.png](assets%2FhystrixParam.png)
#### NOTE: 
with latest version of spring cloud Hystrix is not supported in that case you have to use
<dependency>
<groupId>org.springframework.cloud</groupId>
<artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>
## BulkHead-Pattern
The Bulkhead Pattern is a resilience pattern that isolates resources (like threads, memory, or connection pools) 
for different parts of a system, so that a failure in one area does not bring down the entire system.
![bulkHead.png](assets%2FbulkHead.png)
