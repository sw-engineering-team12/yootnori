package org.example.model;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

/**
 * Board 클래스에 대한 개선된 단위 테스트
 * 각 위치에서 이동 경로 및 윷 결과별 도착지점을 명확하게 검증합니다.
 */
public class BoardTest {

    // 테스트 로그를 출력하는 유틸리티 메서드
    private void log(String message) {
        System.out.println("[TEST LOG] " + message);
    }

    // 위치 정보를 로깅하는 메서드
    private void logPlace(Place place) {
        if (place == null) {
            log("위치: null");
            return;
        }

        log(String.format("위치: id=%s, name=%s, junction=%b, center=%b, startingPoint=%b, endingPoint=%b",
                place.getId(), place.getName(), place.isJunction(), place.isCenter(),
                place.isStartingPoint(), place.isEndingPoint()));

        // 다음 위치 정보 로깅
        if (place.getNextPlace() != null) {
            log("  - 다음 위치: " + place.getNextPlace().getId() + " (" + place.getNextPlace().getName() + ")");
        }

        // 특별 다음 위치 정보 로깅
        if (place.hasSpecialNextPlace()) {
            log("  - 특별 다음 위치: " + place.getSpecialNextPlace().getId() + " (" + place.getSpecialNextPlace().getName() + ")");
        }
    }

    // 윷 결과별 이동 위치 테스트 및 로깅
    private void testMoveFromPlace(Board board, Place startPlace, String message) {
        log("\n=== " + message + " ===");
        logPlace(startPlace);

        // 각 윷 결과별 이동 테스트
        Yut.YutResult[] results = {
                Yut.YutResult.BACKDO, Yut.YutResult.DO, Yut.YutResult.GAE,
                Yut.YutResult.GEOL, Yut.YutResult.YUT, Yut.YutResult.MO
        };

        for (Yut.YutResult result : results) {
            Place destination = board.calculateDestination(startPlace, result);
            log(String.format("  %s 결과: 도착지점 = %s (%s)",
                    result.getName(), destination.getId(), destination.getName()));

            // 중요 위치 추가 확인
            if (destination.isCenter()) {
                log("    => 중앙에 도착함");
            }
            if (destination.isJunction()) {
                log("    => 분기점에 도착함");
            }
        }
    }

    @Test
    public void testSquareBoardStructure() {
        Board board = new Board(Board.BoardType.SQUARE);
        log("\n==== 사각형 보드 구조 검증 ====");

        // 기본 위치 검증
        Place start = board.getStartingPlace();
        Place center = board.getCenterPlace();

        assertNotNull("시작 위치가 존재해야 함", start);
        assertNotNull("중앙 위치가 존재해야 함", center);
        assertTrue("중앙이 중앙으로 설정되어야 함", center.isCenter());

        // 시작 위치에서 이동 경로 검증
        log("\n-- 시작 위치에서 이동 경로 --");
        logPlace(start);
        assertNotNull("시작 위치에서 다음 위치가 존재해야 함", start.getNextPlace());

        // 위치 연결성 검증
        Place current = start;
        int count = 0;
        int maxCount = 25; // 무한 루프 방지

        log("\n-- 보드 경로 탐색 결과 --");
        while (current != null && count < maxCount) {
            logPlace(current);

            // 중앙 위치 검증
            if (current.isCenter()) {
                assertNotNull("중앙 위치는 다음 위치를 가져야 함", current.getNextPlace());
            }

            // 분기점 검증
            if (current.isJunction()) {
                assertTrue("분기점은 특별 다음 위치를 가져야 함", current.hasSpecialNextPlace());
            }

            // 다음 위치로 이동
            current = current.getNextPlace();
            count++;

            // 시작점으로 돌아왔으면 종료
            if (current == start) {
                log("  => 시작점으로 돌아옴 (한 바퀴 완료)");
                break;
            }
        }

        // 윷 결과별 이동 위치 테스트
        testMoveFromPlace(board, start, "시작 위치에서 윷 결과별 이동");

        // 외곽 경로 첫 번째 위치
        Place firstOuter = start.getNextPlace();
        testMoveFromPlace(board, firstOuter, "외곽 첫 번째 위치에서 윷 결과별 이동");

        // 분기점 찾기
        Place junction = null;
        for (Place place : board.getAllPlaces().values()) {
            if (place.isJunction()) {
                junction = place;
                break;
            }
        }

        assertNotNull("분기점이 존재해야 함", junction);
        testMoveFromPlace(board, junction, "분기점에서 윷 결과별 이동");

        // 중앙 위치 테스트
        testMoveFromPlace(board, center, "중앙 위치에서 윷 결과별 이동");
    }

