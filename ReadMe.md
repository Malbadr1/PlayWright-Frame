# 🛍️ Playwright + JUnit5 E2E Testing Framework

A modern, modular end-to-end (E2E) UI testing framework for testing the shopping flow on [saucedemo.com](https://www.saucedemo.com/), built using **Java**, **Playwright**, **JUnit 5**, and **Allure**.



## ✅ Features

- ✅ Modular step definitions via `ShoppingRightwayE2ESteps`
- ✅ Automatic video + screenshot capture for each test
- ✅ Folder renaming with test status (e.g. `_PASS`, `_FAIL`)
- ✅ Allure test reporting integration
- ✅ Clean and maintainable code

---

## 🚀 How to Run

### 🧰 Prerequisites
- Java 17+ or 21
- Maven
- Node.js (Playwright dependencies)




### 🧪 Run All Tests
```bash
mvn clean test

mvn test -Dtest=testFeatures.LoginTest
mvn test -Dtest=testServiceE2E.ShoppingRightwayE2ETest

mvn -Dtest="testFeatures.*Test" test


```

### 📊 View Allure Report
```bash
allure serve target/allure-results
```

---

## 🎥 Artifacts
- Videos: `videos/`
- Screenshots: `target/screenshots/`
- Allure results: `target/allure-results/`

---

## ✍️ Author
**Mohanad Al-Badri** — Crafted for clean, visual UI automation with modular steps and formatted results.

---

## 📌 Notes
- record videos/screenshots when tests fail or pass.
- Each test prints clear emoji-based logs to console.
- Easily extend steps in `ShoppingRightwayE2ESteps.java`.
