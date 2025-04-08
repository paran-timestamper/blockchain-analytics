package com.timestamper.blockchain_analytics.dto.visual.response;

import com.timestamper.blockchain_analytics.dto.visual.NodeDayTransactionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class NodeDayTransactionResponseDTO {
    List<NodeDayTransactionDTO> list;
}