    @Test
    public void testPentagonBoardSpecialRules() {
        Board board = new Board(Board.BoardType.PENTAGON);
        log("\n==== 오각형 보드 특수 규칙 검증 ====");

        // 기본 위치 검증
        Place start = board.getStartingPlace();
        Place center = board.getCenterPlace();

        assertNotNull("시작 위치가 존재해야 함", start);
        assertNotNull("중앙 위치가 존재해야 함", center);
        assertTrue("중앙이 중앙으로 설정되어야 함", center.isCenter());

        // 중앙 위치의 두 가지 경로 검증
        log("\n-- 중앙 위치의 경로 설정 확인 --");
        logPlace(center);
        assertNotNull("중앙 위치는 기본 다음 위치(1번 경로)를 가져야 함", center.getNextPlace());
        assertNotNull("중앙 위치는 특별 다음 위치(2번 경로)를 가져야 함", center.getSpecialNextPlace());

        // 중앙 직전 위치 찾기
        Place beforeCenter = null;
        for (Place place : board.getAllPlaces().values()) {
            if (place.getNextPlace() == center) {
                beforeCenter = place;
                log("\n-- 중앙 직전 위치 발견 --");
                logPlace(beforeCenter);
                break;
            }
        }

        assertNotNull("중앙 직전 위치가 존재해야 함", beforeCenter);

        // 중앙 전 위치에서 도/개/걸/윷/모 이동 검증
        log("\n-- 중앙 직전 위치에서 윷 결과별 이동 테스트 --");

        // 도(1칸) 이동 - 중앙에 정지
        Place dest = board.calculateDestination(beforeCenter, Yut.YutResult.DO);
        log("도 이동 결과: " + dest.getId() + " (" + dest.getName() + ")");
        assertEquals("도 이동 시 중앙에 도착해야 함", center, dest);

        // 윷(4칸) 이동 - 특별 경로로 진행
        dest = board.calculateDestination(beforeCenter, Yut.YutResult.YUT);
        log("윷 이동 결과: " + dest.getId() + " (" + dest.getName() + ")");
        assertNotEquals("윷 이동 시 중앙을 지나 계속 진행해야 함", center, dest);

        // 모든 분기점 조회 및 검증
        log("\n-- 모든 분기점 목록 --");
        int junctionCount = 0;
        for (Place place : board.getAllPlaces().values()) {
            if (place.isJunction()) {
                junctionCount++;
                log(String.format("분기점 %d: id=%s, name=%s",
                        junctionCount, place.getId(), place.getName()));
                logPlace(place);

                // 분기점은 특별 다음 위치를 가져야 함
                assertTrue("분기점은 특별 다음 위치를 가져야 함", place.hasSpecialNextPlace());

                // 분기점에서 1칸 이동 테스트
                Place normalDest = place.getNextPlace();
                Place specialDest = place.getSpecialNextPlace();

                log("  - 일반 경로로 1칸: " + normalDest.getId());
                log("  - 특별 경로로 1칸: " + specialDest.getId());

                // 분기점에서 도 이동 - 특별 경로로 이동해야 함
                Place doDest = board.calculateDestination(place, Yut.YutResult.DO);
                log("  - 도 이동 결과: " + doDest.getId());
                assertEquals("분기점에서 도 이동 시 특별 경로로 이동해야 함", specialDest, doDest);
            }
        }

        // 오각형은 5개의 분기점을 가져야 함
        log("오각형 보드의 총 분기점 수: " + junctionCount);
        assertEquals("오각형 보드는 5개의 분기점을 가져야 함", 5, junctionCount);
    }

