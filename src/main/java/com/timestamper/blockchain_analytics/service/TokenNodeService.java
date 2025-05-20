package com.timestamper.blockchain_analytics.service;

import com.timestamper.blockchain_analytics.dto.tokenNode.response.TokenNodeGraphResponseDTO;
import com.timestamper.blockchain_analytics.repository.TokenNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.timestamper.blockchain_analytics.dto.tokenNode.response.TokenNodeGraphResponseDTO.*;

@Service
@RequiredArgsConstructor
public class TokenNodeService {

     private final TokenNodeRepository tokenNodeRepository;

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
}
