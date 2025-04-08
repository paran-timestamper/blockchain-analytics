package com.timestamper.blockchain_analytics.dto.visual.response;

import com.timestamper.blockchain_analytics.dto.visual.NodeMonthTransactionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class NodeMonthTransactionResponseDTO {
    private List<NodeMonthTransactionDTO> list;
}
