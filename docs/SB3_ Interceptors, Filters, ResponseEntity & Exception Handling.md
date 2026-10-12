# 

**[Interceptors | Spring Boot: Custom Interceptors – Intercept HTTP Requests	2](#interceptors-|-spring-boot:-custom-interceptors-–-intercept-http-requests)**

[Filters vs Interceptors	7](#filters-vs-interceptors)

[**ResponseEntity & HTTP Response Codes Explained	11**](#responseentity-&-http-response-codes-explained)

[**@ExceptionHandler, @ControllerAdvice, and @ResponseStatus	18**](#@exceptionhandler,-@controlleradvice,-and-@responsestatus)

# 

# Interceptors | Spring Boot: Custom Interceptors – Intercept HTTP Requests {#interceptors-|-spring-boot:-custom-interceptors-–-intercept-http-requests}

---

🔍 What is an Interceptor?

* **Interceptor** is a component that **preprocesses** and/or **postprocesses** HTTP requests.

* Can be used to:

  * Log request/response data

  * Perform authentication or authorization

  * Modify headers or request attributes

  * Track metrics, timings, etc.

---

🧱 Types of Interceptors in Spring

1. **Handler Interceptor (HandlerInterceptorAdapter or HandlerInterceptor)**

   * Intercepts requests handled by **Spring MVC controllers**.

2. **Filter**

   * Lower-level, applies to **all** requests (Servlet level).

   * Good for CORS, logging, etc.

For Spring Boot, we usually go with `HandlerInterceptor`.

---

🛠️ Step-by-Step: Creating a Custom Interceptor

---

📌 Step 1: Create an Interceptor Class

java

CopyEdit

`@Component`

`public class CustomRequestInterceptor implements HandlerInterceptor {`

    `// Called before the controller`

    `@Override`

    `public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {`

        `System.out.println("⏳ [preHandle] Incoming request: " + request.getRequestURI());`

        `// Return false to block the request`

        `return true;`

    `}`

    `// Called after controller, before view rendering`

    `@Override`

    `public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,`

                           `ModelAndView modelAndView) throws Exception {`

        `System.out.println("✅ [postHandle] Handler executed.");`

    `}`

    `// Called after view is rendered`

    `@Override`

    `public void afterCompletion(HttpServletRequest request, HttpServletResponse response,`

                                `Object handler, Exception ex) throws Exception {`

        `System.out.println("🏁 [afterCompletion] Request completed.");`

    `}`

`}`

---

📌 Step 2: Register the Interceptor

java

CopyEdit

`@Configuration`

`public class WebMvcConfig implements WebMvcConfigurer {`

    `@Autowired`

    `private CustomRequestInterceptor customRequestInterceptor;`

    `@Override`

    `public void addInterceptors(InterceptorRegistry registry) {`

        `registry.addInterceptor(customRequestInterceptor)`

                `.addPathPatterns("/api/**") // Optional: restrict to certain paths`

                `.excludePathPatterns("/api/public/**"); // Optional: exclude paths`

    `}`

`}`

---

🧪 Test Output

When a request hits `/api/user`, console will show:

less

CopyEdit

`⏳ [preHandle] Incoming request: /api/user`

`✅ [postHandle] Handler executed.`

`🏁 [afterCompletion] Request completed.`

---

✅ Common Use Cases

| Use Case | Method |
| ----- | ----- |
| Logging requests | `preHandle` |
| Add custom headers | `postHandle` |
| Log response time | `afterCompletion` |
| Reject unauthorized access | `preHandle` (return false) |
| Track metrics/errors | `afterCompletion` |

---

⚠️ Gotchas

* Interceptors work **only on Spring MVC endpoints** (i.e., `@Controller`, `@RestController`).

* **Do not** use for heavy authentication logic → use **Spring Security filters** instead.

* For **non-blocking WebFlux**, use `WebFilter`, not interceptors.

---

## Filters vs Interceptors {#filters-vs-interceptors}

---

📌 1\. 📋 Basic Difference

| Feature | Filter | Interceptor |
| ----- | ----- | ----- |
| Layer | Servlet layer (web container) | Spring MVC layer (Controller level) |
| Interface | `javax.servlet.Filter` | `org.springframework.web.servlet.HandlerInterceptor` |
| Triggered | Before request reaches DispatcherServlet | After DispatcherServlet, before Controller |
| Applies to | All requests (static, dynamic, etc.) | Only Spring MVC requests |
| Configuration Style | Servlet API / `@Component` / `FilterRegistrationBean` | Spring MVC Config (`WebMvcConfigurer`) |
| Access to Handler | ❌ No access to controller method info | ✅ Yes, access to `HandlerMethod` |

---

🧪 2\. When Are They Called?

css

CopyEdit

`Request → Filter → DispatcherServlet → Interceptor → Controller`

---

🛠️ 3\. Code Examples

✅ Filter Example

java

CopyEdit

`@Component`

`public class MyCustomFilter implements Filter {`

    `@Override`

    `public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)`

            `throws IOException, ServletException {`

        `System.out.println("🔍 [Filter] Executing filter logic");`

        `chain.doFilter(request, response);`

    `}`

`}`

✅ Interceptor Example

java

CopyEdit

`@Component`

`public class MyInterceptor implements HandlerInterceptor {`

    `@Override`

    `public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {`

        `System.out.println("🔍 [Interceptor] Before controller method");`

        `return true;`

    `}`

`}`

---

🎯 4\. Common Use Cases

| Use Case | Filter ✅ | Interceptor ✅ |
| ----- | ----- | ----- |
| Logging all incoming requests | ✅ | ✅ |
| Authentication (basic, token) | ✅ | ✅ |
| Authorization based on roles | ❌ | ✅ |
| Modify Request / Response objects | ✅ | ❌ (not directly) |
| CORS configuration | ✅ | ❌ |
| Compression (gzip) | ✅ | ❌ |
| Response Timing / Metrics | ✅ | ✅ |
| Access to controller method info | ❌ | ✅ |

---

✅ 5\. Advantages

🧱 Filter \- Advantages

* Can be used for **any kind of request** (static files, REST APIs, etc.)

* **Closer to Servlet layer** – works outside of Spring MVC

* Ideal for **global preprocessing** (e.g., logging, security headers, compression)

🧭 Interceptor \- Advantages

* Works **after DispatcherServlet** → knows the handler method (`@RequestMapping`)

* Perfect for **controller-specific logic**

* Easily integrated with **Spring beans & DI**

---

🧠 When to Use What?

| Scenario | Recommended |
| ----- | ----- |
| Modify request/response for every request | Filter |
| Authorization based on controller path/method | Interceptor |
| Log response time for controllers only | Interceptor |
| Add security headers to all responses | Filter |
| Monitor and log request paths | Interceptor |

---

🧪 Want a Hybrid Use Case?

You can **combine both**:

* Use a **Filter** for logging \+ CORS \+ security headers

* Use an **Interceptor** for access control \+ metrics \+ request tracking

---

# `ResponseEntity` & HTTP Response Codes Explained {#responseentity-&-http-response-codes-explained}

---

✅ What is `ResponseEntity` in Spring Boot?

`ResponseEntity<T>` is a flexible way to send responses from your Spring REST controllers. It allows you to:

* Set the **status code**

* Set **headers**

* Send a **body** (optional)

> 📌 It's a powerful alternative to using just `@ResponseBody`.

---

🔧 Syntax Overview

java

CopyEdit

`return new ResponseEntity<>(body, headers, HttpStatus.OK);`

Or using builder:

java

CopyEdit

`return ResponseEntity.status(HttpStatus.CREATED).body(responseObject);`

---

🔢 HTTP Status Codes Explained

1️⃣ 1xx – Informational (Rarely Used)

| Code | Meaning | Use Case |
| ----- | ----- | ----- |
| 100 | Continue | Rarely used manually Client can send Expect header to get this status |
| 101 | Switching Protocols | WebSocket upgrades, etc. |

📝 *Spring Boot usually does not require you to return 1xx codes explicitly.*

---

2️⃣ 2xx – Success

| Code | Constant | Meaning | When to Use |
| ----- | ----- | ----- | ----- |
| 200 | `HttpStatus.OK` | Request succeeded | Default for successful GET/POST |
| 201 | `HttpStatus.CREATED` | Resource created | After a successful POST/PUT |
| 202 | `HttpStatus.ACCEPTED` | Request accepted for process | Async requests, e.g., job queuing |
| 204 | `HttpStatus.NO_CONTENT` | Success, no response body | DELETE success or update without response |

✅ Example

java

CopyEdit

`return ResponseEntity.ok(user);  // 200 OK`

`return ResponseEntity.status(HttpStatus.CREATED).body(newUser);  // 201`

---

3️⃣ 3xx – Redirection

| Code | Constant | Meaning | Use Case |
| ----- | ----- | ----- | ----- |
| 301 | `HttpStatus.MOVED_PERMANENTLY` | Resource moved | SEO / Old API version Server sends Location header for automatic redirects. Allows changing the request method from POST to GET |
| 302 | `HttpStatus.FOUND` | Temporary redirect | Short-lived redirections |
| 304 | `HttpStatus.NOT_MODIFIED` | Cached content | Used with ETags for caching |
| 308 | `HttpStatus.PERMANENT_REDIRECT` | Resource moved | Same as 301\. Doesn't allow changing the request method from POST to GET |

📝 *You rarely return these directly from controllers, but can be triggered via `RedirectView`, or Spring Security.*

---

4️⃣ 4xx – Client Errors

| Code | Constant | Meaning | When to Use |
| ----- | ----- | ----- | ----- |
| 400 | `HttpStatus.BAD_REQUEST` | Validation failed | Invalid input from client |
| 401 | `HttpStatus.UNAUTHORIZED` | Not authenticated | Requires login/token |
| 403 | `HttpStatus.FORBIDDEN` | Authenticated but not allowed | Access denied |
| 404 | `HttpStatus.NOT_FOUND` | Resource not found | Resource ID doesn’t exist |
| 409 | `HttpStatus.CONFLICT` | Conflict with state | Duplicate data, versioning conflict |
| 422 | `HttpStatus.UNPROCESSABLE_ENTITY` | Semantically invalid | Valid format, but semantically wrong |

❌ Example

java

CopyEdit

`return ResponseEntity.badRequest().body("Invalid email format"); // 400`

`return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"); // 404`

---

5️⃣ 5xx – Server Errors

| Code | Constant | Meaning | When to Use |
| ----- | ----- | ----- | ----- |
| 500 | `HttpStatus.INTERNAL_SERVER_ERROR` | Generic error | Uncaught exceptions |
| 502 | `HttpStatus.BAD_GATEWAY` | Proxy error | Gateway issues |
| 503 | `HttpStatus.SERVICE_UNAVAILABLE` | Server down | Maintenance or overload |
| 504 | `HttpStatus.GATEWAY_TIMEOUT` | Timeout in proxy/gateway | Microservice latency |

🛡 Spring Boot handles many 5xx responses via `@ControllerAdvice` and `@ExceptionHandler`.

---

🔨 How to Return Status Codes in Spring Boot

1\. Basic `ResponseEntity`:

java

CopyEdit

`@GetMapping("/user/{id}")`

`public ResponseEntity<User> getUser(@PathVariable Long id) {`

    `Optional<User> user = userService.findById(id);`

    `return user.map(ResponseEntity::ok)`

               `.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());`

`}`

2\. Using `@ResponseStatus`:

java

CopyEdit

`@ResponseStatus(HttpStatus.CREATED)`

`@PostMapping("/user")`

`public User createUser(@RequestBody User user) {`

    `return userService.save(user);`

`}`

3\. Global Exception Mapping:

java

CopyEdit

`@ControllerAdvice`

`public class GlobalExceptionHandler {`

    `@ExceptionHandler(UserNotFoundException.class)`

    `public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {`

        `return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());`

    `}`

`}`

---

🧠 Pro Tip

* Use `HttpStatus` constants over hardcoded integers for clarity and safety.

* Combine `ResponseEntity` with `@ControllerAdvice` for a clean global error-handling strategy.

* Return **precise status codes** to improve API clarity and debuggability for clients.

---

# `@ExceptionHandler`, `@ControllerAdvice`, and `@ResponseStatus` {#@exceptionhandler,-@controlleradvice,-and-@responsestatus}

Spring Boot offers a powerful and layered exception handling mechanism using annotations like `@ExceptionHandler`, `@ControllerAdvice`, and `@ResponseStatus`. It internally uses **ExceptionResolvers** to route exceptions to the appropriate handlers.

---

📚 Chapters Breakdown

---

⏱ 00:00 \- Class Hierarchy of Exception Handling

Spring’s exception handling is rooted in the `HandlerExceptionResolver` interface. The main hierarchy includes:

go

CopyEdit

`HandlerExceptionResolver (interface)`

 `└── AbstractHandlerExceptionResolver`

      `├── DefaultHandlerExceptionResolver`

      `└── ResponseStatusExceptionResolver`

 `└── ExceptionHandlerExceptionResolver`

Key Players:

* `DefaultHandlerExceptionResolver` – Handles Spring MVC defaults (e.g., `HttpRequestMethodNotSupportedException`).

* `ResponseStatusExceptionResolver` – Looks for `@ResponseStatus`.

* `ExceptionHandlerExceptionResolver` – Handles `@ExceptionHandler` in `@ControllerAdvice`.

---

⏱ 01:07 \- Flow Diagram of ExceptionResolvers

Here’s the **flow when an exception occurs**:

text

CopyEdit

`Controller throws Exception`

      `↓`

`HandlerExceptionResolverComposite`

      `↓`

`[Tries in order]`

   `1. ExceptionHandlerExceptionResolver (@ExceptionHandler/@ControllerAdvice)`

   `2. ResponseStatusExceptionResolver (@ResponseStatus)`

   `3. DefaultHandlerExceptionResolver (Internal MVC handling)`

> Spring uses **Chain of Responsibility** to check each resolver.

---

⏱ 16:55 \- Demo of ExceptionResolvers

In a Spring Boot project, try this:

java

CopyEdit

`@GetMapping("/zero")`

`public int divideByZero() {`

    `return 10 / 0;  // ArithmeticException`

`}`

Without any custom handling, Spring falls back to:

* `DefaultHandlerExceptionResolver` → returns `500 Internal Server Error`.

---

⏱ 19:54 \- `@ExceptionHandler` and `@ControllerAdvice` with Demo

🔹 `@ExceptionHandler`

java

CopyEdit

`@RestController`

`public class DemoController {`

    `@GetMapping("/user/{id}")`

    `public String getUser(@PathVariable int id) {`

        `if (id == 0) throw new IllegalArgumentException("Invalid ID");`

        `return "User";`

    `}`

    `@ExceptionHandler(IllegalArgumentException.class)`

    `public ResponseEntity<String> handleIllegalArg(Exception ex) {`

        `return ResponseEntity.badRequest().body("Bad Input: " + ex.getMessage());`

    `}`

`}`

🔹 `@ControllerAdvice`

Centralized exception handling across controllers:

java

CopyEdit

`@ControllerAdvice`

`public class GlobalExceptionHandler {`

    `@ExceptionHandler(UserNotFoundException.class)`

    `public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {`

        `return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());`

    `}`

    `@ExceptionHandler(Exception.class)`

    `public ResponseEntity<String> handleGeneral(Exception ex) {`

        `return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMessage());`

    `}`

`}`

---

⏱ 36:28 \- `@ResponseStatus` with Demo

Use this to **map an exception to an HTTP status** directly:

java

CopyEdit

`@ResponseStatus(HttpStatus.NOT_FOUND)`

`public class UserNotFoundException extends RuntimeException {`

    `public UserNotFoundException(String message) {`

        `super(message);`

    `}`

`}`

Spring’s `ResponseStatusExceptionResolver` catches this and returns `404`.

---

⏱ 54:10 \- `DefaultHandlerExceptionResolver`

This is Spring’s **last line of defense**.

Handles:

* `HttpRequestMethodNotSupportedException` → 405

* `HttpMediaTypeNotSupportedException` → 415

* `MissingServletRequestParameterException` → 400

You can override it, but typically you handle exceptions via `@ControllerAdvice` before this kicks in.

---

🧠 Summary

| Annotation / Class | Purpose |
| ----- | ----- |
| `@ExceptionHandler` | Handles exceptions locally in a controller |
| `@ControllerAdvice` | Global handler for exceptions across all controllers |
| `@ResponseStatus` | Maps exception to HTTP status without a handler method |
| `DefaultHandlerExceptionResolver` | Handles standard MVC exceptions (e.g., bad params) |

---

⚙ Best Practices

* Use `@ControllerAdvice` \+ `@ExceptionHandler` for global handling.

* Use `@ResponseStatus` for simple, declarative mapping.

* Never expose stack traces or internal messages directly.

* Customize error structure (e.g., timestamp, path, status, message).

---

