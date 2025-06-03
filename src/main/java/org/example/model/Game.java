package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 윷놀이 게임의 전체 상태를 관리하는 모델 클래스
 * MVC 아키텍처에서 Model 역할을 담당
 * GameInteractionService를 사용하여 말 상호작용 로직 분리
 */
public class Game {
    private List<Player> players;
    private int currentTurnIndex;
    private Board board;
    private Yut yut;
    private GameSettings gameSettings;
    private boolean isGameFinished;
    private Player winner;
    private Yut.YutResult lastYutResult;
    private boolean hasExtraTurn;
    private List<String> gameLog;
    private List<Yut.YutResult> pendingYutResults;
    private boolean captureExtraTurnUsed;
    private boolean isExtraTurnThrow;

    // 말 상호작용 서비스 (잡기/업기 로직 분리)
    private GameInteractionService interactionService;

    /**
     * 기본 생성자
     */
    public Game() {
        this.players = new ArrayList<>();
        this.currentTurnIndex = 0;
        this.yut = new Yut();
        this.isGameFinished = false;
        this.hasExtraTurn = false;
        this.gameLog = new ArrayList<>();
        this.pendingYutResults = new ArrayList<>();
        this.captureExtraTurnUsed = false;
        this.isExtraTurnThrow = false;

        // 상호작용 서비스 초기화
        this.interactionService = new GameInteractionService();
    }

    /**
     * 게임 설정을 통한 초기화
     * @param settings 게임 설정
     */
    public void initialize(GameSettings settings) {
        this.gameSettings = settings;
        this.currentTurnIndex = 0;
        this.isGameFinished = false;
        this.winner = null;
        this.hasExtraTurn = false;
        this.gameLog.clear();
        this.pendingYutResults.clear();
        this.captureExtraTurnUsed = false;
        this.isExtraTurnThrow = false;

        // 보드 초기화
        this.board = new Board(settings.getBoardType());

        // 플레이어 초기화
        this.players.clear();
        for (int i = 0; i < settings.getPlayerCount(); i++) {
            Player player = new Player("Player " + (i + 1));

            // 플레이어당 말 생성
            for (int j = 0; j < settings.getPiecePerPlayer(); j++) {
                Piece piece = new Piece("P" + (i + 1) + "-" + (j + 1), player);
                player.addPiece(piece);
            }

            this.players.add(player);
        }

        // 초기 로그 추가
        addToGameLog("게임이 시작되었습니다. 보드 형태: " + settings.getBoardType() +
                ", 플레이어 수: " + settings.getPlayerCount() +
                ", 말 개수: " + settings.getPiecePerPlayer());
    }

    /**
     * 현재 턴의 플레이어 반환
     * @return 현재 턴 플레이어
     */
    public Player getCurrentPlayer() {
        return players.get(currentTurnIndex);
    }

    /**
     * 다음 턴으로 전환
     */
    public void nextTurn() {
        if (!hasExtraTurn) {
            currentTurnIndex = (currentTurnIndex + 1) % players.size();
            addToGameLog(getCurrentPlayer().getName() + "의 턴입니다.");
        } else {
            hasExtraTurn = false;
            captureExtraTurnUsed = false;
            addToGameLog(getCurrentPlayer().getName() + "의 추가 턴입니다.");
        }

        // 디버깅: 턴 시작 시 모든 플레이어의 말 상태 출력
        addToGameLog("[디버그] === 턴 시작 시 게임 상태 ===");
        for (Player player : players) {
            debugPrintPlayerPieces(player);
        }
    }

