package com.timestamper.blockchain_analytics.service;

import com.timestamper.blockchain_analytics.dto.node.NodeInfoDTO;
import com.timestamper.blockchain_analytics.dto.node.request.NodeInfosRequestDTO;
import com.timestamper.blockchain_analytics.dto.node.request.NodeTransactionRequestDTO;
import com.timestamper.blockchain_analytics.dto.node.response.NodeInfosResponseDTO;
import com.timestamper.blockchain_analytics.dto.node.response.NodeTransactionResponseDTO;
import com.timestamper.blockchain_analytics.dto.node.response.TOP1AddressResponseDTO;
import com.timestamper.blockchain_analytics.enums.Filter;
import com.timestamper.blockchain_analytics.repository.NodeRepository;
import com.timestamper.blockchain_analytics.repository.projection.node.NodeTransactionProjection;
import com.timestamper.blockchain_analytics.service.crypto.CryptoPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NodeService {

    private final NodeRepository nodeRepository;
    private final CryptoPriceService cryptoPriceService;

    // TODO topFromAddress 변경 필요
    public TOP1AddressResponseDTO getTOP1Address() {
        String topFromAddress = "cro1yjjlx5qsrj5rxn5xtd5rkm6dcqzlchxkrvsmg6";
        List<NodeInfoDTO> lists = nodeRepository.findTopToAddressesByFromAddress(topFromAddress);
        List<NodeInfoDTO> toAddressesDTO = lists.stream()
                .map(list -> {
                    return new NodeInfoDTO(list.getToAddresses(), list.getDpdenom(), calculateRiskLevel());
                })
                .toList();

        return new TOP1AddressResponseDTO(topFromAddress, toAddressesDTO);
    }

    // TODO riskLevel 구하는 로직 필요
    public NodeInfosResponseDTO getNodeInfos(NodeInfosRequestDTO request) {
        List<NodeInfoDTO> result;

        switch (request.getFilter()) {
            case LATEST -> result = nodeRepository.findNodeInfosByLatest(request.getFromAddress(), request.getToken());
            case TRANSACTION -> result = nodeRepository.findNodeInfosByTransaction(request.getFromAddress(), request.getToken());
            case VALUE -> result = nodeRepository.findNodeInfosByValue(request.getFromAddress(), request.getToken());
            default -> throw new IllegalArgumentException("Unknown filter type: " + request.getFilter());
        }

        List<NodeInfoDTO> processed = result.stream()
                .map(item -> new NodeInfoDTO(item.getToAddresses(), item.getDpdenom(), calculateRiskLevel()))
                .toList();

        return new NodeInfosResponseDTO(request.getFromAddress(), processed);
    }

    public NodeInfosResponseDTO getNodeClickInfos(NodeInfosRequestDTO request) {
        List<NodeInfoDTO> result;

        switch (request.getFilter()) {
            case LATEST -> result = nodeRepository.findNodeInfosByLatestLimit10(request.getFromAddress(), request.getToken());
            case TRANSACTION -> result = nodeRepository.findNodeInfosByTransactionLimit10(request.getFromAddress(), request.getToken());
            case VALUE -> result = nodeRepository.findNodeInfosByValueLimit10(request.getFromAddress(), request.getToken());
            default -> throw new IllegalArgumentException("Unknown filter type: " + request.getFilter());
        }

        List<NodeInfoDTO> processed = result.stream()
                .map(item -> new NodeInfoDTO(item.getToAddresses(), item.getDpdenom(), calculateRiskLevel()))
                .toList();

        return new NodeInfosResponseDTO(request.getFromAddress(), processed);
    }

    private static String calculateRiskLevel() {
        return "green";
    }

    public List<NodeTransactionResponseDTO> getNodeTransaction(NodeTransactionRequestDTO request, Pageable pageable) {
        double tokenPrice = cryptoPriceService.getCryptoPrice(request.getToken(), "USD").block();

        List<NodeTransactionProjection> nodeTransaction = nodeRepository.findNodeTransaction(request.getFromAddress(), request.getToken(), pageable);

        return nodeTransaction.stream()
                .map(item -> {
                    Double amount = item.getAmount();
                    double value = (amount != null ? amount : 0.0) * tokenPrice;
                    return new NodeTransactionResponseDTO(
                            item.getTxHash(),
                            item.getDirection(),
                            item.getToAddress(),
                            value
                    );
                })
                .toList();
    }
}
