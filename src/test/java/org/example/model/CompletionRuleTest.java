package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CompletionRuleTest {

    private Game game;
    private Player player;
    private Piece piece;
    private Board board;

    @BeforeEach
    void setUp() {
        // 게임 설정 초기화
        GameSettings settings = new GameSettings();
        settings.setPlayerCount(2);
        settings.setPiecePerPlayer(2);
        settings.setBoardType(Board.BoardType.SQUARE);

        // 게임 객체 초기화
        game = new Game();
        game.initialize(settings);

        // 테스트용 플레이어와 말 설정
        player = game.getPlayers().get(0);
        piece = player.getPieces().get(0);
        board = game.getBoard();
    }

    /**
     * E 위치에 도달했을 때 말이 완주되지 않아야 함을 테스트
     */
    @Test
    void testPieceNotCompletedAtE() {
        // E 위치 가져오기
        Place ePlace = null;
        for (Place place : board.getAllPlaces().values()) {
            if ("E".equals(place.getId())) {
                ePlace = place;
                break;
            }
        }
        assertNotNull(ePlace, "E 위치가 존재해야 함");

        // 말을 E 위치로 이동
        piece.moveTo(ePlace);

        // E 위치에 있는지 확인
        assertEquals(ePlace, piece.getCurrentPlace(), "말이 E 위치에 있어야 함");

        // 완주로 인정되지 않아야 함
        assertFalse(piece.isCompleted(), "E 위치에 도달했을 때 말이 완주되지 않아야 함");
        assertFalse(player.isAllPiecesCompleted(), "플레이어의 모든 말이 완주되지 않아야 함");
        assertFalse(game.isPlayerWinner(player), "플레이어가 승리자로 인정되지 않아야 함");
    }

    /**
     * FE 위치에 도달했을 때 말이 완주되어야 함을 테스트
     */
    @Test
    void testPieceCompletedAtFE() {
        // FE 위치 가져오기 (실제 도착점)
        Place fePlace = board.getEndingPlace();
        assertNotNull(fePlace, "FE 위치가 존재해야 함");
        assertEquals("FE", fePlace.getId(), "도착점의 ID는 FE여야 함");

        // 말을 FE 위치로 이동
        piece.moveTo(fePlace);

        // FE 위치에 있는지 확인
        assertEquals(fePlace, piece.getCurrentPlace(), "말이 FE 위치에 있어야 함");

        // 완주로 인정되어야 함
        assertTrue(piece.isCompleted(), "FE 위치에 도달했을 때 말이 완주되어야 함");

        // 다른 말은 아직 완주하지 않았으므로 플레이어는 승리하지 않아야 함
        assertFalse(player.isAllPiecesCompleted(), "한 개의 말만 완주한 상태");

        // 다른 말도 FE로 이동시켜 완주 처리
        Piece otherPiece = player.getPieces().get(1);
        otherPiece.moveTo(fePlace);

        // 이제 플레이어가 승리해야 함
        assertTrue(player.isAllPiecesCompleted(), "모든 말이 완주했으므로 플레이어가 승리해야 함");
        assertTrue(game.isPlayerWinner(player), "플레이어가 승리자로 인정되어야 함");
    }

    /**
     * E 위치에서 FE 위치로 이동하는 경우 테스트
     */
    @Test
    void testMoveFromEToFE() {
        // E 위치 가져오기
        Place ePlace = null;
        for (Place place : board.getAllPlaces().values()) {
            if ("E".equals(place.getId())) {
                ePlace = place;
                break;
            }
        }
        assertNotNull(ePlace, "E 위치가 존재해야 함");

        // 말을 E 위치로 이동
        piece.moveTo(ePlace);
        assertFalse(piece.isCompleted(), "E 위치에서는 완주로 인정되지 않아야 함");

        // 윷 던지기 결과를 도(1칸)로 설정
        Yut.YutResult result = Yut.YutResult.DO;

        // E에서 FE로 이동
        Place destination = game.movePiece(piece, result);

        // 최종 도착지가 FE여야 함
        assertEquals("FE", destination.getId(), "E에서 이동한 최종 도착지는 FE여야 함");
        assertTrue(piece.isCompleted(), "FE 위치에 도달한 후 말이 완주되어야 함");
    }

    /**
     * 특별 경로를 통해 E를 거치지 않고 FE로 바로 가는 경우 테스트
     */
    @Test
    void testDirectPathToFE() {
        // 중앙점(C_2)에서 FE로 가는 경로 테스트
        Place centerPlace = board.getPlaceById("C_2");
        assertNotNull(centerPlace, "중앙점(C_2)이 존재해야 함");

        // 말을 중앙점으로 이동
        piece.moveTo(centerPlace);

        // 충분한 이동 거리를 가진 윷 결과 (모: 5칸)로 설정
        Yut.YutResult result = Yut.YutResult.MO;

        // 이동 실행
        Place destination = game.movePiece(piece, result);

        // 목적지가 FE여야 함
        assertEquals(board.getEndingPlace(), destination, "충분한 이동으로 FE에 도달해야 함");
        assertTrue(piece.isCompleted(), "FE 위치에 도달한 후 말이 완주되어야 함");
    }

    /**
     * E 위치를 넘어가는 경우 테스트 (예: E 이전 위치에서 이동 거리가 충분히 클 때)
     */
    @Test
    void testOvershootEToFE() {
        // E 바로 이전 위치 (19) 찾기
        Place place19 = board.getPlaceById("19");
        assertNotNull(place19, "19 위치가 존재해야 함");

        // 말을 19 위치로 이동
        piece.moveTo(place19);

        // 윷 던지기 결과를 개(2칸)로 설정 - E를 지나 FE로 이동해야 함
        Yut.YutResult result = Yut.YutResult.GAE;

        // 이동 실행
        Place destination = game.movePiece(piece, result);

        // 목적지가 FE여야 함
        assertEquals("FE", destination.getId(), "E를 넘어 FE에 도달해야 함");
        assertTrue(piece.isCompleted(), "FE 위치에 도달한 후 말이 완주되어야 함");
    }
}