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
    private void initializeSquareBoard(){
        // 시작/도착 지점 생성
        Place start = createPlace("S", "시작점", false, false, true, false);
        startingPlace = start;
        endingPlace = start; // 시작점과 동일

        Place[] outerPath = new Place[20];
        for(int i =1; i<20; i++){
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
    public Place calculateDestination(Place currentPlace, Yut.YutResult yutResult){
        return currentPlace;
    }

    /**
     * 두 위치 간의 최단 이동 경로 계산
     * (이 기능은 UI에서 경로 표시에 활용할 수 있음)
     * @param from 시작 위치
     * @param to 도착 위치
     * @return 경로 상의 위치 목록
     */
    public List<Place> calculatePath(Place from, Place to){
        return null;
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