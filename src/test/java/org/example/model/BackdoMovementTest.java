package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 빽도 이동에 대한 테스트 케이스
 * 다양한 보드 형태와 위치에서 빽도 이동을 검증합니다.
 */
public class BackdoMovementTest {

    /**
     * 사각형 보드에서의 빽도 이동 테스트
     */
    @Nested
    @DisplayName("사각형 보드에서의 빽도 이동 테스트")
    class SquareBoardBackdoTest {
        private Game game;
        private Board board;
        private Player player;
        private Piece piece1;
        private Piece piece2;

        @BeforeEach
        void setUp() {
            // 게임 설정 및 초기화 - 플레이어 수를 2로 수정
            GameSettings settings = new GameSettings();
            settings.setBoardType(Board.BoardType.SQUARE);
            settings.setPlayerCount(2);  // 최소 2명으로 설정
            settings.setPiecePerPlayer(2);

            game = new Game();
            game.initialize(settings);

            board = game.getBoard();
            player = game.getPlayers().get(0);  // 첫 번째 플레이어만 사용
            piece1 = player.getPieces().get(0);
            piece2 = player.getPieces().get(1);

            // 두 말을 같은 위치(5)로 이동
            Place place5 = board.getPlaceById("5");
            piece1.moveTo(place5);
            piece2.moveTo(place5);

            // piece1이 piece2를 업음
            piece1.stackPiece(piece2);

            // 업기 상태 확인
            assertTrue(piece1.getStackedPieces().contains(piece2), "piece1이 piece2를 업어야 함");
            assertNull(piece2.getCurrentPlace(), "업힌 말은 위치가 null이어야 함");
        }

        @Test
        @DisplayName("시작점(S)에서 빽도 이동 - 제자리 유지 확인")
        void testBackdoFromStartingPoint() {
            Place startingPlace = board.getStartingPlace();
            Place result = board.calculateDestination(startingPlace, Yut.YutResult.BACKDO);

            // 시작점에서 빽도는 제자리에 머무름
            assertEquals(startingPlace, result, "시작점에서 빽도 이동 시 제자리에 머물러야 함");
        }

        @Test
        @DisplayName("업힌 말이 있는 상태에서 도착점에서 빽도 이동 테스트")
        void testBackdoMovementThroughEndWithStackedPiece() {
            // piece1을 도착점(E) 위치로 이동
            Place endPlace = board.getPlaceById("E");
            piece1.moveTo(endPlace);

            // 빽도 윷 결과를 게임에 추가 (이 부분이 중요!)
            game.setSpecificYutResult(Yut.YutResult.BACKDO);

            // 빽도로 이동 (E -> 19)
            Place destination = game.movePiece(piece1, Yut.YutResult.BACKDO);
            Place expectedPlace = board.getPlaceById("19");

            assertEquals(expectedPlace, destination, "빽도 이동 후 위치 19에 있어야 함");
            assertEquals(expectedPlace, piece1.getCurrentPlace(), "piece1이 위치 19에 있어야 함");

            // 업힌 상태 유지 확인
            assertTrue(piece1.getStackedPieces().contains(piece2), "빽도 이동 후에도 piece1이 piece2를 업고 있어야 함");
        }
        @Test
        @DisplayName("도착점(E)에서 빽도 이동")
        void testBackdoFromEndPoint() {
            Place endPlace = board.getPlaceById("E");
            Place expectedPrevious = board.getPlaceById("19");

            Place result = board.calculateDestination(endPlace, Yut.YutResult.BACKDO);
            assertEquals(expectedPrevious, result, "도착점에서 빽도 이동 시 위치 19로 이동해야 함");
        }


        @Test
        @DisplayName("특별 경로(C1~C8)에서 빽도 이동")
        void testBackdoFromSpecialPath() {
            // C1에서 빽도 테스트 (분기점 5에서 진입)
            Place placeC1 = board.getPlaceById("C1");
            Place expectedPrevious = board.getPlaceById("5");

            Place result = board.calculateDestination(placeC1, Yut.YutResult.BACKDO);
            assertEquals(expectedPrevious, result, "C1에서 빽도 이동 시 분기점 5로 이동해야 함");

            // C2에서 빽도 테스트
            Place placeC2 = board.getPlaceById("C2");
            expectedPrevious = board.getPlaceById("C1");

            result = board.calculateDestination(placeC2, Yut.YutResult.BACKDO);
            assertEquals(expectedPrevious, result, "C2에서 빽도 이동 시 C1으로 이동해야 함");
        }

