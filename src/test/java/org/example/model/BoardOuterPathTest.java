package org.example.model;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 윷놀이 보드의 외곽 경로 순회 테스트 (수정 버전)
 * 외곽 경로가 O1부터 시작하는 구조에 맞춘 테스트
 */
public class BoardOuterPathTest {

    /**
     * 사각형 보드 외곽 경로 순회 테스트
     * 시작점에서부터 S → O1 → ... → O19 → S로 돌아오는지 확인
     */
    @Test
    public void testSquareBoardOuterPath() {
        // 1. 보드 생성
        Board board = new Board(Board.BoardType.SQUARE);

        // 2. 시작 위치 확인
        Place start = board.getStartingPlace();
        assertNotNull("시작 위치가 null이 아니어야 함", start);

        // 3. 시작 위치에서 외곽 첫 번째 위치(O1)로 이동
        Place firstPlace = start.getNextPlace();
        assertNotNull("O1 위치가 존재해야 함", firstPlace);
        assertEquals("O1", firstPlace.getId());
        System.out.println("첫 번째 위치: " + firstPlace.getId() + " (" + firstPlace.getName() + ")");

        // 4. 첫 번째 위치부터 외곽을 따라 19번 이동하면 다시 시작점으로 돌아와야 함
        Place current = firstPlace;
        StringBuilder path = new StringBuilder();
        path.append("경로: ").append(start.getId());

        for (int i = 1; i <= 19; i++) {
            // 현재 위치 기록
            path.append(" → ").append(current.getId());

            // 다음 위치로 이동
            Place next = current.getNextPlace();
            assertNotNull(i + "번째 위치에서 다음 위치가 null이 아니어야 함", next);

            // 마지막 위치(O19)의 다음은 시작점(S)이어야 함
            if (i == 19) {
                assertEquals("외곽 마지막 위치의 다음은 시작점이어야 함", start, next);
                path.append(" → ").append(start.getId());
                System.out.println(path.toString());
                break;
            }

            // 다음 위치의 ID 확인 (O1 → O2 → ... → O19)
            String expectedId = "O" + (i + 1);
            assertEquals("다음 위치의 ID가 순서대로 증가해야 함", expectedId, next.getId());

            // 다음 위치로 이동
            current = next;
        }

        // 5. 여기까지 오면 외곽 순회가 성공한 것
        System.out.println("외곽 경로 순회 성공!");
    }

    /**
     * 사각형 보드에서 윷 결과로 외곽을 따라 이동하는 테스트
     * 다양한 윷 결과(도/개/걸/윷/모)로 외곽을 이동할 수 있는지 확인
     */
    @Test
    public void testMoveAlongOuterPath() {
        // 1. 보드 생성
        Board board = new Board(Board.BoardType.SQUARE);

        // 2. 시작 위치 확인
        Place start = board.getStartingPlace();

        // 3. 시작 위치에서 윷 결과별 이동 확인
        System.out.println("\n==== 윷 결과별 이동 테스트 ====");

        // 3.1 도(1칸) 이동
        Place dest = board.calculateDestination(start, Yut.YutResult.DO);
        assertEquals("시작점에서 도 이동 시 O1에 도착해야 함", "O1", dest.getId());
        System.out.println("도(1칸) 이동: " + start.getId() + " → " + dest.getId());

        // 3.2 개(2칸) 이동
        dest = board.calculateDestination(start, Yut.YutResult.GAE);
        assertEquals("시작점에서 개 이동 시 O2에 도착해야 함", "O2", dest.getId());
        System.out.println("개(2칸) 이동: " + start.getId() + " → " + dest.getId());

        // 3.3 걸(3칸) 이동
        dest = board.calculateDestination(start, Yut.YutResult.GEOL);
        assertEquals("시작점에서 걸 이동 시 O3에 도착해야 함", "O3", dest.getId());
        System.out.println("걸(3칸) 이동: " + start.getId() + " → " + dest.getId());

        // 3.4 윷(4칸) 이동
        dest = board.calculateDestination(start, Yut.YutResult.YUT);
        assertEquals("시작점에서 윷 이동 시 O4에 도착해야 함", "O4", dest.getId());
        System.out.println("윷(4칸) 이동: " + start.getId() + " → " + dest.getId());

        // 3.5 모(5칸) 이동
        dest = board.calculateDestination(start, Yut.YutResult.MO);
        assertEquals("시작점에서 모 이동 시 O5에 도착해야 함", "O5", dest.getId());
        System.out.println("모(5칸) 이동: " + start.getId() + " → " + dest.getId());

        // 4. 외곽 위치에서 시작점으로 돌아오는 이동 확인
        Place lastOuterPlace = null;
        Place current = start;

        // O19 위치 찾기 (마지막 외곽 위치)
        for (int i = 0; i < 19; i++) {
            current = current.getNextPlace();
        }
        lastOuterPlace = current;
        assertEquals("O19", lastOuterPlace.getId());

        // 4.1 마지막 위치(O19)에서 도(1칸) 이동 시 시작점으로 돌아와야 함
        dest = board.calculateDestination(lastOuterPlace, Yut.YutResult.DO);
        assertEquals("O19에서 도 이동 시 시작점으로 돌아와야 함", start, dest);
        System.out.println("\n마지막 위치에서 시작점으로 돌아오기: " + lastOuterPlace.getId() + " → " + dest.getId());
    }

    /**
     * 초기화 로직 자체를 검증하는 테스트
     * 배열 인덱스가 1부터 19까지 사용되는지 확인
     */
    @Test
    public void testBoardInitialization() {
        Board board = new Board(Board.BoardType.SQUARE);

        // 시작점 확인
        Place start = board.getStartingPlace();
        assertNotNull("시작 위치가 존재해야 함", start);

        // 시작점 → O1 연결 확인
        Place o1 = start.getNextPlace();
        assertNotNull("O1 위치가 존재해야 함", o1);
        assertEquals("O1", o1.getId());

        // O1 → O2 연결 확인
        Place o2 = o1.getNextPlace();
        assertNotNull("O2 위치가 존재해야 함", o2);
        assertEquals("O2", o2.getId());

        // O19 → 시작점 연결 확인
        Place current = start;
        for (int i = 0; i < 19; i++) {
            current = current.getNextPlace();
        }
        assertEquals("O19", current.getId());

        Place afterO19 = current.getNextPlace();
        assertEquals("O19 다음은 시작점이어야 함", start, afterO19);

        System.out.println("보드 초기화 검증 성공: 외곽 경로가 O1부터 O19까지 존재하고 시작점과 연결됨");
    }
}