# 

**[Spring Boot Cloud	1](#spring-boot-cloud)**

[Spring Boot Microservices: Synchronous Communication using RestTemplate | KeepAliveCache:	1](#spring-boot-microservices:-synchronous-communication-using-resttemplate-|-keepalivecache:)

[RestClient in Spring Boot Microservices | Synchronous Communication using RestClient | Fluent API	5](#restclient-in-spring-boot-microservices-|-synchronous-communication-using-restclient-|-fluent-api)

[FeignClient in Spring Boot Microservices | Synchronous Communication using Declarative HTTP Client	11](#feignclient-in-spring-boot-microservices-|-synchronous-communication-using-declarative-http-client)

[Service Discovery in Microservices | Eureka & its SpringBoot Implementation	20](#service-discovery-in-microservices-|-eureka-&-its-springboot-implementation)

[Client-Side Load Balancer in Microservices (Spring Cloud LoadBalancer)	26](#client-side-load-balancer-in-microservices-\(spring-cloud-loadbalancer\))

# 

# Spring Boot Cloud {#spring-boot-cloud}

---

## **Spring Boot Microservices: Synchronous Communication using `RestTemplate` | KeepAliveCache**: {#spring-boot-microservices:-synchronous-communication-using-resttemplate-|-keepalivecache:}

---

📦 Scenario

* One **client microservice** makes REST API calls to another **server microservice**.

* Communication is **synchronous** (the client waits for the response).

---

🔗 1\. Using `RestTemplate` for Synchronous Communication

java

CopyEdit

`@RestController`

`public class OrderController {`

    `@Autowired`

    `private RestTemplate restTemplate;`

    `@GetMapping("/place-order")`

    `public String placeOrder() {`

        `String response = restTemplate.getForObject("http://localhost:8081/payment", String.class);`

        `return "Order Placed. Payment Status: " + response;`

    `}`

`}`

**Explanation:**

* `RestTemplate` is used to send an HTTP GET request to another service (e.g., `payment-service`).

* The call is **blocking** — the controller waits for a response.

---

⚙️ 2\. Bean Configuration for `RestTemplate`

java

CopyEdit

`@Configuration`

`public class AppConfig {`

    `@Bean`

    `public RestTemplate restTemplate() {`

        `return new RestTemplate();`

    `}`

`}`

---

🚀 3\. What is `KeepAliveCache`?

* `KeepAliveCache` is part of the **JVM's HTTP connection management**, especially with older implementations like `HttpURLConnection`.

* It keeps **TCP connections alive** and **reuses them** across multiple HTTP calls to the same host.

* This avoids the overhead of establishing a new TCP connection for every request.

---

🔧 HTTP Keep-Alive Header

Under the hood, Keep-Alive works like this:

makefile

CopyEdit

`Connection: keep-alive`

`Keep-Alive: timeout=5, max=100`

This means:

* Reuse connection for **next 100 requests** or **5 seconds**, whichever comes first.

---

🧠 Why is it important in Microservices?

* Reduces **latency** and **resource usage**.

* Especially helpful when microservices are frequently calling each other (e.g., Order → Payment → Inventory).

---

❗ Caution: Potential Pitfalls

* `RestTemplate` is thread-safe, but **connection pooling** is not enabled by default.

* For production systems, prefer using a `RestTemplate` backed by **Apache HttpClient** with a connection pool:

java

CopyEdit

`@Bean`

`public RestTemplate restTemplate() {`

    `HttpClient httpClient = HttpClientBuilder.create()`

        `.setMaxConnTotal(100)`

        `.setMaxConnPerRoute(20)`

        `.build();`

    `HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);`

    `return new RestTemplate(factory);`

`}`

---

✅ Summary

| Feature | Description |
| ----- | ----- |
| `RestTemplate` | Used for synchronous inter-service HTTP calls |
| `KeepAliveCache` | JVM feature to reuse TCP connections |
| Benefit | Faster response, less CPU/network overhead |
| For Production | Use Apache HttpClient with connection pooling |

---

## RestClient in Spring Boot Microservices | Synchronous Communication using RestClient | Fluent API {#restclient-in-spring-boot-microservices-|-synchronous-communication-using-restclient-|-fluent-api}

---

🚀 What is `RestClient`?

`RestClient` is the **modern, fluent-style** replacement for `RestTemplate`.

* Introduced in **Spring Boot 3.1+**

* Built on top of **`WebClient`**

* Offers a **simplified API** for synchronous HTTP calls.

---

🆚 `RestTemplate` vs `RestClient`

| Feature | `RestTemplate` | `RestClient` (Spring Boot 3.1+) |
| ----- | ----- | ----- |
| API Style | Verbose | Fluent / Clean |
| Underlying Engine | `HttpURLConnection` | Based on `WebClient` (reactive) |
| Thread-safe | Yes | Yes |
| Recommended? | ❌ Deprecated | ✅ Yes |

---

🔧 1\. Maven Dependency (Spring Boot 3+)

Make sure you’re using:

xml

CopyEdit

`<parent>`

    `<groupId>org.springframework.boot</groupId>`

    `<artifactId>spring-boot-starter-parent</artifactId>`

    `<version>3.1.0</version>`

`</parent>`

Also include:

xml

CopyEdit

`<dependency>`

    `<groupId>org.springframework.boot</groupId>`

    `<artifactId>spring-boot-starter-web</artifactId>`

`</dependency>`

---

🔨 2\. Define a `RestClient` Bean

java

CopyEdit

`@Configuration`

`public class AppConfig {`

    `@Bean`

    `public RestClient restClient(RestClient.Builder builder) {`

        `return builder.baseUrl("http://localhost:8081").build(); // You can override this later`

    `}`

`}`

---

🎯 3\. Use `RestClient` for Synchronous Communication

Example: OrderService calling PaymentService

java

CopyEdit

`@RestController`

`@RequestMapping("/order")`

`public class OrderController {`

    `@Autowired`

    `private RestClient restClient;`

    `@GetMapping("/place")`

    `public String placeOrder() {`

        `String paymentResponse = restClient.get()`

                `.uri("/payment/process")`

                `.retrieve()`

                `.body(String.class);`

        `return "Order placed successfully. Payment Status: " + paymentResponse;`

    `}`

`}`

---

📦 Full Example of Payment Controller (Service B)

java

CopyEdit

`@RestController`

`@RequestMapping("/payment")`

`public class PaymentController {`

    `@GetMapping("/process")`

    `public String processPayment() {`

        `return "Payment Successful";`

    `}`

`}`

---

🧪 Testing the Flow

1. Start **PaymentService** (port 8081).

2. Start **OrderService** (port 8080).

3. Call:

bash

CopyEdit

`GET http://localhost:8080/order/place`

✅ You’ll see:  
 `Order placed successfully. Payment Status: Payment Successful`

---

🧠 Bonus: Using Inline Base URL

java

CopyEdit

`RestClient client = RestClient.create("http://localhost:8081");`

`String response = client.get()`

    `.uri("/payment/process")`

    `.retrieve()`

    `.body(String.class);`

---

✅ Summary

| Feature | Supported in `RestClient` |
| ----- | ----- |
| Fluent API | ✅ |
| Sync HTTP calls | ✅ |
| Custom headers, params | ✅ |
| JSON → POJO conversion | ✅ |
| Async / Reactive support | ❌ (Use `WebClient`) |

---

🔮 When to use `RestClient`?

Use `RestClient` if:

* You're on Spring Boot **3.1+**

* You want clean synchronous HTTP code

* You’re looking to **migrate away from `RestTemplate`**

---

## FeignClient in Spring Boot Microservices | Synchronous Communication using Declarative HTTP Client {#feignclient-in-spring-boot-microservices-|-synchronous-communication-using-declarative-http-client}

---

🔍 What is FeignClient?

**`@FeignClient`** is a declarative REST client provided by **Spring Cloud OpenFeign**.

* Eliminates boilerplate code (like using `RestTemplate` or `RestClient`)

* Simplifies inter-service HTTP communication

* Uses **interfaces** to define HTTP clients

---

📦 Dependency

To use `FeignClient`, add Spring Cloud and OpenFeign to your project.

1\. Update `pom.xml`:

xml

CopyEdit

`<dependency>`

    `<groupId>org.springframework.cloud</groupId>`

    `<artifactId>spring-cloud-starter-openfeign</artifactId>`

`</dependency>`

Also include the Spring Cloud BOM in `<dependencyManagement>`:

xml

CopyEdit

`<dependencyManagement>`

    `<dependencies>`

        `<dependency>`

            `<groupId>org.springframework.cloud</groupId>`

            `<artifactId>spring-cloud-dependencies</artifactId>`

            `<version>2022.0.3</version> <!-- Match Spring Boot version -->`

            `<type>pom</type>`

            `<scope>import</scope>`

        `</dependency>`

    `</dependencies>`

`</dependencyManagement>`

---

🧩 Enable Feign Client in Main Class

java

CopyEdit

`@SpringBootApplication`

`@EnableFeignClients`

`public class OrderServiceApplication {`

    `public static void main(String[] args) {`

        `SpringApplication.run(OrderServiceApplication.class, args);`

    `}`

`}`

---

🏗️ Define a Feign Client Interface

Let’s say `OrderService` wants to call `PaymentService`.

java

CopyEdit

`@FeignClient(name = "payment-service", url = "http://localhost:8081")`

`public interface PaymentClient {`

    `@GetMapping("/payment/process")`

    `String processPayment();`

`}`

---

📞 Use Feign Client in Controller

java

CopyEdit

`@RestController`

`@RequestMapping("/order")`

`public class OrderController {`

    `@Autowired`

    `private PaymentClient paymentClient;`

    `@GetMapping("/place")`

    `public String placeOrder() {`

        `String status = paymentClient.processPayment();`

        `return "Order placed. Payment status: " + status;`

    `}`

`}`

---

🧪 Output

When you hit:

bash

CopyEdit

`GET http://localhost:8080/order/place`

If `payment-service` on port `8081` returns `"Payment Successful"`,  
 you’ll get:

css

CopyEdit

`Order placed. Payment status: Payment Successful`

---

✅ Advantages of FeignClient

| Feature | Supported |
| ----- | ----- |
| Declarative REST client | ✅ |
| Load balancing (with Eureka) | ✅ |
| Circuit breaker support | ✅ |
| Easy integration with OpenAPI | ✅ |
| Cleaner than RestTemplate | ✅ |

---

🚨 Best Practices

* Use `@FeignClient(name="service-name")` with **Eureka Discovery** instead of hardcoded URLs.

* Add fallback logic with **Resilience4j** or **Spring Cloud Circuit Breaker**.

* Customize request interceptors for headers/logging.

---

🔁 Feign with Load Balancing (Optional)

If you use Eureka:

java

CopyEdit

`@FeignClient(name = "payment-service") // No need for URL`

`public interface PaymentClient {`

    `@GetMapping("/payment/process")`

    `String processPayment();`

`}`

Spring Cloud will resolve `payment-service` from Eureka.

---

🧠 Summary

| Property | FeignClient |
| ----- | ----- |
| Style | Declarative interface |
| Communication Type | Synchronous HTTP |
| Replaces | RestTemplate / RestClient |
| Integration | Eureka, Resilience4j, etc. |

---

**Retry/Timeout config with Feign**

---

🔧 1\. Timeout Configuration

Feign uses **Ribbon** (older) or **Spring Cloud LoadBalancer** (newer) \+ `HttpClient`/`OkHttp`.

✅ Add to `application.yml` or `application.properties`

yaml

CopyEdit

`feign:`

  `client:`

    `config:`

      `default:`

        `connectTimeout: 5000     # milliseconds`

        `readTimeout: 5000        # milliseconds`

> You can also configure per-client:

yaml

CopyEdit

`feign:`

  `client:`

    `config:`

      `payment-service:  # Feign client name`

        `connectTimeout: 3000`

        `readTimeout: 3000`

---

🔁 2\. Retry Configuration

By default, Feign **retries** on network errors but **not on 5xx or 4xx**.

✅ Enable and configure retry:

yaml

CopyEdit

`feign:`

  `client:`

    `config:`

      `default:`

        `retryer:`

          `period: 100     # Initial interval between retries (ms)`

          `maxPeriod: 1000 # Max backoff (ms)`

          `maxAttempts: 3  # Total attempts (1 original + 2 retries)`

> Note: `retryer` properties only work if you **don’t override with a custom Retryer bean**.

---

🧪 3\. Custom Retryer Bean (Optional)

java

CopyEdit

`@Bean`

`public Retryer retryer() {`

    `// period, maxPeriod, maxAttempts`

    `return new Retryer.Default(100, 1000, 3);`

`}`

You can disable retry by using:

java

CopyEdit

`@Bean`

`public Retryer neverRetry() {`

    `return Retryer.NEVER_RETRY;`

`}`

---

🧰 4\. Feign Logger for Debugging Retries

To see retry attempts and timeouts:

yaml

CopyEdit

`logging:`

  `level:`

    `feign: DEBUG`

---

🧠 Summary Table

| Property | Purpose |
| ----- | ----- |
| `connectTimeout` | Time to establish connection |
| `readTimeout` | Time to read response |
| `maxAttempts` | Total attempts (including 1st) |
| `period` | Delay between retries |
| `maxPeriod` | Max delay |

---

## Service Discovery in Microservices | Eureka & its SpringBoot Implementation {#service-discovery-in-microservices-|-eureka-&-its-springboot-implementation}

---

🔍 What is Service Discovery?

In microservices, services are often **dynamic**—they start/stop, scale up/down. Hardcoding IPs or URLs makes communication fragile.

**Service Discovery** solves this by:

* Registering services automatically

* Allowing clients to discover services dynamically

---

🧭 Eureka Overview

**Eureka** is a Service Discovery Server by **Netflix** (integrated into Spring Cloud).

* **Eureka Server**: Central registry where services register.

* **Eureka Client**: Microservice that registers itself with the Eureka Server.

---

⚙️ Eureka Architecture

css

CopyEdit

`[Order Service] <---> [Eureka Server] <---> [Payment Service]`

                              `↑`

                    `Dynamic registry of instances`

---

✅ Step-by-Step Spring Boot Setup

1\. 🎯 Create Eureka Server

Add Dependencies (`pom.xml`):

xml

CopyEdit

`<dependency>`

    `<groupId>org.springframework.cloud</groupId>`

    `<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>`

`</dependency>`

Enable Eureka Server:

java

CopyEdit

`@SpringBootApplication`

`@EnableEurekaServer`

`public class EurekaServerApp {`

    `public static void main(String[] args) {`

        `SpringApplication.run(EurekaServerApp.class, args);`

    `}`

`}`

`application.yml`:

yaml

CopyEdit

`server:`

  `port: 8761`

`eureka:`

  `client:`

    `register-with-eureka: false`

    `fetch-registry: false`

Access dashboard:  
 `http://localhost:8761`

---

2\. 🧩 Register Microservice as Eureka Client

Add to `order-service`, `payment-service`, etc.

Add Dependencies:

xml

CopyEdit

`<dependency>`

    `<groupId>org.springframework.cloud</groupId>`

    `<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>`

`</dependency>`

Enable Eureka Client:

> No need for `@EnableEurekaClient` from Spring Boot 2.1+, it's auto-configured.

`application.yml`:

yaml

CopyEdit

`server:`

  `port: 8081`

`spring:`

  `application:`

    `name: order-service`

`eureka:`

  `client:`

    `service-url:`

      `defaultZone: http://localhost:8761/eureka`

---

3\. 📞 Inter-Service Communication with Service Discovery

Instead of calling:

bash

CopyEdit

`http://localhost:8082/payment/process`

Use **FeignClient** or **RestTemplate** with **service name**:

java

CopyEdit

`@FeignClient(name = "payment-service")`

`public interface PaymentClient {`

    `@GetMapping("/payment/process")`

    `String processPayment();`

`}`

> Eureka will resolve the actual IP and port.

---

🔁 Optional Load Balancing

Add:

xml

CopyEdit

`<dependency>`

  `<groupId>org.springframework.cloud</groupId>`

  `<artifactId>spring-cloud-starter-loadbalancer</artifactId>`

`</dependency>`

And inject:

java

CopyEdit

`@Autowired`

`@LoadBalanced`

`private RestTemplate restTemplate;`

Now you can do:

java

CopyEdit

`restTemplate.getForObject("http://payment-service/payment/process", String.class);`

---

🔐 Bonus: Health Checks & Metadata

* Eureka uses `/actuator/health` to determine instance status.

* Add `spring-boot-starter-actuator` to expose endpoints.

---

✅ Summary

| Component | Role |
| ----- | ----- |
| Eureka Server | Central registry |
| Eureka Client | Registers itself with server |
| Feign/RestTemplate | Uses service name to communicate |
| Load Balancer | Picks a healthy instance |

---

## Client-Side Load Balancer in Microservices (Spring Cloud LoadBalancer) {#client-side-load-balancer-in-microservices-(spring-cloud-loadbalancer)}

---

1\. What is a Load Balancer?

* **Definition:** Distributes traffic across multiple service instances → avoids overloading a single server.

* **Purpose:** High availability, fault tolerance, scalability.

![][image1]

---

2\. Types of Load Balancer

1. **Server-Side Load Balancer**

   * Centralized load balancer (e.g., Nginx, API Gateway).

   * Client sends request → Load balancer server forwards to target service instance.

   * Client has **no logic of balancing**.

2. **Client-Side Load Balancer**

   * Load balancing logic exists **inside the client** (Spring Boot app).

   * Workflow:

     1. Client queries **Service Discovery (Eureka)** → gets list of instances.

     2. Client applies **load balancing algorithm** (round robin, random, etc.).

     3. Request sent to chosen instance.

   * Example libraries: Netflix Ribbon (deprecated), **Spring Cloud LoadBalancer**, Service Mesh (Istio).

👉 **Pre-requisite:** Service Discovery (Eureka, Consul, etc.).

---

3\. Load Balancing with RestTemplate

* **Before Spring Cloud LoadBalancer:**

  * Used `DiscoveryClient` to fetch instances manually.

  * Example (bad practice): always picking instance `0`.

  * Needed to **write custom balancing logic** manually.

* **With Spring Cloud LoadBalancer:**

  * Add dependency: `spring-cloud-starter-loadbalancer`.

Configure RestTemplate bean with `@LoadBalanced` annotation:

 `@Bean`

`@LoadBalanced`

`public RestTemplate restTemplate() {`

    `return new RestTemplate();`

`}`

* 

Now, call service by **service name**, not IP:

 `restTemplate.getForObject("http://product-service/products/1", Product.class);`

*   
  * Internally handled by **LoadBalancerInterceptor**:

    * Calls service discovery.

    * Selects instance using algorithm.

    * Forwards request.

---

4\. Internal Flow (LoadBalancerInterceptor)

* On `RestTemplate.execute()` → interceptor invoked.

* Does two things:

  * Picks load balancing algorithm from **LoadBalancerClientFactory**.

  * Calls service discovery → fetches instances → applies algorithm → selects one.

* **Algorithms available by default:**

  * Round Robin (default).

  * Random.

* Can **write custom algorithm** if needed.

---

5\. Service ID & Algorithm Binding

* **Service ID** \= Application name in Eureka (e.g., `product-service`, `order-service`).

* Load balancing algorithm is **attached to a specific service ID**.

  * Example:

    * `product-service` → Random.

    * `order-service` → Round Robin.

    * `xyz-service` → Custom algorithm.

⚠️ One algorithm per service ID.  
 ⚠️ By default → **Round Robin for all services**.

---

6\. Overriding Algorithms

🔹 Per-Service Algorithm

Use `@LoadBalancerClient` annotation in client app:

 `@LoadBalancerClient(`

    `name = "product-service",`

    `configuration = ProductServiceLoadBalancerConfig.class`

`)`

* 

Config class defines algorithm (e.g., Random):

 `@Configuration`

`public class ProductServiceLoadBalancerConfig {`

    `@Bean`

    `ReactorServiceInstanceLoadBalancer randomLB(`

        `Environment env,`

        `LoadBalancerClientFactory factory`

    `) {`

        `String serviceId = env.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);`

        `return new RandomLoadBalancer(`

            `factory.getLazyProvider(serviceId, ServiceInstanceListSupplier.class),`

            `serviceId`

        `);`

    `}`

`}`

* 

🔹 Global Algorithm (Default for all services)

* Use `@LoadBalancerClients` with **default configuration**.

* Example: set Round Robin globally, but override Random only for `product-service`.

* Use `@ConditionalOnMissingBean` to prevent duplicate configs (avoids runtime exceptions).

---

7\. Writing Custom Load Balancer

* Extend `ReactorServiceInstanceLoadBalancer`.

* Implement `choose(Request request)` method.

* Logic:

  1. Call Service Discovery via `ServiceInstanceListSupplier`.

  2. Get list of instances.

  3. Apply custom algorithm → return chosen instance.

Example (pick first instance):

`public class CustomLoadBalancer implements ReactorServiceInstanceLoadBalancer {`

    `private final ServiceInstanceListSupplier supplier;`

    `private final String serviceId;`

    `public CustomLoadBalancer(ServiceInstanceListSupplier supplier, String serviceId) {`

        `this.supplier = supplier;`

        `this.serviceId = serviceId;`

    `}`

    `@Override`

    `public Mono<Response<ServiceInstance>> choose(Request request) {`

        `return supplier.get(request).next().map(instances -> {`

            `if (instances.isEmpty()) return new EmptyResponse();`

            `return new DefaultResponse(instances.get(0)); // custom logic`

        `});`

    `}`

`}`

---

8\. Load Balancing with Feign Client

* Feign client integrates automatically with **Spring Cloud LoadBalancer**.

Just annotate interface:

 `@FeignClient(name = "product-service")`

`public interface ProductClient {`

    `@GetMapping("/products/{id}")`

    `Product getProductById(@PathVariable Long id);`

`}`

*   
* Service name (`product-service`) → resolved using service discovery \+ load balancer.

* Default algorithm \= Round Robin.

* Can override per-service algorithm same way as RestTemplate (`@LoadBalancerClient`).

---

9\. Key Interview Points

1. Difference between **server-side** vs **client-side** load balancer.

2. How Spring Cloud LoadBalancer works internally (`LoadBalancerInterceptor`).

3. Default algorithm → Round Robin.

4. Service ID concept (algorithm bound to service ID).

5. Overriding algorithm: per-service & global.

6. Writing custom load balancer.

7. Feign client also uses the same mechanism.

---

[image1]: <data:image/png;base64,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>