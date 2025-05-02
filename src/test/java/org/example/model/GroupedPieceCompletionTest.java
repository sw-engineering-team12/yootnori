package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 업힌 말의 완주 처리 테스트 케이스
 */
public class GroupedPieceCompletionTest {

    private Game game;
    private Player player;
    private Piece piece1;
    private Piece piece2;
    private Piece piece3;

    @BeforeEach
    void setUp() {
        // 게임 설정 초기화
        GameSettings settings = new GameSettings();
        settings.setPlayerCount(2);
        settings.setPiecePerPlayer(3); // 테스트를 위해 3개의 말 사용
        settings.setBoardType(Board.BoardType.SQUARE);

        // 게임 객체 초기화
        game = new Game();
        game.initialize(settings);

        // 테스트용 플레이어와 말 설정
        player = game.getPlayers().get(0);

        // 3개의 말 참조 저장
        piece1 = player.getPieces().get(0);
        piece2 = player.getPieces().get(1);
        piece3 = player.getPieces().get(2);

        System.out.println("\n===== 테스트 초기화 =====");
        System.out.println("플레이어: " + player.getName());
        System.out.println("말 목록:");
        for (Piece p : player.getPieces()) {
            System.out.println("  - " + p.getId());
        }
        System.out.println("=======================\n");
    }

