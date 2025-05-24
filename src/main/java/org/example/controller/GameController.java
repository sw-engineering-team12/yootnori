package org.example.controller;

import org.example.model.*;
import java.util.List;

/**
 * 게임 컨트롤러 인터페이스
 * UI 프레임워크에 독립적인 게임 로직을 정의합니다.
 */
public interface GameController {

    /**
     * 게임 초기화
     * @param settings 게임 설정
     */
    void initializeGame(GameSettings settings);

    /**
     * 윷 던지기 실행
     * @return 윷 결과
     */
    Yut.YutResult throwYut();

    /**
     * 특정 윷 결과 지정 (테스트용)
     * @param result 지정할 윷 결과
     * @return 지정된 윷 결과
     */
    Yut.YutResult setSpecificYutResult(Yut.YutResult result);

    /**
     * 말 이동 실행
     * @param piece 이동할 말
     * @param yutResult 이동에 사용할 윷 결과
     * @return 이동 후 위치
     */
    Place movePiece(Piece piece, Yut.YutResult yutResult);

    /**
     * 말 이동 실행 (기본 윷 결과 사용)
     * @param piece 이동할 말
     * @return 이동 후 위치
     */
    Place movePiece(Piece piece);

    /**
     * 현재 턴 플레이어의 이동 가능한 말 목록 반환
     * @return 이동 가능한 말 목록
     */
    List<Piece> getMovablePieces();

    /**
     * 현재 보류 중인 윷 결과 목록 반환
     * @return 윷 결과 목록
     */
    List<Yut.YutResult> getPendingYutResults();

    /**
     * 게임 객체 반환
     * @return 게임 객체
     */
    Game getGame();

    /**
     * 게임 상태 업데이트 알림
     * UI별로 구현 방식이 다를 수 있습니다.
     */
    void notifyGameStateChanged();

    /**
     * 게임 종료 처리
     * @param winner 승리한 플레이어
     */
    void handleGameEnd(Player winner);
}