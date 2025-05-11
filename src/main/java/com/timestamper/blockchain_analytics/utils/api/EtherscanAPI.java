package com.timestamper.blockchain_analytics.utils.api;

import com.timestamper.blockchain_analytics.dto.visual.etherscan.EtherscanResponseDTO;
import com.timestamper.blockchain_analytics.dto.visual.etherscan.EtherscanResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Component
public class EtherscanAPI {

    @Value("${etherscan.api.key}")
    private String apiKey;

    @Value("${etherscan.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean isExchangeAddress(String address) {
        return false;
//        String url = String.format(baseUrl, address, apiKey);
//
//        try {
//            ResponseEntity<EtherscanResponseDTO> response =
//                    restTemplate.getForEntity(url, EtherscanResponseDTO.class);
//
//            EtherscanResponseDTO body = response.getBody();
//            if (body != null) {
//                EtherscanResultDTO[] results = body.getResult();
//                if (results != null && results.length > 0) {
//                    String[] labels = results[0].getLabels();
//                    return labels != null && Arrays.asList(labels).contains("Exchange");
//                }
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("API 호출 실패", e);
//        }
//
//        return false;
    }
}
