package org.example.model;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 말의 상호작용(잡기, 업기)을 처리하는 서비스 클래스
 * 기존 Game 클래스의 잡기/업기 로직을 그대로 이동
 */
public class GameInteractionService {

    /**
     * 말이 다른 플레이어의 말을 잡을 수 있는지 확인
     * @param place 위치
     * @param currentPlayer 현재 플레이어
     * @return 잡기 가능 여부
     */
    public boolean isCapture(Place place, Player currentPlayer) {
        List<Piece> opponentPieces = place.getOpponentPieces(currentPlayer);
        return !opponentPieces.isEmpty();
    }

    /**
     * 말이 다른 플레이어의 말을 잡음 (기존 Game.applyCapture 로직 이동)
     * @param capturingPiece 잡는 말
     * @param board 게임 보드
     * @param gameLog 게임 로그 (기존과 동일하게 처리)
     * @return 잡기 성공 여부
     */
    public boolean applyCapture(Piece capturingPiece, Board board, List<String> gameLog) {
        Place currentPlace = capturingPiece.getCurrentPlace();
        if (currentPlace == null) {
            gameLog.add("[디버그] 잡기 실패: 현재 말의 위치가 null입니다.");
            return false;
        }

        Player capturingPlayer = capturingPiece.getPlayer();
        List<Piece> opponentPieces = new ArrayList<>();
        for (Piece piece : currentPlace.getPieces()) {
            if (!piece.getPlayer().equals(capturingPlayer) && !piece.equals(capturingPiece)) {
                opponentPieces.add(piece);
            }
        }

        if (opponentPieces.isEmpty()) {
            gameLog.add("[디버그] 잡기 실패: 상대방 말이 없습니다.");
            return false;
        }

        // 잡기 실행
        for (Piece opponentPiece : opponentPieces) {
            Player opponentPlayer = opponentPiece.getPlayer();

            gameLog.add("[디버그] 잡히는 말 정보:");
            gameLog.add("말 ID: " + opponentPiece.getId());
            gameLog.add("소유자: " + opponentPlayer.getName());
            gameLog.add("업힌 말 개수: " + opponentPiece.getStackedPieces().size());

            List<Piece> stackedPieces = new ArrayList<>(opponentPiece.getStackedPieces());

            gameLog.add(capturingPlayer.getName() + "의 말 " + capturingPiece.getId() +
                    "이(가) " + opponentPlayer.getName() + "의 말 " +
                    opponentPiece.getId() + "을(를) 잡았습니다.");

            if (!stackedPieces.isEmpty()) {
                gameLog.add("업힌 말 " + stackedPieces.size() + "개도 함께 시작점으로 돌아갑니다.");

                for (Piece stackedPiece : stackedPieces) {
                    gameLog.add("업힌 말 " + stackedPiece.getId() + "이(가) 시작점으로 돌아갑니다.");
                    stackedPiece.setCarriedBy(null);
                    opponentPiece.getStackedPieces().remove(stackedPiece);
                    stackedPiece.moveTo(board.getStartingPlace());
                }
            }

            boolean removed = currentPlace.removePiece(opponentPiece);
            gameLog.add("[디버그] 말이 현재 위치에서 제거되었는지: " + removed);

            opponentPiece.moveTo(board.getStartingPlace());
            opponentPiece.clearStackedPieces();
        }

        return true;
    }

    /**
     * 중심점 간의 잡기를 처리하는 메서드 (기존 Game.checkCenterCapture 로직 이동)
     * @param piece 현재 이동한 말
     * @param board 게임 보드
     * @param gameLog 게임 로그
     * @return 잡기가 발생했으면 true, 아니면 false
     */
    public boolean checkCenterCapture(Piece piece, Board board, List<String> gameLog) {
        Player currentPlayer = piece.getPlayer();
        Place currentPlace = piece.getCurrentPlace();

        if (currentPlace == null || !currentPlace.isCenter()) {
            return false;
        }

        Map<String, Place> centerPlaces = board.getCenterPlaces();
        List<Piece> opponentPiecesToCapture = new ArrayList<>();

        for (Place centerPlace : centerPlaces.values()) {
            if (!centerPlace.equals(currentPlace)) {
                for (Piece otherPiece : centerPlace.getPieces()) {
                    if (!otherPiece.getPlayer().equals(currentPlayer)) {
                        opponentPiecesToCapture.add(otherPiece);
                    }
                }
            }
        }

        if (opponentPiecesToCapture.isEmpty()) {
            return false;
        }

        for (Piece opponentPiece : opponentPiecesToCapture) {
            gameLog.add(currentPlayer.getName() + "의 말 " + piece.getId() +
                    "이(가) 중심점에서 " + opponentPiece.getPlayer().getName() +
                    "의 말 " + opponentPiece.getId() + "을(를) 잡았습니다.");

            if (!opponentPiece.getStackedPieces().isEmpty()) {
                gameLog.add("업힌 말 " + opponentPiece.getStackedPieces().size() +
                        "개도 함께 시작점으로 돌아갑니다.");
            }

            Place opponentPlace = opponentPiece.getCurrentPlace();
            if (opponentPlace != null) {
                opponentPlace.removePiece(opponentPiece);
            }

            opponentPiece.unstackAllPieces();
            opponentPiece.moveTo(board.getStartingPlace());
        }

        return true;
    }