        @Test
        @DisplayName("중앙점(C_1, C_2)에서 빽도 이동")
        void testBackdoFromCenterPoint() {
            // 중앙점 C_1에서 빽도 테스트
            Place centerC1 = board.getPlaceById("C_1");
            Place expectedPrevious = board.getPlaceById("C2");

            Place result = board.calculateDestination(centerC1, Yut.YutResult.BACKDO);
            assertEquals(expectedPrevious, result, "중앙점 C_1에서 빽도 이동 시 C2로 이동해야 함");

            // 중앙점 C_2에서 빽도 테스트
            Place centerC2 = board.getPlaceById("C_2");
            expectedPrevious = board.getPlaceById("C4");

            result = board.calculateDestination(centerC2, Yut.YutResult.BACKDO);
            assertEquals(expectedPrevious, result, "중앙점 C_2에서 빽도 이동 시 C4로 이동해야 함");
        }
        @Test
        @DisplayName("실제 말 이동으로 빽도 테스트")
        void testActualPieceMovementWithBackdo() {
            Player player = game.getPlayers().get(0);
            Piece piece = player.getPieces().get(0);

            // 먼저 말을 위치 5로 이동
            Place place5 = board.getPlaceById("5");
            piece.moveTo(place5);
            assertEquals(place5, piece.getCurrentPlace(), "말이 위치 5에 있어야 함");

            // 빽도 윷 결과를 게임에 추가
            game.setSpecificYutResult(Yut.YutResult.BACKDO);

            // 빽도로 이동 실행
            Place destination = game.movePiece(piece, Yut.YutResult.BACKDO);
            Place expectedPlace = board.getPlaceById("4");

            assertEquals(expectedPlace, destination, "빽도 이동 결과가 위치 4여야 함");
            assertEquals(expectedPlace, piece.getCurrentPlace(), "말이 위치 4로 이동해야 함");
        }
    }

    /**
     * 오각형 보드에서의 빽도 이동 테스트
     */
    @Nested
    @DisplayName("오각형 보드에서의 빽도 이동 테스트")
    class PentagonBoardBackdoTest {
        private Board board;
        private Game game;

        @BeforeEach
        void setUp() {
            // 오각형 보드 초기화
            board = new Board(Board.BoardType.PENTAGON);

            // 게임 설정 및 초기화
            GameSettings settings = new GameSettings();
            settings.setBoardType(Board.BoardType.PENTAGON);
            settings.setPlayerCount(2);
            settings.setPiecePerPlayer(2);

            game = new Game();
            game.initialize(settings);
        }

        @Test
        @DisplayName("시작점(S)에서 빽도 이동 - 제자리 유지 확인")
        void testBackdoFromStartingPoint() {
            Place startingPlace = board.getStartingPlace();
            Place result = board.calculateDestination(startingPlace, Yut.YutResult.BACKDO);

            assertEquals(startingPlace, result, "시작점에서 빽도 이동 시 제자리에 머물러야 함");
        }

        @Test
        @DisplayName("일반 외곽 위치(1~24)에서 빽도 이동")
        void testBackdoFromOuterPath() {
            // 위치 10에서 빽도 테스트
            Place place10 = board.getPlaceById("10");
            Place expectedPrevious = board.getPlaceById("9");

            Place result = board.calculateDestination(place10, Yut.YutResult.BACKDO);
            assertEquals(expectedPrevious, result, "위치 10에서 빽도 이동 시 위치 9로 이동해야 함");
        }

        @Test
        @DisplayName("도착점(E)에서 빽도 이동")
        void testBackdoFromEndPoint() {
            Place endPlace = board.getPlaceById("E");
            Place expectedPrevious = board.getPlaceById("24");

            Place result = board.calculateDestination(endPlace, Yut.YutResult.BACKDO);
            assertEquals(expectedPrevious, result, "도착점에서 빽도 이동 시 위치 24로 이동해야 함");
        }

        @Test
        @DisplayName("특별 경로 진입점(C1, C3, C5)에서 빽도 이동")
        void testBackdoFromSpecialPathEntrance() {
            // C1에서 빽도 테스트
            Place placeC1 = board.getPlaceById("C1");
            Place expectedPrevious = board.getPlaceById("5");

            Place result = board.calculateDestination(placeC1, Yut.YutResult.BACKDO);
            assertEquals(expectedPrevious, result, "C1에서 빽도 이동 시 분기점 5로 이동해야 함");

            // C3에서 빽도 테스트
            Place placeC3 = board.getPlaceById("C3");
            expectedPrevious = board.getPlaceById("10");

            result = board.calculateDestination(placeC3, Yut.YutResult.BACKDO);
            assertEquals(expectedPrevious, result, "C3에서 빽도 이동 시 분기점 10으로 이동해야 함");
        }

