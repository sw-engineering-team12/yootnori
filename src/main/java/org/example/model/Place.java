package org.example.model;

import java.util.ArrayList;

public class Place {
    private String id;
    private String name;
    private boolean isJunction;      // 분기점 여부
    private boolean isCenter;        // 중앙점 여부
    private boolean isStartingPoint; // 시작점 여부
    private boolean isEndingPoint;   // 도착점 여부

    private Place nextPlace;         // 기본 다음 위치
    private Place specialNextPlace;  // 특별 이동 경로 (예: 대각선 이동)

    private ArrayList<Piece> pieces;      // 현재 이 위치에 있는 말들

    // 생성자, getter, setter 메서드

    // 말을 이 위치에 추가하는 메서드
    public void addPiece(Piece piece) {
        pieces.add(piece);
    }

    // 말을 이 위치에서 제거하는 메서드
    public boolean removePiece(Piece piece) {
        return pieces.remove(piece);
    }

    // 특정 플레이어의 말이 있는지 확인하는 메서드
    public boolean hasPlayerPieces(Player player) {
        return false;
    }

    // 다른 플레이어의 말이 있는지 확인하는 메서드
    public ArrayList<Piece> getOpponentPieces(Player currentPlayer) {
        ArrayList<Piece> opponentPieces = new ArrayList<>();
        //business logic
        return opponentPieces;
    }
}

// Q. 말의 대각선 이동은 어디서 고려해야하는가?
// 1. 보드
// 2. 게임
// 3. Place
// 일단은 보드나 게임쪽으로 넘기는 걸로