    /**
     * 윷 던지기 (랜덤)
     * @return 윷 결과
     */
    public Yut.YutResult throwYut() {
        if (!hasExtraTurn) {
            pendingYutResults.clear();
            isExtraTurnThrow = false;
        } else {
            isExtraTurnThrow = true;
        }

        if (hasExtraTurn && pendingYutResults.isEmpty()) {
            captureExtraTurnUsed = true;
        }

        lastYutResult = yut.throwYut();
        addToGameLog(getCurrentPlayer().getName() + "이(가) 윷을 던져 " +
                lastYutResult.getName() + "(" + lastYutResult.getMoveCount() + "칸)가 나왔습니다.");

        if (lastYutResult == Yut.YutResult.YUT || lastYutResult == Yut.YutResult.MO) {
            hasExtraTurn = true;
            addToGameLog(getCurrentPlayer().getName() + "에게 추가 턴이 부여되었습니다. (윷/모)");
        }else{
            hasExtraTurn = false;
        }

        pendingYutResults.add(lastYutResult);
        return lastYutResult;
    }

    /**
     * 지정된 윷 결과 설정 (테스트용)
     * @param result 지정할 윷 결과
     * @return 지정된 윷 결과
     */
    public Yut.YutResult setSpecificYutResult(Yut.YutResult result) {
        if (!hasExtraTurn) {
            pendingYutResults.clear();
            isExtraTurnThrow = false;
        } else {
            isExtraTurnThrow = true;
        }

        if (hasExtraTurn && pendingYutResults.isEmpty()) {
            captureExtraTurnUsed = true;
        }

        lastYutResult = result;
        addToGameLog(getCurrentPlayer().getName() + "이(가) " +
                result.getName() + "(" + result.getMoveCount() + "칸)로 지정했습니다.");

        if (result == Yut.YutResult.YUT || result == Yut.YutResult.MO) {
            hasExtraTurn = true;
            addToGameLog(getCurrentPlayer().getName() + "에게 추가 턴이 부여되었습니다. (윷/모)");
        }

        pendingYutResults.add(result);
        return result;
    }

    /**
     * 현재 플레이어의 이동 가능한 말 목록 반환
     * @param player 플레이어
     * @param result 윷 결과
     * @return 이동 가능한 말 목록
     */
    public List<Piece> getMovablePieces(Player player, Yut.YutResult result) {
        List<Piece> movablePieces = new ArrayList<>();

        for (Piece piece : player.getPieces()) {
            if (!piece.isCompleted() && !piece.isCarried()) {
                if (result == Yut.YutResult.BACKDO &&
                        (piece.getCurrentPlace() == null || piece.getCurrentPlace().isStartingPoint())) {
                    continue;
                }
                movablePieces.add(piece);
            }
        }

        return movablePieces;
    }

    /**
     * 말 이동 실행 (GameInteractionService를 사용하여 리팩토링)
     * @param piece 이동할 말
     * @param result 윷 결과
     * @return 이동 후 위치
     */
    public Place movePiece(Piece piece, Yut.YutResult result) {
        // 요청한 결과가 pendingYutResults에 있는지 확인
        if (!pendingYutResults.contains(result)) {
            addToGameLog("[오류] 이동에 사용할 수 없는 윷 결과입니다.");
            return null;
        }

        // 현재 위치 계산
        Place currentPlace = piece.getCurrentPlace();
        if (currentPlace == null) {
            currentPlace = board.getStartingPlace();
        }

        // 목적지 계산
        Place destination = board.calculateDestination(currentPlace, result);
        addToGameLog(destination.toString());

        // 이동 실행
        piece.moveTo(destination);

        // 이동 로그 추가
        addToGameLog(getCurrentPlayer().getName() + "의 말 " + piece.getId() +
                "이(가) " + (currentPlace.getName() != null ? currentPlace.getName() : "시작점") +
                "에서 " + (destination.getName() != null ? destination.getName() : "도착점") +
                "으로 " + result.getName() + "(" + result.getMoveCount() + "칸) 만큼 이동했습니다.");

        // 사용한 윷 결과 제거
        pendingYutResults.remove(result);

        // 상호작용 처리 (GameInteractionService 사용)
        handlePieceInteractions(piece, destination);

        return destination;
    }

