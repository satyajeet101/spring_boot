
# Contents
[Common Annotations](#Annotations) | [Microservice vs service oriented architectures (SOA)](#Microservice-vs-service-oriented-architectures-SOA) |
[Profile](#Profile) | [Spring Cloud Config Server](#Spring-cloud-config-server) | [RestTemplate](#RestTemplate) | [WebClient](#WebClient) | [Service Discovery](#Service-Discovery) |
[Issues With Microservices](#Issues-With-Microservices) | [Hystrix](#Hystrix) | [BulkHead Pattern](#BulkHead-Pattern) | [Virtual vs Platform Threads](#Virtual-vs-Platform-Threads) |
[Spring Security](#Spring-Security) | [Multi data source](Multi-data-source) | [Spring Caching](#Spring-Caching) | [PACT](#PACT) | [CDC](#CDC) | [Exceptions](#Exceptions-Handling) |
[Request Validation](#Request-Validation) | [Custom HTTP Status](#Custom-HTTP-Status) | [DataBase Configuration](#DataBase-Configuration) |
[Runtime Load](#Runtime-Load) | [Transaction](#Transaction) | [AOP](#AOP) | [Spring Batch](#Spring-Batch) |
[Spring WebFlux](#Spring-WebFlux) | [Log](#Log) | [Caching](#Caching)
## Annotations
- <span style="color:red>@SpringBootApplication</span>
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
- In case of query param
    - @GetMapping("/users")
        - (@RequestParam String name)
        - (@RequestParam(required = false, defaultValue = "Guest") String name)
- In case of path param
    - @GetMapping("/users/{id}")
        - public String getUserById(@PathVariable int id)
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
- @ConfigurationProperties("db")
    - put this on class and that class property will be bind by db.propName in property file
      will be assigned to respective property of the class
    - If Propert name is "driverClassName" then in propert file it should be "db.driver-class-name"
- @SpringBootTest integration testing and it sets the spring context
- @MockBean create a fake bean during unit testing
- @Transactional
- Acync operation
    - add @Async on method
    - @EnableAsync by adding in config class
- @Qualifier("beanName") to mark which bean to inject
    - When you have multiple beans of the same type.
    - Often used in multi-implementation scenarios
    - When you want fine-grained control over which bean to inject.
    - @Primary on one bean to make it as default injection
- @ControllerAdvice to mark any class global exception handler
    - @ExceptionHandler // to mark the method as exception handler class in global exception handler class
- @Valid Input validation
    - public ResponseEntity<String> createUser(@RequestBody @Valid UserRequest userRequest) {}
    - @Email(message = "Email should be valid") private String email;
    - @Size(min = 6, message = "Password must be at least 6 characters") private String password;
    - @NotBlank(message = "Name is required")
        - @Pattern(regexp = "^[a-zA-Z0-9]{5,15}$", message = "Username must be alphanumeric and 5 to 15 characters long")
        - Make sure your class is annotated with @Validated if you're using it in a service layer

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
![img.png](mdFiles/assets/clientSideDiscovery.png)
### Server Side
![serverSideDiscovery.png](assets%2FserverSideDiscovery.png)
### Eureka Configuration
#### Server Configuration
- Add @EnableEurekaServer in app class
- Add pom dependency
     ```xml
     <groupId>org.springframework.cloud</groupId>
     <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
     ```
- Add property
    ```properties
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
## Issues-With-Microservices
#### Instance of microservice is down
#### Microservice is slow
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
- Add spring security in pom, it will enable the form login by default
- Create a configuration class 	@Configuration @EnableWebsucrity
- SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http){
- Take controll of 	public UserDetailsService userDetailsService(){
- Create encoder	public PasswordEncoder passwordEncoder(){return new BcryptPasswordEncoder();}
- Add below annotation to endpoint in controller @PreAuthorize("hasRole('USER')")
## Multi-data-source
- in prop file enter detail for read and write both
- no create 2 diff repo for read na write
- @JdbcRepository(dataSource = "read") or  @JdbcRepository(dataSource = "write") //whatevr source name you have given in prop file
- inject what ever you need
## Spring-Caching
- add dependency spring-boot-starter-cache
- @EnableCaching in main class
- @Cacheable on method need to be cached
- we can custmize cache behaviour using @CacheEvict and @CachePut
- chose cache provide like(EhCache or HazelCast) or use default concurant map based cache provided by spring-boot-starter-cache
## CDC
- Consumer Driven Contract
- A testing approach where the consumer of a service defines the expectations of the API contract (request & response).
- The provider (the microservice exposing the API) must then honor that contract.
## PACT
- Pact is a tool/framework that implements CDC testing.
- It allows consumers and providers to share API contracts and verify them automatically.
- How it works (simplified flow):
    - Consumer side:
        - Consumer writes tests using Pact DSL to define the expected request/response.
        - Pact generates a contract file (JSON).
        - Contract sharing:
            - The contract file is published to a Pact Broker (a central repo for contracts).
    - Provider side:
        - Provider runs Pact verification tests against its API implementation.
        - If provider API matches the contract → ✅
            - If provider changed something unexpectedly → ❌ Test fails.
## Exceptions-Handling
- Best Practices:
    - Use appropriate HTTP status codes (400, 404, 500, etc.)
    - Return a structured JSON error response
    - Log the error for debugging
    - Avoid exposing sensitive internal details
- Using @ControllerAdvice for Global Exception Handling
    - Define a Custom Exception
  ```java
    public class ResourceNotFoundException extends RuntimeException {
      public ResourceNotFoundException(String message) {
      super(message);
     }
    }
  ```
  - Create the Global Exception Handler
  ```java
  @ControllerAdvice
  public class GlobalExceptionHandler {

      @ExceptionHandler(ResourceNotFoundException.class)
      public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
          ErrorResponse error = new ErrorResponse();
          error.setMessage(ex.getMessage());
          error.setTimestamp(LocalDateTime.now().toString());
          error.setStatus(HttpStatus.NOT_FOUND.value());

          return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
      }

      @ExceptionHandler(Exception.class)
      public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
          ErrorResponse error = new ErrorResponse();
          error.setMessage("Internal Server Error");
          error.setTimestamp(LocalDateTime.now().toString());
          error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

          return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
      }
  	}
  ```
  	  - Throw the Exception in Any Class
  ```java
  	    @GetMapping("/users/{id}")
  	public User getUser(@PathVariable int id) {
  	    User user = userService.findById(id);
  	    if (user == null) {
  	        throw new ResourceNotFoundException("User not found with ID: " + id);
  	    }
  	    return user;
  	}
  ```
  	  - @ControllerAdvice	Automatically intercepts exceptions thrown in controllers
    - @ExceptionHandler	Handles specific exception types
## Request-Validation
- Add dependency
  ```pom
	<dependency>
	    <groupId>org.springframework.boot</groupId>
	    <artifactId>spring-boot-starter-validation</artifactId>
	</dependency>
  ```
- Create a DTO with Validation Annotations
  ```java
	import jakarta.validation.constraints.NotBlank;
	import jakarta.validation.constraints.Email;
	import jakarta.validation.constraints.Size;
	
	public class UserRequest {
	
	    @NotBlank(message = "Name is required")
	    private String name;
	
	    @Email(message = "Email should be valid")
	    private String email;
	
	    @Size(min = 6, message = "Password must be at least 6 characters")
	    private String password;
	
	    // Getters and setters
	}
  ```
- Use @Valid in the Controller
  ```java
    import org.springframework.web.bind.annotation.*;
	import org.springframework.http.ResponseEntity;
	import jakarta.validation.Valid;
	
	@RestController
	@RequestMapping("/api/users")
	public class UserController {
	
	    @PostMapping
	    public ResponseEntity<String> createUser(@RequestBody @Valid UserRequest userRequest) {
	        // If validation passes, proceed
	        return ResponseEntity.ok("User created successfully");
	    }
	}
  ```
- Handle Validation Errors (Optional but Recommended)
  ```java
  @ControllerAdvice
  public class ValidationExceptionHandler {
  
      @ExceptionHandler(MethodArgumentNotValidException.class)
      public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
          Map<String, String> errors = new HashMap<>();
          ex.getBindingResult().getFieldErrors().forEach(error ->
              errors.put(error.getField(), error.getDefaultMessage())
          );
          return ResponseEntity.badRequest().body(errors);
      }
  }
  
  ```
- Common Validation Annotations
  ```java
	- @NotNull	//Field must not be null
	- @NotBlank	//Field must not be empty or blank
	- @Size(min, max)	//String/collection size limits
	- @Email	//Valid email format
	- @Pattern	//Regex-based validation
	- @Min, @Max	//Numeric range
  ```
## Custom-HTTP-Status
- Create a Custom Exception Class
  ```java
	  public class ResourceNotFoundException extends RuntimeException {
	    public ResourceNotFoundException(String message) {
	        super(message);
	    }
	}
  ```
- Create a Global Exception Handler
  ```java
    import org.springframework.http.HttpStatus;
	import org.springframework.http.ResponseEntity;
	import org.springframework.web.bind.annotation.ControllerAdvice;
	import org.springframework.web.bind.annotation.ExceptionHandler;
	
	@ControllerAdvice
	public class GlobalExceptionHandler {
	
	    @ExceptionHandler(ResourceNotFoundException.class)
	    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
	        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
	        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	    }
	
	    // You can add more handlers for other exceptions
	}
  ```
- Create an Error Response DTO
  ```java
	  public class ErrorResponse {
	    private int status;
	    private String message;
	
	    public ErrorResponse(int status, String message) {
	        this.status = status;
	        this.message = message;
	    }
	
	    // Getters and setters
	}
  ```
- Throw the Custom Exception in Your Controller or Service
   ```java
  @GetMapping("/items/{id}")
  public Item getItem(@PathVariable Long id) {
      return itemRepository.findById(id)
          .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
  }
  ```
    - You can create other exception classes like:
        - BadRequestException → HttpStatus.BAD_REQUEST
        - UnauthorizedException → HttpStatus.UNAUTHORIZED
        - ConflictException → HttpStatus.CONFLICT
## DataBase-Configuration
1. Add dependency
```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
    </dependency>
```
2. Add below in property file
```properties
    spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=yourDB
    spring.datasource.username=yourUsername
    spring.datasource.password=yourPassword
    spring.datasource.driver-class-name=com.sqlserver.jdbc.SQLServerDriver
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
```
- Spring Boot's auto-configuration mechanism uses DataSourceProperties (A JPA class) to configure and create a DataSource bean based on the provided properties, simplifying data source setup.
- Customization and Multiple Data Sources:
    - While auto-configuration is convenient for single data source scenarios, we can create custom DataSourceProperties instances for more advanced configurations, including setting up multiple data sources with distinct properties.
        - Example
            - Create configuration for the primary data source
          ```java
          @Configuration
          @EnableTransactionManagement
          @EnableJpaRepositories(
            basePackages = "com.example.repository.primary",
            entityManagerFactoryRef = "primaryEntityManagerFactory",
            transactionManagerRef = "primaryTransactionManager"
            )
          public class PrimaryDataSourceConfig {
              @Bean
              @ConfigurationProperties("spring.datasource.sqlserver")
              public DataSourceProperties primaryDataSourceProperties() {
                  return new DataSourceProperties();
              }
              @Bean
              public DataSource primaryDataSource() {
                  return primaryDataSourceProperties().initializeDataSourceBuilder().build();
              }
              @Bean
              public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
                      EntityManagerFactoryBuilder builder) {
                  return builder
                          .dataSource(primaryDataSource())
                          .packages("com.example.model.primary")
                          .build();
              }
              @Bean
              public PlatformTransactionManager primaryTransactionManager(
                      @Qualifier("primaryEntityManagerFactory") EntityManagerFactory emf) {
                  return new JpaTransactionManager(emf);
              }
            }
          ```
3. Create entity class and annotate with @Entity
4. Create repo interface and extend with JpaRepository<Entity, primary key type>

## Runtime-Load
### Option 1: Using CommandLineRunner
- Implement CommandLineRunner interface in your main application class or any @Component class.
- Override the run(String... args) method to include your startup logic.
- The run method will be executed after the application context is loaded and right before the Spring Boot application starts.
```java
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;    
@Component
public class MyStartupRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        // Your startup logic here
        System.out.println("Application started with command-line arguments: " + Arrays.toString(args));
        // You can perform tasks like loading initial data, setting up resources, etc.
    }
}   
```
### Option 2: Using ApplicationRunner
- Similar to CommandLineRunner, but provides access to ApplicationArguments which can be more convenient for parsing command-line arguments.
- Implement ApplicationRunner interface in your main application class or any @Component class.
- Override the run(ApplicationArguments args) method to include your startup logic.
```java
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;    
@Component
public class MyAppStartupRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Your startup logic here
        System.out.println("Application started with option names: " + args.getOptionNames());
        // You can perform tasks like loading initial data, setting up resources, etc.
    }
}
```
- To Use Application Arguments
    - java -jar yourapp.jar --option1=value1 --option2=value2
    - Access in run method using args.getOptionValues("option1")
    - args.getNonOptionArgs() for non-option arguments
    - args.containsOption("option1") to check if an option is present
    - args.getOptionNames() to get all option names
    - args.getSourceArgs() to get raw arguments array
    - args.getOptionValues("option1") to get values for a specific option

### Option 3: Using @PostConstruct
- Annotate a method with @PostConstruct in any @Component or @Service class.
- The method will be executed after the bean's properties have been set and before the bean is put into service.
```java
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;    
@Component
public class MyBean {
    @PostConstruct
    public void init() {
        // Your initialization logic here
        System.out.println("Bean is initialized and ready to use.");
        // You can perform tasks like loading initial data, setting up resources, etc.
    }
}   
```
### Option 4: Using ApplicationListener<ApplicationReadyEvent>
- Implement ApplicationListener<ApplicationReadyEvent> interface in your main application class or any @Component class.
- Override the onApplicationEvent(ApplicationReadyEvent event) method to include your startup logic.
- The method will be executed when the application is fully started and ready to service requests.
- This is useful for tasks that should only run after the application is completely up and running.
```java
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;    
@Component
public class MyAppReadyListener implements ApplicationListener<ApplicationReadyEvent> {
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Your startup logic here
        System.out.println("Application is ready to service requests.");
        // You can perform tasks like loading initial data, setting up resources, etc.
    }
}
```
## Transaction
- Add dependency
```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
```
- Add @EnableTransactionManagement in config class
- Add @Transactional on method or class
- By default, runtime exceptions trigger rollback, while checked exceptions do not.
- You can customize rollback behavior using the rollbackFor and noRollbackFor attributes of @Transactional.

### Declarative Transaction Management
- Use @Transactional annotation on service methods or classes to manage transactions declaratively.
- Spring automatically handles transaction begin, commit, and rollback based on method execution and exceptions.

### Programmatic Transaction Management
- Use PlatformTransactionManager and TransactionTemplate for fine-grained control over transactions in code.
- Manually begin, commit, and rollback transactions as needed.
```java
    @Autowired
    private PlatformTransactionManager transactionManager;
    public void performTransaction() {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            // Business logic here
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }
```

### Transaction Propagation
- Defines how transactions behave when calling methods that are already transactional.
- Common propagation levels:
    - REQUIRED (default): Join existing transaction or create a new one.
    - REQUIRES_NEW: Suspend existing transaction and create a new one.
    - SUPPORTS: Join existing transaction if present, otherwise execute non-transactionally.
    - MANDATORY: Must join an existing transaction, otherwise throw an exception.
    - NOT_SUPPORTED: Suspend existing transaction and execute non-transactionally.
    - NEVER: Must execute non-transactionally, otherwise throw an exception.
    - NESTED: Execute within a nested transaction if a current transaction exists.
    - Example:
```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void newTransactionMethod() {
    // This method will always run in a new transaction
}
```

### Isolation Levels
- Defines the degree to which a transaction must be isolated from data modifications made by other transactions.
- Common isolation levels:
    - DEFAULT: Use the default isolation level of the underlying database.
    - READ_UNCOMMITTED: Allows dirty reads, non-repeatable reads, and phantom reads.
    - READ_COMMITTED: Prevents dirty reads, allows non-repeatable reads and phantom reads.
    - REPEATABLE_READ: Prevents dirty reads and non-repeatable reads, allows phantom reads.
    - SERIALIZABLE: Prevents dirty reads, non-repeatable reads, and phantom reads.
    - Example:
```java
@Transactional(isolation = Isolation.SERIALIZABLE)
public void serializableMethod() {
    // This method will run with SERIALIZABLE isolation level
}
```
### Rollback Rules
- By default, Spring rolls back transactions for unchecked exceptions (subclasses of RuntimeException) and errors.
- Checked exceptions (subclasses of Exception) do not trigger a rollback by default.
- You can customize rollback behavior using the rollbackFor and noRollbackFor attributes of @Transactional.
```java
@Transactional(rollbackFor = {CustomCheckedException.class})
public void methodWithCustomRollback() {
    // This method will roll back for CustomCheckedException
}
``` 
### Read-Only Transactions
- Use readOnly = true attribute in @Transactional for methods that only read data and do not modify it.
- This can help optimize performance by allowing the database to apply optimizations for read-only operations.
```java
@Transactional(readOnly = true)
public List<Entity> fetchEntities() {
    // This method is read-only
}
```

## AOP
- Aspect-Oriented Programming allows you to separate cross-cutting concerns (like logging, security, transactions) from your business logic.
- In Spring Boot, AOP is commonly used for:
    - Logging
    - Performance monitoring
    - Security checks
    - Exception handling
### Key Concepts
- Aspect: A modularization of a concern that cuts across multiple classes. It is a class that contains advice and pointcuts.
- Join Point: A specific point in the execution of a program, such as method execution or exception handling.
- Pointcut: A rule that selects which Join Points to apply logic to. It defines where advice should be applied.
    - @Pointcut("within(com.example..*)") // all methods in com.example package
    - @Pointcut("execution(* com.example.service.*.*(..))") // all methods in service package
    - Why Use @Pointcut?
        - Reusability: You can reference the same pointcut in multiple advices.
        - Readability: Gives meaningful names to complex expressions.
        - Maintainability: Easier to update one pointcut than multiple expressions.
        - Example
          ```java
              import org.aspectj.lang.annotation.Aspect;
              import org.aspectj.lang.annotation.Pointcut;
              import org.aspectj.lang.annotation.Before;
              import org.springframework.stereotype.Component;
            
              @Aspect
              @Component
              public class LoggingAspect {
            
                  // Step 2: Define a reusable pointcut
                  @Pointcut("execution(* com.example.service.UserService.*(..))")
                  public void userServiceMethods() {
                      // This method is just a placeholder for the pointcut expression
                  }
            
                  // Step 3: Use the pointcut in an advice
                  @Before("userServiceMethods()")
                  public void logBeforeUserServiceMethods() {
                      System.out.println("Before executing a method in UserService");
                  }
              }
            ```
- Advice: Action taken by an aspect at a particular join point. Types of advice include:
    - Before: Runs before the method execution.
    - After: Runs after the method execution (regardless of outcome).
    - After Returning: Runs after the method execution only if it completes successfully.
    - After Throwing: Runs if the method throws an exception.
    - Around: Surrounds the method execution, allowing you to control when the method is executed.
### Example
1. Add dependency
```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>
```
2. Create a Service class
```java
import org.springframework.stereotype.Service;
@Service
public class UserService {