    /**
     * 업힌 말 완주 처리 테스트
     * - 3번 말이 1번, 2번 말을 업음
     * - 3번 말이 완주했을 때 1번, 2번 말도 완주 상태가 되는지 확인
     */
    @Test
    void testGroupedPieceCompletion() {
        System.out.println("===== 업힌 말 완주 처리 테스트 =====");

        // 1. 세 말을 먼저 보드 위의 같은 위치로 이동
        Place intermediatePlace = game.getBoard().getPlaceById("10");
        System.out.println("세 말을 " + intermediatePlace.getId() + " 위치로 이동");

        piece1.moveTo(intermediatePlace);
        piece2.moveTo(intermediatePlace);
        piece3.moveTo(intermediatePlace);

        // 세 말이 같은 위치에 있는지 확인
        System.out.println("\n세 말의 위치 확인:");
        System.out.println("piece1 위치: " + (piece1.getCurrentPlace() != null ? piece1.getCurrentPlace().getId() : "없음"));
        System.out.println("piece2 위치: " + (piece2.getCurrentPlace() != null ? piece2.getCurrentPlace().getId() : "없음"));
        System.out.println("piece3 위치: " + (piece3.getCurrentPlace() != null ? piece3.getCurrentPlace().getId() : "없음"));

        assertEquals(intermediatePlace, piece1.getCurrentPlace(), "piece1이 중간 위치에 있어야 함");
        assertEquals(intermediatePlace, piece2.getCurrentPlace(), "piece2가 중간 위치에 있어야 함");
        assertEquals(intermediatePlace, piece3.getCurrentPlace(), "piece3이 중간 위치에 있어야 함");

        // 2. 3번 말이 1번, 2번 말을 업음
        System.out.println("\n3번 말이 1번, 2번 말을 업음");
        boolean stacked1 = piece3.stackPiece(piece1);
        boolean stacked2 = piece3.stackPiece(piece2);

        System.out.println("1번 말 업기 성공 여부: " + stacked1);
        System.out.println("2번 말 업기 성공 여부: " + stacked2);

        assertTrue(stacked1, "3번 말이 1번 말을 업는 데 성공해야 함");
        assertTrue(stacked2, "3번 말이 2번 말을 업는 데 성공해야 함");

        // 3. 업힌 상태 확인
        System.out.println("\n업힌 상태 확인:");
        System.out.println("3번 말에 업힌 말 수: " + piece3.getStackedPieces().size());
        for (Piece p : piece3.getStackedPieces()) {
            System.out.println("  - 업힌 말: " + p.getId());
        }

        assertEquals(2, piece3.getStackedPieces().size(), "3번 말에 2개의 말이 업혀 있어야 함");
        assertTrue(piece3.getStackedPieces().contains(piece1), "1번 말이 3번 말에 업혀 있어야 함");
        assertTrue(piece3.getStackedPieces().contains(piece2), "2번 말이 3번 말에 업혀 있어야 함");

        // 업힌 말들의 위치가 null인지 확인 (보드에서 제거됨)
        System.out.println("\n업힌 후 말 위치 확인:");
        System.out.println("piece1 위치: " + (piece1.getCurrentPlace() != null ? piece1.getCurrentPlace().getId() : "없음"));
        System.out.println("piece2 위치: " + (piece2.getCurrentPlace() != null ? piece2.getCurrentPlace().getId() : "없음"));
        System.out.println("piece3 위치: " + (piece3.getCurrentPlace() != null ? piece3.getCurrentPlace().getId() : "없음"));

        assertNull(piece1.getCurrentPlace(), "업힌 1번 말은 보드 위치가 없어야 함");
        assertNull(piece2.getCurrentPlace(), "업힌 2번 말은 보드 위치가 없어야 함");
        assertNotNull(piece3.getCurrentPlace(), "3번 말은 보드 위에 있어야 함");

        // 4. 말의 완주 상태 확인 (모두 완주하지 않은 상태여야 함)
        System.out.println("\n업힌 후 완주 상태 확인:");
        System.out.println("piece1 완주 상태: " + piece1.isCompleted());
        System.out.println("piece2 완주 상태: " + piece2.isCompleted());
        System.out.println("piece3 완주 상태: " + piece3.isCompleted());

        assertFalse(piece1.isCompleted(), "1번 말은 아직 완주하지 않아야 함");
        assertFalse(piece2.isCompleted(), "2번 말은 아직 완주하지 않아야 함");
        assertFalse(piece3.isCompleted(), "3번 말은 아직 완주하지 않아야 함");

        // 5. 3번 말을 완주 시킴
        Place finalDestination = game.getBoard().getEndingPlace(); // FE 위치
        System.out.println("\n3번 말을 " + finalDestination.getId() + " 위치로 이동하여 완주");
        piece3.moveTo(finalDestination);

        // 6. 완주 후 상태 확인
        System.out.println("\n완주 후 위치 및 상태 확인:");
        System.out.println("piece3 위치: " + (piece3.getCurrentPlace() != null ? piece3.getCurrentPlace().getId() : "없음"));
        System.out.println("piece3 완주 상태: " + piece3.isCompleted());

        assertEquals(finalDestination, piece3.getCurrentPlace(), "3번 말이 도착점에 있어야 함");
        assertTrue(piece3.isCompleted(), "3번 말은 완주 상태여야 함");

        // 업힌 말들은 여전히 3번 말에 업혀 있어야 함
        System.out.println("\n완주 후 업힌 말 상태 확인:");
        System.out.println("3번 말에 업힌 말 수: " + piece3.getStackedPieces().size());
        for (Piece p : piece3.getStackedPieces()) {
            System.out.println("  - 업힌 말: " + p.getId() + ", 완주 상태: " + p.isCompleted());
        }

        assertEquals(2, piece3.getStackedPieces().size(), "완주 후에도 3번 말에 2개의 말이 업혀 있어야 함");

        // 중요: 업힌 말도 완주 상태로 처리되어야 함
        System.out.println("\n업힌 말들의 완주 상태 확인:");
        System.out.println("piece1 완주 상태: " + piece1.isCompleted());
        System.out.println("piece2 완주 상태: " + piece2.isCompleted());

        assertTrue(piece1.isCompleted(), "업힌 1번 말도 완주 상태여야 함");
        assertTrue(piece2.isCompleted(), "업힌 2번 말도 완주 상태여야 함");

        // 플레이어의 모든 말이 완주 상태인지 확인
        System.out.println("\n플레이어 완주 상태 확인:");
        System.out.println("모든 말 완주 여부: " + player.isAllPiecesCompleted());
        System.out.println("플레이어 승리 여부: " + game.isPlayerWinner(player));

        assertTrue(player.isAllPiecesCompleted(), "플레이어의 모든 말이 완주 상태여야 함");
        assertTrue(game.isPlayerWinner(player), "플레이어가 승리자여야 함");

        // 이동 가능한 말이 없어야 함
        System.out.println("\n이동 가능한 말 수: " + player.getMovablePieces().size());
        assertEquals(0, player.getMovablePieces().size(), "모든 말이 완주했으므로 이동 가능한 말이 없어야 함");

        System.out.println("\n===== 테스트 종료 =====");
    }

