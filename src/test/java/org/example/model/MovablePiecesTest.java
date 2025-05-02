package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * 출발 가능한 말에 대한 테스트 케이스
 */
public class MovablePiecesTest {

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
     * 테스트 1: 말이 잡혔을 때 이동 가능한 말의 수
     * - 초기 상태에서 말을 보드 위로 이동
     * - 말이 잡혔을 때 이동 가능한 말 목록 확인
     */
    @Test
    void testMovablePiecesAfterCapture() {
        System.out.println("\n===== 테스트 1: 말이 잡혔을 때 이동 가능한 말의 수 =====");

        // 초기 상태 확인 (모든 말이 이동 가능해야 함)
        List<Piece> initialMovablePieces = player.getMovablePieces();
        System.out.println("초기 이동 가능한 말 수: " + initialMovablePieces.size());
        for (Piece p : initialMovablePieces) {
            System.out.println("  - " + p.getId() + ", 현재 위치: " +
                    (p.getCurrentPlace() != null ? p.getCurrentPlace().getId() : "없음"));
        }

        assertEquals(3, initialMovablePieces.size(), "초기 상태에서 3개의 말이 모두 이동 가능해야 함");

        // piece1을 먼저 보드 위로 이동
        Place somePlace = game.getBoard().getPlaceById("5"); // 임의의 위치
        System.out.println("\n말 이동: " + piece1.getId() + " -> 위치 " + somePlace.getId());
        piece1.moveTo(somePlace);

        // piece1이 보드 위에 있는지 확인
        System.out.println("이동 후 " + piece1.getId() + "의 위치: " +
                (piece1.getCurrentPlace() != null ? piece1.getCurrentPlace().getId() : "없음"));
        assertNotNull(piece1.getCurrentPlace(), "piece1이 보드 위에 있어야 함");

        // 이동 가능한 말 목록 확인 (여전히 3개여야 함)
        List<Piece> movablePiecesBeforeCapture = player.getMovablePieces();
        System.out.println("\n이동 전 이동 가능한 말 수: " + movablePiecesBeforeCapture.size());
        for (Piece p : movablePiecesBeforeCapture) {
            System.out.println("  - " + p.getId() + ", 현재 위치: " +
                    (p.getCurrentPlace() != null ? p.getCurrentPlace().getId() : "없음"));
        }
        assertEquals(3, movablePiecesBeforeCapture.size(), "모든 말이 아직 이동 가능해야 함");

        // piece1이 잡힌 상황을 시뮬레이션 (시작점으로 되돌림)
        System.out.println("\n말이 잡힘 시뮬레이션: " + piece1.getId() + " -> 시작점");
        piece1.moveTo(game.getBoard().getStartingPlace());

        // 이동 가능한 말 목록 확인 (여전히 3개여야 함 - 잡혀도 이동 가능)
        List<Piece> movablePiecesAfterCapture = player.getMovablePieces();
        System.out.println("\n말이 잡힌 후 이동 가능한 말 수: " + movablePiecesAfterCapture.size());
        for (Piece p : movablePiecesAfterCapture) {
            System.out.println("  - " + p.getId() + ", 현재 위치: " +
                    (p.getCurrentPlace() != null ? p.getCurrentPlace().getId() : "없음"));
        }
        assertEquals(3, movablePiecesAfterCapture.size(), "말이 잡힌 후에도 3개의 말이 모두 이동 가능해야 함");
        assertTrue(movablePiecesAfterCapture.contains(piece1), "잡힌 말(piece1)도 이동 가능해야 함");

        // piece1이 올바르게 시작점에 있는지 확인
        System.out.println("\n잡힌 말 " + piece1.getId() + "의 현재 위치: " +
                (piece1.getCurrentPlace() != null ? piece1.getCurrentPlace().getId() : "없음"));
        assertEquals(game.getBoard().getStartingPlace(), piece1.getCurrentPlace(), "잡힌 말은 시작점에 있어야 함");

        System.out.println("\n===== 테스트 1 종료 =====\n");
    }