    public void createUser(String name) {
        System.out.println("Creating user: " + name);
    }

    public void deleteUser(String name) {
        System.out.println("Deleting user: " + name);
    }
}
```
3. Create an Aspect class
```java
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.example.demo.UserService.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Before method: " + joinPoint.getSignature().getName());
    }

    @After("execution(* com.example.demo.UserService.*(..))")
    public void logAfter(JoinPoint joinPoint) {
        System.out.println("After method: " + joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "execution(* com.example.demo.UserService.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("Method returned: " + joinPoint.getSignature().getName());
    }

    @AfterThrowing(pointcut = "execution(* com.example.demo.UserService.*(..))", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        System.out.println("Exception in method: " + joinPoint.getSignature().getName());
        System.out.println("Error: " + error);
    }
}
```
- What’s Happening?
    - @Aspect: Marks the class as an aspect.
    - @Before, @After, etc.: Define when the advice runs.
    - execution(...): Pointcut expression that matches method executions.
    - how to infer param to execution(* *.*.checkout())
        - any return type
        - any package
        - any class
        - checkout() method
        - any param
- Common Pointcut Examples
    - execution(* com.example..*(..)): Matches all methods in the com.example package and its sub-packages.
    - execution(public * *(..)): Matches all public methods.
    - execution(* *..set*(..)): Matches all setter methods.
    - within(com.example..*): Matches all methods within classes in the com.example package and its sub-packages.
    - args(String): Matches methods that take a String argument.
    - this(com.example.MyInterface): Matches methods in beans that implement MyInterface.
    - target(com.example.MyClass): Matches methods in beans of type MyClass.
- we can use JoinPoint object to read param as below
```java
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.example.service.UserService.createUser(..))")
    public void logMethodParams(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs(); // Get method arguments

        System.out.println("Method: " + joinPoint.getSignature().getName());
        for (int i = 0; i < args.length; i++) {
            System.out.println("Arg " + i + ": " + args[i]);
        }
    }
}
```
## Spring-Batch

## Spring-WebFlux

## Log
- Add dependency
```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-logging</artifactId>
    </dependency>
