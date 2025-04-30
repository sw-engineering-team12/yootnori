package org.example.model;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * 윷놀이 게임 보드 클래스
 * 사각형, 오각형, 육각형 보드 형태를 지원합니다.
 */
public class Board {
    private BoardType boardType; // 사각형, 오각형, 육각형
    private Map<String, Place> places; // ID로 접근 가능한 모든 위치
    private Place startingPlace; // 시작 위치
    private Place endingPlace; // 도착 위치
    private Place centerPlace; // 중앙 위치

    /**
     * 보드 타입 열거형
     */
    public enum BoardType {
        SQUARE, PENTAGON, HEXAGON
    }

    /**
     * 생성자
     * @param boardType 보드의 형태(사각형, 오각형, 육각형)
     */
    public Board(BoardType boardType) {
        this.boardType = boardType;
        this.places = new HashMap<>();

        // 보드 초기화
        initializeBoard();
    }

    /**
     * 보드 타입에 따라 보드 초기화
     */
    private void initializeBoard() {
        switch (boardType) {
            case SQUARE:
                initializeSquareBoard();
                break;
            case PENTAGON:
                initializePentagonBoard();
                break;
            case HEXAGON:
                initializeHexagonBoard();
                break;
        }
    }
    /**
     * 사각형 보드 초기화 메서드
     * 일반적인 윷놀이판 구조를 생성합니다.
     */
    private void initializeSquareBoard() {
        // 시작/도착 지점 생성
        Place start = createPlace("S", "시작점", false, false, true, false);
        startingPlace = start;
        endingPlace = start; // 시작점과 동일

        // 외곽 경로 생성 (인덱스 1~19 사용)
        Place[] outerPath = new Place[20];
        for (int i = 1; i < 20; i++) {
            String id = "O" + i;
            String name = "외곽" + (i + 1);
            outerPath[i] = createPlace(id, name);
        }

        // 외곽 경로 연결
        for (int i = 1; i < 19; i++) {
            outerPath[i].setNextPlace(outerPath[i + 1]);
        }
        outerPath[19].setNextPlace(start); // 마지막 위치는 시작점으로 연결
        start.setNextPlace(outerPath[1]);

        // 중앙 지점 생성
        Place center = createPlace("C", "중앙", false, true, false, false);
        centerPlace = center;

        // 중앙에서 나가는 경로 1 (C5, C6)
        Place C5 = createPlace("C5", "C5");
        Place C6 = createPlace("C6", "C6");

        // 중앙에서 나가는 경로 2 (C7, C8)
        Place C7 = createPlace("C7", "C7");
        Place C8 = createPlace("C8", "C8");

        // 중앙에서 두 가지 경로 설정
        // - 중앙에 도달한 경우(도/개/걸): C7->C8 경로 사용
        // - 중앙을 지나는 경우(윷/모): C5->C6 경로 사용
        center.setNextPlace(C7); // 기본 경로 (중앙 도달 시)
        center.setSpecialNextPlace(C5); // 특별 경로 (중앙 통과 시)

        // 경로 1 연결 (C5->C6->O15)
        C5.setNextPlace(C6);
        C6.setNextPlace(outerPath[15]); // O15로 연결

        // 경로 2 연결 (C7->C8->O15)
        C7.setNextPlace(C8);
        C8.setNextPlace(outerPath[15]); // O15로 연결

        // O5에서 중앙으로 가는 경로 (O5->C1->C2->중앙)
        Place C1 = createPlace("C1", "C1");
        Place C2 = createPlace("C2", "C2");
        outerPath[5].setSpecialNextPlace(C1);
        outerPath[5].setJunction(true);
        C1.setNextPlace(C2);
        C2.setNextPlace(center);

        // O10에서 중앙으로 가는 경로 (O10->C3->C4->중앙)
        Place C3 = createPlace("C3", "C3");
        Place C4 = createPlace("C4", "C4");
        outerPath[10].setSpecialNextPlace(C3);
        outerPath[10].setJunction(true);
        C3.setNextPlace(C4);
        C4.setNextPlace(center);
    }
    /**
     * 오각형 보드 초기화 메서드
     * 오각형 구조의 윷놀이판을 생성합니다.
     */
    private void initializePentagonBoard(){}
    /**
     * 육각형 보드 초기화 메서드
     * 육각형 구조의 윷놀이판을 생성합니다.
     */
    private void initializeHexagonBoard(){

    }
    /**
     * 위치 생성 및 맵에 추가하는 유틸리티 메서드
     */
    private Place createPlace(String id, String name, boolean isJunction, boolean isCenter,
                              boolean isStartingPoint, boolean isEndingPoint) {
        Place place = new Place(id, name, isJunction, isCenter, isStartingPoint, isEndingPoint);
        places.put(id, place);
        return place;
    }

