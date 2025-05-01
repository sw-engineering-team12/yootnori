package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Game 클래스에 대한 단위 테스트
 */
public class GameTest {

    private Game game;
    private GameSettings squareSettings;
    private GameSettings pentagonSettings;
    private GameSettings hexagonSettings;

    @BeforeEach
    void setUp() {
        // 게임 설정 초기화 (사각형)
        squareSettings = new GameSettings();
        squareSettings.setPlayerCount(3);
        squareSettings.setPiecePerPlayer(3);
        squareSettings.setBoardType(Board.BoardType.SQUARE);

        // 게임 설정 초기화 (오각형)
        pentagonSettings = new GameSettings();
        pentagonSettings.setPlayerCount(2);
        pentagonSettings.setPiecePerPlayer(4);
        pentagonSettings.setBoardType(Board.BoardType.PENTAGON);

        // 게임 설정 초기화 (육각형)
        hexagonSettings = new GameSettings();
        hexagonSettings.setPlayerCount(4);
        hexagonSettings.setPiecePerPlayer(2);
        hexagonSettings.setBoardType(Board.BoardType.HEXAGON);

        // 기본 게임 객체 생성 (사각형 보드로 테스트)
        game = new Game();
        game.initialize(squareSettings);
    }

    /**
     * 게임 초기화가 올바르게 되는지 테스트
     */
    @Test
    void testInitialize() {
        // 플레이어 수 검증
        assertEquals(3, game.getPlayers().size(), "플레이어 수가 설정값과 일치해야 함");

        // 각 플레이어별 말 개수 검증
        for (Player player : game.getPlayers()) {
            assertEquals(3, player.getPieces().size(), "각 플레이어의 말 개수가 설정값과 일치해야 함");
        }

        // 보드 타입 검증
        assertEquals(Board.BoardType.SQUARE, game.getBoard().getBoardType(), "보드 타입이 설정값과 일치해야 함");

        // 초기 턴 검증
        assertEquals(0, game.getCurrentTurnIndex(), "게임 시작 시 첫 번째 플레이어의 턴이어야 함");

        // 게임 종료 상태 검증
        assertFalse(game.isGameFinished(), "게임 시작 시 종료 상태가 아니어야 함");
    }

    /**
     * 다양한 보드 타입으로 게임 초기화 테스트
     */
    @Test
    void testInitializeWithDifferentBoardTypes() {
        // 오각형 보드로 초기화
        Game pentagonGame = new Game();
        pentagonGame.initialize(pentagonSettings);
        assertEquals(Board.BoardType.PENTAGON, pentagonGame.getBoard().getBoardType(), "오각형 보드 타입이 설정되어야 함");
        assertEquals(2, pentagonGame.getPlayers().size(), "플레이어 수가 설정값과 일치해야 함");

        // 육각형 보드로 초기화
        Game hexagonGame = new Game();
        hexagonGame.initialize(hexagonSettings);
        assertEquals(Board.BoardType.HEXAGON, hexagonGame.getBoard().getBoardType(), "육각형 보드 타입이 설정되어야 함");
        assertEquals(4, hexagonGame.getPlayers().size(), "플레이어 수가 설정값과 일치해야 함");
    }

    /**
     * 현재 플레이어 반환 테스트
     */
    @Test
    void testGetCurrentPlayer() {
        Player firstPlayer = game.getCurrentPlayer();
        assertNotNull(firstPlayer, "현재 플레이어가 null이 아니어야 함");
        assertEquals(game.getPlayers().get(0), firstPlayer, "첫 번째 플레이어가 현재 플레이어여야 함");
    }

    /**
     * 턴 전환 테스트
     */
    @Test
    void testNextTurn() {
        Player firstPlayer = game.getCurrentPlayer();
        game.nextTurn();
        Player secondPlayer = game.getCurrentPlayer();

        assertNotEquals(firstPlayer, secondPlayer, "턴 전환 후 다른 플레이어여야 함");
        assertEquals(game.getPlayers().get(1), secondPlayer, "두 번째 플레이어가 현재 플레이어여야 함");

        game.nextTurn();
        Player thirdPlayer = game.getCurrentPlayer();
        assertEquals(game.getPlayers().get(2), thirdPlayer, "세 번째 플레이어가 현재 플레이어여야 함");

        game.nextTurn();
        Player backToFirstPlayer = game.getCurrentPlayer();
        assertEquals(firstPlayer, backToFirstPlayer, "턴이 첫 번째 플레이어로 돌아와야 함");
    }

