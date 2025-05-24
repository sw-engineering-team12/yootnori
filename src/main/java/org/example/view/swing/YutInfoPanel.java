package org.example.view.swing;

import org.example.model.Yut;

import javax.swing.*;
import java.awt.*;

/**
 * 윷 결과를 시각적으로 표시하는 패널
 */
public class YutInfoPanel extends JPanel {
    private Yut.YutResult currentResult;

    public YutInfoPanel() {
        setPreferredSize(new Dimension(150, 100));
        setBorder(BorderFactory.createTitledBorder("윷 결과"));
        setBackground(Color.WHITE);
    }

    public void setYutResult(Yut.YutResult result) {
        this.currentResult = result;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 윷 결과가 없으면 그리지 않음
        if (currentResult == null) {
            g2d.drawString("윷을 던져주세요", 30, 50);
            return;
        }

        // 윷 막대기 그리기
        int stickWidth = 15;
        int stickHeight = 60;
        int spacing = 20;
        int startX = (getWidth() - (stickWidth * 4 + spacing * 3)) / 2;
        int startY = (getHeight() - stickHeight) / 2;

        switch (currentResult) {
            case BACKDO:
                // 빽도: 앞면(배) 1개, 뒷면(등) 3개
                drawYutStick(g2d, startX, startY, stickWidth, stickHeight, true);  // 배
                drawYutStick(g2d, startX + stickWidth + spacing, startY, stickWidth, stickHeight, false);  // 등
                drawYutStick(g2d, startX + (stickWidth + spacing) * 2, startY, stickWidth, stickHeight, false);  // 등
                drawYutStick(g2d, startX + (stickWidth + spacing) * 3, startY, stickWidth, stickHeight, false);  // 등
                break;

            case DO:
                // 도: 앞면(배) 1개, 뒷면(등) 3개
                drawYutStick(g2d, startX, startY, stickWidth, stickHeight, false);  // 등
                drawYutStick(g2d, startX + stickWidth + spacing, startY, stickWidth, stickHeight, true);  // 배
                drawYutStick(g2d, startX + (stickWidth + spacing) * 2, startY, stickWidth, stickHeight, true);  // 배
                drawYutStick(g2d, startX + (stickWidth + spacing) * 3, startY, stickWidth, stickHeight, true);  // 배
                break;

            case GAE:
                // 개: 앞면(배) 2개, 뒷면(등) 2개
                drawYutStick(g2d, startX, startY, stickWidth, stickHeight, false);  // 등
                drawYutStick(g2d, startX + stickWidth + spacing, startY, stickWidth, stickHeight, false);  // 등
                drawYutStick(g2d, startX + (stickWidth + spacing) * 2, startY, stickWidth, stickHeight, true);  // 배
                drawYutStick(g2d, startX + (stickWidth + spacing) * 3, startY, stickWidth, stickHeight, true);  // 배
                break;

            case GEOL:
                // 걸: 앞면(배) 3개, 뒷면(등) 1개
                drawYutStick(g2d, startX, startY, stickWidth, stickHeight, false);  // 등
                drawYutStick(g2d, startX + stickWidth + spacing, startY, stickWidth, stickHeight, true);  // 배
                drawYutStick(g2d, startX + (stickWidth + spacing) * 2, startY, stickWidth, stickHeight, true);  // 배
                drawYutStick(g2d, startX + (stickWidth + spacing) * 3, startY, stickWidth, stickHeight, true);  // 배
                break;

            case YUT:
                // 윷: 앞면(배) 4개
                drawYutStick(g2d, startX, startY, stickWidth, stickHeight, false);  // 등
                drawYutStick(g2d, startX + stickWidth + spacing, startY, stickWidth, stickHeight, false);  // 등
                drawYutStick(g2d, startX + (stickWidth + spacing) * 2, startY, stickWidth, stickHeight, false);  // 등
                drawYutStick(g2d, startX + (stickWidth + spacing) * 3, startY, stickWidth, stickHeight, false);  // 등
                break;

            case MO:
                // 모: 앞면(배) 0개, 뒷면(등) 4개
                drawYutStick(g2d, startX, startY, stickWidth, stickHeight, true);  // 배
                drawYutStick(g2d, startX + stickWidth + spacing, startY, stickWidth, stickHeight, true);  // 배
                drawYutStick(g2d, startX + (stickWidth + spacing) * 2, startY, stickWidth, stickHeight, true);  // 배
                drawYutStick(g2d, startX + (stickWidth + spacing) * 3, startY, stickWidth, stickHeight, true);  // 배
                break;
        }

        // 결과 이름 표시
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        String resultText = currentResult.getName() + " (" + currentResult.getMoveCount() + "칸)";
        int textWidth = fm.stringWidth(resultText);
        g2d.drawString(resultText, (getWidth() - textWidth) / 2, getHeight() - 20);
    }

    /**
     * 윷 막대기 그리기
     * @param g2d 그래픽스 객체
     * @param x X 좌표
     * @param y Y 좌표
     * @param width 너비
     * @param height 높이
     * @param isBelly 앞면(배) 여부
     */
    private void drawYutStick(Graphics2D g2d, int x, int y, int width, int height, boolean isBelly) {
        // 막대기 기본 형태
        g2d.setColor(new Color(222, 184, 135)); // 나무 색
        g2d.fillRoundRect(x, y, width, height, 5, 5);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(x, y, width, height, 5, 5);

        // 앞면(배)은 중앙에 선 표시
        if (isBelly) {
            g2d.setColor(Color.BLACK);
            g2d.drawLine(x + width / 2, y + 10, x + width / 2, y + height - 10);
        }
    }
}