    /**
     * 같은 플레이어의 말 업기 (기존 Game.applyGrouping 로직 이동)
     * @param piece1 기준 말
     * @param piece2 업힐 말
     * @param gameLog 게임 로그
     * @return 업기 성공 여부
     */
    public boolean applyGrouping(Piece piece1, Piece piece2, List<String> gameLog) {
        gameLog.add("[디버그] === 업기 전 상태 ===");
        gameLog.add("업는 말: " + piece1.getId() + ", 업히는 말: " + piece2.getId());

        if (piece1.getCurrentPlace() != null) {
            debugPrintPlaceInfo(piece1.getCurrentPlace(), gameLog);
        } else {
            gameLog.add("[디버그] 업는 말의 현재 위치가 null입니다.");
        }

        if (!piece1.getPlayer().equals(piece2.getPlayer())) {
            gameLog.add("업기 실패: 서로 다른 플레이어의 말입니다.");
            return false;
        }

        Place place1 = piece1.getCurrentPlace();
        Place place2 = piece2.getCurrentPlace();

        if (place1 == null) {
            gameLog.add("[디버그] 업기 실패: 업는 말이 보드 위에 없습니다.");
            return false;
        }

        if (place2 == null) {
            gameLog.add("[디버그] 업기 실패: 업히는 말이 보드 위에 없습니다. (이미 업힌 상태일 수 있음)");
            return false;
        }

        boolean sameLocation = place1.equals(place2);
        if (!sameLocation &&
                ((place1.getId().equals("C_1") && place2.getId().equals("C_2")) ||
                        (place1.getId().equals("C_2") && place2.getId().equals("C_1")))) {
            sameLocation = true;
        }

        if (!sameLocation) {
            gameLog.add("[디버그] 업기 실패: 두 말이 서로 다른 위치에 있습니다. place1: " +
                    place1.getId() + ", place2: " + place2.getId());
            return false;
        }

        gameLog.add("[디버그] 업기 전 위치에 있는 말 목록:");
        for (Piece p : place1.getPieces()) {
            gameLog.add("- " + p.getId() + " (소유자: " + p.getPlayer().getName() + ")");
        }

        boolean stackResult = piece1.stackPiece(piece2);

        if (stackResult) {
            gameLog.add(piece1.getPlayer().getName() + "의 말 " + piece1.getId() +
                    "이(가) " + piece2.getId() + "을(를) 업었습니다.");
        } else {
            gameLog.add("업기 실패: stackPiece 메서드가 false를 반환했습니다.");
            return false;
        }

        gameLog.add("[디버그] 업기 후 위치에 있는 말 목록:");
        for (Piece p : place1.getPieces()) {
            gameLog.add("- " + p.getId() + " (소유자: " + p.getPlayer().getName() + ")");
        }

        gameLog.add("[디버그] === 업기 후 상태 ===");
        if (piece1.getCurrentPlace() != null) {
            debugPrintPlaceInfo(piece1.getCurrentPlace(), gameLog);
        }
        gameLog.add("[디버그] 업는 말 " + piece1.getId() + "에 업힌 말 개수: " + piece1.getStackedPieces().size());

        return true;
    }

