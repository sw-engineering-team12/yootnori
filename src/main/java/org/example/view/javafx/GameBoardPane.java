package org.example.view.javafx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Font;
import org.example.controller.GameController;
import org.example.model.Board;
import org.example.model.Game;
import org.example.model.Piece;
import org.example.model.Place;
import org.example.model.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JavaFX 게임 보드 패널
 */
public class GameBoardPane extends Pane {
    private GameController controller;
    private Canvas canvas;
    private Map<String, Point2D> placePositions; // 위치 ID와 화면 좌표 매핑
    private Map<String, Color> playerColors; // 플레이어 ID와 색상 매핑

    private static final double BOARD_PADDING = 50;
    private static final double NODE_SIZE = 30;
    private static final double PIECE_SIZE = 20;

    /**
     * 내부 Point2D 클래스
     */
    private static class Point2D {
        public final double x, y;

        public Point2D(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public GameBoardPane(GameController controller) {
        this.controller = controller;
        this.placePositions = new HashMap<>();
        this.playerColors = new HashMap<>();

        // 플레이어 색상 설정
        playerColors.put("player_1", Color.RED);
        playerColors.put("player_2", Color.BLUE);
        playerColors.put("player_3", Color.GREEN);
        playerColors.put("player_4", Color.YELLOW);

        // 캔버스 생성
        canvas = new Canvas(600, 600);
        getChildren().add(canvas);

        // 마우스 클릭 이벤트 처리
        canvas.setOnMouseClicked(this::handleMouseClick);

        // 크기 변경 시 캔버스 크기도 조정
        widthProperty().addListener((obs, oldVal, newVal) -> {
            canvas.setWidth(newVal.doubleValue());
            updateBoard();
        });
        heightProperty().addListener((obs, oldVal, newVal) -> {
            canvas.setHeight(newVal.doubleValue());
            updateBoard();
        });
    }

    /**
     * 마우스 클릭 이벤트 처리
     */
    private void handleMouseClick(MouseEvent e) {
        // 이동 가능한 말이 없으면 처리하지 않음
        List<Piece> movablePieces = controller.getMovablePieces();
        if (movablePieces == null || movablePieces.isEmpty()) {
            return;
        }

        // 클릭된 위치에 있는 말 찾기
        Game game = controller.getGame();
        Board board = game.getBoard();

        for (Map.Entry<String, Point2D> entry : placePositions.entrySet()) {
            Point2D point = entry.getValue();
            double distance = Math.sqrt(Math.pow(e.getX() - point.x, 2) + Math.pow(e.getY() - point.y, 2));

            // 노드 범위 내에 클릭되었는지 확인
            if (distance <= NODE_SIZE) {
                String placeId = entry.getKey();
                Place place = board.getPlaceById(placeId);

                if (place != null) {
                    // 이 위치에 현재 플레이어의 말이 있는지 확인
                    Player currentPlayer = game.getCurrentPlayer();
                    List<Piece> piecesAtPlace = place.getPieces();

                    for (Piece piece : piecesAtPlace) {
                        if (piece.getPlayer().equals(currentPlayer) && movablePieces.contains(piece)) {
                            // 말 이동 실행
                            controller.movePiece(piece);
                            return;
                        }
                    }
                }
                break;
            }
        }
    }

    /**
     * 보드 업데이트
     */
    public void updateBoard() {
        drawBoard();
    }

    /**
     * 보드 그리기
     */
    private void drawBoard() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // 캔버스 클리어
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        Game game = controller.getGame();
        if (game == null) return;

        Board board = game.getBoard();
        Board.BoardType boardType = board.getBoardType();

        // 보드 크기 계산
        double width = canvas.getWidth() - (2 * BOARD_PADDING);
        double height = canvas.getHeight() - (2 * BOARD_PADDING);
        double size = Math.min(width, height);

        // 보드 타입에 따라 다르게 그리기
        placePositions.clear();

        switch (boardType) {
            case SQUARE:
                drawSquareBoard(gc, board, BOARD_PADDING, BOARD_PADDING, size);
                break;
            case PENTAGON:
                drawPentagonBoard(gc, board, BOARD_PADDING, BOARD_PADDING, size);
                break;
            case HEXAGON:
                drawHexagonBoard(gc, board, BOARD_PADDING, BOARD_PADDING, size);
                break;
        }

        // 말 그리기
        drawPieces(gc, game);
    }