    /**
     * 말 이동 후 상호작용 처리 (잡기/업기)
     * @param piece 이동한 말
     * @param destination 이동한 위치
     */
    private void handlePieceInteractions(Piece piece, Place destination) {
        // 중심점 특별 처리
        if (destination.isCenter()) {
            // 중심점 잡기 확인
            boolean captured = interactionService.checkCenterCapture(piece, board, gameLog);
            if (captured) {
                // 잡기 발생 시 추가 턴 부여
                Player currentPlayer = getCurrentPlayer();
                if (piece.getPlayer().equals(currentPlayer)) {
                    hasExtraTurn = true;
                    captureExtraTurnUsed = false;
                    addToGameLog(currentPlayer.getName() + "에게 추가 턴이 부여되었습니다. (중심점 잡기)");
                }
                return; // 잡기가 발생하면 업기 처리 안함
            }

            // 잡기가 발생하지 않았으면 중심점 업기 확인
            boolean stacked = interactionService.checkCenterStacking(piece, board, gameLog);

            // 중심점 업기 이후에도 같은 위치의 업기 확인
            interactionService.checkAndApplyGrouping(destination, piece, board, gameLog);
        }
        // 일반적인 잡기 확인
        else if (interactionService.isCapture(destination, getCurrentPlayer())) {
            boolean captured = interactionService.applyCapture(piece, board, gameLog);
            if (captured) {
                // 잡기 후 추가 턴 부여
                Player currentPlayer = getCurrentPlayer();
                if (piece.getPlayer().equals(currentPlayer)) {
                    hasExtraTurn = true;
                    captureExtraTurnUsed = false;
                    addToGameLog(currentPlayer.getName() + "에게 추가 턴이 부여되었습니다. (잡기)");
                }
            }
        }
        // 업기 확인
        else {
            interactionService.checkAndApplyGrouping(destination, piece, board, gameLog);
        }
    }

    /**
     * 말이 다른 플레이어의 말을 잡을 수 있는지 확인 (서비스로 위임)
     * @param place 위치
     * @return 잡기 가능 여부
     */
    public boolean isCapture(Place place) {
        return interactionService.isCapture(place, getCurrentPlayer());
    }

    /**
     * 말이 다른 플레이어의 말을 잡음 (서비스로 위임)
     * @param capturingPiece 잡는 말
     * @return 잡기 성공 여부
     */
    public boolean applyCapture(Piece capturingPiece) {
        boolean result = interactionService.applyCapture(capturingPiece, board, gameLog);
        if (result) {
            // 잡기 후 추가 턴 부여
            Player currentPlayer = getCurrentPlayer();
            if (capturingPiece.getPlayer().equals(currentPlayer)) {
                hasExtraTurn = true;
                captureExtraTurnUsed = false;
                addToGameLog(currentPlayer.getName() + "에게 추가 턴이 부여되었습니다. (잡기)");
            }
        }
        return result;
    }

    /**
     * 같은 플레이어의 말 업기 (서비스로 위임)
     * @param piece1 기준 말
     * @param piece2 업힐 말
     * @return 업기 성공 여부
     */
    public boolean applyGrouping(Piece piece1, Piece piece2) {
        return interactionService.applyGrouping(piece1, piece2, gameLog);
    }

    /**
     * 중심점 간의 업기를 처리하는 메서드 (서비스로 위임)
     * @param piece 현재 이동한 말
     * @return 업기가 발생했으면 true, 아니면 false
     */
    public boolean checkCenterStacking(Piece piece) {
        return interactionService.checkCenterStacking(piece, board, gameLog);
    }

    /**
     * 중심점 간의 잡기를 처리하는 메서드 (서비스로 위임)
     * @param piece 현재 이동한 말
     * @return 잡기가 발생했으면 true, 아니면 false
     */
    public boolean checkCenterCapture(Piece piece) {
        boolean result = interactionService.checkCenterCapture(piece, board, gameLog);
        if (result) {
            // 잡기 후 추가 턴 부여
            Player currentPlayer = getCurrentPlayer();
            if (piece.getPlayer().equals(currentPlayer)) {
                hasExtraTurn = true;
                captureExtraTurnUsed = false;
                addToGameLog(currentPlayer.getName() + "에게 추가 턴이 부여되었습니다. (중심점 잡기)");
            }
        }
        return result;
    }

