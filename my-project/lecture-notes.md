# Builder Pattern 講解稿

大家好！今天我們要來探討在軟體設計中一個非常實用且常見的設計模式——**Builder Pattern (建構者模式)**。

## 為什麼需要 Builder Pattern？

在物件導向程式設計中，我們經常需要建立一個物件。如果這個物件很簡單，只有幾個屬性，那麼使用一個建構子 (Constructor) 就可以輕鬆搞定。

但是，如果物件的屬性非常多，情況就會變得很複雜。你可能會遇到以下問題：

1.  **建構子參數過多 (Telescoping Constructor)**：如果物件有多個可選屬性，你可能需要提供多個不同參數組合的建構子。這會讓程式碼變得臃腫且難以維護。
2.  **可讀性差**：當建構子的參數列表很長時，例如 `new User("John", "Doe", 30, "123-456-7890", null, null, "123 Main St")`，你很難一眼看出每個參數代表什麼意思，很容易搞錯順序。
3.  **無法建立不可變物件 (Immutable Object)**：如果使用傳統的 JavaBean 模式（提供一個無參建構子和一堆 setter 方法），物件在被完整建立之前，可能會處於一個不一致的狀態。而且，一旦建立後，它的狀態還可以被輕易改變，這在多執行緒環境下可能會引發問題。

為了解決這些問題，Builder Pattern 應運而生。

---

## 範例一：簡單的 User Builder

讓我們來看一個經典的例子：建立一個 `User` 物件。一個 `User` 可能有姓名、年齡、電話、地址等多個屬性，其中姓名是必填的，其他則是可選的。

這是我們的 `User` 類別，它使用了一個靜態內部類別 `UserBuilder` 來建立物件。

```java
// 檔案路徑: src/main/java/com/example/springbuilderexample/builder/simple/User.java

package com.example.springbuilderexample.builder.simple;

public class User {
    private final String firstName; // 必填
    private final String lastName;  // 必填
    private final int age;          // 可選
    private final String phone;     // 可選
    private final String address;   // 可選

    private User(UserBuilder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.age = builder.age;
        this.phone = builder.phone;
        this.address = builder.address;
    }

    // ... Getters ...

    public static class UserBuilder {
        private final String firstName;
        private final String lastName;
        private int age;
        private String phone;
        private String address;

        public UserBuilder(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public UserBuilder age(int age) {
            this.age = age;
            return this;
        }

        public UserBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserBuilder address(String address) {
            this.address = address;
            return this;
        }

        public User build() {
            User user = new User(this);
            if (user.getAge() > 120 || user.getAge() < 0) {
                throw new IllegalStateException("Age is not valid!");
            }
            return user;
        }
    }
}
```

### 如何使用？

使用起來非常直觀，像是在寫一個流暢的句子：

```java
User user = new User.UserBuilder("Jules", "Verne")
    .age(30)
    .phone("123-456-7890")
    .address("123 Main St")
    .build();
```

### 優點分析

1.  **程式碼可讀性高**：`age(30)`、`phone("...")` 這樣的寫法，讓每個值的用途一目了然。
2.  **彈性設定參數**：可以自由選擇要設定哪些可選參數，順序也不重要。
3.  **建立不可變物件**：`User` 物件的屬性都是 `final` 的，而且沒有 `setter`，一旦透過 `build()` 建立後，就不能再被修改。
4.  **集中驗證邏輯**：可以在 `build()` 方法中對所有參數進行統一的驗證，確保建立出來的物件是有效的。

---

## 範例二：受限制的 SQL Builder (Type-Safe Builder)

接下來，我們來看一個更進階的應用。有時候，我們希望方法的呼叫有固定的順序。例如，在組合一個 SQL `SELECT` 查詢時，`SELECT` 必須在 `FROM` 之前，`FROM` 必須在 `WHERE` 之前。

如果用簡單的 Builder，可能會寫出這樣的錯誤程式碼：

```java
// 錯誤的順序
sqlBuilder.where("id = 1").select("*").from("users").build();
```

為了在**編譯時期**就阻止這種錯誤，我們可以使用 "Type-Safe Builder" 模式，透過**介面 (Interface)** 來限制方法的呼叫順序。

這是我們的 `SQL` Builder 實作：

```java
// 檔案路徑: src/main/java/com/example/springbuilderexample/builder/sql/SQL.java

package com.example.springbuilderexample.builder.sql;

import java.util.StringJoiner;

public final class SQL {

    // --- Builder 的進入點 ---
    public static SelectStep select(String... columns) { ... }
    public static UpdateStep update(String table) { ... }
    public static DeleteFromStep deleteFrom(String table) { ... }

    // --- 步驟介面 (Step Interfaces) ---

    // SELECT
    public interface SelectStep { FromStep from(String table); }
    public interface FromStep extends BuildStep { WhereStep where(String condition); }

    // UPDATE
    public interface UpdateStep { SetStep set(String... assignments); }
    public interface SetStep extends BuildStep { WhereStep where(String condition); }

    // DELETE
    public interface DeleteFromStep extends BuildStep { WhereStep where(String condition); }

    // 通用介面
    public interface WhereStep extends BuildStep { }
    public interface BuildStep { String build(); }

    // --- Builder 的私有實作 ---
    private static class SQLBuilder implements SelectStep, FromStep, UpdateStep, SetStep, DeleteFromStep, WhereStep {
        // ... 實作細節 ...
    }
}
```

### 如何運作？

這個模式的精髓在於，每個方法的回傳類型都是下一個步驟的**介面**。

-   `select()` 回傳 `SelectStep` 介面，這個介面裡**只有** `from()` 方法。
-   `from()` 回傳 `FromStep` 介面，這個介面裡**只有** `where()` 和 `build()` 方法。
-   `update()` 回傳 `UpdateStep` 介面，這個介面裡**只有** `set()` 方法。
-   ... 以此類推。

這樣一來，你的 IDE 會自動提示你下一步能做什麼，如果你試圖呼叫不被允許的方法，程式在編譯階段就會報錯！

### 使用範例

```java
// SELECT
String selectSql = SQL.select("*", "name")
    .from("users")
    .where("age > 18")
    .build();
// -> SELECT *, name FROM users WHERE age > 18

// UPDATE
String updateSql = SQL.update("users")
    .set("name = 'New Name'", "status = 1")
    .where("id = 123")
    .build();
// -> UPDATE users SET name = 'New Name', status = 1 WHERE id = 123

// DELETE
String deleteSql = SQL.deleteFrom("users")
    .where("id = 123")
    .build();
// -> DELETE FROM users WHERE id = 123
```

如果你試圖寫出這樣的程式碼，編譯器會直接告訴你錯誤：

```java
// 編譯錯誤！因為 SelectStep 沒有 where() 方法
SQL.select("*").where("id = 1");

// 編譯錯誤！因為 SetStep 沒有 from() 方法
SQL.update("users").set("name = 'J'").from("other_table");
```

---

## 總結

Builder Pattern 是一個強大且靈活的設計模式，它能幫助我們：

-   **提高程式碼的可讀性與可維護性。**
-   **優雅地處理大量可選參數。**
-   **建立不可變的物件，提升程式的穩健性。**
-   **(進階) 透過介面實現型別安全的步驟，引導使用者正確地建立物件。**

當你需要建立的物件比較複雜時，不妨考慮使用 Builder Pattern！

謝謝大家。