```
- Default logging framework is Logback
- Default log file name is spring.log
- To change log file name add below in prop file
```properties
    logging.file.name=app.log   
    logging.file.path=/var/logs
```
- To change log pattern add below in prop file
    ```properties
        logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
        logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n    
    ```
- To change log level add below in prop file
```properties
    logging.level.root=INFO
    logging.level.org.springframework.web=DEBUG
    logging.level.com.example=TRACE
```
- To use in code
```java
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    public class MyService {
        private static final Logger logger = LoggerFactory.getLogger(MyService.class);
    
        public void performTask() {
            logger.info("Task started");
            try {
                // Task logic here
                logger.debug("Performing task step 1");
                // More logic
                logger.debug("Performing task step 2");
            } catch (Exception e) {
                logger.error("Error occurred while performing task", e);
            }
            logger.info("Task completed");
        }
    }
```

- Using through Lombook dependency by annotation @Slf4j

```java
    import lombok.extern.slf4j.Slf4j;
    @Slf4j
    public class MyService {
    
        public void performTask() {
            log.info("Task started");
            try {
                // Task logic here
                log.debug("Performing task step 1");
                // More logic
                log.debug("Performing task step 2");
            } catch (Exception e) {
                log.error("Error occurred while performing task", e);
            }
            log.info("Task completed");
        }
    }   
