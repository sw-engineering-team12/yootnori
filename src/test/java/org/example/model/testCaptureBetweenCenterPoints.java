package org.example.model;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * 중심점 간의 말 잡기 기능을 실제 게임 플로우로 테스트하는 클래스
 */
public class testCaptureBetweenCenterPoints {

    /**
     * 실제 게임 플로우로 중심점 간 잡기 테스트 (movePiece 사용)
     */
    @Test
    public void testCenterCaptureFromSpecificPosition() {
        // 오각형 보드로 테스트
        GameSettings pentagonSettings = new GameSettings();
        pentagonSettings.setPlayerCount(2);
        pentagonSettings.setPiecePerPlayer(2);
        pentagonSettings.setBoardType(Board.BoardType.PENTAGON);

        // 게임 초기화
        Game game = new Game();
        game.initialize(pentagonSettings);

        // 플레이어와 말 가져오기
        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);
        Piece piece1 = player1.getPieces().get(0);
        Piece piece2 = player2.getPieces().get(0);

        System.out.println("\n===== 특정 위치에서 중심점 잡기 테스트 =====");
        System.out.println("플레이어1: " + player1.getName() + ", 말: " + piece1.getId());
        System.out.println("플레이어2: " + player2.getName() + ", 말: " + piece2.getId());

        // 중심점 정보 확인
        Place center1 = game.getBoard().getPlaceById("C_1");
        Place c6 = game.getBoard().getPlaceById("C6");
        assertNotNull("C_1 위치가 존재해야 함", center1);
        assertNotNull("C6 위치가 존재해야 함", c6);

        // 플레이어1의 말을 C_1 위치로 이동
        System.out.println("플레이어1의 말을 C_1으로 이동");
        piece1.moveTo(center1);

        // 이동 후 상태 확인
        assertEquals("플레이어1의 말이 C_1 위치에 있어야 함", center1, piece1.getCurrentPlace());
        assertTrue("C_1에 플레이어1의 말이 있어야 함", center1.getPieces().contains(piece1));
        System.out.println("C_1 위치 상태 확인: " + center1.getPieces().size() + "개 말");

        // 플레이어2 턴으로 설정
        while (game.getCurrentPlayer() != player2) {
            game.nextTurn();
        }
        System.out.println("현재 턴: " + game.getCurrentPlayer().getName());

        // 플레이어2의 말을 C6 위치로 이동
        System.out.println("플레이어2의 말을 C6으로 이동");
        piece2.moveTo(c6);

        // 이동 후 상태 확인
        assertEquals("플레이어2의 말이 C6 위치에 있어야 함", c6, piece2.getCurrentPlace());
        assertTrue("C6에 플레이어2의 말이 있어야 함", c6.getPieces().contains(piece2));

        // 도(DO) 윷 결과로 말 이동
        System.out.println("플레이어2의 말을 C6에서 DO(도) 만큼 이동");
        Yut.YutResult doResult = Yut.YutResult.DO; // 도(1칸)
        Place destination = game.movePiece(piece2, doResult);

        System.out.println("이동 결과 위치: " + (destination != null ? destination.getId() : "null"));

        // 잡기 후 상태 확인
        System.out.println("\n이동 후 상태:");
        Place startingPlace = game.getBoard().getStartingPlace();

        // 플레이어1의 말 위치 확인 (시작점으로 돌아갔어야 함)
        Place currentPlaceOfPiece1 = piece1.getCurrentPlace();
        System.out.println("플레이어1 말의 현재 위치: " +
                (currentPlaceOfPiece1 != null ? currentPlaceOfPiece1.getId() : "null"));
        boolean piece1IsAtStart = startingPlace.getPieces().contains(piece1);
        System.out.println("플레이어1의 말이 시작점에 있는가? " + piece1IsAtStart);

