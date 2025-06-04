package org.example.model;

import java.util.List;

/**
 * 게임 턴 관리를 담당하는 서비스 클래스
 * 턴 전환, 추가 턴 처리, 윷 결과 관리 등의 책임을 분리
 */
public class TurnService {
    private int currentTurnIndex;
    private boolean hasExtraTurn;
    private boolean captureExtraTurnUsed;
    private boolean isExtraTurnThrow;
    private List<Yut.YutResult> pendingYutResults;
    private List<String> gameLog;

    /**
     * 생성자
     * @param pendingYutResults 윷 결과 목록 참조
     * @param gameLog 게임 로그 참조
     */
    public TurnService(List<Yut.YutResult> pendingYutResults, List<String> gameLog) {
        this.currentTurnIndex = 0;
        this.hasExtraTurn = false;
        this.captureExtraTurnUsed = false;
        this.isExtraTurnThrow = false;
        this.pendingYutResults = pendingYutResults;
        this.gameLog = gameLog;
    }

    /**
     * 턴 초기화
     */
    public void initializeTurn() {
        this.currentTurnIndex = 0;
        this.hasExtraTurn = false;
        this.captureExtraTurnUsed = false;
        this.isExtraTurnThrow = false;
    }

    /**
     * 현재 턴 인덱스 반환
     */
    public int getCurrentTurnIndex() {
        return currentTurnIndex;
    }

    /**
     * 추가 턴 여부 반환
     */
    public boolean hasExtraTurn() {
        return hasExtraTurn;
    }

    /**
     * 다음 턴으로 전환
     * @param players 플레이어 목록
     */
    public void nextTurn(List<Player> players) {
        if (!hasExtraTurn) {
            currentTurnIndex = (currentTurnIndex + 1) % players.size();
            addToGameLog(players.get(currentTurnIndex).getName() + "의 턴입니다.");
        } else {
            hasExtraTurn = false;
            captureExtraTurnUsed = false;
            addToGameLog(players.get(currentTurnIndex).getName() + "의 추가 턴입니다.");
        }

//        // 디버깅: 턴 시작 시 모든 플레이어의 말 상태 출력
//        for (Player player : players) {
//            debugPrintPlayerPieces(player);
//        }
    }

    /**
     * 윷 던지기 전 처리
     */
    public void prepareForYutThrow() {
        if (!hasExtraTurn) {
            pendingYutResults.clear();
            isExtraTurnThrow = false;
        } else {
            isExtraTurnThrow = true;
        }

        if (hasExtraTurn && pendingYutResults.isEmpty()) {
            captureExtraTurnUsed = true;
        }
    }

    /**
     * 윷 결과에 따른 추가 턴 처리
     * @param result 윷 결과
     * @param currentPlayer 현재 플레이어
     */
    public void processYutResult(Yut.YutResult result, Player currentPlayer) {
        if (result == Yut.YutResult.YUT || result == Yut.YutResult.MO) {
            hasExtraTurn = true;
            addToGameLog(currentPlayer.getName() + "에게 추가 턴이 부여되었습니다. (윷/모)");
        } else {
            hasExtraTurn = false;
        }

        pendingYutResults.add(result);
    }

    /**
     * 잡기로 인한 추가 턴 부여
     * @param currentPlayer 현재 플레이어
     */
    public void grantCaptureExtraTurn(Player currentPlayer) {
        hasExtraTurn = true;
        captureExtraTurnUsed = false;
        addToGameLog(currentPlayer.getName() + "에게 추가 턴이 부여되었습니다. (잡기)");
    }

    /**
     * 윷 결과 사용 처리
     * @param result 사용할 윷 결과
     */
    public void useYutResult(Yut.YutResult result) {
        pendingYutResults.remove(result);
    }

    /**
     * 턴 종료 처리
     * @param players 플레이어 목록
     */
    public void endTurnIfNoExtraTurn(List<Player> players) {
        // 경우 1: 추가 턴이 있고, 윷 결과가 비어있는 경우
        if (hasExtraTurn && pendingYutResults.isEmpty()) {
            // 1-1: 상대방 말을 잡아서 추가 턴을 얻은 경우
            if (captureExtraTurnUsed) {
                // 잡기로 얻은 추가 턴이 이미 사용되었으므로 턴 종료
                hasExtraTurn = false;
                isExtraTurnThrow = false;
                captureExtraTurnUsed = false;
                nextTurn(players);
                return;
            }

            // 1-2: 윷/모로 추가 턴을 얻었거나, 잡기 추가 턴을 아직 사용하지 않은 경우
            // → 현재 플레이어가 계속해서 윷을 던져야 함
            addToGameLog(players.get(currentTurnIndex).getName() + "의 추가 턴입니다. 윷을 던지세요.");
            return;
        } else if (!pendingYutResults.isEmpty()) {
            return;
        }

        // 경우 2: 추가 턴이 없는 경우 - 다음 플레이어로 턴 전환
        if (!hasExtraTurn) {
            nextTurn(players);
            pendingYutResults.clear();
            return;
        }

        // 경우 3: 추가 턴이 있고, 아직 윷 결과가 남아있는 경우
        // → 현재 플레이어가 계속 진행
        addToGameLog(players.get(currentTurnIndex).getName() + "의 추가 턴이 남아있습니다. 윷 결과: " +
                formatPendingResults());
    }

    /**
     * 추가 턴 강제 해제
     */
    public void setHasExtraTransferFalse() {
        hasExtraTurn = false;
    }

    /**
     * 저장된 윷 결과 목록을 문자열로 변환
     */
    private String formatPendingResults() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pendingYutResults.size(); i++) {
            Yut.YutResult result = pendingYutResults.get(i);
            sb.append(result.getName()).append("(").append(result.getMoveCount()).append("칸)");
            if (i < pendingYutResults.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    /**
     * 게임 로그에 메시지 추가
     */
    private void addToGameLog(String message) {
        gameLog.add(message);
    }

    /**
     * 디버깅용: 플레이어의 모든 말 상태 출력
     */
    private void debugPrintPlayerPieces(Player player) {
        if (player == null) {
            addToGameLog("[디버그] 플레이어 객체가 null입니다.");
            return;
        }

        List<Piece> pieces = player.getPieces();

        for (Piece piece : pieces) {
            Place currentPlace = piece.getCurrentPlace();

            if (!piece.getStackedPieces().isEmpty()) {
                addToGameLog("  업힌 말 목록:");
                for (Piece stackedPiece : piece.getStackedPieces()) {
                    addToGameLog("    - " + stackedPiece.getId());
                }
            }
            addToGameLog("-----------");
        }
        addToGameLog("===================");
    }
}