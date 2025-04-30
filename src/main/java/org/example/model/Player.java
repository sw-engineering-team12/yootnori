package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 윷놀이 게임 플레이어 클래스
 */
public class Player {
    private String id;
    private String name;
    private List<Piece> pieces;

    /**
     * 기본 생성자
     */
    public Player() {
        this.pieces = new ArrayList<>();
    }

    /**
     * 이름으로 플레이어 생성
     * @param name 플레이어 이름
     */
    public Player(String name) {
        this.name = name;
        this.id = name.replaceAll("\\s+", "_").toLowerCase();
        this.pieces = new ArrayList<>();
    }

    /**
     * 말 추가
     * @param piece 추가할 말
     */
    public void addPiece(Piece piece) {
        if (piece != null && !pieces.contains(piece)) {
            pieces.add(piece);
        }
    }

    /**
     * 플레이어의 모든 말 반환
     * @return 말 목록
     */
    public List<Piece> getPieces() {
        return new ArrayList<>(pieces);
    }

    /**
     * 이동 가능한 말 목록 반환
     * @return 이동 가능한 말 목록
     */
    public List<Piece> getMovablePieces() {
        List<Piece> movablePieces = new ArrayList<>();
        for (Piece piece : pieces) {
            if (!piece.isCompleted()) {
                movablePieces.add(piece);
            }
        }
        return movablePieces;
    }

    /**
     * ID로 말 찾기
     * @param pieceId 찾을 말의 ID
     * @return 해당 ID의 말 객체
     */
    public Piece getPieceById(String pieceId) {
        for (Piece piece : pieces) {
            if (piece.getId().equals(pieceId)) {
                return piece;
            }
        }
        return null;
    }

    /**
     * 모든 말이 완주했는지 확인
     * @return 모든 말이 완주했으면 true
     */
    public boolean isAllPiecesCompleted() {
        for (Piece piece : pieces) {
            if (!piece.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 완주한 말의 개수 반환
     * @return 완주한 말 개수
     */
    public int getCompletedPieceCount() {
        int count = 0;
        for (Piece piece : pieces) {
            if (piece.isCompleted()) {
                count++;
            }
        }
        return count;
    }

    // 게터 메서드
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Player{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", pieces=" + pieces.size() +
                '}';
    }
}