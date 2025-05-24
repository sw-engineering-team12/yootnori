package org.example;

import javax.swing.*;

/**
 * 윷놀이 게임의 메인 진입점
 * 사용자가 Swing 또는 JavaFX UI를 선택할 수 있습니다.
 */
public class Main {
    public static void main(String[] args) {
        // 명령행 인수로 UI 타입 선택
        String uiType = "auto"; // 기본값: 자동 선택

        if (args.length > 0) {
            uiType = args[0].toLowerCase();
        }

        switch (uiType) {
            case "swing":
                startSwingUI();
                break;
            case "javafx":
                startJavaFXUI();
                break;
            case "auto":
            default:
                showUISelectionDialog();
                break;
        }
    }

    /**
     * Swing UI 시작
     */
    private static void startSwingUI() {
        System.out.println("Swing UI로 게임을 시작합니다...");
        YutGameApp.main(new String[0]);
    }

    /**
     * JavaFX UI 시작
     */
    private static void startJavaFXUI() {
        System.out.println("JavaFX UI로 게임을 시작합니다...");
        YutGameFXApp.main(new String[0]);
    }

    /**
     * UI 선택 다이얼로그 표시
     */
    private static void showUISelectionDialog() {
        // Look and Feel 설정 제거 (호환성 문제 방지)

        String[] options = {"Swing UI", "JavaFX UI", "취소"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "사용할 UI를 선택하세요:",
                "윷놀이 게임 - UI 선택",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        switch (choice) {
            case 0: // Swing UI
                startSwingUI();
                break;
            case 1: // JavaFX UI
                startJavaFXUI();
                break;
            default: // 취소 또는 닫기
                System.out.println("게임이 취소되었습니다.");
                System.exit(0);
                break;
        }
    }
}