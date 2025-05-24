package com.timestamper.blockchain_analytics.service;

import com.timestamper.blockchain_analytics.dto.TransactionFilteredAmountResponse;
import com.timestamper.blockchain_analytics.dto.TransactionFilteredLatestDTO;
import com.timestamper.blockchain_analytics.dto.TransactionFilteredVolumeDTO;
import com.timestamper.blockchain_analytics.dto.transaction.response.TransactionGraphResponseDTO;
import com.timestamper.blockchain_analytics.entity.Transaction;
import com.timestamper.blockchain_analytics.repository.TransactionRepository;
import com.timestamper.blockchain_analytics.repository.projection.transaction.MvTopAddressWithChildrenProjection;
import com.timestamper.blockchain_analytics.utils.api.TokenPriceAPIClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TokenPriceAPIClient tokenPriceAPIClient;
    private final TransactionRepository transactionRepository;

    public List<TransactionFilteredVolumeDTO> getTransactionFilteredVolume(String token) {
        return transactionRepository.findByTransactionFilteredVolume(token);
    }

    public List<TransactionFilteredLatestDTO> getTransactionFilteredLatest(String token) {
        return transactionRepository.findByTransactionFilteredLatest(token);
    }

    public TransactionFilteredAmountResponse getTransactionFilteredAmount(String token) {
        return new TransactionFilteredAmountResponse(tokenPriceAPIClient.updatePrice(token, "USD"),
                                                     transactionRepository.findByTransactionFilteredAmount(token));
    }

    public List<Transaction> getTransactionByAddress(String token, String fromAddress) {
        return transactionRepository.findByDpdenomAndFromAddressOrderByTimestampDesc(token, fromAddress);
    }

    public TransactionGraphResponseDTO getGraph(String fromAddress) {
        return (fromAddress == null || fromAddress.isBlank())
                ? getGlobalGraph()
                : getAddressGraph(fromAddress);
    }

    private TransactionGraphResponseDTO getGlobalGraph() {
        List<MvTopAddressWithChildrenProjection> rows = transactionRepository.findAllTopAddressWithChildren();

        Map<String, Map<String, List<MvTopAddressWithChildrenProjection>>> grouped =
                rows.stream().collect(Collectors.groupingBy(
                        MvTopAddressWithChildrenProjection::getSectionLabel,
                        LinkedHashMap::new,
                        Collectors.groupingBy(MvTopAddressWithChildrenProjection::getToAddress)
                ));

        List<TransactionGraphResponseDTO.SectionData> sections = new ArrayList<>();

        for (Map.Entry<String, Map<String, List<MvTopAddressWithChildrenProjection>>> sectionEntry : grouped.entrySet()) {
            String section = sectionEntry.getKey();

            List<TransactionGraphResponseDTO.TokenData> tokenDataList = sectionEntry.getValue().entrySet().stream()
                    .map(addrEntry -> {
                        List<MvTopAddressWithChildrenProjection> list = addrEntry.getValue();
                        MvTopAddressWithChildrenProjection first = list.get(0);

                        List<TransactionGraphResponseDTO.SecondToAddressDTO> secondNodes = list.stream()
                                .filter(r -> r.getSecondToAddress() != null)
                                .map(r -> new TransactionGraphResponseDTO.SecondToAddressDTO(
                                        r.getSecondToken(),
                                        r.getSecondEcosystem(),
                                        r.getSecondNodeSize(),
                                        r.getSecondValue()
                                ))
                                .toList();

                        return new TransactionGraphResponseDTO.TokenData(
                                first.getToken(),
                                first.getEcosystem(),
                                first.getNodeSize(),
                                first.getValue(),
                                first.getTotalValue(),
                                first.getTransactionAmount(),
                                first.getToAddress(),
                                secondNodes
                        );
                    })
                    .sorted(Comparator.comparing(TransactionGraphResponseDTO.TokenData::getTotalValue).reversed())
                    .toList();

            sections.add(new TransactionGraphResponseDTO.SectionData(section, tokenDataList));
        }

        return new TransactionGraphResponseDTO("GLOBAL", sections);
    }

    private TransactionGraphResponseDTO getAddressGraph(String fromAddress) {
        return null;
    }
}
