# 

**[Spring Boot JPA	3](#spring-boot-jpa)**

[JDBC Template	3](#jdbc-template)

[Setup, JPA Architecture, Entity Lifecycle	8](#setup,-jpa-architecture,-entity-lifecycle)

[First Level Caching in JPA	14](#first-level-caching-in-jpa)

[Second-Level Cache (L2)	19](#second-level-cache-\(l2\))

[Hibernate DDL Configuration	31](#hibernate-ddl-configuration)

[JPA One-to-One Mapping	44](#jpa-one-to-one-mapping)

[JPA OneToMany, ManyToOne, and ManyToMany	50](#jpa-onetomany,-manytoone,-and-manytomany)

[JPQL, Derived Query, N+1 Problem, Joins, Pagination, Sorting, and more	57](#jpql,-derived-query,-n+1-problem,-joins,-pagination,-sorting,-and-more)

[Native Query and Criteria API	64](#native-query-and-criteria-api)

[Specification API, Problem with Criteria API and its solution	70](#specification-api,-problem-with-criteria-api-and-its-solution)

# 

# Spring Boot JPA {#spring-boot-jpa}

![][image1]

## JDBC Template {#jdbc-template}

In this post, we explore how database interaction evolved from **plain JDBC** to the more efficient and developer-friendly **Spring JDBC Template**, which is often the entry point before moving into JPA/Hibernate.

---

📚 Chapters Breakdown

---

🔹 00:00 – Introduction about JDBC

**JDBC (Java Database Connectivity)** is Java’s standard API for connecting to relational databases. It provides:

* A set of interfaces (`Connection`, `Statement`, `ResultSet`, etc.)

* Ability to write SQL directly and execute it

* Vendor-specific drivers

Basic Example:

java

CopyEdit

`Connection conn = DriverManager.getConnection(url, user, password);`

`Statement stmt = conn.createStatement();`

`ResultSet rs = stmt.executeQuery("SELECT * FROM users");`

---

🔹 Plain JDBC Handling

✅ Pros:

* Complete control over SQL

* Good for learning underlying DB interaction

❌ Cons:

* Lots of boilerplate code

* Manual resource management (`try-catch-finally`)

* Tedious error handling

* Verbose mapping logic (ResultSet → Java Object)

---

🔹 Problem with Plain JDBC

* **Connection leaks** due to unclosed resources

* **Hardcoded SQL** makes refactoring painful

* **Manual object mapping** is error-prone

* **Not scalable** for large apps or rapid development

---

🔹 Spring Boot JDBC (Spring JDBC Template)

Spring solves JDBC pain points with `JdbcTemplate`, part of Spring JDBC module.

✅ What it offers:

* Auto resource management

* Preconfigured connection pooling (via `DataSource`)

* Simplified query execution

* Clean exception translation (via `DataAccessException`)

* Easy integration with Spring Boot

---

🔹 Spring Boot JDBC Helper Methods

Spring Boot auto-configures the `JdbcTemplate` bean if a datasource is available.

Example:

java

CopyEdit

`@Repository`

`public class UserRepository {`

    `@Autowired`

    `private JdbcTemplate jdbcTemplate;`

    `public List<User> findAll() {`

        `String sql = "SELECT * FROM users";`

        `return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));`

    `}`

    `public int save(User user) {`

        `String sql = "INSERT INTO users(name, email) VALUES (?, ?)";`

        `return jdbcTemplate.update(sql, user.getName(), user.getEmail());`

    `}`

`}`

🔧 Common Helper Methods:

* `query(...)` – Returns list of objects

* `queryForObject(...)` – Single object

* `update(...)` – For insert/update/delete

* `batchUpdate(...)` – Bulk operations

Row Mapping:

* `BeanPropertyRowMapper` – Maps ResultSet to POJO automatically

* `RowMapper` – Interface for custom mapping logic

---

🧠 Summary

| Feature | Plain JDBC | Spring JDBC Template |
| ----- | ----- | ----- |
| Boilerplate | High | Minimal |
| Resource Handling | Manual | Automatic |
| SQL Writing | Manual | Manual but concise |
| Exception Translation | Raw SQLExceptions | Spring's DataAccessException |
| Object Mapping | Manual | Built-in with mappers |

---

✅ When to use Spring JDBC over JPA?

* Lightweight apps with limited CRUD

* Performance-critical queries

* Full SQL control needed

* No ORM requirements

---

## Setup, JPA Architecture, Entity Lifecycle {#setup,-jpa-architecture,-entity-lifecycle}

![][image2]

In this part, we dive deeper into **JPA's architecture**, understand its components like **EntityManager**, **Persistence Context**, and the **lifecycle of an entity**. You’ll also see how Spring Boot wires everything for a seamless experience.

---

📚 Chapters Breakdown

---

🔹 00:00 – Introduction about ORM

**Object Relational Mapping (ORM)** allows mapping Java objects to database tables automatically.

* Java Class ↔ DB Table

* Java Field ↔ DB Column

Popular ORMs:

* Hibernate (most common JPA implementation)

* EclipseLink, OpenJPA

---

🔹 Setup of JPA Happy Flow

Spring Boot makes JPA setup painless:

Dependencies:

xml

CopyEdit

`<dependency>`

  `<groupId>org.springframework.boot</groupId>`

  `<artifactId>spring-boot-starter-data-jpa</artifactId>`

`</dependency>`

`<dependency>`

  `<groupId>com.h2database</groupId>`

  `<artifactId>h2</artifactId>`

  `<scope>runtime</scope>`

`</dependency>`

Application Properties:

properties

CopyEdit

`spring.datasource.url=jdbc:h2:mem:testdb`

`spring.jpa.hibernate.ddl-auto=update`

`spring.jpa.show-sql=true`

Entity Example:

java

CopyEdit

`@Entity`

`public class User {`

    `@Id`

    `@GeneratedValue`

    `private Long id;`

    `private String name;`

`}`

---

🔹 Architecture of JPA

text

CopyEdit

`Your App`

   `|`

   `| --> EntityManager (API)`

   `|         |`

   `|         --> Persistence Context`

   `|         --> Interacts with database via JPA Provider (e.g., Hibernate)`

   `|`

`JPA Provider (Hibernate)`

   `|`

   `--> JDBC --> Database`

---

🔹 What is Persistence Unit in JPA

A **Persistence Unit** defines a set of entities and configuration for interacting with a DB.

In Spring Boot, this is **auto-configured** using application properties.

In plain JPA (non-Spring), you define it in `persistence.xml`:

xml

CopyEdit

`<persistence-unit name="myUnit">`

  `<class>com.example.User</class>`

`</persistence-unit>`

---

🔹 What is EntityManagerFactory

* It’s a **factory** for creating `EntityManager` instances.

* Heavyweight and thread-safe.

* Created once per persistence unit.

In Spring Boot, it’s auto-configured by the `LocalContainerEntityManagerFactoryBean`.

---

🔹 Transaction Manager Association with EntityManagerFactory

Spring Boot binds `EntityManagerFactory` with `JpaTransactionManager`.

This allows:

* Declarative transactions with `@Transactional`

* Auto rollback on exceptions

* Thread-bound `EntityManager`

---

🔹 EntityManager and PersistenceContext

`EntityManager`:

* Interface for CRUD, JPQL, native queries.

* Manages entity lifecycle.

`PersistenceContext`:

* First-level cache of managed entities.

* Keeps entities in sync with DB.

* Injected by Spring into services via:

java

CopyEdit

`@PersistenceContext`

`private EntityManager entityManager;`

---

🔹 Lifecycle of an Entity

1. **Transient** – New object, not associated with DB.

2. **Persistent** – Managed by `EntityManager`, will be synced to DB.

3. **Detached** – Disconnected from `EntityManager`.

4. **Removed** – Scheduled for deletion.

Lifecycle Flow:

text

CopyEdit

`new → persist() → managed → remove() → removed → (flush) → deleted`

Example:

java

CopyEdit

`User user = new User("John");      // Transient`

`em.persist(user);                  // Persistent`

`em.detach(user);                   // Detached`

`em.merge(user);                    // Back to Persistent`

`em.remove(user);                   // Removed`

---

🧠 Summary

| Concept | Description |
| ----- | ----- |
| `EntityManager` | Interface to interact with DB |
| `EntityManagerFactory` | Produces `EntityManager`, heavyweight |
| `PersistenceContext` | Manages entity states (first-level cache) |
| `Persistence Unit` | Group of entities and config |
| `@Transactional` | Binds persistence and transaction management |

---

## First Level Caching in JPA {#first-level-caching-in-jpa}

🎯 Objective

Understand how **first level caching** works in JPA using **EntityManager** and **PersistenceContext**, and why no additional DB calls are triggered for repeat queries in the same transaction.

---

📌 Quick Recap – JPA Lifecycle (in short)

You create a new entity:

 java  
CopyEdit  
`User a = new User(); // NEW state`

* 

Begin transaction and persist:

 java  
CopyEdit  
`entityManager.persist(a); // MANAGED state`

* 

Remove entity:

 java  
CopyEdit  
`entityManager.remove(a); // REMOVED state`

* 

Persist again before commit:

 java  
CopyEdit  
`entityManager.persist(a); // Back to MANAGED`

*   
* Finally, on `commit()`, flush happens, and entity is synced with DB.

**Key Point:** Until flush/commit, all changes live **only in memory**, managed by the **Persistence Context**.

---

🔁 What is First Level Cache?

> First level cache is enabled by default and scoped to the **EntityManager**.

* EntityManager keeps a **PersistenceContext (in-memory map)** of all managed entities.

* When you fetch the same entity again in the same transaction, **JPA doesn't hit the database**.

* Instead, it **returns the object from the cache**.

---

⚙️ Working Example

Suppose your flow is:

java

CopyEdit

`userService.saveUser();      // INSERT`

`userService.getUserById(1); // SELECT?`

Behind the scenes:

1. **Save** calls `entityManager.persist(user)`

   * Adds the entity to PersistenceContext

   * Still not flushed to DB unless `@Transactional` commits

2. **Get by ID** (`findById(1)`)

   * JPA **first checks PersistenceContext**

   * Since user was just saved (and not detached), it's found in cache

   * **No DB SELECT query is executed\!**

---

🔍 How is EntityManager created?

* **One EntityManager per HTTP Request**

* Managed by Spring’s transaction manager (via DispatcherServlet)

* Inside `DispatcherServlet` → `applyPreHandle()` creates an EntityManager from the factory

**So:**  
 ✅ Same HTTP request → Same EntityManager → Same PersistenceContext  
 🚫 Different HTTP request → Different EntityManager → Separate context → Hits DB

---

🧪 Testing the Caching

Case 1: Save \+ Get in same HTTP request

* **Insert:** Yes

* **Select:** ❌ No select query (fetched from cache)

Case 2: New HTTP request, only getUser(1)

* New request → new EntityManager

* **Select:** ✅ Required (PersistenceContext is new)

* Subsequent calls in same request? ❌ No new select

---

🧠 Deep Understanding

💡 Inside JPA's Flow:

* `entityManager.persist()` → goes to Hibernate's `SessionImpl`

* `SessionImpl` → Adds entity to a **Map\<K, V\>**

  * `K` \= Entity class \+ ID

  * `V` \= Managed object instance

When you do `findById()`:

* Checks the map first

* If present → return

* Else → SQL query → add to map → return

---

👥 Multiple EntityManager Behavior

* Two EntityManagers \= Two isolated PersistenceContexts

* Data in one is not visible to the other

* That’s why even if you haven’t closed EM1, EM2 can’t reuse its cache

---

🧪 Manual EM Demo (Programmatic)

java

CopyEdit

`EntityManager em1 = emf.createEntityManager();`

`em1.getTransaction().begin();`

`em1.persist(user);              // Insert`

`em1.find(User.class, user.getId()); // From cache`

`em1.find(User.class, user.getId()); // From cache`

`em1.getTransaction().commit();`

`em1.close();`

`EntityManager em2 = emf.createEntityManager();`

`em2.getTransaction().begin();`

`em2.find(User.class, user.getId()); // Select from DB`

`em2.find(User.class, user.getId()); // From cache`

`em2.getTransaction().commit();`

`em2.close();`

📌 Observations

* EM1 – no select

* EM2 – one select (first call), second from cache

---

📦 Conclusion

✅ **First Level Cache** is:

* Always ON by default

* Bound to one EntityManager

* Resets with each new HTTP request

🚫 Does **not** work across requests or across multiple EntityManagers  
 👉 That’s where **Second Level Caching** helps (we’ll explore that next\!)

---

## Second-Level Cache (L2)  {#second-level-cache-(l2)}

First, Quick Recap of First-Level Cache (L1)

* **L1 Cache is enabled by default.**

* It is **associated with a specific `EntityManager`**.

* One HTTP request \= one EntityManager \= one persistence context.

* L1 cache **lives during a transaction/request** and is **not shared**.

* It is cleared after transaction/session ends.

> Drawback: If another request comes in, it won’t find any data from previous request → forces a DB hit.

---

🧠 What is Second-Level Cache (L2)?

> **Second-Level Cache is a shared cache across multiple sessions (EntityManagers).**

| Feature | L1 Cache | L2 Cache |
| ----- | ----- | ----- |
| Scope | Per `EntityManager` (transaction/request) | Across `EntityManager`s (application-wide) |
| Default Behavior | Enabled | **Disabled** (must be configured) |
| Purpose | Reduce repeated DB hits within a transaction | Reduce DB hits across transactions |
| Storage Mechanism | In-memory map (temporary) | Pluggable cache providers (Ehcache, Hazelcast, Redis, etc.) |

---

✅ When to Use Second-Level Cache?

* Frequently accessed entities that rarely change.

* Read-heavy applications (product catalog, user profiles).

* Use it **with caution** for data that updates frequently → may lead to stale data unless managed well.

---

🔧 How to Enable Second-Level Caching in Spring Boot (Hibernate)

Step 1: Add Cache Provider (e.g., Ehcache)

In `pom.xml`:

xml

CopyEdit

`<dependency>`

    `<groupId>org.ehcache</groupId>`

    `<artifactId>ehcache</artifactId>`

`</dependency>`

---

Step 2: Enable Caching in `application.properties` or `application.yml`

properties

CopyEdit

`spring.jpa.properties.hibernate.cache.use_second_level_cache=true`

`spring.jpa.properties.hibernate.cache.region.factory_class=org.hibernate.cache.jcache.JCacheRegionFactory`

`spring.cache.jcache.config=classpath:ehcache.xml`

---

Step 3: Configure Entity to be Cacheable

java

CopyEdit

`@Entity`

`@Cacheable`

`@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)`

`public class Product {`

    `@Id`

    `private Long id;`

    `private String name;`

`}`

> You can also use other strategies like `READ_WRITE`, `NONSTRICT_READ_WRITE`, `TRANSACTIONAL`.

---

Step 4: Add `ehcache.xml` in `src/main/resources`

xml

CopyEdit

`<config xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'`

        `xmlns='http://www.ehcache.org/v3'`

        `xsi:schemaLocation="http://www.ehcache.org/v3 http://www.ehcache.org/schema/ehcache-core-3.0.xsd">`

    `<cache alias="com.example.demo.entity.Product">`

        `<heap>100</heap>`

        `<expiry>`

            `<ttl unit="minutes">10</ttl>`

        `</expiry>`

    `</cache>`

`</config>`

> The cache alias should match the **fully qualified class name** of the entity.

---

Step 5: How It Works

1. First time you fetch an entity → **select query runs**, entity goes to:

   * L1 cache (for this transaction)

   * L2 cache (if enabled)

2. Subsequent requests:

   * Hibernate checks **L1** → if not found,

   * Hibernate checks **L2** → if found, **no DB hit**.

3. If **not found in L2**, Hibernate hits DB and then stores in L2 for future use.

---

🧪 Test Example

java

CopyEdit

`@RestController`

`public class ProductController {`

    `@Autowired private ProductRepository productRepo;`

    `@GetMapping("/product/{id}")`

    `public Product getProduct(@PathVariable Long id) {`

        `return productRepo.findById(id).orElse(null);`

    `}`

`}`

* First time: `SELECT` query logged.

* Second time (same ID): **No query** if L2 cache hits.

---

⚠️ Important Considerations

* Cache consistency: If you update/delete frequently, **invalidate** or **evict** from cache.

* Avoid caching large datasets.

* Not all repositories or queries are cacheable (e.g., native queries).

* Enable Hibernate statistics to debug cache hit/miss:

properties

CopyEdit

`spring.jpa.properties.hibernate.generate_statistics=true`

---

🧩 Popular L2 Cache Providers

| Cache Provider | Notes |
| ----- | ----- |
| **Ehcache** | Embedded, easy for single-node |
| **Hazelcast** | Distributed in-memory grid |
| **Redis** | Great for distributed apps |
| **Caffeine** | Super-fast, in-memory |

---

🧠 Summary

* **First-Level Cache**: short-lived, per-transaction.

* **Second-Level Cache**: app-wide, shared across sessions.

* Can **dramatically reduce DB load** if used wisely.

* **Not a replacement** for good query and DB design.

* Great for read-heavy apps with stable data.

---

Let me know if you want a live code demo or a GitHub repo setup example for this\!

4o

You said:

explain regions and concurrenacy also

ChatGPT said:

Perfect — let's get into the **L2 Caching Regions** and **Concurrency Strategies** in Hibernate. These are key concepts to get caching right and avoid headaches like stale data or race conditions.

---

🗂️ What are Cache *Regions*?

Think of **regions** as **namespaces or logical partitions** within the second-level cache.

✅ Why Regions?

* Help segregate data per entity or query.

* Allow **custom cache configurations** per region.

* Improve **eviction and expiry control**.

🧠 Examples of Regions:

* One region per entity:  
   e.g., `com.example.demo.entity.Product`

* One region per collection (if you cache relationships):  
   e.g., `com.example.demo.entity.Product.categories`

* One region per query result:  
   For query caching (different from entity cache).

---

⚙️ How Regions Are Named

Hibernate automatically generates region names using the **FQCN of the entity** or collection. You can override it:

java

CopyEdit

`@Cache(`

    `usage = CacheConcurrencyStrategy.READ_WRITE,`

    `region = "my_product_region"`

`)`

> Now you can define separate settings for `"my_product_region"` in your `ehcache.xml`.

---

🔐 What is Cache Concurrency Strategy?

This tells Hibernate **how to manage cached data when multiple threads or transactions access/modify it concurrently.**

Think of it as your cache’s "transaction isolation level."

🔸 Available Strategies:

| Strategy | Description |
| ----- | ----- |
| **READ\_ONLY** | Best for static data. No updates allowed. Super fast. |
| **NONSTRICT\_READ\_WRITE** | Data can become stale. OK for rarely updated data. No locking. |
| **READ\_WRITE** | Uses soft locking. Safer for updates. Slower. |
| **TRANSACTIONAL** | Uses JTA transactions. Only works with specific providers like JBoss. Most strict and safest. |

---

🔍 Breakdown of Strategies

✅ `READ_ONLY`

* Data is **never updated** via Hibernate.

* Great for **lookup tables**, **reference data**, etc.

* Throws exception if entity is modified and saved.

java

CopyEdit

`@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)`

---

⚠️ `NONSTRICT_READ_WRITE`

* No locking. Hibernate just **evicts** cache on updates.

* Data **may be stale** between update and eviction.

* Use when **updates are rare** and **slight staleness is acceptable**.

java

CopyEdit

`@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)`

---

🛡️ `READ_WRITE`

* Hibernate **soft-locks** the cache entry before updating.

* Prevents dirty reads.

* Safer for **mutable** entities.

* Slight performance overhead due to locks.

java

CopyEdit

`@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)`

> Requires proper equals/hashCode implementation for dirty check.

---

🔒 `TRANSACTIONAL`

* Requires **JTA (Java Transaction API)**.

* Most strict — updates go through transactional cache (e.g., JBoss Cache, Infinispan).

* **Not commonly used** in simple Spring Boot \+ Hibernate apps.

---

⚖️ Which Strategy to Choose?

| Use Case | Recommended Strategy |
| ----- | ----- |
| Static / Immutable data | `READ_ONLY` |
| Read-mostly data with infrequent writes | `NONSTRICT_READ_WRITE` |
| Frequently updated shared data | `READ_WRITE` |
| Full JTA setup with distributed caching | `TRANSACTIONAL` (rare) |

---

👀 Example:

java

CopyEdit

`@Entity`

`@Cacheable`

`@Cache(`

    `usage = CacheConcurrencyStrategy.READ_WRITE,`

    `region = "productRegion"`

`)`

`public class Product {`

    `@Id`

    `private Long id;`

    `private String name;`

`}`

And in `ehcache.xml`:

xml

CopyEdit

`<cache alias="productRegion">`

    `<heap>100</heap>`

    `<expiry>`

        `<ttl unit="minutes">10</ttl>`

    `</expiry>`

`</cache>`

---

🧠 Summary

* **Regions** help isolate and configure cache per entity/collection/query.

* **Concurrency strategies** balance **data consistency vs performance**.

* Choose wisely based on:

  * How often your data changes.

  * Whether your app can tolerate stale reads.

  * Your performance/consistency needs.

---

---

## Hibernate DDL Configuration {#hibernate-ddl-configuration}

JPA uses **Hibernate’s DDL auto** feature to automatically generate/update the database schema from your entity classes.

Common `spring.jpa.hibernate.ddl-auto` Options:

* `none` – No action

* `validate` – Validates schema

* `update` – Updates schema (good for dev)

* `create` – Drops and recreates schema on startup

* `create-drop` – Same as `create`, but also drops schema on shutdown

yaml

CopyEdit

`spring:`

  `jpa:`

    `hibernate:`

      `ddl-auto: update`

---

🏷️ `@Table` Annotation

Maps an entity class to a specific database **table**.

java

CopyEdit

`@Entity`

`@Table(name = "products")`

`public class Product {`

    `...`

`}`

✅ Optional Attributes:

* `name` – Table name

* `schema` – Schema name

* `catalog` – Catalog name

* `uniqueConstraints` – Unique constraints

---

📌 `@Column` Annotation

Maps a Java field to a specific **column** in the DB.

java

CopyEdit

`@Column(name = "product_name", nullable = false, length = 100)`

`private String name;`

✅ Common Attributes:

* `name` – DB column name

* `nullable` – Can be null?

* `length` – Max length (esp. for VARCHAR)

* `unique`, `insertable`, `updatable`, etc.

---

🆔 `@Id` Annotation

Marks a field as the **primary key**.

java

CopyEdit

`@Id`

`private Long id;`

Can be used with `@GeneratedValue` for auto-generated keys.

---

🔑 `@IdClass` – Composite Primary Key (External Class Approach)

Used when a primary key is made up of **multiple columns**.

1️⃣ Step 1: Create the PK Class

java

CopyEdit

`public class OrderPK implements Serializable {`

    `private Long orderId;`

    `private Long productId;`

    `// equals() and hashCode() are required`

`}`

2️⃣ Step 2: Annotate Entity with `@IdClass`

java

CopyEdit

`@Entity`

`@IdClass(OrderPK.class)`

`public class Order {`

    `@Id`

    `private Long orderId;`

    `@Id`

    `private Long productId;`

    `private int quantity;`

`}`

---

🧩 `@Embeddable` – Composite Key (Embedded Class Approach)

Alternative to `@IdClass`. Cleaner and more object-oriented.

1️⃣ Define Embeddable Class

java

CopyEdit

`@Embeddable`

`public class OrderKey implements Serializable {`

    `private Long orderId;`

    `private Long productId;`

    `// equals() and hashCode()`

`}`

2️⃣ Embed the Key in the Entity

java

CopyEdit

`@Entity`

`public class Order {`

    `@EmbeddedId`

    `private OrderKey id;`

    `private int quantity;`

`}`

> This way, your entity holds the composite key as a single embedded object.

---

🔁 `@GeneratedValue` – Auto-generated Primary Keys

Used to auto-generate primary key values (identity, sequence, or table-based).

java

CopyEdit

`@Id`

`@GeneratedValue(strategy = GenerationType.IDENTITY)`

`private Long id;`

Strategies:

| Strategy | Description |
| ----- | ----- |
| `AUTO` | Hibernate chooses (default) |
| `IDENTITY` | Uses DB identity column (e.g., MySQL) |
| `SEQUENCE` | Uses DB sequence (e.g., PostgreSQL) |
| `TABLE` | Uses a table to simulate sequence |

---

✅ Full Example

java

CopyEdit

`@Entity`

`@Table(name = "products")`

`public class Product {`

    `@Id`

    `@GeneratedValue(strategy = GenerationType.IDENTITY)`

    `private Long id;`

    `@Column(name = "product_name", nullable = false, length = 100)`

    `private String name;`

    `@Column(nullable = false)`

    `private Double price;`

`}`

---

🧠 Summary

| Annotation | Purpose |
| ----- | ----- |
| `@Entity` | Marks a class as a JPA entity |
| `@Table` | Maps entity to DB table |
| `@Column` | Maps field to DB column |
| `@Id` | Marks primary key |
| `@GeneratedValue` | Auto-generates PK |
| `@IdClass` | Composite key using separate class |
| `@Embeddable` | Composite key as embedded class |

---

Let me know if you want:

* A code demo with `@Embeddable` vs `@IdClass`

* A Spring Boot project template with DDL generation and sample DTOs

* A cheatsheet image for these annotations

Want me to summarize this in a table format or generate visual diagrams?

4o

You said:

Give me more detailed notes

ChatGPT said:

Memory updated

Here are detailed notes for your Spring Boot JPA (Part-5) content covering entity-to-table mapping using annotations:

---

🔧 Hibernate DDL Configuration (hibernate.hbm2ddl.auto)

This property defines how the database schema is handled:

* `none` – No action.

* `create` – Drops and re-creates schema every time.

* `create-drop` – Creates schema at startup, drops it at shutdown.

* `update` – Updates schema based on entity changes (not suitable for production).

* `validate` – Validates schema with entity mappings.

* `auto` – Alias for one of the above.

Example in `application.properties`:

properties

CopyEdit

`spring.jpa.hibernate.ddl-auto=update`

---

🏷️ `@Table` Annotation

Maps an entity class to a database table.

java

CopyEdit

`@Entity`

`@Table(name = "employees")`

`public class Employee {`

    `// fields`

`}`

* Optional attributes: `catalog`, `schema`, `uniqueConstraints`, `indexes`.

---

📄 `@Column` Annotation

Maps a Java field to a specific database column.

java

CopyEdit

`@Column(name = "emp_name", nullable = false, length = 100)`

`private String name;`

* `name`: DB column name.

* `nullable`: if `false`, generates `NOT NULL`.

* `length`: for `VARCHAR` columns.

* `unique`, `precision`, `scale`: control DDL further.

---

🔑 `@Id` Annotation

Marks a field as the primary key.

java

CopyEdit

`@Id`

`private Long id;`

---

🔑 `@IdClass` Annotation (Composite Keys)

Used to define composite primary keys using a separate class.

java

CopyEdit

`@Entity`

`@IdClass(OrderId.class)`

`public class Order {`

    `@Id`

    `private Long orderId;`

    `@Id`

    `private Long productId;`

`}`

java

CopyEdit

`public class OrderId implements Serializable {`

    `private Long orderId;`

    `private Long productId;`

    `// equals() and hashCode() must be overridden`

`}`

---

🧩 `@Embeddable` and `@EmbeddedId` (Alternative Composite Key Handling)

More elegant than `@IdClass`. Composite key is an object.

java

CopyEdit

`@Embeddable`

`public class OrderId implements Serializable {`

    `private Long orderId;`

    `private Long productId;`

`}`

java

CopyEdit

`@Entity`

`public class Order {`

    `@EmbeddedId`

    `private OrderId id;`

`}`

---

🔢 `@GeneratedValue` Annotation

Used with `@Id` to auto-generate primary key values.

java

CopyEdit

`@Id`

`@GeneratedValue(strategy = GenerationType.IDENTITY)`

`private Long id;`

Strategies:

* `AUTO`: Let the persistence provider decide.

* `IDENTITY`: Auto-increment in DB (MySQL).

* `SEQUENCE`: Uses a DB sequence (PostgreSQL).

* `TABLE`: Uses a separate table to generate IDs.

---

## JPA One-to-One Mapping {#jpa-one-to-one-mapping}

---

✅ 1\. OneToOne Unidirectional Mapping

**Description**: One entity has a reference to another, but not vice versa.

java

CopyEdit

`@Entity`

`public class User {`

    `@Id`

    `@GeneratedValue(strategy = GenerationType.IDENTITY)`

    `private Long id;`

    `private String username;`

    `@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)`

    `@JoinColumn(name = "profile_id") // FK column in User table`

    `private UserProfile profile;`

`}`

java

CopyEdit

`@Entity`

`public class UserProfile {`

    `@Id`

    `@GeneratedValue(strategy = GenerationType.IDENTITY)`

    `private Long id;`

    `private String bio;`

    `private String phone;`

`}`

* `@JoinColumn(name = "profile_id")` tells Hibernate to add a foreign key in the `User` table referencing `UserProfile`.

* This is **unidirectional**, as `UserProfile` doesn’t know about `User`.

---

🔁 2\. CascadeType Explained

Used to propagate operations (like persist, delete) to the associated entity.

| Type | Description |
| ----- | ----- |
| `ALL` | All operations (save, update, delete, etc.) |
| `PERSIST` | Save child when parent is saved |
| `MERGE` | Update child when parent is updated |
| `REMOVE` | Delete child when parent is deleted |
| `DETACH` | Detach child entity |
| `REFRESH` | Refresh child when parent is refreshed |

> Best practice: Be careful using `CascadeType.ALL` for relationships with shared entities.

---

⏳ 3\. Eager vs Lazy Loading

* `EAGER`: Load associated entity immediately with the main entity.

* `LAZY`: Load associated entity **only** when accessed (requires proxying).

java

CopyEdit

`@OneToOne(fetch = FetchType.EAGER)`

`private UserProfile profile;`

By default, `@OneToOne` is `EAGER` (you can override it).

---

🔄 4\. OneToOne Bidirectional Mapping

**Description**: Both entities refer to each other.

java

CopyEdit

`@Entity`

`public class User {`

    `@Id`

    `@GeneratedValue`

    `private Long id;`

    `private String username;`

    `@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)`

    `private UserProfile profile;`

`}`

java

CopyEdit

`@Entity`

`public class UserProfile {`

    `@Id`

    `@GeneratedValue`

    `private Long id;`

    `private String bio;`

    `@OneToOne`

    `@JoinColumn(name = "user_id")`

    `private User user;`

`}`

* `mappedBy = "user"` in `User` indicates that the mapping is owned by `UserProfile`.

* `JoinColumn` is defined only on the **owning side** (`UserProfile` here).

---

⚠️ 5\. Infinite Recursion Problem in Bidirectional Mapping

This happens during **serialization** (e.g., returning entities in a REST API using Jackson).

Example:

json

CopyEdit

`{`

  `"user": {`

    `"profile": {`

      `"user": {`

        `"profile": {`

          `...`

        `}`

      `}`

    `}`

  `}`

`}`

---

✅ 6\. Solution: Prevent Infinite Recursion

🔁 a. Use Jackson Annotations

java

CopyEdit

`@Entity`

`public class User {`

    `@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)`

    `@JsonManagedReference`

    `private UserProfile profile;`

`}`

java

CopyEdit

`@Entity`

`public class UserProfile {`

    `@OneToOne`

    `@JoinColumn(name = "user_id")`

    `@JsonBackReference`

    `private User user;`

`}`

* `@JsonManagedReference` → forward part of reference (parent)

* `@JsonBackReference` → back part (child)

✅ b. Alternative: `@JsonIgnore`

Use on one side (usually the back-reference):

java

CopyEdit

`@JsonIgnore`

`private User user;`

---

## JPA OneToMany, ManyToOne, and ManyToMany {#jpa-onetomany,-manytoone,-and-manytomany}

---

🔸 1\. OneToMany Unidirectional

**Owner has the list, child doesn't reference the parent.**

java

CopyEdit

`@Entity`

`public class Department {`

    `@Id`

    `@GeneratedValue`

    `private Long id;`

    `private String name;`

    `@OneToMany(cascade = CascadeType.ALL)`

    `@JoinColumn(name = "department_id") // FK in Employee table`

    `private List<Employee> employees = new ArrayList<>();`

`}`

java

CopyEdit

`@Entity`

`public class Employee {`

    `@Id`

    `@GeneratedValue`

    `private Long id;`

    `private String name;`

`}`

🔹 `@JoinColumn` must be used to tell Hibernate where the FK is. 🔹 No reference of `Department` in `Employee`.

---

🧹 2\. Orphan Removal

* If an `Employee` is removed from the `employees` list and you use `orphanRemoval = true`, it’s **deleted from the DB**.

java

CopyEdit

`@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)`

`@JoinColumn(name = "department_id")`

`private List<Employee> employees;`

---

🔁 3\. OneToMany Bidirectional

Now both parent and child reference each other.

java

CopyEdit

`@Entity`

`public class Department {`

    `@OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)`

    `private List<Employee> employees;`

`}`

java

CopyEdit

`@Entity`

`public class Employee {`

    `@ManyToOne`

    `@JoinColumn(name = "department_id")`

    `private Department department;`

`}`

✔ `mappedBy` is used on the **inverse side** (Department). ✔ `@JoinColumn` stays on the **owning side** (Employee).

---

🔁 4\. ManyToOne Unidirectional and Bidirectional

🔸 Unidirectional (Child has the reference)

java

CopyEdit

`@Entity`

`public class Employee {`

    `@ManyToOne`

    `@JoinColumn(name = "department_id")`

    `private Department department;`

`}`

🔄 Bidirectional (Parent has list, child has reference)

java

CopyEdit

`@Entity`

`public class Department {`

    `@OneToMany(mappedBy = "department")`

    `private List<Employee> employees;`

`}`

java

CopyEdit

`@Entity`

`public class Employee {`

    `@ManyToOne`

    `@JoinColumn(name = "department_id")`

    `private Department department;`

`}`

---

🔁 5\. ManyToMany Unidirectional

Each entity has a list of the other, but in **unidirectional**, only one side knows.

java

CopyEdit

`@Entity`

`public class Student {`

    `@ManyToMany`

    `@JoinTable(`

        `name = "student_course",`

        `joinColumns = @JoinColumn(name = "student_id"),`

        `inverseJoinColumns = @JoinColumn(name = "course_id")`

    `)`

    `private List<Course> courses;`

`}`

java

CopyEdit

`@Entity`

`public class Course {`

    `@Id`

    `@GeneratedValue`

    `private Long id;`

    `private String name;`

`}`

➡ Creates a `student_course` join table.

---

🔁 6\. ManyToMany Bidirectional

Both sides maintain a list.

java

CopyEdit

`@Entity`

`public class Student {`

    `@ManyToMany`

    `@JoinTable(`

        `name = "student_course",`

        `joinColumns = @JoinColumn(name = "student_id"),`

        `inverseJoinColumns = @JoinColumn(name = "course_id")`

    `)`

    `private List<Course> courses;`

`}`

java

CopyEdit

`@Entity`

`public class Course {`

    `@ManyToMany(mappedBy = "courses")`

    `private List<Student> students;`

`}`

➡ One side owns the relation (`Student`), the other is inverse (`Course` with `mappedBy`).

---

⚠️ Tips & Best Practices

| Concept | Key Point |
| ----- | ----- |
| Owning side | Always defines `@JoinColumn` or `@JoinTable` |
| mappedBy | Used on inverse side (non-owning) |
| CascadeType | Use `CascadeType.ALL` carefully in shared relationships |
| orphanRemoval | Only deletes if child is **no longer referenced** |
| Lazy vs Eager | Relationships are **lazy** by default (except OneToOne) |
| Infinite Recursion | Use `@JsonManagedReference`, `@JsonBackReference`, or `@JsonIgnore` for serialization |

---

## JPQL, Derived Query, N+1 Problem, Joins, Pagination, Sorting, and more {#jpql,-derived-query,-n+1-problem,-joins,-pagination,-sorting,-and-more}

---

🧱 1\. Setting Up the Context

Typical setup includes:

* `Spring Data JPA`

* `JpaRepository`/`CrudRepository`

* Entities with relationships

* Sample data preloaded using `data.sql` or CommandLineRunner

---

🔍 2\. Derived Queries

Spring Data JPA generates SQL from method names.

**Examples:**

java

CopyEdit

`List<Employee> findByDepartment(String department);`

`List<Employee> findBySalaryGreaterThan(Double salary);`

`List<Employee> findByFirstNameAndLastName(String firstName, String lastName);`

💡 Supports `And`, `Or`, `Not`, `Like`, `In`, `Between`, `IsNull`, etc.

---

📄 3\. Pagination and Sorting

Return type should be `Page<T>` or `Slice<T>`.

java

CopyEdit

`Page<Employee> findAll(Pageable pageable);`

✅ Pagination:

java

CopyEdit

`PageRequest.of(page, size)`

✅ Sorting:

java

CopyEdit

`Sort.by("salary").descending()`

**Example:**

java

CopyEdit

`Page<Employee> page = repo.findAll(PageRequest.of(0, 10, Sort.by("name")));`

---

🔎 4\. JPQL (Java Persistence Query Language)

Object-oriented version of SQL.

**Example:**

java

CopyEdit

`@Query("SELECT e FROM Employee e WHERE e.salary > :salary")`

`List<Employee> getEmployeesWithSalaryAbove(@Param("salary") double salary);`

* JPQL uses **entity names**, not table names.

* Fields used must match the **Java field names**.

---

🔗 5\. Joins in JPQL

You can join entity relationships in JPQL.

java

CopyEdit

`@Query("SELECT e FROM Employee e JOIN e.department d WHERE d.name = :deptName")`

`List<Employee> findByDepartmentName(@Param("deptName") String deptName);`

Types:

* `JOIN`: Inner join

* `LEFT JOIN`: Include nulls from the right side

* `FETCH JOIN`: Load child eagerly to solve N+1

---

🐛 6\. N+1 Problem

Occurs when you fetch a list of entities and JPA fires additional queries for each associated entity.

java

CopyEdit

`List<Employee> employees = employeeRepo.findAll(); // 1 query`

`for (Employee e : employees) {`

    `System.out.println(e.getDepartment().getName()); // N queries`

`}`

**Total Queries:** `1 (main) + N (lazy fetches)` — leads to **performance issues**.

---

✅ 7\. Solution to N+1 Problem

A. Eager Fetching (Not recommended globally)

java

CopyEdit

`@OneToMany(fetch = FetchType.EAGER)`

B. Fetch Join (Recommended)

java

CopyEdit

`@Query("SELECT e FROM Employee e JOIN FETCH e.department")`

`List<Employee> getAllWithDepartment();`

💡 Uses **single query** to load employee and department.

C. EntityGraph

java

CopyEdit

`@EntityGraph(attributePaths = {"department"})`

`List<Employee> findAll();`

---

🛠️ 8\. @Modifying Annotation

Used for **update/delete queries** in JPQL.

java

CopyEdit

`@Modifying`

`@Query("UPDATE Employee e SET e.salary = :salary WHERE e.id = :id")`

`int updateSalary(@Param("id") Long id, @Param("salary") Double salary);`

➡ Must be used with `@Transactional`

java

CopyEdit

`@Transactional`

`@Modifying`

`...`

---

📛 9\. @NamedQuery Annotation

Defined directly on the entity class.

java

CopyEdit

`@Entity`

`@NamedQuery(`

    `name = "Employee.findByName",`

    `query = "SELECT e FROM Employee e WHERE e.name = :name"`

`)`

`public class Employee {`

    `...`

`}`

Usage:

java

CopyEdit

`@Query(name = "Employee.findByName")`

`List<Employee> findByName(@Param("name") String name);`

---

🔁 Summary Table

| Feature | Key Point |
| ----- | ----- |
| Derived Queries | Auto-generated based on method names |
| Pagination & Sorting | Uses `Pageable` and `Sort` |
| JPQL | Object-oriented queries on entities |
| Joins | `JOIN`, `LEFT JOIN`, `FETCH JOIN` |
| N+1 Problem | Caused by lazy loading |
| Fix N+1 | Use `JOIN FETCH` or `@EntityGraph` |
| @Modifying | Enables update/delete queries |
| @NamedQuery | Predefined JPQL in entity class |

---

## Native Query and Criteria API {#native-query-and-criteria-api}

---

📌 1\. Native Query

Allows execution of **raw SQL queries** directly against the database.

✅ Syntax:

java

CopyEdit

`@Query(value = "SELECT * FROM employee WHERE department = :dept", nativeQuery = true)`

`List<Employee> findByDepartment(@Param("dept") String department);`

* `nativeQuery = true` tells Spring Data JPA to interpret it as raw SQL.

* Returns can be mapped to:

  * Entity

  * DTO

  * Object\[\]

---

🏷️ 2\. NamedNativeQuery & SqlResultSetMapping

✅ A. NamedNativeQuery

Defined on the entity using `@NamedNativeQuery`.

java

CopyEdit

`@Entity`

`@NamedNativeQuery(`

    `name = "Employee.findByDept",`

    `query = "SELECT * FROM employee WHERE department = :dept",`

    `resultClass = Employee.class`

`)`

`public class Employee { ... }`

Used like:

java

CopyEdit

`@Query(nativeQuery = true, name = "Employee.findByDept")`

`List<Employee> findByDept(@Param("dept") String dept);`

---

✅ B. SqlResultSetMapping

Used when mapping **Native Query result to a DTO** or custom object.

Step 1: Create DTO

java

CopyEdit

`public class EmpDeptDTO {`

    `private String empName;`

    `private String deptName;`

`}`

Step 2: Annotate entity with mapping

java

CopyEdit

`@SqlResultSetMapping(`

    `name = "EmpDeptMapping",`

    `classes = @ConstructorResult(`

        `targetClass = EmpDeptDTO.class,`

        `columns = {`

            `@ColumnResult(name = "emp_name", type = String.class),`

            `@ColumnResult(name = "dept_name", type = String.class)`

        `}`

    `)`

`)`

`@NamedNativeQuery(`

    `name = "Employee.getEmpDept",`

    `query = "SELECT e.name AS emp_name, d.name AS dept_name FROM employee e JOIN department d ON e.dept_id = d.id",`

    `resultSetMapping = "EmpDeptMapping"`

`)`

---

🔁 3\. Dynamic Native Query

Generated at runtime based on input.

✅ Using `EntityManager`:

java

CopyEdit

`@Autowired`

`EntityManager em;`

`public List<Employee> dynamicQuery(String dept) {`

    `String sql = "SELECT * FROM employee WHERE department = :dept";`

    `Query query = em.createNativeQuery(sql, Employee.class);`

    `query.setParameter("dept", dept);`

    `return query.getResultList();`

`}`

You can dynamically build SQL based on conditions (like search filters).

---

📄 4\. Pagination and Sorting in Native Query

Handled via `EntityManager`.

java

CopyEdit

`Query query = em.createNativeQuery("SELECT * FROM employee ORDER BY name", Employee.class);`

`query.setFirstResult(0); // offset`

`query.setMaxResults(10); // limit`

`List<Employee> result = query.getResultList();`

You can also create **count queries** for paged responses.

---

📚 5\. Criteria API

Used to construct **type-safe, dynamic queries** in Java code.

✅ Step-by-step:

java

CopyEdit

`@Autowired`

`EntityManager em;`

`public List<Employee> getEmployeesByCriteria(String department) {`

    `CriteriaBuilder cb = em.getCriteriaBuilder();`

    `CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);`

    `Root<Employee> root = cq.from(Employee.class);`

    `Predicate deptPredicate = cb.equal(root.get("department"), department);`

    `cq.where(deptPredicate);`

    `TypedQuery<Employee> query = em.createQuery(cq);`

    `return query.getResultList();`

`}`

Benefits:

* Type-safe

* Build complex filters dynamically

* Avoids string-based queries

---

🧠 Summary Table

| Feature | Description |
| ----- | ----- |
| Native Query | Executes raw SQL |
| NamedNativeQuery | Predefined native queries on entity |
| SqlResultSetMapping | Maps raw result to custom DTO |
| Dynamic Native Query | Generated via `EntityManager` |
| Pagination in Native Query | Use `setFirstResult`, `setMaxResults` |
| Criteria API | Type-safe, dynamic query building in Java |

---

## Specification API, Problem with Criteria API and its solution {#specification-api,-problem-with-criteria-api-and-its-solution}

---

🔥 Problems with Criteria API

1. **Code Duplication**:

   * Same predicates (conditions) may appear across multiple services/methods.

   * Example: A phone number filter might be reused in 10+ places, leading to repetition.

2. **Boilerplate Code**:

   * Writing Criteria API involves a lot of setup:

     * Creating `CriteriaBuilder`

     * Creating `CriteriaQuery`

     * Root definitions

     * Adding `where` clauses

     * TypedQuery creation

     * Fetching result

   * All of this is verbose and not part of business logic.

---

✅ Solution: Specification API

The `Specification` interface provides a clean abstraction to encapsulate **predicates**.

🎯 Benefits:

* Solves **code duplicity** — reusable predicates as methods.

* Solves **boilerplate code** — when used with `JpaSpecificationExecutor`, most of the setup is done by Spring Data JPA.

---

📦 Functional Interface

* `Specification<T>` is a functional interface.

Abstract method:

 java  
CopyEdit  
`Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb);`

* 

---

🛠️ Steps to Use Specification API

Step 1: Extend `JpaSpecificationExecutor` in your Repository

java

CopyEdit

`public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {}`

Step 2: Create Specification Class with Static Methods

java

CopyEdit

`public class UserSpecification {`

    `public static Specification<User> hasPhone(String phone) {`

        `return (root, query, cb) -> cb.equal(root.get("phone"), phone);`

    `}`

    `public static Specification<User> hasName(String name) {`

        `return (root, query, cb) -> cb.equal(root.get("name"), name);`

    `}`

    `public static Specification<User> joinAddress() {`

        `return (root, query, cb) -> {`

            `root.join("address", JoinType.INNER);`

            `return null; // No predicate, just a join`

        `};`

    `}`

`}`

> You can also use `cb.conjunction()` to return `1=1` (always true) instead of `null`.

---

Step 3: Use the Specification in the Service Layer

java

CopyEdit

`Specification<User> spec = Specification`

    `.where(UserSpecification.joinAddress())`

    `.and(UserSpecification.hasPhone("1234567890"))`

    `.and(UserSpecification.hasName("Alex"));`

`List<User> users = userRepository.findAll(spec);`

---

🔁 Under the Hood

When you pass the specification to `findAll`, Spring Data:

* Internally builds `CriteriaBuilder`, `CriteriaQuery`, `Root`.

* Recursively resolves all `AND` / `OR` predicates.

* Executes the final query using JPA.

---

🧠 Summary

| Criteria API | Specification API |
| ----- | ----- |
| Verbose and boilerplate | Minimal, expressive code |
| Predicate logic inline | Reusable predicate methods |
| Manual setup of query | Framework handles boilerplate |
| Not DRY | Promotes reusability and clean code |

---

[image1]: <data:image/png;base64,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>

[image2]: <data:image/png;base64,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>