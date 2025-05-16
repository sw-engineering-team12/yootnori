package org.example.viewfx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.example.controller.GameController;
import javafx.scene.text.Text;
import org.example.model.Board.BoardType;
import java.util.Arrays;
import org.example.model.Piece;
import org.example.model.Place;

import java.util.List;

public class GameBoardCanvasFX extends Canvas {
    private GameController controller;

    public GameBoardCanvasFX(GameController controller) {
        super(700, 700); // 기본 크기
        this.controller = controller;
        setFocusTraversable(true);
        setOnMouseClicked(e -> draw()); // 클릭 시 다시 그리기 (디버깅용)
    }

    public void updateBoard() {
        draw();
    }

    private void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        BoardType boardType = controller.getGame().getGameSettings().getBoardType();

        switch (boardType) {
            case SQUARE:
                drawSquareBoard(gc);
                break;
            case PENTAGON:
                drawPentagonBoard(gc);
                break;
            case HEXAGON:
                drawHexagonBoard(gc);
                break;
            default:
                drawSquareBoard(gc);
        }
    }





    private void drawSquareBoard(GraphicsContext gc) {
        double padding = 50;
        double cellSize = 70; // 크기 적당히 크게 설정
        double radius = 25; // 원의 반지름 (크기 조정 가능)

        gc.clearRect(0, 0, getWidth(), getHeight());

        // 총 외곽 20개 점 + 중앙 1개 + 대각선 위 추가 점 8개 = 29개
        double[][] positions = new double[29][2];

// 모서리 위치 (4개)
        positions[0] = new double[]{padding, padding};                             // 좌상단
        positions[5] = new double[]{padding + 5 * cellSize, padding};              // 우상단
        positions[10] = new double[]{padding + 5 * cellSize, padding + 5 * cellSize}; // 우하단
        positions[15] = new double[]{padding, padding + 5 * cellSize};             // 좌하단

// 상단 라인 (positions[0]은 좌상단, positions[1] ~ [4]는 중간, [5]는 우상단)
        for (int i = 1; i <= 4; i++) // positions[1]~[4]
            positions[i] = new double[]{padding + i * cellSize, padding};

// 우측 라인 (positions[6] ~ [9], [5]는 우상단, [10]은 우하단)
        for (int i = 1; i <= 4; i++) // positions[6]~[9]
            positions[5 + i] = new double[]{padding + 5 * cellSize, padding + i * cellSize};

// 하단 라인 (positions[10]은 우하단, [11]~[14]는 중간, [15]는 좌하단)
        for (int i = 1; i <= 4; i++) // positions[11]~[14]
            positions[10 + i] = new double[]{padding + (5 - i) * cellSize, padding + 5 * cellSize};

// 좌측 라인 (positions[15]은 좌하단, [16]~[19]는 위로 올라옴)
        for (int i = 1; i <= 4; i++) // positions[16]~[19]
            positions[15 + i] = new double[]{padding, padding + (5 - i) * cellSize};

// 중앙점 X (20번 인덱스)
        positions[20] = new double[]{padding + 2.5 * cellSize, padding + 2.5 * cellSize};


// 좌상 ↔ 우하 대각선 (C1~C4)
        positions[21] = new double[]{padding + 1 * cellSize, padding + 1 * cellSize};         // C1
        positions[22] = new double[]{padding + 1.75 * cellSize, padding + 1.75 * cellSize};   // C2
        positions[23] = new double[]{padding + 3.25 * cellSize, padding + 3.25 * cellSize};   // C3
        positions[24] = new double[]{padding + 4 * cellSize, padding + 4 * cellSize};         // C4

// 우상 ↔ 좌하 대각선 (C5~C8)
        positions[25] = new double[]{padding + 4 * cellSize, padding + 1 * cellSize};         // C5
        positions[26] = new double[]{padding + 3.25 * cellSize, padding + 1.75 * cellSize};   // C6
        positions[27] = new double[]{padding + 1.75 * cellSize, padding + 3.25 * cellSize};   // C7
        positions[28] = new double[]{padding + 1 * cellSize, padding + 4 * cellSize};         // C8



        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        // 외곽 라인 연결
        for (int i = 0; i < 20; i++) {
            double[] start = positions[i];
            double[] end = positions[(i + 1) % 20];
            gc.strokeLine(start[0], start[1], end[0], end[1]);
        }

//수정
        gc.strokeLine(positions[0][0], positions[0][1], positions[10][0], positions[10][1]);  // 좌상 ↔ 우하
        gc.strokeLine(positions[5][0], positions[5][1], positions[15][0], positions[15][1]);  // 우상 ↔ 좌하


// C1 → C2 → X
        gc.strokeLine(positions[21][0], positions[21][1], positions[22][0], positions[22][1]);
        gc.strokeLine(positions[22][0], positions[22][1], positions[20][0], positions[20][1]);

// C3 → C4 → X
        gc.strokeLine(positions[23][0], positions[23][1], positions[24][0], positions[24][1]);
        gc.strokeLine(positions[24][0], positions[24][1], positions[20][0], positions[20][1]);

// C5 → C6 → X
        gc.strokeLine(positions[25][0], positions[25][1], positions[26][0], positions[26][1]);
        gc.strokeLine(positions[26][0], positions[26][1], positions[20][0], positions[20][1]);

// C7 → C8 → X
        gc.strokeLine(positions[27][0], positions[27][1], positions[28][0], positions[28][1]);
        gc.strokeLine(positions[28][0], positions[28][1], positions[20][0], positions[20][1]);


        String[] outerIds = {
                "0", "1", "2", "3", "4",
                "5", "6", "7", "8", "9",
                "10", "11", "12", "13", "14",
                "15", "16", "17", "18", "19"
        };


// 위치 원 그리기
        gc.setFill(Color.WHITE);
        gc.setFont(new Font(12));

        for (int i = 0; i < positions.length; i++) {
            double x = positions[i][0] - radius / 2;
            double y = positions[i][1] - radius / 2;

            gc.fillOval(x, y, radius, radius);
            gc.strokeOval(x, y, radius, radius);

            String id;
            if (i < 20) {
                id = outerIds[i];
            } else if (i == 20) {
                id = "X";
            } else {
                id = "C" + (i - 20);
            }

            Place place = controller.getGame().getBoard().getPlaceById(id);
            String text = (place != null && place.getName() != null) ? place.getName() : "?";

            // ⬇ 중앙 정렬된 텍스트
            gc.setFill(Color.BLACK);
            Text textNode = new Text(text);
            textNode.setFont(gc.getFont());
            double textWidth = textNode.getLayoutBounds().getWidth();
            double textHeight = textNode.getLayoutBounds().getHeight();
            double centerX = positions[i][0] - textWidth / 2;
            double centerY = positions[i][1] + textHeight / 4;
            gc.fillText(text, centerX, centerY);

            gc.setFill(Color.WHITE); // 다음 원을 위해 초기화
        }




// 중요 지점 강조
        highlightCircle(gc, positions[0], radius, Color.GREEN);     // 시작점 S (좌상단)
        highlightCircle(gc, positions[5], radius, Color.GOLD);      // 우상단 모서리
        highlightCircle(gc, positions[10], radius, Color.GOLD);     // 우하단 모서리
        highlightCircle(gc, positions[15], radius, Color.GOLD);     // 좌하단 모서리
        highlightCircle(gc, positions[20], radius, Color.GOLDENROD); // 중앙 X


        for (int i = 0; i < positions.length; i++) {
            String id;
            if (i < 24) id = String.valueOf(i);
            else if (i == 24) id = "X";
            else id = "C" + (i - 24);

            Place place = controller.getGame().getBoard().getPlaceById(id);
            if (place == null) continue;

            List<Piece> pieces = place.getPieces();
            for (int j = 0; j < pieces.size(); j++) {
                Piece piece = pieces.get(j);
                double px = positions[i][0] + j * 6 - 10;  // 겹치지 않게 약간씩 위치 이동
                double py = positions[i][1] + 10;

                gc.setFill(piece.getPlayer().getColor()); // 플레이어별 색상
                gc.fillOval(px, py, 15, 15);

                gc.setFill(Color.BLACK);
                gc.setFont(new Font(10));
                gc.fillText(piece.getId(), px, py + 8); // 말 ID 표시
            }
        }
        // ===== 말 그리기 (추가) =====
        for (int i = 0; i < positions.length; i++) {
            String id;
            if (i < 24) id = String.valueOf(i);
            else if (i == 24) id = "X";
            else id = "C" + (i - 24);

            Place place = controller.getGame().getBoard().getPlaceById(id);
            if (place == null) continue;

            List<Piece> pieces = place.getPieces();
            for (int j = 0; j < pieces.size(); j++) {
                Piece piece = pieces.get(j);
                double px = positions[i][0] + j * 8 - 10;
                double py = positions[i][1] + 10;

                gc.setFill(piece.getPlayer().getColor());  // 플레이어별 색상
                gc.fillOval(px, py, 15, 15);

                gc.setFill(Color.BLACK);
                gc.setFont(new Font(10));
                gc.fillText(pieces.get(j).getId(), px + 1, py + 10);
            }
        }

    }

    // 위치 강조 메서드
    private void highlightCircle(GraphicsContext gc, double[] position, double radius, Color color) {
        gc.setStroke(color);
        gc.setLineWidth(3);
        double x = position[0] - radius / 2;
        double y = position[1] - radius / 2;
        gc.strokeOval(x, y, radius, radius);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
    }

    private void drawPentagonBoard(GraphicsContext gc) {
        double padding = 50;
        double size = 500;
        double radius = 25;
        double centerX = padding + size / 2;
        double centerY = padding + size / 2;
        double outerRadius = size / 2;

        // 외곽 꼭짓점 5개 위치 계산
        double[][] outerPoints = new double[5][2];
        for (int i = 0; i < 5; i++) {
            double angle = 2 * Math.PI * i / 5 - Math.PI / 2;
            outerPoints[i][0] = centerX + outerRadius * Math.cos(angle);
            outerPoints[i][1] = centerY + outerRadius * Math.sin(angle);
        }

        // 외곽 점 25개 계산
        double[][] positions = new double[36][2]; // 0~24 외곽, 25 = 중앙, 26~35 = C1~C10
        int idx = 0;
        for (int i = 0; i < 5; i++) {
            int next = (i + 1) % 5;
            for (int j = 0; j < 5; j++) {
                double x = outerPoints[i][0] + (outerPoints[next][0] - outerPoints[i][0]) * j / 5.0;
                double y = outerPoints[i][1] + (outerPoints[next][1] - outerPoints[i][1]) * j / 5.0;
                positions[idx++] = new double[]{x, y};
            }
        }

        // 중앙점
        positions[25] = new double[]{centerX, centerY};

        // 대각선 점 (2개씩 × 5방향 = 10개)
        for (int i = 0; i < 5; i++) {
            double[] outer = outerPoints[i];
            // 1/3 지점
            positions[26 + i * 2] = new double[]{
                    outer[0] * 2 / 3 + centerX / 3,
                    outer[1] * 2 / 3 + centerY / 3
            };
            // 2/3 지점
            positions[27 + i * 2] = new double[]{
                    outer[0] / 3 + centerX * 2 / 3,
                    outer[1] / 3 + centerY * 2 / 3
            };
        }

        // 연결선 (외곽)
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        for (int i = 0; i < 25; i++) {
            int next = (i + 1) % 25;
            gc.strokeLine(positions[i][0], positions[i][1], positions[next][0], positions[next][1]);
        }

        // 연결선 (대각선)
        for (int i = 0; i < 5; i++) {
            double[] outer = outerPoints[i];
            double[] c1 = positions[26 + i * 2];
            double[] c2 = positions[27 + i * 2];
            gc.strokeLine(outer[0], outer[1], c1[0], c1[1]);
            gc.strokeLine(c1[0], c1[1], c2[0], c2[1]);
            gc.strokeLine(c2[0], c2[1], centerX, centerY);
        }

        // 텍스트 라벨
        String[] labels = {
                "0", "1", "2", "3", "4",    // 꼭짓점 → 다음 꼭짓점
                "5", "6", "7", "8", "9",
                "10", "11", "12", "13", "14",
                "15", "16", "17", "18", "19",
                "20", "21", "22", "23", "24",  // 총 25개 외곽
                "X",  // 중앙
                "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "C10"
        };

        gc.setFont(new Font(12));

        for (int i = 0; i < positions.length; i++) {
            double x = positions[i][0] - radius / 2;
            double y = positions[i][1] - radius / 2;

            // 원 그리기
            gc.setFill(Color.WHITE);
            gc.fillOval(x, y, radius, radius);
            gc.setStroke(Color.BLACK);
            gc.strokeOval(x, y, radius, radius);

            // 텍스트
            String text = (i < labels.length) ? labels[i] : "?";
            Text textNode = new Text(text);
            textNode.setFont(gc.getFont());
            double textWidth = textNode.getLayoutBounds().getWidth();
            double textHeight = textNode.getLayoutBounds().getHeight();
            gc.setFill(Color.BLACK);
            gc.fillText(text, positions[i][0] - textWidth / 2, positions[i][1] + textHeight / 4);
        }

        // 강조 (시작점 0, 꼭짓점 5, 10, 15, 20, 중앙)
        highlightCircle(gc, positions[0], radius, Color.GREEN);       // 시작점
        highlightCircle(gc, positions[5], radius, Color.GOLD);
        highlightCircle(gc, positions[10], radius, Color.GOLD);
        highlightCircle(gc, positions[15], radius, Color.GOLD);
        highlightCircle(gc, positions[20], radius, Color.GOLD);
        highlightCircle(gc, positions[25], radius, Color.GOLDENROD);  // 중앙점

        // 말 그리기
        for (int i = 0; i < positions.length; i++) {
            if (i >= labels.length) continue; // 잘못된 인덱스 방지

            String id = labels[i];
            Place place = controller.getGame().getBoard().getPlaceById(id);
            if (place == null) continue;

            List<Piece> pieces = place.getPieces();
            for (int j = 0; j < pieces.size(); j++) {
                Piece piece = pieces.get(j);
                double px = positions[i][0] + j * 6 - 10;
                double py = positions[i][1] + 10;

                gc.setFill(piece.getPlayer().getColor());
                gc.fillOval(px, py, 15, 15);

                gc.setFill(Color.BLACK);
                gc.setFont(new Font(10));
                gc.fillText(piece.getId(), px + 1, py + 10);
            }
        }

    }

    private void drawHexagonBoard(GraphicsContext gc) {
        double radius = 25;
        double centerX = getWidth() / 2;
        double centerY = getHeight() / 2;
        double outerRadius = 250;

        // 총 43개 위치: 0~29 (외곽), 30 (중앙 X), 31~42 (C1~C12)
        double[][] positions = new double[43][2];

        // 육각형 꼭짓점 6개 정의
        double[][] corners = new double[6][2];
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i - 30);
            corners[i][0] = centerX + outerRadius * Math.cos(angle);
            corners[i][1] = centerY + outerRadius * Math.sin(angle);
        }

        // 외곽 점 30개 (각 변마다 5점 = 시작점 + 중간 4개)
        int idx = 0;
        for (int i = 0; i < 6; i++) {
            double[] start = corners[i];
            double[] end = corners[(i + 1) % 6];
            for (int j = 0; j < 5; j++) {
                double t = j / 5.0;
                double x = start[0] + (end[0] - start[0]) * t;
                double y = start[1] + (end[1] - start[1]) * t;
                positions[idx++] = new double[]{x, y};
            }
        }

        // 중앙점
        positions[30] = new double[]{centerX, centerY};

        // 내부 대각선 점 C1~C12 (6방향 * 2점씩)
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i - 30);
            double x1 = centerX + outerRadius * Math.cos(angle) * 1 / 3;
            double y1 = centerY + outerRadius * Math.sin(angle) * 1 / 3;
            double x2 = centerX + outerRadius * Math.cos(angle) * 2 / 3;
            double y2 = centerY + outerRadius * Math.sin(angle) * 2 / 3;

            positions[31 + i * 2] = new double[]{x1, y1};
            positions[31 + i * 2 + 1] = new double[]{x2, y2};
        }

        // 라벨 설정
        String[] labels = new String[43];

        int offset = 25;
        for (int i = 0; i < 30; i++) {
            labels[i] = String.valueOf((i + 30 - offset) % 30);  // ← 반시계 회전
        }


        labels[30] = "X";
        for (int i = 31; i <= 42; i++) labels[i] = "C" + (i - 30);

        // 연결선 (외곽)
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        for (int i = 0; i < 30; i++) {
            int next = (i + 1) % 30;
            gc.strokeLine(positions[i][0], positions[i][1], positions[next][0], positions[next][1]);
        }

        // 내부 대각선 연결 (C1~C12 → X)
        for (int i = 31; i <= 42; i++) {
            gc.strokeLine(positions[i][0], positions[i][1], positions[30][0], positions[30][1]);
        }

        // 중심점 X ↔ 6개 꼭짓점 직접 연결
        int[] cornerIndices = {0, 5, 10, 15, 20, 25};
        for (int i : cornerIndices) {
            gc.strokeLine(positions[30][0], positions[30][1], positions[i][0], positions[i][1]);
        }

        // 위치 원 + 텍스트 출력
        gc.setFont(new Font(12));
        for (int i = 0; i < positions.length; i++) {
            double x = positions[i][0] - radius / 2;
            double y = positions[i][1] - radius / 2;

            gc.setFill(Color.WHITE);
            gc.fillOval(x, y, radius, radius);
            gc.setStroke(Color.BLACK);
            gc.strokeOval(x, y, radius, radius);

            String text = labels[i];
            Text textNode = new Text(text);
            textNode.setFont(gc.getFont());
            double textWidth = textNode.getLayoutBounds().getWidth();
            double textHeight = textNode.getLayoutBounds().getHeight();
            gc.setFill(Color.BLACK);
            gc.fillText(text, positions[i][0] - textWidth / 2, positions[i][1] + textHeight / 4);
        }