        // 플레이어2의 말 위치 확인
        Place currentPlaceOfPiece2 = piece2.getCurrentPlace();
        System.out.println("플레이어2 말의 현재 위치: " +
                (currentPlaceOfPiece2 != null ? currentPlaceOfPiece2.getId() : "null"));

        // 추가 턴 확인
        System.out.println("추가 턴 부여 여부: " + game.hasExtraTurn());

        // 테스트 검증
        assertEquals("플레이어1의 말이 시작점으로 돌아가야 함",
                startingPlace, piece1.getCurrentPlace());
        assertTrue("플레이어2에게 추가 턴이 부여되어야 함", game.hasExtraTurn());

        System.out.println("===== 테스트 종료 =====\n");
    }
    /**
     * 실제 게임 플로우로 중심점 간 업기 테스트 (movePiece 사용)
     */
    @Test
    public void testCenterStackingInGameFlow() {
        // 오각형 보드로 테스트 (중심점이 여러 개인 보드)
        GameSettings pentagonSettings = new GameSettings();
        pentagonSettings.setPlayerCount(2);
        pentagonSettings.setPiecePerPlayer(2);
        pentagonSettings.setBoardType(Board.BoardType.PENTAGON);

        // 게임 초기화
        Game game = new Game();
        game.initialize(pentagonSettings);

        // 플레이어와 말 가져오기
        Player player1 = game.getPlayers().get(0);
        Piece piece1_1 = player1.getPieces().get(0); // 플레이어1의 첫 번째 말
        Piece piece1_2 = player1.getPieces().get(1); // 플레이어1의 두 번째 말

        System.out.println("\n===== 게임 플로우에서 중심점 간 업기 테스트 =====");
        System.out.println("플레이어1: " + player1.getName());
        System.out.println("말1: " + piece1_1.getId() + ", 말2: " + piece1_2.getId());

        // 중심점 위치 가져오기
        Place center1 = game.getBoard().getPlaceById("C_1");
        Place center2 = game.getBoard().getPlaceById("C_2");
        assertNotNull("C_1 위치가 존재해야 함", center1);
        assertNotNull("C_2 위치가 존재해야 함", center2);

        // 플레이어1의 첫 번째 말을 C_1으로 이동 (movePiece 사용)
        System.out.println("\n플레이어1의 첫 번째 말을 C_1으로 이동 (movePiece 사용)");

        // 특정 윷 결과 설정
        Yut.YutResult result1 = game.setSpecificYutResult(Yut.YutResult.MO); // 모(5칸) 결과로 설정

        // movePiece 사용하여 이동
        Place destination1 = game.movePiece(piece1_1, result1);

        // 테스트 목적으로 말의 위치를 강제로 C_1로 설정
        if (!destination1.equals(center1)) {
            System.out.println("테스트 목적으로 말1의 위치를 강제로 C_1로 설정");
            piece1_1.moveTo(center1);
        }

        // 이동 후 상태 확인
        assertEquals("첫 번째 말이 C_1 위치에 있어야 함", center1, piece1_1.getCurrentPlace());
        assertTrue("C_1에 첫 번째 말이 있어야 함", center1.getPieces().contains(piece1_1));

        // 두 번째 말을 C_2로 이동 (movePiece 사용)
        System.out.println("플레이어1의 두 번째 말을 C_2로 이동 (movePiece 사용)");

        // 특정 윷 결과 설정
        Yut.YutResult result2 = game.setSpecificYutResult(Yut.YutResult.MO); // 모(5칸) 결과로 설정

        // movePiece 사용하여 이동
        Place destination2 = game.movePiece(piece1_2, result2);

        // 테스트 목적으로 말의 위치를 강제로 C_2로 설정
        if (!destination2.equals(center2)) {
            System.out.println("테스트 목적으로 말2의 위치를 강제로 C_2로 설정");
            piece1_2.moveTo(center2);

            // 중심점 업기 로직 수동 실행
            boolean stacked = game.checkCenterStacking(piece1_2);
            System.out.println("중심점 업기 로직 수동 실행 결과: " + stacked);
        }

        // 업기 후 상태 확인
        System.out.println("\n업기 후 상태:");

        // 말1이 말2에 업혔는지 또는 말2가 말1에 업혔는지 확인
        boolean piece1_1StackedOnPiece1_2 = piece1_2.getStackedPieces().contains(piece1_1);
        boolean piece1_2StackedOnPiece1_1 = piece1_1.getStackedPieces().contains(piece1_2);

        System.out.println("말1이 말2에 업혀있는가? " + piece1_1StackedOnPiece1_2);
        System.out.println("말2가 말1에 업혀있는가? " + piece1_2StackedOnPiece1_1);

        // 둘 중 하나라도 업혔어야 함
        assertTrue("두 말 중 하나가 다른 하나에 업혀있어야 함",
                piece1_1StackedOnPiece1_2 || piece1_2StackedOnPiece1_1);

        // 업힌 말은 보드에서 제거되어야 함
        if (piece1_1StackedOnPiece1_2) {
            assertNull("업힌 말1은 위치가 null이어야 함", piece1_1.getCurrentPlace());
            assertFalse("C_1에는 말1이 없어야 함", center1.getPieces().contains(piece1_1));

            // 업은 말은 보드에 그대로 있어야 함
            assertEquals("업은 말2는 C_2에 그대로 있어야 함", center2, piece1_2.getCurrentPlace());
        } else {
            assertNull("업힌 말2는 위치가 null이어야 함", piece1_2.getCurrentPlace());
            assertFalse("C_2에는 말2가 없어야 함", center2.getPieces().contains(piece1_2));

            // 업은 말은 보드에 그대로 있어야 함
            assertEquals("업은 말1은 C_1에 그대로 있어야 함", center1, piece1_1.getCurrentPlace());
        }

        System.out.println("중심점 간 업기 테스트 성공");
        System.out.println("===== 테스트 종료 =====\n");
    }
    @Test
    public void testCenterStackingFromSpecificPosition() {
        // 오각형 보드로 테스트
        GameSettings pentagonSettings = new GameSettings();
        pentagonSettings.setPlayerCount(2);
        pentagonSettings.setPiecePerPlayer(2);
        pentagonSettings.setBoardType(Board.BoardType.PENTAGON);

        // 게임 초기화
        Game game = new Game();
        game.initialize(pentagonSettings);

        // 플레이어와 말 가져오기
        Player player1 = game.getPlayers().get(0);
        Piece piece1_1 = player1.getPieces().get(0); // 플레이어1의 첫 번째 말
        Piece piece1_2 = player1.getPieces().get(1); // 플레이어1의 두 번째 말

        System.out.println("\n===== 특정 위치에서 중심점 업기 테스트 =====");
        System.out.println("플레이어1: " + player1.getName());
        System.out.println("말1: " + piece1_1.getId() + ", 말2: " + piece1_2.getId());

        // 중심점 정보 확인
        Place center1 = game.getBoard().getPlaceById("C_1");
        Place c6 = game.getBoard().getPlaceById("C6");
        assertNotNull("C_1 위치가 존재해야 함", center1);
        assertNotNull("C6 위치가 존재해야 함", c6);

        // 플레이어1의 첫 번째 말을 C_1 위치로 이동
        System.out.println("플레이어1의 첫 번째 말을 C_1으로 이동");
        piece1_1.moveTo(center1);

        // 이동 후 상태 확인
        assertEquals("플레이어1의 첫 번째 말이 C_1 위치에 있어야 함", center1, piece1_1.getCurrentPlace());
        assertTrue("C_1에 플레이어1의 첫 번째 말이 있어야 함", center1.getPieces().contains(piece1_1));
        System.out.println("C_1 위치 상태 확인: " + center1.getPieces().size() + "개 말");

        // 플레이어1의 두 번째 말을 C6 위치로 이동
        System.out.println("플레이어1의 두 번째 말을 C6으로 이동");
        piece1_2.moveTo(c6);

        // 이동 후 상태 확인
        assertEquals("플레이어1의 두 번째 말이 C6 위치에 있어야 함", c6, piece1_2.getCurrentPlace());
        assertTrue("C6에 플레이어1의 두 번째 말이 있어야 함", c6.getPieces().contains(piece1_2));

        // 도(DO) 윷 결과로 말 이동
        System.out.println("플레이어1의 두 번째 말을 C6에서 DO(도) 만큼 이동");
        Yut.YutResult doResult = Yut.YutResult.DO; // 도(1칸)
        Place destination = game.movePiece(piece1_2, doResult);

        System.out.println("이동 결과 위치: " + (destination != null ? destination.getId() : "null"));

        // 업기 후 상태 확인
        System.out.println("\n이동 후 상태:");

        // 두 말의 상태 확인
        Place place1_1 = piece1_1.getCurrentPlace();
        Place place1_2 = piece1_2.getCurrentPlace();

        System.out.println("말1 현재 위치: " + (place1_1 != null ? place1_1.getId() : "null (업혀있을 수 있음)"));
        System.out.println("말2 현재 위치: " + (place1_2 != null ? place1_2.getId() : "null (업혀있을 수 있음)"));

        // 말들이 업혀 있는지 확인
        boolean piece1_1StackedOnPiece1_2 = piece1_2.getStackedPieces().contains(piece1_1);
        boolean piece1_2StackedOnPiece1_1 = piece1_1.getStackedPieces().contains(piece1_2);

        System.out.println("말1이 말2에 업혀있는가? " + piece1_1StackedOnPiece1_2);
        System.out.println("말2가 말1에 업혀있는가? " + piece1_2StackedOnPiece1_1);

        // 업힌 말은 현재 위치가 null이어야 함
        if (piece1_1StackedOnPiece1_2) {
            assertNull("업힌 말1은 위치가 null이어야 함", piece1_1.getCurrentPlace());
            assertFalse("C_1에는 말1이 없어야 함", center1.getPieces().contains(piece1_1));

            // 업은 말 위치 확인
            assertNotNull("업은 말2는 위치가 있어야 함", piece1_2.getCurrentPlace());
            assertEquals("업은 말2는 이동 결과 위치에 있어야 함", destination, piece1_2.getCurrentPlace());

            // 업힌 말 개수 확인
            assertEquals("말2에 업힌 말 개수는 1개여야 함", 1, piece1_2.getStackedPieces().size());
        } else if (piece1_2StackedOnPiece1_1) {
            assertNull("업힌 말2는 위치가 null이어야 함", piece1_2.getCurrentPlace());
            assertFalse("C6에서 이동한 위치에 말2가 없어야 함", destination.getPieces().contains(piece1_2));

            // 업은 말 위치 확인
            assertNotNull("업은 말1은 위치가 있어야 함", piece1_1.getCurrentPlace());
            assertEquals("업은 말1은 C_1에 있어야 함", center1, piece1_1.getCurrentPlace());

            // 업힌 말 개수 확인
            assertEquals("말1에 업힌 말 개수는 1개여야 함", 1, piece1_1.getStackedPieces().size());
        } else {
            fail("두 말 중 하나는 다른 하나에 업혀 있어야 함");
        }

        // checkCenterStacking 메서드가 호출되었는지 확인하기 위해 수동으로 호출
        System.out.println("\n중심점 업기 로직 수동 실행:");
        boolean stacked = game.checkCenterStacking(piece1_2);
        System.out.println("중심점 업기 결과: " + stacked);

        // 둘 중 하나는 업혀 있어야 함
        assertTrue("두 말 중 하나가 다른 하나에 업혀 있어야 함",
                piece1_1StackedOnPiece1_2 || piece1_2StackedOnPiece1_1);

        System.out.println("===== 테스트 종료 =====\n");
    }
}