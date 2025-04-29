package org.example.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class YutTest {
    // 로깅을 위한 Logger 인스턴스 생성
    private static final Logger logger = Logger.getLogger(YutTest.class.getName());

    private Yut yut;

    @Before
    public void setUp() {
        yut = new Yut();
        logger.info("새로운 Yut 객체가 생성되었습니다.");
    }

    @Test
    public void testGetSpecificResult() {
        logger.info("지정 결과 테스트 시작");

        // 각 지정 결과가 정확히 반환되는지 확인
        for (Yut.YutResult expected : Yut.YutResult.values()) {
            Yut.YutResult actual = yut.getSpecificResult(expected);
            assertEquals(expected, actual);
            logger.info(String.format("지정 결과 테스트 성공: %s", expected.getName()));
        }

        logger.info("지정 결과 테스트 완료");
    }

    @Test
    public void testYutResultValues() {
        logger.info("윷 결과 값 테스트 시작");

        // 이동 칸 수 검증
        Map<Yut.YutResult, Integer> expectedMoveCounts = new HashMap<>();
        expectedMoveCounts.put(Yut.YutResult.BACKDO, -1);
        expectedMoveCounts.put(Yut.YutResult.DO, 1);
        expectedMoveCounts.put(Yut.YutResult.GAE, 2);
        expectedMoveCounts.put(Yut.YutResult.GEOL, 3);
        expectedMoveCounts.put(Yut.YutResult.YUT, 4);
        expectedMoveCounts.put(Yut.YutResult.MO, 5);

        for (Map.Entry<Yut.YutResult, Integer> entry : expectedMoveCounts.entrySet()) {
            Yut.YutResult result = entry.getKey();
            int expectedCount = entry.getValue();
            assertEquals(expectedCount, result.getMoveCount());
            logger.info(String.format("이동 칸 수 테스트 성공: %s = %d칸", result.getName(), expectedCount));
        }

        // 이름 검증
        Map<Yut.YutResult, String> expectedNames = new HashMap<>();
        expectedNames.put(Yut.YutResult.BACKDO, "빽도");
        expectedNames.put(Yut.YutResult.DO, "도");
        expectedNames.put(Yut.YutResult.GAE, "개");
        expectedNames.put(Yut.YutResult.GEOL, "걸");
        expectedNames.put(Yut.YutResult.YUT, "윷");
        expectedNames.put(Yut.YutResult.MO, "모");

        for (Map.Entry<Yut.YutResult, String> entry : expectedNames.entrySet()) {
            Yut.YutResult result = entry.getKey();
            String expectedName = entry.getValue();
            assertEquals(expectedName, result.getName());
            logger.info(String.format("이름 테스트 성공: %s", expectedName));
        }

        logger.info("윷 결과 값 테스트 완료");
    }

    @Test
    public void testThrowYutReturnsValidResult() {
        logger.info("윷 던지기 유효성 테스트 시작");

        // 여러 번 던져서 결과가 항상 유효한 열거형 값 중 하나인지 확인
        for (int i = 0; i < 10; i++) {
            Yut.YutResult result = yut.throwYut();
            assertNotNull(result);
            assertTrue(result == Yut.YutResult.BACKDO ||
                    result == Yut.YutResult.DO ||
                    result == Yut.YutResult.GAE ||
                    result == Yut.YutResult.GEOL ||
                    result == Yut.YutResult.YUT ||
                    result == Yut.YutResult.MO);
            logger.info(String.format("던지기 #%d 결과: %s (%d칸)",
                    i+1, result.getName(), result.getMoveCount()));
        }

        logger.info("윷 던지기 유효성 테스트 완료");
    }

    @Test
    public void testThrowYutDistribution() {
        logger.info("윷 던지기 분포 테스트 시작");

        // 윷 던지기 결과 분포 테스트 (대략적인 확률 검증)
        Map<Yut.YutResult, Integer> resultCounts = new HashMap<>();
        for (Yut.YutResult result : Yut.YutResult.values()) {
            resultCounts.put(result, 0);
        }

        int totalThrows = 10000;
        logger.info(String.format("총 %d회 윷 던지기 실행", totalThrows));

        for (int i = 0; i < totalThrows; i++) {
            Yut.YutResult result = yut.throwYut();
            resultCounts.put(result, resultCounts.get(result) + 1);

            // 1000번마다 중간 결과 로깅
            if ((i+1) % 1000 == 0) {
                logger.info(String.format("%d회 완료: %s", i+1, resultCountsToString(resultCounts)));
            }
        }

        // 빽도는 특수 케이스라 낮은 확률을 기대
        int backDoCount = resultCounts.get(Yut.YutResult.BACKDO);
        double backDoPercentage = (double)backDoCount / totalThrows;
        assertTrue("빽도 확률이 너무 높습니다: " + backDoPercentage,
                backDoCount < totalThrows * 0.10);
        logger.info(String.format("빽도 확률 검증 성공: %.2f%% (기준: 10%% 미만)", backDoPercentage * 100));

        // 다른 결과들은 비교적 균등하게 분포(정확한 확률은 구현에 따라 다름)
        for (Yut.YutResult result : Yut.YutResult.values()) {
            if (result != Yut.YutResult.BACKDO) {
                int count = resultCounts.get(result);
                double percentage = (double)count / totalThrows;
                String message = String.format("Result %s has unexpected distribution: %.2f%%",
                        result, percentage * 100);

                assertTrue(message, count >= totalThrows * 0.05 && count <= totalThrows * 0.40);
                logger.info(String.format("%s 확률 검증 성공: %.2f%% (기준: 5%%~40%%)",
                        result.getName(), percentage * 100));
            }
        }

        // 결과 분포 요약
        logger.info("윷 던지기 결과 분포 요약 (총 " + totalThrows + "회):");
        for (Yut.YutResult result : Yut.YutResult.values()) {
            int count = resultCounts.get(result);
            double percentage = (double)count / totalThrows * 100;
            logger.info(String.format("%s: %d (%.2f%%)", result.getName(), count, percentage));
            // 콘솔 출력도 유지
            System.out.printf("%s: %d (%.2f%%)\n", result.getName(), count, percentage);
        }

        logger.info("윷 던지기 분포 테스트 완료");
    }

    // 모든 윷놀이 결과가 최소 한 번은 나오는지 테스트
    @Test
    public void testAllResultsAppear() {
        logger.info("모든 결과 출현 테스트 시작");

        Map<Yut.YutResult, Boolean> resultAppeared = new HashMap<>();
        for (Yut.YutResult result : Yut.YutResult.values()) {
            resultAppeared.put(result, false);
        }

        int throwCount = 0;
        // 충분히 많은 수의 시도로 모든 결과가 한 번은 나와야 함
        for (throwCount = 0; throwCount < 1000 && resultAppeared.containsValue(false); throwCount++) {
            Yut.YutResult result = yut.throwYut();

            if (!resultAppeared.get(result)) {
                resultAppeared.put(result, true);
                logger.info(String.format("%d번째 던지기에서 %s 첫 등장", throwCount + 1, result.getName()));
            }

            // 100번마다 중간 결과 로깅
            if ((throwCount+1) % 100 == 0) {
                logger.info(String.format("%d회 완료, 남은 결과: %s",
                        throwCount+1, getRemainingResults(resultAppeared)));
            }
        }

        // 모든 결과가 최소 한 번 이상 나왔는지 확인
        boolean allAppeared = true;
        for (Yut.YutResult result : Yut.YutResult.values()) {
            boolean appeared = resultAppeared.get(result);
            if (!appeared) {
                allAppeared = false;
                logger.warning(String.format("%s가 %d회 던지기에서 나오지 않았습니다",
                        result.getName(), throwCount));
            }
            assertTrue("Result " + result.getName() + " did not appear in " + throwCount + " throws",
                    appeared);
        }

        if (allAppeared) {
            logger.info(String.format("모든 결과가 %d회 내에 적어도 한 번씩 나왔습니다", throwCount));
        }

        logger.info("모든 결과 출현 테스트 완료");
    }

    // 결과 카운트를 문자열로 변환하는 헬퍼 메소드
    private String resultCountsToString(Map<Yut.YutResult, Integer> counts) {
        StringBuilder sb = new StringBuilder();
        for (Yut.YutResult result : Yut.YutResult.values()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(result.getName()).append("=").append(counts.get(result));
        }
        return sb.toString();
    }

    // 아직 나오지 않은 결과를 문자열로 반환하는 헬퍼 메소드
    private String getRemainingResults(Map<Yut.YutResult, Boolean> appeared) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Yut.YutResult, Boolean> entry : appeared.entrySet()) {
            if (!entry.getValue()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(entry.getKey().getName());
            }
        }
        return sb.length() > 0 ? sb.toString() : "없음 (모두 등장)";
    }

}