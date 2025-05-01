package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 윷놀이 게임의 전체 상태를 관리하는 모델 클래스
 * MVC 아키텍처에서 Model 역할을 담당
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
            addToGameLog(getCurrentPlayer().getName() + "의 추가 턴입니다.");
        }
    }

    /**
     * 윷 던지기 (랜덤)
     * @return 윷 결과
     */
    public Yut.YutResult throwYut() {
        lastYutResult = yut.throwYut();
        addToGameLog(getCurrentPlayer().getName() + "이(가) 윷을 던져 " +
                lastYutResult.getName() + "(" + lastYutResult.getMoveCount() + "칸)가 나왔습니다.");

        // 윷/모 결과에 대한 추가 턴 처리
        if (lastYutResult == Yut.YutResult.YUT || lastYutResult == Yut.YutResult.MO) {
            hasExtraTurn = true;
            addToGameLog(getCurrentPlayer().getName() + "에게 추가 턴이 부여되었습니다. (윷/모)");
        }

        return lastYutResult;
    }

    /**
     * 지정된 윷 결과 설정 (테스트용)
     * @param result 지정할 윷 결과
     * @return 지정된 윷 결과
     */
    public Yut.YutResult setSpecificYutResult(Yut.YutResult result) {
        lastYutResult = result;
        hasExtraTurn = false;
        addToGameLog(getCurrentPlayer().getName() + "이(가) " +
                result.getName() + "(" + result.getMoveCount() + "칸)로 지정했습니다.");

        // 윷/모 결과에 대한 추가 턴 처리
        if (result == Yut.YutResult.YUT || result == Yut.YutResult.MO) {
            hasExtraTurn = true;
            addToGameLog(getCurrentPlayer().getName() + "에게 추가 턴이 부여되었습니다. (윷/모)");
        }

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

        // 완주하지 않은 말만 포함
        for (Piece piece : player.getPieces()) {
            if (!piece.isCompleted()) {
                movablePieces.add(piece);
            }
        }

        return movablePieces;
    }

    /**
     * 말 이동 실행
     * @param piece 이동할 말
     * @param result 윷 결과
     * @return 이동 후 위치
     */
    public Place movePiece(Piece piece, Yut.YutResult result) {
        // 현재 위치 (시작점이거나 이미 보드에 있는 경우)
        Place currentPlace = piece.getCurrentPlace();
        if (currentPlace == null) {
            // 시작점에서 출발
            currentPlace = board.getStartingPlace();
        }

        // 목적지 계산
        Place destination = board.calculateDestination(currentPlace, result);

        // 이동 실행
        piece.moveTo(destination);

        // 이동 로그 추가
        addToGameLog(getCurrentPlayer().getName() + "의 말 " + piece.getId() +
                "이(가) " + (currentPlace.getName() != null ? currentPlace.getName() : "시작점") +
                "에서 " + (destination.getName() != null ? destination.getName() : "도착점") +
                "으로 이동했습니다.");

        // 업힌 말이 있는 경우 함께 이동
        if (!piece.getStackedPieces().isEmpty()) {
            addToGameLog("업힌 말 " + piece.getStackedPieces().size() + "개가 함께 이동했습니다.");
        }

        // 도착 위치에서 상대방 말 잡기 확인
        if (isCapture(destination)) {
            addToGameLog("도착 위치에 상대방 말이 있습니다. 잡기를 시도합니다.");
            applyCapture(piece);
        }
        // 도착 위치에서 같은 플레이어 말 업기 확인
        else {
            checkAndApplyGrouping(destination, piece);
        }

        return destination;
    }
    /**
     * 말이 다른 플레이어의 말을 잡을 수 있는지 확인
     * @param place 위치
     * @return 잡기 가능 여부
     */
    public boolean isCapture(Place place) {
        Player currentPlayer = getCurrentPlayer();

        // 해당 위치에 다른 플레이어의 말이 있는지 확인
        List<Piece> opponentPieces = place.getOpponentPieces(currentPlayer);
        return !opponentPieces.isEmpty();
    }

    /**
     * 말이 다른 플레이어의 말을 잡음
     * @param capturingPiece 잡는 말
     * @return 잡기 성공 여부
     */
    public boolean applyCapture(Piece capturingPiece) {
        Place currentPlace = capturingPiece.getCurrentPlace();
        if (currentPlace == null) {
            return false;
        }

        Player currentPlayer = getCurrentPlayer();
        List<Piece> opponentPieces = currentPlace.getOpponentPieces(currentPlayer);

        if (opponentPieces.isEmpty()) {
            return false;
        }

        // 잡기 실행
        for (Piece opponentPiece : opponentPieces) {
            Player opponentPlayer = opponentPiece.getPlayer();

            // 업힌 말 목록을 복사 (ConcurrentModificationException 방지)
            List<Piece> stackedPieces = new ArrayList<>(opponentPiece.getStackedPieces());

            // 로그에 잡기 기록
            addToGameLog(currentPlayer.getName() + "의 말 " + capturingPiece.getId() +
                    "이(가) " + opponentPlayer.getName() + "의 말 " +
                    opponentPiece.getId() + "을(를) 잡았습니다.");

            // 업힌 말들 먼저 처리
            if (!stackedPieces.isEmpty()) {
                addToGameLog("업힌 말 " + stackedPieces.size() + "개도 함께 시작점으로 돌아갑니다.");

                // 그룹화 해제 전에 업힌 말들을 정확히 로깅
                for (Piece stackedPiece : stackedPieces) {
                    addToGameLog("업힌 말 " + stackedPiece.getId() + "이(가) 시작점으로 돌아갑니다.");
                }
            }

            // 시작점으로 돌아가기 전에 그룹화 해제
            opponentPiece.unstackAllPieces();

            // 메인 말 시작점으로 이동 (실제 보드의 시작 위치로)
            opponentPiece.moveTo(board.getStartingPlace());
        }

        // 잡기 후 추가 턴 부여
        hasExtraTurn = true;
        addToGameLog(currentPlayer.getName() + "에게 추가 턴이 부여되었습니다. (잡기)");

        return true;
    }
    /**
     * 같은 플레이어의 말 업기
     * @param piece1 기준 말
     * @param piece2 업힐 말
     * @return 업기 성공 여부
     */
    public boolean applyGrouping(Piece piece1, Piece piece2) {
        // 같은 플레이어의 말인지 확인
        if (!piece1.getPlayer().equals(piece2.getPlayer())) {
            addToGameLog("업기 실패: 서로 다른 플레이어의 말입니다.");
            return false;
        }

        // 같은 위치에 있는지 확인
        Place place1 = piece1.getCurrentPlace();
        Place place2 = piece2.getCurrentPlace();

        if (place1 == null || place2 == null) {
            addToGameLog("업기 실패: 말 중 하나가 보드 위에 없습니다.");
            return false;
        }

        if (!place1.equals(place2)) {
            addToGameLog("업기 실패: 두 말이 서로 다른 위치에 있습니다.");
            return false;
        }

        // 업기 실행
        piece1.stackPiece(piece2);

        addToGameLog(piece1.getPlayer().getName() + "의 말 " + piece1.getId() +
                "이(가) " + piece2.getId() + "을(를) 업었습니다.");

        // 업기 후 place2에서 piece2가 제거되었는지 확인
        if (place2.getPieces().contains(piece2)) {
            // 문제가 있는 경우 로그 추가 및 수동으로 제거
            addToGameLog("경고: 업기 후에도 말이 보드에 남아있습니다. 수동으로 제거합니다.");
            place2.removePiece(piece2);
        }

        return true;
    }
    /**
     * 현재 위치에서 같은 플레이어의 모든 말 업기 확인 및 처리
     * @param place 현재 위치
     * @param currentPiece 현재 말
     */
    public void checkAndApplyGrouping(Place place, Piece currentPiece) {
        if (place == null || currentPiece == null) {
            return;
        }

        Player currentPlayer = currentPiece.getPlayer();
        List<Piece> piecesAtPlace = place.getPieces();

        // 이미 같은 플레이어의 말이 이 위치에 있는지 확인
        for (Piece otherPiece : piecesAtPlace) {
            // 자기 자신이 아니고, 같은 플레이어의 말이면 업기
            if (!otherPiece.equals(currentPiece) && otherPiece.getPlayer().equals(currentPlayer)) {
                // 로그에 업기 시도 기록
                addToGameLog(currentPlayer.getName() + "의 말 " + currentPiece.getId() +
                        "이(가) 같은 위치의 말 " + otherPiece.getId() + "을(를) 업기 시도합니다.");

                // 업기 적용 (현재 이동한 말에 기존 말을 업음)
                boolean groupingResult = applyGrouping(currentPiece, otherPiece);

                if (groupingResult) {
                    addToGameLog("업기 성공: " + currentPiece.getId() + "에 " + otherPiece.getId() + "이(가) 업혔습니다.");
                } else {
                    addToGameLog("업기 실패: 조건이 맞지 않습니다.");
                }
            }
        }
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
     * 추가 턴이 없으면 다음 플레이어로 턴 전환
     */
    public void endTurnIfNoExtraTurn() {
        if (!hasExtraTurn) {
            nextTurn();
        } else {
            // 추가 턴이 있으면 hasExtraTurn은 false로 설정하지 않고, 다음 턴 시작 시 처리
            addToGameLog(getCurrentPlayer().getName() + "의 추가 턴이 시작됩니다.");
        }
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
}