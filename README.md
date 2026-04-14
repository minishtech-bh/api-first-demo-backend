# api-first-demo-backend

Todo API의 Spring Boot 3 백엔드입니다.  
API 스펙은 [api-first-demo-spec](https://github.com/minishtech-bh/api-first-demo-spec) 레포에서 관리됩니다.

---

## 동작 원리

이 레포는 `openapi.yaml`을 직접 수정하지 않습니다.  
대신 스펙에서 인터페이스를 **자동생성**하고, 그 인터페이스를 구현합니다.

```
open-api/openapi.yaml (submodule)
        │
        └── ./gradlew build
                └── openapi-generator-gradle-plugin
                        ├── TodoApi.java         ← @RequestMapping 인터페이스 (자동생성, 수정 금지)
                        ├── Todo.java            ← 모델 클래스 (자동생성, 수정 금지)
                        ├── CreateTodoRequest.java
                        └── UpdateTodoRequest.java

TodoController.java ← 직접 작성. TodoApi를 implements.
```

스펙이 바뀌면 `TodoApi.java`가 재생성되고, `TodoController`에서 컴파일 에러로 즉시 감지됩니다.

---

## 로컬 환경 세팅

> **Prerequisites:** Java 17+

```bash
git clone --recurse-submodules https://github.com/minishtech-bh/api-first-demo-backend.git
cd api-first-demo-backend
./gradlew build -x test
```

`--recurse-submodules` 없이 클론하면 `open-api/` 디렉토리가 비어있어 빌드가 실패합니다.  
이미 클론했다면:

```bash
git submodule update --init
```

---

## 주요 명령어

```bash
# 인터페이스만 재생성 (yaml 변경 확인 시)
./gradlew openApiGenerate

# 빌드 (openApiGenerate 자동 포함)
./gradlew build -x test

# 서버 실행
./gradlew bootRun
# → http://localhost:8080
```

---

## 작업 흐름

### 일반 기능 개발

```
1. 스펙 변경이 없는 작업이면 바로 구현
2. 서버 실행 후 curl 또는 Postman으로 확인
3. PR 오픈
```

### 스펙이 변경되는 작업

```
1. api-first-demo-spec 레포에서 feature 브랜치 생성
   → openapi.yaml 수정 후 PR 오픈 (CI 통과 확인)

2. 이 레포에서 해당 브랜치로 submodule 전환
   cd open-api
   git fetch origin
   git checkout feature/브랜치명
   cd ..

3. ./gradlew openApiGenerate → 새 인터페이스 확인
4. TodoController.java에서 컴파일 에러 수정 후 구현
5. PR 오픈 (스펙 PR 링크 명시)

6. 스펙 PR merge 후 submodule main으로 복귀
   cd open-api && git checkout master && git pull
   cd .. && git add open-api
   git commit -m "chore: update api spec"
```

### submodule 업데이트 방법

```bash
# 최신 스펙으로 업데이트
cd open-api
git pull origin master
cd ..
git add open-api
git commit -m "chore: update api spec to latest"
```

---

## curl 테스트

```bash
# Create
curl -s -X POST http://localhost:8080/todos \
  -H 'Content-Type: application/json' \
  -d '{"title":"Buy milk"}' | jq .

# List
curl -s http://localhost:8080/todos | jq .

# Update
curl -s -X PUT http://localhost:8080/todos/1 \
  -H 'Content-Type: application/json' \
  -d '{"title":"Buy milk","completed":true}' | jq .

# Delete
curl -s -o /dev/null -w "%{http_code}" -X DELETE http://localhost:8080/todos/1
```

---

## 디렉토리 구조

```
.
├── open-api/                          ← submodule (api-first-demo-spec)
├── build.gradle                       ← openapi-generator 플러그인 설정
├── src/main/java/com/example/todo/
│   ├── TodoApplication.java
│   ├── config/
│   │   └── CorsConfig.java
│   └── controller/
│       └── TodoController.java        ← 여기만 직접 작성
└── build/generated/                   ← 자동생성 (git 제외)
    └── src/main/java/com/example/todo/
        ├── api/TodoApi.java
        └── model/
```
