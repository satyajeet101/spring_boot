# Content
[Beans](#Beans) | [Getting-hold-of-bean](#getting-hold-of-bean) | [Types-of-injection](#types-of-injection) | 
[Cyclic-Dependency](#cyclic-dependency) | [Unsatisfied-Dependency](#unsatisfied-dependency) | 
[@PostConstruct](#postconstruct) | [@PreDestroy](#predestroy) | [@ConditionalOnProperty](#ConditionalOnProperty)

## Beans
#### Getting-hold-of-bean
```java
    @Autowired
    private ApplicationContext applicationContext;
    
    MyService myService = applicationContext.getBean(MyService.class);
```
#### Types-of-injection
- Field Injection (using @Autowired on field)
    ```java
          @Autowired
          private MyService myService;
    ```
- Setter Injection (using @Autowired on setter method)
  ```java
          private MyService myService;
          
          @Autowired
          public void setMyService(MyService myService) {
              this.myService = myService;
          }
  ```
- Constructor Injection (using @Autowired on constructor or without it if there is only one constructor)
- Most recommended as it makes the class immutable and easier to test
  ```java
          private final MyService myService;
          
          @Autowired
          public MyComponent(MyService myService) {
              this.myService = myService;
          }
  ```
#### Cyclic-Dependency
- Occurs when two or more beans depend on each other directly or indirectly, creating a circular reference.
- Example
  ```java
        @Component
        public class A {
            @Autowired
            private B b;
        }
        
        @Component
        public class B {
            @Autowired
            private A a;
        }
  ```
- Solutions
    - Refactor the design to eliminate the circular dependency.
    - Use setter injection for one of the beans instead of constructor injection.
    - Use @Lazy annotation on one of the dependencies to delay its initialization.
    - Use ApplicationContext to get the bean manually when needed.
    - Use interfaces to break the direct dependency.
    - Use @PostConstruct to initialize one of the beans after both are created.
  ```java
    @Component
    public class A {
    @Autowired
    private B b;
      @PostConstruct
      public void init() {
          b.setA(this);
      }
    }

    @Component
    public class B {
    private A a;
    public void setA(A a) {
        this.a = a;
    }
  ```
#### Unsatisfied-Dependency
- Occurs when Spring cannot find a suitable bean to inject into a dependency.
- Common causes
    - Missing @Component, @Service, @Repository, or @Configuration annotation on the class.
    - Bean is defined in a package not scanned by Spring (missing @ComponentScan).
    - Multiple beans of the same type exist and Spring cannot determine which one to inject (ambiguity).
    - Incorrect bean name specified in @Qualifier annotation.
    - Bean is defined with a different scope than expected (e.g., prototype vs singleton).
- Solutions
    - Ensure the class is annotated with the appropriate stereotype annotation.
    - Verify that the package containing the bean is included in component scanning.
    - Use @Qualifier to specify which bean to inject when multiple candidates exist.
    - Check for typos in bean names and ensure they match exactly.
    - Ensure that the bean's scope aligns with how it is being used.
    - Define a default bean using @Primary annotation if multiple beans of the same type exist.
#### @PostConstruct
- Used to annotate a method that should be executed after the bean's properties have been set and before the bean is put into service.
- Commonly used for initialization logic that depends on injected properties.
- Example
  ```java
        @Component
        public class MyBean {
            @Autowired
            private MyService myService;
            
            @PostConstruct
            public void init() {
                // Initialization logic here
                myService.setup();
            }
        }
  ```
#### @PreDestroy
- Used to annotate a method that should be executed just before the bean is destroyed by the container.
- Commonly used for cleanup logic, such as releasing resources or closing connections.
- Example
  ```java
        @Component
        public class MyBean {
            @PreDestroy
            public void cleanup() {
                // Cleanup logic here
                System.out.println("Bean is being destroyed");
            }
        }
#### @ConditionalOnProperty
- Used to conditionally create a bean based on the presence and value of a specific property in the application configuration file.
- Commonly used to enable or disable certain beans based on environment-specific settings.
- Example
  ```java
        @Component
        @ConditionalOnProperty(prefix = "feature", value = "enabled", havingValue = "true", matchIfMissing = false)
        public class FeatureBean {
            // This bean will only be created if 'feature.enabled' property is set to 'true'
            // if matchIfMissing is true, the bean will be created if the property is missing
        }
  ```
- Configuration in application.properties or application.yml
  ```properties
    feature.enabled=true
    ```
- During @Autowiring, if the condition is not met, the bean will not be created, and any dependencies on it will result in an UnsatisfiedDependencyException unless handled appropriately.
- To handle optional dependencies, you can use @Autowired(required = false) or Optional<T> in the constructor or setter injection.
  ```java
        @Component
        public class MyComponent {
            private final FeatureBean featureBean;
            
            @Autowired
            public MyComponent(@Autowired(required = false) FeatureBean featureBean) {
                this.featureBean = featureBean;
            }
        }
  ```


