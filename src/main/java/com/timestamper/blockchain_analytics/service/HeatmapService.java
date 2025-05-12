package com.timestamper.blockchain_analytics.service;

import com.timestamper.blockchain_analytics.dto.heatmap.response.DailyInfoDetailResponseDTO;
import com.timestamper.blockchain_analytics.dto.heatmap.response.DailyInfoResponseDTO;
import com.timestamper.blockchain_analytics.dto.heatmap.response.MonthlyInfoDetailResponseDTO;
import com.timestamper.blockchain_analytics.dto.heatmap.response.MonthlyInfoResponseDTO;
import com.timestamper.blockchain_analytics.repository.HeatmapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.timestamper.blockchain_analytics.dto.heatmap.response.DailyInfoDetailResponseDTO.*;
import static com.timestamper.blockchain_analytics.dto.heatmap.response.MonthlyInfoDetailResponseDTO.*;

@Service
@RequiredArgsConstructor
public class HeatmapService {

    private final HeatmapRepository heatmapRepository;

    public List<MonthlyInfoResponseDTO> getMonthlyInfo(String fromAddress, YearMonth date) {
        String startDate = date
                .atDay(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        return heatmapRepository.selectMonthlyInfo(fromAddress, startDate).stream()
                                                                        .map(row -> new MonthlyInfoResponseDTO(
                                                                                (String) row[0],
                                                                                ((Number) row[1]).intValue()
                                                                        ))
                                                                        .toList();
    }

    public DailyInfoResponseDTO getDailyInfo(String fromAddress, LocalDate date) {
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String dayStr = date.getDayOfMonth() + "일";

        List<DailyInfoResponseDTO.TimeBlock> blocks = heatmapRepository
                .selectDailyInfo(fromAddress, dateStr)
                .stream()
                .map(row -> new DailyInfoResponseDTO.TimeBlock(
                        (String) row[0],
                        ((Number) row[1]).intValue()
                ))
                .toList();

        return new DailyInfoResponseDTO(dayStr, blocks);
    }

    public MonthlyInfoDetailResponseDTO getMonthlyInfoDetail(String fromAddress, LocalDate date) {
        LocalDate localDate = date.withDayOfMonth(1);
        String startDate = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<Object[]> rows = heatmapRepository.selectMonthlyDetail(fromAddress, startDate);
        int[] rank = {1};
        List<DataTransaction> data = rows.stream()
                .map(row -> new DataTransaction(
                        rank[0]++,
                        (String) row[0],
                        ((Number) row[1]).intValue(),
                        ((Number) row[2]).doubleValue(),
                        ((Number) row[3]).doubleValue()
                ))
                .toList();

        return new MonthlyInfoDetailResponseDTO(data);
    }

    public DailyInfoDetailResponseDTO getDailyInfoDetail(String fromAddress, LocalDateTime date) {
        int baseHour = (date.getHour() / 4) * 4;
        LocalDateTime start = date.withHour(baseHour).withMinute(0).withSecond(0);
        LocalDateTime end = start.plusHours(4);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<Object[]> rows = heatmapRepository.selectDailyDetail(
                fromAddress,
                start.format(formatter),
                end.format(formatter)
        );

        List<DataDailyTransaction> data = new ArrayList<>();
        int rank = 1;
        for (Object[] row : rows) {
            data.add(new DataDailyTransaction(
                    rank++,
                    (String) row[0],
                    ((Number) row[1]).intValue(),
                    ((Number) row[2]).doubleValue(),
                    ((Number) row[3]).doubleValue()
            ));
        }

        return new DailyInfoDetailResponseDTO(data);
    }
}
