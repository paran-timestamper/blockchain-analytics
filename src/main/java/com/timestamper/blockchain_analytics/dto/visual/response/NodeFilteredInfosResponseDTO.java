package com.timestamper.blockchain_analytics.dto.visual.response;

import com.timestamper.blockchain_analytics.dto.visual.NodeFilteredInfosDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class NodeFilteredInfosResponseDTO {
    private List<NodeFilteredInfosDTO> list;
}
