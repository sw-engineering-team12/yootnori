package org.example.model;

import java.util.ArrayList;

public class Piece {
    private String id;
    private Player player;
    private Boolean isComplete;
    private Boolean isOwnhand;
    private Place currentPlace;
    private ArrayList<Piece> stackPieces;

    //생성자
    public Piece(String id, Player player) {
        this.id = id;
        this.player = player;
        this.isComplete = false;
        this.isOwnhand = true;
        this.stackPieces = new ArrayList<Piece>();
    }

    // 말 움직임
    public boolean moveTo(Place newPlace) {
        // 현재 위치에서 제거
        if (currentPlace != null) {
            currentPlace.removePiece(this);
        }

        // 새 위치로 이동
        this.currentPlace = newPlace;
        if (newPlace != null) {
            newPlace.addPiece(this);

            // 도착점에 도달했는지 확인 메서드 필요
            // 도착점에 도달했는지 확인
            if (newPlace.isEndingPoint()) {
                this.isComplete = true;
            }

        }
        return true;
    }

    // 말 업기
    public void stackPiece(Piece piece) {
        if (!this.equals(piece) && !this.stackPieces.contains(piece)) {
            // 업힐 말이 이미 다른 말을 업고 있다면 모두 함께 업기
            if (!piece.stackPieces.isEmpty()) {
                this.stackPieces.addAll(piece.stackPieces);
                piece.stackPieces.clear();
            }
            this.stackPieces.add(piece);

            // 업힌 말의 위치 정보 변경
            if (piece.currentPlace != null) {
                piece.currentPlace.removePiece(piece);
            }
            piece.currentPlace = null;
        }
    }
    public void unstackAllPieces() {
        if (currentPlace != null && !stackPieces.isEmpty()) {
            for (Piece piece : stackPieces) {
                piece.moveTo(currentPlace);
            }
            stackPieces.clear();
        }
    }

    // getter 메서드들
    public String getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public Place getCurrentPlace() {
        return currentPlace;
    }

    public boolean isCompleted() {
        return isComplete;
    }

    public ArrayList<Piece> getStackedPieces() {
        return new ArrayList<>(stackPieces);
    }

    public int getTotalPieceCount() {
        return 1 + stackPieces.size();
    }


    // 말 잡기
    //말 꺼내기
    //말 골인


}
