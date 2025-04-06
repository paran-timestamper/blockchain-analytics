package com.timestamper.blockchain_analytics.dto.heatmap.response;

import com.timestamper.blockchain_analytics.dto.heatmap.DateInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DataInfoResponseDTO {
    private List<DateInfoDTO> dates;
}
