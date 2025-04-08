package com.timestamper.blockchain_analytics.service;

import com.timestamper.blockchain_analytics.dto.visual.NodeDayTransactionDTO;
import com.timestamper.blockchain_analytics.dto.visual.NodeInfoDTO;
import com.timestamper.blockchain_analytics.dto.visual.response.NodeDayTransactionResponseDTO;
import com.timestamper.blockchain_analytics.dto.visual.response.NodeInfosResponseDTO;
import com.timestamper.blockchain_analytics.entity.Transaction;
import com.timestamper.blockchain_analytics.enums.TimeSlot;
import com.timestamper.blockchain_analytics.repository.VisualRepository;
import com.timestamper.blockchain_analytics.repository.projection.visual.NodeInfoProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisualService {

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

        List<NodeDayTransactionDTO> dtoList = Arrays.stream(TimeSlot.values())
                .map(slot -> mapToDto(slot, date, transactions))
                .collect(Collectors.toList());

        return new NodeDayTransactionResponseDTO(dtoList);
    }

    private NodeDayTransactionDTO mapToDto(TimeSlot slot, LocalDate date, List<Transaction> transactions) {
        LocalDateTime slotStart = slot.getStartDateTime(date);
        LocalDateTime slotEnd = slot.getEndDateTime(date);
        double sum = calculateTransactionSum(transactions, slotStart, slotEnd);

        return new NodeDayTransactionDTO(sum, slot);
    }

    private double calculateTransactionSum(List<Transaction> transactions,
                                           LocalDateTime start, LocalDateTime end) {
        return transactions.stream()
                .filter(tx -> !tx.getConvertedDate().isBefore(start) && !tx.getConvertedDate().isAfter(end))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
}
