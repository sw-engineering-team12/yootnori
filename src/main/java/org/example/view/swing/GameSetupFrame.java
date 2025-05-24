package org.example.view.swing;

import org.example.controller.GameController;
import org.example.model.Board;
import org.example.model.GameSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 게임 설정 화면
 */
public class GameSetupFrame extends JFrame {
    private JComboBox<String> boardTypeCombo;
    private JComboBox<Integer> playerCountCombo;
    private JComboBox<Integer> pieceCountCombo;
    private JButton startButton;

    public GameSetupFrame() {
        // 윈도우 설정
        setTitle("윷놀이 게임 설정");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null); // 화면 중앙에 표시

        // 컴포넌트 초기화
        initComponents();

        // 레이아웃 설정
        layoutComponents();
    }

    private void initComponents() {
        // 보드 타입 선택
        String[] boardTypes = {"사각형", "오각형", "육각형"};
        boardTypeCombo = new JComboBox<>(boardTypes);

        // 플레이어 수 선택 (2-4명)
        Integer[] playerCounts = {2, 3, 4};
        playerCountCombo = new JComboBox<>(playerCounts);

        // 말 개수 선택 (2-5개)
        Integer[] pieceCounts = {2, 3, 4, 5};
        pieceCountCombo = new JComboBox<>(pieceCounts);

        // 시작 버튼
        startButton = new JButton("게임 시작");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
    }

    private void layoutComponents() {
        // 전체 패널
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 보드 타입 패널
        JPanel boardTypePanel = new JPanel(new BorderLayout(10, 0));
        boardTypePanel.add(new JLabel("보드 형태:"), BorderLayout.WEST);
        boardTypePanel.add(boardTypeCombo, BorderLayout.CENTER);

        // 플레이어 수 패널
        JPanel playerCountPanel = new JPanel(new BorderLayout(10, 0));
        playerCountPanel.add(new JLabel("플레이어 수:"), BorderLayout.WEST);
        playerCountPanel.add(playerCountCombo, BorderLayout.CENTER);

        // 말 개수 패널
        JPanel pieceCountPanel = new JPanel(new BorderLayout(10, 0));
        pieceCountPanel.add(new JLabel("말 개수:"), BorderLayout.WEST);
        pieceCountPanel.add(pieceCountCombo, BorderLayout.CENTER);

        // 패널 추가
        mainPanel.add(boardTypePanel);
        mainPanel.add(playerCountPanel);
        mainPanel.add(pieceCountPanel);
        mainPanel.add(startButton);

        add(mainPanel);
    }

    private void startGame() {
        // 선택된 설정 가져오기
        Board.BoardType boardType;
        switch (boardTypeCombo.getSelectedIndex()) {
            case 1:
                boardType = Board.BoardType.PENTAGON;
                break;
            case 2:
                boardType = Board.BoardType.HEXAGON;
                break;
            default:
                boardType = Board.BoardType.SQUARE;
        }

        int playerCount = (Integer) playerCountCombo.getSelectedItem();
        int pieceCount = (Integer) pieceCountCombo.getSelectedItem();

        // 게임 설정 생성
        GameSettings settings = new GameSettings(playerCount, pieceCount, boardType);

        // 게임 컨트롤러 생성
        GameController controller = new GameController(settings);

        // 게임 화면 생성 및 표시
        GameFrame gameFrame = new GameFrame(controller);
        gameFrame.setVisible(true);

        // 설정 화면 닫기
        dispose();
    }
}