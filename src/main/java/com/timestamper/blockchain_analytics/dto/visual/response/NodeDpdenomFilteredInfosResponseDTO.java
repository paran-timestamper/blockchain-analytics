package com.timestamper.blockchain_analytics.dto.visual.response;

import com.timestamper.blockchain_analytics.dto.visual.NodeDpdenomFilteredInfosDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class NodeDpdenomFilteredInfosResponseDTO {
    private List<NodeDpdenomFilteredInfosDTO> list;
}
