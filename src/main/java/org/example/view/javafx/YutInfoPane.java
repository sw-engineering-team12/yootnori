package org.example.view.javafx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.example.model.Yut;

/**
 * JavaFX 윷 결과를 시각적으로 표시하는 패널
 */
public class YutInfoPane extends Pane {
    private Yut.YutResult currentResult;
    private Canvas canvas;

    public YutInfoPane() {
        canvas = new Canvas(150, 100);
        getChildren().add(canvas);

        setStyle("-fx-border-color: gray; -fx-border-width: 1;");
        setPrefSize(150, 100);

        drawInitialState();
    }

    public void setYutResult(Yut.YutResult result) {
        this.currentResult = result;
        drawYutResult();
    }

    private void drawInitialState() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        gc.fillText("윷을 던져주세요", 30, 50);
    }

    private void drawYutResult() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // 윷 결과가 없으면 초기 상태 그리기
        if (currentResult == null) {
            drawInitialState();
            return;
        }

        // 윷 막대기 그리기
        double stickWidth = 15;
        double stickHeight = 60;
        double spacing = 20;
        double startX = (canvas.getWidth() - (stickWidth * 4 + spacing * 3)) / 2;
        double startY = (canvas.getHeight() - stickHeight) / 2;

        switch (currentResult) {
            case BACKDO:
                // 빽도: 앞면(배) 1개, 뒷면(등) 3개
                drawYutStick(gc, startX, startY, stickWidth, stickHeight, true);  // 배
                drawYutStick(gc, startX + stickWidth + spacing, startY, stickWidth, stickHeight, false);  // 등
                drawYutStick(gc, startX + (stickWidth + spacing) * 2, startY, stickWidth, stickHeight, false);  // 등
                drawYutStick(gc, startX + (stickWidth + spacing) * 3, startY, stickWidth, stickHeight, false);  // 등
                break;

            case DO:
                // 도: 앞면(배) 1개, 뒷면(등) 3개
                drawYutStick(gc, startX, startY, stickWidth, stickHeight, false);  // 등
                drawYutStick(gc, startX + stickWidth + spacing, startY, stickWidth, stickHeight, true);  // 배
                drawYutStick(gc, startX + (stickWidth + spacing) * 2, startY, stickWidth, stickHeight, true);  // 배
                drawYutStick(gc, startX + (stickWidth + spacing) * 3, startY, stickWidth, stickHeight, true);  // 배
                break;

            case GAE:
                // 개: 앞면(배) 2개, 뒷면(등) 2개
                drawYutStick(gc, startX, startY, stickWidth, stickHeight, false);  // 등
                drawYutStick(gc, startX + stickWidth + spacing, startY, stickWidth, stickHeight, false);  // 등
                drawYutStick(gc, startX + (stickWidth + spacing) * 2, startY, stickWidth, stickHeight, true);  // 배
                drawYutStick(gc, startX + (stickWidth + spacing) * 3, startY, stickWidth, stickHeight, true);  // 배
                break;

            case GEOL:
                // 걸: 앞면(배) 3개, 뒷면(등) 1개
                drawYutStick(gc, startX, startY, stickWidth, stickHeight, false);  // 등
                drawYutStick(gc, startX + stickWidth + spacing, startY, stickWidth, stickHeight, true);  // 배
                drawYutStick(gc, startX + (stickWidth + spacing) * 2, startY, stickWidth, stickHeight, true);  // 배
                drawYutStick(gc, startX + (stickWidth + spacing) * 3, startY, stickWidth, stickHeight, true);  // 배
                break;

            case YUT:
                // 윷: 앞면(배) 4개
                drawYutStick(gc, startX, startY, stickWidth, stickHeight, false);  // 등
                drawYutStick(gc, startX + stickWidth + spacing, startY, stickWidth, stickHeight, false);  // 등
                drawYutStick(gc, startX + (stickWidth + spacing) * 2, startY, stickWidth, stickHeight, false);  // 등
                drawYutStick(gc, startX + (stickWidth + spacing) * 3, startY, stickWidth, stickHeight, false);  // 등
                break;

            case MO:
                // 모: 앞면(배) 0개, 뒷면(등) 4개
                drawYutStick(gc, startX, startY, stickWidth, stickHeight, true);  // 배
                drawYutStick(gc, startX + stickWidth + spacing, startY, stickWidth, stickHeight, true);  // 배
                drawYutStick(gc, startX + (stickWidth + spacing) * 2, startY, stickWidth, stickHeight, true);  // 배
                drawYutStick(gc, startX + (stickWidth + spacing) * 3, startY, stickWidth, stickHeight, true);  // 배
                break;
        }

        // 결과 이름 표시
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        String resultText = currentResult.getName() + " (" + currentResult.getMoveCount() + "칸)";
        double textWidth = gc.getFont().getSize() * resultText.length() * 0.6; // 대략적인 텍스트 너비
        gc.fillText(resultText, (canvas.getWidth() - textWidth) / 2, canvas.getHeight() - 10);
    }

    /**
     * 윷 막대기 그리기
     * @param gc 그래픽스 컨텍스트
     * @param x X 좌표
     * @param y Y 좌표
     * @param width 너비
     * @param height 높이
     * @param isBelly 앞면(배) 여부
     */
    private void drawYutStick(GraphicsContext gc, double x, double y, double width, double height, boolean isBelly) {
        // 막대기 기본 형태
        gc.setFill(Color.rgb(222, 184, 135)); // 나무 색
        gc.fillRoundRect(x, y, width, height, 5, 5);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRoundRect(x, y, width, height, 5, 5);

        // 앞면(배)은 중앙에 선 표시
        if (isBelly) {
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeLine(x + width / 2, y + 10, x + width / 2, y + height - 10);
        }
    }
}