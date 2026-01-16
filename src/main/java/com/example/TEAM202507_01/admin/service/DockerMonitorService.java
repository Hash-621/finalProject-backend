package com.example.TEAM202507_01.admin.service;

import com.example.TEAM202507_01.admin.dto.ContainerStatsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DockerMonitorService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    private static final String CADVISOR_URL = "http://cadvisor:8080/api/v1.3/docker/";

    public double getTotalCpuUsage() {
        try {
            String response = restTemplate.getForObject(CADVISOR_URL, String.class);

            // [수정 포인트] 제네릭 타입에 내부 클래스 경로(CAdvisorDto.ContainerInfo) 지정
            Map<String, ContainerStatsDto.ContainerInfo> containers = objectMapper.readValue(
                    response,
                    new TypeReference<Map<String, ContainerStatsDto.ContainerInfo>>() {
                    }
            );

            double totalCpuPercent = 0.0;

            // [수정 포인트] Map의 Value 타입도 변경
            for (Map.Entry<String, ContainerStatsDto.ContainerInfo> entry : containers.entrySet()) {
                ContainerStatsDto.ContainerInfo info = entry.getValue();
                List<ContainerStatsDto.Stat> stats = info.getStats(); // 내부 클래스 타입 사용

                if (stats != null && stats.size() >= 2) {
                    ContainerStatsDto.Stat current = stats.get(stats.size() - 1);
                    ContainerStatsDto.Stat prev = stats.get(stats.size() - 2);

                    totalCpuPercent += calculateCpuPercent(current, prev);
                }
            }

            return Math.round(totalCpuPercent * 100) / 100.0;

        } catch (Exception e) {
            log.error("cAdvisor 데이터 수집 실패", e);
            return 0.0;
        }
    }
    // 1. CPU 퍼센트 계산 함수 (누락 주의!)
    private double calculateCpuPercent(ContainerStatsDto.Stat current, ContainerStatsDto.Stat prev) {
        // CPU 사용량 차이 (나노초 단위)
        long totalUsageDelta = current.getCpu().getUsage().getTotal() - prev.getCpu().getUsage().getTotal();

        // 시간 차이 (나노초 단위)
        long timeDeltaNs = getNanos(current.getTimestamp()) - getNanos(prev.getTimestamp());

        if (timeDeltaNs <= 0) return 0.0;

        // (사용량 증가분 / 시간 경과분) * 100 = 사용률(%)
        return ((double) totalUsageDelta / timeDeltaNs) * 100.0;
    }

    // 2. 시간 파싱 함수 (누락 주의!)
    private long getNanos(String timestamp) {
        // String("2024-01-01T...") -> Instant -> Nanoseconds 변환
        Instant instant = Instant.parse(timestamp);
        return instant.getEpochSecond() * 1_000_000_000L + instant.getNano();
    }
}
