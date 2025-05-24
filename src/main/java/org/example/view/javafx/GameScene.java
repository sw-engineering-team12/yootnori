package org.example.view.javafx;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.controller.javafx.FXGameController;
import org.example.model.Game;
import org.example.model.Piece;
import org.example.model.Yut;

import java.util.List;

/**
 * JavaFX 게임 메인 화면
 */
public class GameScene {
    private Scene scene;
    private Stage primaryStage;
    private FXGameController controller;
    private GameBoardPane boardPane;

    // UI 컴포넌트들
    private Label currentPlayerLabel;
    private Label yutResultLabel;
    private TextArea logTextArea;
    private Button randomYutButton;
    private Button specificYutButton;
    private ComboBox<String> yutSelectionCombo;
    private ListView<String> pieceListView;
    private ListView<String> pendingYutListView;
    private Label pendingYutLabel;
    private Button moveButton;

    public GameScene(Stage primaryStage, FXGameController controller) {
        this.primaryStage = primaryStage;
        this.controller = controller;

        initializeComponents();
        setupLayout();

        // 컨트롤러에 UI 설정
        controller.setUI(this, boardPane);
    }

    private void initializeComponents() {
        // 게임 보드
        boardPane = new GameBoardPane(controller);

        // 컨트롤 버튼들
        randomYutButton = new Button("랜덤 윷 던지기");
        randomYutButton.setOnAction(e -> {
            Yut.YutResult result = controller.throwYut();
            updateYutResult(result);
            updatePieceList();
            updatePendingYutList();
        });

        specificYutButton = new Button("지정 윷 던지기");
        specificYutButton.setOnAction(e -> {
            String selected = yutSelectionCombo.getValue();
            Yut.YutResult result;

            switch (selected) {
                case "빽도": result = Yut.YutResult.BACKDO; break;
                case "도": result = Yut.YutResult.DO; break;
                case "개": result = Yut.YutResult.GAE; break;
                case "걸": result = Yut.YutResult.GEOL; break;
                case "윷": result = Yut.YutResult.YUT; break;
                case "모": result = Yut.YutResult.MO; break;
                default: result = Yut.YutResult.DO;
            }

            Yut.YutResult setResult = controller.setSpecificYutResult(result);
            updateYutResult(setResult);
            updatePieceList();
            updatePendingYutList();
        });

        yutSelectionCombo = new ComboBox<>();
        yutSelectionCombo.getItems().addAll("빽도", "도", "개", "걸", "윷", "모");
        yutSelectionCombo.setValue("도");

        pieceListView = new ListView<>();
        pieceListView.setPrefHeight(100);

        pendingYutListView = new ListView<>();
        pendingYutListView.setPrefHeight(80);
        pendingYutLabel = new Label("이동에 사용할 윷 결과:");

        moveButton = new Button("말 이동");
        moveButton.setOnAction(e -> {
            int selectedPieceIndex = pieceListView.getSelectionModel().getSelectedIndex();
            if (selectedPieceIndex == -1) {
                showAlert("이동할 말을 선택해주세요.", "알림");
                return;
            }

            int selectedYutIndex = pendingYutListView.getSelectionModel().getSelectedIndex();
            Yut.YutResult selectedYutResult = null;
            List<Yut.YutResult> pendingResults = controller.getPendingYutResults();

            if (selectedYutIndex != -1 && selectedYutIndex < pendingResults.size()) {
                selectedYutResult = pendingResults.get(selectedYutIndex);
            } else if (!pendingResults.isEmpty()) {
                selectedYutResult = pendingResults.get(0);
            } else {
                showAlert("사용할 윷 결과가 없습니다.", "알림");
                return;
            }

            List<Piece> movablePieces = controller.getMovablePieces();
            if (movablePieces != null && !movablePieces.isEmpty() && selectedPieceIndex < movablePieces.size()) {
                Piece selectedPiece = movablePieces.get(selectedPieceIndex);

                if (!selectedPiece.getStackedPieces().isEmpty() &&
                        (selectedPiece.getCurrentPlace() == null ||
                                "시작점".equals(selectedPiece.getCurrentPlace().getName()) ||
                                "start".equalsIgnoreCase(selectedPiece.getCurrentPlace().getName()))) {

                    showAlert("업힌 말입니다.", "알림");
                    return;
                }

                controller.movePiece(selectedPiece, selectedYutResult);
                updatePendingYutList();
            }
        });

        // 정보 라벨들
        currentPlayerLabel = new Label("현재 턴: Player 1");
        yutResultLabel = new Label("윷 결과: 없음");

        // 로그 영역
        logTextArea = new TextArea();
        logTextArea.setEditable(false);
        logTextArea.setPrefRowCount(8);
    }

    private void setupLayout() {
        // 메인 컨테이너
        BorderPane mainContainer = new BorderPane();

        // 보드를 중앙에 배치
        mainContainer.setCenter(boardPane);

        // 오른쪽 사이드 패널
        VBox sidePanel = new VBox(10);
        sidePanel.setPadding(new Insets(10));
        sidePanel.setPrefWidth(300);

        // 컨트롤 섹션
        VBox controlSection = createControlSection();

        // 정보 섹션
        VBox infoSection = createInfoSection();

        // 로그 섹션
        VBox logSection = createLogSection();

        sidePanel.getChildren().addAll(controlSection, infoSection, logSection);

        mainContainer.setRight(sidePanel);

        // 씬 생성
        scene = new Scene(mainContainer, 1024, 768);
    }

