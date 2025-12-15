# springboot rest api validation i18n

### 1️⃣ Controller (AccountAPI)
```java

@GetMapping("/saveAndFindAll.do")
public Object saveAndFindAll(@Valid Account account){

    
```
- اینکه GET نباید دیتا ذخیره کند
- این کار خلاف REST principles است

✅ اصلاح استاندارد

```java

@PostMapping
public List<Account> saveAndFindAll(@Valid @RequestBody Account account){

    
```


@Valid با @RequestBody
وگرنه الان Spring انتظار دارد پارامترها از query string بیایند، نه body


✅ درستش:

```java

public List<Account> saveAndFindAll(@Valid @RequestBody Account account)

```


برای API حرفه‌ای، نوع بازگشتی Object اصلاً خوب نیست.

✅ بهتر:

```java

public List<Account>


```

یا 

```java

public ResponseEntity<List<Account>>


```

### Validation عالی 👍

✔ @Email
✔ @NotBlank
✔ @Size

فقط پیشنهاد حرفه‌ای:

```java

@Transactional
public void save(Account account){
    entityManager.persist(account);
}

```

### ✅ این بخش عالیه
- استفاده مستقیم از EntityManager
- کاملاً مناسب سؤال مصاحبه:
  "JPA vs Spring Data JPA"


### 4️⃣ ErrorHandler (Exception Handling)

❌ مشکل بزرگ

```java

response.setStatus(errorCode);


```

- Status Code باید HTTP استاندارد باشد (400, 404, 500)
- عدد 700 یا 900 از نظر HTTP غلط است

### ✅ نسخه حرفه‌ای (مناسب مصاحبه)

```java

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex){
        return ResponseEntity
                .badRequest()
                .body("Validation failed");
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<String> handleSql(SQLException ex){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Database error");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex){
        log.error("Unexpected error", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected error");
    }
}


```

### 7️⃣ جمع‌بندی مصاحبه‌ای (خیلی مهم)

اگر مصاحبه‌گر بپرسه:
چرا Repository ننوشتی؟
جواب طلایی:

«برای درک عمیق JPA و Transaction Management، در این پروژه از EntityManager استفاده کردم.


در پروژه‌های واقعی معمولاً از Spring Data JPA Repository استفاده می‌کنم.»

### گر بخواهیم errorCode ها یا Status Code های سفارشی برای فرانت اند بفرستیم

خلاصه‌ی کوتاه جواب:
✅ بله، همیشه HTTP Status Code باید استاندارد باشد

✅ کدهای سفارشی را نباید در Status Code بفرستیم

✅ کدهای سفارشی را داخل Body پاسخ می‌فرستیم

### 1️⃣ چرا Status Code باید استاندارد باشد؟
HTTP Status Code ها برای زیرساخت هستند:
- Browser
- Load Balancer
- API Gateway
- CDN
- Monitoring (Prometheus, Grafana)
- Client libraries (Axios, Fetch)

مثلاً:
- 400 → درخواست غلط
- 401 → احراز هویت نشده
- 403 → دسترسی ممنوع
- 404 → پیدا نشد
- 500 → خطای سرور


❌ اگر بفرستی:

```java

Status: 703

```

- خیلی از کلاینت‌ها نمی‌فهمند این یعنی چی
- بعضی‌ها حتی exception می‌زنند
- ابزارهای مانیتورینگ خراب می‌شوند

👉 پس Status Code = زبان مشترک HTTP

### 2️⃣ پس Frontend کد سفارشی را کجا بگیرد؟

✅ داخل Response Body

الگوی حرفه‌ای (Industry Standard)

```java

{
  "timestamp": "2025-12-13T10:30:00",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "errorCode": 703,
  "message": "Account owner address must not be blank",
  "path": "/accounts"
}

```

🔹 status → استاندارد HTTP

🔹 errorCode → کد داخلی مخصوص فرانت‌اند

🔹 error → enum یا string قابل فهم
🔹 message → متن نمایشی

### 3️⃣ پیاده‌سازی درست در Spring Boot

DTO مخصوص خطا

```java
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int status;       // HTTP Status
    private int errorCode;    // Custom App Code
    private String message;
}

```

ErrorHandler حرفه‌ای

```java

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validationError() {

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(
                        400,
                        703,
                        "Validation failed"
                ));
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> sqlError() {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        500,
                        700,
                        "Database error"
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> generic(Exception ex) {

        log.error("Unexpected error", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        500,
                        900,
                        "Unexpected error"
                ));
    }
}

```


### 5️⃣ دید مصاحبه‌ای (خیلی مهم 🔥)

اگر ازت پرسیدند:
چرا errorCode را داخل body گذاشتی؟