    /**
     * 현재 위치에서 같은 플레이어의 모든 말 업기 확인 및 처리 (서비스로 위임)
     * @param place 현재 위치
     * @param currentPiece 현재 말
     */
    public void checkAndApplyGrouping(Place place, Piece currentPiece) {
        interactionService.checkAndApplyGrouping(place, currentPiece, board, gameLog);
    }

    /**
     * 플레이어의 모든 말이 완주했는지 확인 (승리 조건)
     * @param player 확인할 플레이어
     * @return 승리 여부
     */
    public boolean isPlayerWinner(Player player) {
        return player.isAllPiecesCompleted();
    }

    /**
     * 게임 종료 조건 확인
     * @return 게임 종료 여부
     */
    public boolean checkGameEnd() {
        for (Player player : players) {
            if (isPlayerWinner(player)) {
                isGameFinished = true;
                winner = player;
                addToGameLog(player.getName() + "이(가) 게임에서 승리했습니다!");
                return true;
            }
        }
        return false;
    }

    /**
     * 턴 종료 시 호출되는 메소드
     */
    public void endTurnIfNoExtraTurn() {
        // 경우 1: 추가 턴이 있고, 윷 결과가 비어있는 경우
        if (hasExtraTurn && pendingYutResults.isEmpty()) {
            // 1-1: 상대방 말을 잡아서 추가 턴을 얻은 경우
            if (captureExtraTurnUsed) {
                // 잡기로 얻은 추가 턴이 이미 사용되었으므로 턴 종료
                hasExtraTurn = false;
                isExtraTurnThrow = false;
                captureExtraTurnUsed = false;
                nextTurn();
                return;
            }

            // 1-2: 윷/모로 추가 턴을 얻었거나, 잡기 추가 턴을 아직 사용하지 않은 경우
            // → 현재 플레이어가 계속해서 윷을 던져야 함
            addToGameLog(getCurrentPlayer().getName() + "의 추가 턴입니다. 윷을 던지세요.");
            return;
        }

        // 경우 2: 추가 턴이 없는 경우 - 다음 플레이어로 턴 전환
        if (!hasExtraTurn) {
            nextTurn();
            pendingYutResults.clear();
            return;
        }

        // 경우 3: 추가 턴이 있고, 아직 윷 결과가 남아있는 경우
        // → 현재 플레이어가 계속 진행
        addToGameLog(getCurrentPlayer().getName() + "의 추가 턴이 남아있습니다. 윷 결과: " +
                formatPendingResults());
    }
    /**
     * 저장된 윷 결과 목록을 문자열로 변환
     * @return 윷 결과 문자열
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
     * 게임 재시작
     * @param settings 새 게임 설정 (null이면 기존 설정 유지)
     */
    public void restartGame(GameSettings settings) {
        if (settings != null) {
            initialize(settings);
        } else if (gameSettings != null) {
            initialize(gameSettings);
        }
        addToGameLog("게임이 재시작되었습니다.");
    }

