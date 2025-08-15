package com.timestamper.blockchain_analytics.service;

import com.timestamper.blockchain_analytics.dto.treemap.response.TreemapInfoResponseDTO;
import com.timestamper.blockchain_analytics.repository.TreemapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TreemapService {

    private final TreemapRepository treemapRepository;

    public List<TreemapInfoResponseDTO> getTreemapInfo(String fromAddress) {
        return treemapRepository.selectTreemapInfo(fromAddress).stream()
                .map(row -> new TreemapInfoResponseDTO(
                        (String) row[0],
                        ((Number) row[1]).doubleValue(),
                        (String) row[2]
                ))
                .toList();
    }
}
