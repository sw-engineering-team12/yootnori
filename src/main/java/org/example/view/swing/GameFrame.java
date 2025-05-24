package org.example.view.swing;

import org.example.controller.GameController;
import org.example.model.Game;
import org.example.model.Piece;
import org.example.model.Yut;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * 게임 메인 화면 프레임
 */
public class GameFrame extends JFrame {
    private GameController controller;
    private GameBoardPanel boardPanel;
    private JPanel controlPanel;
    private JPanel infoPanel;
    private JPanel logPanel;

    private JLabel currentPlayerLabel;
    private JLabel yutResultLabel;
    private JTextArea logTextArea;
    private JButton randomYutButton;
    private JButton specificYutButton;
    private JComboBox<String> yutSelectionCombo;
    private JList<String> pieceList;
    private DefaultListModel<String> pieceListModel;
    private JButton moveButton;
    
    // 저장된 윷 결과 선택 관련 컴포넌트
    private JList<String> pendingYutList;
    private DefaultListModel<String> pendingYutListModel;
    private JLabel pendingYutLabel;

    /**
     * 생성자
     * @param controller 게임 컨트롤러
     */
    public GameFrame(GameController controller) {
        this.controller = controller;

        // 윈도우 설정
        setTitle("윷놀이 게임");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null); // 화면 중앙에 표시

        // 레이아웃 설정
        setLayout(new BorderLayout());

        // 컴포넌트 초기화
        initComponents();

        // 레이아웃 설정
        layoutComponents();