    /**
     * 디버깅용: 특정 위치의 말 정보 출력
     */
    public void debugPrintPlaceInfo(Place place) {
        if (place == null) {
            addToGameLog("[디버그] 위치 객체가 null입니다.");
            return;
        }

        addToGameLog("=== [디버그] 위치 정보 ===");
        addToGameLog("위치 ID: " + place.getId());
        addToGameLog("위치 이름: " + place.getName());
        addToGameLog("분기점 여부: " + place.isJunction());
        addToGameLog("중앙점 여부: " + place.isCenter());
        addToGameLog("시작점 여부: " + place.isStartingPoint());
        addToGameLog("도착점 여부: " + place.isEndingPoint());

        List<Piece> pieces = place.getPieces();
        addToGameLog("말 개수: " + pieces.size());

        if (!pieces.isEmpty()) {
            addToGameLog("--- 말 목록 ---");
            for (int i = 0; i < pieces.size(); i++) {
                Piece piece = pieces.get(i);
                addToGameLog((i+1) + ". ID: " + piece.getId() +
                        ", 플레이어: " + piece.getPlayer().getName() +
                        ", 업힌 말 수: " + piece.getStackedPieces().size());

                if (!piece.getStackedPieces().isEmpty()) {
                    addToGameLog("   업힌 말 목록:");
                    for (Piece stackedPiece : piece.getStackedPieces()) {
                        addToGameLog("    - " + stackedPiece.getId() +
                                " (소유자: " + stackedPiece.getPlayer().getName() + ")");
                    }
                }
            }
        }
        addToGameLog("===============");
    }

    /**
     * 디버깅용: 플레이어의 모든 말 상태 출력
     */
    public void debugPrintPlayerPieces(Player player) {
        if (player == null) {
            addToGameLog("[디버그] 플레이어 객체가 null입니다.");
            return;
        }

        addToGameLog("=== [디버그] " + player.getName() + "의 말 상태 ===");
        List<Piece> pieces = player.getPieces();
        addToGameLog("총 말 개수: " + pieces.size());

        for (Piece piece : pieces) {
            Place currentPlace = piece.getCurrentPlace();
            String locationInfo = currentPlace != null ?
                    (currentPlace.getId() + " (" + currentPlace.getName() + ")") :
                    "보드에 없음 (업힌 상태 또는 초기 상태)";

            addToGameLog("말 " + piece.getId() + ":");
            addToGameLog("  위치: " + locationInfo);
            addToGameLog("  완주 여부: " + piece.isCompleted());
            addToGameLog("  업힌 말 수: " + piece.getStackedPieces().size());

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

    /**
     * 게임 로그에 메시지 추가
     * @param message 로그 메시지
     */
    private void addToGameLog(String message) {
        gameLog.add(message);
    }

    // Getter 메소드들

    /**
     * 현재 플레이어 목록 반환
     * @return 플레이어 목록
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * 현재 턴 인덱스 반환
     * @return 현재 턴 인덱스
     */
    public int getCurrentTurnIndex() {
        return currentTurnIndex;
    }

    /**
     * 게임 보드 반환
     * @return 게임 보드
     */
    public Board getBoard() {
        return board;
    }

    /**
     * 게임 설정 반환
     * @return 게임 설정
     */
    public GameSettings getGameSettings() {
        return gameSettings;
    }

    /**
     * 게임 종료 여부 반환
     * @return 게임 종료 여부
     */
    public boolean isGameFinished() {
        return isGameFinished;
    }

    /**
     * 승리자 반환
     * @return 승리 플레이어 (없으면 null)
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * 마지막 윷 결과 반환
     * @return 마지막 윷 결과
     */
    public Yut.YutResult getLastYutResult() {
        return lastYutResult;
    }

    /**
     * 마지막 윷 결과 설정 (테스트용)
     * @param result 윷 결과
     */
    void setLastYutResult(Yut.YutResult result) {
        this.lastYutResult = result;
    }

    public void setHasExrtraTurnFalse(){
        hasExtraTurn = false;
    }
    /**
     * 추가 턴 여부 반환
     * @return 추가 턴 여부
     */
    public boolean hasExtraTurn() {
        return hasExtraTurn;
    }

    /**
     * 게임 로그 반환
     * @return 게임 로그 목록
     */
    public List<String> getGameLog() {
        return new ArrayList<>(gameLog);
    }

    /**
     * 윷/모 결과를 저장하는 리스트 반환
     * @return 윷/모 결과를 저장하는 리스트
     */
    public List<Yut.YutResult> getPendingYutResults() {
        return new ArrayList<>(pendingYutResults);
    }
}