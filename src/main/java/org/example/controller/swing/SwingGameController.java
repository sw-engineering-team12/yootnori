package org.example.controller.swing;

import org.example.controller.AbstractGameController;
import org.example.view.swing.GameBoardPanel;
import org.example.view.swing.GameFrame;

import javax.swing.*;

/**
 * Swing UI용 게임 컨트롤러
 */
public class SwingGameController extends AbstractGameController {
    private GameFrame gameFrame;
    private GameBoardPanel boardPanel;

    /**
     * UI 설정
     * @param gameFrame 게임 프레임
     * @param boardPanel 게임 보드 패널
     */
    public void setUI(GameFrame gameFrame, GameBoardPanel boardPanel) {
        this.gameFrame = gameFrame;
        this.boardPanel = boardPanel;
        notifyGameStateChanged();
    }

    @Override
    public void notifyGameStateChanged() {
        SwingUtilities.invokeLater(() -> {
            if (boardPanel != null) {
                boardPanel.updateBoard();
            }
            if (gameFrame != null) {
                gameFrame.updateGameInfo();
            }
        });
    }

    @Override
    protected void showMessage(String message, String title) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(gameFrame, message, title, JOptionPane.INFORMATION_MESSAGE);
        });
    }

    @Override
    protected boolean showConfirmDialog(String message, String title) {
        int option = JOptionPane.showConfirmDialog(
                gameFrame,
                message,
                title,
                JOptionPane.YES_NO_OPTION
        );
        return option == JOptionPane.YES_OPTION;
    }

    @Override
    protected void handleApplicationExit() {
        System.exit(0);
    }
}