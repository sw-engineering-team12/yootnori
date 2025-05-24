package org.example.view.javafx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.controller.javafx.FXGameController;
import org.example.model.Board;
import org.example.model.GameSettings;

/**
 * JavaFX 게임 설정 화면
 */
public class GameSetupScene {
    private Scene scene;
    private Stage primaryStage;

    private ComboBox<String> boardTypeCombo;
    private ComboBox<Integer> playerCountCombo;
    private ComboBox<Integer> pieceCountCombo;
    private Button startButton;

    public GameSetupScene(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        // 보드 타입 선택
        boardTypeCombo = new ComboBox<>();
        boardTypeCombo.getItems().addAll("사각형", "오각형", "육각형");
        boardTypeCombo.setValue("사각형");

        // 플레이어 수 선택 (2-4명)
        playerCountCombo = new ComboBox<>();
        playerCountCombo.getItems().addAll(2, 3, 4);
        playerCountCombo.setValue(2);

        // 말 개수 선택 (2-5개)
        pieceCountCombo = new ComboBox<>();
        pieceCountCombo.getItems().addAll(2, 3, 4, 5);
        pieceCountCombo.setValue(4);

        // 시작 버튼
        startButton = new Button("게임 시작");
        startButton.setOnAction(e -> startGame());
        startButton.setPrefWidth(120);
    }

    private void setupLayout() {
        // 메인 컨테이너
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(30));

        // 제목
        Label titleLabel = new Label("윷놀이 게임 설정");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // 설정 그리드
        GridPane settingsGrid = new GridPane();
        settingsGrid.setHgap(10);
        settingsGrid.setVgap(15);
        settingsGrid.setAlignment(Pos.CENTER);

        // 보드 형태
        settingsGrid.add(new Label("보드 형태:"), 0, 0);
        settingsGrid.add(boardTypeCombo, 1, 0);

        // 플레이어 수
        settingsGrid.add(new Label("플레이어 수:"), 0, 1);
        settingsGrid.add(playerCountCombo, 1, 1);

        // 말 개수
        settingsGrid.add(new Label("말 개수:"), 0, 2);
        settingsGrid.add(pieceCountCombo, 1, 2);

        // 컴포넌트 추가
        mainContainer.getChildren().addAll(titleLabel, settingsGrid, startButton);

        // 씬 생성
        scene = new Scene(mainContainer, 400, 300);
    }

    private void startGame() {
        // 선택된 설정 가져오기
        Board.BoardType boardType;
        switch (boardTypeCombo.getValue()) {
            case "오각형":
                boardType = Board.BoardType.PENTAGON;
                break;
            case "육각형":
                boardType = Board.BoardType.HEXAGON;
                break;
            default:
                boardType = Board.BoardType.SQUARE;
        }

        int playerCount = playerCountCombo.getValue();
        int pieceCount = pieceCountCombo.getValue();

        // 게임 설정 생성
        GameSettings settings = new GameSettings(playerCount, pieceCount, boardType);

        // 게임 컨트롤러 생성
        FXGameController controller = new FXGameController();
        controller.initializeGame(settings);

        // 게임 화면으로 전환
        GameScene gameScene = new GameScene(primaryStage, controller);
        primaryStage.setScene(gameScene.getScene());
        primaryStage.setTitle("윷놀이 게임 - JavaFX");
    }

    public Scene getScene() {
        return scene;
    }
}