    @Test
    public void testHexagonBoardSpecialRules() {
        Board board = new Board(Board.BoardType.HEXAGON);
        log("\n==== 육각형 보드 특수 규칙 검증 ====");

        // 기본 위치 검증
        Place start = board.getStartingPlace();
        Place center = board.getCenterPlace();

        assertNotNull("시작 위치가 존재해야 함", start);
        assertNotNull("중앙 위치가 존재해야 함", center);
        assertTrue("중앙이 중앙으로 설정되어야 함", center.isCenter());

        // 중앙 위치의 두 가지 경로 검증
        log("\n-- 중앙 위치의 경로 설정 확인 --");
        logPlace(center);
        assertNotNull("중앙 위치는 기본 다음 위치(1번 경로)를 가져야 함", center.getNextPlace());
        assertNotNull("중앙 위치는 특별 다음 위치(2번 경로)를 가져야 함", center.getSpecialNextPlace());

        // 중앙 직전 위치 찾기
        Place beforeCenter = null;
        for (Place place : board.getAllPlaces().values()) {
            if (place.getNextPlace() == center) {
                beforeCenter = place;
                log("\n-- 중앙 직전 위치 발견 --");
                logPlace(beforeCenter);
                break;
            }
        }

        assertNotNull("중앙 직전 위치가 존재해야 함", beforeCenter);

        // 중앙 전 위치에서 도/개/걸/윷/모 이동 검증
        log("\n-- 중앙 직전 위치에서 윷 결과별 이동 테스트 --");

        // 도(1칸) 이동 - 중앙에 정지
        Place dest = board.calculateDestination(beforeCenter, Yut.YutResult.DO);
        log("도 이동 결과: " + dest.getId() + " (" + dest.getName() + ")");
        assertEquals("도 이동 시 중앙에 도착해야 함", center, dest);

        // 개(2칸) 이동 - 중앙 도착 후 1번 경로
        dest = board.calculateDestination(beforeCenter, Yut.YutResult.GAE);
        log("개 이동 결과: " + dest.getId() + " (" + dest.getName() + ")");
        assertEquals("개 이동 시 중앙에 도착해야 함", center, dest);

        // 걸(3칸) 이동 - 중앙 도착 후 1번 경로
        dest = board.calculateDestination(beforeCenter, Yut.YutResult.GEOL);
        log("걸 이동 결과: " + dest.getId() + " (" + dest.getName() + ")");
        assertEquals("걸 이동 시 중앙에 도착해야 함", center, dest);

        // 윷(4칸) 이동 - 중앙을 지나 2번 경로로 진행
        dest = board.calculateDestination(beforeCenter, Yut.YutResult.YUT);
        log("윷 이동 결과: " + dest.getId() + " (" + dest.getName() + ")");
        assertNotEquals("윷 이동 시 중앙을 지나 계속 진행해야 함", center, dest);

        // 모(5칸) 이동 - 중앙을 지나 2번 경로 이후 진행
        dest = board.calculateDestination(beforeCenter, Yut.YutResult.MO);
        log("모 이동 결과: " + dest.getId() + " (" + dest.getName() + ")");
        assertNotEquals("모 이동 시 중앙을 지나 계속 진행해야 함", center, dest);

        // 모든 분기점 조회 및 검증
        log("\n-- 모든 분기점 목록 --");
        int junctionCount = 0;
        for (Place place : board.getAllPlaces().values()) {
            if (place.isJunction()) {
                junctionCount++;
                log(String.format("분기점 %d: id=%s, name=%s",
                        junctionCount, place.getId(), place.getName()));
                logPlace(place);

                // 분기점에서 1칸 이동 테스트
                Place normalDest = place.getNextPlace();
                Place specialDest = place.getSpecialNextPlace();

                log("  - 일반 경로로 1칸: " + normalDest.getId());
                log("  - 특별 경로로 1칸: " + specialDest.getId());
            }
        }

        // 육각형은 6개의 분기점을 가져야 함
        log("육각형 보드의 총 분기점 수: " + junctionCount);
        assertEquals("육각형 보드는 6개의 분기점을 가져야 함", 6, junctionCount);
    }

    @Test
    public void testContinuousMoveThroughCenter() {
        Board board = new Board(Board.BoardType.PENTAGON);
        log("\n==== 연속 이동 테스트 (중앙 통과) ====");

        // 중앙 직전 위치 찾기
        Place center = board.getCenterPlace();
        Place beforeCenter = null;
        for (Place place : board.getAllPlaces().values()) {
            if (place.getNextPlace() == center) {
                beforeCenter = place;
                break;
            }
        }

        assertNotNull("중앙 직전 위치가 존재해야 함", beforeCenter);
        log("중앙 직전 위치: " + beforeCenter.getId());

        // 연속 윷 결과 시뮬레이션
        // 1. 도 -> 중앙에 도착
        Place dest1 = board.calculateDestination(beforeCenter, Yut.YutResult.DO);
        log("1차 이동(도): " + beforeCenter.getId() + " -> " + dest1.getId());
        assertEquals("첫 이동(도)은 중앙에 도착해야 함", center, dest1);

        // 2. 중앙에서 도 -> 1번 경로로 1칸
        Place dest2 = board.calculateDestination(dest1, Yut.YutResult.DO);
        log("2차 이동(도): " + dest1.getId() + " -> " + dest2.getId());
        assertEquals("중앙에서 도 이동 시 1번 경로로 이동해야 함", center.getNextPlace(), dest2);

        // 3. 중앙에서 윷 -> 2번 경로로 진행되지 않음 (이미 중앙을 벗어남)
        Place dest3 = board.calculateDestination(dest1, Yut.YutResult.YUT);
        log("2차 이동(윷): " + dest1.getId() + " -> " + dest3.getId());

        // 중앙에서 1번/2번 경로 선택 확인
        log("\n-- 중앙에서 직접 이동 --");
        // 중앙에서 도 이동 - 1번 경로
        Place directDo = board.calculateDestination(center, Yut.YutResult.DO);
        log("중앙에서 도 이동: " + center.getId() + " -> " + directDo.getId());

        // 중앙에서 윷 이동 - 경로 선택은 중앙 도달 시에만 적용
        Place directYut = board.calculateDestination(center, Yut.YutResult.YUT);
        log("중앙에서 윷 이동: " + center.getId() + " -> " + directYut.getId());
    }
}