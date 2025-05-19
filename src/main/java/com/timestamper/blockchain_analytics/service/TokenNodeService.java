package com.timestamper.blockchain_analytics.service;

import com.timestamper.blockchain_analytics.dto.tokenNode.TransactionDTO;
import com.timestamper.blockchain_analytics.dto.tokenNode.request.TokenNodeTransactionRequestDTO;
import com.timestamper.blockchain_analytics.dto.tokenNode.response.TokenNodeGraphResponseDTO;
import com.timestamper.blockchain_analytics.dto.tokenNode.response.TokenNodeTransactionResponseDTO;
import com.timestamper.blockchain_analytics.repository.TokenNodeRepository;
import com.timestamper.blockchain_analytics.repository.TokenNodeTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.timestamper.blockchain_analytics.dto.tokenNode.response.TokenNodeGraphResponseDTO.*;

@Service
@RequiredArgsConstructor
public class TokenNodeService {

     private final TokenNodeRepository tokenNodeRepository;
     private final TokenNodeTransactionRepository tokenNodeTransactionRepository;

     public TokenNodeGraphResponseDTO getTokenNodeGraphData() {
          List<String> sectionDates = List.of(
                  "2021_02", "2021_05", "2021_08", "2021_11",
                  "2022_02", "2022_05", "2022_08"
          );

          List<SectionData> sections = new ArrayList<>();

          for (String section : sectionDates) {
               String tableName = "token_node_" + section;

               List<TokenData> tokens = tokenNodeRepository.findAllFromTable(tableName);

               sections.add(new SectionData(
                       section.replace("_", "-"),
                       tokens
               ));
          }

          return new TokenNodeGraphResponseDTO(sections);
     }

     public TokenNodeTransactionResponseDTO getTransaction(TokenNodeTransactionRequestDTO requestDTO) {
          int page = requestDTO.getPage();
          int size = requestDTO.getSize();

          String cursor = null;
          List<Object[]> batch = null;

          for (int i = 0; i <= page; i++) {
               Pageable pageable = PageRequest.of(0, size);
               batch = tokenNodeTransactionRepository.findTopNTransactionsByToken(requestDTO.getToken(), cursor, pageable);

               if (batch == null || batch.isEmpty()) break;

               Object[] lastRow = batch.get(batch.size() - 1);
               cursor = lastRow[4].toString(); // timestamp
          }

          List<TransactionDTO> dtoList = (batch == null) ? List.of() : batch.stream()
                  .map(row -> new TransactionDTO(
                          (String) row[0],
                          (String) row[1],
                          (String) row[2],
                          row[3] != null ? ((Number) row[3]).doubleValue() : null
                  ))
                  .toList();

          return new TokenNodeTransactionResponseDTO(
                  requestDTO.getToken(),
                  requestDTO.getPage(),
                  requestDTO.getSize(),
                  dtoList
          );
     }
}