    /**
     * 사각형 보드 그리기
     */
    private void drawSquareBoard(GraphicsContext gc, Board board, double x, double y, double size) {
        // 보드 테두리
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(x, y, size, size);

        double nodeSpacing = size / 5;

        // 외곽 노드 그리기
        for (int i = 0; i <= 5; i++) {
            for (int j = 0; j <= 5; j++) {
                if (i == 0 || i == 5 || j == 0 || j == 5) {
                    double nodeX = x + (j * nodeSpacing);
                    double nodeY = y + (i * nodeSpacing);
                    String placeId = getPlaceIdForSquarePosition(i, j);
                    drawNode(gc, nodeX, nodeY, placeId, board);
                    placePositions.put(placeId, new Point2D(nodeX, nodeY));
                }
            }
        }

        // 중앙 노드
        double centerX = x + (size / 2);
        double centerY = y + (size / 2);
        // C_2 노드를 먼저 그리고
        drawNode(gc, centerX, centerY, "C_2", board);
        placePositions.put("C_2", new Point2D(centerX, centerY));
        // 그 다음 C_1 노드를 그립니다
        drawNode(gc, centerX, centerY, "C_1", board);
        placePositions.put("C_1", new Point2D(centerX, centerY));

        // 각 모서리 좌표
        double sx = x, sy = y; // S
        double s5x = x + size, s5y = y; // 5
        double s10x = x + size, s10y = y + size; // 10
        double s15x = x, s15y = y + size; // 15

        // 대각선 경로: 각 모서리~중앙 1/3, 2/3 지점에 C노드 배치
        // S~C_1: C8, C7
        double c8x = sx * 2.0/3 + centerX * 1.0/3;
        double c8y = sy * 2.0/3 + centerY * 1.0/3;
        double c7x = sx * 1.0/3 + centerX * 2.0/3;
        double c7y = sy * 1.0/3 + centerY * 2.0/3;
        drawNode(gc, c8x, c8y, "C8", board);
        drawNode(gc, c7x, c7y, "C7", board);
        placePositions.put("C8", new Point2D(c8x, c8y));
        placePositions.put("C7", new Point2D(c7x, c7y));
        drawPath(gc, sx, sy, c8x, c8y);
        drawPath(gc, c8x, c8y, c7x, c7y);
        drawPath(gc, c7x, c7y, centerX, centerY);

        // 5~C_1: C1, C2
        double c1x = s5x * 2.0/3 + centerX * 1.0/3;
        double c1y = s5y * 2.0/3 + centerY * 1.0/3;
        double c2x = s5x * 1.0/3 + centerX * 2.0/3;
        double c2y = s5y * 1.0/3 + centerY * 2.0/3;
        drawNode(gc, c1x, c1y, "C1", board);
        drawNode(gc, c2x, c2y, "C2", board);
        placePositions.put("C1", new Point2D(c1x, c1y));
        placePositions.put("C2", new Point2D(c2x, c2y));
        drawPath(gc, s5x, s5y, c1x, c1y);
        drawPath(gc, c1x, c1y, c2x, c2y);
        drawPath(gc, c2x, c2y, centerX, centerY);

        // 15~C_1: C6, C5
        double c6x = s15x * 2.0/3 + centerX * 1.0/3;
        double c6y = s15y * 2.0/3 + centerY * 1.0/3;
        double c5x = s15x * 1.0/3 + centerX * 2.0/3;
        double c5y = s15y * 1.0/3 + centerY * 2.0/3;
        drawNode(gc, c6x, c6y, "C6", board);
        drawNode(gc, c5x, c5y, "C5", board);
        placePositions.put("C6", new Point2D(c6x, c6y));
        placePositions.put("C5", new Point2D(c5x, c5y));
        drawPath(gc, s15x, s15y, c6x, c6y);
        drawPath(gc, c6x, c6y, c5x, c5y);
        drawPath(gc, c5x, c5y, centerX, centerY);

        // 10~C_2: C3, C4
        double c3x = s10x * 2.0/3 + centerX * 1.0/3;
        double c3y = s10y * 2.0/3 + centerY * 1.0/3;
        double c4x = s10x * 1.0/3 + centerX * 2.0/3;
        double c4y = s10y * 1.0/3 + centerY * 2.0/3;
        drawNode(gc, c3x, c3y, "C3", board);
        drawNode(gc, c4x, c4y, "C4", board);
        placePositions.put("C3", new Point2D(c3x, c3y));
        placePositions.put("C4", new Point2D(c4x, c4y));
        drawPath(gc, s10x, s10y, c3x, c3y);
        drawPath(gc, c3x, c3y, c4x, c4y);
        drawPath(gc, c4x, c4y, centerX, centerY);
    }