    /**
     * 중심점 간의 업기를 처리하는 메서드 (기존 Game.checkCenterStacking 로직 이동)
     * @param piece 현재 이동한 말
     * @param board 게임 보드
     * @param gameLog 게임 로그
     * @return 업기가 발생했으면 true, 아니면 false
     */
    public boolean checkCenterStacking(Piece piece, Board board, List<String> gameLog) {
        Player player = piece.getPlayer();
        Place currentPlace = piece.getCurrentPlace();

        if (currentPlace == null || !currentPlace.isCenter()) {
            return false;
        }

        Map<String, Place> centerPlaces = board.getCenterPlaces();
        List<Piece> samePiecesToStack = new ArrayList<>();

        for (Place centerPlace : centerPlaces.values()) {
            if (!centerPlace.equals(currentPlace)) {
                for (Piece otherPiece : centerPlace.getPieces()) {
                    if (otherPiece.getPlayer().equals(player) && !otherPiece.equals(piece)) {
                        samePiecesToStack.add(otherPiece);
                    }
                }
            }
        }

        if (samePiecesToStack.isEmpty()) {
            return false;
        }

        boolean stackingOccurred = false;

        for (Piece otherPiece : samePiecesToStack) {
            boolean result = piece.stackPiece(otherPiece);
            if (result) {
                stackingOccurred = true;
                gameLog.add(player.getName() + "의 말 " + piece.getId() +
                        "이(가) 다른 중심점에 있던 " + otherPiece.getId() + "을(를) 업었습니다.");
            }
        }

        return stackingOccurred;
    }

    /**
     * 현재 위치에서 같은 플레이어의 모든 말 업기 확인 및 처리 (기존 Game.checkAndApplyGrouping 로직 이동)
     * @param place 현재 위치
     * @param currentPiece 현재 말
     * @param board 게임 보드
     * @param gameLog 게임 로그
     */
    public void checkAndApplyGrouping(Place place, Piece currentPiece, Board board, List<String> gameLog) {
        if (place == null || currentPiece == null) {
            return;
        }

        Player currentPlayer = currentPiece.getPlayer();
        List<Piece> piecesAtPlace = new ArrayList<>(place.getPieces());

        if (place.isCenter() && (place.getId().equals("C_1") || place.getId().equals("C_2"))) {
            String otherCenterId = place.getId().equals("C_1") ? "C_2" : "C_1";
            Place otherCenter = board.getPlaceById(otherCenterId);

            if (otherCenter != null) {
                piecesAtPlace.addAll(otherCenter.getPieces());
            }
        }

        List<Piece> samePlayerPieces = new ArrayList<>();
        for (Piece p : piecesAtPlace) {
            if (p.getPlayer().equals(currentPlayer) && !p.equals(currentPiece)) {
                samePlayerPieces.add(p);
            }
        }

        if (!samePlayerPieces.isEmpty()) {
            for (Piece otherPiece : samePlayerPieces) {
                boolean result = applyGrouping(currentPiece, otherPiece, gameLog);
                if (result) {
                    gameLog.add(currentPlayer.getName() + "의 말 " + currentPiece.getId() +
                            "이(가) " + otherPiece.getId() + "을(를) 업었습니다.");
                }
            }
        }
    }

    // 디버그 헬퍼 메서드
    private void debugPrintPlaceInfo(Place place, List<String> gameLog) {
        if (place == null) {
            gameLog.add("[디버그] 위치 객체가 null입니다.");
            return;
        }

        gameLog.add("=== [디버그] 위치 정보 ===");
        gameLog.add("위치 ID: " + place.getId());
        gameLog.add("위치 이름: " + place.getName());
        gameLog.add("분기점 여부: " + place.isJunction());
        gameLog.add("중앙점 여부: " + place.isCenter());
        gameLog.add("시작점 여부: " + place.isStartingPoint());
        gameLog.add("도착점 여부: " + place.isEndingPoint());

        List<Piece> pieces = place.getPieces();
        gameLog.add("말 개수: " + pieces.size());

        if (!pieces.isEmpty()) {
            gameLog.add("--- 말 목록 ---");
            for (int i = 0; i < pieces.size(); i++) {
                Piece piece = pieces.get(i);
                gameLog.add((i+1) + ". ID: " + piece.getId() +
                        ", 플레이어: " + piece.getPlayer().getName() +
                        ", 업힌 말 수: " + piece.getStackedPieces().size());

                if (!piece.getStackedPieces().isEmpty()) {
                    gameLog.add("   업힌 말 목록:");
                    for (Piece stackedPiece : piece.getStackedPieces()) {
                        gameLog.add("    - " + stackedPiece.getId() +
                                " (소유자: " + stackedPiece.getPlayer().getName() + ")");
                    }
                }
            }
        }
        gameLog.add("===============");
    }
}