```
- Different log level
    - TRACE
    - DEBUG
    - INFO
    - WARN
    - ERROR
    - FATAL
    - OFF

## Caching
### In memory Caching
1. Add dependency
```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-cache</artifactId>
    </dependency>   
```
2. Enable caching in main class @EnableCaching
3. Add @Cacheable to method need to be cached
```java
    @Cacheable(value = "items", key = "#id")
    public Item getItemById(Long id) {
        // Simulate a slow service call
        try {
            Thread.sleep(3000); // 3 seconds delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return itemRepository.findById(id).orElse(null);
    }
```
4. Add @CacheEvict to method need to clear cache
```java
    @CacheEvict(value = "items", key = "#id")
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
```
5. Add @CachePut to method need to update cache
```java
    @CachePut(value = "items", key = "#item.id")
    public Item updateItem(Item item) {
        return itemRepository.save(item);   
    }
```
- By default it uses ConcurrentMapCacheManager which is not suitable for production
- You can use other cache providers like EhCache, Hazelcast, Caffeine, Redis etc.
### Redis Caching
1. Add dependency
```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
```
2. Add below in prop file
```properties
    spring.cache.type=redis 
    spring.redis.host=localhost
    spring.redis.port=6379
```
3. Enable caching in main class @EnableCaching
4. Add @Cacheable, @CacheEvict, @CachePut to method need to be cached/evicted/updated
5. By default it uses StringRedisSerializer for key and JdkSerializationRedisSerializer for value
6. You can customize it by creating a bean of RedisCacheConfiguration
```java
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // Set TTL for cache entries
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }
```

```TODO
SPRING SECURITY
https://www.youtube.com/watch?v=GH7L4D8Q_ak&list=PLxhSr_SLdXGOpdX60nHze41CvExvBOn09&index=10
@SpringBootApplication