    /**
     * 오각형 보드 그리기
     */
    private void drawPentagonBoard(GraphicsContext gc, Board board, double x, double y, double size) {
        double[] xPoints = new double[5];
        double[] yPoints = new double[5];
        double centerX = x + (size / 2);
        double centerY = y + (size / 2);
        double radius = size / 2;

        for (int i = 0; i < 5; i++) {
            double angle = 2 * Math.PI * i / 5 - Math.PI / 2;
            xPoints[i] = centerX + radius * Math.cos(angle);
            yPoints[i] = centerY + radius * Math.sin(angle);
        }

        // 오각형 테두리 그리기
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokePolygon(xPoints, yPoints, 5);

        // 꼭지점 노드
        for (int i = 0; i < 5; i++) {
            drawNode(gc, xPoints[i], yPoints[i], String.valueOf(i * 5), board);
            placePositions.put(String.valueOf(i * 5), new Point2D(xPoints[i], yPoints[i]));
        }

        // 변의 중간 노드들
        for (int i = 0; i < 5; i++) {
            int nextIdx = (i + 1) % 5;
            for (int j = 1; j < 5; j++) {
                double nodeX = xPoints[i] + (xPoints[nextIdx] - xPoints[i]) * j / 5;
                double nodeY = yPoints[i] + (yPoints[nextIdx] - yPoints[i]) * j / 5;
                String placeId = String.valueOf(i * 5 + j);
                drawNode(gc, nodeX, nodeY, placeId, board);
                placePositions.put(placeId, new Point2D(nodeX, nodeY));
            }
        }

        // S 위치 추가 (0번 인덱스의 꼭지점)
        drawNode(gc, xPoints[0], yPoints[0], "S", board);
        placePositions.put("S", new Point2D(xPoints[0], yPoints[0]));

        // 중앙 노드 (하나만)
        drawNode(gc, centerX, centerY, "C_1", board);
        placePositions.put("C_1", new Point2D(centerX, centerY));

        // 대각선 경로: 각 꼭지점~중앙 1/3, 2/3 지점에 C노드 배치 (C10, C9, C1, C2, C3, C4, C5, C6, C8, C7)
        String[] cNodeIds = {"C10", "C9", "C1", "C2", "C3", "C4", "C5", "C6", "C8", "C7"};
        for (int i = 0; i < 5; i++) {
            double c1x = xPoints[i] * 2.0/3 + centerX * 1.0/3;
            double c1y = yPoints[i] * 2.0/3 + centerY * 1.0/3;
            double c2x = xPoints[i] * 1.0/3 + centerX * 2.0/3;
            double c2y = yPoints[i] * 1.0/3 + centerY * 2.0/3;
            String c1id = cNodeIds[i * 2];
            String c2id = cNodeIds[i * 2 + 1];
            drawNode(gc, c1x, c1y, c1id, board);
            drawNode(gc, c2x, c2y, c2id, board);
            placePositions.put(c1id, new Point2D(c1x, c1y));
            placePositions.put(c2id, new Point2D(c2x, c2y));
            drawPath(gc, xPoints[i], yPoints[i], c1x, c1y);
            drawPath(gc, c1x, c1y, c2x, c2y);
            drawPath(gc, c2x, c2y, centerX, centerY);
        }
    }

