package com.timestamper.blockchain_analytics.repository;

import com.timestamper.blockchain_analytics.dto.tokenNode.response.TokenNodeGraphResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.timestamper.blockchain_analytics.dto.tokenNode.response.TokenNodeGraphResponseDTO.*;

@Repository
@RequiredArgsConstructor
public class TokenNodeRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<TokenData> findAllFromTable(String tableName) {
        String sql = "SELECT token, ecosystem, color_darkening, timestamp, generation_date, " +
                "nodesize, total_value, transcationamount FROM " + tableName;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new TokenData(
                        rs.getString("token"),
                        rs.getString("ecosystem"),
                        rs.getInt("color_darkening"),
                        rs.getString("timestamp"),
                        rs.getString("generation_date"),
                        rs.getInt("nodesize"),
                        rs.getString("total_value"),
                        rs.getString("transcationamount")
                )
        );
    }
}