    /**
     * 간소화된 위치 생성 유틸리티 메서드
     */
    private Place createPlace(String id, String name) {
        return createPlace(id, name, false, false, false, false);
    }

    /**
     * ID로 위치 찾기
     * @param id 위치 ID
     * @return 해당 ID의 위치 객체
     */
    public Place getPlaceById(String id) {
        return places.get(id);
    }

    /**
     * 시작 위치 반환
     * @return 시작 위치
     */
    public Place getStartingPlace() {
        return startingPlace;
    }

    /**
     * 도착 위치 반환
     * @return 도착 위치
     */
    public Place getEndingPlace() {
        return endingPlace;
    }

    /**
     * 중앙 위치 반환
     * @return 중앙 위치
     */
    public Place getCenterPlace() {
        return centerPlace;
    }

    /**
     * 현재 보드 타입 반환
     * @return 보드 타입
     */
    public BoardType getBoardType() {
        return boardType;
    }

    /**
     * 모든 위치 정보 반환
     * @return 위치 맵
     */
    public Map<String, Place> getAllPlaces() {
        return new HashMap<>(places);
    }

    /**
     * 윷 결과와 현재 위치에 따라 이동 가능한 목적지 계산
     * @param currentPlace 현재 위치
     * @param yutResult 윷 결과
     * @return 이동 가능한 목적지
     */
    public Place calculateDestination(Place currentPlace, Yut.YutResult yutResult) {
        int moveCount = yutResult.getMoveCount();
        Place currentPos = currentPlace;

        // 시작 전이면 시작 위치에서 시작
        if (currentPos == null) {
            currentPos = startingPlace;
        }

        // 빽도 케이스
        if (moveCount < 0) {
            // 시작점에서는 빽도가 적용되지 않음
            if (currentPos.isStartingPoint()) {
                System.out.println("[DEBUG] 시작점에서 빽도: 제자리");
                return currentPos;
            }

            // 실제 구현에서는 이전 위치를 찾는 로직이 필요
            System.out.println("[DEBUG] 빽도 적용 (미구현): 제자리");
            return currentPos;
        }

        // 디버그 로그
        System.out.println("[DEBUG] 이동 시작: " + currentPos.getId() + " (" + currentPos.getName() + ")");
        System.out.println("[DEBUG] 윷 결과: " + yutResult.getName() + " (이동 칸수: " + moveCount + ")");

        // 중앙 도달 여부를 추적하는 변수
        boolean willReachCenter = false;
        boolean passedCenter = false;

        // O5와 O10 출발 지점 구분을 위한 변수
        boolean startedFromO5 = "O5".equals(currentPos.getId());
        boolean startedFromO10 = "O10".equals(currentPos.getId());

        // 중앙에 도달하는지 미리 확인
        Place tempPos = currentPos;
        for (int i = 0; i < moveCount; i++) {
            if (tempPos.isJunction() && tempPos.hasSpecialNextPlace() && i == 0) {
                tempPos = tempPos.getSpecialNextPlace(); // 첫 이동에서 특별 경로 선택
            } else if (tempPos.getNextPlace() != null) {
                tempPos = tempPos.getNextPlace();
            }

            // 이동 중에 중앙에 도달하는지 확인
            if (tempPos == centerPlace) {
                if (i == moveCount - 1) {
                    // 정확히 중앙에 도착
                    willReachCenter = true;
                    System.out.println("[DEBUG] 예측: 이동 후 중앙에 정확히 도착 예정");
                } else {
                    // 중앙을 지나침
                    passedCenter = true;
                    System.out.println("[DEBUG] 예측: 이동 중 중앙을 지나갈 예정");
                }
            }
        }

        // 실제 이동
        for (int i = 0; i < moveCount; i++) {
            System.out.println("[DEBUG] 단계 " + (i+1) + "/" + moveCount + " - 현재 위치: " + currentPos.getId());

            // 첫 이동에서 분기점이면서 특별 다음 위치가 있는 경우 (대각선 진입)
            if (i == 0 && currentPos.isJunction() && currentPos.hasSpecialNextPlace()) {
                System.out.println("[DEBUG] 분기점에서 특별 경로 선택: " + currentPos.getId() + " -> " +
                        currentPos.getSpecialNextPlace().getId());
                currentPos = currentPos.getSpecialNextPlace();
            }
            // 중앙에 도달하는 경우 - 다음 위치가 중앙이면서 아직 이동할 칸이 남은 경우
            else if (currentPos.getNextPlace() == centerPlace && i < moveCount - 1) {
                // 중앙으로 이동
                currentPos = centerPlace;
                System.out.println("[DEBUG] 중앙에 도달: " + centerPlace.getId());

                // 중앙을 지나치는 경우 - O10에서 시작한 경우 항상 기본 경로(C7)로,
                // O5에서 시작한 경우만 특별 경로(C5)로
                i++; // 이미 한 칸 이동했으므로 증가

                if (startedFromO10) {
                    // O10에서 출발했다면 항상 C7(기본 경로) 사용
                    currentPos = centerPlace.getNextPlace();
                    System.out.println("[DEBUG] O10 출발 중앙 통과 - 기본 경로 선택: " + currentPos.getId());
                } else if (startedFromO5) {
                    // O5에서 출발했다면 중앙 통과 시 C5(특별 경로) 사용
                    currentPos = centerPlace.getSpecialNextPlace();
                    System.out.println("[DEBUG] O5 출발 중앙 통과 - 특별 경로 선택: " + currentPos.getId());
                } else {
                    // 다른 위치에서 출발한 경우 기본적으로 특별 경로(C5) 사용
                    currentPos = centerPlace.getSpecialNextPlace();
                    System.out.println("[DEBUG] 중앙 통과 - 특별 경로 선택: " + currentPos.getId());
                }
            }
            // 중앙에 정확히 도착하는 경우
            else if (currentPos.getNextPlace() == centerPlace && i == moveCount - 1) {
                currentPos = centerPlace;
                System.out.println("[DEBUG] 정확히 중앙에 도착 (이동 종료): " + centerPlace.getId());
            }
            // 일반 이동
            else {
                if (currentPos.getNextPlace() != null) {
                    System.out.println("[DEBUG] 일반 이동: " + currentPos.getId() + " -> " + currentPos.getNextPlace().getId());
                    currentPos = currentPos.getNextPlace();
                } else {
                    System.out.println("[DEBUG] 더 이상 이동할 수 없음");
                    break; // 더 이상 이동할 수 없음
                }
            }
        }

        System.out.println("[DEBUG] 최종 도착 위치: " + currentPos.getId() + " (" + currentPos.getName() + ")");
        return currentPos;
    }

    /**
     * 두 위치 간의 최단 이동 경로 계산
     * (이 기능은 UI에서 경로 표시에 활용할 수 있음)
     * @param from 시작 위치
     * @param to 도착 위치
     * @return 경로 상의 위치 목록
     */
    public List<Place> calculatePath(Place from, Place to) {
        List<Place> path = new ArrayList<>();
        path.add(from);

        // 간단한 구현: 다음 위치로 계속 이동하면서 목적지에 도달하는지 확인
        // 실제 구현에서는 BFS나 다익스트라 알고리즘을 사용하여 최단 경로 탐색 필요
        Place current = from;
        while (current != to && path.size() < 50) { // 무한 루프 방지
            if (current.getNextPlace() != null) {
                current = current.getNextPlace();
                path.add(current);
            } else {
                break;
            }
        }

        return path;
    }

    /**
     * 보드의 모든 위치 정보를 문자열로 반환
     * @return 보드 정보 문자열
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Board Type: ").append(boardType).append("\n");
        sb.append("Total Places: ").append(places.size()).append("\n");
        sb.append("Starting Place: ").append(startingPlace.getName()).append("\n");
        sb.append("Center Place: ").append(centerPlace.getName()).append("\n");

        return sb.toString();
    }
}