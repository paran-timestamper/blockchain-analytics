package com.timestamper.blockchain_analytics.dto;

import com.timestamper.blockchain_analytics.utils.converter.TimestampConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TransactionFilteredLatestDTO {

    public TransactionFilteredLatestDTO(String txhash, String fromAddress, String toAddress, Double amount, String dpdenom, Long timestamp) {
        this.txhash = txhash;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.amount = amount;
        this.dpdenom = dpdenom;
        this.timestamp = timestamp;
        this.formattedTimestamp = TimestampConverter.convertTimestampToLocalDateTime(timestamp);
    }

    private String txhash;      // 트랜잭션 해시
    private String fromAddress; // 송신자 주소
    private String toAddress;   // 수신자 주소
    private Double amount;      // 거래 금액
    private String dpdenom;     // 토큰 명칭 (예: ATOM, INJ 등)
    private Long timestamp;     // 거래 발생 시간

    private LocalDateTime formattedTimestamp; // 거래 발생 시간 (포맷팅된 값)
}
