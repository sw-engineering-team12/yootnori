package org.example.model;

import java.util.ArrayList;
import java.util.List;

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

            // 도착점에 도달했는지 확인
            if (newPlace.isEndingPoint()) {
                this.isComplete = true;
            }
        }

        // 업힌 말들도 함께 이동 (위치만 업데이트, 실제 보드에는 추가 안함)
        for (Piece stackedPiece : stackPieces) {
            // 업힌 말의 위치 정보만 업데이트 (보드에는 추가하지 않음)
            stackedPiece.currentPlace = newPlace;

            // 도착점이면 완주 상태 업데이트
            if (newPlace != null && newPlace.isEndingPoint()) {
                stackedPiece.isComplete = true;
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

            // 업힐 말을 현재 위치에서 제거
            if (piece.currentPlace != null) {
                piece.currentPlace.removePiece(piece);
            }

            // 업힌 상태로 추가
            this.stackPieces.add(piece);

            // 업힌 말의 위치는 null로 설정 (보드에서 제거)
            piece.currentPlace = null;
        }
    }

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

                // 업힌 말 목록 초기화
                stackPieces.clear();
            }
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

    @Override
    public int hashCode() {
        return id.hashCode() + 31 * player.hashCode();
    }
}