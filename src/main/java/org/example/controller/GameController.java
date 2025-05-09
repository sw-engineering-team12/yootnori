package org.example.controller;

import org.example.model.*;
import org.example.view.GameBoardPanel;
import org.example.view.GameFrame;

import javax.swing.*;
import java.util.ArrayList;
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

        // 윷 던진 후 이동 가능한 말이 있는지 확인
        checkMovablePieces();

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

        // 윷 던진 후 이동 가능한 말이 있는지 확인
        checkMovablePieces();

        return setResult;
    }

    /**
     * 이동 가능한 말이 있는지 확인하고, 없으면 턴을 자동 종료
     */
    private void checkMovablePieces() {
        if (!game.getPendingYutResults().isEmpty()) {
            List<Piece> movablePieces = getMovablePieces();
            if (movablePieces.isEmpty()) {
                // 이동 가능한 말이 없으면 메시지 표시
                if (gameFrame != null) {
                    JOptionPane.showMessageDialog(gameFrame,
                            "이동 가능한 말이 없습니다. 턴을 넘깁니다.",
                            "알림",
                            JOptionPane.INFORMATION_MESSAGE);
                }

                // 턴 종료 처리
                game.endTurnIfNoExtraTurn();
                updateUI();
            }
        }
    }

    /**
     * 말 이동 실행
     * @param piece 이동할 말
     * @param yutResult 이동에 사용할 윷 결과
     * @return 이동 후 위치
     */
    public Place movePiece(Piece piece, Yut.YutResult yutResult) {
        if (game.getPendingYutResults().isEmpty()) {
            JOptionPane.showMessageDialog(gameFrame, "먼저 윷을 던져야 합니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

        // 윷 결과가 제공되지 않은 경우 첫 번째 결과를 사용 (기존 기능과의 호환성)
        if (yutResult == null) {
            yutResult = game.getPendingYutResults().get(0);
        }

        Place destination = game.movePiece(piece, yutResult);
        if (destination == null) {
            JOptionPane.showMessageDialog(gameFrame, "이동할 수 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

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

            // 다음 플레이어의 턴으로 넘어갔을 때, 이동 가능한 말이 있는지 확인
            SwingUtilities.invokeLater(() -> {
                updateUI();
                checkMovablePieces();
            });
        }

        updateUI();
        return destination;
    }

    /**
     * 말 이동 실행 (기존 메서드와의 호환성 유지)
     * @param piece 이동할 말
     * @return 이동 후 위치
     */
    public Place movePiece(Piece piece) {
        return movePiece(piece, null);
    }

    /**
     * 현재 턴 플레이어의 이동 가능한 말 목록 반환
     * @return 이동 가능한 말 목록
     */
    public List<Piece> getMovablePieces() {
        if (game.getPendingYutResults().isEmpty()) {
            return new ArrayList<>();
        }

        // 여러 윷 결과가 있어도 말이 이동할 수 있는지 여부는 동일함
        Yut.YutResult firstResult = game.getPendingYutResults().get(0);
        List<Piece> allPieces = game.getMovablePieces(game.getCurrentPlayer(), firstResult);
        List<Piece> validMovablePieces = new ArrayList<>();

        // 업혀있지 않은 말만 반환
        for (Piece piece : allPieces) {
            if (!piece.isCarried()) {
                validMovablePieces.add(piece);
            }
        }

        return validMovablePieces;
    }

    /**
     * 현재 보류 중인 윷 결과 목록 반환
     * @return 윷 결과 목록
     */
    public List<Yut.YutResult> getPendingYutResults() {
        return game.getPendingYutResults();
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