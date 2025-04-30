package org.example.model;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Map;

/**
 * 윷놀이 보드의 특수 경로와 중앙 규칙 테스트 (요구사항 수정 반영)
 */
public class BoardPathTest {

    /**
     * 중앙 경로 규칙 테스트
     * 1. O5에서 시작해서 중앙에 도달했을 때 C7, C8로 이동
     * 2. O5에서 시작해서 중앙에 도달하지 못했을 때(거쳐갔을 때) C5, C6로 이동
     * 3. O10에서 시작해서 중앙에 도달했을 때 C7, C8로 이동
     * 4. O10에서 시작해서 중앙에 도달하지 못했을 때(거쳐갔을 때)도 C7, C8로 이동
     * 5. O15에서는 C5, C6 등으로 직접 접근 불가
     */
    @Test
    public void testCenterPathRules() {
        Board board = new Board(Board.BoardType.SQUARE);
        System.out.println("\n==== 중앙 경로 규칙 테스트 ====");

        // 주요 위치 참조 얻기
        Place start = board.getStartingPlace();
        Place center = board.getCenterPlace();

        // O5, O10, O15 위치 찾기
        Place o5 = null;
        Place o10 = null;
        Place o15 = null;

        Place current = start.getNextPlace(); // O1
        for (int i = 1; i <= 19; i++) {
            if (i == 5) o5 = current;
            if (i == 10) o10 = current;
            if (i == 15) o15 = current;
            current = current.getNextPlace();
        }

        assertNotNull("O5 위치가 존재해야 함", o5);
        assertNotNull("O10 위치가 존재해야 함", o10);
        assertNotNull("O15 위치가 존재해야 함", o15);

        System.out.println("O5 위치: " + o5.getId() + " (" + o5.getName() + ")");
        System.out.println("O10 위치: " + o10.getId() + " (" + o10.getName() + ")");
        System.out.println("O15 위치: " + o15.getId() + " (" + o15.getName() + ")");

        // 각 분기점이 제대로 설정되었는지 확인
        assertTrue("O5는 분기점이어야 함", o5.isJunction());
        assertTrue("O10은 분기점이어야 함", o10.isJunction());
        assertFalse("O15는 분기점이 아니어야 함", o15.isJunction());

        // 1. O5에서 중앙까지 정확히 도달 테스트 (대각선 + 정확히 중앙)
        System.out.println("\n-- 테스트 1: O5에서 중앙까지 정확히 도달 --");
        // O5 -> C1 -> C2 -> 중앙 = 3칸 이동
        Place dest = board.calculateDestination(o5, Yut.YutResult.GEOL); // 걸(3칸)

        assertEquals("O5에서 걸(3칸) 이동 시 중앙에 도착해야 함", center, dest);

        // 2. O5에서 중앙을 지나치는 테스트 (대각선 + 중앙 통과)
        System.out.println("\n-- 테스트 2: O5에서 중앙을 지나치는 경우 --");
        // O5 -> C1 -> C2 -> 중앙 -> C5 = 4칸 이동
        dest = board.calculateDestination(o5, Yut.YutResult.YUT); // 윷(4칸)

        assertEquals("O5에서 윷(4칸) 이동 시 C5에 도착해야 함", "C5", dest.getId());

        // 3. O10에서 중앙까지 정확히 도달 테스트
        System.out.println("\n-- 테스트 3: O10에서 중앙까지 정확히 도달 --");
        // O10 -> C3 -> C4 -> 중앙 = 3칸 이동
        dest = board.calculateDestination(o10, Yut.YutResult.GEOL); // 걸(3칸)

        assertEquals("O10에서 걸(3칸) 이동 시 중앙에 도착해야 함", center, dest);

        // 4. O10에서 중앙을 지나치는 테스트 (수정된 요구사항: C7로 이동)
        System.out.println("\n-- 테스트 4: O10에서 중앙을 지나치는 경우 --");
        // O10 -> C3 -> C4 -> 중앙 -> C7 = 4칸 이동
        dest = board.calculateDestination(o10, Yut.YutResult.YUT); // 윷(4칸)

        assertEquals("O10에서 윷(4칸) 이동 시 C7에 도착해야 함", "C7", dest.getId());

        // 5. 중앙에서 이동 테스트
        System.out.println("\n-- 테스트 5: 중앙에서 이동 --");
        // 중앙 -> C7 = 1칸 이동 (기본 경로)
        dest = board.calculateDestination(center, Yut.YutResult.DO); // 도(1칸)

        assertEquals("중앙에서 도(1칸) 이동 시 C7에 도착해야 함", "C7", dest.getId());

        // 6. O15에서 직접 C5/C6/C7/C8 접근 불가능 테스트
        System.out.println("\n-- 테스트 6: O15에서 직접 중앙 경로 접근 불가 --");
        dest = board.calculateDestination(o15, Yut.YutResult.MO); // 모(5칸)

        assertNotEquals("O15에서 모(5칸) 이동 시 C5에 도착하면 안 됨", "C5", dest.getId());
        assertNotEquals("O15에서 모(5칸) 이동 시 C7에 도착하면 안 됨", "C7", dest.getId());
    }

