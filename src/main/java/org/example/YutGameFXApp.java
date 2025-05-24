package org.example;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.view.javafx.GameSetupScene;

/**
 * JavaFX 기반 윷놀이 게임 애플리케이션
 */
public class YutGameFXApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("윷놀이 게임 - JavaFX");

        // 게임 설정 화면으로 시작
        GameSetupScene setupScene = new GameSetupScene(primaryStage);
        primaryStage.setScene(setupScene.getScene());

        primaryStage.setResizable(true);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(300);

        primaryStage.show();
    }
}