جواب طلایی:

«HTTP Status Codes برای semantics پروتکل هستند و باید استاندارد باقی بمانند.
Business Error Codes را داخل Response Body می‌فرستم تا Frontend بتواند تصمیم بگیرد بدون شکستن قرارداد HTTP.»

🔹 این جواب Senior-level است.

###  6️⃣ یک نکته حرفه‌ای اضافه ✨

در پروژه‌های بزرگ:
- errorCode معمولاً Enum است
- مستند می‌شود در Swagger
- Frontend دقیقاً می‌داند هر کد یعنی چه

مثلاً:

```java
public enum ErrorCode {
    VALIDATION_ERROR(703),
    DATABASE_ERROR(700),
    UNKNOWN_ERROR(900);
}

```

### نمونه خروجی واقعی JSON

اگر این درخواست بیاید:

```java
{
  "accountOwnerName": "A",
  "accountOwnerMail": "not-mail",
  "accountOwnerAddress": ""
}

```

پاسخ API:

```java

{
  "timestamp": "2025-12-13T10:45:12",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "errorCode": 703,
  "message": "Validation failed",
  "path": "/accounts",
  "validationErrors": [
    {
      "field": "accountOwnerName",
      "message": "size must be between 2 and 10"
    },
    {
      "field": "accountOwnerMail",
      "message": "must be a well-formed email address"
    },
    {
      "field": "accountOwnerAddress",
      "message": "must not be blank"
    }
  ]
}

```


👈 فرانت‌اند عاشق این خروجی می‌شود 😄
می‌تواند:

- کنار هر input پیام نشان دهد

- بر اساس errorCode تصمیم بگیرد

- بدون parsing عجیب

### 5️⃣ نکات خیلی حرفه‌ای 🔥

✅ چرا List از ValidationError؟

- امکان چند خطا هم‌زمان
- UX بهتر


✅ چرا timestamp؟

- Logging
- Debug
- Trace در distributed systems

✅ چرا path؟

- کمک به frontend
- کمک به backend debugging


### 6️⃣ جواب مصاحبه‌ای آماده 🎤

اگر بپرسند:

چرا ValidationError جدا ساختی؟

جواب:

«برای اینکه قرارداد پاسخ خطا واضح، قابل توسعه و مستقل از Spring باشد.
Frontend فقط با DTO کار می‌کند، نه با Exceptionهای Spring.»


### جواب طلایی مصاحبه 🎤
اگر بپرسند:

چرا یک متد baseResponse نوشتی؟

جواب:

«برای جلوگیری از تکرار، یکپارچگی قرارداد خطا و اینکه هر نوع خطا دقیقاً یک ساختار ثابت داشته باشد.»


### 5️⃣ تفاوت مهم در یک جدول (سؤال مصاحبه 🔥)

| سناریو                       | Exception                       |
| ---------------------------- | ------------------------------- |
| @Valid روی @RequestBody      | MethodArgumentNotValidException |
| @Valid روی @ModelAttribute   | MethodArgumentNotValidException |
| @Validated روی @PathVariable | ConstraintViolationException    |
| JSON خراب                    | HttpMessageNotReadableException |



### 7️⃣ جواب طلایی مصاحبه 🎤

اگر بپرسند:

MethodArgumentNotValidException کی رخ می‌دهد؟

جواب:

«وقتی Bean Validation روی ورودی متد Controller که با @Valid مشخص شده اجرا شود و حداقل یکی از constraintها نقض شود.»


### 2️⃣ جدول طلایی (خیلی مهم 🔥)

| ورودی Controller                             | Exception                       |
| -------------------------------------------- | ------------------------------- |
| `@Valid @RequestBody`                        | MethodArgumentNotValidException |
| `@Valid @ModelAttribute` (GET / query param) | **BindException**               |
| `@PathVariable` / `@RequestParam`            | ConstraintViolationException    |


### 🔴 مشکل اصلی (خیلی خیلی مهم)

تو می‌گویی:

من این لینک را در Browser می‌زنم
و انتظار دارم MethodArgumentNotValidException بیاید

اما API تو این است:

```java

@PostMapping("/saveAndFindAll.do")
public List<Account> createAccount(
        @Valid @RequestBody Account account)

```

و لینکی که می‌زنی:

http://localhost:8081/account/saveAndFindAll.do?accountBalance=10000&...


❌ این درخواست POST نیست
❌ Body هم ندارد
❌ JSON هم ندارد

👉 Browser وقتی URL را مستقیم می‌زنی:

- همیشه GET می‌فرستد

- Body نمی‌فرستد

- @RequestBody اجرا نمی‌شود