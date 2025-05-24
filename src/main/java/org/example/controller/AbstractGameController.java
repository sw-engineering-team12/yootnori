package org.example.controller;

import org.example.model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 게임 컨트롤러의 추상 구현
 * UI에 독립적인 게임 로직을 제공합니다.
 */
public abstract class AbstractGameController implements GameController {
    protected Game game;

    @Override
    public void initializeGame(GameSettings settings) {
        this.game = new Game();
        this.game.initialize(settings);
        notifyGameStateChanged();
    }

    @Override
    public Yut.YutResult throwYut() {
        if (game == null) {
            throw new IllegalStateException("게임이 초기화되지 않았습니다.");
        }

        Yut.YutResult result = game.throwYut();
        notifyGameStateChanged();

        // 이동 가능한 말이 있는지 확인
        checkMovablePieces();

        return result;
    }

    @Override
    public Yut.YutResult setSpecificYutResult(Yut.YutResult result) {
        if (game == null) {
            throw new IllegalStateException("게임이 초기화되지 않았습니다.");
        }

        Yut.YutResult setResult = game.setSpecificYutResult(result);
        notifyGameStateChanged();

        // 이동 가능한 말이 있는지 확인
        checkMovablePieces();

        return setResult;
    }

    @Override
    public Place movePiece(Piece piece, Yut.YutResult yutResult) {
        if (game == null) {
            throw new IllegalStateException("게임이 초기화되지 않았습니다.");
        }

        if (game.getPendingYutResults().isEmpty()) {
            showMessage("먼저 윷을 던져야 합니다.", "알림");
            return null;
        }

        // 윷 결과가 제공되지 않은 경우 첫 번째 결과를 사용
        if (yutResult == null) {
            yutResult = game.getPendingYutResults().get(0);
        }

        Place destination = game.movePiece(piece, yutResult);
        if (destination == null) {
            showMessage("이동할 수 없습니다.", "알림");
            return null;
        }

        // 이동 후 게임 종료 체크
        if (game.checkGameEnd()) {
            handleGameEnd(game.getWinner());
        } else {
            // 다음 턴으로 진행
            game.endTurnIfNoExtraTurn();
            notifyGameStateChanged();
        }

        return destination;
    }

    @Override
    public Place movePiece(Piece piece) {
        return movePiece(piece, null);
    }

    @Override
    public List<Piece> getMovablePieces() {
        if (game == null || game.getPendingYutResults().isEmpty()) {
            return new ArrayList<>();
        }

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

    @Override
    public List<Yut.YutResult> getPendingYutResults() {
        if (game == null) {
            return new ArrayList<>();
        }
        return game.getPendingYutResults();
    }

    @Override
    public Game getGame() {
        return game;
    }

    /**
     * 이동 가능한 말이 있는지 확인하고, 없으면 턴을 자동 종료
     */
    private void checkMovablePieces() {
        if (!game.getPendingYutResults().isEmpty()) {
            List<Piece> movablePieces = getMovablePieces();
            if (movablePieces.isEmpty()) {
                showMessage("이동 가능한 말이 없습니다. 턴을 넘깁니다.", "알림");
                game.endTurnIfNoExtraTurn();
                notifyGameStateChanged();
            }
        }
    }

    @Override
    public void handleGameEnd(Player winner) {
        showMessage(winner.getName() + "님이 승리했습니다!", "게임 종료");

        boolean restart = showConfirmDialog(
                "게임을 다시 시작하시겠습니까?",
                "게임 재시작"
        );

        if (restart) {
            game.restartGame(game.getGameSettings());
            notifyGameStateChanged();
        } else {
            handleApplicationExit();
        }
    }

    // 추상 메서드들 - UI별로 구현 필요
    protected abstract void showMessage(String message, String title);
    protected abstract boolean showConfirmDialog(String message, String title);
    protected abstract void handleApplicationExit();
}