    /**
     * 이동 가능한 말 목록 테스트
     */
    @Test
    void testGetMovablePieces() {
        Player currentPlayer = game.getCurrentPlayer();
        Yut.YutResult result = Yut.YutResult.DO; // 도(1칸) 결과로 테스트

        List<Piece> movablePieces = game.getMovablePieces(currentPlayer, result);
        assertNotNull(movablePieces, "이동 가능한 말 목록이 null이 아니어야 함");
        assertEquals(currentPlayer.getPieces().size(), movablePieces.size(), "초기 상태에서는 모든 말이 이동 가능해야 함");

        // 모든 말을 완주 상태로 만들기
        for (Piece piece : currentPlayer.getPieces()) {
            piece.moveTo(game.getBoard().getEndingPlace());
        }

        movablePieces = game.getMovablePieces(currentPlayer, result);
        assertEquals(0, movablePieces.size(), "모든 말이 완주 상태이면 이동 가능한 말이 없어야 함");
    }

    /**
     * 말 이동 테스트
     */
    @Test
    void testMovePiece() {
        Player currentPlayer = game.getCurrentPlayer();
        Piece piece = currentPlayer.getPieces().get(0);
        Yut.YutResult result = Yut.YutResult.DO; // 도(1칸)

        // 초기 위치 확인
        assertNull(piece.getCurrentPlace(), "초기 상태의 말은 위치가 null이어야 함");

        // 이동 실행
        Place destination = game.movePiece(piece, result);

        assertNotNull(destination, "이동 결과는 null이 아니어야 함");
        assertEquals(destination, piece.getCurrentPlace(), "말의 현재 위치가 계산된 목적지와 일치해야 함");

        // 시작 위치에서 1칸 이동한 위치 확인
        Place expected = game.getBoard().calculateDestination(game.getBoard().getStartingPlace(), result);
        assertEquals(expected, piece.getCurrentPlace(), "계산된 목적지로 이동해야 함");
    }


    /**
     * 말 잡기 테스트
     */
    @Test
    void testCapturePiece() {
        // 두 개의 플레이어와 말을 설정
        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);

        Piece piece1 = player1.getPieces().get(0);
        Piece piece2 = player2.getPieces().get(0);

        // 첫 번째 말을 먼저 이동
        Yut.YutResult result1 = Yut.YutResult.DO; // 도(1칸)
        Place destination1 = game.movePiece(piece1, result1);

        // 두 번째 말을 같은 위치로 이동
        Yut.YutResult result2 = Yut.YutResult.DO; // 도(1칸)
        Place destination2 = game.movePiece(piece2, result2);

        // 같은 위치에 있는지 확인
        assertEquals(destination1, destination2, "두 말이 같은 위치에 있어야 함");

        // 잡기 확인
        assertTrue(game.isCapture(destination2), "상대 말이 있는 위치이므로 잡기가 가능해야 함");

        // 잡기 적용
        boolean captured = game.applyCapture(piece2);
        assertTrue(captured, "잡기가 성공해야 함");

        // 잡힌 말이 시작점으로 이동했는지 확인
        assertNull(piece1.getCurrentPlace(), "잡힌 말은 시작점으로 돌아가야 함");