    /**
     * 테스트 2: 말이 완주했을 때 이동 가능한 말의 수
     * - 초기 상태에서 말을 완주 처리
     * - 완주한 말이 이동 가능한 말 목록에서 제외되는지 확인
     */
    @Test
    void testMovablePiecesAfterCompletion() {
        System.out.println("\n===== 테스트 2: The number of pieces that can move after a piece reached the destination =====");

        // 초기 상태 확인 (모든 말이 이동 가능해야 함)
        List<Piece> initialMovablePieces = player.getMovablePieces();
        System.out.println("초기 이동 가능한 말 수: " + initialMovablePieces.size());
        for (Piece p : initialMovablePieces) {
            System.out.println("  - " + p.getId() + ", 완주 상태: " + p.isCompleted());
        }
        assertEquals(3, initialMovablePieces.size(), "초기 상태에서 3개의 말이 모두 이동 가능해야 함");

        // piece1을 완주 상태로 만듦 (FE 위치로 이동)
        Place finalDestination = game.getBoard().getEndingPlace(); // FE 위치
        System.out.println("\n말 완주 처리: " + piece1.getId() + " -> " + finalDestination.getId());
        piece1.moveTo(finalDestination);

        // piece1이 완주했는지 확인
        System.out.println(piece1.getId() + " 완주 상태: " + piece1.isCompleted());
        System.out.println(piece1.getId() + " 현재 위치: " +
                (piece1.getCurrentPlace() != null ? piece1.getCurrentPlace().getId() : "없음"));
        assertTrue(piece1.isCompleted(), "FE 위치에 도달한 말은 완주 상태여야 함");

        // 이동 가능한 말 목록 확인 (2개만 남아야 함)
        List<Piece> movablePiecesAfterCompletion = player.getMovablePieces();
        System.out.println("\n한 말 완주 후 이동 가능한 말 수: " + movablePiecesAfterCompletion.size());
        for (Piece p : movablePiecesAfterCompletion) {
            System.out.println("  - " + p.getId() + ", 완주 상태: " + p.isCompleted());
        }
        assertEquals(2, movablePiecesAfterCompletion.size(), "완주한 말을 제외하고 2개의 말만 이동 가능해야 함");
        assertFalse(movablePiecesAfterCompletion.contains(piece1), "완주한 말(piece1)은 이동 가능한 말 목록에 포함되지 않아야 함");
        assertTrue(movablePiecesAfterCompletion.contains(piece2), "완주하지 않은 말(piece2)은 여전히 이동 가능해야 함");
        assertTrue(movablePiecesAfterCompletion.contains(piece3), "완주하지 않은 말(piece3)은 여전히 이동 가능해야 함");

        // 추가로 piece2도 완주 처리
        System.out.println("\n두 번째 말 완주 처리: " + piece2.getId() + " -> " + finalDestination.getId());
        piece2.moveTo(finalDestination);
        System.out.println(piece2.getId() + " 완주 상태: " + piece2.isCompleted());

        // 이동 가능한 말 목록 다시 확인 (1개만 남아야 함)
        List<Piece> movablePiecesAfterSecondCompletion = player.getMovablePieces();
        System.out.println("\n두 말 완주 후 이동 가능한 말 수: " + movablePiecesAfterSecondCompletion.size());
        for (Piece p : movablePiecesAfterSecondCompletion) {
            System.out.println("  - " + p.getId() + ", 완주 상태: " + p.isCompleted());
        }
        assertEquals(1, movablePiecesAfterSecondCompletion.size(), "두 개의 말이 완주한 후에는 1개의 말만 이동 가능해야 함");
        assertTrue(movablePiecesAfterSecondCompletion.contains(piece3), "완주하지 않은 말(piece3)만 이동 가능해야 함");

        // 마지막 말도 완주 처리
        System.out.println("\n마지막 말 완주 처리: " + piece3.getId() + " -> " + finalDestination.getId());
        piece3.moveTo(finalDestination);
        System.out.println(piece3.getId() + " 완주 상태: " + piece3.isCompleted());

        // 이동 가능한 말 목록 다시 확인 (0개여야 함)
        List<Piece> movablePiecesAfterAllCompletion = player.getMovablePieces();
        System.out.println("\n모든 말 완주 후 이동 가능한 말 수: " + movablePiecesAfterAllCompletion.size());
        assertEquals(0, movablePiecesAfterAllCompletion.size(), "모든 말이 완주한 후에는 이동 가능한 말이 없어야 함");

        // 플레이어가 승리 상태인지 확인
        System.out.println("\n모든 말 완주 여부: " + player.isAllPiecesCompleted());
        System.out.println("플레이어 승리 상태: " + game.isPlayerWinner(player));
        assertTrue(player.isAllPiecesCompleted(), "모든 말이 완주했으므로 플레이어는 승리 상태여야 함");
        assertTrue(game.isPlayerWinner(player), "모든 말이 완주한 플레이어는 승리자여야 함");

        System.out.println("\n===== 테스트 2 종료 =====\n");
    }

