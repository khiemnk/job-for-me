package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Slf4j
public class Job {

    private final ObjectMapper objectMapper;

    private final String getList = "https://market.okvip.business/api/order/orders/market?pageSize=10&pageIndex=1&search=";

    private final String PUT_BASE_URL = "https://market.okvip.business/api/order/orders/receive/";

    private Integer countSuccess = 0;
    public Job(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Scheduled(cron = "0/2 * * * * ?")
    public void cronJob() throws Exception {
        if (countSuccess > 5) {
            return;
        }
        HttpEntity<String> requestEntity = new HttpEntity<>("", getHeader());
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> jsonResponse = restTemplate.exchange(getList, HttpMethod.GET, requestEntity,  String.class);
        log.info("Get list {}", jsonResponse.getBody());
        LocalDateTime minExpireDate = LocalDateTime.of(2024, 11, 19, 15, 0, 0);
        GetListResponse getListResponse = objectMapper.readValue(jsonResponse.getBody(), GetListResponse.class);
        if (Objects.nonNull(getListResponse.getData())) {
            log.info("Get list success: {}", getListResponse.getData().size());
            getListResponse.getData().forEach(order -> {
                log.info("Order with exireDate and word: {} {}", order.getRequire().getExpiresDate(), order.getRequire().getWords());
               if (order.getRequire().getExpiresDate().isAfter(minExpireDate) && order.getRequire().getWords() == 1000) {
                   log.info("Order accept with exireDate and word: {} {}", order.getRequire().getExpiresDate(), order.getRequire().getWords());
                   ResponseEntity<String> response = restTemplate.exchange(PUT_BASE_URL + order.get_id(), HttpMethod.PUT, requestEntity, String.class);
                   PutResponse putResponse = null;
                   try {
                       putResponse = objectMapper.readValue(response.getBody(), PutResponse.class);
                   } catch (JsonProcessingException e) {
                       throw new RuntimeException(e);
                   }
                   if (Objects.nonNull(putResponse) && putResponse.isSuccess() && putResponse.getMessage() == "Nhận bài viết thành công!") {
                       countSuccess ++;
                       log.info("success count: {}", countSuccess);
                   }
               }
            });
        }
    }

    private HttpHeaders getHeader () {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("accept", "application/json, text/plain, */*");
        headers.add("accept-language", "vi-VN,vi;q=0.9,en-US;q=0.8,en;q=0.7");
        headers.add("authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2NmZiYWI1ZjU3N2RkZWQxYTVjODkyNTIiLCJpZCI6IjY2ZmJhYjVmNTc3ZGRlZDFhNWM4OTI1MiIsImlhdCI6MTczMTg1MjIyOCwiZXhwIjoxNzMxODk1NDI4fQ.XP8Ot65Mc_2g_D_OwTRqgs4upZJYW2_hwrPLAHoAK_0");
        headers.add("origin", "https://market.okvip.business");
        headers.add("referer", "https://market.okvip.business/market");
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36");
        headers.add("content-length", "0");
        return headers;
    }
}
