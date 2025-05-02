package org.example.model;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * 사각형 보드의 경로 순회 테스트 (로깅 기능 추가)
 */
public class SquareBoardPathTest {
    private Board squareBoard;

    @Before
    public void setUp() {
        // 사각형 보드 초기화
        squareBoard = new Board(Board.BoardType.SQUARE);
    }

    /**
     * 외곽 경로 순회 테스트 (로깅 추가)
     * 시작점에서 도착점까지 전체 경로를 따라 순회하면서
     * 각 위치의 일반 경로와 특별 경로를 함께 로깅
     */
    @Test
    public void testOuterPathCircuit() {
        // 시작점에서 출발
        Place current = squareBoard.getStartingPlace();
        List<String> pathIds = new ArrayList<>();
        pathIds.add(current.getId());

        System.out.println("==== 외곽 경로 순회 테스트 (로깅) ====");
        System.out.println("현재 위치: " + current.getId() + " (" + current.getName() + ")");
        logPathInfo(current);

        // 외곽 경로를 따라 도착점까지 순회
        while (current != null && !current.equals(squareBoard.getEndingPlace())) {
            current = current.getNextPlace();
            if (current != null) {
                pathIds.add(current.getId());
                System.out.println("현재 위치: " + current.getId() + " (" + current.getName() + ")");
                logPathInfo(current);
            }
        }

        // 예상 경로 (S -> 1 -> 2 -> ... -> 19 -> E)
        List<String> expectedPath = new ArrayList<>();
        expectedPath.add("S");
        for (int i = 1; i <= 19; i++) {
            expectedPath.add(String.valueOf(i));
        }
        expectedPath.add("E");
        expectedPath.add("FE");

        // 경로 검증
        assertEquals("외곽 경로 순회가 올바르지 않습니다", expectedPath, pathIds);

        // 도착점에 정상적으로 도달했는지 확인
        assertEquals("도착점에 도달하지 못했습니다", squareBoard.getEndingPlace(), current);

        // 전체 경로 요약 출력
        System.out.println("\n==== 순회 경로 요약 ====");
        System.out.println("경로: " + String.join(" -> ", pathIds));
        System.out.println("총 위치 수: " + pathIds.size());
        System.out.println("==============================\n");
    }

    /**
     * 주어진 위치의 일반 경로 및 특별 경로 정보를 로깅
     * @param place 로깅할 위치
     */
    private void logPathInfo(Place place) {
        StringBuilder log = new StringBuilder();
        log.append("  - 속성: ");

        if (place.isStartingPoint()) log.append("시작점 ");
        if (place.isEndingPoint()) log.append("도착점 ");
        if (place.isJunction()) log.append("분기점 ");
        if (place.isCenter()) log.append("중앙점 ");

        log.append("\n  - 일반 경로: ");
        if (place.getNextPlace() != null) {
            log.append(place.getNextPlace().getId())
                    .append(" (").append(place.getNextPlace().getName()).append(")");
        } else {
            log.append("없음");
        }

        log.append("\n  - 특별 경로: ");
        if (place.hasSpecialNextPlace()) {
            log.append(place.getSpecialNextPlace().getId())
                    .append(" (").append(place.getSpecialNextPlace().getName()).append(")");
        } else {
            log.append("없음");
        }

        System.out.println(log.toString());
        System.out.println("------------------------------");
    }

    /**
     * 분기점(5)에서 시작하여 중앙을 거쳐 외곽으로 나가는 경로를 로깅
     */
    @Test
    public void testJunctionToCenterPathLogging() {
        // 분기점(5)에서 출발
        Place current = squareBoard.getPlaceById("5");
        List<String> pathIds = new ArrayList<>();
        pathIds.add(current.getId());

        System.out.println("==== 분기점 -> 중앙 -> 외곽 경로 순회 테스트 (로깅) ====");
        System.out.println("현재 위치: " + current.getId() + " (" + current.getName() + ")");
        logPathInfo(current);

        // 특별 경로로 이동
        current = current.getSpecialNextPlace(); // C1
        pathIds.add(current.getId());
        System.out.println("현재 위치: " + current.getId() + " (" + current.getName() + ") [특별 경로 사용]");
        logPathInfo(current);

        // 특별 경로를 따라 순회
        while (current != null && !current.getId().equals("15")) { // 15에 도달할 때까지 이동
            current = current.getNextPlace();
            if (current != null) {
                pathIds.add(current.getId());
                System.out.println("현재 위치: " + current.getId() + " (" + current.getName() + ")");
                logPathInfo(current);
            }
        }

        // 경로 요약 출력
        System.out.println("\n==== 순회 경로 요약 ====");
        System.out.println("경로: " + String.join(" -> ", pathIds));
        System.out.println("총 위치 수: " + pathIds.size());
        System.out.println("==============================\n");
    }

