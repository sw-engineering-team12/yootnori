package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Piece {
    private String id;
    private Player player;
    private Boolean isComplete;
    private Place currentPlace;
    private ArrayList<Piece> stackPieces;

    //생성자
    public Piece(String id, Player player) {
        this.id = id;
        this.player = player;
        this.isComplete = false;
        this.stackPieces = new ArrayList<Piece>();
    }

    /**
     * 말 움직임
     * @param newPlace 새 위치
     * @return 이동 성공 여부
     */
    public boolean moveTo(Place newPlace) {
        // 현재 위치에서 제거
        if(currentPlace!=null) {
            currentPlace.removePiece(this);
        }
        // 새 위치로 이동
        this.currentPlace = newPlace;
        if (newPlace != null) {
            newPlace.addPiece(this);

            // 도착점에 도달했는지 확인 메서드 필요
            // 도착점에 도달했는지 확인
//            if (newPlace.isEndingPoint()) {
//                this.isComplete = true;
//            }
        }
        return true;
    }
    /**
     * 다른 말을 업는 메서드
     * @param piece 업힐 말
     * @return 업기 성공 여부
     */
    public boolean stackPiece(Piece piece) {
        // 자기 자신이거나 이미 업힌 말이면 업기 불가
        if (this.equals(piece) || this.stackPieces.contains(piece)) {
            return false;
        }

        // 업힐 말이 이미 다른 말을 업고 있다면 모두 함께 업기
        if (!piece.stackPieces.isEmpty()) {
            // 업힐 말이 업고 있는 모든 말을 가져옴 (복사본 생성 - ConcurrentModification 방지)
            ArrayList<Piece> piecesToStack = new ArrayList<>(piece.stackPieces);

            // 모든 업힌 말들을 현재 말에 추가
            for (Piece stackedPiece : piecesToStack) {
                this.stackPieces.add(stackedPiece);
                stackedPiece.currentPlace = null; // 위치 정보 제거
            }

            // 원래 업힌 말들의 목록 초기화
            piece.stackPieces.clear();
        }

        // 새로운 말 추가
        this.stackPieces.add(piece);

        // 업힌 말의 위치 정보 변경
        if (piece.currentPlace != null) {
            piece.currentPlace.removePiece(piece);
            piece.currentPlace = null;
        }

        return true;
    }

    // 업힌 말 모두 해제
// 업힌 말 모두 해제
    public void unstackAllPieces() {
        if (!stackPieces.isEmpty()) {
            // 현재 위치가 있을 경우에만 업힌 말들을 해당 위치로 이동
            if (currentPlace != null) {
                // ConcurrentModificationException 방지를 위해 복사본 사용
                List<Piece> stackedPiecesCopy = new ArrayList<>(stackPieces);

                for (Piece piece : stackedPiecesCopy) {
                    // 현재 말과 같은 위치로 이동
                    piece.moveTo(currentPlace);
                }
            }

            // 업힌 말 목록 명시적으로 초기화 - 이 부분 추가
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

    @Override
    public String toString() {
        return "Piece{" +
                "id='" + id + '\'' +
                ", player=" + player.getName() +
                ", isComplete=" + isComplete +
                ", stackedPieces=" + stackPieces.size() +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Piece other = (Piece) obj;
        return id.equals(other.id) && player.equals(other.player);
    }
    // Piece.java에 추가
    public void clearStackedPieces() {
        stackPieces.clear();
    }

    @Override
    public int hashCode() {
        return id.hashCode() + 31 * player.hashCode();
    }
}