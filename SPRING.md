
# Contents
[Microservice vs service oriented architectures (SOA)](#Microservice-vs-service-oriented-architectures-SOA) | [RestTemplate](#RestTemplate) | [WebClient](#WebClient)

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