// === 강조: "0" → 초록, 나머지 → 노란색 ===
        String[] highlightLabels = {"0", "5", "10", "15", "20", "25"};

        for (String label : highlightLabels) {
            for (int i = 0; i < labels.length; i++) {
                if (labels[i].equals(label)) {
                    Color color = label.equals("0") ? Color.GREEN : Color.GOLD;
                    highlightCircle(gc, positions[i], radius, color);
                    break;
                }
            }
        }

// 중앙 "X" 강조
        highlightCircle(gc, positions[30], radius, Color.GOLDENROD);


        // ✅ 말 그리기 루프 추가 (중복 선언 없이)
        for (int k = 0; k < positions.length; k++) {
            if (k >= labels.length) continue;

            String id = labels[k];
            Place place = controller.getGame().getBoard().getPlaceById(id);
            if (place == null) continue;

            List<Piece> pieces = place.getPieces();
            for (int j = 0; j < pieces.size(); j++) {
                Piece piece = pieces.get(j);
                double px = positions[k][0] + j * 6 - 10;
                double py = positions[k][1] + 10;

                gc.setFill(piece.getPlayer().getColor());
                gc.fillOval(px, py, 15, 15);

                gc.setFill(Color.BLACK);
                gc.setFont(new Font(10));
                gc.fillText(piece.getId(), px + 1, py + 10);
            }
        }
    }



}