    /**
     * 테스트 3: 말이 보드에 없을 때 (시작 상태)의 이동 가능 여부
     */
    @Test
    void testInitialMovablePieces() {
        System.out.println("\n===== 테스트 3: 말이 보드에 없을 때 이동 가능 여부 =====");

        // 모든 말이 아직 보드에 놓이지 않은 초기 상태
        System.out.println("초기 말 상태 확인:");
        for (Piece piece : player.getPieces()) {
            System.out.println("  - " + piece.getId() + ", 현재 위치: " +
                    (piece.getCurrentPlace() != null ? piece.getCurrentPlace().getId() : "없음"));
            assertNull(piece.getCurrentPlace(), "초기 상태에서 말은 보드 위에 없어야 함");
        }

        // 이동 가능한 말 목록 확인 (모든 말이 이동 가능)
        List<Piece> movablePieces = player.getMovablePieces();
        System.out.println("\n초기 상태에서 이동 가능한 말 수: " + movablePieces.size());
        for (Piece p : movablePieces) {
            System.out.println("  - " + p.getId());
        }
        assertEquals(3, movablePieces.size(), "초기 상태에서 모든 말이 이동 가능해야 함");

        // 특정 윷 결과에 대한 이동 가능한 말 확인 (Game.getMovablePieces 메서드 테스트)
        Yut.YutResult result = Yut.YutResult.DO; // 도(1칸)
        List<Piece> movablePiecesForResult = game.getMovablePieces(player, result);
        System.out.println("\n도(1칸) 결과에 대한 이동 가능한 말 수: " + movablePiecesForResult.size());
        for (Piece p : movablePiecesForResult) {
            System.out.println("  - " + p.getId());
        }
        assertEquals(3, movablePiecesForResult.size(), "특정 윷 결과에 대해서도 모든 말이 이동 가능해야 함");

        System.out.println("\n===== 테스트 3 종료 =====\n");
    }

    /**
     * 테스트 4: 윷 결과에 따른 이동 가능한 말 목록
     */
    @Test
    void testMovablePiecesForYutResults() {
        System.out.println("\n===== 테스트 4: 윷 결과에 따른 이동 가능한 말 목록 =====");

        // 모든 윷 결과에 대해 테스트
        Yut.YutResult[] results = Yut.YutResult.values();

        System.out.println("모든 윷 결과에 대한 이동 가능한 말 확인:");
        for (Yut.YutResult result : results) {
            // 각 윷 결과에 대한 이동 가능한 말 목록 확인
            List<Piece> movablePieces = game.getMovablePieces(player, result);
            System.out.println("\n" + result.getName() + "(" + result.getMoveCount() + "칸) 결과에 대한 이동 가능한 말 수: " + movablePieces.size());
            for (Piece p : movablePieces) {
                System.out.println("  - " + p.getId() + ", 현재 위치: " +
                        (p.getCurrentPlace() != null ? p.getCurrentPlace().getId() : "없음"));
            }

            assertEquals(3, movablePieces.size(),
                    result.getName() + " 결과에 대해 모든 말이 이동 가능해야 함");

            // 빽도 결과일 때 추가 검증 (보드 위에 없는 말은 빽도로 이동 불가능할 수 있음)
            if (result == Yut.YutResult.BACKDO) {
                System.out.println("\n빽도 특수 케이스 테스트: piece1을 보드 위로 이동");
                // piece1을 보드 위로 이동
                Place somePlace = game.getBoard().getPlaceById("5");
                piece1.moveTo(somePlace);
                System.out.println(piece1.getId() + " 이동 후 위치: " +
                        (piece1.getCurrentPlace() != null ? piece1.getCurrentPlace().getId() : "없음"));

                // 빽도에 대한 이동 가능한 말 목록 재확인
                movablePieces = game.getMovablePieces(player, result);

                System.out.println("\n빽도 결과에 대한 이동 가능한 말 수 (piece1 이동 후): " + movablePieces.size());
                for (Piece p : movablePieces) {
                    System.out.println("  - 이동 가능한 말: " + p.getId() +
                            ", 현재 위치: " + (p.getCurrentPlace() != null ? p.getCurrentPlace().getId() : "없음"));
                }

                // 빽도 테스트 후 말 위치 복원
                System.out.println("\n테스트 후 말 위치 복원");
                piece1.moveTo(null);
            }
        }

        System.out.println("\n===== 테스트 4 종료 =====\n");
    }
}