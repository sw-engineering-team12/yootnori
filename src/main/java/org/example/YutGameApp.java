package org.example;

import org.example.view.swing.GameSetupFrame;

import javax.swing.*;

/**
 * Swing 기반 윷놀이 게임 애플리케이션
 */
public class YutGameApp {
    public static void main(String[] args) {
        // Look and Feel 설정 제거 (호환성 문제 방지)

        // UI 컴포넌트는 Event Dispatch Thread에서 생성해야 함
        SwingUtilities.invokeLater(() -> {
            // 게임 설정 화면 표시
            GameSetupFrame setupFrame = new GameSetupFrame();
            setupFrame.setVisible(true);
        });
    }
}