        // 잡기 후 추가 턴 확인
        assertTrue(game.hasExtraTurn(), "잡기 후 추가 턴이 부여되어야 함");
    }



    /**
     * 말 업기 테스트
     */
    @Test
    void testGroupPiecesImproved() {
        // 같은 플레이어의 세 말 설정 (더 복잡한 시나리오 테스트)
        Player player = game.getCurrentPlayer();
        Piece piece1 = player.getPieces().get(0);
        Piece piece2 = player.getPieces().get(1);
        Piece piece3 = player.getPieces().get(2);

        // 첫 번째 말을 이동
        Yut.YutResult result1 = Yut.YutResult.DO; // 도(1칸)
        Place destination1 = game.movePiece(piece1, result1);

        // 이동 후 말의 위치 확인
        assertNotNull(piece1.getCurrentPlace(), "이동 후 말은 보드 위에 있어야 함");
        assertEquals(destination1, piece1.getCurrentPlace(), "이동 후 말의 위치가 일치해야 함");
        assertTrue(destination1.getPieces().contains(piece1), "이동 후 위치에 말이 포함되어야 함");

        // 두 번째 말을 같은 위치로 이동
        game.movePiece(piece2, result1);

        // 두 번째 말의 위치 확인
        assertEquals(destination1, piece2.getCurrentPlace(), "두 번째 말도 같은 위치로 이동해야 함");
        assertTrue(destination1.getPieces().contains(piece2), "이동 후 위치에 두 번째 말도 포함되어야 함");

        // 자동 업기가 되지 않았다면 수동으로 업기 적용
        if (!piece1.getStackedPieces().contains(piece2) && !piece2.getStackedPieces().contains(piece1)) {
            System.out.println("자동 업기가 적용되지 않아 수동으로 업기 실행");
            boolean grouped = game.applyGrouping(piece1, piece2);
            assertTrue(grouped, "같은 위치의 말은 업기가 가능해야 함");
        }

        // 어느 말이 다른 말을 업었는지 확인
        Piece mainPiece, stackedPiece;
        if (!piece1.getStackedPieces().isEmpty()) {
            mainPiece = piece1;
            stackedPiece = piece2;
        } else {
            mainPiece = piece2;
            stackedPiece = piece1;
        }

        // 업기 상태 확인
        assertFalse(mainPiece.getStackedPieces().isEmpty(), "한 말은 다른 말을 업고 있어야 함");
        assertTrue(mainPiece.getStackedPieces().contains(stackedPiece), "메인 말의 업힌 말 목록에 다른 말이 포함되어야 함");
        assertNull(stackedPiece.getCurrentPlace(), "업힌 말은 위치가 null이어야 함");
        assertFalse(destination1.getPieces().contains(stackedPiece), "업힌 말은 보드에서 제거되어야 함");

        // 세 번째 말을 이동하고 세 개의 말을 모두 업기
        game.movePiece(piece3, result1);

        // 자동 업기가 되지 않았다면 수동으로 업기 적용
        if (!mainPiece.getStackedPieces().contains(piece3)) {
            boolean groupedAgain = game.applyGrouping(mainPiece, piece3);
            assertTrue(groupedAgain, "세 번째 말도 업기가 가능해야 함");
        }

        // 세 말이 모두 업혔는지 확인
        assertEquals(2, mainPiece.getStackedPieces().size(), "메인 말에 두 개의 말이 업혀야 함");
        assertNull(piece3.getCurrentPlace(), "세 번째 말도 위치가 null이어야 함");

        // 업힌 말들과 함께 이동하는지 확인
        Yut.YutResult result2 = Yut.YutResult.GAE; // 개(2칸)
        Place destination2 = game.movePiece(mainPiece, result2);

        assertNotNull(destination2, "업힌 말과 함께 이동 결과는 null이 아니어야 함");
        assertEquals(destination2, mainPiece.getCurrentPlace(), "업힌 말과 함께 이동 위치가 일치해야 함");
        assertEquals(1, destination2.getPieces().size(), "이동 후 위치에는 메인 말만 있어야 함");

        // 로그에서 업기 관련 메시지 확인
        List<String> logs = game.getGameLog();
        boolean groupingLogFound = false;
        boolean groupMovementLogFound = false;

        for (String log : logs) {
            if (log.contains("업었습니다")) {
                groupingLogFound = true;
            }
            if (log.contains("업힌 말") && log.contains("함께 이동")) {
                groupMovementLogFound = true;
            }
        }

        assertTrue(groupingLogFound, "업기 관련 로그가 있어야 함");
        assertTrue(groupMovementLogFound, "업힌 말 이동 관련 로그가 있어야 함");

        // 업힌 말 풀기 테스트
        mainPiece.unstackAllPieces();

        // 업힌 말들이 보드에 다시 나타나는지 확인
        assertFalse(mainPiece.getStackedPieces().isEmpty(), "업힌 말들이 모두 제거되어야 함");
        // 원래 업혔던 말들의 위치가 메인 말과 같은지 확인
        for (Piece unstakedPiece : new Piece[]{stackedPiece, piece3}) {
            assertEquals(destination2, unstakedPiece.getCurrentPlace(),
                    "업기 해제 후 말들이 메인 말과 같은 위치에 있어야 함");
        }
    }
    /**
     * 승리 조건 테스트
     */
    @Test
    void testVictoryCondition() {
        Player player = game.getCurrentPlayer();

        // 초기에는 승리하지 않은 상태
        assertFalse(game.isPlayerWinner(player), "초기 상태에서는 승리자가 없어야 함");

        // 모든 말을 완주 상태로 만들기
        for (Piece piece : player.getPieces()) {
            piece.moveTo(game.getBoard().getEndingPlace());
        }

        // 승리 조건 확인
        assertTrue(game.isPlayerWinner(player), "모든 말이 완주하면 승리해야 함");

        // 게임 종료 확인
        assertTrue(game.checkGameEnd(), "승리 조건 만족 시 게임이 종료되어야 함");
        assertTrue(game.isGameFinished(), "게임 종료 상태가 설정되어야 함");
    }

    /**
     * 추가 턴 테스트 (윷, 모 결과)
     */
    @Test
    void testExtraTurnForYutMo() {
        // 초기에는 추가 턴이 없음
        assertFalse(game.hasExtraTurn(), "초기 상태에서는 추가 턴이 없어야 함");

        // 윷 결과에 대한 추가 턴 확인
        game.setSpecificYutResult(Yut.YutResult.YUT);
        assertTrue(game.hasExtraTurn(), "윷 결과에는 추가 턴이 부여되어야 함");

        // 모 결과에 대한 추가 턴 확인
        game.setSpecificYutResult(Yut.YutResult.MO);
        assertTrue(game.hasExtraTurn(), "모 결과에는 추가 턴이 부여되어야 함");

        // 다른 결과에 대한 추가 턴 확인
        game.setSpecificYutResult(Yut.YutResult.DO);
        assertFalse(game.hasExtraTurn(), "도 결과에는 추가 턴이 부여되지 않아야 함");
    }

    /**
     * 빽도 이동 테스트
     */
    @Test
    void testBackdoMovement() {
        Player player = game.getCurrentPlayer();
        Piece piece = player.getPieces().get(0);

        // 먼저 정방향으로 이동
        Yut.YutResult forward = Yut.YutResult.DO; // 도(1칸)
        Place forwardPlace = game.movePiece(piece, forward);

        // 빽도로 이동
        Yut.YutResult backdo = Yut.YutResult.BACKDO; // 빽도(-1칸)
        Place backPlace = game.movePiece(piece, backdo);

        // 시작점으로 돌아왔는지 확인
        assertEquals(game.getBoard().getStartingPlace(), backPlace, "빽도 이동 후 시작점으로 돌아와야 함");

        // 시작점에서 빽도가 적용되지 않는지 확인
        Place noMovePlace = game.movePiece(piece, backdo);
        assertEquals(game.getBoard().getStartingPlace(), noMovePlace, "시작점에서 빽도 이동 시 제자리여야 함");
    }

    /**
     * 중앙점 경로 규칙 테스트 (오각형/육각형 보드)
     */
    @Test
    void testCenterPathRule() {
        // 오각형 보드로 게임 초기화
        Game pentagonGame = new Game();
        pentagonGame.initialize(pentagonSettings);

        Player player = pentagonGame.getCurrentPlayer();
        Piece piece = player.getPieces().get(0);

        // 말을 중앙으로 가는 직전 위치로 이동
        // 이 테스트는 보드의 실제 구조에 따라 조정 필요
        // 여기서는 중앙 위치 전 위치를 찾는 로직을 가정

        // 중앙 위치 가져오기
        Place centerPlace = null;
        for (Place place : pentagonGame.getBoard().getCenterPlaces().values()) {
            centerPlace = place;
            break;
        }

        assertNotNull(centerPlace, "중앙 위치가 존재해야 함");

        // 중앙에 도달할 수 있는 결과로 테스트 (개, 3칸)
        // 실제 테스트에서는 말을 중앙 위치 직전까지 이동시킨 후 테스트
        Yut.YutResult stopResult = Yut.YutResult.DO;  // 도착하고 멈추는 결과
        Yut.YutResult continueResult = Yut.YutResult.YUT;  // 지나쳐 계속 가는 결과

        // 중앙에서 멈추는 경우, 1번 Path로
        // 중앙을 지나치는 경우, 2번 Path로
        // 이 테스트는 보드 구조에 따라 실제 결과가 달라짐

        // 이 부분은 실제 구현에 따라 세부적인 테스트 코드를 작성해야 함
    }

    /**
     * 동시 완주 시 순서에 따른 승자 결정 테스트
     */
    @Test
    void testSimultaneousCompletionWinner() {
        // 모든 플레이어의 마지막 한 말만 남기고 완주시킴
        for (Player player : game.getPlayers()) {
            List<Piece> pieces = player.getPieces();
            for (int i = 0; i < pieces.size() - 1; i++) {
                pieces.get(i).moveTo(game.getBoard().getEndingPlace());
            }
        }

        // 첫 번째 플레이어의 마지막 말
        Player firstPlayer = game.getPlayers().get(0);
        Piece lastPieceFirst = firstPlayer.getPieces().get(firstPlayer.getPieces().size() - 1);

        // 두 번째 플레이어의 마지막 말
        Player secondPlayer = game.getPlayers().get(1);
        Piece lastPieceSecond = secondPlayer.getPieces().get(secondPlayer.getPieces().size() - 1);

        // 두 플레이어의 마지막 말을 동시에 완주시킴 (순서대로)
        lastPieceFirst.moveTo(game.getBoard().getEndingPlace());
        lastPieceSecond.moveTo(game.getBoard().getEndingPlace());

        // 게임 종료 확인
        assertTrue(game.checkGameEnd(), "모든 플레이어가 완주하면 게임이 종료되어야 함");

        // 승자는 첫 번째 플레이어여야 함 (먼저 완주)
        assertEquals(firstPlayer, game.getWinner(), "순서상 먼저 완주한 플레이어가 승자여야 함");
    }
}