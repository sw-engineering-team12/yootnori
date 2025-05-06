package org.example;

import org.example.controller.GameController;
import org.example.model.Board;
import org.example.model.GameSettings;
import org.example.view.GameSetupFrame;

import javax.swing.*;

public class YutGameApp {
    public static void main(String[] args) {
        try {
            // Swing UI 테마 설정
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // UI 컴포넌트는 Event Dispatch Thread에서 생성해야 함
        SwingUtilities.invokeLater(() -> {
            // 게임 설정 화면 표시
            GameSetupFrame setupFrame = new GameSetupFrame();
            setupFrame.setVisible(true);
        });
    }
}