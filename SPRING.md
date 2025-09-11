
# Contents
[Microservice vs service oriented architectures (SOA)](#Microservice-vs-service-oriented-architectures-SOA) | [Pipe](#Pipe)

## Microservice-vs-service-oriented-architectures-SOA
SOA is an older architecture style where services are typically larger, coarse-grained, and rely on a central Enterprise Service Bus (ESB) for communication and orchestration. This often leads to bottlenecks and tighter coupling.

Microservices take the concept further by breaking applications into smaller, fine-grained services, each owning its data and deployed independently. They communicate using lightweight protocols (REST, gRPC, messaging) and are decentralized.

SOA is more suitable for legacy, enterprise-wide integration, while microservices are designed for cloud-native, scalable, CI/CD-friendly systems.

In short: SOA = centralized, coarse-grained, ESB-driven; Microservices = decentralized, fine-grained, independently deployable.