package org.example.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

/**
 * Player 클래스에 대한 단위 테스트
 */
public class PlayerTest {
    private Player player;
    private final String TEST_PLAYER_NAME = "Test Player";

    @Before
    public void setUp() {
        // 각 테스트 전에 새로운 Player 객체 생성
        player = new Player(TEST_PLAYER_NAME);
    }

    @Test
    public void testPlayerCreation() {
        // 기본 생성자 테스트
        Player defaultPlayer = new Player();
        assertNotNull("기본 생성자로 생성된 플레이어가 null이 아니어야 함", defaultPlayer);
        assertTrue("기본 생성자로 생성된 플레이어의 말 목록이 비어있어야 함", defaultPlayer.getPieces().isEmpty());

        // 이름으로 생성자 테스트
        assertNotNull("이름으로 생성된 플레이어가 null이 아니어야 함", player);
        assertEquals("플레이어 이름이 설정한 이름과 일치해야 함", TEST_PLAYER_NAME, player.getName());
        assertEquals("플레이어 ID가 이름을 기반으로 올바르게 생성되어야 함", "test_player", player.getId());
        assertTrue("초기에 플레이어의 말 목록이 비어있어야 함", player.getPieces().isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlayerCreationWithEmptyName() {
        // 빈 이름으로 플레이어 생성 시 예외 발생 테스트
        new Player("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlayerCreationWithNullName() {
        // null 이름으로 플레이어 생성 시 예외 발생 테스트
        new Player(null);
    }

    @Test
    public void testAddPiece() {
        // 말 추가 테스트
        Piece piece1 = new Piece("P1", player);
        player.addPiece(piece1);

        List<Piece> pieces = player.getPieces();
        assertEquals("말을 한 개 추가한 후 목록 크기가 1이어야 함", 1, pieces.size());
        assertEquals("추가한 말이 목록에 포함되어야 함", piece1, pieces.get(0));

        // 동일한 말 중복 추가 테스트
        player.addPiece(piece1);
        assertEquals("동일한 말 중복 추가 시 목록 크기가 변경되지 않아야 함", 1, player.getPieces().size());

        // 다른 말 추가 테스트
        Piece piece2 = new Piece("P2", player);
        player.addPiece(piece2);
        assertEquals("다른 말 추가 시 목록 크기가 증가해야 함", 2, player.getPieces().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNullPiece() {
        // null 말 추가 시 예외 발생 테스트
        player.addPiece(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddPieceExceedingMaximum() {
        // 최대 개수 초과 테스트
        for (int i = 0; i < 6; i++) {
            Piece piece = new Piece("P" + i, player);
            player.addPiece(piece);
        }
    }

    @Test
    public void testHasMinimumRequiredPieces() {
        // 최소 필요 말 개수 체크 테스트
        assertFalse("초기 상태에서는 최소 필요 말 개수를 충족하지 않아야 함", player.hasMinimumRequiredPieces());

        // 1개 말 추가
        player.addPiece(new Piece("P1", player));
        assertFalse("1개 말로는 최소 필요 말 개수를 충족하지 않아야 함", player.hasMinimumRequiredPieces());

        // 2개 말 추가 (총 2개)
        player.addPiece(new Piece("P2", player));
        assertTrue("2개 말로는 최소 필요 말 개수를 충족해야 함", player.hasMinimumRequiredPieces());
    }

    @Test
    public void testGetPieces() {
        // 말 목록 반환 테스트 (방어적 복사 확인)
        Piece piece = new Piece("P1", player);
        player.addPiece(piece);

        List<Piece> pieces = player.getPieces();
        assertEquals("목록에 말이 포함되어야 함", 1, pieces.size());

        // 반환된 목록을 수정해도 원본에 영향이 없어야 함 (방어적 복사 확인)
        pieces.clear();
        assertEquals("반환된 목록 수정이 원본에 영향을 주지 않아야 함", 1, player.getPieces().size());
    }

    @Test
    public void testGetMovablePieces() {
        // 이동 가능한 말 목록 테스트
        Piece piece1 = new Piece("P1", player);
        Piece piece2 = new Piece("P2", player);
        player.addPiece(piece1);
        player.addPiece(piece2);

        // 초기에는 모든 말이 이동 가능
        assertEquals("초기에는 모든 말이 이동 가능해야 함", 2, player.getMovablePieces().size());

        // piece1을 완주 상태로 설정
        // Piece 객체의 isCompleted가 private 필드이므로, 테스트를 위해 임시 방법 필요
        // 실제로는 말의 완주 로직을 통해 설정해야 함
        // 여기서는 완주 상태를 설정하는 방법이 없으므로 실제 구현에 맞게 수정 필요

        // 가정: Place 클래스에 도착점이 있고, 말을 그곳으로 이동시킴
        Place endingPlace = new Place("E", "도착점", false, false, false, true);
        piece1.moveTo(endingPlace);

        // 완주한 말 하나를 제외하고 이동 가능한 말만 반환되어야 함
        List<Piece> movablePieces = player.getMovablePieces();
        assertEquals("완주한 말을 제외한 이동 가능한 말만 반환되어야 함", 1, movablePieces.size());
        assertEquals("이동 가능한 말이 piece2여야 함", piece2, movablePieces.get(0));
    }

    @Test
    public void testGetPieceById() {
        // ID로 말 찾기 테스트
        Piece piece1 = new Piece("P1", player);
        Piece piece2 = new Piece("P2", player);
        player.addPiece(piece1);
        player.addPiece(piece2);

        assertEquals("ID로 정확한 말을 찾아야 함", piece1, player.getPieceById("P1"));
        assertEquals("ID로 정확한 말을 찾아야 함", piece2, player.getPieceById("P2"));
        assertNull("존재하지 않는 ID로 검색 시 null을 반환해야 함", player.getPieceById("P3"));
        assertNull("null ID로 검색 시 null을 반환해야 함", player.getPieceById(null));
    }

    @Test
    public void testIsAllPiecesCompleted() {
        // 모든 말 완주 확인 테스트
        assertTrue("말이 없을 때는 모든 말 완주가 false여야 함", !player.isAllPiecesCompleted());

        Piece piece1 = new Piece("P1", player);
        Piece piece2 = new Piece("P2", player);
        player.addPiece(piece1);
        player.addPiece(piece2);

        // 초기에는 어떤 말도 완주하지 않음
        assertFalse("초기에는 모든 말 완주가 false여야 함", player.isAllPiecesCompleted());

        // 한 말만 완주
        Place endingPlace = new Place("E", "도착점", false, false, false, true);
        piece1.moveTo(endingPlace);
        assertFalse("일부 말만 완주했을 때 모든 말 완주가 false여야 함", player.isAllPiecesCompleted());

        // 모든 말 완주
        piece2.moveTo(endingPlace);
        assertTrue("모든 말이 완주했을 때 모든 말 완주가 true여야 함", player.isAllPiecesCompleted());
    }

    @Test
    public void testGetCompletedPieceCount() {
        // 완주한 말 개수 테스트
        assertEquals("초기에는 완주한 말이 없어야 함", 0, player.getCompletedPieceCount());

        Piece piece1 = new Piece("P1", player);
        Piece piece2 = new Piece("P2", player);
        Piece piece3 = new Piece("P3", player);
        player.addPiece(piece1);
        player.addPiece(piece2);
        player.addPiece(piece3);

        // 말 하나 완주
        Place endingPlace = new Place("E", "도착점", false, false, false, true);
        piece1.moveTo(endingPlace);
        assertEquals("말 하나 완주 후 완주 개수가 1이어야 함", 1, player.getCompletedPieceCount());

        // 말 하나 더 완주
        piece2.moveTo(endingPlace);
        assertEquals("말 두 개 완주 후 완주 개수가 2여야 함", 2, player.getCompletedPieceCount());
    }

    @Test
    public void testSetName() {
        // 이름 설정 테스트
        String newName = "New Name";
        player.setName(newName);
        assertEquals("이름이 변경되어야 함", newName, player.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetEmptyName() {
        // 빈 이름 설정 시 예외 발생 테스트
        player.setName("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNullName() {
        // null 이름 설정 시 예외 발생 테스트
        player.setName(null);
    }

    @Test
    public void testEquals() {
        // 동등성 비교 테스트
        Player sameIdPlayer = new Player(TEST_PLAYER_NAME);
        Player differentPlayer = new Player("Another Player");

        // 자기 자신과 비교
        assertTrue("자기 자신과 비교 시 true를 반환해야 함", player.equals(player));

        // 같은 ID를 가진 다른 객체와 비교
        assertTrue("같은 ID를 가진 객체와 비교 시 true를 반환해야 함", player.equals(sameIdPlayer));

        // 다른 ID를 가진 객체와 비교
        assertFalse("다른 ID를 가진 객체와 비교 시 false를 반환해야 함", player.equals(differentPlayer));

        // null과 비교
        assertFalse("null과 비교 시 false를 반환해야 함", player.equals(null));

        // 다른 타입의 객체와 비교
        assertFalse("다른 타입의 객체와 비교 시 false를 반환해야 함", player.equals("Not a Player"));
    }

    @Test
    public void testHashCode() {
        // 해시코드 테스트
        Player sameIdPlayer = new Player(TEST_PLAYER_NAME);

        // 같은 ID를 가진 객체는 같은 해시코드를 가져야 함
        assertEquals("같은 ID를 가진 객체는 같은 해시코드를 가져야 함",
                player.hashCode(), sameIdPlayer.hashCode());
    }

    @Test
    public void testToString() {
        // toString 메서드 테스트
        String playerString = player.toString();

        // toString 결과가 필요한 정보를 포함해야 함
        assertTrue("toString 결과에 ID가 포함되어야 함", playerString.contains(player.getId()));
        assertTrue("toString 결과에 이름이 포함되어야 함", playerString.contains(player.getName()));
        assertTrue("toString 결과에 말 개수가 포함되어야 함", playerString.contains("pieces=" + player.getPieces().size()));
    }
}