    /**
     * 중앙점(C_1)에서 특별 경로를 따라 도착점까지 로깅
     */
    @Test
    public void testCenterToFinishPathLogging() {
        // 중앙점(C_1)에서 출발
        Place current = squareBoard.getPlaceById("C_1");
        List<String> pathIds = new ArrayList<>();
        pathIds.add(current.getId());

        System.out.println("==== 중앙점 -> 도착점 경로 순회 테스트 (로깅) ====");
        System.out.println("현재 위치: " + current.getId() + " (" + current.getName() + ")");
        logPathInfo(current);

        // 특별 경로가 있을 경우 사용
        if (current.hasSpecialNextPlace()) {
            current = current.getSpecialNextPlace(); // 특별 경로 (C7)
            pathIds.add(current.getId());
            System.out.println("현재 위치: " + current.getId() + " (" + current.getName() + ") [특별 경로 사용]");
            logPathInfo(current);

            // 도착점에 도달할 때까지 이동
            while (current != null && !current.equals(squareBoard.getEndingPlace())) {
                current = current.getNextPlace();
                if (current != null) {
                    pathIds.add(current.getId());
                    System.out.println("현재 위치: " + current.getId() + " (" + current.getName() + ")");
                    logPathInfo(current);
                }
            }
        } else {
            // 일반 경로로 진행
            while (current != null && !current.equals(squareBoard.getEndingPlace())) {
                current = current.getNextPlace();
                if (current != null) {
                    pathIds.add(current.getId());
                    System.out.println("현재 위치: " + current.getId() + " (" + current.getName() + ")");
                    logPathInfo(current);
                }
            }
        }

        // 경로 요약 출력
        System.out.println("\n==== 순회 경로 요약 ====");
        System.out.println("경로: " + String.join(" -> ", pathIds));
        System.out.println("총 위치 수: " + pathIds.size());
        System.out.println("==============================\n");
    }

    /**
     * 모든 윷 결과에 대해 특정 위치에서의 이동 로깅
     * 시작점, 일반 위치, 분기점, 중앙점 각각에서 테스트
     */
    @Test
    public void testAllYutResultsPathLogging() {
        Yut.YutResult[] allResults = {
//                Yut.YutResult.BACKDO,
                Yut.YutResult.DO,
                Yut.YutResult.GAE,
                Yut.YutResult.GEOL,
                Yut.YutResult.YUT,
                Yut.YutResult.MO
        };

        // 테스트할 시작 위치들
        String[] startPositions = {"S", "3", "5", "10", "C_1", "C_2"};

        System.out.println("==== 모든 윷 결과에 대한 이동 경로 테스트 (로깅) ====");

        for (String posId : startPositions) {
            Place startPlace = squareBoard.getPlaceById(posId);
            System.out.println("\n=== 시작 위치: " + posId + " (" + startPlace.getName() + ") ===");
            logPathInfo(startPlace);

            for (Yut.YutResult result : allResults) {
                Place destination = squareBoard.calculateDestination(startPlace, result);

                System.out.println("\n윷 결과: " + result.getName() + " (" + result.getMoveCount() + "칸)");
                System.out.println("  - 목적지: " + destination.getId() + " (" + destination.getName() + ")");

                // 경로 재구성 (로직이 단순한 경우에만 작동)
                List<String> path = new ArrayList<>();
                path.add(startPlace.getId());

                Place current = startPlace;
                int steps = Math.abs(result.getMoveCount());
                boolean usedSpecial = false;

                // 빽도 처리
//                if (result == Yut.YutResult.BACKDO) {
//                    path.add(destination.getId());
//                    System.out.println("  - 경로: " + String.join(" -> ", path) + " [빽도]");
//                    continue;
//                }

                // 분기점에서 특별 경로를 사용하는 경우
                if (current.isJunction() && current.hasSpecialNextPlace()) {
                    current = current.getSpecialNextPlace();
                    path.add(current.getId());
                    usedSpecial = true;
                    steps--;
                }

                // 남은 이동
                for (int i = 0; i < steps && current != null; i++) {
                    if (current.getNextPlace() != null) {
                        current = current.getNextPlace();
                        path.add(current.getId());
                    }
                }

                System.out.println("  - 경로: " + String.join(" -> ", path) +
                        (usedSpecial ? " [특별 경로 사용]" : ""));
            }
        }

        System.out.println("\n==============================\n");
    }
}