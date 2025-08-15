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

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.timestamper.blockchain_analytics.dto.transaction.response.TransactionGraphResponseDTO.*;

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
        List<SectionData> sections = getData(rows);
        return new TransactionGraphResponseDTO("GLOBAL", sections);
    }

    public TransactionGraphResponseDTO getAddressGraph(String fromAddress) {
        List<MvTopAddressWithChildrenProjection> rows = transactionRepository.findAllTopAddressWithChildrenForAddress(fromAddress);
        List<SectionData> sections = getDataForAddress(rows);
        return new TransactionGraphResponseDTO(fromAddress, sections);
    }

    private static List<SectionData> getData(List<MvTopAddressWithChildrenProjection> rows) {
        Map<String, Map<String, List<MvTopAddressWithChildrenProjection>>> grouped =
                rows.stream().collect(Collectors.groupingBy(
                        MvTopAddressWithChildrenProjection::getSectionLabel,
                        LinkedHashMap::new,
                        Collectors.groupingBy(MvTopAddressWithChildrenProjection::getToAddress)
                ));

        List<SectionData> sections = new ArrayList<>();

        for (Map.Entry<String, Map<String, List<MvTopAddressWithChildrenProjection>>> sectionEntry : grouped.entrySet()) {
            String section = sectionEntry.getKey();

            List<TokenData> tokenDataList = sectionEntry.getValue().entrySet().stream()
                    .map(addrEntry -> {
                        List<MvTopAddressWithChildrenProjection> list = addrEntry.getValue();
                        MvTopAddressWithChildrenProjection first = list.get(0);

                        List<SecondToAddressDTO> secondNodes = list.stream()
                                .filter(r -> r.getSecondToAddress() != null)
                                .map(r -> new SecondToAddressDTO(
                                        r.getSecondToken(),
                                        r.getSecondEcosystem(),
                                        r.getSecondNodeSize(),
                                        r.getSecondValue()
                                ))
                                .toList();

                        return new TokenData(
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
                    .sorted(Comparator.comparing(TokenData::getTotalValue).reversed())
                    .toList();

            sections.add(new SectionData(section, tokenDataList));
        }
        return sections;
    }

    private static List<SectionData> getDataForAddress(List<MvTopAddressWithChildrenProjection> rows) {
        Map<String, Map<String, List<MvTopAddressWithChildrenProjection>>> grouped =
                rows.stream().collect(Collectors.groupingBy(
                        MvTopAddressWithChildrenProjection::getSectionLabel,
                        LinkedHashMap::new,
                        Collectors.groupingBy(MvTopAddressWithChildrenProjection::getToAddress)
                ));

        List<SectionData> sections = new ArrayList<>();

        for (var sectionEntry : grouped.entrySet()) {
            String section = sectionEntry.getKey();

            List<TokenData> tokenDataList = sectionEntry.getValue().entrySet().stream()
                    .map(addrEntry -> {
                        List<MvTopAddressWithChildrenProjection> list = addrEntry.getValue();
                        MvTopAddressWithChildrenProjection first = list.get(0);

                        // <-- 여기만 변경 -->
                        List<SecondToAddressDTO> secondNodes = list.stream()
                                .filter(r -> r.getSecondToAddress() != null)
                                .map(r -> new SecondToAddressDTO(
                                        r.getSecondToken(),
                                        r.getSecondEcosystem(),
                                        r.getSecondNodeSize(),
                                        r.getSecondValue()
                                ))
                                .limit(10)   // 10개까지만
                                .toList();
                        // <-- 변경 끝 -->

                        return new TokenData(
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
                    .sorted(Comparator.comparing(TokenData::getTotalValue).reversed())
                    .toList();

            sections.add(new SectionData(section, tokenDataList));
        }
        return sections;
    }
}
