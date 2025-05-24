package com.timestamper.blockchain_analytics.dto.tokenNode.response;

import com.timestamper.blockchain_analytics.dto.tokenNode.TransactionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TokenNodeTransactionResponseDTO {

    private String token;
    private int page;
    private int size;
    private List<TransactionDTO> transactions;
}
