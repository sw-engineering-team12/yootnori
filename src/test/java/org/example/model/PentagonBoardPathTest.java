package org.example.model;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * 오각형 보드의 경로 순회 및 특별 위치에서의 순회 방법 테스트
 */
public class PentagonBoardPathTest {
    private Board pentagonBoard;

    @Before
    public void setUp() {
        // 오각형 보드 초기화
        pentagonBoard = new Board(Board.BoardType.PENTAGON);
    }

    /**
     * 외곽 경로 순회 테스트 (로깅 추가)
     * 시작점에서 도착점까지 전체 경로를 따라 순회하면서
     * 각 위치의 일반 경로와 특별 경로를 함께 로깅
     */
    @Test
    public void testOuterPathCircuit() {
        // 시작점에서 출발
        Place current = pentagonBoard.getStartingPlace();
        List<String> pathIds = new ArrayList<>();
        pathIds.add(current.getId());

        System.out.println("==== 오각형 보드 외곽 경로 순회 테스트 (로깅) ====");
        System.out.println("현재 위치: " + current.getId() + " (" + current.getName() + ")");
        logPathInfo(current);

        // 외곽 경로를 따라 도착점까지 순회
        while (current != null && !current.equals(pentagonBoard.getEndingPlace())) {
            current = current.getNextPlace();
            if (current != null) {
                pathIds.add(current.getId());
                System.out.println("현재 위치: " + current.getId() + " (" + current.getName() + ")");
                logPathInfo(current);
            }
        }

        // 예상 경로 (S -> 1 -> 2 -> ... -> 24 -> E)
        List<String> expectedPath = new ArrayList<>();
        expectedPath.add("S");
        for (int i = 1; i <= 24; i++) {
            expectedPath.add(String.valueOf(i));
        }
        expectedPath.add("E");

        // 경로 검증
        assertEquals("외곽 경로 순회가 올바르지 않습니다", expectedPath, pathIds);

        // 도착점에 정상적으로 도달했는지 확인
        assertEquals("도착점에 도달하지 못했습니다", pentagonBoard.getEndingPlace(), current);

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
        Place current = pentagonBoard.getPlaceById("5");
        List<String> pathIds = new ArrayList<>();
        pathIds.add(current.getId());

        System.out.println("==== 오각형 보드 분기점 -> 중앙 -> 외곽 경로 순회 테스트 (로깅) ====");
        System.out.println("현재 위치: " + current.getId() + " (" + current.getName() + ")");
        logPathInfo(current);

        // 특별 경로로 이동
        current = current.getSpecialNextPlace(); // C1
        pathIds.add(current.getId());
        System.out.println("현재 위치: " + current.getId() + " (" + current.getName() + ") [특별 경로 사용]");
        logPathInfo(current);

        // 특별 경로를 따라 순회
        while (current != null && !current.getId().equals("20")) { // 20에 도달할 때까지 이동
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
     * 두 번째 분기점(10)에서 특별 경로를 통한 순회 테스트
     */
    @Test
    public void testSecondJunctionPathLogging() {
        // 분기점(10)에서 출발
        Place current = pentagonBoard.getPlaceById("10");
        List<String> pathIds = new ArrayList<>();
        pathIds.add(current.getId());

        System.out.println("==== 오각형 보드 두 번째 분기점 경로 순회 테스트 (로깅) ====");
        System.out.println("현재 위치: " + current.getId() + " (" + current.getName() + ")");
        logPathInfo(current);

        // 특별 경로로 이동
        current = current.getSpecialNextPlace(); // C3
        pathIds.add(current.getId());
        System.out.println("현재 위치: " + current.getId() + " (" + current.getName() + ") [특별 경로 사용]");
        logPathInfo(current);

        // 중앙점까지 이동
        while (current != null && !current.isCenter()) {
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
     * 중앙점(C_1)에서 특별 경로를 통한 순회 테스트
     * 중앙점 -> 특별 경로 -> 도착점으로 가는 경로 검증
     */
    @Test
    public void testCenterToSpecialPathCircuit() {
        // 중앙점(C_1)에서 출발
        Place current = pentagonBoard.getPlaceById("C_1");
        List<String> pathIds = new ArrayList<>();
        pathIds.add(current.getId());

        System.out.println("==== 오각형 보드 중앙점 -> 특별 경로 -> 도착점 테스트 (로깅) ====");
        System.out.println("현재 위치: " + current.getId() + " (" + current.getName() + ")");
        logPathInfo(current);

        // 특별 경로가 있을 경우 사용
        if (current.hasSpecialNextPlace()) {
            current = current.getSpecialNextPlace(); // 특별 경로 (C9)
            pathIds.add(current.getId());
            System.out.println("현재 위치: " + current.getId() + " (" + current.getName() + ") [특별 경로 사용]");
            logPathInfo(current);

            // 도착점에 도달할 때까지 이동
            while (current != null && !current.equals(pentagonBoard.getEndingPlace())) {
                current = current.getNextPlace();
                if (current != null) {
                    pathIds.add(current.getId());
                    System.out.println("현재 위치: " + current.getId() + " (" + current.getName() + ")");
                    logPathInfo(current);
                }
            }
        } else {
            // 일반 경로로 진행
            while (current != null && !current.equals(pentagonBoard.getEndingPlace())) {
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
     * 세 번째 분기점(15)에서 특별 경로 테스트
     */
    @Test
    public void testThirdJunctionCircuit() {
        // 분기점(15)에서 출발
        Place junction = pentagonBoard.getPlaceById("15");

        // 특별 경로가 있는지 확인
        if (junction.hasSpecialNextPlace()) {
            Place current = junction;
            List<String> pathIds = new ArrayList<>();
            pathIds.add(current.getId());

            System.out.println("==== 오각형 보드 세 번째 분기점 경로 테스트 (로깅) ====");
            System.out.println("현재 위치: " + current.getId() + " (" + current.getName() + ")");
            logPathInfo(current);

            // 특별 경로로 이동
            current = current.getSpecialNextPlace(); // C5
            pathIds.add(current.getId());
            System.out.println("현재 위치: " + current.getId() + " (" + current.getName() + ") [특별 경로 사용]");
            logPathInfo(current);

            // 중앙2까지 이동
            while (current != null && !current.getId().equals("C_2")) {
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
        } else {
            System.out.println("분기점 15에 특별 경로가 없습니다.");
        }
        System.out.println("==============================\n");
    }

    /**
     * 모든 윷 결과에 대해 특정 위치에서의 이동 로깅
     * 시작점, 일반 위치, 분기점, 중앙점 각각에서 테스트
     */
    @Test
    public void testAllYutResultsPathLogging() {
        Yut.YutResult[] allResults = {
                Yut.YutResult.BACKDO,
                Yut.YutResult.DO,
                Yut.YutResult.GAE,
                Yut.YutResult.GEOL,
                Yut.YutResult.YUT,
                Yut.YutResult.MO
        };

        // 테스트할 시작 위치들
        String[] startPositions = {"S", "3", "5", "10", "15", "C_1", "C_2"};

        System.out.println("==== 오각형 보드 모든 윷 결과에 대한 이동 경로 테스트 (로깅) ====");

        for (String posId : startPositions) {
            Place startPlace = pentagonBoard.getPlaceById(posId);
            System.out.println("\n=== 시작 위치: " + posId + " (" + startPlace.getName() + ") ===");
            logPathInfo(startPlace);

            for (Yut.YutResult result : allResults) {
                try {
                    Place destination = pentagonBoard.calculateDestination(startPlace, result);

                    System.out.println("\n윷 결과: " + result.getName() + " (" + result.getMoveCount() + "칸)");
                    System.out.println("  - 목적지: " + destination.getId() + " (" + destination.getName() + ")");

                    // 경로 재구성 (로직이 단순한 경우에만 작동)
                    List<String> path = new ArrayList<>();
                    path.add(startPlace.getId());

                    Place current = startPlace;
                    int steps = Math.abs(result.getMoveCount());
                    boolean usedSpecial = false;

                    // 빽도 처리 - 단순화를 위해 최종 결과만 표시
                    if (result == Yut.YutResult.BACKDO) {
                        path.add(destination.getId());
                        System.out.println("  - 경로: " + String.join(" -> ", path) + " [빽도]");
                        continue;
                    }

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
                } catch (Exception e) {
                    System.out.println("\n윷 결과: " + result.getName() + " (" + result.getMoveCount() + "칸)");
                    System.out.println("  - 오류 발생: " + e.getMessage());
                }
            }
        }

        System.out.println("\n==============================\n");
    }

    /**
     * 중앙점에서 도/개/걸 vs 윷/모에 따른 경로 선택 테스트
     */
    @Test
    public void testCenterPointPathSelection() {
        // 중앙1(C_1)에서 출발
        Place center = pentagonBoard.getPlaceById("C_1");

        System.out.println("==== 오각형 보드 중앙점 경로 선택 테스트 (로깅) ====");
        System.out.println("중앙1(C_1) 위치 정보:");
        logPathInfo(center);

        // 도(1칸) 이동: 일반 경로
        Place destination1 = pentagonBoard.calculateDestination(center, Yut.YutResult.DO);
        System.out.println("\n도(1칸) 결과:");
        System.out.println("  - 목적지: " + destination1.getId() + " (" + destination1.getName() + ")");

        // 개(2칸) 이동: 일반 경로 2칸
        Place destination2 = pentagonBoard.calculateDestination(center, Yut.YutResult.GAE);
        System.out.println("\n개(2칸) 결과:");
        System.out.println("  - 목적지: " + destination2.getId() + " (" + destination2.getName() + ")");

        // 걸(3칸) 이동: 일반 경로 3칸
        Place destination3 = pentagonBoard.calculateDestination(center, Yut.YutResult.GEOL);
        System.out.println("\n걸(3칸) 결과:");
        System.out.println("  - 목적지: " + destination3.getId() + " (" + destination3.getName() + ")");

        // 윷(4칸) 이동: 특별 경로
        Place destination4 = pentagonBoard.calculateDestination(center, Yut.YutResult.YUT);
        System.out.println("\n윷(4칸) 결과:");
        System.out.println("  - 목적지: " + destination4.getId() + " (" + destination4.getName() + ")");

        // 모(5칸) 이동: 특별 경로
        Place destination5 = pentagonBoard.calculateDestination(center, Yut.YutResult.MO);
        System.out.println("\n모(5칸) 결과:");
        System.out.println("  - 목적지: " + destination5.getId() + " (" + destination5.getName() + ")");

        // 중앙2(C_2)에서도 유사하게 테스트
        Place center2 = pentagonBoard.getPlaceById("C_2");
        System.out.println("\n중앙2(C_2) 위치 정보:");
        logPathInfo(center2);

        // 도(1칸) 이동
        Place destination1_2 = pentagonBoard.calculateDestination(center2, Yut.YutResult.DO);
        System.out.println("\n도(1칸) 결과:");
        System.out.println("  - 목적지: " + destination1_2.getId() + " (" + destination1_2.getName() + ")");

        // 윷(4칸) 이동
        Place destination4_2 = pentagonBoard.calculateDestination(center2, Yut.YutResult.YUT);
        System.out.println("\n윷(4칸) 결과:");
        System.out.println("  - 목적지: " + destination4_2.getId() + " (" + destination4_2.getName() + ")");

        System.out.println("\n==============================\n");
    }

    /**
     * 복합 경로 테스트: 여러 분기점과 중앙점을 지나는 경로
     */
    @Test
    public void testComplexPathCircuit() {
        System.out.println("==== 오각형 보드 복합 경로 테스트 (로깅) ====");

        // 시작점에서 출발하여 첫 번째 분기점까지
        Place start = pentagonBoard.getStartingPlace();
        System.out.println("시작점:");
        logPathInfo(start);

        Place junction1 = pentagonBoard.calculateDestination(start, Yut.YutResult.MO); // 5칸 이동 -> 5번 위치
        System.out.println("\n시작점 -> 첫 분기점 (모, 5칸):");
        System.out.println("  - 목적지: " + junction1.getId() + " (" + junction1.getName() + ")");
        logPathInfo(junction1);

        // 첫 번째 분기점에서 중앙으로
        Place center1 = pentagonBoard.calculateDestination(junction1, Yut.YutResult.GEOL); // 3칸 이동 -> C_1
        System.out.println("\n첫 분기점 -> 중앙 (걸, 3칸):");
        System.out.println("  - 목적지: " + center1.getId() + " (" + center1.getName() + ")");
        logPathInfo(center1);

        // 중앙에서 특별 경로로
        Place special = pentagonBoard.calculateDestination(center1, Yut.YutResult.YUT); // 4칸 이동 -> 특별 경로
        System.out.println("\n중앙 -> 특별 경로 (윷, 4칸):");
        System.out.println("  - 목적지: " + special.getId() + " (" + special.getName() + ")");
        logPathInfo(special);

        // 특별 경로에서 도착점까지
        Place end = pentagonBoard.calculateDestination(special, Yut.YutResult.GAE); // 2칸 이동
        System.out.println("\n특별 경로 -> 다음 위치 (개, 2칸):");
        System.out.println("  - 목적지: " + end.getId() + " (" + end.getName() + ")");
        logPathInfo(end);

        // 복합 경로 요약
        List<String> pathIds = new ArrayList<>();
        pathIds.add(start.getId());
        pathIds.add(junction1.getId());
        pathIds.add(center1.getId());
        pathIds.add(special.getId());
        pathIds.add(end.getId());

        System.out.println("\n==== 복합 경로 요약 ====");
        System.out.println("경로: " + String.join(" -> ", pathIds));
        System.out.println("==============================\n");
    }
}