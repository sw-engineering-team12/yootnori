package org.example.controller.javafx;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.example.controller.AbstractGameController;
import org.example.view.javafx.GameBoardPane;
import org.example.view.javafx.GameScene;

import java.util.Optional;

/**
 * JavaFX UI용 게임 컨트롤러
 */
public class FXGameController extends AbstractGameController {
    private GameScene gameScene;
    private GameBoardPane boardPane;

    /**
     * UI 설정
     * @param gameScene 게임 화면
     * @param boardPane 게임 보드 패널
     */
    public void setUI(GameScene gameScene, GameBoardPane boardPane) {
        this.gameScene = gameScene;
        this.boardPane = boardPane;
        notifyGameStateChanged();
    }

    @Override
    public void notifyGameStateChanged() {
        Platform.runLater(() -> {
            if (boardPane != null) {
                boardPane.updateBoard();
            }
            if (gameScene != null) {
                gameScene.updateGameInfo();
            }
        });
    }

    @Override
    protected void showMessage(String message, String title) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @Override
    protected boolean showConfirmDialog(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    @Override
    protected void handleApplicationExit() {
        Platform.exit();
    }
}