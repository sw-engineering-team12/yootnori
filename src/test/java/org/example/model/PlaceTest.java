package org.example.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

/**
 * Place 클래스에 대한 단위 테스트
 */
public class PlaceTest {

    private Place place;
    private Player player1;
    private Player player2;
    private Piece piece1;
    private Piece piece2;

    @Before
    public void setUp() {
        // 테스트용 Place 객체 생성
        place = new Place("p1", "시작점", false, false, true, false);

        // 테스트용 Player 객체 생성
        player1 = new Player("Player 1");
        player2 = new Player("Player 2");

        // 테스트용 Piece 객체 생성
        piece1 = new Piece("piece1", player1);
        piece2 = new Piece("piece2", player2);
    }

    @Test
    public void testConstructor() {
        // ID, 이름만 설정하는 생성자 테스트
        Place simplePlace = new Place("p2", "일반 위치");
        assertEquals("p2", simplePlace.getId());
        assertEquals("일반 위치", simplePlace.getName());
        assertFalse(simplePlace.isJunction());
        assertFalse(simplePlace.isCenter());
        assertFalse(simplePlace.isStartingPoint());
        assertFalse(simplePlace.isEndingPoint());

        // 모든 속성 설정하는 생성자 테스트
        Place specialPlace = new Place("p3", "분기점", true, false, false, false);
        assertEquals("p3", specialPlace.getId());
        assertEquals("분기점", specialPlace.getName());
        assertTrue(specialPlace.isJunction());
        assertFalse(specialPlace.isCenter());
        assertFalse(specialPlace.isStartingPoint());
        assertFalse(specialPlace.isEndingPoint());
    }

    @Test
    public void testAddAndRemovePiece() {
        // 말 추가 테스트
        place.addPiece(piece1);
        assertEquals(1, place.getPieceCount());
        assertTrue(place.hasPlayerPieces(player1));

        // 중복 추가 방지 테스트
        place.addPiece(piece1);
        assertEquals(1, place.getPieceCount());

        // 다른 말 추가 테스트
        place.addPiece(piece2);
        assertEquals(2, place.getPieceCount());
        assertTrue(place.hasPlayerPieces(player2));

        // 말 제거 테스트
        boolean removed = place.removePiece(piece1);
        assertTrue(removed);
        assertEquals(1, place.getPieceCount());
        assertFalse(place.hasPlayerPieces(player1));

        // 존재하지 않는 말 제거 시도 테스트
        removed = place.removePiece(piece1);
        assertFalse(removed);
    }

    @Test
    public void testGetOpponentPieces() {
        // 두 플레이어의 말 추가
        place.addPiece(piece1);
        place.addPiece(piece2);

        // player1 기준으로 상대 말 가져오기
        List<Piece> opponentPieces = place.getOpponentPieces(player1);
        assertEquals(1, opponentPieces.size());
        assertEquals(piece2, opponentPieces.get(0));

        // player2 기준으로 상대 말 가져오기
        opponentPieces = place.getOpponentPieces(player2);
        assertEquals(1, opponentPieces.size());
        assertEquals(piece1, opponentPieces.get(0));
    }

    @Test
    public void testGetPlayerPiece() {
        // 말 추가
        place.addPiece(piece1);
        place.addPiece(piece2);

        // 플레이어별 말 가져오기
        Piece foundPiece = place.getPlayerPiece(player1);
        assertEquals(piece1, foundPiece);

        foundPiece = place.getPlayerPiece(player2);
        assertEquals(piece2, foundPiece);

        // 말이 없는 경우
        Player player3 = new Player("Player 3");
        foundPiece = place.getPlayerPiece(player3);
        assertNull(foundPiece);
    }

    @Test
    public void testIsEmpty() {
        // 초기 상태는 비어있음
        assertTrue(place.isEmpty());

        // 말을 추가하면 비어있지 않음
        place.addPiece(piece1);
        assertFalse(place.isEmpty());

        // 말을 모두 제거하면 다시 비어있음
        place.removePiece(piece1);
        assertTrue(place.isEmpty());
    }

    @Test
    public void testNextPlaceConnection() {
        // 새로운 위치 생성
        Place nextPlace = new Place("p4", "다음 위치");

        // 기본 다음 위치 연결
        place.setNextPlace(nextPlace);
        assertEquals(nextPlace, place.getNextPlace());

        // 특별 다음 위치 연결
        Place specialNextPlace = new Place("p5", "특별 다음 위치");
        place.setSpecialNextPlace(specialNextPlace);
        assertEquals(specialNextPlace, place.getSpecialNextPlace());
        assertTrue(place.hasSpecialNextPlace());

        // 특별 다음 위치 제거
        place.setSpecialNextPlace(null);
        assertNull(place.getSpecialNextPlace());
        assertFalse(place.hasSpecialNextPlace());
    }

    @Test
    public void testEqualsAndHashCode() {
        // 같은 ID를 가진 Place는 동일하게 취급
        Place sameIdPlace = new Place("p1", "다른 이름");
        assertEquals(place, sameIdPlace);
        assertEquals(place.hashCode(), sameIdPlace.hashCode());

        // 다른 ID를 가진 Place는 다르게 취급
        Place differentIdPlace = new Place("p2", "시작점");
        assertNotEquals(place, differentIdPlace);
        assertNotEquals(place.hashCode(), differentIdPlace.hashCode());
    }
}