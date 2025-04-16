package com.timestamper.blockchain_analytics.service;

import com.timestamper.blockchain_analytics.dto.visual.*;
import com.timestamper.blockchain_analytics.dto.visual.reqeust.NodeAddressRequestDTO;
import com.timestamper.blockchain_analytics.dto.visual.response.*;
import com.timestamper.blockchain_analytics.entity.Transaction;
import com.timestamper.blockchain_analytics.enums.DaySlot;
import com.timestamper.blockchain_analytics.enums.MonthSlot;
import com.timestamper.blockchain_analytics.repository.VisualRepository;
import com.timestamper.blockchain_analytics.repository.projection.visual.NodeInfoEmptyTokenProjection;
import com.timestamper.blockchain_analytics.repository.projection.visual.NodeInfoProjection;
import com.timestamper.blockchain_analytics.service.crypto.CryptoPriceService;
import com.timestamper.blockchain_analytics.utils.api.EtherscanAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class VisualService {

    private final EtherscanAPI etherscanAPI;
    private final CryptoPriceService cryptoPriceService;
    private final VisualRepository visualRepository;

    public NodeInfosResponseDTO getNodeInfos(NodeAddressRequestDTO request) {
        if (request.getToken() == null) {
            List<NodeInfoEmptyTokenProjection> nodeInfos = visualRepository.getNodeInfosEmptyToken(request.getFromAddress());
            List<NodeInfoDTO> nodeInfoDTOs = nodeInfos.stream()
                    .map(node -> {
                        Double currentPrice = cryptoPriceService.getCryptoPrice(node.getDpdenom(), "USD").block();
                        return new NodeInfoDTO(
                                node.getTxHash(),
                                node.getType(),
                                node.getToAddress(),
                                node.getAmount() * currentPrice
                        );
                    })
                    .toList();

            return new NodeInfosResponseDTO(nodeInfoDTOs);
        } else {
            List<NodeInfoProjection> nodeInfos = visualRepository.getNodeInfos(request.getFromAddress(), request.getToken());
            Double currentPrice = cryptoPriceService.getCryptoPrice(request.getToken(), "USD").block();

            List<NodeInfoDTO> list = nodeInfos.stream()
                    .map(nodeInfo -> new NodeInfoDTO(
                            nodeInfo.getTxHash(),
                            nodeInfo.getType(),
                            nodeInfo.getToAddress(),
                            nodeInfo.getAmount() * currentPrice
                    ))
                    .toList();

            return new NodeInfosResponseDTO(list);
        }
    }

    // 토큰의 정보가 있으면 해당 토큰 가격을 들고와서 totalValue를 계산하고
    // 토큰의 정보가 없으면 각 트랜잭션에서 각각의 가격을 들고와서 계산한다.

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

    // 토큰의 정보가 있으면 해당 토큰 가격을 들고와서 totalValue를 계산하고
    // 토큰의 정보가 없으면 각 트랜잭션에서 amount 값을 넘겨주고 mouseover를 통해서 가격을 계산한다.
    public NodeDpdenomFilteredInfosResponseDTO getNodeInfosByLatest(String token, String fromAddress) {
        double price = Optional.ofNullable(token)
                .map(t -> cryptoPriceService.getCryptoPrice(t, "USD").block())
                .orElse(0.0);

        List<NodeDpdenomFilteredInfosDTO> result = visualRepository.getNodeInfosByLatest(token, fromAddress).stream()
                .map(p -> {
                    double value = (price == 0.0) ? p.getTotalValue() : p.getTotalValue() * price;

                    return new NodeDpdenomFilteredInfosDTO(
                            p.getToAddress(),
                            etherscanAPI.isExchangeAddress(p.getToAddress()),
                            value,
                            p.getTransactionCount(),
                            p.getConvertedDate(),
                            p.getDpdenom()
                    );
                })
                .toList();

        return new NodeDpdenomFilteredInfosResponseDTO(result);
    }

    public NodeFilteredInfosResponseDTO getNodeInfosByTransactionPrice(String token, String fromAddress) {
        Double price = Optional.ofNullable(token)
                .map(t -> cryptoPriceService.getCryptoPrice(t, "USD").block())
                .orElse(0.0);

        List<NodeFilteredInfosDTO> result = visualRepository.getNodeInfosByTransactionPrice(token, fromAddress).stream()
                .map(p -> new NodeFilteredInfosDTO(p.getToAddress(), etherscanAPI.isExchangeAddress(p.getToAddress()),
                                                                  p.getTotalValue() * price, p.getTransactionCount(), p.getConvertedDate()))
                .toList();

        return new NodeFilteredInfosResponseDTO(result);
    }

    public NodeFilteredInfosResponseDTO getNodeInfosByTransactionAmount(String token, String fromAddress) {
        Double price = Optional.ofNullable(token)
                .map(t -> cryptoPriceService.getCryptoPrice(t, "USD").block())
                .orElse(0.0);

        List<NodeFilteredInfosDTO> result = visualRepository.getNodeInfosByTransactionAmount(token, fromAddress).stream()
                .map(p -> new NodeFilteredInfosDTO(p.getToAddress(), etherscanAPI.isExchangeAddress(p.getToAddress()),
                        p.getTotalValue() * price, p.getTransactionCount(), p.getConvertedDate()))
                .toList();

        return new NodeFilteredInfosResponseDTO(result);
    }
}
