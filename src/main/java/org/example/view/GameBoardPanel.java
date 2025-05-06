package org.example.view;

import org.example.controller.GameController;
import org.example.model.Board;
import org.example.model.Game;
import org.example.model.Piece;
import org.example.model.Place;
import org.example.model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 게임 보드를 그리는 패널
 */
public class GameBoardPanel extends JPanel {
    private GameController controller;
    private Map<String, Point> placePositions; // 위치 ID와 화면 좌표 매핑
    private Map<String, Color> playerColors; // 플레이어 ID와 색상 매핑

    private static final int BOARD_PADDING = 50;
    private static final int NODE_SIZE = 30;
    private static final int PIECE_SIZE = 20;

    /**
     * 생성자
     * @param controller 게임 컨트롤러
     */
    public GameBoardPanel(GameController controller) {
        this.controller = controller;
        this.placePositions = new HashMap<>();
        this.playerColors = new HashMap<>();

        // 플레이어 색상 설정
        playerColors.put("player_1", Color.RED);
        playerColors.put("player_2", Color.BLUE);
        playerColors.put("player_3", Color.GREEN);
        playerColors.put("player_4", Color.YELLOW);

        // 마우스 클릭 이벤트 처리
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });

        setBackground(Color.WHITE);
    }

    /**
     * 마우스 클릭 이벤트 처리
     * @param x X 좌표
     * @param y Y 좌표
     */
    private void handleMouseClick(int x, int y) {
        // 이동 가능한 말이 없으면 처리하지 않음
        List<Piece> movablePieces = controller.getMovablePieces();
        if (movablePieces == null || movablePieces.isEmpty()) {
            return;
        }

        // 클릭된 위치에 있는 말 찾기
        Game game = controller.getGame();
        Board board = game.getBoard();

        for (Map.Entry<String, Point> entry : placePositions.entrySet()) {
            Point point = entry.getValue();
            double distance = Math.sqrt(Math.pow(x - point.x, 2) + Math.pow(y - point.y, 2));

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
     * 컴포넌트 그리기
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 보드 그리기
        drawBoard(g2d);

        // 말 그리기
        drawPieces(g2d);
    }

    /**
     * 보드 그리기
     * @param g2d 그래픽스 객체
     */
    private void drawBoard(Graphics2D g2d) {
        Game game = controller.getGame();
        if (game == null) return;

        Board board = game.getBoard();
        Board.BoardType boardType = board.getBoardType();

        // 보드 크기 계산
        int width = getWidth() - (2 * BOARD_PADDING);
        int height = getHeight() - (2 * BOARD_PADDING);
        int size = Math.min(width, height);

        // 보드 타입에 따라 다르게 그리기
        placePositions.clear();

        switch (boardType) {
            case SQUARE:
                drawSquareBoard(g2d, board, BOARD_PADDING, BOARD_PADDING, size);
                break;
            case PENTAGON:
                drawPentagonBoard(g2d, board, BOARD_PADDING, BOARD_PADDING, size);
                break;
            case HEXAGON:
                drawHexagonBoard(g2d, board, BOARD_PADDING, BOARD_PADDING, size);
                break;
        }
    }

    /**
     * 사각형 보드 그리기
     */
    private void drawSquareBoard(Graphics2D g2d, Board board, int x, int y, int size) {
        // 보드 테두리만 그림 (외곽 경로 연결선은 생략)
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(x, y, size, size);

        int nodeSpacing = size / 5;

        // 외곽 노드만 그리기 (연결선은 생략)
        for (int i = 0; i <= 5; i++) {
            for (int j = 0; j <= 5; j++) {
                if (i == 0 || i == 5 || j == 0 || j == 5) {
                    int nodeX = x + (j * nodeSpacing);
                    int nodeY = y + (i * nodeSpacing);
                    String placeId = getPlaceIdForSquarePosition(i, j);
                    drawNode(g2d, nodeX, nodeY, placeId, board);
                    placePositions.put(placeId, new Point(nodeX, nodeY));
                }
            }
        }

        // 중앙 노드
        int centerX = x + (size / 2);
        int centerY = y + (size / 2);
        drawNode(g2d, centerX, centerY, "C_1", board);
        placePositions.put("C_1", new Point(centerX, centerY));

        // 각 모서리 좌표
        int sx = x, sy = y; // S
        int sex = x, sey = y; // E
        int s5x = x + size, s5y = y; // 5
        int s10x = x + size, s10y = y + size; // 10
        int s15x = x, s15y = y + size; // 15
        // int sex = x + size, sey = y + size; // E

        // 대각선 경로: 각 모서리~중앙 1/3, 2/3 지점에 C노드 배치
        // S~C_1: C8, C7
        int c8x = (int)(sx * 2.0/3 + centerX * 1.0/3);
        int c8y = (int)(sy * 2.0/3 + centerY * 1.0/3);
        int c7x = (int)(sx * 1.0/3 + centerX * 2.0/3);
        int c7y = (int)(sy * 1.0/3 + centerY * 2.0/3);
        drawNode(g2d, c8x, c8y, "C8", board);
        drawNode(g2d, c7x, c7y, "C7", board);
        placePositions.put("C8", new Point(c8x, c8y));
        placePositions.put("C7", new Point(c7x, c7y));
        drawPath(g2d, sx, sy, c8x, c8y);
        drawPath(g2d, c8x, c8y, c7x, c7y);
        drawPath(g2d, c7x, c7y, centerX, centerY);

        // 5~C_1: C1, C2
        int c1x = (int)(s5x * 2.0/3 + centerX * 1.0/3);
        int c1y = (int)(s5y * 2.0/3 + centerY * 1.0/3);
        int c2x = (int)(s5x * 1.0/3 + centerX * 2.0/3);
        int c2y = (int)(s5y * 1.0/3 + centerY * 2.0/3);
        drawNode(g2d, c1x, c1y, "C1", board);
        drawNode(g2d, c2x, c2y, "C2", board);
        placePositions.put("C1", new Point(c1x, c1y));
        placePositions.put("C2", new Point(c2x, c2y));
        drawPath(g2d, s5x, s5y, c1x, c1y);
        drawPath(g2d, c1x, c1y, c2x, c2y);
        drawPath(g2d, c2x, c2y, centerX, centerY);

        // 15~C_1: C6, C5
        int c6x = (int)(s15x * 2.0/3 + centerX * 1.0/3);
        int c6y = (int)(s15y * 2.0/3 + centerY * 1.0/3);
        int c5x = (int)(s15x * 1.0/3 + centerX * 2.0/3);
        int c5y = (int)(s15y * 1.0/3 + centerY * 2.0/3);
        drawNode(g2d, c6x, c6y, "C6", board);
        drawNode(g2d, c5x, c5y, "C5", board);
        placePositions.put("C6", new Point(c6x, c6y));
        placePositions.put("C5", new Point(c5x, c5y));
        drawPath(g2d, s15x, s15y, c6x, c6y);
        drawPath(g2d, c6x, c6y, c5x, c5y);
        drawPath(g2d, c5x, c5y, centerX, centerY);

        // E~C_1: C3, C4
        int c3x = (int)(s10x * 2.0/3 + centerX * 1.0/3);
        int c3y = (int)(s10y * 2.0/3 + centerY * 1.0/3);
        int c4x = (int)(s10x * 1.0/3 + centerX * 2.0/3);
        int c4y = (int)(s10y * 1.0/3 + centerY * 2.0/3);
        drawNode(g2d, c3x, c3y, "C3", board);
        drawNode(g2d, c4x, c4y, "C4", board);
        placePositions.put("C3", new Point(c3x, c3y));
        placePositions.put("C4", new Point(c4x, c4y));
        drawPath(g2d, s10x, s10y, c3x, c3y);
        drawPath(g2d, c3x, c3y, c4x, c4y);
        drawPath(g2d, c4x, c4y, centerX, centerY);
    }

    /**
     * 오각형 보드 그리기
     */
    private void drawPentagonBoard(Graphics2D g2d, Board board, int x, int y, int size) {
        int[] xPoints = new int[5];
        int[] yPoints = new int[5];
        int centerX = x + (size / 2);
        int centerY = y + (size / 2);
        int radius = size / 2;
        for (int i = 0; i < 5; i++) {
            double angle = 2 * Math.PI * i / 5 - Math.PI / 2;
            xPoints[i] = (int) (centerX + radius * Math.cos(angle));
            yPoints[i] = (int) (centerY + radius * Math.sin(angle));
        }
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawPolygon(xPoints, yPoints, 5);
        // 꼭지점 노드
        for (int i = 0; i < 5; i++) {
            drawNode(g2d, xPoints[i], yPoints[i], String.valueOf(i * 5), board);
            placePositions.put(String.valueOf(i * 5), new Point(xPoints[i], yPoints[i]));
        }
        // 변의 중간 노드들
        for (int i = 0; i < 5; i++) {
            int nextIdx = (i + 1) % 5;
            for (int j = 1; j < 5; j++) {
                int nodeX = xPoints[i] + (xPoints[nextIdx] - xPoints[i]) * j / 5;
                int nodeY = yPoints[i] + (yPoints[nextIdx] - yPoints[i]) * j / 5;
                String placeId = String.valueOf(i * 5 + j);
                drawNode(g2d, nodeX, nodeY, placeId, board);
                placePositions.put(placeId, new Point(nodeX, nodeY));
            }
        }
        // 중앙 노드 (하나만)
        drawNode(g2d, centerX, centerY, "C_1", board);
        placePositions.put("C_1", new Point(centerX, centerY));
        // 대각선 경로: 각 꼭지점~중앙 1/3, 2/3 지점에 C노드 배치 (C10, C9, C1, C2, C3, C4, C5, C6, C8, C7)
        String[] cNodeIds = {"C10", "C9", "C1", "C2", "C3", "C4", "C5", "C6", "C8", "C7"};
        for (int i = 0; i < 5; i++) {
            int c1x = (int)(xPoints[i] * 2.0/3 + centerX * 1.0/3);
            int c1y = (int)(yPoints[i] * 2.0/3 + centerY * 1.0/3);
            int c2x = (int)(xPoints[i] * 1.0/3 + centerX * 2.0/3);
            int c2y = (int)(yPoints[i] * 1.0/3 + centerY * 2.0/3);
            String c1id = cNodeIds[i * 2];
            String c2id = cNodeIds[i * 2 + 1];
            drawNode(g2d, c1x, c1y, c1id, board);
            drawNode(g2d, c2x, c2y, c2id, board);
            placePositions.put(c1id, new Point(c1x, c1y));
            placePositions.put(c2id, new Point(c2x, c2y));
            drawPath(g2d, xPoints[i], yPoints[i], c1x, c1y);
            drawPath(g2d, c1x, c1y, c2x, c2y);
            drawPath(g2d, c2x, c2y, centerX, centerY);
        }
    }

    /**
     * 육각형 보드 그리기
     */
    private void drawHexagonBoard(Graphics2D g2d, Board board, int x, int y, int size) {
        int[] xPoints = new int[6];
        int[] yPoints = new int[6];
        int centerX = x + (size / 2);
        int centerY = y + (size / 2);
        int radius = size / 2;
        for (int i = 0; i < 6; i++) {
            double angle = 2 * Math.PI * i / 6 - Math.PI / 2;
            xPoints[i] = (int) (centerX + radius * Math.cos(angle));
            yPoints[i] = (int) (centerY + radius * Math.sin(angle));
        }
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawPolygon(xPoints, yPoints, 6);
        // 꼭지점 노드
        for (int i = 0; i < 6; i++) {
            drawNode(g2d, xPoints[i], yPoints[i], String.valueOf(i * 5), board);
            placePositions.put(String.valueOf(i * 5), new Point(xPoints[i], yPoints[i]));
        }
        // 변의 중간 노드들
        for (int i = 0; i < 6; i++) {
            int nextIdx = (i + 1) % 6;
            for (int j = 1; j < 5; j++) {
                int nodeX = xPoints[i] + (xPoints[nextIdx] - xPoints[i]) * j / 5;
                int nodeY = yPoints[i] + (yPoints[nextIdx] - yPoints[i]) * j / 5;
                String placeId = String.valueOf(i * 5 + j);
                drawNode(g2d, nodeX, nodeY, placeId, board);
                placePositions.put(placeId, new Point(nodeX, nodeY));
            }
        }
        // 중앙 노드 (하나만)
        drawNode(g2d, centerX, centerY, "C_1", board);
        placePositions.put("C_1", new Point(centerX, centerY));
        // 대각선 경로: 각 꼭지점~중앙 1/3, 2/3 지점에 C노드 배치 (C12, C11, C1-C8, C10, C9)
        String[] cNodeIds = {"C12", "C11", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C10", "C9"};
        for (int i = 0; i < 6; i++) {
            int c1x = (int)(xPoints[i] * 2.0/3 + centerX * 1.0/3);
            int c1y = (int)(yPoints[i] * 2.0/3 + centerY * 1.0/3);
            int c2x = (int)(xPoints[i] * 1.0/3 + centerX * 2.0/3);
            int c2y = (int)(yPoints[i] * 1.0/3 + centerY * 2.0/3);
            String c1id = cNodeIds[i * 2];
            String c2id = cNodeIds[i * 2 + 1];
            drawNode(g2d, c1x, c1y, c1id, board);
            drawNode(g2d, c2x, c2y, c2id, board);
            placePositions.put(c1id, new Point(c1x, c1y));
            placePositions.put(c2id, new Point(c2x, c2y));
            drawPath(g2d, xPoints[i], yPoints[i], c1x, c1y);
            drawPath(g2d, c1x, c1y, c2x, c2y);
            drawPath(g2d, c2x, c2y, centerX, centerY);
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
    private void drawNode(Graphics2D g2d, int x, int y, String placeId, Board board) {
        Place place = board.getPlaceById(placeId);
        int nodeSize = NODE_SIZE;
        // 배경색으로 더 크게 한 번 채우기
        g2d.setColor(getBackground());
        g2d.fillOval(x - (nodeSize / 2) - 2, y - (nodeSize / 2) - 2, nodeSize + 4, nodeSize + 4);

        if (place != null) {
            if (place.isJunction()) {
                g2d.setColor(Color.ORANGE);
                g2d.fillOval(x - (nodeSize / 2) - 2, y - (nodeSize / 2) - 2, nodeSize + 4, nodeSize + 4);
            } else if (place.isCenter()) {
                g2d.setColor(Color.CYAN);
                g2d.fillOval(x - (nodeSize / 2) - 2, y - (nodeSize / 2) - 2, nodeSize + 4, nodeSize + 4);
            } else if (place.isStartingPoint()) {
                g2d.setColor(Color.GREEN);
                g2d.fillOval(x - (nodeSize / 2) - 2, y - (nodeSize / 2) - 2, nodeSize + 4, nodeSize + 4);
            } else if (place.isEndingPoint()) {
                g2d.setColor(Color.RED);
                g2d.fillOval(x - (nodeSize / 2) - 2, y - (nodeSize / 2) - 2, nodeSize + 4, nodeSize + 4);
            }
        }
        // 기본 노드 그리기
        g2d.setColor(Color.WHITE);
        g2d.fillOval(x - (nodeSize / 2), y - (nodeSize / 2), nodeSize, nodeSize);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3)); // 더 두꺼운 테두리
        g2d.drawOval(x - (nodeSize / 2), y - (nodeSize / 2), nodeSize, nodeSize);
        g2d.setStroke(new BasicStroke(2)); // 원래대로 복원
        // 노드 ID 표시
        g2d.setColor(Color.BLACK);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(placeId);
        int textHeight = fm.getHeight();
        g2d.drawString(placeId, x - (textWidth / 2), y + (textHeight / 4));
    }

    /**
     * 경로 그리기
     */
    private void drawPath(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2)); // 실선, 두께 2
        g2d.drawLine(x1, y1, x2, y2);
    }

    /**
     * 말 그리기
     */
    private void drawPieces(Graphics2D g2d) {
        Game game = controller.getGame();
        if (game == null) return;

        // 모든 플레이어의 말 그리기
        for (Player player : game.getPlayers()) {
            for (Piece piece : player.getPieces()) {
                Place place = piece.getCurrentPlace();
                if (place != null && placePositions.containsKey(place.getId())) {
                    Point point = placePositions.get(place.getId());

                    // 말 색상 설정
                    Color color = playerColors.getOrDefault(player.getId(), Color.GRAY);

                    // 현재 플레이어의 말은 테두리 강조
                    if (player.equals(game.getCurrentPlayer())) {
                        g2d.setColor(Color.BLACK);
                        g2d.fillOval(point.x - (PIECE_SIZE / 2) - 2,
                                point.y - (PIECE_SIZE / 2) - 2,
                                PIECE_SIZE + 4, PIECE_SIZE + 4);
                    }

                    // 말 그리기
                    g2d.setColor(color);
                    g2d.fillOval(point.x - (PIECE_SIZE / 2),
                            point.y - (PIECE_SIZE / 2),
                            PIECE_SIZE, PIECE_SIZE);

                    // 업힌 말이 있으면 숫자 표시
                    int stackCount = piece.getStackedPieces().size();
                    if (stackCount > 0) {
                        g2d.setColor(Color.WHITE);
                        g2d.drawString(String.valueOf(stackCount + 1),
                                point.x - 4, point.y + 4);
                    }
                }
            }
        }
    }

    /**
     * 보드 업데이트
     */
    public void updateBoard() {
        repaint();
    }
}