    /**
     * 육각형 보드 그리기
     */
    private void drawHexagonBoard(GraphicsContext gc, Board board, double x, double y, double size) {
        double[] xPoints = new double[6];
        double[] yPoints = new double[6];
        double centerX = x + (size / 2);
        double centerY = y + (size / 2);
        double radius = size / 2;

        for (int i = 0; i < 6; i++) {
            double angle = 2 * Math.PI * i / 6 - Math.PI / 2;
            xPoints[i] = centerX + radius * Math.cos(angle);
            yPoints[i] = centerY + radius * Math.sin(angle);
        }

        // 육각형 테두리 그리기
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokePolygon(xPoints, yPoints, 6);

        // S 위치 추가 (0번 인덱스의 꼭지점)
        drawNode(gc, xPoints[0], yPoints[0], "S", board);
        placePositions.put("S", new Point2D(xPoints[0], yPoints[0]));

        // 꼭지점 노드
        for (int i = 0; i < 6; i++) {
            drawNode(gc, xPoints[i], yPoints[i], String.valueOf(i * 5), board);
            placePositions.put(String.valueOf(i * 5), new Point2D(xPoints[i], yPoints[i]));
        }

        // 변의 중간 노드들
        for (int i = 0; i < 6; i++) {
            int nextIdx = (i + 1) % 6;
            for (int j = 1; j < 5; j++) {
                double nodeX = xPoints[i] + (xPoints[nextIdx] - xPoints[i]) * j / 5;
                double nodeY = yPoints[i] + (yPoints[nextIdx] - yPoints[i]) * j / 5;
                String placeId = String.valueOf(i * 5 + j);
                drawNode(gc, nodeX, nodeY, placeId, board);
                placePositions.put(placeId, new Point2D(nodeX, nodeY));
            }
        }

        // 중앙 노드 (하나만)
        drawNode(gc, centerX, centerY, "C_1", board);
        placePositions.put("C_1", new Point2D(centerX, centerY));

        // 대각선 경로: 각 꼭지점~중앙 1/3, 2/3 지점에 C노드 배치 (C12, C11, C1-C8, C10, C9)
        String[] cNodeIds = {"C12", "C11", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C10", "C9"};
        for (int i = 0; i < 6; i++) {
            double c1x = xPoints[i] * 2.0/3 + centerX * 1.0/3;
            double c1y = yPoints[i] * 2.0/3 + centerY * 1.0/3;
            double c2x = xPoints[i] * 1.0/3 + centerX * 2.0/3;
            double c2y = yPoints[i] * 1.0/3 + centerY * 2.0/3;
            String c1id = cNodeIds[i * 2];
            String c2id = cNodeIds[i * 2 + 1];
            drawNode(gc, c1x, c1y, c1id, board);
            drawNode(gc, c2x, c2y, c2id, board);
            placePositions.put(c1id, new Point2D(c1x, c1y));
            placePositions.put(c2id, new Point2D(c2x, c2y));
            drawPath(gc, xPoints[i], yPoints[i], c1x, c1y);
            drawPath(gc, c1x, c1y, c2x, c2y);
            drawPath(gc, c2x, c2y, centerX, centerY);
        }
    }

    /**
     * 사각형 보드의 위치에 해당하는 ID 반환
     */
    private String getPlaceIdForSquarePosition(int row, int col) {
        // 외곽 노드 번호 계산
        if (row == 0 && col == 0) return "S"; // 좌상단 (시작점, 도착점)
        if (row == 0 && col == 5) return "5"; // 우상단
        if (row == 5 && col == 5) return "10"; // 우하단
        if (row == 5 && col == 0) return "15"; // 좌하단

        if (row == 0) return String.valueOf(col);     // 상단 변
        if (col == 5) return String.valueOf(5 + row); // 우측 변
        if (row == 5) return String.valueOf(15 - col); // 하단 변
        if (col == 0) return String.valueOf(20 - row); // 좌측 변

        return ""; // 내부 노드 (사용하지 않음)
    }

    /**
     * 노드 그리기
     */
    private void drawNode(GraphicsContext gc, double x, double y, String placeId, Board board) {
        Place place = board.getPlaceById(placeId);
        double nodeSize = NODE_SIZE;

        // 배경 클리어
        gc.clearRect(x - nodeSize/2 - 2, y - nodeSize/2 - 2, nodeSize + 4, nodeSize + 4);

        // 특별한 위치 배경색
        if (place != null) {
            if (place.isJunction()) {
                gc.setFill(Color.ORANGE);
                gc.fillOval(x - nodeSize/2 - 2, y - nodeSize/2 - 2, nodeSize + 4, nodeSize + 4);
            } else if (place.isCenter()) {
                gc.setFill(Color.CYAN);
                gc.fillOval(x - nodeSize/2 - 2, y - nodeSize/2 - 2, nodeSize + 4, nodeSize + 4);
            } else if (place.isStartingPoint()) {
                gc.setFill(Color.LIGHTGREEN);
                gc.fillOval(x - nodeSize/2 - 2, y - nodeSize/2 - 2, nodeSize + 4, nodeSize + 4);
            } else if (place.isEndingPoint()) {
                gc.setFill(Color.LIGHTCORAL);
                gc.fillOval(x - nodeSize/2 - 2, y - nodeSize/2 - 2, nodeSize + 4, nodeSize + 4);
            }
        }

        // 기본 노드
        gc.setFill(Color.WHITE);
        gc.fillOval(x - nodeSize/2, y - nodeSize/2, nodeSize, nodeSize);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.strokeOval(x - nodeSize/2, y - nodeSize/2, nodeSize, nodeSize);

        // 노드 ID 표시
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        gc.fillText(placeId, x - placeId.length() * 3, y + 3);
    }

    /**
     * 경로 그리기
     */
    private void drawPath(GraphicsContext gc, double x1, double y1, double x2, double y2) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeLine(x1, y1, x2, y2);
    }

