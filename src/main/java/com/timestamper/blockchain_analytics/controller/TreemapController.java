package com.timestamper.blockchain_analytics.controller;

import com.timestamper.blockchain_analytics.dto.treemap.request.TreemapInfoRequestDTO;
import com.timestamper.blockchain_analytics.dto.treemap.response.TreemapInfoResponseDTO;
import com.timestamper.blockchain_analytics.service.TreemapService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/treemap")
public class TreemapController {

    private final TreemapService treemapService;

    @PostMapping("info")
    public List<TreemapInfoResponseDTO> getTreemapInfo(@RequestBody TreemapInfoRequestDTO request) {
        return treemapService.getTreemapInfo(request.getFromAddress());
    }
}