    private VBox createControlSection() {
        VBox controlSection = new VBox(10);
        controlSection.setPadding(new Insets(10));
        controlSection.setStyle("-fx-border-color: gray; -fx-border-width: 1;");

        Label controlTitle = new Label("컨트롤");
        controlTitle.setStyle("-fx-font-weight: bold;");

        // 윷 던지기 섹션
        HBox yutSection = new HBox(5);
        yutSection.getChildren().addAll(randomYutButton, specificYutButton, yutSelectionCombo);

        // 저장된 윷 결과 섹션
        VBox pendingYutSection = new VBox(5);
        pendingYutSection.getChildren().addAll(pendingYutLabel, pendingYutListView);

        // 말 목록 섹션
        VBox pieceSection = new VBox(5);
        pieceSection.getChildren().addAll(new Label("이동 가능한 말:"), pieceListView);

        controlSection.getChildren().addAll(controlTitle, yutSection, pendingYutSection, pieceSection, moveButton);

        return controlSection;
    }

    private VBox createInfoSection() {
        VBox infoSection = new VBox(5);
        infoSection.setPadding(new Insets(10));
        infoSection.setStyle("-fx-border-color: gray; -fx-border-width: 1;");

        Label infoTitle = new Label("게임 정보");
        infoTitle.setStyle("-fx-font-weight: bold;");

        infoSection.getChildren().addAll(infoTitle, currentPlayerLabel, yutResultLabel);

        return infoSection;
    }

    private VBox createLogSection() {
        VBox logSection = new VBox(5);
        logSection.setPadding(new Insets(10));
        logSection.setStyle("-fx-border-color: gray; -fx-border-width: 1;");

        Label logTitle = new Label("게임 로그");
        logTitle.setStyle("-fx-font-weight: bold;");

        ScrollPane logScrollPane = new ScrollPane(logTextArea);
        logScrollPane.setFitToWidth(true);
        logScrollPane.setPrefHeight(200);

        logSection.getChildren().addAll(logTitle, logScrollPane);

        return logSection;
    }

    private void showAlert(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * 윷 결과 업데이트
     */
    private void updateYutResult(Yut.YutResult result) {
        if (result != null) {
            yutResultLabel.setText("마지막 윷 결과: " + result.getName() + " (" + result.getMoveCount() + "칸)");
        } else {
            yutResultLabel.setText("윷 결과: 없음");
        }
    }

    /**
     * 이동 가능한 말 목록 업데이트
     */
    private void updatePieceList() {
        pieceListView.getItems().clear();
        List<Piece> movablePieces = controller.getMovablePieces();

        if (movablePieces != null) {
            for (Piece piece : movablePieces) {
                String location = piece.getCurrentPlace() != null ?
                        piece.getCurrentPlace().getName() : "시작점";

                String stackInfo = piece.getStackedPieces().isEmpty() ?
                        "" : " (업힌 말: " + piece.getStackedPieces().size() + "개)";

                String carriedInfo = piece.isCarried() ?
                        " [" + piece.getCarriedBy().getId() + "에 업힘]" : "";

                pieceListView.getItems().add(piece.getId() + " - " + location + stackInfo + carriedInfo);
            }
        }
    }

    /**
     * 저장된 윷 결과 목록 업데이트
     */
    private void updatePendingYutList() {
        pendingYutListView.getItems().clear();
        List<Yut.YutResult> pendingResults = controller.getPendingYutResults();

        if (pendingResults != null && !pendingResults.isEmpty()) {
            for (Yut.YutResult result : pendingResults) {
                pendingYutListView.getItems().add(result.getName() + " (" + result.getMoveCount() + "칸)");
            }
            pendingYutLabel.setText("이동에 사용할 윷 결과 선택 (" + pendingResults.size() + "개):");
        } else {
            pendingYutLabel.setText("이동에 사용할 윷 결과가 없습니다.");
        }
    }

    /**
     * 게임 정보 업데이트
     */
    public void updateGameInfo() {
        Game game = controller.getGame();
        if (game != null) {
            currentPlayerLabel.setText("현재 턴: " + game.getCurrentPlayer().getName());

            // 게임 로그 업데이트
            StringBuilder logBuilder = new StringBuilder();
            List<String> logs = game.getGameLog();
            for (String log : logs) {
                logBuilder.append(log).append("\n");
            }
            logTextArea.setText(logBuilder.toString());

            // 스크롤을 맨 아래로
            logTextArea.setScrollTop(Double.MAX_VALUE);

            // 윷 결과 업데이트
            updateYutResult(game.getLastYutResult());

            // 말 목록 업데이트
            updatePieceList();

            // 저장된 윷 결과 목록 업데이트
            updatePendingYutList();
        }
    }

    public Scene getScene() {
        return scene;
    }
}