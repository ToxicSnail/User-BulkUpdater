# User-BulkUpdater


> *A Spring Boot micro‑service that keeps your user directory in sync by processing uploads.*

---

## Table of Contents

1. [Problem statement](#problem-statement)
2. [Key features](#key-features)
3. [Upload file requirements](#upload-file-requirements)
4. [Typical work‑flow](#typical-work-flow)
5. [REST API](#rest-api)
6. [Running locally](#running-locally)
7. [Project structure](#project-structure)
8. [Contributing](#contributing)

---

## Problem statement

The service receives a **file** with personal data, validates every row and then **adds or updates** users stored in PostgreSQL. All statistics about the processing session are persisted and exposed through the API so that a client can monitor progress and inspect errors.

The functional and non‑functional requirements are taken from the original [assignment](README_RU.md)

## Key features

* Upload a user list via **`/files`**.
* Trigger asynchronous processing via **`/files/{fileId}/processing`**.
* Inspect a single file (status & aggregated counters) or the whole history via **`/files/{id}`** and **`/files/statistics`**.
* Drill down into validation errors row‑by‑row via **`/files/{fileId}/statistics`**.
* Browse the current user catalogue with flexible filtering via **`/clients`**.
* Four processing statuses are tracked:

  * `NEW` – file is saved but not yet processed.
  * `IN_PROGRESS` – background job is running.
  * `DONE` – finished successfully, even if some rows failed.
  * `FAILED` – fatal error, job aborted.

## Upload file requirements

The file **must** be UTF‑8 without quotes and contain six columns in the order listed below. Full validation rules are summarised here – see the detailed document for edge cases.

| # | Field       | Required | Rules (short)                                                   |
| - | ----------- | -------- | --------------------------------------------------------------- |
| 1 | First name  | ✔        | Cyrillic, first letter upper‑case, 3‑50 chars                   |
| 2 | Last name   | ✔        | Same as first name                                              |
| 3 | Middle name | –        | Same charset rules, optional                                    |
| 4 | E‑mail      | ✔        | Standard pattern, domain `shift.com` or `shift.ru`, ≤ 100 chars |
| 5 | Phone       | ✔        | Digits, starts with `7`, unique, length 11                      |
| 6 | Birth date  | ✔        | `yyyy‑MM‑dd`, age ≥ 18                                          |

> **Example**
>
> ```csv
> Иванов,Иван,Иванович,ivan.ivanov@shift.com,79161234567,1990-05-15
> ```

## Typical work‑flow

1. **Upload** a file – receive `fileId`.
2. **Start processing** → `IN_PROGRESS`.
3. Poll **statistics** until the status becomes `DONE` or `FAILED`.
4. If `errorProcessedLinesCount > 0`, fetch the **detailed statistics**, fix bad rows and repeat.
5. Verify the result through **`/clients`**.

Sequence and component diagrams are located in `images/` (see `save_file.png`, `process_file.png`).

## REST API

The full OpenAPI description lives in [`api/openapi.yaml`](documentation/api/openapi.yaml). A shortened cheat‑sheet:

| Method | Path                         | Purpose                      |
| ------ | ---------------------------- | ---------------------------- |
| POST   | `/files`                     | Upload a CSV file            |
| POST   | `/files/{fileId}/processing` | Launch async processing      |
| GET    | `/files/{fileId}`            | File meta & counters         |
| GET    | `/files/statistics`          | List of all files (filtered) |
| GET    | `/files/{fileId}/statistics` | Row‑level error list         |
| GET    | `/clients`                   | Search current users         |

> **Versioning** – the base path can be prefixed with `/api/v1` if you enable it in `application.yml`; the controller mappings are isolated so both styles can live side‑by‑side.

## Running locally

### Prerequisites

* JDK 17+
* Docker & Docker Compose
* `make` or Gradle wrapper (`./gradlew`)

### Quick start

```bash
#run containers
docker compose up

# build the artefact without tests
./gradlew clean build -x test

# start the stack (app + PostgreSQL)
./gradlew bootRun      # or:  docker compose up --build

# open swagger‑ui
http://localhost:8080/swagger-ui.html
```

The default DB connection parameters are configured in `application.yml`. You can override them via environment variables.

## Project structure

```
backend/
 ├─ src/main/java/ru/shift/userimporter
 │   ├─ api        # REST controllers, DTOs and mappers
 │   ├─ config     # some configs
 │   ├─ core       # business logic & domain models
 │   └─ infrastructure
 ├─ api/openapi.yaml
 └─ docker-compose.yml
```

---

© 2025 Gorky Kirill – MIT licence