        // 컨트롤러에 UI 설정
        controller.setUI(this, boardPanel);
    }

    private void initComponents() {
        // 게임 보드 패널
        boardPanel = new GameBoardPanel(controller);

        // 컨트롤 패널 컴포넌트
        randomYutButton = new JButton("랜덤 윷 던지기");
        randomYutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Yut.YutResult result = controller.throwYut();
                updateYutResult(result);
                updatePieceList();
                updatePendingYutList();
            }
        });

        specificYutButton = new JButton("지정 윷 던지기");
        specificYutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) yutSelectionCombo.getSelectedItem();
                Yut.YutResult result;

                switch (selected) {
                    case "빽도":
                        result = Yut.YutResult.BACKDO;
                        break;
                    case "도":
                        result = Yut.YutResult.DO;
                        break;
                    case "개":
                        result = Yut.YutResult.GAE;
                        break;
                    case "걸":
                        result = Yut.YutResult.GEOL;
                        break;
                    case "윷":
                        result = Yut.YutResult.YUT;
                        break;
                    case "모":
                        result = Yut.YutResult.MO;
                        break;
                    default:
                        result = Yut.YutResult.DO;
                }

                Yut.YutResult setResult = controller.setSpecificYutResult(result);
                updateYutResult(setResult);
                updatePieceList();
                updatePendingYutList();
            }
        });

        String[] yutTypes = {"빽도", "도", "개", "걸", "윷", "모"};
        yutSelectionCombo = new JComboBox<>(yutTypes);

        pieceListModel = new DefaultListModel<>();
        pieceList = new JList<>(pieceListModel);
        pieceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // 저장된 윷 결과 리스트
        pendingYutListModel = new DefaultListModel<>();
        pendingYutList = new JList<>(pendingYutListModel);
        pendingYutList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pendingYutLabel = new JLabel("이동에 사용할 윷 결과:");

        moveButton = new JButton("말 이동");
        moveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedPieceIndex = pieceList.getSelectedIndex();
                if (selectedPieceIndex == -1) {
                    JOptionPane.showMessageDialog(GameFrame.this, "이동할 말을 선택해주세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                // 선택한 윷 결과 확인
                int selectedYutIndex = pendingYutList.getSelectedIndex();
                Yut.YutResult selectedYutResult = null;
                List<Yut.YutResult> pendingResults = controller.getPendingYutResults();
                
                if (selectedYutIndex != -1 && selectedYutIndex < pendingResults.size()) {
                    selectedYutResult = pendingResults.get(selectedYutIndex);
                } else if (!pendingResults.isEmpty()) {
                    // 선택하지 않았으면 첫 번째 결과 사용
                    selectedYutResult = pendingResults.get(0);
                } else {
                    JOptionPane.showMessageDialog(GameFrame.this, "사용할 윷 결과가 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                List<Piece> movablePieces = controller.getMovablePieces();
                if (movablePieces != null && !movablePieces.isEmpty() && selectedPieceIndex < movablePieces.size()) {
                    Piece selectedPiece = movablePieces.get(selectedPieceIndex);

                    // 업힌 말이고 출발점이 null 또는 start인지 확인
                    if (!selectedPiece.getStackedPieces().isEmpty() &&
                            (selectedPiece.getCurrentPlace() == null ||
                                    "시작점".equals(selectedPiece.getCurrentPlace().getName()) ||
                                    "start".equalsIgnoreCase(selectedPiece.getCurrentPlace().getName()))) {

                        JOptionPane.showMessageDialog(GameFrame.this,
                                "업힌 말입니다.",
                                "알림",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // 조건을 통과하면 말 이동 실행
                    controller.movePiece(selectedPiece, selectedYutResult);
                    
                    // 윷 결과 목록 업데이트
                    updatePendingYutList();
                }
            }
        });

        // 정보 패널 컴포넌트
        currentPlayerLabel = new JLabel("현재 턴: Player 1");
        yutResultLabel = new JLabel("윷 결과: 없음");

        // 로그 패널 컴포넌트
        logTextArea = new JTextArea(10, 30);
        logTextArea.setEditable(false);
    }

    private void layoutComponents() {
        // 컨트롤 패널 레이아웃
        controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(4, 1, 5, 5)); // 한 행 추가
        controlPanel.setBorder(BorderFactory.createTitledBorder("컨트롤"));

        JPanel yutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        yutPanel.add(randomYutButton);
        yutPanel.add(specificYutButton);
        yutPanel.add(yutSelectionCombo);

        JPanel piecePanel = new JPanel(new BorderLayout());
        piecePanel.add(new JLabel("이동 가능한 말:"), BorderLayout.NORTH);
        piecePanel.add(new JScrollPane(pieceList), BorderLayout.CENTER);
        
        // 저장된 윷 결과 패널
        JPanel pendingYutPanel = new JPanel(new BorderLayout());
        pendingYutPanel.add(pendingYutLabel, BorderLayout.NORTH);
        pendingYutPanel.add(new JScrollPane(pendingYutList), BorderLayout.CENTER);

        JPanel movePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        movePanel.add(moveButton);

        controlPanel.add(yutPanel);
        controlPanel.add(pendingYutPanel); // 새로운 패널 추가
        controlPanel.add(piecePanel);
        controlPanel.add(movePanel);

        // 정보 패널 레이아웃
        infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(2, 1));
        infoPanel.setBorder(BorderFactory.createTitledBorder("게임 정보"));
        infoPanel.add(currentPlayerLabel);
        infoPanel.add(yutResultLabel);
        infoPanel.setPreferredSize(new Dimension(300, 150)); // 크기 키움

        // 로그 패널 레이아웃
        logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("게임 로그"));
        logPanel.add(new JScrollPane(logTextArea), BorderLayout.CENTER);
        logPanel.setPreferredSize(new Dimension(300, 300)); // 로그 패널 크기

        // 사이드 패널 통합
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.add(controlPanel);
        sidePanel.add(infoPanel);
        sidePanel.add(logPanel);

        // 전체 레이아웃
        add(boardPanel, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);
    }

    /**
     * 윷 결과 업데이트
     * @param result 윷 결과
     */
    private void updateYutResult(Yut.YutResult result) {
        if (result != null) {
            yutResultLabel.setText("마지막 윷 결과: " + result.getName() + " (" + result.getMoveCount() + "칸)");
        } else {
            yutResultLabel.setText("윷 결과: 없음");
        }
    }

    /**
     * 이동 가능한 말 목록 업데이트
     */
    private void updatePieceList() {
        pieceListModel.clear();
        List<Piece> movablePieces = controller.getMovablePieces();

        if (movablePieces != null) {
            for (Piece piece : movablePieces) {
                String location = piece.getCurrentPlace() != null ?
                        piece.getCurrentPlace().getName() : "시작점";

                // 업힌 말 정보
                String stackInfo = piece.getStackedPieces().isEmpty() ?
                        "" : " (업힌 말: " + piece.getStackedPieces().size() + "개)";

                // 업혀있는 상태 정보 (이 말을 업고 있는 말)
                String carriedInfo = piece.isCarried() ?
                        " [" + piece.getCarriedBy().getId() + "에 업힘]" : "";

                pieceListModel.addElement(piece.getId() + " - " + location + stackInfo + carriedInfo);
            }
        }
    }
    
    /**
     * 저장된 윷 결과 목록 업데이트
     */
    private void updatePendingYutList() {
        pendingYutListModel.clear();
        List<Yut.YutResult> pendingResults = controller.getPendingYutResults();
        
        if (pendingResults != null && !pendingResults.isEmpty()) {
            for (Yut.YutResult result : pendingResults) {
                pendingYutListModel.addElement(result.getName() + " (" + result.getMoveCount() + "칸)");
            }
            pendingYutLabel.setText("이동에 사용할 윷 결과 선택 (" + pendingResults.size() + "개):");
        } else {
            pendingYutLabel.setText("이동에 사용할 윷 결과가 없습니다.");
        }
    }

    /**
     * 게임 정보 업데이트
     */
    public void updateGameInfo() {
        Game game = controller.getGame();
        currentPlayerLabel.setText("현재 턴: " + game.getCurrentPlayer().getName());

        // 게임 로그 업데이트
        logTextArea.setText("");
        List<String> logs = game.getGameLog();
        for (String log : logs) {
            logTextArea.append(log + "\n");
        }

        // 항상 최신 로그가 보이도록 스크롤
        logTextArea.setCaretPosition(logTextArea.getDocument().getLength());

        // 윷 결과 업데이트
        updateYutResult(game.getLastYutResult());

        // 말 목록 업데이트
        updatePieceList();
        
        // 저장된 윷 결과 목록 업데이트
        updatePendingYutList();
    }
}