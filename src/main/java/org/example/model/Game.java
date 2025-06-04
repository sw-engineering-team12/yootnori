package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 윷놀이 게임의 전체 상태를 관리하는 모델 클래스
 * MVC 아키텍처에서 Model 역할을 담당
 * GameInteractionService를 사용하여 말 상호작용 로직 분리
 * TurnService를 사용하여 턴 관리 로직 분리
 */
public class Game {
    private List<Player> players;
    private Board board;
    private Yut yut;
    private GameSettings gameSettings;
    private boolean isGameFinished;
    private Player winner;
    private Yut.YutResult lastYutResult;
    private List<String> gameLog;
    private List<Yut.YutResult> pendingYutResults;

    // 말 상호작용 서비스 (잡기/업기 로직 분리)
    private GameInteractionService interactionService;
    // 턴 관리 서비스 (턴 전환 로직 분리)
    private TurnService turnService;

    /**
     * 기본 생성자
     */
    public Game() {
        this.players = new ArrayList<>();
        this.yut = new Yut();
        this.isGameFinished = false;
        this.gameLog = new ArrayList<>();
        this.pendingYutResults = new ArrayList<>();

        // 상호작용 서비스 초기화
        this.interactionService = new GameInteractionService();

        // 턴 서비스 초기화
        this.turnService = new TurnService(pendingYutResults, gameLog);
    }

    /**
     * 게임 설정을 통한 초기화
     * @param settings 게임 설정
     */
    public void initialize(GameSettings settings) {
        this.gameSettings = settings;
        this.isGameFinished = false;
        this.winner = null;
        this.gameLog.clear();
        this.pendingYutResults.clear();

        // 턴 서비스 초기화
        this.turnService.initializeTurn();

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
        return players.get(turnService.getCurrentTurnIndex());
    }

    /**
     * 다음 턴으로 전환
     */
    public void nextTurn() {
        turnService.nextTurn(players);
    }

    /**
     * 윷 던지기 (랜덤)
     * @return 윷 결과
     */
    public Yut.YutResult throwYut() {
        turnService.prepareForYutThrow();

        lastYutResult = yut.throwYut();
        addToGameLog(getCurrentPlayer().getName() + "이(가) 윷을 던져 " +
                lastYutResult.getName() + "(" + lastYutResult.getMoveCount() + "칸)가 나왔습니다.");

        turnService.processYutResult(lastYutResult, getCurrentPlayer());

        // 이동 가능한 말이 있는지 확인
        checkMovablePieces();

        return lastYutResult;
    }

    /**
     * 지정된 윷 결과 설정 (테스트용)
     * @param result 지정할 윷 결과
     * @return 지정된 윷 결과
     */
    public Yut.YutResult setSpecificYutResult(Yut.YutResult result) {
        turnService.prepareForYutThrow();

        lastYutResult = result;
        addToGameLog(getCurrentPlayer().getName() + "이(가) " +
                result.getName() + "(" + result.getMoveCount() + "칸)로 지정했습니다.");

        turnService.processYutResult(result, getCurrentPlayer());
        checkMovablePieces();

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
        turnService.useYutResult(result);

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
                    turnService.grantCaptureExtraTurn(currentPlayer);
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
                    turnService.grantCaptureExtraTurn(currentPlayer);
                }
            }
        }
        // 업기 확인
        else {
            interactionService.checkAndApplyGrouping(destination, piece, board, gameLog);
        }
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
        turnService.endTurnIfNoExtraTurn(players);
    }

    /**
     * 이동 가능한 말이 있는지 확인하고, 없으면 턴을 자동 종료
     */
    private void checkMovablePieces() {
        if (!pendingYutResults.isEmpty()) {
            List<Piece> movablePieces = getMovablePieces();
            if (movablePieces.isEmpty()) {
                addToGameLog("이동 가능한 말이 없습니다. 턴을 넘깁니다.");
                pendingYutResults.clear(); // 이 줄 추가 필요!
                endTurnIfNoExtraTurn();
            }
        }
    }

    /**
     * 현재 턴 플레이어의 이동 가능한 말 목록 반환
     * @return 이동 가능한 말 목록
     */
    public List<Piece> getMovablePieces() {
        if (pendingYutResults.isEmpty()) {
            return new ArrayList<>();
        }

        Yut.YutResult firstResult = pendingYutResults.get(0);
        List<Piece> allPieces = getMovablePieces(getCurrentPlayer(), firstResult);
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
        return turnService.getCurrentTurnIndex();
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

    /**
     * 추가 턴 강제 해제 (테스트용)
     */
    public void setHasExrtraTurnFalse(){
        turnService.setHasExtraTransferFalse();
    }

    /**
     * 추가 턴 여부 반환
     * @return 추가 턴 여부
     */
    public boolean hasExtraTurn() {
        return turnService.hasExtraTurn();
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