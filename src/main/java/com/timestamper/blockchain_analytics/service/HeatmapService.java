package com.timestamper.blockchain_analytics.service;

import com.timestamper.blockchain_analytics.dto.heatmap.DateInfoDTO;
import com.timestamper.blockchain_analytics.dto.heatmap.request.DateInfoRequestDTO;
import com.timestamper.blockchain_analytics.dto.heatmap.response.DataInfoResponseDTO;
import com.timestamper.blockchain_analytics.repository.HeatmapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HeatmapService {

    private final HeatmapRepository heatmapRepository;

    public DataInfoResponseDTO getInfo(DateInfoRequestDTO request) {
        LocalDateTime startDateTime = request.getStartDate();
        LocalDateTime endDateTime = startDateTime.plusDays(7);

        List<DateInfoDTO> dateInfoList = heatmapRepository.getInfo(startDateTime, endDateTime).stream()
                .map(p -> new DateInfoDTO(p.getDay().trim(), p.getHour(), p.getTxCount()))
                .toList();

        return new DataInfoResponseDTO(dateInfoList);
    }
}