        @Test
        @DisplayName("중앙점(C_1, C_2)에서 빽도 이동")
        void testBackdoFromCenterPoint() {
            // 중앙점 C_1에서 빽도 테스트
            Place centerC1 = board.getPlaceById("C_1");
            Place expectedPrevious = board.getPlaceById("C2");

            Place result = board.calculateDestination(centerC1, Yut.YutResult.BACKDO);
            assertEquals(expectedPrevious, result, "중앙점 C_1에서 빽도 이동 시 C2로 이동해야 함");

            // 중앙점 C_2에서 빽도 테스트
            Place centerC2 = board.getPlaceById("C_2");
            expectedPrevious = board.getPlaceById("C6");

            result = board.calculateDestination(centerC2, Yut.YutResult.BACKDO);
            assertEquals(expectedPrevious, result, "중앙점 C_2에서 빽도 이동 시 C6로 이동해야 함");
        }

        @Test
        @DisplayName("특별 경로(C7~C10)에서 빽도 이동")
        void testBackdoFromSpecialPath() {
            // C7에서 빽도 테스트
            Place placeC7 = board.getPlaceById("C7");
            Place expectedPrevious = board.getPlaceById("C_1");

            Place result = board.calculateDestination(placeC7, Yut.YutResult.BACKDO);
            assertEquals(expectedPrevious, result, "C7에서 빽도 이동 시 중앙점 C_1으로 이동해야 함");

            // C9에서 빽도 테스트
            Place placeC9 = board.getPlaceById("C9");
            expectedPrevious = board.getPlaceById("C_1");

            result = board.calculateDestination(placeC9, Yut.YutResult.BACKDO);
            assertEquals(expectedPrevious, result, "C9에서 빽도 이동 시 중앙점 C_1으로 이동해야 함");
        }
    }

    /**
     * 육각형 보드에서의 빽도 이동 테스트
     */
    @Nested
    @DisplayName("육각형 보드에서의 빽도 이동 테스트")
    class HexagonBoardBackdoTest {
        private Board board;
        private Game game;

        @BeforeEach
        void setUp() {
            // 육각형 보드 초기화
            board = new Board(Board.BoardType.HEXAGON);

            // 게임 설정 및 초기화
            GameSettings settings = new GameSettings();
            settings.setBoardType(Board.BoardType.HEXAGON);
            settings.setPlayerCount(2);
            settings.setPiecePerPlayer(2);

            game = new Game();
            game.initialize(settings);
        }

        @Test
        @DisplayName("시작점(S)에서 빽도 이동 - 제자리 유지 확인")
        void testBackdoFromStartingPoint() {
            Place startingPlace = board.getStartingPlace();
            Place result = board.calculateDestination(startingPlace, Yut.YutResult.BACKDO);

            assertEquals(startingPlace, result, "시작점에서 빽도 이동 시 제자리에 머물러야 함");
        }

        @Test
        @DisplayName("일반 외곽 위치(1~29)에서 빽도 이동")
        void testBackdoFromOuterPath() {
            // 위치 15에서 빽도 테스트
            Place place15 = board.getPlaceById("15");
            Place expectedPrevious = board.getPlaceById("14");

            Place result = board.calculateDestination(place15, Yut.YutResult.BACKDO);
            assertEquals(expectedPrevious, result, "위치 15에서 빽도 이동 시 위치 14로 이동해야 함");
        }

        @Test
        @DisplayName("도착점(E)에서 빽도 이동")
        void testBackdoFromEndPoint() {
            Place endPlace = board.getPlaceById("E");
            Place expectedPrevious = board.getPlaceById("29");

            Place result = board.calculateDestination(endPlace, Yut.YutResult.BACKDO);
            assertEquals(expectedPrevious, result, "도착점에서 빽도 이동 시 위치 29로 이동해야 함");
        }

        @Test
        @DisplayName("특별 경로 진입점(C1, C3, C5, C7)에서 빽도 이동")
        void testBackdoFromSpecialPathEntrance() {
            // C1에서 빽도 테스트
            Place placeC1 = board.getPlaceById("C1");
            Place expectedPrevious = board.getPlaceById("5");

            Place result = board.calculateDestination(placeC1, Yut.YutResult.BACKDO);
            assertEquals(expectedPrevious, result, "C1에서 빽도 이동 시 분기점 5로 이동해야 함");

            // C7에서 빽도 테스트
            Place placeC7 = board.getPlaceById("C7");
            expectedPrevious = board.getPlaceById("20");

            result = board.calculateDestination(placeC7, Yut.YutResult.BACKDO);
            assertEquals(expectedPrevious, result, "C7에서 빽도 이동 시 분기점 20으로 이동해야 함");
        }

