# 

**[Introduction to Spring boot | Its Advantage over Spring MVC and Servlets based Web applications	2](#introduction-to-spring-boot-|-its-advantage-over-spring-mvc-and-servlets-based-web-applications)**

[Servlets	2](#servlets)

[Spring MVC and Its Advantage Over Servlets	4](#spring-mvc-and-its-advantage-over-servlets)

[Spring Boot vs. Spring MVC: Key Differences and Advantages of Spring Boot	6](#spring-boot-vs.-spring-mvc:-key-differences-and-advantages-of-spring-boot)

[**Spring boot Project setup and Layered Architecture Introduction	9**](#spring-boot-project-setup-and-layered-architecture-introduction)

[Setting Up a Spring Boot Project Using Spring Initializr	9](#setting-up-a-spring-boot-project-using-spring-initializr)

[Spring Boot Layered Architecture Overview	12](#spring-boot-layered-architecture-overview)

[**Introduction to Maven and its Lifecycle	17**](#introduction-to-maven-and-its-lifecycle)

[**@Controller, @RestController, @RequestMapping etc.	19**](#@controller,-@restcontroller,-@requestmapping-etc.)

[**Bean and its Lifecycle | Inversion of Control (IOC)	24**](#bean-and-its-lifecycle-|-inversion-of-control-\(ioc\))

[**Dependency Injection in Spring boot | With Advantages and Disadvantages	29**](#dependency-injection-in-spring-boot-|-with-advantages-and-disadvantages)

[**Bean Scopes | Singleton, Prototype, Request, Session Scopes with Examples in Java	30**](#bean-scopes-|-singleton,-prototype,-request,-session-scopes-with-examples-in-java)

[**@ConditionalOnProperty Annotation in Spring Boot	36**](#@conditionalonproperty-annotation-in-spring-boot)

[**@Profile Annotation in Spring Boot	40**](#@profile-annotation-in-spring-boot)

# 

# **Introduction to Spring boot | Its Advantage over Spring MVC and Servlets based Web applications** {#introduction-to-spring-boot-|-its-advantage-over-spring-mvc-and-servlets-based-web-applications}

## **Servlets** {#servlets}

1. **Introduction to Spring Boot**  
   * **Context**: The speaker introduces the historical context leading up to Spring Boot's popularity by highlighting the issues with Servlets.  
   * **Problem Addressed**: Spring Boot aims to simplify web application development, making it easier and more efficient than traditional Servlet-based applications.  
2. **What are Servlets?**  
   * **Definition**: A Servlet is a Java class responsible for handling client requests, processing them, and returning a response.  
   * **Example**:  
     * The process involves defining APIs using Servlets, where requests are mapped to specific methods like `doGet()`, `doPost()`, etc.  
   * **Core Methods**:  
     * `doGet()`, `doPost()`, `doPut()`, `doDelete()`: Handle HTTP requests based on the method type.  
3. **What is a Servlet Container?**  
   * **Definition**: A Servlet Container (e.g., Apache Tomcat) manages the lifecycle of Servlets. It is responsible for:  
     * Handling incoming HTTP requests.  
     * Mapping requests to the appropriate Servlet and method.  
     * Sending back the processed response.  
4. **Servlet Workflow**  
   * **Web.xml Configuration**:  
     * Earlier, applications heavily relied on the `web.xml` file for Servlet mappings. This was essential for defining which URL patterns are handled by which Servlet.  
     * **Example**:  
       * Mapping `demoServlet1` to URL `/demoServlet1/firstEndpoint` means any GET request to this URL will invoke the `doGet()` method of `demoServlet1`.  
   * **Request Handling**:  
     * When a request hits the server, the Servlet container (e.g., Tomcat) consults the `web.xml` mappings.  
     * It identifies the correct Servlet to handle the request and invokes the respective method (like `doGet` for GET requests).  
5. **Issues with Servlet-based Development**  
   * **Configuration Overhead**:  
     * The manual configuration in `web.xml` can become cumbersome and error-prone as the application scales.  
   * **Tight Coupling**:  
     * The approach often results in tightly coupled code, making it harder to maintain and test.  
   * **Lack of Flexibility**:  
     * Adding or modifying endpoints requires manual changes in multiple places, increasing the risk of errors.  
6. **Introduction to Spring Framework**  
   * **Evolution from Servlets**:  
     * The Spring Framework emerged to solve many of these issues by providing a more organized and flexible way to handle web requests.  
   * **Spring MVC**:  
     * Before Spring Boot, Spring MVC was a popular module that simplified web development using Controllers instead of Servlets, reducing boilerplate code and enhancing maintainability.  
7. **Transition to Spring Boot**  
   * **Simplification with Spring Boot**:  
     * Spring Boot builds on top of the Spring Framework to further simplify application setup by providing:  
       * Auto-configuration  
       * Embedded server support (like Tomcat)  
       * Annotations-based configuration, eliminating the need for extensive XML configuration.

**Key Takeaways for Interviews**

* **Difference between Servlet and Spring Boot**:  
  * While Servlets are lower-level constructs for handling web requests, Spring Boot abstracts these details, making it easier to build, configure, and deploy web applications.  
* **Why Spring Boot over Servlets?**:  
  * **Ease of Development**: Auto-configuration and embedded servers simplify the process.  
  * **Convention over Configuration**: Reduces the need for explicit configurations.  
  * **Enhanced Testing**: Spring Boot's structure and annotations make it easier to write and maintain unit and integration tests.

---

## **Spring MVC and Its Advantage Over Servlets** {#spring-mvc-and-its-advantage-over-servlets}

**Issues with Servlet-based Development**:

* **Complex Configuration**: The traditional approach of configuring Servlets using `web.xml` becomes cumbersome, especially in large-scale applications with hundreds of Servlets. This XML file contains mappings, filters, and configuration, making it hard to manage as the application grows.  
* **Tight Coupling**: Direct instantiation of objects within classes leads to tight coupling. For instance, creating a new object inside a method (`new User()`) makes it difficult to mock during testing, thus complicating unit tests.

**Advantages of Spring MVC over Servlets**:

1. **Annotation-based Configuration**:  
   * **Simplified Setup**: Spring MVC eliminates the need for `web.xml` by using annotations like `@Controller`, `@RequestMapping`, and `@GetMapping`. This reduces boilerplate code and makes the setup more intuitive and maintainable.  
2. **Inversion of Control (IoC) and Dependency Injection**:  
   * **Definition**: IoC is a design principle where the control of object creation and dependency resolution is inverted from the developer to the Spring framework. Dependency Injection (DI) is a key implementation of IoC.  
   * **Example**:  
     * **Without IoC**: In a traditional Java class, an object is created manually (`User user = new User();`), leading to tight coupling.  
     * **With IoC**: Using Spring’s `@Autowired` annotation, Spring handles the object creation. The `Payment` class, for example, does not manually instantiate a `User` object but relies on Spring to inject it, reducing coupling.  
   * **Benefit for Testing**: This separation of object creation makes unit testing easier. Developers can mock dependencies (`User` in this case) without altering the `Payment` class, allowing for isolated testing of specific methods.  
3. **Simplified RESTful API Development**:  
   * **Path Mapping Flexibility**: In Servlet-based development, handling multiple endpoints within a single Servlet requires complex `if-else` structures for URL pattern matching.  
   * **Organized Request Handling**: Spring MVC allows developers to use annotations like `@GetMapping`, `@PostMapping`, etc., directly on methods to define endpoints. This leads to a cleaner and more organized way to handle HTTP requests without extensive conditionals.

| @RestControllerpublic class PaymentController {    @GetMapping("/payment/status")    public String getStatus() {        return "Payment Status OK";    }    @PostMapping("/payment/process")    public String processPayment() {        return "Payment Processed";    }} |
| :---- |

   * **Clear Mapping**: Each method is clearly mapped to a specific endpoint, making the code more readable and easier to maintain.  
4. **Integration with Various Technologies**:  
   * **Support for Different Tools**: Spring MVC supports integration with a wide range of technologies, including Hibernate, JPA for ORM, JUnit for unit testing, and many more. This allows developers to choose the best-suited technology stack for their requirements without worrying about compatibility issues.  
5. **DispatcherServlet and Request Handling**:  
   * **Centralized Controller**: The `DispatcherServlet` acts as the central controller in Spring MVC. It intercepts incoming HTTP requests and delegates them to the appropriate handler methods using the configured annotations (`@RequestMapping`, `@GetMapping`).  
   * **Process**:  
     * The request first goes to the `DispatcherServlet`.  
     * It uses `HandlerMapping` to determine which controller should handle the request.  
     * It then invokes the specific method in the identified controller based on the request mapping.  
6. **Flexible Deployment Options**:  
   * Spring MVC applications typically need to be deployed in a Servlet container like Apache Tomcat. You package your application as a WAR file and deploy it manually.

---

## Spring Boot vs. Spring MVC: Key Differences and Advantages of Spring Boot {#spring-boot-vs.-spring-mvc:-key-differences-and-advantages-of-spring-boot}

Spring Boot builds on top of Spring MVC to simplify the development process. Let's break down why Spring Boot was created and what challenges it solves compared to Spring MVC:

**1\. Dependency Management**

* **Spring MVC**:  
  * In traditional Spring MVC projects, you need to manage dependencies manually using `pom.xml` in Maven or `build.gradle` in Gradle.  
  * Each dependency version has to be specified, and updating one library can lead to compatibility issues. For example, updating JUnit might break compatibility with other libraries due to version conflicts.  
* **Spring Boot**:  
  * Spring Boot simplifies this with **Starter Dependencies**. For example, using `spring-boot-starter-web` automatically includes all necessary dependencies to set up a web application.  
  * You don't need to specify versions for dependencies explicitly. Spring Boot's **parent POM** manages compatible versions for you, reducing conflicts.

**Example**:

| \<dependency\>    \<groupId\>org.springframework.boot\</groupId\>    \<artifactId\>spring-boot-starter-web\</artifactId\>\</dependency\> |
| :---- |

* Here, Spring Boot automatically pulls the correct versions of dependencies like Spring Core, Spring MVC, Jackson, etc.

**Advantage**:

* No need to manage individual library versions.  
* Reduced risk of version conflicts.  
* Easier maintenance and upgrades.

2\. Auto Configuration

* **Spring MVC**:  
  * Requires explicit configuration, such as setting up the `DispatcherServlet`, defining component scanning, and specifying configurations in XML files or Java classes.

Example:

| xmlCopy code\<servlet\>    \<servlet-name\>dispatcher\</servlet-name\>    \<servlet-class\>org.springframework.web.servlet.DispatcherServlet\</servlet-class\>\</servlet\> |
| :---- |

*   
* **Spring Boot**:  
  * Introduces **Auto Configuration**, which automatically configures your Spring application based on the dependencies present on the classpath.  
  * The `@SpringBootApplication` annotation is a composite annotation that includes `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`.  
  * Spring Boot follows an **opinionated approach**, meaning it provides sensible defaults for configuration. However, developers can override these defaults if needed.

**Example**:

| javaCopy code@SpringBootApplicationpublic class Application {    public static void main(String\[\] args) {        SpringApplication.run(Application.class, args);    }} |
| :---- |

**Advantage**:

* Reduces boilerplate configuration code.  
* Faster development setup.  
* Convention over configuration: Developers follow Spring Boot’s default setup, but it can be customized.

3\. Embedded Server

* **Spring MVC**:  
  * Traditionally, Spring MVC applications are packaged as WAR files and deployed to an external servlet container like Tomcat or Jetty.  
  * This requires setting up a separate server environment and deploying the WAR manually.  
* **Spring Boot**:  
  * Comes with an **embedded server** (Tomcat, Jetty, or Undertow), allowing you to run the application directly without external deployment.  
  * The application can be packaged as a JAR file and executed using `java -jar`.

**Advantage**:

* Simplifies deployment and testing: Just run the JAR file, and the embedded server starts automatically.  
* Eliminates the need for an external server setup.  
* Supports faster development and testing cycles.

**Example**:

bash  
Copy code  
`java -jar myapp.jar`

Output:

scss  
Copy code  
`Tomcat started on port(s): 8080`

---

# Spring boot Project setup and Layered Architecture Introduction {#spring-boot-project-setup-and-layered-architecture-introduction}

### **Setting Up a Spring Boot Project Using Spring Initializr** {#setting-up-a-spring-boot-project-using-spring-initializr}

Spring Boot makes it incredibly easy to set up a new project, especially when using **Spring Initializr**. Here’s a step-by-step guide on how to create a Spring Boot project, followed by a brief introduction to layered architecture commonly used in Spring Boot applications.

Step 1: Visit Spring Initializr

* Open your browser and go to [Spring Initializr](https://start.spring.io/).  
* Spring Initializr is a web-based tool that allows you to quickly bootstrap a Spring Boot project with the necessary dependencies.

Step 2: Configure Project Settings

1. **Project Type**:  
   * Choose **Maven Project** or **Gradle Project** based on your preference.  
   * **Maven** is widely used and preferred for managing dependencies.  
2. **Language**:  
   * Select **Java** as the language.  
3. **Spring Boot Version**:  
   * Choose the latest stable version (e.g., `3.2.3`).  
4. **Project Metadata**:  
   * **Group**: Typically follows a reverse domain pattern, e.g., `com.conceptandcoding`.  
   * **Artifact**: This is the project name, e.g., `learning-spring-boot`.  
   * **Name** and **Description**: Provide meaningful names and descriptions for your project.  
   * **Package Name**: This will be generated based on the group and artifact, e.g., `com.conceptandcoding.learningspringboot`.  
5. **Packaging**:  
   * Choose **JAR** (Java Archive), which is common for microservices and standalone applications.  
   * **WAR** (Web Archive) is used when deploying to an external server, but with embedded servers, JAR is sufficient.  
6. **Why JAR over WAR?**  
   * **JAR** files are standalone and include an embedded server (Tomcat, Jetty, etc.). This makes deployment easier as you don’t need an external server.  
   * **WAR** files require deployment on a servlet container like Tomcat. This was common in traditional monolithic applications but is less common in microservices.  
7. **Java Version**:  
   * Choose a compatible version like Java **17** or **21**.

Step 3: Add Dependencies

* Click on **Add Dependencies** and select the following:  
  * **Spring Web**: For building RESTful APIs and web applications. It includes an embedded Tomcat server by default.  
  * You can add more dependencies like **Spring Data JPA** (for database integration), **Spring Security**, etc., as needed later.

Step 4: Generate the Project

* Click on the **Generate** button. The project will be downloaded as a ZIP file.  
* Extract the ZIP file to your desired location.

Step 5: Open the Project in an IDE

* Open your preferred IDE (IntelliJ IDEA, Eclipse, or VS Code).  
  * If you are using **IntelliJ IDEA**:  
    * **Ultimate Version**: It includes built-in Spring support. You can create Spring Boot projects directly from the IDE.  
    * **Community Version**: You need to import the project manually from the downloaded ZIP.  
  * Open the project by selecting the `pom.xml` file (for Maven projects) to import all dependencies automatically.

Step 6: Project Structure Overview

After importing, you will see the following basic structure:

| cssCopy code├── src│   └── main│       ├── java│       │   └── com│       │       └── conceptandcoding│       │           └── learningspringboot│       │               └── LearningSpringBootApplication.java│       └── resources│           ├── application.properties├── pom.xml |
| :---- |

*   
1. **LearningSpringBootApplication.java**:  
   * The entry point of the Spring Boot application.

| javaCopy codepackage com.conceptandcoding.learningspringboot;import org.springframework.boot.SpringApplication;import org.springframework.boot.autoconfigure.SpringBootApplication;@SpringBootApplicationpublic class LearningSpringBootApplication {    public static void main(String\[\] args) {        SpringApplication.run(LearningSpringBootApplication.class, args);    }} |
| :---- |

2.   
   * The `@SpringBootApplication` annotation enables auto-configuration and component scanning.  
3. **pom.xml**:  
   * It includes basic project dependencies. For example:

| xmlCopy code\<dependencies\>    \<dependency\>        \<groupId\>org.springframework.boot\</groupId\>        \<artifactId\>spring-boot-starter-web\</artifactId\>    \</dependency\>    \<dependency\>        \<groupId\>org.springframework.boot\</groupId\>        \<artifactId\>spring-boot-starter-test\</artifactId\>        \<scope\>test\</scope\>    \</dependency\>\</dependencies\> |
| :---- |

4. 

Step 7: Run the Application

You can run the application directly from your IDE using the `main` method in `LearningSpringBootApplication.java` or through the terminal using:

| mvn spring-boot:run |
| :---- |

* 

Output:  
scss  
Copy code  
`Tomcat started on port(s): 8080 (http)`

* 

Step 8: Verify the Setup

* Open a browser and go to `http://localhost:8080`. If no controller is defined, you might see a 404 error, but this means the server is running successfully.

---

### **Spring Boot Layered Architecture Overview** {#spring-boot-layered-architecture-overview}

Layered architecture is a widely adopted pattern in Spring Boot applications, especially in large organizations and enterprise-level applications. It promotes separation of concerns, maintainability, and scalability.

Here's a detailed breakdown of each layer and its components:

1\. Controller Layer

* **Purpose**: Acts as the entry point for client requests. It handles HTTP requests, processes input data, and returns HTTP responses.  
* **Annotations**:  
  * `@Controller`: Used for traditional MVC-based web applications.  
  * `@RestController`: Used for RESTful web services; combines `@Controller` and `@ResponseBody`.  
* **Responsibility**:  
  * Receives incoming HTTP requests.  
  * Calls the service layer to handle business logic.  
  * Returns HTTP responses to the client.

| @RestController@RequestMapping("/api/v1/payments")public class PaymentController {    @Autowired    private PaymentService paymentService;    @GetMapping("/{id}")    public ResponseEntity\<PaymentResponse\> getPaymentById(@PathVariable Long id) {        PaymentResponse response \= paymentService.getPaymentById(id);        return ResponseEntity.ok(response);    }} |
| :---- |

2\. Service Layer

* **Purpose**: Contains business logic. It processes requests and performs operations as per the business requirements.  
* **Annotations**:  
  * `@Service`: Marks the class as a service component in Spring's context.  
* **Responsibility**:  
  * Implements business logic.  
  * Calls the repository layer for database operations.  
  * Handles DTO (Data Transfer Object) mapping and validation.

| @Servicepublic class PaymentService {    @Autowired    private PaymentRepository paymentRepository;    public PaymentResponse getPaymentById(Long id) {        PaymentEntity paymentEntity \= paymentRepository.findById(id)                .orElseThrow(() \-\> new RuntimeException("Payment not found"));                // Map entity to response DTO        PaymentResponse response \= new PaymentResponse();        response.setPaymentId(paymentEntity.getId());        response.setAmount(paymentEntity.getAmount());        response.setCurrency(paymentEntity.getCurrency());        return response;    }} |
| :---- |

3\. Repository Layer

* **Purpose**: Interacts with the database. It fetches, updates, deletes, and saves data using JPA or JDBC.  
* **Annotations**:  
  * `@Repository`: Indicates that the class is a data repository.  
* **Responsibility**:  
  * Defines methods for data access and manipulation.  
  * Abstracts the database interactions.

| @Repositorypublic interface PaymentRepository extends JpaRepository\<PaymentEntity, Long\> {} |
| :---- |

4\. DTO (Data Transfer Object) Layer

* **Purpose**: Represents data that is exchanged between the client and server. It helps in mapping request/response data and ensures a consistent schema.  
* **Types**:  
  * **Request DTO**: Maps incoming request data.  
  * **Response DTO**: Structures outgoing response data.  
* **Advantages**:  
  * Prevents exposure of internal data structures.  
  * Provides flexibility to change internal models without affecting API contracts.

| public class PaymentRequest {    private Long id;    private BigDecimal amount;    private String currency;    // Getters and Setters}public class PaymentResponse {    private Long paymentId;    private BigDecimal amount;    private String currency;    // Getters and Setters} |
| :---- |

5\. Entity Layer

* **Purpose**: Represents database tables. Each entity class is mapped to a table using JPA annotations.  
* **Annotations**:  
  * `@Entity`: Indicates that the class is a JPA entity.  
* **Responsibility**:  
  * Defines fields corresponding to table columns.  
  * Used for ORM (Object Relational Mapping) with JPA/Hibernate.

| @Entity@Table(name \= "payments")public class PaymentEntity {    @Id    @GeneratedValue(strategy \= GenerationType.IDENTITY)    private Long id;    private BigDecimal amount;    private String currency;    // Getters and Setters} |
| :---- |

6\. Utility Layer

* **Purpose**: Contains helper or utility methods used across multiple layers. Examples include common date formatters, string manipulation functions, and logging utilities.  
* **Responsibility**:  
  * Provides reusable functions to avoid code duplication.

| public class DateUtil {    public static String formatDate(LocalDate date) {        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));    }} |
| :---- |

7\. Configuration Layer

* **Purpose**: Holds configuration settings for the application. Configurations are usually stored in the `application.properties` or `application.yml` file.  
* **Examples**:  
  * Database configuration  
  * Server port  
  * Logging levels

**`application.properties`**:

`server.port=8080`

`spring.datasource.url=jdbc:mysql://localhost:3306/mydb`

`spring.datasource.username=root`

`spring.datasource.password=pass`

**Custom Configuration Class**:  
java  
Copy code

| @Configurationpublic class AppConfig {    @Value("\${custom.config.value}")    private String configValue;    public String getConfigValue() {        return configValue;    }} |
| :---- |

---

# Introduction to Maven and its Lifecycle {#introduction-to-maven-and-its-lifecycle}

Maven automates much of the build process by following a standard lifecycle and using plugins. It provides flexibility to customize phases with additional tasks and allows for the management of dependencies and repositories. The build process runs sequentially through phases, and developers can configure and extend Maven to fit specific project needs.

Maven `pom.xml` Configuration:

1. **Dependencies**: Dependencies in `pom.xml` specify external libraries required by the project. These dependencies are resolved from remote repositories (such as Maven Central) and downloaded to your local repository.  
   * The `repositories` tag specifies the location (URL) where Maven should look for dependencies.  
   * In practice, Maven Central is usually the default repository, so the repository configuration may not need to be explicitly defined in every `pom.xml` file.  
   * The downloaded project dependencies are stored in the local repository, which helps speed up builds by not needing to fetch the same dependencies repeatedly.  
2. **Build Lifecycle**:  
   * The Maven build lifecycle consists of several phases, each corresponding to a specific task during the build process.  
   * These phases include:  
     1. **Validate** – Validates the project structure.  
     2. **Compile** – Compiles source code into bytecode.  
     3. **Test** – Runs unit tests on the compiled code.  
     4. **Package** – Packages the compiled code into a JAR (or other specified formats like WAR).  
     5. **Verify** – Verifies the integrity of the package.  
     6. **Install** – Installs the package into the local repository.  
     7. **Deploy** – Deploys the package to a remote repository.

Execution Flow:

* When you run a specific phase (like `mvn package`), Maven executes that phase along with all previous phases (like `mvn compile`, `mvn test`).  
* Each phase can have multiple goals (tasks), and they are executed sequentially.

Customizing the Build with Plugins:

* **Plugins**: You can enhance each phase with custom tasks using plugins. For example, you can add a plugin to the **validate** phase to check code style or perform static code analysis before compiling the code.  
* Plugins are added under the `<build>` section in `pom.xml`, where you specify the plugin and its configuration (such as which goal/task to execute).  
* Example: Adding a `checkstyle` plugin to validate code style or a `pmd` plugin for static analysis in the **verify** phase.

Build Commands:

* **`mvn compile`**: Compiles the source code into bytecode and places the `.class` files in the target directory.  
* **`mvn test`**: Runs unit tests after compiling the code.  
* **`mvn package`**: Packages the compiled code into a JAR or WAR file.  
* **`mvn install`**: Installs the JAR or WAR into the local repository.  
* **`mvn deploy`**: Deploys the built JAR/WAR to a remote repository (could be a company's private repository or Maven Central).

Local and Remote Repositories:

* **Local Repository**: Located on your machine, typically under `~/.m2/repository`. This stores all your project dependencies and built artifacts.  
* **Remote Repository**: Could be Maven Central or a company’s private repository. Remote repositories store shared dependencies and artifacts that can be accessed by other developers.  
* Maven first looks for dependencies in the **local repository**. If the required dependency is not found, it will attempt to download it from a **remote repository**.

`settings.xml` Configuration:

* **Local Repository Path**: The location of the local repository is configured in `settings.xml`, which is located in the `.m2` directory. You can customize this path if necessary.  
* **Authentication for Remote Repositories**: For deploying artifacts to remote repositories, you may need to provide authentication (username/password), which is configured in the `settings.xml` file under the `<servers>` section.

Phases in Detail:

1. **Validate**: Checks the project structure but does not enforce rules unless plugins are added.  
2. **Compile**: Converts Java source code into bytecode (`.class` files). No need to specify how to compile (e.g., Java compiler), as Maven uses built-in plugins.  
3. **Test**: Runs unit tests after compiling the code. The tests are executed using the configured test framework (e.g., JUnit).  
4. **Package**: Packages the project into a distributable format, typically JAR or WAR. The package is created in the `target` directory.  
5. **Verify**: A phase for additional checks, such as static code analysis (using tools like PMD).  
6. **Install**: Copies the packaged artifact (JAR) into your local Maven repository so it can be reused by other projects on the same system.  
7. **Deploy**: Deploys the artifact to a remote repository (e.g., Maven Central or a company’s internal repository) to make it available to other developers or projects.

---

# @Controller, @RestController, @RequestMapping etc. {#@controller,-@restcontroller,-@requestmapping-etc.}

1\. Controller and RestController Annotation

* **`@Controller` Annotation:**  
  * **Purpose:** This annotation is used to define a Spring MVC controller, typically for handling traditional web requests (e.g., returning HTML views). By default, it expects to return a view name (i.e., a template name) rather than directly returning the response body.

**Example:**  
java  
Copy code

| @Controllerpublic class UserController {        @GetMapping("/user")    public String getUser(Model model) {        model.addAttribute("username", "Shan");        return "userView";  // Name of the view (like a JSP or HTML page)    }} |
| :---- |

*   
  * **Explanation:** When a user visits `/user`, it will return the view `userView` (e.g., a JSP or HTML file), and the model attribute `username` will be passed to that view.  
* **`@RestController` Annotation:**  
  * **Purpose:** A specialized version of `@Controller` that combines `@Controller` and `@ResponseBody`. This annotation is used for building RESTful APIs where every method’s return value is written directly to the HTTP response body (usually in JSON or XML format).

**Example:**  
java  
Copy code

| @RestControllerpublic class UserRestController {        @GetMapping("/user")    public User getUser() {        return new User("Shan", "shan@example.com"); // Will be converted to JSON    }} |
| :---- |

* **Explanation:** When a user visits `/user`, it returns a `User` object, which is automatically converted to a JSON response (due to the `@RestController`).

**Key Difference Between `@Controller` and `@RestController`:**

* **`@Controller`** is typically used for rendering views (like HTML pages) and does **not** automatically convert the return value to JSON/XML.  
* **`@RestController`** is used for RESTful APIs and **automatically serializes** the return value into JSON (or XML if specified).

2\. RequestParam Annotation

* **Purpose:** Used to extract query parameters from the request URL and bind them to method parameters in the controller.  
* **Example:**  
  * URL: `/api/sl/fetchUser?firstName=Shan&lastName=Smith`

Method:  
java  
Copy code

| @GetMapping("/fetchUser")public String fetchUser(@RequestParam String firstName, @RequestParam(required \= false) String lastName) {    return "Fetch user details of " \+ firstName \+ " " \+ (lastName \!= null ? lastName : "No last name provided");} |
| :---- |

*   
* **Explanation:** The query parameters `firstName` and `lastName` are extracted and mapped to method parameters.

3\. InitBinder Annotation

* **Purpose:** Used for custom type conversion or pre-processing of request parameters before they are bound to the method parameters.  
* **Example:**

Custom editor to convert and normalize a `firstName` parameter to lowercase and trim it.  
java  
Copy code

| @InitBinderpublic void initBinder(WebDataBinder binder) {    binder.registerCustomEditor(String.class, new MyFirstNameEditor());} |
| :---- |

*   
* **Explanation:** This ensures that input parameters are processed (e.g., trimmed and converted to lowercase) before being assigned to method parameters.

4\. PathVariable Annotation

* **Purpose:** Extracts values from the URL path (rather than query parameters) and binds them to method parameters.  
* **Example:**  
  * URL: `/api/sl/fetchUser/12345`

Method:  
java  
Copy code

| @GetMapping("/fetchUser/{id}")public String fetchUser(@PathVariable String id) {    return "User ID: " \+ id;} |
| :---- |

*   
* **Explanation:** The `{id}` part of the URL path is extracted and bound to the `id` method parameter.

5\. RequestBody Annotation

* **Purpose:** Binds the HTTP request body (often in JSON or XML format) to a method parameter, typically a Java object.  
* **Example:**

Request Body (JSON):  
json  
Copy code

| {  "username": "Shan",  "email": "shan@example.com"} |
| :---- |

* 

Method:  
java  
Copy code

| @PostMapping("/saveUser")public String saveUser(@RequestBody User user) {    return "User saved: " \+ user.getUsername();} |
| :---- |

*   
* **Explanation:** The `@RequestBody` annotation automatically maps the incoming JSON data to the `User` object.

6\. ResponseEntity Annotation

* **Purpose:** Represents the entire HTTP response, including the body, status, and headers.  
* **Example:**

Method:  
java  
Copy code

| @GetMapping("/fetchUser")public ResponseEntity\<String\> fetchUser() {    return ResponseEntity.status(HttpStatus.OK)                         .body("User details fetched successfully");} |
| :---- |

*   
* **Explanation:** This allows more control over the response, including setting HTTP status codes and headers in addition to the body content.

---

# Bean and its Lifecycle | Inversion of Control (IOC) {#bean-and-its-lifecycle-|-inversion-of-control-(ioc)}

In Spring Boot, the concepts of **Beans**, **Bean Lifecycle**, and **Inversion of Control (IOC)** form the backbone of how Spring manages objects and dependencies within the application. Let's explore these concepts in detail:

1\. Inversion of Control (IOC)

* **Definition:**  
  * Inversion of Control (IOC) is a design principle in which the control of object creation and dependency management is shifted from the application to the framework. In traditional programming, the application code controls how objects are created and dependencies are injected. In contrast, with IOC, the framework (Spring in this case) controls object creation and dependency injection.  
* **How IOC Works in Spring:**  
  * Spring manages the creation, configuration, and wiring of objects (beans) in the application through its **ApplicationContext**. The context holds and manages the lifecycle of beans and handles dependency injection (DI) automatically.  
* **Dependency Injection (DI):**  
  * DI is a form of IOC where the Spring framework injects required dependencies into classes. This is done by either constructor injection, setter injection, or field injection.

**Example of Dependency Injection (DI):**  
java  
Copy code

| @Componentpublic class Car {    private Engine engine;        // Constructor Injection (recommended)    @Autowired    public Car(Engine engine) {        this.engine \= engine;    }        public void start() {        engine.run();    }}@Componentpublic class Engine {    public void run() {        System.out.println("Engine is running");    }} |
| :---- |

* **Explanation:** Spring injects the `Engine` bean into the `Car` bean at runtime via constructor injection.

---

2\. Beans in Spring

* **Definition:**  
  * A **Bean** in Spring is simply an object that is instantiated, assembled, and managed by the Spring IoC container. In Spring Boot, beans can be defined using annotations like `@Component`, `@Service`, `@Repository`, or `@Controller` depending on the type of class.  
* **Types of Beans:**  
  * **Singleton Beans (default):** Only one instance of the bean is created in the Spring container. This instance is shared throughout the application.  
  * **Prototype Beans:** A new instance of the bean is created every time it is requested from the Spring container.  
  * **Request and Session Scoped Beans:** These beans are tied to the HTTP request and session lifecycle, respectively.

**Defining Beans:** You can define beans in Spring Boot using annotations like `@Component`, `@Service`, `@Repository`, `@Controller`, etc.  
**Example:**  
java  
Copy code

| @Servicepublic class UserService {    public String getUserInfo() {        return "User information";    }} |
| :---- |

* **Explanation:** The `UserService` class is a Spring-managed bean, and Spring will manage its lifecycle.  
* **Configuration-based Bean Definition:**  
  * You can also define beans using Java-based configuration with `@Configuration` and `@Bean` annotations.

**Example:**  
java  
Copy code

| @Configurationpublic class AppConfig {    @Bean    public Engine engine() {        return new Engine();    }    @Bean    public Car car() {        return new Car(engine());    }} |
| :---- |

3\. Bean Lifecycle in Spring

Spring beans go through several lifecycle stages, from creation to destruction. The lifecycle of a Spring Bean is managed by the Spring container, which follows the **BeanFactory** and **ApplicationContext** lifecycle phases.

* **Lifecycle Phases:**  
  * **Instantiation:** The Spring container creates an instance of the bean (using the class constructor).  
  * **Populate Properties (Dependency Injection):** After instantiation, Spring injects any required dependencies into the bean using DI.  
  * **Bean Initialization:**  
    * If the bean implements `InitializingBean`, the `afterPropertiesSet()` method is called.  
    * If the bean has a custom initialization method (specified using `@PostConstruct` or XML configuration), that method is called.  
  * **Bean Ready for Use:** At this point, the bean is fully initialized and ready for use in the application.  
  * **Destruction:**  
    * If the bean implements `DisposableBean`, the `destroy()` method is called when the application context is closed.  
    * Alternatively, if a custom destroy method is specified using `@PreDestroy`, it will be invoked.  
* **Lifecycle Annotations:**  
  * **`@PostConstruct`:** Marks a method to be called after the bean’s properties have been set and the bean is ready to be used. This is equivalent to `afterPropertiesSet()` in `InitializingBean`.  
  * **`@PreDestroy`:** Marks a method to be called before the bean is destroyed. This is equivalent to `destroy()` in `DisposableBean`.

**Example:**  
java  
Copy code

| @Componentpublic class UserService {    @PostConstruct    public void init() {        System.out.println("UserService initialized");    }    @PreDestroy    public void cleanup() {        System.out.println("UserService destroyed");    }} |
| :---- |

* **Explanation:** The `init()` method is called once the `UserService` bean is created, and the `cleanup()` method is called before the bean is destroyed.

**Custom Initialization and Destruction:** You can specify custom initialization and destruction methods for beans using annotations or through configuration in Java or XML.  
**Example:**  
java  
Copy code

| @Component@Scope("singleton")  // Default scopepublic class Car {    public void start() {        System.out.println("Car is starting");    }    @PostConstruct    public void init() {        System.out.println("Car initialized");    }    @PreDestroy    public void destroy() {        System.out.println("Car destroyed");    }} |
| :---- |

4\. Managing Bean Lifecycle Using `@Bean` and `@Configuration`

* **`@Bean` method lifecycle:** When using `@Configuration` and `@Bean` to define beans, the Spring container manages the lifecycle of these beans as well. The lifecycle stages (initialization, destruction, etc.) are handled the same way as beans defined through annotations like `@Component`.

**Example using `@Configuration`**:  
java  
Copy code

| @Configurationpublic class AppConfig {    @Bean(initMethod \= "init", destroyMethod \= "cleanup")    public Car car() {        return new Car();    }} |
| :---- |

* In this case, the `Car` bean will go through the same lifecycle phases, with custom initialization (`init()`) and destruction (`cleanup()`).

---

# Dependency Injection in Spring boot | With Advantages and Disadvantages {#dependency-injection-in-spring-boot-|-with-advantages-and-disadvantages}

1\. Field Injection:

* **Explanation**: Dependencies are injected directly into fields via the `@Autowired` annotation, without the need for setter or constructor methods. Spring automatically handles the injection during object creation.  
* **Advantages**:  
  * **Simplicity**: It’s easy to use and requires less boilerplate code, making it quick and convenient to inject dependencies.  
  * **Less Code**: No need to write getter, setter, or constructor methods for dependency injection.  
* **Disadvantages**:  
  * **Harder to Test**: Testing is more difficult because dependencies are injected automatically, and you cannot easily mock or replace dependencies.  
  * **Difficult to Maintain**: Since the dependencies are injected into fields, it’s harder to know whether the field is being managed by Spring. This can lead to maintenance challenges.  
  * **Immutability**: Cannot use `final` on fields, making it impossible to make the fields immutable.  
  * **Potential for Hidden Dependencies**: It can be harder to detect which fields are injected, leading to code that’s less transparent and more prone to errors.  
* **Field Injection** is often considered the least preferred way to perform dependency injection because it is more difficult to maintain and harder to test. It’s useful for quick prototypes or when a class has minimal dependencies, but it doesn’t offer the same benefits as Constructor Injection in terms of immutability and fail-fast behavior.

2\. Setter Injection:

* **Explanation**: Dependency is set via a setter method. The `@Autowired` annotation is used on the setter, not the field.  
* **Advantages**:  
  * **Flexibility**: Dependencies can be changed after object creation (e.g., switching between different types of orders like online or offline).  
  * **Ease of Testing**: Can easily pass mock dependencies via the setter method.  
* **Disadvantages**:  
  * **Cannot Use `final` Fields**: The field cannot be made immutable, as it can be changed after object creation.  
  * **Harder to Maintain**: It’s difficult to track if a field is managed by Spring because you need to check for the setter method marked with `@Autowired`.  
  * **Readability Issues**: It’s less readable and maintainable than other types of injection.

3\. Constructor Injection:

* **Explanation**: Dependencies are injected at the time of object creation, either via constructor parameters or automatically by Spring if there is only one constructor.  
* **Advantages**:  
  * **Mandatory Dependencies**: Ensures all mandatory dependencies are set at object creation.  
  * **Immutability**: Can use `final` on fields, making them immutable.  
  * **Fail Fast**: If a dependency is missing, the application fails early during the initialization phase, preventing runtime errors.  
  * **Easier Unit Testing**: Dependencies can be easily mocked and injected through the constructor.  
* **Disadvantages**:  
  * **Long Constructors**: If a class has many dependencies, the constructor can become unwieldy, which may signal that refactoring is needed.

4\. Circular Dependency:

* **Problem**: When two or more classes depend on each other, forming a circular reference (e.g., `Order` depends on `Invoice` and vice versa).  
* **Solutions**:  
  1. **Refactor Code**: Break the cycle by introducing a utility class for shared logic.  
  2. **`@Lazy` Annotation**: Mark one of the dependencies as lazy-loaded, so Spring resolves them when needed, not at startup.  
  3. **`@PostConstruct` Method**: Use `@PostConstruct` to manually set the dependencies after initialization.

5\. Unsatisfied Dependency:

* **Problem**: When Spring cannot resolve which bean to inject when there are multiple candidates (e.g., `OnlineOrder` and `OfflineOrder` both implement `Order`).  
* **Solutions**:  
  1. **`@Primary` Annotation**: Mark one of the beans as primary, so Spring automatically injects it when multiple candidates exist.  
  2. **`@Qualifier` Annotation**: Specify the exact bean to inject using the `@Qualifier` annotation, referencing the name of the bean.

---

# Bean Scopes | Singleton, Prototype, Request, Session Scopes with Examples in Java {#bean-scopes-|-singleton,-prototype,-request,-session-scopes-with-examples-in-java}

In **Spring Boot**, beans are managed by the Spring **Application Context**, and their **scopes** define the lifecycle and visibility of those beans within the application. The most common bean scopes are:

1. **Singleton**  
2. **Prototype**  
3. **Request**  
4. **Session**

Each of these scopes has its own characteristics and is useful in different scenarios. Let's dive into the details of each with examples:

1\. Singleton Scope

* **Default Scope** in Spring Boot.  
* **Explanation**: A single instance of the bean is created and shared across the entire application context. The same instance is injected wherever the bean is required.  
* **Use Case**: Use Singleton when the bean is stateless, and it can be shared across different components or requests.

**Example**:

| import org.springframework.stereotype.Component;@Componentpublic class SingletonBean {    public SingletonBean() {        System.out.println("SingletonBean instance created.");    }    public void showMessage() {        System.out.println("Hello from Singleton Bean\!");    }} |
| :---- |

**Usage**:

| import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Component;@Componentpublic class SingletonClient {    @Autowired    private SingletonBean singletonBean;    public void execute() {        singletonBean.showMessage();    }} |
| :---- |

In the above example, **`SingletonBean`** is a singleton, and the same instance will be injected into **`SingletonClient`** across multiple invocations.

2\. Prototype Scope

* **Explanation**: A new instance of the bean is created each time it is requested from the Spring container.  
* **Use Case**: Use Prototype when the bean contains state, and each request should get a fresh, independent instance.

**Example**:

| import org.springframework.context.annotation.Scope;import org.springframework.stereotype.Component;@Component@Scope("prototype")  // Specify Prototype scopepublic class PrototypeBean {    public PrototypeBean() {        System.out.println("PrototypeBean instance created.");    }    public void showMessage() {        System.out.println("Hello from Prototype Bean\!");    }} |
| :---- |

**Usage**:

| import org.springframework.beans.factory.annotation.Autowired;import org.springframework.context.ApplicationContext;import org.springframework.stereotype.Component;@Componentpublic class PrototypeClient {    @Autowired    private ApplicationContext context;    public void execute() {        PrototypeBean prototypeBean1 \= context.getBean(PrototypeBean.class);        prototypeBean1.showMessage();        PrototypeBean prototypeBean2 \= context.getBean(PrototypeBean.class);        prototypeBean2.showMessage();        System.out.println("Are both beans the same instance? " \+ (prototypeBean1 \== prototypeBean2));  // Should print false    }} |
| :---- |

In this case, **`PrototypeBean`** will create a new instance each time it is requested, so **`prototypeBean1`** and **`prototypeBean2`** will be different instances.

3\. Request Scope

* **Explanation**: A new instance of the bean is created for each HTTP request. This scope is typically used in **web applications** where the bean is tied to a specific HTTP request.  
* **Use Case**: Use Request scope for beans that hold request-specific state, such as user data or request-specific parameters.

**Example**:

| import org.springframework.context.annotation.Scope;import org.springframework.stereotype.Component;@Component@Scope("request")  // Specify Request scopepublic class RequestBean {    public RequestBean() {        System.out.println("RequestBean instance created for each request.");    }    public void showMessage() {        System.out.println("Hello from Request Bean\!");    }} |
| :---- |

**Usage**:

In a Spring Boot web application, the Request scope works automatically when the request is processed. You don’t need to call anything directly in your service class; Spring will handle the injection during the HTTP request lifecycle.

If you need to access a **Request scoped bean** in a controller:

| import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Controller;@Controllerpublic class RequestController {    @Autowired    private RequestBean requestBean;    public void handleRequest() {        requestBean.showMessage();    }} |
| :---- |

Every time an HTTP request is made to the controller, a new instance of **`RequestBean`** will be created.

4\. Session Scope

* **Explanation**: A new instance of the bean is created for each **HTTP session**. It’s similar to the **Request** scope, but the instance lives for the duration of the user's session.  
* **Use Case**: Use Session scope for user-specific data that should persist during the session, such as user preferences or authentication details.

**Example**:

| import org.springframework.context.annotation.Scope;import org.springframework.stereotype.Component;@Component@Scope("session")  // Specify Session scopepublic class SessionBean {    public SessionBean() {        System.out.println("SessionBean instance created for the session.");    }    public void showMessage() {        System.out.println("Hello from Session Bean\!");    }} |
| :---- |

**Usage**:

In a **Spring Boot web application**, **Session scope** can be injected into controllers or services just like the Request scope, but the bean’s lifetime will extend across multiple requests within the same session.

| import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Controller;@Controllerpublic class SessionController {    @Autowired    private SessionBean sessionBean;    public void handleSession() {        sessionBean.showMessage();    }} |
| :---- |

Each user who opens a session will have their own instance of **`SessionBean`**.

---

# @ConditionalOnProperty Annotation in Spring Boot {#@conditionalonproperty-annotation-in-spring-boot}

`@ConditionalOnProperty` in Spring Boot

**`@ConditionalOnProperty`** is a Spring Boot annotation used to conditionally enable or disable beans based on the presence or value of a specific configuration property. It is part of the **`spring-boot-autoconfigure`** module and is typically used for **conditional bean registration** based on application properties.

Why Use `@ConditionalOnProperty`?

* To **control bean creation** based on environment-specific settings.  
* To enable/disable features without changing the code, using just the configuration.  
* To keep your application flexible and configurable.

Usage and Example

**Basic Syntax**:

| @ConditionalOnProperty(    prefix \= "property.prefix",    name \= "property-name",    havingValue \= "value",    matchIfMissing \= false) |
| :---- |

* **`prefix`**: The prefix of the property in the configuration file (e.g., `application.properties`).  
* **`name`**: The specific property name to check.  
* **`havingValue`**: The value that the property should have for the bean to be created.  
* **`matchIfMissing`**: If set to `true`, the bean will be created even if the property is not defined.

Example 1: Enabling a Bean Conditionally

Suppose you have a feature in your application that you want to enable based on a configuration property:

**`application.properties`**:

`feature.enabled=true`

**Java Bean**:

| import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;import org.springframework.stereotype.Component;@Component@ConditionalOnProperty(    prefix \= "feature",    name \= "enabled",    havingValue \= "true",    matchIfMissing \= false)public class FeatureService {    public void executeFeature() {        System.out.println("Feature is enabled and executed.");    }} |
| :---- |

In this example:

* If **`feature.enabled=true`** in `application.properties`, the **`FeatureService`** bean will be created.  
* If the property is set to `false` or not defined, the bean will **not be created**.

**Usage**:

| import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Service;@Servicepublic class MyService {    @Autowired(required \= false)  // Autowired as optional    private FeatureService featureService;    public void process() {        if (featureService \!= null) {            featureService.executeFeature();        } else {            System.out.println("Feature is disabled.");        }    }} |
| :---- |

Example 2: Default Value with `matchIfMissing`

If you want the bean to be created when the property is **missing** or has a default value:

**`application.properties`**:

| \# feature.auto-start is not set |
| :---- |

**Java Bean**:

| @Component@ConditionalOnProperty(    prefix \= "feature",    name \= "auto-start",    havingValue \= "true",    matchIfMissing \= true  // Will create the bean if the property is missing)public class AutoStartService {    public void start() {        System.out.println("AutoStartService bean created because matchIfMissing=true");    }} |
| :---- |

In this case:

* **`AutoStartService`** bean will be created even if **`feature.auto-start`** is not set because **`matchIfMissing`** is `true`.

Example 3: Using Multiple Properties

You can also use multiple properties with logical **AND** conditions:

| @Component@ConditionalOnProperty(    prefix \= "service",    name \= {"enabled", "use-cache"},    havingValue \= "true")public class CachedService {    public void cacheData() {        System.out.println("CachedService bean created and caching enabled.");    }} |
| :---- |

* Here, **`CachedService`** will only be created if **both** properties **`service.enabled`** and **`service.use-cache`** are set to **`true`**.

**`application.properties`**:

| service.enabled=trueservice.use-cache=true |
| :---- |

Advantages of `@ConditionalOnProperty`

1. **Flexibility**: Enables features dynamically based on configuration properties without code changes.  
2. **Decoupled Configuration**: Helps in decoupling the feature toggling from the code.  
3. **Environment Specific**: Useful in creating different beans for different environments (e.g., Dev, QA, Prod).

Common Use Cases

* **Feature Flags**: Enable or disable features in your application.  
* **Environment-Specific Beans**: Define beans that should only be loaded in specific environments.  
* **Auto-Configuration**: Used in Spring Boot's auto-configuration to conditionally enable certain configurations.

Conclusion

* **`@ConditionalOnProperty`** is a powerful tool for controlling bean creation based on configuration properties.  
* It helps maintain a clean codebase by avoiding hard-coded conditions.  
* It provides flexibility to enable or disable features without needing code changes.

---

# `@Profile` Annotation in Spring Boot {#@profile-annotation-in-spring-boot}

The **`@Profile`** annotation in Spring Boot is used to conditionally activate beans or configurations based on the currently active Spring profile. It allows you to segregate parts of your application configuration for different environments (e.g., **development**, **testing**, **production**), making it easier to manage environment-specific settings.

How Profiling Works in Spring Boot

Spring profiles are a way to create and manage environment-specific configurations. For instance, you might have different beans or configurations for **development**, **testing**, and **production** environments.

Defining Profiles

You can define profiles using:

* **Application properties** (`application.properties` or `application.yml`)  
* **Java code** using `@Profile` annotation

Setting Active Profiles

To activate a specific profile, you can use:

**1\. Application Properties**:  
properties  
Copy code

| spring.profiles.active=dev |
| :---- |

**2\. Environment Variable**:  
arduino  
Copy code

| export SPRING\_PROFILES\_ACTIVE=prod |
| :---- |

**3\. Command Line Argument**:  
css  
Copy code

| java \-jar myapp.jar \--spring.profiles.active=dev |
| :---- |

**4\. VM Option**:  
bash  
Copy code

| \-Dspring.profiles.active=test |
| :---- |

Using `@Profile` Annotation

**Basic Example**:

Let's say you have a service with different implementations based on the environment.

| import org.springframework.context.annotation.Profile;import org.springframework.stereotype.Service;@Service@Profile("dev")public class DevDataSourceConfig implements DataSourceConfig {    @Override    public void setup() {        System.out.println("Setting up DEV DataSource");    }} |
| :---- |

| @Service@Profile("prod")public class ProdDataSourceConfig implements DataSourceConfig {    @Override    public void setup() {        System.out.println("Setting up PROD DataSource");    }} |
| :---- |

* When **`spring.profiles.active=dev`** is set, **`DevDataSourceConfig`** will be used.  
* When **`spring.profiles.active=prod`** is set, **`ProdDataSourceConfig`** will be used.

**Interface**:

| public interface DataSourceConfig {    void setup();} |
| :---- |

**Using the Config**:

| import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Component;@Componentpublic class DataSourceInitializer {    private final DataSourceConfig dataSourceConfig;    @Autowired    public DataSourceInitializer(DataSourceConfig dataSourceConfig) {        this.dataSourceConfig \= dataSourceConfig;    }    public void initialize() {        dataSourceConfig.setup();    }} |
| :---- |

**Output**:

If **`spring.profiles.active=dev`**:  
mathematica  
Copy code

| Setting up DEV DataSource |
| :---- |

If **`spring.profiles.active=prod`**:  
mathematica  
Copy code

| Setting up PROD DataSource |
| :---- |

**Profile-Specific Configuration Files**

Spring Boot supports profile-specific configuration files:

* **`application-dev.properties`**  
* **`application-prod.properties`**

Example: **`application-dev.properties`**:

| app.name=My Spring Boot Dev Application |
| :---- |

**`application-prod.properties`**:

| app.name=My Spring Boot Prod Application |
| :---- |

With **`spring.profiles.active=dev`**:

| @Value("\${app.name}")private String appName; |
| :---- |

Output: `My Spring Boot Dev Application`

With **`spring.profiles.active=prod`**: 

Output: `My Spring Boot Prod Application`

Example 2: Conditional Beans with `@Profile` in Configuration Class

| import org.springframework.context.annotation.Bean;import org.springframework.context.annotation.Configuration;import org.springframework.context.annotation.Profile;@Configurationpublic class AppConfig {    @Bean    @Profile("dev")    public String devBean() {        return "Development Bean";    }    @Bean    @Profile("prod")    public String prodBean() {        return "Production Bean";    }} |
| :---- |

**Test Code**:

| import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Component;@Componentpublic class ProfileTester {    @Autowired    private String bean;    public void test() {        System.out.println("Active Profile Bean: " \+ bean);    }} |
| :---- |

**Output**:

For **`spring.profiles.active=dev`**:  
mathematica  
Copy code  
`Active Profile Bean: Development Bean`

For **`spring.profiles.active=prod`**:  
mathematica  
Copy code  
`Active Profile Bean: Production Bean`

**Advantages of Using `@Profile`**

1. **Environment-Specific Beans**: Define beans specific to environments like dev, test, or prod.  
2. **Easy Configuration Management**: Handle different configurations without changing code.  
3. **Modular Design**: Keep environment-specific configurations separate and modular.

**Best Practices**

1. **Define Default Profile**: You can define a default profile using `spring.profiles.default` to ensure a fallback profile.  
   properties  
   Copy code  
   `spring.profiles.default=dev`  
2. **Avoid Hardcoding Profiles**: Use configuration files or environment variables to manage active profiles.  
3. **Use `@Profile` for Environment-Specific Beans**: For beans that differ in each environment, use `@Profile` to control their creation.

**Conclusion**

The **`@Profile`** annotation in Spring Boot is a powerful tool for environment-specific configuration. It enables you to:

* Define different beans and configurations for different environments.  
* Easily switch configurations without modifying the codebase.  
* Keep your Spring Boot application modular and flexible.

By leveraging Spring profiles effectively, you can simplify configuration management and ensure your application behaves as expected across various environments.

---