    /**
     * 말 그리기
     */
    private void drawPieces(GraphicsContext gc, Game game) {
        // 시작점 위치 좌표 가져오기
        Point2D startPoint = placePositions.get("S");

        // 모든 플레이어의 말 그리기
        for (Player player : game.getPlayers()) {
            for (Piece piece : player.getPieces()) {
                Place place = piece.getCurrentPlace();

                // 말이 보드 위에 없으면 그리지 않음
                if (place == null) continue;

                // S 위치에 있는 말은 그리지 않음 (시작점)
                if (place.isStartingPoint()) {
                    continue;
                }

                // E 위치에 있는 말은 S 위치에 그림
                if (place.getId().equals("E")) {
                    if (startPoint != null) {
                        drawPieceAt(gc, piece, player, startPoint);
                    }
                    continue;
                }

                // 그 외 위치에 있는 말 그리기
                Point2D point = placePositions.get(place.getId());

                if (point != null) {
                    drawPieceAt(gc, piece, player, point);
                }
            }
        }
    }

    /**
     * 지정된 위치에 말 그리기
     */
    private void drawPieceAt(GraphicsContext gc, Piece piece, Player player, Point2D point) {
        // 말 색상 설정
        Color color = playerColors.getOrDefault(player.getId(), Color.GRAY);

        // 현재 플레이어의 말은 테두리 강조
        if (player.equals(controller.getGame().getCurrentPlayer())) {
            gc.setFill(Color.BLACK);
            gc.fillOval(point.x - PIECE_SIZE/2 - 2,
                    point.y - PIECE_SIZE/2 - 2,
                    PIECE_SIZE + 4, PIECE_SIZE + 4);
        }

        // 말 그리기
        gc.setFill(color);
        gc.fillOval(point.x - PIECE_SIZE/2,
                point.y - PIECE_SIZE/2,
                PIECE_SIZE, PIECE_SIZE);

        // 업힌 말이 있으면 숫자 표시
        int stackCount = piece.getStackedPieces().size();
        if (stackCount > 0) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            gc.fillText(String.valueOf(stackCount + 1), point.x - 4, point.y + 4);
        }

        // C_2 노드에 있는 말은 C_1에도 표시
        if (piece.getCurrentPlace() != null && piece.getCurrentPlace().getId().equals("C_2")) {
            Point2D c1Point = placePositions.get("C_1");
            if (c1Point != null) {
                // 동일한 말 그리기 로직 적용
                if (player.equals(controller.getGame().getCurrentPlayer())) {
                    gc.setFill(Color.BLACK);
                    gc.fillOval(c1Point.x - PIECE_SIZE/2 - 2,
                            c1Point.y - PIECE_SIZE/2 - 2,
                            PIECE_SIZE + 4, PIECE_SIZE + 4);
                }

                gc.setFill(color);
                gc.fillOval(c1Point.x - PIECE_SIZE/2,
                        c1Point.y - PIECE_SIZE/2,
                        PIECE_SIZE, PIECE_SIZE);

                if (stackCount > 0) {
                    gc.setFill(Color.WHITE);
                    gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
                    gc.fillText(String.valueOf(stackCount + 1), c1Point.x - 4, c1Point.y + 4);
                }
            }
        }
    }
}