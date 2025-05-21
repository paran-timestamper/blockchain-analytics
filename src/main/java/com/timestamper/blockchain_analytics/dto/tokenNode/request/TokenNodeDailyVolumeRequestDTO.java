package com.timestamper.blockchain_analytics.dto.tokenNode.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class TokenNodeDailyVolumeRequestDTO {
    private List<String> tokens;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
}
