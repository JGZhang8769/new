# Spring Boot 交易 (Transaction) 機制深度講解

大家好！今天我們將深入探討 Spring Boot 中一個非常核心且重要的功能——**交易管理 (Transaction Management)**。

## 什麼是交易 (Transaction)？

在資料庫操作中，一個「交易」是指一組必須**全部成功**或**全部失敗**的操作單元。它具有四個基本特性，通常被稱為 **ACID**：

-   **原子性 (Atomicity)**：一個交易是不可分割的最小工作單元。
-   **一致性 (Consistency)**：交易必須使資料庫從一個一致的狀態轉變到另一個一致的狀態。
-   **隔離性 (Isolation)**：一個交易的執行不能被其他交易干擾。
-   **持久性 (Durability)**：一個交易一旦被提交，它對資料庫中資料的改變就是永久性的。

## Spring 的宣告式交易管理: `@Transactional`

Spring 提供了強大的**宣告式交易管理**，我們只需要一個簡單的註解 `@Transactional` 就可以搞定！

---

## 基礎範例：成功與回滾

### 1. 成功的交易
`createProductsSuccessfully()` 方法被 `@Transactional` 包裹，兩個 `save` 操作在同一個交易中，會一起成功。

```java
// 檔案路徑: src/main/java/com/example/springbuilderexample/transaction/ProductService.java
@Transactional
public void createProductsSuccessfully() {
    productRepository.save(new Product("Book"));
    productRepository.save(new Product("Pen"));
}
```
-   **測試**: `POST /products/success`，然後 `GET /products` 會看到兩筆新資料。

### 2. 因執行期例外而回滾的交易
`createProductsWithRollback()` 方法在儲存 "Laptop" 後拋出 `RuntimeException`。

```java
@Transactional
public void createProductsWithRollback() {
    productRepository.save(new Product("Laptop"));
    throw new RuntimeException("Simulating an error during transaction!");
    // productRepository.save(new Product("Mouse")); // 不會執行
}
```
-   **測試**: `POST /products/rollback`，然後 `GET /products` 會發現資料庫是空的，"Laptop" 被成功回滾。

---

## `@Transactional` 的進階設定

`@Transactional` 註解有很多可以設定的屬性，讓我們來看看幾個最重要的。

### 1. `propagation` (交易傳播行為)

這個屬性定義了當一個交易方法被另一個交易方法呼叫時，交易應該如何傳播。最常見的兩個是：

-   `REQUIRED` (預設值): 如果當前已經存在一個交易，那麼就加入該交易，否則就自己建立一個新的交易。
-   `REQUIRES_NEW`: 不論當前是否存在交易，都會為自己建立一個**全新的、獨立的**交易。如果外部存在交易，外部交易會被暫停，直到這個新交易完成。

**範例**: `outerRequiresNew()` 呼叫 `innerRequiresNew()`。
-   `outerRequiresNew`：使用預設的 `REQUIRED`。
-   `innerRequiresNew`：使用 `REQUIRES_NEW`。

```java
@Transactional
public void outerRequiresNew() {
    productRepository.save(new Product("Outer Product")); // 會被回滾

    try {
        self.innerRequiresNew(); // 呼叫自己類別的方法需要透過代理
    } catch (Exception e) {
        // 內部交易的例外不會影響外部
    }

    throw new RuntimeException("Rollback outer transaction");
}

@Transactional(propagation = Propagation.REQUIRES_NEW)
public void innerRequiresNew() {
    productRepository.save(new Product("Inner Product")); // 會成功提交
}
```
**關鍵點**: 為了讓 `propagation` 生效，`innerRequiresNew()` 必須透過 Spring 的代理物件來呼叫。這就是為什麼我們需要注入 `ProductService self`。

**如何驗證？**
1.  `DELETE /products` 清空資料庫。
2.  `POST /products/requires-new`。
3.  `GET /products`。你會發現資料庫中**只有 "Inner Product"**！因為外部交易被回滾了，但內部交易 (`REQUIRES_NEW`) 已經獨立提交了。

### 2. `rollbackFor` (指定回滾的例外)

預設情況下，Spring 只會對 `RuntimeException` 和 `Error` 進行回滾。如果你希望在發生**受檢例外 (Checked Exception)** 時也回滾交易，就需要使用 `rollbackFor`。

```java
// 自訂一個受檢例外
public class CustomCheckedException extends Exception { ... }

// 在 Service 中使用 rollbackFor
@Transactional(rollbackFor = CustomCheckedException.class)
public void createProductWithCheckedException() throws CustomCheckedException {
    productRepository.save(new Product("Checked Exception Product"));
    throw new CustomCheckedException("This should cause a rollback.");
}
```

**如何驗證？**
1.  `DELETE /products` 清空資料庫。
2.  `POST /products/checked-exception`。
3.  `GET /products`。你會發現資料庫是空的，證明即使是受檢例外，交易也成功回滾了。

### 3. `readOnly` (唯讀交易)

這是一個優化選項。當你將交易設定為 `readOnly = true`，你等於在告訴資料庫和 JPA Provider，這個交易中**不會有任何寫入操作**。

```java
@Transactional(readOnly = true)
public List<Product> findAllProducts() {
    return productRepository.findAll();
}
```
這可以帶來一些效能上的好處，例如，資料庫可以不做一些鎖定，JPA Provider 也可以避免一些不必要的髒檢查 (dirty checking)。

---

## 如何觸發進階範例？

-   **Propagation**: `POST /products/requires-new`
-   **RollbackFor**: `POST /products/checked-exception`
-   **清空資料庫**: `DELETE /products`
-   **查看結果**: `GET /products`
-   **H2 Console**: `http://localhost:8080/h2-console`

## 總結

透過靈活運用 `@Transactional` 的各項屬性，我們可以非常精準地控制應用程式的交易行為，從而建構出更加穩健、高效的系統。

謝謝大家。