Form based auth and basic auth

1. Add Spring security dependency,
As soon as you add it for any endpoit it will starts giving login page by default
you can add User/PWD in property file

default security class is 
	SpringBootWebSecurityConfiguration.java
default security method is 
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http){
	}

Now to take controll of default method we have to create our own class
1. Create any class
2. Add annotations,
	@Configuration
	@EnableWebsucrity
3. add @Autowire for DataSource dataSource	
4. Add method
5. Add annotation
	@Bean
6. by default it is statefull and done through jsessionId
   to make it stateless we have to add 
   http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

To take controll of user/password
1. create a method and annotate with bean as below
	public UserDetailsService userDetailsService(){
		UserDetails user1 = User.withUserName("user1")
								.password(passwordEncoder().encode("password").roles("USER").build();
		JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);//datasource is autowired
		userDetailsManager.create(user1)
		return new userDetailsManager;
	}

Now lets add the encoder
1. Create a method and annotate @Bean as below
	public PasswordEncoder passwordEncoder(){
		return new BcryptPasswordEncoder();
	}

Now to execute any endpoint based on roles,
1. Add below annotation to endpoint in controller 
	@PreAuthorize("hasRole('USER')")
2. Add below in securty	classs created earlier
	@EnableMethodSecurity

Till now we are able to create user and authenticate the user.

