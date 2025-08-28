# Spring Boot 交易 (Transaction) 機制講解

大家好！今天我們將深入探討 Spring Boot 中一個非常核心且重要的功能——**交易管理 (Transaction Management)**。

## 什麼是交易 (Transaction)？

在資料庫操作中，一個「交易」是指一組必須**全部成功**或**全部失敗**的操作單元。這組操作要麼全部被提交 (Commit) 到資料庫，要麼在出現問題時全部被復原 (Rollback) 到操作前的狀態。

這確保了資料的**一致性 (Consistency)** 和**完整性 (Integrity)**。交易具有四個基本特性，通常被稱為 **ACID**：

-   **原子性 (Atomicity)**：一個交易是不可分割的最小工作單元，要麼全部執行，要麼全部不執行。
-   **一致性 (Consistency)**：交易必須使資料庫從一個一致的狀態轉變到另一個一致的狀態。
-   **隔離性 (Isolation)**：一個交易的執行不能被其他交易干擾。
-   **持久性 (Durability)**：一個交易一旦被提交，它對資料庫中資料的改變就是永久性的。

## Spring 的宣告式交易管理

在 Spring 中，我們不需要手動編寫 `try-catch-finally` 區塊來提交或回滾交易。Spring 提供了強大的**宣告式交易管理**，我們只需要一個簡單的註解 `@Transactional` 就可以搞定！

`@Transactional` 註解可以被應用在類別或方法上。當它被應用在類別上時，該類別中所有的 `public` 方法都將擁有預設的交易設定。當它被應用在方法上時，它會覆蓋類別級別的設定。

---

## 範例：產品服務的交易

讓我們透過一個 `ProductService` 的例子來看看 `@Transactional` 如何運作。

### 1. 成功的交易

這個方法用來儲存兩個產品。整個方法被 `@Transactional` 包裹，代表這兩個儲存操作在同一個交易中。

```java
// 檔案路徑: src/main/java/com/example/springbuilderexample/transaction/ProductService.java

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public void createProductsSuccessfully() {
        productRepository.save(new Product("Book"));
        productRepository.save(new Product("Pen"));
    }
    // ...
}
```

**如何驗證？**
1.  啟動應用程式。
2.  發送一個 `POST` 請求到 `/products/success`。
3.  再發送一個 `GET` 請求到 `/products`。
4.  你會看到資料庫中有 "Book" 和 "Pen" 兩筆資料，證明交易成功提交。

### 2. 失敗並回滾的交易

這個方法模擬了一個在交易過程中發生錯誤的場景。它先儲存了一個產品 "Laptop"，然後手動拋出一個 `RuntimeException`。

```java
// 檔案路徑: src/main/java/com/example/springbuilderexample/transaction/ProductService.java

@Service
public class ProductService {

    // ...

    @Transactional
    public void createProductsWithRollback() {
        productRepository.save(new Product("Laptop"));
        if (true) {
            throw new RuntimeException("Simulating an error during transaction!");
        }
        productRepository.save(new Product("Mouse")); // 這行程式碼永遠不會被執行
    }
    // ...
}
```

**關鍵點：**
-   Spring 的交易管理**預設只會對 `RuntimeException` 和 `Error` 進行回滾**。對於受檢例外 (Checked Exception)，它預設是不會回滾的。
-   因為我們拋出了 `RuntimeException`，Spring 會捕捉到它並觸發交易回滾。

**如何驗證？**
1.  啟動應用程式。
2.  （可選）先發送 `GET` 到 `/products` 確認資料庫是空的。
3.  發送一個 `POST` 請求到 `/products/rollback`。你會收到一個錯誤回應。
4.  再次發送 `GET` 請求到 `/products`。
5.  你會發現資料庫**仍然是空的**！即使 `save(new Product("Laptop"))` 已經被執行，但因為整個交易被回滾了，所以 "Laptop" 並沒有被真正存入資料庫。這就是交易的原子性！

---

## 如何觸發範例？

為了方便展示，我們建立了一個 `ProductController` 來提供 API 端點。

```java
// 檔案路徑: src/main/java/com/example/springbuilderexample/transaction/ProductController.java
@RestController
@RequestMapping("/products")
public class ProductController {
    // ...
    @PostMapping("/success")
    public ResponseEntity<String> createProductsSuccessfully() { ... }

    @PostMapping("/rollback")
    public ResponseEntity<String> createProductsWithRollback() { ... }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() { ... }
}
```

你可以使用 `curl` 或 Postman 等工具來測試：

-   **成功交易**: `curl -X POST http://localhost:8080/products/success`
-   **失敗交易**: `curl -X POST http://localhost:8080/products/rollback`
-   **查看結果**: `curl http://localhost:8080/products`

同時，你也可以登入 H2 資料庫的主控台來直接查看資料。應用程式啟動後，在瀏覽器中開啟 `http://localhost:8080/h2-console`，並使用以下設定連接：
-   **JDBC URL**: `jdbc:h2:mem:testdb`
-   **User Name**: `sa`
-   **Password**: (留空)

## 總結

Spring 的 `@Transactional` 註解為我們提供了一個非常強大且易於使用的宣告式交易管理機制。它大大簡化了我們的程式碼，讓我們可以專注於業務邏輯，而不必處理繁瑣的交易控制程式碼。

透過今天的範例，我們看到了它如何確保操作的原子性，在成功時提交，在失敗時回滾，從而保護我們資料的完整與一致。

謝謝大家。
