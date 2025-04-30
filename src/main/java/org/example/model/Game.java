package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 윷놀이 게임 진행을 관리하는 클래스
 */
public class Game {
    private Board board;
    private List<Player> players;
    private Player currentPlayer;
    private int currentPlayerIndex;
    private Yut yut;
    private GameState state;
    private List<Yut.YutResult> pendingResults; // 던진 윷 결과 대기열

    /**
     * 게임 상태를 나타내는 열거형
     */
    public enum GameState {
        NOT_STARTED, // 시작 전
        WAITING_FOR_YUT, // 윷 던지기 대기
        WAITING_FOR_PIECE_SELECTION, // 말 선택 대기
        GAME_OVER // 게임 종료
    }

    /**
     * 생성자
     * @param boardType 보드 타입
     * @param playerCount 플레이어 수
     * @param pieceCount 말의 개수
     */
    public Game(Board.BoardType boardType, int playerCount, int pieceCount) {
        this.board = new Board(boardType);
        this.players = new ArrayList<>();
        this.yut = new Yut();
        this.state = GameState.NOT_STARTED;
        this.pendingResults = new ArrayList<>();

        // 플레이어 초기화
        initializePlayers(playerCount, pieceCount);

        // 첫 플레이어 설정
        this.currentPlayerIndex = 0;
        this.currentPlayer = players.get(currentPlayerIndex);

        // 게임 시작
        this.state = GameState.WAITING_FOR_YUT;
    }

    /**
     * 플레이어와 말 초기화
     * @param playerCount 플레이어 수
     * @param pieceCount 말의 개수
     */
    private void initializePlayers(int playerCount, int pieceCount) {
        for (int i = 0; i < playerCount; i++) {
            Player player = new Player("Player " + (i + 1));

            // 말 생성
            for (int j = 0; j < pieceCount; j++) {
                Piece piece = new Piece("P" + i + "-" + j, player);
                player.addPiece(piece);
            }

            players.add(player);
        }
    }

    /**
     * 랜덤 윷 던지기
     * @return 윷 던지기 결과
     */
    public Yut.YutResult throwYut() {
        if (state != GameState.WAITING_FOR_YUT) {
            throw new IllegalStateException("Not waiting for yut throw");
        }

        Yut.YutResult result = yut.throwYut();
        pendingResults.add(result);

        // 윷이나 모가 나오면 한 번 더 던질 수 있음
        if (result == Yut.YutResult.YUT || result == Yut.YutResult.MO) {
            // 상태는 그대로 유지
        } else {
            state = GameState.WAITING_FOR_PIECE_SELECTION;
        }

        return result;
    }

    /**
     * 특정 윷 결과 지정 (테스트용)
     * @param result 지정할 윷 결과
     * @return 지정된 윷 결과
     */
    public Yut.YutResult throwSpecificYut(Yut.YutResult result) {
        if (state != GameState.WAITING_FOR_YUT) {
            throw new IllegalStateException("Not waiting for yut throw");
        }

        pendingResults.add(result);

        // 윷이나 모가 나오면 한 번 더 던질 수 있음
        if (result == Yut.YutResult.YUT || result == Yut.YutResult.MO) {
            // 상태는 그대로 유지
        } else {
            state = GameState.WAITING_FOR_PIECE_SELECTION;
        }

        return result;
    }

    /**
     * 말 선택 및 이동
     * @param piece 선택한 말
     * @return 이동 결과 정보
     */
    public MoveResult movePiece(Piece piece) {
        if (state != GameState.WAITING_FOR_PIECE_SELECTION || pendingResults.isEmpty()) {
            throw new IllegalStateException("Not waiting for piece selection or no pending yut results");
        }

        // 현재 플레이어의 말인지 확인
        if (!piece.getPlayer().equals(currentPlayer)) {
            throw new IllegalArgumentException("Selected piece does not belong to current player");
        }

        // 첫 번째 대기 중인 윷 결과 사용
        Yut.YutResult yutResult = pendingResults.remove(0);

        // 현재 위치
        Place currentPlace = piece.getCurrentPlace();
        if (currentPlace == null) {
            currentPlace = board.getStartingPlace();
        }

        // 이동할 위치 계산
        Place destinationPlace = board.calculateDestination(currentPlace, yutResult);

        // 말 이동
        boolean moved = piece.moveTo(destinationPlace);

        // 이동 결과 객체 생성
        MoveResult moveResult = new MoveResult(piece, currentPlace, destinationPlace);

        // 말 잡기 체크
        List<Piece> capturedPieces = checkCapture(piece, destinationPlace);
        moveResult.setCapturedPieces(capturedPieces);

        // 말 업기 체크
        checkStack(piece, destinationPlace);

        // 게임 종료 체크
        if (checkGameOver()) {
            state = GameState.GAME_OVER;
            moveResult.setGameOver(true);
            moveResult.setWinner(currentPlayer);
        }
        // 다음 차례 결정
        else if (pendingResults.isEmpty() && capturedPieces.isEmpty()) {
            // 잡은 말이 없고 더 이상 윷 결과가 없으면 다음 플레이어로
            nextPlayer();
            state = GameState.WAITING_FOR_YUT;
        } else if (pendingResults.isEmpty() && !capturedPieces.isEmpty()) {
            // 말을 잡았으면 한 번 더 윷 던지기
            state = GameState.WAITING_FOR_YUT;
        } else {
            // 아직 처리할 윷 결과가 남아있으면 계속 말 선택 대기
            state = GameState.WAITING_FOR_PIECE_SELECTION;
        }

        return moveResult;
    }

