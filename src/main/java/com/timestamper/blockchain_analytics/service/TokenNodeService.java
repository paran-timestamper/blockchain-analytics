package com.timestamper.blockchain_analytics.service;

import com.timestamper.blockchain_analytics.dto.tokenNode.TransactionDTO;
import com.timestamper.blockchain_analytics.dto.tokenNode.request.TokenNodeMonthlyVolumeRequestDTO;
import com.timestamper.blockchain_analytics.dto.tokenNode.request.TokenNodeTotalVolumeRequestDTO;
import com.timestamper.blockchain_analytics.dto.tokenNode.request.TokenNodeTransactionRequestDTO;
import com.timestamper.blockchain_analytics.dto.tokenNode.response.TokenNodeGraphResponseDTO;
import com.timestamper.blockchain_analytics.dto.tokenNode.response.TokenNodeMonthlyVolumeResponseDTO;
import com.timestamper.blockchain_analytics.dto.tokenNode.response.TokenNodeMonthlyVolumeResponseDTO.MonthlyTimeBlock;
import com.timestamper.blockchain_analytics.dto.tokenNode.response.TokenNodeTotalVolumeResponseDTO;
import com.timestamper.blockchain_analytics.dto.tokenNode.response.TokenNodeTotalVolumeResponseDTO.TimeBlock;
import com.timestamper.blockchain_analytics.dto.tokenNode.response.TokenNodeTransactionResponseDTO;
import com.timestamper.blockchain_analytics.repository.TokenNodeRepository;
import com.timestamper.blockchain_analytics.repository.TokenNodeTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

     public TokenNodeMonthlyVolumeResponseDTO getMonthlyVolume(TokenNodeMonthlyVolumeRequestDTO request) {
          List<String> tokens = request.getTokens();
          YearMonth yearMonth = request.getDate();

          LocalDate start = yearMonth.atDay(1);
          LocalDate end = yearMonth.atEndOfMonth();

          List<Object[]> rows = tokenNodeTransactionRepository.findMonthlyTransactions(tokens, start, end);

          // Map<range, Map<token, amount>>
          Map<String, Map<String, Double>> rangeMap = new LinkedHashMap<>();
          List<String> rangeOrder = getDateRanges(end.getDayOfMonth());

          for (Object[] row : rows) {
               String token = (String) row[0];
               LocalDate date = ((java.sql.Date) row[1]).toLocalDate();
               Double amount = ((Number) row[2]).doubleValue();

               String rangeKey = getRangeKey(date.getDayOfMonth(), end.getDayOfMonth());

               rangeMap
                       .computeIfAbsent(rangeKey, k -> new HashMap<>())
                       .merge(token, amount, Double::sum);
          }

          List<MonthlyTimeBlock> timeBlocks = new ArrayList<>();
          for (String range : rangeOrder) {
               Map<String, Double> values = rangeMap.getOrDefault(range, new HashMap<>());
               timeBlocks.add(new MonthlyTimeBlock(range, values));
          }

          return new TokenNodeMonthlyVolumeResponseDTO(tokens, timeBlocks);
     }

     private String getRangeKey(int day, int lastDay) {
          if (day <= 5) return "1일~5일";
          else if (day <= 10) return "6일~10일";
          else if (day <= 15) return "11일~15일";
          else if (day <= 20) return "16일~20일";
          else if (day <= 25) return "21일~25일";
          else return String.format("26일~%d일", lastDay);
     }

     private List<String> getDateRanges(int lastDay) {
          return List.of(
                  "1일~5일",
                  "6일~10일",
                  "11일~15일",
                  "16일~20일",
                  "21일~25일",
                  String.format("26일~%d일", lastDay)
          );
     }

     public TokenNodeTotalVolumeResponseDTO getTotalTransactionVolume(TokenNodeTotalVolumeRequestDTO request) {
          List<String> tokens = request.getTokens();
          List<Object[]> rawData = tokenNodeTransactionRepository.findTotalVolumeByTokens(tokens);
          Map<String, Map<String, Double>> quarterMap = new LinkedHashMap<>();

          for (Object[] row : rawData) {
               String token = (String) row[0];
               String yearMonthStr = (String) row[1];
               Double amount = ((Number) row[2]).doubleValue();
               YearMonth ym = YearMonth.parse(yearMonthStr, DateTimeFormatter.ofPattern("yyyy.MM"));
               String quarterKey = getQuarterRange(ym);

               quarterMap
                       .computeIfAbsent(quarterKey, k -> new HashMap<>())
                       .merge(token, amount, Double::sum);
          }

          List<TimeBlock> timeBlocks = new ArrayList<>();
          for (Map.Entry<String, Map<String, Double>> entry : quarterMap.entrySet()) {
               timeBlocks.add(new TimeBlock(entry.getKey(), entry.getValue()));
          }

          return new TokenNodeTotalVolumeResponseDTO(tokens, timeBlocks);
     }

     private String getQuarterRange(YearMonth ym) {
          int startMonth = ((ym.getMonthValue() - 1) / 3) * 3 + 1;
          YearMonth start = YearMonth.of(ym.getYear(), startMonth);
          YearMonth end = start.plusMonths(2);

          return start.format(DateTimeFormatter.ofPattern("yyyy.MM")) + "~" +
                 end.format(DateTimeFormatter.ofPattern("yyyy.MM"));
     }
}
