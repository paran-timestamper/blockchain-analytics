package com.timestamper.blockchain_analytics.dto.heatmap.response;

import com.timestamper.blockchain_analytics.dto.heatmap.DataTransactionDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DataTransactionResponseDTO {
    List<DataTransactionDTO> dataTransactions;
}