    /**
     * 말 잡기 체크
     * @param piece 이동한 말
     * @param place 말이 이동한 위치
     * @return 잡힌 말 목록
     */
    private List<Piece> checkCapture(Piece piece, Place place) {
        List<Piece> capturedPieces = new ArrayList<>();

        // 시작점, 도착점, 중앙점은 잡기 불가
        if (place.isStartingPoint() || place.isEndingPoint() || place.isCenter()) {
            return capturedPieces;
        }

        // 모든 상대방 말 확인
        List<Piece> opponentPieces = place.getOpponentPieces(piece.getPlayer());
        for (Piece opponentPiece : opponentPieces) {
            // 말 잡기
            capturedPieces.add(opponentPiece);

            // 잡힌 말을 시작점으로 이동
            opponentPiece.moveTo(board.getStartingPlace());

            // 업혀있던 말들도 함께 이동
            for (Piece stackedPiece : opponentPiece.getStackedPieces()) {
                stackedPiece.moveTo(board.getStartingPlace());
                capturedPieces.add(stackedPiece);
            }
        }

        return capturedPieces;
    }

    /**
     * 말 업기 체크
     * @param piece 이동한 말
     * @param place 말이 이동한 위치
     */
    private void checkStack(Piece piece, Place place) {
        // 같은 플레이어의 다른 말이 있는지 확인
        Piece otherPiece = place.getPlayerPiece(piece.getPlayer());

        // 자기 자신이 아니고, 다른 말이 있으면 업기
        if (otherPiece != null && !otherPiece.equals(piece)) {
            otherPiece.stackPiece(piece);
        }
    }

    /**
     * 게임 종료 조건 체크
     * @return 게임 종료 여부
     */
    private boolean checkGameOver() {
        // 현재 플레이어의 모든 말이 완주했는지 확인
        return currentPlayer.isAllPiecesCompleted();
    }

    /**
     * 다음 플레이어로 턴 변경
     */
    private void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        currentPlayer = players.get(currentPlayerIndex);
    }

    // 게터 메서드들
    public Board getBoard() {
        return board;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public GameState getState() {
        return state;
    }

    public List<Yut.YutResult> getPendingResults() {
        return new ArrayList<>(pendingResults);
    }

    /**
     * 이동 결과를 담는 내부 클래스
     */
    public class MoveResult {
        private Piece piece;
        private Place fromPlace;
        private Place toPlace;
        private List<Piece> capturedPieces;
        private boolean isGameOver;
        private Player winner;

        public MoveResult(Piece piece, Place fromPlace, Place toPlace) {
            this.piece = piece;
            this.fromPlace = fromPlace;
            this.toPlace = toPlace;
            this.capturedPieces = new ArrayList<>();
            this.isGameOver = false;
            this.winner = null;
        }

        // 게터와 세터
        public Piece getPiece() {
            return piece;
        }

        public Place getFromPlace() {
            return fromPlace;
        }

        public Place getToPlace() {
            return toPlace;
        }

        public List<Piece> getCapturedPieces() {
            return capturedPieces;
        }

        public void setCapturedPieces(List<Piece> capturedPieces) {
            this.capturedPieces = capturedPieces;
        }

        public boolean isGameOver() {
            return isGameOver;
        }

        public void setGameOver(boolean gameOver) {
            isGameOver = gameOver;
        }

        public Player getWinner() {
            return winner;
        }

        public void setWinner(Player winner) {
            this.winner = winner;
        }
    }
}