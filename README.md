# 윷놀이 게임 (Yootnori Game)

한국의 전통 보드게임인 윷놀이를 Java로 구현한 프로젝트입니다. Swing과 JavaFX 두 가지 UI 프레임워크를 지원하며, 사각형, 오각형, 육각형 보드에서 게임을 즐길 수 있습니다.

## 게임 특징

### 지원하는 보드 형태
- **사각형 보드**: 전통적인 윷놀이판 (20칸 + 특별 경로)
- **오각형 보드**: 5각형 구조의 확장된 윷놀이판 (25칸 + 특별 경로)
- **육각형 보드**: 6각형 구조의 대형 윷놀이판 (30칸 + 특별 경로)

### 게임 규칙
- **플레이어**: 2-4명
- **말의 개수**: 플레이어당 2-5개
- **윷 결과**: 도(1칸), 개(2칸), 걸(3칸), 윷(4칸), 모(5칸), 빽도(-1칸)
- **특별 규칙**: 말 업기, 말 잡기, 추가 턴, 중앙점 경로

### UI 옵션
- **Swing UI**: 전통적인 데스크톱 인터페이스
- **JavaFX UI**: 현대적인 그래픽 인터페이스
- **자동 UI 선택**: 시작 시 원하는 UI 선택 가능

## 시작하기

### 필요 조건
- Java 23 이상
- Maven 3.6 이상

### 설치 및 실행

1. **저장소 클론**
```bash
git clone https://github.com/yourusername/yootnori.git
cd yootnori
```

2. **프로젝트 빌드**
```bash
mvn clean compile
```

3. **게임 실행**
```bash
# UI 선택 다이얼로그로 실행
mvn exec:java -Dexec.mainClass="org.example.Main"

# 또는 직접 Swing UI로 실행
mvn exec:java -Dexec.mainClass="org.example.Main" -Dexec.args="swing"

# JavaFX UI로 실행 (JavaFX 런타임 필요)
mvn javafx:run
```

4. **JAR 파일 생성 및 실행**
```bash
# 빌드
mvn clean package

# Swing 버전 실행
java -jar target/yootnori-swing.jar

# JavaFX 버전 실행 (JavaFX 모듈 패스 설정 필요)
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar target/yootnori-javafx.jar
```

## 🏗️ 프로젝트 구조

```
src/main/java/org/example/
├── Main.java                     # 메인 진입점
├── YutGameApp.java              # Swing 앱 시작점
├── YutGameFXApp.java            # JavaFX 앱 시작점
├── controller/                   # 게임 컨트롤러
│   ├── AbstractGameController.java
│   ├── GameController.java
│   ├── swing/SwingGameController.java
│   └── javafx/FXGameController.java
├── model/                       # 게임 모델 (비즈니스 로직)
│   ├── Game.java               # 게임 상태 관리
│   ├── Board.java              # 보드 구조
│   ├── Player.java             # 플레이어
│   ├── Piece.java              # 말
│   ├── Place.java              # 보드 위치
│   ├── Yut.java                # 윷 던지기
│   ├── GameSettings.java       # 게임 설정
│   └── GameLog.java            # 게임 로그
└── view/                       # UI 계층
    ├── swing/                  # Swing UI 컴포넌트
    │   ├── GameSetupFrame.java
    │   ├── GameFrame.java
    │   ├── GameBoardPanel.java
    │   └── YutInfoPanel.java
    └── javafx/                 # JavaFX UI 컴포넌트
        ├── GameSetupScene.java
        ├── GameScene.java
        ├── GameBoardPane.java
        └── YutInfoPane.java
```

## 🎯 게임 플레이 방법

### 게임 설정
1. 게임 시작 시 보드 형태 선택 (사각형/오각형/육각형)
2. 플레이어 수 설정 (2-4명)
3. 플레이어당 말 개수 설정 (2-5개)

### 게임 진행
1. **윷 던지기**: 랜덤 또는 특정 결과 선택
2. **말 이동**: 이동 가능한 말 선택하여 이동
3. **특별 상황 처리**:
    - **말 업기**: 같은 위치의 같은 플레이어 말 자동 업기
    - **말 잡기**: 상대방 말 잡으면 추가 턴 획득
    - **윷/모**: 추가 턴 획득
    - **중앙점**: 특별 경로 이용 가능

### 승리 조건
- 플레이어의 모든 말이 최종 도착점(FE)에 도달하면 승리

## 테스트

```bash
# 모든 테스트 실행
mvn test

# 특정 테스트 클래스 실행
mvn test -Dtest=GameTest
mvn test -Dtest=BackdoMovementTest
```

### 주요 테스트 케이스
- `GameTest`: 기본 게임 로직 테스트
- `BackdoMovementTest`: 빽도 이동 규칙 테스트
- `CompletionRuleTest`: 완주 규칙 테스트
- `MovablePiecesTest`: 이동 가능한 말 판별 테스트
- `GroupedPieceCompletionTest`: 업힌 말 완주 처리 테스트
- 보드별 경로 테스트: `SquareBoardPathTest`, `PentagonBoardPathTest`, `HexagonBoardPathTest`

## 빌드 설정

### Maven 프로파일
```bash
# Swing 전용 빌드
mvn clean package -P swing

# JavaFX 전용 빌드  
mvn clean package -P javafx
```

### 의존성
- **JUnit 5**: 테스트 프레임워크
- **JavaFX 21**: 현대적인 UI (선택사항)
- **Java 23**: 최신 Java 기능 활용

## UI 스크린샷

### 게임 설정 화면
사용자가 보드 형태, 플레이어 수, 말 개수를 선택할 수 있는 초기 설정 화면

### 게임 플레이 화면
- 보드 표시 영역
- 컨트롤 패널 (윷 던지기, 말 선택)
- 게임 정보 패널 (현재 턴, 윷 결과)
- 게임 로그 영역

## 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.