        @Test
        @DisplayName("중앙점(C_1, C_2)에서 빽도 이동")
        void testBackdoFromCenterPoint() {
            // 중앙점 C_1에서 빽도 테스트
            Place centerC1 = board.getPlaceById("C_1");
            Place expectedPrevious = board.getPlaceById("C2");

            Place result = board.calculateDestination(centerC1, Yut.YutResult.BACKDO);
            assertEquals(expectedPrevious, result, "중앙점 C_1에서 빽도 이동 시 C2로 이동해야 함");

            // 중앙점 C_2에서 빽도 테스트
            Place centerC2 = board.getPlaceById("C_2");
            expectedPrevious = board.getPlaceById("C8");

            result = board.calculateDestination(centerC2, Yut.YutResult.BACKDO);
            assertEquals(expectedPrevious, result, "중앙점 C_2에서 빽도 이동 시 C8로 이동해야 함");
        }

        @Test
        @DisplayName("특별 경로 종료 지점(C11, C12)에서 빽도 이동")
        void testBackdoFromSpecialPathEnd() {
            // C11에서 빽도 테스트
            Place placeC11 = board.getPlaceById("C11");
            Place expectedPrevious = board.getPlaceById("C_1");

            Place result = board.calculateDestination(placeC11, Yut.YutResult.BACKDO);
            assertEquals(expectedPrevious, result, "C11에서 빽도 이동 시 중앙점 C_1으로 이동해야 함");

            // C12에서 빽도 테스트
            Place placeC12 = board.getPlaceById("C12");
            expectedPrevious = board.getPlaceById("C11");

            result = board.calculateDestination(placeC12, Yut.YutResult.BACKDO);
            assertEquals(expectedPrevious, result, "C12에서 빽도 이동 시 C11으로 이동해야 함");
        }
    }

    /**
     * 빽도 이동 시 업힌 말 처리 테스트
     */
    @Nested
    @DisplayName("빽도 이동 시 업힌 말 처리 테스트")
    class BackdoWithStackedPieceTest {
        private Game game;
        private Board board;
        private Player player;
        private Piece piece1;
        private Piece piece2;

        @BeforeEach
        void setUp() {
            // 게임 설정 및 초기화
            GameSettings settings = new GameSettings();
            settings.setBoardType(Board.BoardType.SQUARE);
            settings.setPlayerCount(2);
            settings.setPiecePerPlayer(2);

            game = new Game();
            game.initialize(settings);

            board = game.getBoard();
            player = game.getPlayers().get(0);
            piece1 = player.getPieces().get(0);
            piece2 = player.getPieces().get(1);

            // 두 말을 같은 위치(5)로 이동
            Place place5 = board.getPlaceById("5");
            piece1.moveTo(place5);
            piece2.moveTo(place5);

            // piece1이 piece2를 업음
            piece1.stackPiece(piece2);

            // 업기 상태 확인
            assertTrue(piece1.getStackedPieces().contains(piece2), "piece1이 piece2를 업어야 함");
            assertNull(piece2.getCurrentPlace(), "업힌 말은 위치가 null이어야 함");
        }

        @Test
        @DisplayName("업힌 말과 함께 빽도 이동 테스트")
        void testBackdoMovementWithStackedPiece() {
            // 빽도 윷 결과를 게임에 추가
            game.setSpecificYutResult(Yut.YutResult.BACKDO);

            // 빽도로 이동
            Place expectedPlace = board.getPlaceById("4");
            Place destination = game.movePiece(piece1, Yut.YutResult.BACKDO);

            assertEquals(expectedPlace, destination, "업힌 말과 함께 빽도 이동 후 위치 4에 있어야 함");
            assertEquals(expectedPlace, piece1.getCurrentPlace(), "piece1이 위치 4에 있어야 함");

            // 업힌 상태 유지 확인
            assertTrue(piece1.getStackedPieces().contains(piece2), "빽도 이동 후에도 piece1이 piece2를 업고 있어야 함");
            assertNull(piece2.getCurrentPlace(), "업힌 말은 여전히 위치가 null이어야 함");
        }



    }
}