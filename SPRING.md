
# Contents
[Common Annotations](#Annotations) | [Microservice vs service oriented architectures (SOA)](#Microservice-vs-service-oriented-architectures-SOA) |
[Profile](#Profile) | [Spring Cloud Config Server](#Spring-cloud-config-server) | [RestTemplate](#RestTemplate) | [WebClient](#WebClient) | [Service Discovery](#Service-Discovery) | 
[Issues With Microservices](#Issues) | [Hystrix](#Hystrix) | [BulkHead Pattern](#BulkHead-Pattern) | [Virtual vs Platform Threads](#Virtual-vs-Platform-Threads) | 
[Spring Security](#Spring-Security) | [PACT](#PACT) | [CDC](#CDC)

## Annotations
- @SpringBootApplication
    - @Configuration + @EnableAutoConfiguration + @ComponentScan
- @Component
    - if a class doesn't fit into controller, Repo or service we can mark with this to be managed by spring
- @Service
- @Repository
- @Controller
    - returns view
- @REstController
    - @Controller + @ResponseBody
    - Returns http response
- @RequestMapping("/api")
- @GetMapping("/users")
- @PostMapping("user")
    - (@RequestBody User user){}
- @Entity
    - @Id
    - @GeneratedValue(strategy = GenerationType.IDENTITY)
- @Value
  - @Value("${my.name}")
  - @Value("${my.name: default value}")
  - @Value("some static message") private String str;
  - @Value("${my.values}") private List<String>listValue
- @ConfigurationProperties("db") // anything starting with db like db.host, db.name 
  will be assigned to respective property of the class  
## Microservice-vs-service-oriented-architectures-SOA
SOA is an older architecture style where services are typically larger, coarse-grained, and rely on a central Enterprise Service Bus (ESB) for communication and orchestration. This often leads to bottlenecks and tighter coupling.

Microservices take the concept further by breaking applications into smaller, fine-grained services, each owning its data and deployed independently. They communicate using lightweight protocols (REST, gRPC, messaging) and are decentralized.

SOA is more suitable for legacy, enterprise-wide integration, while microservices are designed for cloud-native, scalable, CI/CD-friendly systems.

In short: SOA = centralized, coarse-grained, ESB-driven; Microservices = decentralized, fine-grained, independently deployable.
## Profile
- Default profile is always active
- Naming
  - application-<profile name>.extn
- In prop file, Add
  - spring.profile.active: profile name
- We can also select which bean to load during startup 
  - @Profile("profile name") 
## Spring-cloud-config-server
- Create a Spring project with dependency 
  - spring-cloud-config-server
- Add 
  - @EnableConfigServer to main class
- In prop file add
  - spring.cloud.config.server.git.url = github path wher we maintain config
- Now its ready at
  - localhost:port/application/profileName
- Now to use this config server in any project
  - Add client dependency
    - spring-cloud-starter-config
  - In prop file
    - spring.cloud.config.uri= localhost:port
  - Now all the prop in files will be updated with the value from config server
- Now the app is associated with prop file only during startup so to read the latest config
  - Add actuator dependency to use one of its endpoint
  - Add @RefreshScope to the property we need to update
  - Call /actuator/refresh end point
  - here you go you got your prop updated 
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
![img.png](assets/clientSideDiscovery.png)
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
## Virtual-vs-Platform-Threads
1. Platform Threads
    - Platform threads are OS-level threads created and managed by the Java Virtual Machine (JVM) but mapped directly to underlying operating system threads.
    - Key Characteristics
        - Backed by OS threads → heavy resource usage.
        - Managed by OS scheduler.
        - Thread creation is expensive.
        - Limited scalability due to 1:1 mapping with OS threads.
      ```java
        Thread platformThread = new Thread(() -> System.out.println("Running in " + Thread.currentThread()));
        // Platform Thread
        platformThread.start();
      ```
2. Virtual Threads
    - Virtual threads are lightweight threads managed by the JVM (not the OS) and are built on top of platform threads using the ForkJoinPool internally.
    - Key Characteristics
        - User-mode threads → created and scheduled by the JVM, not the OS.
        - Much lighter than platform threads.
        - Thousands to millions can be created without high memory cost.
        - Perfect for I/O-bound tasks (e.g., HTTP calls, DB queries).
        - Uses continuations internally to park and resume threads efficiently.
      ```java
        Thread virtualThread = Thread.ofVirtual().start(() -> System.out.println("Running in " + Thread.currentThread()));
      ```
## Spring-Security
with Spring security we can manage
- Authentication (Who the user is)
- Authorization (What User can access)
- with combination of
  - Password encoding
  - Role based access control
  - session management
  - OAuth2, JWT, and more
### Steps
1. Add spring security dependency
    - Every endpoint is now secure
    - Spring auto generate a login form 
    - And default user is created with random password
## PACT
## CDC