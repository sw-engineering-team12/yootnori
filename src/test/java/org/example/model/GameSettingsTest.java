package org.example.model;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.Parameter;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * 게임 설정(GameSettings) 클래스에 대한 단위 테스트 (JUnit 4 버전)
 */
@RunWith(Enclosed.class)
public class GameSettingsTest {

    /**
     * 올바른 설정값으로 초기화 테스트
     */
    @Test
    public void testValidSettings() {
        GameSettings settings = new GameSettings();

        // 플레이어 수 설정 (유효 범위: 2-4)
        settings.setPlayerCount(3);
        assertEquals("플레이어 수가 올바르게 설정되어야 함", 3, settings.getPlayerCount());

        // 말 개수 설정 (유효 범위: 2-5)
        settings.setPiecePerPlayer(4);
        assertEquals("말 개수가 올바르게 설정되어야 함", 4, settings.getPiecePerPlayer());

        // 보드 타입 설정
        settings.setBoardType(Board.BoardType.HEXAGON);
        assertEquals("보드 타입이 올바르게 설정되어야 함", Board.BoardType.HEXAGON, settings.getBoardType());
    }

    /**
     * 잘못된 플레이어 수 설정에 대한 예외 처리 테스트
     */
    @RunWith(Parameterized.class)
    public static class InvalidPlayerCountTest {

        @Parameter
        public int invalidCount;

        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][] {
                    {0}, {1}, {5}, {10}
            });
        }

        @Test(expected = IllegalArgumentException.class)
        public void testInvalidPlayerCount() {
            GameSettings settings = new GameSettings();
            settings.setPlayerCount(invalidCount);
            // 예외 메시지 검증은 JUnit 4에서는 조금 복잡하므로 예외 타입만 검증
        }
    }

    /**
     * 잘못된 말 개수 설정에 대한 예외 처리 테스트
     */
    @RunWith(Parameterized.class)
    public static class InvalidPieceCountTest {

        @Parameter
        public int invalidCount;

        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][] {
                    {0}, {1}, {6}, {10}
            });
        }

        @Test(expected = IllegalArgumentException.class)
        public void testInvalidPieceCount() {
            GameSettings settings = new GameSettings();
            settings.setPiecePerPlayer(invalidCount);
            // 예외 메시지 검증은 JUnit 4에서는 조금 복잡하므로 예외 타입만 검증
        }
    }

    /**
     * 기본값 테스트
     */
    @Test
    public void testDefaultSettings() {
        GameSettings settings = new GameSettings();

        // 기본값 검증
        assertEquals("플레이어 수 기본값은 2여야 함", 2, settings.getPlayerCount());
        assertEquals("말 개수 기본값은 4여야 함", 4, settings.getPiecePerPlayer());
        assertEquals("보드 타입 기본값은 사각형(SQUARE)이어야 함", Board.BoardType.SQUARE, settings.getBoardType());
    }

    /**
     * 경계값 테스트 - 플레이어 수
     */
    @Test
    public void testPlayerCountBoundaryValues() {
        GameSettings settings = new GameSettings();

        // 최소값
        settings.setPlayerCount(2);
        assertEquals("최소 플레이어 수(2)가 허용되어야 함", 2, settings.getPlayerCount());

        // 최대값
        settings.setPlayerCount(4);
        assertEquals("최대 플레이어 수(4)가 허용되어야 함", 4, settings.getPlayerCount());
    }

    /**
     * 경계값 테스트 - 말 개수
     */
    @Test
    public void testPieceCountBoundaryValues() {
        GameSettings settings = new GameSettings();

        // 최소값
        settings.setPiecePerPlayer(2);
        assertEquals("최소 말 개수(2)가 허용되어야 함", 2, settings.getPiecePerPlayer());

        // 최대값
        settings.setPiecePerPlayer(5);
        assertEquals("최대 말 개수(5)가 허용되어야 함", 5, settings.getPiecePerPlayer());
    }

    /**
     * 모든 보드 타입 설정 테스트
     */
    @Test
    public void testAllBoardTypes() {
        GameSettings settings = new GameSettings();

        // 사각형 보드
        settings.setBoardType(Board.BoardType.SQUARE);
        assertEquals("사각형 보드 타입이 설정되어야 함", Board.BoardType.SQUARE, settings.getBoardType());

        // 오각형 보드
        settings.setBoardType(Board.BoardType.PENTAGON);
        assertEquals("오각형 보드 타입이 설정되어야 함", Board.BoardType.PENTAGON, settings.getBoardType());

        // 육각형 보드
        settings.setBoardType(Board.BoardType.HEXAGON);
        assertEquals("육각형 보드 타입이 설정되어야 함", Board.BoardType.HEXAGON, settings.getBoardType());
    }

    /**
     * null 보드 타입 설정 테스트
     */
    @Test(expected = NullPointerException.class)
    public void testNullBoardType() {
        GameSettings settings = new GameSettings();
        settings.setBoardType(null);
        // JUnit 4에서는 expected 속성으로 예외 유형을 검증
    }

    /**
     * 예외 메시지 검증을 위한 별도의 테스트
     */
    @Test
    public void testExceptionMessages() {
        GameSettings settings = new GameSettings();

        // 플레이어 수 예외 메시지 검증
        try {
            settings.setPlayerCount(0);
            fail("예외가 발생해야 함");
        } catch (IllegalArgumentException e) {
            assertTrue("예외 메시지가 올바르게 포함되어야 함",
                    e.getMessage().contains("플레이어 수는 2명에서 4명 사이여야 합니다"));
        }

        // 말 개수 예외 메시지 검증
        try {
            settings.setPiecePerPlayer(0);
            fail("예외가 발생해야 함");
        } catch (IllegalArgumentException e) {
            assertTrue("예외 메시지가 올바르게 포함되어야 함",
                    e.getMessage().contains("플레이어당 말 개수는 2개에서 5개 사이여야 합니다"));
        }

        // null 보드 타입 예외 메시지 검증
        try {
            settings.setBoardType(null);
            fail("예외가 발생해야 함");
        } catch (NullPointerException e) {
            assertTrue("예외 메시지가 올바르게 포함되어야 함",
                    e.getMessage().contains("보드 타입은 null일 수 없습니다"));
        }
    }
}