    /**
     * 조건을 제안하여, 업힌 말의 완주 상태를 추가로 검증하는 테스트
     */
    @Test
    void testGroupedPieceCompletionEdgeCases() {
        System.out.println("===== 업힌 말 완주 처리 추가 테스트 =====");

        // 중간 위치로 말들 이동
        Place intermediatePlace = game.getBoard().getPlaceById("10");
        piece1.moveTo(intermediatePlace);
        piece2.moveTo(intermediatePlace);
        piece3.moveTo(intermediatePlace);

        // 3번 말이 1번, 2번 말을 업음
        piece3.stackPiece(piece1);
        piece3.stackPiece(piece2);

        System.out.println("3번 말이 1번, 2번 말을 업음");
        System.out.println("업힌 말 수: " + piece3.getStackedPieces().size());

        // **테스트 1: unstackAllPieces 후 완주 시 모든 말의 완주 상태 확인**
        System.out.println("\n테스트 1: 그룹 해제 후 완주");

        // 그룹 해제
        System.out.println("3번 말에서 모든 말 그룹 해제");
        piece3.unstackAllPieces();

        System.out.println("그룹 해제 후 위치 확인:");
        System.out.println("piece1 위치: " + (piece1.getCurrentPlace() != null ? piece1.getCurrentPlace().getId() : "없음"));
        System.out.println("piece2 위치: " + (piece2.getCurrentPlace() != null ? piece2.getCurrentPlace().getId() : "없음"));
        System.out.println("piece3 위치: " + (piece3.getCurrentPlace() != null ? piece3.getCurrentPlace().getId() : "없음"));

        // 모든 말을 완주 처리
        Place finalDestination = game.getBoard().getEndingPlace();
        System.out.println("\n모든 말을 개별적으로 완주 처리");

        piece1.moveTo(finalDestination);
        piece2.moveTo(finalDestination);
        piece3.moveTo(finalDestination);

        System.out.println("완주 후 상태 확인:");
        System.out.println("piece1 완주 상태: " + piece1.isCompleted());
        System.out.println("piece2 완주 상태: " + piece2.isCompleted());
        System.out.println("piece3 완주 상태: " + piece3.isCompleted());

        assertTrue(piece1.isCompleted(), "piece1은 완주 상태여야 함");
        assertTrue(piece2.isCompleted(), "piece2는 완주 상태여야 함");
        assertTrue(piece3.isCompleted(), "piece3은 완주 상태여야 함");

        // 모든 말 완주 상태 초기화
        System.out.println("\n상태 초기화");
        piece1.moveTo(null);
        piece2.moveTo(null);
        piece3.moveTo(null);

        // **테스트 2: 다른 순서로 말을 업고 완주했을 때 상태 확인**
        System.out.println("\n테스트 2: 다른 순서로 말 업기");

        // 중간 위치로 말들 이동
        piece1.moveTo(intermediatePlace);
        piece2.moveTo(intermediatePlace);
        piece3.moveTo(intermediatePlace);

        // 1번 말이 다른 말들을 업음
        piece1.stackPiece(piece2);
        piece1.stackPiece(piece3);

        System.out.println("1번 말이 2번, 3번 말을 업음");
        System.out.println("업힌 말 수: " + piece1.getStackedPieces().size());

        // 1번 말을 완주 처리
        System.out.println("\n1번 말을 완주 처리");
        piece1.moveTo(finalDestination);

        System.out.println("완주 후 상태 확인:");
        System.out.println("piece1 완주 상태: " + piece1.isCompleted());
        System.out.println("piece2 완주 상태: " + piece2.isCompleted());
        System.out.println("piece3 완주 상태: " + piece3.isCompleted());

        assertTrue(piece1.isCompleted(), "piece1은 완주 상태여야 함");
        assertTrue(piece2.isCompleted(), "업힌 piece2도 완주 상태여야 함");
        assertTrue(piece3.isCompleted(), "업힌 piece3도 완주 상태여야 함");

        // 플레이어 승리 상태 확인
        assertTrue(player.isAllPiecesCompleted(), "모든 말이 완주 상태이므로 플레이어는 승리해야 함");

        System.out.println("\n===== 추가 테스트 종료 =====");
    }
}