>>>>>>>>>>>>>>>>>>  >>>>>>>>>>>>>>>>>>  JWT  <<<<<<<<<<<<<<<<<<    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
JwtUtils
AuthTokenFilter
AuthEntryPointJwt
SecurityConfig


1. Add maven dependency for jwt 
	jjwt-impl
	jjwt-jackson
	jjwt-api
	
2. Create a class JwtUtils to perforn basic jwwt related task and annotate with @Component
a. add secret 
b. add expiration
c. method to generate token based on username,  date, exp time, secret key
	return Jwts.builder().subject(userName).issuedAt(new Date()).expiration(expiration).signWith(key()).compact();
d. method to read authorization from header and return token after trimming everything
e. method to get userName from token
	return Jwts.parser().verifyWith(key()).build().parsedSignedClaims(token).getPayload().getSubject();
f. method to validate token 
	Jwts.parser().verifyWith(key()).build().parsedSignedClaims(token);
	
private Key key(){
	return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
}	

3. Create a coustom class AuthTokenFilter to intercept any request annotate with @Component and extend with OncePerRequestFilter
a. @Autowire JwtUtils
b. @Autowire UserDetailsService
c. override doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	get jwt using util
	validate jwt using util
	get username using util
	validate the user 
	and set to context
	and continue the filter chain >> filterChain.doFilter(request, response)
	
4. Create a class AuthEntryPointJwt annotate with @Component and implement AuthenticationEntryPoint
a. override commence(HttpServletRequest request, HttpServletResponse response) method 
	which will be called in case of auth exception from filter and update the response to hold the unauthorized
```
