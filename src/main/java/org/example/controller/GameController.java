package org.example.controller;

import org.example.model.*;
import org.example.view.GameBoardPanel;
import org.example.view.GameFrame;

import javax.swing.*;
import java.util.List;

/**
 * 게임 컨트롤러 클래스
 * Model과 View 사이의 중개자 역할
 */
public class GameController {
    private Game game;
    private GameFrame gameFrame;
    private GameBoardPanel boardPanel;

    /**
     * 생성자
     * @param settings 게임 설정
     */
    public GameController(GameSettings settings) {
        // 게임 모델 초기화
        game = new Game();
        game.initialize(settings);
    }

    /**
     * UI 설정
     * @param gameFrame 게임 프레임
     * @param boardPanel 게임 보드 패널
     */
    public void setUI(GameFrame gameFrame, GameBoardPanel boardPanel) {
        this.gameFrame = gameFrame;
        this.boardPanel = boardPanel;
        updateUI();
    }

    /**
     * 윷 던지기 실행
     * @return 윷 결과
     */
    public Yut.YutResult throwYut() {
        Yut.YutResult result = game.throwYut();
        updateUI();
        return result;
    }

    /**
     * 특정 윷 결과 지정 (테스트용)
     * @param result 지정할 윷 결과
     * @return 지정된 윷 결과
     */
    public Yut.YutResult setSpecificYutResult(Yut.YutResult result) {
        Yut.YutResult setResult = game.setSpecificYutResult(result);
        updateUI();
        return setResult;
    }

    /**
     * 말 이동 실행
     * @param piece 이동할 말
     * @return 이동 후 위치
     */
    public Place movePiece(Piece piece) {
        if (game.getLastYutResult() == null) {
            JOptionPane.showMessageDialog(gameFrame, "먼저 윷을 던져야 합니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

        Place destination = game.movePiece(piece, game.getLastYutResult());

        // 이동 후 게임 종료 체크
        if (game.checkGameEnd()) {
            JOptionPane.showMessageDialog(gameFrame,
                    game.getWinner().getName() + "님이 승리했습니다!",
                    "게임 종료", JOptionPane.INFORMATION_MESSAGE);

            // 재시작 여부 확인
            int option = JOptionPane.showConfirmDialog(gameFrame,
                    "게임을 다시 시작하시겠습니까?",
                    "게임 재시작",
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                game.restartGame(game.getGameSettings());
            } else {
                // 게임 종료
                System.exit(0);
            }
        } else {
            // 다음 턴으로 진행
            game.endTurnIfNoExtraTurn();
        }

        updateUI();
        return destination;
    }

    /**
     * 현재 턴 플레이어의 이동 가능한 말 목록 반환
     * @return 이동 가능한 말 목록
     */
    public List<Piece> getMovablePieces() {
        if (game.getLastYutResult() == null) {
            return null;
        }
        return game.getMovablePieces(game.getCurrentPlayer(), game.getLastYutResult());
    }

    /**
     * UI 업데이트
     */
    public void updateUI() {
        if (boardPanel != null) {
            boardPanel.updateBoard();
        }
        if (gameFrame != null) {
            gameFrame.updateGameInfo();
        }
    }

    /**
     * 게임 객체 반환
     * @return 게임 객체
     */
    public Game getGame() {
        return game;
    }
}