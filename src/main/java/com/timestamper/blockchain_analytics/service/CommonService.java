package com.timestamper.blockchain_analytics.service;

import com.timestamper.blockchain_analytics.repository.CommonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommonService {

    private final CommonRepository commonRepository;

    public List<String> getAllTokens() {
        return commonRepository.findDistinctDpdenom();
    }

}
