package org.example.model;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * 윷놀이 게임 보드 클래스
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

    // 사각형 보드 초기화 메서드
    private void initializeSquareBoard() {
        // 위치 생성 및 연결
        // ...
    }

    // 오각형 보드 초기화 메서드
    private void initializePentagonBoard() {
        // 위치 생성 및 연결
        // ...
    }

    // 육각형 보드 초기화 메서드
    private void initializeHexagonBoard() {
        // 위치 생성 및 연결
        // ...
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
     * 윷 결과와 현재 위치에 따라 이동 가능한 목적지 계산
     * @param currentPlace 현재 위치
     * @param yutResult 윷 결과
     * @return 이동 가능한 목적지
     */
    public Place calculateDestination(Place currentPlace, Yut.YutResult yutResult) {
        int moveCount = yutResult.getMoveCount();
        Place currentPos = currentPlace;

        // 빽도 케이스
        if (moveCount < 0) {
            return currentPos.getNextPlace();
        }

        // 이동 계산
        for (int i = 0; i < moveCount; i++) {
            // 첫 이동에서 분기점이면서 특별 다음 위치가 있는 경우
            if (i == 0 && currentPos.isJunction() && currentPos.hasSpecialNextPlace()) {
                currentPos = currentPos.getSpecialNextPlace();
            }
            // 중앙점에 도달하는 경우 (오각형, 육각형 보드 규칙)
            else if (i == moveCount - 1 && currentPos.getNextPlace() == centerPlace &&
                    (boardType == BoardType.PENTAGON || boardType == BoardType.HEXAGON)) {
                // 도, 개, 걸 - 중앙에 정지
                if (moveCount <= 3) {
                    currentPos = centerPlace;
                }
                // 윷, 모 - 두 번째 가까운 경로로 진행
                else {
                    // 구현 필요: 두 번째 경로 선택 로직
                }
            }
            // 일반 이동
            else {
                if (currentPos.getNextPlace() != null) {
                    currentPos = currentPos.getNextPlace();
                } else {
                    break; // 더 이상 이동할 수 없음
                }
            }
        }

        return currentPos;
    }
}