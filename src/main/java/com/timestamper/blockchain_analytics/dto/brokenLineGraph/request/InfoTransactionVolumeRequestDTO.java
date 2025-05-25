package com.timestamper.blockchain_analytics.dto.brokenLineGraph.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfoTransactionVolumeRequestDTO {
    private String fromAddress;
    private String token;


    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
}
