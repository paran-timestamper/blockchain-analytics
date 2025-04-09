package com.timestamper.blockchain_analytics.service;

import com.timestamper.blockchain_analytics.dto.visual.NodeDayTransactionDTO;
import com.timestamper.blockchain_analytics.dto.visual.NodeInfoByLatestDTO;
import com.timestamper.blockchain_analytics.dto.visual.NodeInfoDTO;
import com.timestamper.blockchain_analytics.dto.visual.NodeMonthTransactionDTO;
import com.timestamper.blockchain_analytics.dto.visual.response.NodeDayTransactionResponseDTO;
import com.timestamper.blockchain_analytics.dto.visual.response.NodeInfosByLatestResponseDTO;
import com.timestamper.blockchain_analytics.dto.visual.response.NodeInfosResponseDTO;
import com.timestamper.blockchain_analytics.dto.visual.response.NodeMonthTransactionResponseDTO;
import com.timestamper.blockchain_analytics.entity.Transaction;
import com.timestamper.blockchain_analytics.enums.DaySlot;
import com.timestamper.blockchain_analytics.enums.MonthSlot;
import com.timestamper.blockchain_analytics.repository.VisualRepository;
import com.timestamper.blockchain_analytics.repository.projection.visual.NodeInfoProjection;
import com.timestamper.blockchain_analytics.service.crypto.CryptoPriceService;
import com.timestamper.blockchain_analytics.utils.api.EtherscanAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class VisualService {

    private final EtherscanAPI etherscanAPI;
    private final CryptoPriceService cryptoPriceService;
    private final VisualRepository visualRepository;

    public NodeInfosResponseDTO getNodeInfos(String fromAddress) {
        List<NodeInfoProjection> nodeInfos = visualRepository.getNodeInfos(fromAddress);

        // TODO totalValue 어떻게 계산하는지 물어보고 구현해야함 ㄱㄱ
        double totalValue = 0.0;

        List<NodeInfoDTO> list = nodeInfos.stream()
                .map(nodeInfo -> new NodeInfoDTO(
                        nodeInfo.getTxHash(),
                        nodeInfo.getType(),
                        nodeInfo.getToAddress(),
                        totalValue
                ))
                .toList();

        return new NodeInfosResponseDTO(list);
    }

    public NodeDayTransactionResponseDTO getNodeDayTransaction(String fromAddress, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Transaction> transactions = visualRepository
                .findByFromAddressAndConvertedDateBetween(fromAddress, startOfDay, endOfDay);

        List<NodeDayTransactionDTO> dtoList = Arrays.stream(DaySlot.values())
                .map(slot -> mapToDto(slot, date, transactions))
                .collect(Collectors.toList());

        return new NodeDayTransactionResponseDTO(dtoList);
    }

    private NodeDayTransactionDTO mapToDto(DaySlot slot, LocalDate date, List<Transaction> transactions) {
        LocalDateTime slotStart = slot.getStartDateTime(date);
        LocalDateTime slotEnd = slot.getEndDateTime(date);
        double sum = calculateDayTransactionSum(transactions, slotStart, slotEnd);

        return new NodeDayTransactionDTO(sum, slot);
    }

    private double calculateDayTransactionSum(List<Transaction> transactions, LocalDateTime start, LocalDateTime end) {
        return transactions.stream()
                .filter(tx -> !tx.getConvertedDate().isBefore(start) && !tx.getConvertedDate().isAfter(end))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public NodeMonthTransactionResponseDTO getNodeMonthTransaction(String fromAddress, YearMonth date) {
        List<Transaction> transactions = fetchTransactionsForMonth(fromAddress, date);

        List<NodeMonthTransactionDTO> result =
                Stream.of(MonthSlot.values())
                        .map(slot -> mapToDto(slot, date, transactions))
                        .collect(Collectors.toList());

        return new NodeMonthTransactionResponseDTO(result);
    }

    private List<Transaction> fetchTransactionsForMonth(String fromAddress, YearMonth date) {
        LocalDateTime start = date.atDay(1).atStartOfDay();
        LocalDateTime end = date.atEndOfMonth().atTime(LocalTime.MAX);
        return visualRepository.findByFromAddressAndConvertedDateBetween(fromAddress, start, end);
    }

    private NodeMonthTransactionDTO mapToDto(MonthSlot slot, YearMonth date, List<Transaction> transactions) {
        LocalDateTime start = slot.getStartDateTime(date);
        LocalDateTime end = slot.getEndDateTime(date);

        double sum = calculateMonthTransactionSum(transactions, start, end);
        return new NodeMonthTransactionDTO(sum, slot);
    }

    private double calculateMonthTransactionSum(List<Transaction> transactions, LocalDateTime start, LocalDateTime end) {
        return transactions.stream()
                .filter(tx -> !tx.getConvertedDate().isBefore(start) &&
                        !tx.getConvertedDate().isAfter(end))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public NodeInfosByLatestResponseDTO getNodeInfosByLatest(String token, String fromAddress) {
        double price = cryptoPriceService.getCryptoPrice(token, "USD").block();

        List<NodeInfoByLatestDTO> result = visualRepository.getNodeInfosByLatest(token, fromAddress).stream()
                .map(p -> new NodeInfoByLatestDTO(p.getToAddress(), etherscanAPI.isExchangeAddress(p.getToAddress()),
                                                                  p.getTotalValue() * price, p.getConvertedDate()))
                .toList();

        return new NodeInfosByLatestResponseDTO(result);
    }
}