    /**
     * 연속 이동 테스트
     * 분기점 -> 중앙 -> 다시 이동 시나리오
     */
    @Test
    public void testConsecutiveMove() {
        Board board = new Board(Board.BoardType.SQUARE);
        System.out.println("\n==== 연속 이동 테스트 ====");

        // 주요 위치 참조 얻기
        Place start = board.getStartingPlace();
        Place center = board.getCenterPlace();

        // O5, O10 위치 찾기
        Place o5 = null;
        Place o10 = null;
        Place current = start.getNextPlace(); // O1
        for (int i = 1; i <= 10; i++) {
            if (i == 5) o5 = current;
            if (i == 10) o10 = current;
            current = current.getNextPlace();
        }

        assertNotNull("O5 위치가 존재해야 함", o5);
        assertNotNull("O10 위치가 존재해야 함", o10);

        // 1. O5에서 중앙까지 도달
        System.out.println("\n-- 1차 이동(O5): O5에서 중앙까지 --");
        Place dest1 = board.calculateDestination(o5, Yut.YutResult.GEOL); // 걸(3칸)
        assertEquals("O5에서 걸 이동 시 중앙에 도착해야 함", center, dest1);

        // 2. 중앙에서 다시 이동
        System.out.println("\n-- 2차 이동(O5): 중앙에서 C7로 --");
        Place dest2 = board.calculateDestination(dest1, Yut.YutResult.DO); // 도(1칸)
        assertEquals("중앙에서 도 이동 시 C7에 도착해야 함", "C7", dest2.getId());

        // 3. O10에서 중앙까지 도달
        System.out.println("\n\n-- 1차 이동(O10): O10에서 중앙까지 --");
        dest1 = board.calculateDestination(o10, Yut.YutResult.GEOL); // 걸(3칸)
        assertEquals("O10에서 걸 이동 시 중앙에 도착해야 함", center, dest1);

        // 4. 중앙에서 다시 이동
        System.out.println("\n-- 2차 이동(O10): 중앙에서 C7로 --");
        dest2 = board.calculateDestination(dest1, Yut.YutResult.DO); // 도(1칸)
        assertEquals("중앙에서 도 이동 시 C7에 도착해야 함", "C7", dest2.getId());

        // 5. O10에서 중앙을 통과하여 C7로 이동
        System.out.println("\n\n-- 1차 이동(O10 통과): O10에서 중앙 통과 --");
        dest1 = board.calculateDestination(o10, Yut.YutResult.YUT); // 윷(4칸)
        assertEquals("O10에서 윷 이동 시 C7에 도착해야 함", "C7", dest1.getId());

        // 6. C7에서 다시 이동
        System.out.println("\n-- 2차 이동(O10 통과): C7에서 C8로 --");
        dest2 = board.calculateDestination(dest1, Yut.YutResult.DO); // 도(1칸)
        assertEquals("C7에서 도 이동 시 C8에 도착해야 함", "C8", dest2.getId());
    }

    /**
     * 모든 경로 연결 검증 테스트
     */
    @Test
    public void testAllPathConnections() {
        Board board = new Board(Board.BoardType.SQUARE);
        System.out.println("\n==== 모든 경로 연결 검증 ====");

        // 모든 위치 얻기
        Map<String, Place> allPlaces = board.getAllPlaces();

        // 모든 위치의 연결 정보 출력
        for (Map.Entry<String, Place> entry : allPlaces.entrySet()) {
            Place place = entry.getValue();
            System.out.println("\n위치: " + place.getId() + " (" + place.getName() + ")");

            // 다음 위치 정보
            if (place.getNextPlace() != null) {
                System.out.println("  - 다음 위치: " + place.getNextPlace().getId() +
                        " (" + place.getNextPlace().getName() + ")");
            } else {
                System.out.println("  - 다음 위치: 없음");
            }

            // 특별 다음 위치 정보
            if (place.hasSpecialNextPlace()) {
                System.out.println("  - 특별 다음 위치: " + place.getSpecialNextPlace().getId() +
                        " (" + place.getSpecialNextPlace().getName() + ")");
            } else {
                System.out.println("  - 특별 다음 위치: 없음");
            }

            // 위치 속성
            System.out.println("  - 분기점: " + place.isJunction());
            System.out.println("  - 중앙점: " + place.isCenter());
            System.out.println("  - 시작점: " + place.isStartingPoint());
            System.out.println("  - 도착점: " + place.isEndingPoint());
        }
    }
}