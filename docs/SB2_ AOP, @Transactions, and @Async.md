# 

**[Spring Boot AOP (Aspect-Oriented Programming)	2](#spring-boot-aop-\(aspect-oriented-programming\))**

[Logging with Spring Boot AOP	4](#logging-with-spring-boot-aop)

[Security with Spring Boot AOP	7](#security-with-spring-boot-aop)

[Transaction Management with Spring AOP	12](#transaction-management-with-spring-aop)

[**@Transactional Annotation in Spring Boot	18**](#@transactional-annotation-in-spring-boot)

[Declarative vs Programmatic Transaction Management	25](#declarative-vs-programmatic-transaction-management)

[Spring Boot @Transactional Annotation \- Isolation Levels	27](#spring-boot-@transactional-annotation---isolation-levels)

[**@Async Annotation \- Part 1 | ThreadPoolExecutor	31**](#@async-annotation---part-1-|-threadpoolexecutor)

[@Async Annotation Interview Questions	35](#@async-annotation-interview-questions)

# 

# Spring Boot AOP (Aspect-Oriented Programming) {#spring-boot-aop-(aspect-oriented-programming)}

Aspect-Oriented Programming (AOP) is a programming paradigm that aims to increase modularity by allowing the separation of cross-cutting concerns. In Spring Boot, AOP is used to handle aspects of the application that are not part of the core business logic, such as logging, security, transaction management, and exception handling.

**Key Concepts of AOP**

1. **Aspect**: A modular part of a program that encapsulates a concern affecting multiple classes (e.g., logging, security).  
2. **Join Point**: A point during the execution of a program, such as a method call or an exception being thrown, where an aspect can be applied.  
3. **Advice**: Action taken by an aspect at a particular join point. It defines what the aspect does (e.g., before or after method execution).  
   * **Types of Advice**:  
     * **Before**: Executes before a join point (method execution).  
     * **After Returning**: Executes after a join point completes successfully.  
     * **After Throwing**: Executes if a method throws an exception.  
     * **After (Finally)**: Executes after a join point, regardless of its outcome.  
     * **Around**: Wraps a join point, allowing the advice to perform custom behavior before and after the method execution.  
4. **Pointcut**: A predicate that matches join points. It is an expression that selects one or more join points where advice should be applied.  
5. **Weaving**: The process of applying aspects to target objects to create new proxy objects.

**How Spring AOP Works**

Spring AOP uses **dynamic proxies** to apply advice at runtime. It primarily uses proxies, either JDK dynamic proxies or CGLIB proxies, depending on whether the target class implements an interface.

**Setting Up AOP in Spring Boot**

1. **Add the Dependency**: If using Spring Boot Starter, it is included by default.

| \<dependency\>    \<groupId\>org.springframework.boot\</groupId\>    \<artifactId\>spring-boot-starter-aop\</artifactId\>\</dependency\> |
| :---- |

2. **Enable AOP Configuration**: You can enable AOP by using the **`@EnableAspectJAutoProxy`** annotation in your configuration class (optional as it is usually auto-configured).

| import org.springframework.context.annotation.Configuration;import org.springframework.context.annotation.EnableAspectJAutoProxy;@Configuration@EnableAspectJAutoProxypublic class AopConfig {} |
| :---- |

**Pointcut Expressions**

Spring AOP uses AspectJ pointcut expressions to define join points:

* **`execution(* com.example.service.*.*(..))`**: Matches any method in `com.example.service` package.  
* **`within(com.example.service..*)`**: Matches all methods within classes in `com.example.service` and its sub-packages.  
* **`@annotation(org.springframework.web.bind.annotation.GetMapping)`**: Matches methods annotated with `@GetMapping`.

**Advantages of Using AOP in Spring Boot**

1. **Separation of Concerns**: Separate cross-cutting concerns like logging, security, and transaction management.  
2. **Code Reusability**: Common logic can be reused across multiple modules.  
3. **Reduced Code Duplication**: Centralize repeated logic, reducing code duplication.  
4. **Cleaner Code**: Core business logic is not cluttered with secondary concerns.

**Common Use Cases of AOP**

1. **Logging and Monitoring**: Track method execution times, parameters, and outcomes.  
2. **Security**: Implement security checks before method execution.  
3. **Transaction Management**: Automatically manage transactions around service methods.  
4. **Exception Handling**: Capture and handle exceptions in a standardized way.

**Conclusion**

Spring Boot AOP is a powerful feature for handling cross-cutting concerns without cluttering the core business logic. With a minimal configuration, you can intercept method executions, log important events, enforce security checks, and manage transactions effectively, making your application cleaner and more maintainable.

---

## Logging with Spring Boot AOP {#logging-with-spring-boot-aop}

**Creating an Aspect**

Here's an example demonstrating logging using AOP:

1\. Define the Aspect Class

| import org.aspectj.lang.annotation.Aspect;import org.aspectj.lang.annotation.Before;import org.aspectj.lang.annotation.After;import org.aspectj.lang.annotation.AfterReturning;import org.aspectj.lang.annotation.AfterThrowing;import org.aspectj.lang.annotation.Around;import org.aspectj.lang.ProceedingJoinPoint;import org.springframework.stereotype.Component;@Aspect@Componentpublic class LoggingAspect {    // Before Advice    @Before("execution(\* com.example.service.\*.\*(..))")    public void logBeforeMethod() {        System.out.println("LoggingAspect: Before method execution");    }    // After Advice    @After("execution(\* com.example.service.\*.\*(..))")    public void logAfterMethod() {        System.out.println("LoggingAspect: After method execution");    }    // After Returning Advice    @AfterReturning(pointcut \= "execution(\* com.example.service.\*.\*(..))", returning \= "result")    public void logAfterReturning(Object result) {        System.out.println("LoggingAspect: Method returned value is " \+ result);    }    // After Throwing Advice    @AfterThrowing(pointcut \= "execution(\* com.example.service.\*.\*(..))", throwing \= "error")    public void logAfterThrowing(Exception error) {        System.out.println("LoggingAspect: Method threw an exception " \+ error);    }    // Around Advice    @Around("execution(\* com.example.service.\*.\*(..))")    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {        System.out.println("LoggingAspect: Before around method execution");        Object result \= joinPoint.proceed();        System.out.println("LoggingAspect: After around method execution");        return result;    }} |
| :---- |

2\. Define a Service to Apply the Aspect

| import org.springframework.stereotype.Service;@Servicepublic class UserService {    public String getUserById(Long id) {        System.out.println("Executing getUserById in UserService");        return "User-" \+ id;    }    public void createUser(String name) {        System.out.println("Executing createUser in UserService");    }    public void throwException() {        throw new RuntimeException("Intentional Exception");    }} |
| :---- |

3\. Using the Service

| import org.springframework.boot.CommandLineRunner;import org.springframework.stereotype.Component;@Componentpublic class AppRunner implements CommandLineRunner {    private final UserService userService;    public AppRunner(UserService userService) {        this.userService \= userService;    }    @Override    public void run(String... args) throws Exception {        userService.getUserById(1L);        userService.createUser("John Doe");        try {            userService.throwException();        } catch (Exception e) {            System.out.println("Caught exception: " \+ e.getMessage());        }    }} |
| :---- |

**Expected Output**

`LoggingAspect: Before method execution`  
`Executing getUserById in UserService`  
`LoggingAspect: After method execution`  
`LoggingAspect: Before method execution`  
`Executing createUser in UserService`  
`LoggingAspect: After method execution`  
`LoggingAspect: Before method execution`  
`LoggingAspect: Method threw an exception java.lang.RuntimeException: Intentional Exception`  
`Caught exception: Intentional Exception`

---

## Security with Spring Boot AOP {#security-with-spring-boot-aop}

Aspect-Oriented Programming (AOP) can be an effective tool for implementing security concerns in a Spring Boot application. While Spring Security is a comprehensive framework for handling security, AOP can be used for custom security checks or simple authorization logic without the full overhead of Spring Security.

**Use Case for AOP in Security**

A typical scenario could involve checking if a user has the correct role or permission to execute a specific method. For example:

* Restricting access to specific service methods based on user roles or permissions.  
* Validating a custom security token or API key before executing a method.

Implementing Security with AOP in Spring Boot

Here's how you can implement a basic security check using AOP:

**1\. Define a Custom Security Annotation**

Create a custom annotation, `@Secured`, that will be used to mark methods that require role-based access control.

| package com.example.security;import java.lang.annotation.\*;@Target(ElementType.METHOD)@Retention(RetentionPolicy.RUNTIME)public @interface Secured {    String role() default "USER";} |
| :---- |

**2\. Create a Security Aspect**

This aspect will intercept the methods annotated with `@Secured` and perform a role check.

| package com.example.security;import org.aspectj.lang.annotation.Aspect;import org.aspectj.lang.annotation.Before;import org.aspectj.lang.JoinPoint;import org.aspectj.lang.reflect.MethodSignature;import org.springframework.stereotype.Component;import java.lang.reflect.Method;@Aspect@Componentpublic class SecurityAspect {    // Simulated security context (in real cases, use Spring Security's context)    private static final ThreadLocal\<String\> currentUserRole \= ThreadLocal.withInitial(() \-\> "USER");    // Method to set current user role (for testing purposes)    public static void setCurrentUserRole(String role) {        currentUserRole.set(role);    }    // Aspect method to check security based on role    @Before("@annotation(com.example.security.Secured)")    public void checkUserRole(JoinPoint joinPoint) throws Throwable {        // Get the method being called        MethodSignature signature \= (MethodSignature) joinPoint.getSignature();        Method method \= signature.getMethod();        // Retrieve the @Secured annotation        Secured secured \= method.getAnnotation(Secured.class);        // Get required role from annotation        String requiredRole \= secured.role();        // Get the current user role (simulated)        String currentRole \= currentUserRole.get();        System.out.println("Current Role: " \+ currentRole \+ ", Required Role: " \+ requiredRole);        // Check if the current user's role matches the required role        if (\!currentRole.equalsIgnoreCase(requiredRole)) {            throw new SecurityException("Access denied: insufficient role");        }        System.out.println("Access granted to method: " \+ method.getName());    }} |
| :---- |

**3\. Applying the Custom Security Annotation**

Use the `@Secured` annotation on methods where you want to enforce role-based access control.

| package com.example.service;import com.example.security.Secured;import org.springframework.stereotype.Service;@Servicepublic class AdminService {    @Secured(role \= "ADMIN")    public void performAdminTask() {        System.out.println("Executing admin task");    }    @Secured(role \= "USER")    public void performUserTask() {        System.out.println("Executing user task");    }} |
| :---- |

**4\. Testing the Security Aspect**

Here's a simple `CommandLineRunner` to test the security behavior:

| package com.example;import com.example.security.SecurityAspect;import com.example.service.AdminService;import org.springframework.boot.CommandLineRunner;import org.springframework.boot.SpringApplication;import org.springframework.boot.autoconfigure.SpringBootApplication;import org.springframework.context.annotation.Bean;@SpringBootApplicationpublic class AopSecurityApplication {    public static void main(String\[\] args) {        SpringApplication.run(AopSecurityApplication.class, args);    }    @Bean    CommandLineRunner run(AdminService adminService) {        return args \-\> {            try {                // Simulate a user with "USER" role                SecurityAspect.setCurrentUserRole("USER");                adminService.performUserTask(); // Should pass                adminService.performAdminTask(); // Should fail                // Simulate a user with "ADMIN" role                SecurityAspect.setCurrentUserRole("ADMIN");                adminService.performAdminTask(); // Should pass            } catch (SecurityException e) {                System.out.println(e.getMessage());            }        };    }} |
| :---- |

**Expected Output**

sql  
Copy code  
`Current Role: USER, Required Role: USER`  
`Access granted to method: performUserTask`  
`Executing user task`

`Current Role: USER, Required Role: ADMIN`  
`Access denied: insufficient role`

`Current Role: ADMIN, Required Role: ADMIN`  
`Access granted to method: performAdminTask`  
`Executing admin task`

**Advantages of Using AOP for Security**

1. **Separation of Concerns**: Security logic is separated from the business logic of the service methods.  
2. **Reusability**: The same security checks can be applied to multiple methods without repeating the code.  
3. **Ease of Implementation**: Using annotations makes it easy to apply security checks across different methods.

**Limitations and Best Practices**

1. **Limited Use Case**: AOP-based security is suitable for simple, custom checks. For comprehensive security requirements, such as authentication, complex authorization rules, and secure session management, **Spring Security** is a better choice.  
2. **No Authentication**: This AOP approach only handles role-based authorization. It does not provide authentication mechanisms.  
3. **Performance Overhead**: The use of AOP introduces a small performance overhead due to method interception.

---

## Transaction Management with Spring AOP {#transaction-management-with-spring-aop}

In Spring Boot, transaction management is often handled using the `@Transactional` annotation, which is implemented using **Aspect-Oriented Programming (AOP)**. The idea behind this approach is to apply transaction handling as a cross-cutting concern that can be transparently applied to methods without embedding transactional logic directly into the business code.

**How AOP Works for Transaction Management**

1. **Pointcut**: The `@Transactional` annotation acts as a pointcut, marking methods or classes where transaction management should be applied.  
2. **Advice**: The transactional advice (provided by Spring's `TransactionInterceptor`) is executed before and after the annotated method:  
   * **Before the method**: Starts a new transaction or joins an existing one.  
   * **After the method**: Commits the transaction if no exceptions occurred; otherwise, it rolls back the transaction in case of a checked exception (based on configuration).  
3. **Aspect**: The aspect itself is defined by Spring's built-in transaction infrastructure, leveraging AOP to manage transactions declaratively.

**Implementation with Example**

Let's go through an example where we demonstrate how transaction management is applied using AOP.

**1\. Define the Service Layer with @Transactional**

| package com.example.service;import com.example.repository.AccountRepository;import com.example.model.Account;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;@Servicepublic class AccountService {    @Autowired    private AccountRepository accountRepository;    // A method with transaction management    @Transactional    public void transferMoney(Long fromAccountId, Long toAccountId, Double amount) {        Account fromAccount \= accountRepository.findById(fromAccountId).orElseThrow();        Account toAccount \= accountRepository.findById(toAccountId).orElseThrow();        if (fromAccount.getBalance() \< amount) {            throw new IllegalArgumentException("Insufficient funds");        }        fromAccount.setBalance(fromAccount.getBalance() \- amount);        toAccount.setBalance(toAccount.getBalance() \+ amount);        // Save updates        accountRepository.save(fromAccount);        accountRepository.save(toAccount);        System.out.println("Transaction successful");    }} |
| :---- |

**2\. Configure DataSource and Enable Transaction Management**

In your Spring Boot application, enable transaction management using `@EnableTransactionManagement`. This is typically handled by Spring Boot auto-configuration but can be explicitly defined as follows:

| package com.example.config;import org.springframework.context.annotation.Configuration;import org.springframework.transaction.annotation.EnableTransactionManagement;@Configuration@EnableTransactionManagementpublic class AppConfig {} |
| :---- |

3\. Transaction Interception Mechanism

When the `transferMoney` method is called:

* **AOP Interceptor**:  
  * Spring's `TransactionInterceptor` checks for the presence of `@Transactional`.  
  * It creates a transactional context using the `TransactionManager`.  
* **Before Advice**:  
  * If no existing transaction is present, it begins a new transaction.  
* **Method Execution**:  
  * The business logic (money transfer) executes within this transactional context.  
* **After Advice**:  
  * If the method completes successfully, the transaction is committed.  
  * If an exception occurs, the transaction is rolled back based on the exception type.

**How Spring AOP Handles This Behind the Scenes**

Spring uses **CGLIB** or **JDK dynamic proxies** to wrap the `AccountService` bean. When `transferMoney` is called:

* The proxy intercepts the method call.  
* The AOP interceptor checks for the `@Transactional` annotation.  
* The transaction advice (`TransactionInterceptor`) is applied, starting or joining a transaction.  
* The original `transferMoney` method is invoked within the transaction context.

**Advantages of Using AOP for Transaction Management**

1. **Declarative Approach**: With `@Transactional`, you don't have to write boilerplate code for starting and committing transactions.  
2. **Separation of Concerns**: Business logic is separated from transaction management logic.  
3. **Consistency**: Ensures consistent behavior across different methods and classes, making the transaction handling centralized and easier to manage.

**Custom Implementation Using AOP (For Learning Purpose)**

To understand how it works under the hood, let's create a simple custom transaction aspect using AOP.

**Custom Transaction Aspect Example**

| package com.example.aspect;import org.aspectj.lang.annotation.Aspect;import org.aspectj.lang.annotation.Before;import org.aspectj.lang.annotation.AfterReturning;import org.aspectj.lang.annotation.AfterThrowing;import org.aspectj.lang.ProceedingJoinPoint;import org.aspectj.lang.annotation.Around;import org.springframework.stereotype.Component;@Aspect@Componentpublic class CustomTransactionAspect {    @Before("@annotation(org.springframework.transaction.annotation.Transactional)")    public void beginTransaction() {        System.out.println("Starting transaction...");        // In real implementation, call transactionManager.getTransaction()    }    @AfterReturning("@annotation(org.springframework.transaction.annotation.Transactional)")    public void commitTransaction() {        System.out.println("Committing transaction...");        // In real implementation, call transactionManager.commit()    }    @AfterThrowing("@annotation(org.springframework.transaction.annotation.Transactional)")    public void rollbackTransaction() {        System.out.println("Rolling back transaction...");        // In real implementation, call transactionManager.rollback()    }} |
| :---- |

**In this custom implementation:**

* **@Before**: Starts the transaction before the method execution.  
* **@AfterReturning**: Commits the transaction if the method completes without exceptions.  
* **@AfterThrowing**: Rolls back the transaction if an exception is thrown.

**Testing the Transaction Aspect**

You can test this by calling the `transferMoney` method in your `CommandLineRunner` or any test setup:

| @SpringBootApplicationpublic class TransactionAopDemoApplication {    public static void main(String\[\] args) {        SpringApplication.run(TransactionAopDemoApplication.class, args);    }    @Bean    CommandLineRunner run(AccountService accountService) {        return args \-\> {            try {                accountService.transferMoney(1L, 2L, 100.0);            } catch (Exception e) {                System.out.println("Exception occurred: " \+ e.getMessage());            }        };    }} |
| :---- |

**Output**

Copy code

`Starting transaction...`

`Transaction successful`

`Committing transaction...`

Or, in case of an exception:

php

Copy code

`Starting transaction...`

`Exception occurred: Insufficient funds`

`Rolling back transaction...`

**Summary**

Spring Boot uses AOP to manage transactions declaratively:

* The `@Transactional` annotation acts as a pointcut.  
* Spring AOP proxies intercept the call to apply transactional advice.  
* The `TransactionInterceptor` handles starting, committing, or rolling back the transaction.

This approach makes transaction handling transparent and ensures that transactional concerns are separated from business logic, enhancing maintainability and readability.

---

# `@Transactional` Annotation in Spring Boot {#@transactional-annotation-in-spring-boot}

The `@Transactional` annotation in Spring Boot is used to manage **transactional behavior** in your application declaratively. It provides a way to define **transaction boundaries** at the method or class level, making it easier to handle database operations without manually managing the transactions.

Key Features of `@Transactional` Annotation

* **Automatic Transaction Management**: Starts a transaction before executing a method and commits it afterward. If an exception occurs, it can roll back the transaction.  
* **Declarative Approach**: You can apply it directly to methods or classes without writing boilerplate transaction code.  
* **Configuration Options**: Provides various attributes to customize the transaction behavior, such as `propagation`, `isolation`, `readOnly`, etc.

**Example Usage of `@Transactional`**

**1\. Basic Usage**

| import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;@Servicepublic class UserService {    private final UserRepository userRepository;    public UserService(UserRepository userRepository) {        this.userRepository \= userRepository;    }    @Transactional    public void createUser(String username, String email) {        User user \= new User(username, email);        userRepository.save(user);        System.out.println("User created successfully");    }} |
| :---- |

**In this example:**

* The `createUser` method is wrapped in a transactional context.  
* Spring handles starting the transaction before the method execution and commits it afterward.  
* If any exception occurs during execution, the transaction is rolled back.

**2\. Applying `@Transactional` at the Class Level**

You can apply `@Transactional` at the class level to make all public methods transactional.

| import org.springframework.transaction.annotation.Transactional;import org.springframework.stereotype.Service;@Service@Transactionalpublic class AccountService {    private final AccountRepository accountRepository;    public AccountService(AccountRepository accountRepository) {        this.accountRepository \= accountRepository;    }    public void deposit(Long accountId, Double amount) {        Account account \= accountRepository.findById(accountId).orElseThrow();        account.setBalance(account.getBalance() \+ amount);        accountRepository.save(account);    }    public void withdraw(Long accountId, Double amount) {        Account account \= accountRepository.findById(accountId).orElseThrow();        account.setBalance(account.getBalance() \- amount);        accountRepository.save(account);    }} |
| :---- |

Here:

* All public methods of `AccountService` are transactional because the `@Transactional` is applied at the class level.

**Transaction Attributes**

The `@Transactional` annotation has several attributes to customize its behavior:

1\. `propagation`

Specifies how transactions should behave in terms of existing transactions.

* **`REQUIRED`** (default): Joins the current transaction or starts a new one if none exists.  
* **`REQUIRES_NEW`**: Suspends the current transaction and starts a new one.  
* **`SUPPORTS`**: Executes within the current transaction if one exists; otherwise, it runs non-transactionally.  
* **`NOT_SUPPORTED`**: Executes without a transaction, suspending any existing one.  
* **`MANDATORY`**: Requires an existing transaction; throws an exception if none exists.  
* **`NEVER`**: Throws an exception if a transaction exists.  
* **`NESTED`**: Creates a nested transaction if the current transaction exists.

**Example of Propagation Types**

| import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Propagation;import org.springframework.transaction.annotation.Transactional;@Servicepublic class OrderService {    private final PaymentService paymentService;    private final OrderRepository orderRepository;    public OrderService(PaymentService paymentService, OrderRepository orderRepository) {        this.paymentService \= paymentService;        this.orderRepository \= orderRepository;    }    @Transactional    public void placeOrder(Order order) {        // Main Transaction Starts        orderRepository.save(order);        // Calls a method with Propagation.REQUIRES\_NEW        paymentService.processPayment(order);        // Resumes main transaction after processPayment completes        orderRepository.updateStatus(order.getId(), "COMPLETED");    }}@Servicepublic class PaymentService {    private final PaymentRepository paymentRepository;    public PaymentService(PaymentRepository paymentRepository) {        this.paymentRepository \= paymentRepository;    }    @Transactional(propagation \= Propagation.REQUIRES\_NEW)    public void processPayment(Order order) {        // New Transaction Starts        paymentRepository.save(new Payment(order.getId(), "PAID"));        // New Transaction Ends (committed or rolled back)    }} |
| :---- |

**Explanation**:

* **`processPayment`** uses `Propagation.REQUIRED`, meaning it will participate in the existing transaction if available.  
* **`recordAudit`** uses `Propagation.REQUIRES_NEW`, which starts a new transaction, suspending the existing one. This ensures that any issues in the audit logging do not affect the main payment process.

**Use Cases of Different Propagations:**

* **`REQUIRED`**: Most common; use when the method should always be part of an existing transaction or start a new one.  
* **`REQUIRES_NEW`**: Use when you need a separate transaction, e.g., logging, audit, or when an operation must not be rolled back with the main transaction.  
* **`SUPPORTS`**: Use for optional transactional behavior, e.g., a method that can work with or without a transaction context.  
* **`NESTED`**: Use when you want a rollback of only part of a transaction while keeping the main transaction intact.

**Common Issues with Propagation and Best Practices**

1. **Avoid Overusing `REQUIRES_NEW`**:  
   * Using `REQUIRES_NEW` excessively can lead to performance issues because it creates multiple database transactions.

2\. `isolation`

Defines the isolation level for the transaction to manage concurrent data access:

* **`DEFAULT`**: Uses the default isolation level of the database.  
* **`READ_UNCOMMITTED`**: Allows dirty reads (the lowest level of isolation).  
* **`READ_COMMITTED`**: Prevents dirty reads but allows non-repeatable reads.  
* **`REPEATABLE_READ`**: Prevents dirty reads and non-repeatable reads but allows phantom reads.  
* **`SERIALIZABLE`**: The highest isolation level, preventing dirty reads, non-repeatable reads, and phantom reads.

**Example**:

| @Transactional(isolation \= Isolation.SERIALIZABLE)public void updateUserBalance(Long userId, Double amount) {    // This method uses SERIALIZABLE isolation level} |
| :---- |

3\. `readOnly`

Indicates whether the transaction is read-only. This can help optimize performance by avoiding unnecessary locking.

**Example**:

| @Transactional(readOnly \= true)public List\<User\> getAllUsers() {    return userRepository.findAll();} |
| :---- |

* Setting `readOnly = true` hints to the database that no data modification is expected.

4\. `timeout`

Specifies the maximum time (in seconds) a transaction can take before it's rolled back automatically.

**Example**:

| @Transactional(timeout \= 5\)public void performLongRunningOperation() {    // If this method takes longer than 5 seconds, the transaction is rolled back} |
| :---- |

5\. `rollbackFor` and `noRollbackFor`

Defines which exceptions should trigger a rollback or should not trigger a rollback.

* **`rollbackFor`**: Specifies exception classes that should trigger a rollback.  
* **`noRollbackFor`**: Specifies exception classes that should not trigger a rollback.

**Example**:

| @Transactional(rollbackFor \= {IllegalArgumentException.class})public void updateUserEmail(Long userId, String email) {    // Rollback if IllegalArgumentException occurs}@Transactional(noRollbackFor \= {RuntimeException.class})public void updateUserStatus(Long userId, String status) {    // Do not rollback if a RuntimeException occurs} |
| :---- |

**How `@Transactional` Works Internally with AOP**

* Spring creates a **proxy** for the class annotated with `@Transactional`.  
* When a method with `@Transactional` is called, the proxy intercepts the call.  
* The proxy uses Spring's `TransactionInterceptor` to begin a transaction, execute the method, and then either commit or roll back the transaction based on the outcome.

**Important**:

* If you call a `@Transactional` method internally from another method within the same class, the transaction may not work as expected because the proxy is bypassed. This is known as the **self-invocation problem**.

**Best Practices with `@Transactional`**

1. **Apply to Service Layer**: Use `@Transactional` on service layer methods, not on repository layer methods. Repositories already provide basic transaction management.  
2. **Avoid Overuse**: Only apply `@Transactional` when necessary, especially for read-only methods, as it can add unnecessary overhead.  
3. **Handle Exceptions Correctly**: By default, Spring only rolls back on unchecked exceptions (`RuntimeException` or `Error`). Use `rollbackFor` to specify checked exceptions if needed.

**Summary**

* `@Transactional` simplifies transaction management by allowing you to declaratively control transactions.  
* It integrates with Spring AOP to apply transactional behavior to methods transparently.  
* Customizable through attributes like `propagation`, `isolation`, `readOnly`, `timeout`, `rollbackFor`, etc.  
* Helps maintain a clean separation between business logic and transaction management.

This approach promotes clean, maintainable, and consistent transaction handling across your Spring Boot application.

---

## Declarative vs Programmatic Transaction Management {#declarative-vs-programmatic-transaction-management}

**Declarative Transaction Management**

* **Declarative** transaction management involves using the `@Transactional` annotation directly on methods or classes.  
* It allows you to manage transactions without explicitly starting, committing, or rolling them back in the code.  
* **Pros**: Cleaner code, reduced boilerplate, separation of concerns.  
* **Cons**: Less control over the transaction boundary; can be limited in certain complex scenarios.

**Example**:

| import org.springframework.transaction.annotation.Transactional;import org.springframework.stereotype.Service;@Servicepublic class OrderService {    private final OrderRepository orderRepository;    public OrderService(OrderRepository orderRepository) {        this.orderRepository \= orderRepository;    }    @Transactional    public void placeOrder(Order order) {        orderRepository.save(order);        System.out.println("Order placed successfully.");    }} |
| :---- |

In this example:

* The `placeOrder` method is annotated with `@Transactional`.  
* Spring manages the transaction automatically.

**Programmatic Transaction Management**

* **Programmatic** transaction management uses the `TransactionTemplate` or the `PlatformTransactionManager` to explicitly control transactions.  
* It provides greater control over the transaction boundaries but adds more code complexity.

**Example Using `TransactionTemplate`**:

| import org.springframework.stereotype.Service;import org.springframework.transaction.PlatformTransactionManager;import org.springframework.transaction.support.TransactionTemplate;@Servicepublic class ProductService {    private final TransactionTemplate transactionTemplate;    private final ProductRepository productRepository;    public ProductService(PlatformTransactionManager transactionManager, ProductRepository productRepository) {        this.transactionTemplate \= new TransactionTemplate(transactionManager);        this.productRepository \= productRepository;    }    public void addProduct(Product product) {        transactionTemplate.execute(status \-\> {            try {                productRepository.save(product);                System.out.println("Product added successfully.");                return true;            } catch (Exception ex) {                status.setRollbackOnly();                return false;            }        });    }} |
| :---- |

In this example:

* The `TransactionTemplate` is used to manage the transaction programmatically.  
* The transaction is rolled back manually using `status.setRollbackOnly()` in case of an exception.

**When to Use Which**:

* Use **declarative** management (`@Transactional`) for most use cases due to its simplicity and integration with Spring AOP.  
* Use **programmatic** management when you need fine-grained control or when working outside of Spring's AOP proxies (e.g., in helper methods).

---

## Spring Boot @Transactional Annotation \- Isolation Levels {#spring-boot-@transactional-annotation---isolation-levels}

In Spring Boot, the `@Transactional` annotation not only manages transaction boundaries but also controls **isolation levels** of a transaction. The isolation level defines how the transaction is isolated from other concurrent transactions, especially in terms of visibility of changes.

**Why Isolation Levels?**

Isolation levels help prevent problems like:

* **Dirty Reads**: Reading uncommitted data from another transaction.  
* **Non-repeatable Reads**: Data changes between reads in the same transaction.  
* **Phantom Reads**: New rows are added or deleted by other transactions during a read operation.

**Types of Isolation Levels**

Spring Boot supports the following isolation levels, corresponding to those defined by SQL standards:

1. **Isolation.DEFAULT**:  
   * This is the default isolation level of the underlying database.  
   * It is typically database-specific (e.g., in MySQL, it's `REPEATABLE READ` by default).  
   * It leaves the isolation level unchanged from the database default.

java  
Copy code

| @Transactional(isolation \= Isolation.DEFAULT)public void defaultIsolation() {    // Uses the database's default isolation level} |
| :---- |

2. **Isolation.READ\_UNCOMMITTED**:  
   * **Allows dirty reads**: A transaction can read data that is not yet committed by another transaction.  
   * **Problems**: Dirty reads, non-repeatable reads, and phantom reads can occur.  
   * **Usage**: Rarely used due to high risk of inconsistency.

java  
Copy code

| @Transactional(isolation \= Isolation.READ\_UNCOMMITTED)public void readUncommitted() {    // Data changes made by other transactions are visible before commit} |
| :---- |

3. **Isolation.READ\_COMMITTED**:  
   * **Prevents dirty reads**: A transaction only reads committed data.  
   * **Problems**: Non-repeatable reads and phantom reads can still occur.  
   * **Usage**: Commonly used in many databases like SQL Server and PostgreSQL.

java  
Copy code

| @Transactional(isolation \= Isolation.READ\_COMMITTED)public void readCommitted() {    // Only committed changes from other transactions are visible} |
| :---- |

4. **Isolation.REPEATABLE\_READ**:  
   * **Prevents dirty reads and non-repeatable reads**: A transaction sees consistent data during its execution.  
   * **Problems**: Phantom reads can still occur, where new rows are added by other transactions.  
   * **Usage**: Provides higher consistency; MySQL uses this as its default isolation level.

java  
Copy code

| @Transactional(isolation \= Isolation.REPEATABLE\_READ)public void repeatableRead() {    // Ensures consistent data across multiple reads in the same transaction} |
| :---- |

5. **Isolation.SERIALIZABLE**:  
   * **Prevents dirty reads, non-repeatable reads, and phantom reads**: This is the strictest isolation level.  
   * **Usage**: Achieves full isolation but may cause performance issues due to extensive locking.  
   * **Performance**: May lead to higher latency and reduced throughput due to locking of read and write operations.

java  
Copy code

| @Transactional(isolation \= Isolation.SERIALIZABLE)public void serializable() {    // The most isolated but slowest level} |
| :---- |

**Practical Example**

Let's look at a real-world example involving the `READ_COMMITTED` isolation level:

java

Copy code

| @Servicepublic class AccountService {    @Autowired    private AccountRepository accountRepository;    @Transactional(isolation \= Isolation.READ\_COMMITTED)    public void transferMoney(Long fromAccountId, Long toAccountId, Double amount) {        Account fromAccount \= accountRepository.findById(fromAccountId).orElseThrow();        Account toAccount \= accountRepository.findById(toAccountId).orElseThrow();        fromAccount.debit(amount);        toAccount.credit(amount);        accountRepository.save(fromAccount);        accountRepository.save(toAccount);    }} |
| :---- |

Explanation:

* **Scenario**: Two transactions are trying to transfer money at the same time.  
* **Using `READ_COMMITTED`**:  
  * It ensures that changes made by one transaction are only visible after commit, preventing **dirty reads**.  
  * However, there might still be issues with **non-repeatable reads** or **phantom reads**.

**Choosing the Right Isolation Level:**

* **Performance vs Consistency**:  
  * **Higher isolation levels** (e.g., `SERIALIZABLE`) provide better consistency but can cause significant performance overhead due to extensive locking.  
  * **Lower isolation levels** (e.g., `READ_UNCOMMITTED`) can improve performance but may result in data inconsistencies.  
* **Application Use Case**:  
  * For most business operations, `READ_COMMITTED` or `REPEATABLE_READ` is often sufficient.  
  * For critical financial transactions, `SERIALIZABLE` might be chosen to ensure maximum data integrity.

**Summary Table:**

| Isolation Level | Dirty Read | Non-repeatable Read | Phantom Read |
| ----- | ----- | ----- | ----- |
| **READ\_UNCOMMITTED** | Yes | Yes | Yes |
| **READ\_COMMITTED** | No | Yes | Yes |
| **REPEATABLE\_READ** | No | No | Yes |
| **SERIALIZABLE** | No | No | No |

In summary, isolation levels in Spring Boot allow fine-grained control over transactional behavior, helping to balance performance and data integrity based on application requirements.

---

# `@Async` Annotation \- Part 1 | `ThreadPoolExecutor` {#@async-annotation---part-1-|-threadpoolexecutor}

🧠 Purpose of `@Async`

* Enables **asynchronous method execution**.

* Executes the annotated method in a **separate thread**, not blocking the caller.

* Helps improve **throughput and responsiveness** in web and microservice applications.

---

✅ How to Enable `@Async` in Spring Boot

**Enable Asynchronous Processing**

 java  
CopyEdit  
`@SpringBootApplication`

`@EnableAsync`

`public class MyApp {`

    `public static void main(String[] args) {`

        `SpringApplication.run(MyApp.class, args);`

    `}`

`}`

1. 

**Use `@Async` on Methods**

 java  
CopyEdit  
`@Service`

`public class MyService {`

    `@Async`

    `public void processAsyncTask() {`

        `// Code that runs in a separate thread`

        `System.out.println("Running in thread: " + Thread.currentThread().getName());`

    `}`

`}`

2. 

---

🔧 Behind the Scenes: `ThreadPoolExecutor`

* By default, Spring uses `SimpleAsyncTaskExecutor` (not recommended for production).

* You can **customize the thread pool** by defining a `TaskExecutor` bean.

Example: Custom `ThreadPoolTaskExecutor`

java

CopyEdit

`@Configuration`

`public class AsyncConfig {`

   `@Bean(name = "customExecutor")`

   `public Executor taskExecutor() {`

       `ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();`

       `executor.setCorePoolSize(5);        // Minimum threads`

       `executor.setMaxPoolSize(10);        // Max threads`

       `executor.setQueueCapacity(25);      // Queue size`

       `executor.setThreadNamePrefix("AsyncThread-");`

       `executor.initialize();`

       `return executor;`

   `}`

`}`

Usage with Custom Executor

java

CopyEdit

`@Async("customExecutor")`

`public void executeWithCustomExecutor() {`

    `// This will use the configured ThreadPoolTaskExecutor`

`}`

---

⚠️ Important Points

* Methods marked with `@Async`:

  * Must be `public`.

  * Should not be called from within the same class (self-invocation issue).

* Can return `void`, `Future<T>`, `CompletableFuture<T>`, or `ListenableFuture<T>`.

* Exceptions in `@Async` methods are not propagated back to the caller directly.

---

🧪 Example Use Case

java

CopyEdit

`@RestController`

`public class MyController {`

    `@Autowired`

    `private MyService myService;`

    `@GetMapping("/run")`

    `public String triggerAsyncTask() {`

        `myService.processAsyncTask();  // returns immediately`

        `return "Task started!";`

    `}`

`}`

---

## `@Async` Annotation Interview Questions {#@async-annotation-interview-questions}

---

✅ Key Conditions for `@Async` to Work

1. **Method must be public**

2. **Async method must be in a different class** than the caller

📌 **Why?**

* Spring uses **AOP proxies** for `@Async`.

* AOP **interception won't work**:

  * On private/protected methods

  * For internal method calls within the same class

---

❓ Interview Question 1: Why `@Async` doesn't work inside the same class?

* Spring's AOP creates a proxy object to intercept method calls.

* Internal method calls (same class) **bypass the proxy**, so no interception occurs.

* Solution: **Call async method from another class**.

---

❓ Interview Question 2: How does `@Async` interact with `@Transactional`?

🚫 Use Case 1: `@Transactional` method calls `@Async` method

java

CopyEdit

`@Transactional`

`public void updateUser() {`

    `userRepository.update(); // in transaction`

    `asyncService.updateBalance(); // @Async - runs in another thread`

`}`

* 🔥 **Issue**: Transaction context is **not propagated** to the new thread.

* 💥 Rollback won’t affect `@Async` method execution.

* ❌ **Avoid this pattern**.

⚠️ Use Case 2: Method with both `@Async` and `@Transactional`

java

CopyEdit

`@Async`

`@Transactional`

`public void updateUser() {`

    `// Transaction applies here, but...`

`}`

* ✅ Transaction works in **new thread**.

* ⚠️ Parent transaction context **is not shared**.

* 🔥 **Propagations like `REQUIRED` or `SUPPORTS` won't behave as expected**.

* ⚠️ Use with caution.

✅ Use Case 3: Async triggers a method, which is `@Transactional`

java

CopyEdit

`// Controller or Caller`

`@Async`

`public void triggerUpdate() {`

    `updateService.updateUser(); // @Transactional`

`}`

* ✅ Best practice: Let async method call a transactional method.

* 🔁 Thread is free, new thread runs transactional logic.

* ✔️ Clean separation, transaction works independently.

---

🧵 Async Return Types

🔁 If method returns a result:

Use `Future` or `CompletableFuture`.

java

CopyEdit

`@Async`

`public CompletableFuture<String> performTask() {`

    `return CompletableFuture.completedFuture("done");`

`}`

* Caller can `.get()` or chain with `.thenApply()`, etc.

⛔ If method returns `void`:

* No way to get result or exception unless you handle it manually.

---

❓ Interview Question 3: How to handle exceptions in `@Async` methods?

✅ Return type methods (e.g., `CompletableFuture<String>`)

* Handle exception at `.get()` or with chained methods (`exceptionally`, etc.)

🚨 Void-returning methods:

* Exception is swallowed unless handled.

* **Two solutions**:

1. **Try-catch inside method**

java

CopyEdit

`@Async`

`public void asyncTask() {`

    `try {`

        `// risky logic`

    `} catch (Exception e) {`

        `log.error("Error occurred", e);`

    `}`

`}`

2. **Custom Async Exception Handler (preferred)**

**Step 1**: Implement `AsyncUncaughtExceptionHandler`

java

CopyEdit

`@Component`

`public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {`

    `@Override`

    `public void handleUncaughtException(Throwable ex, Method method, Object... params) {`

        `log.error("Exception in async method: {}", method.getName(), ex);`

    `}`

`}`

**Step 2**: Register with Spring

java

CopyEdit

`@Configuration`

`@EnableAsync`

`public class AppConfig implements AsyncConfigurer {`

    

    `@Autowired`

    `private CustomAsyncExceptionHandler handler;`

    `@Override`

    `public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {`

        `return handler;`

    `}`

`}`

* Spring will now use this for void-returning `@Async` methods.

---

🧠 Summary of Best Practices

| Scenario | Recommendation |
| ----- | ----- |
| Same class method call | ❌ Won't work, move method to separate class |
| Void method with exception | ✅ Use custom `AsyncUncaughtExceptionHandler` |
| Async \+ Transaction | ✅ Trigger `@Transactional` method from async class |
| Return type required | ✅ Use `CompletableFuture<T>` |
| Need chaining or advanced features | ✅ Use `CompletableFuture` instead of `Future` |

---

