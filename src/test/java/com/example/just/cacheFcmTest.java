package com.example.just;

import com.example.just.Service.FCMService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class cacheFcmTest {
    @Autowired
    public FCMService fcmService;

    @Test
    @DisplayName("토큰 저장 테스트")
    public void cacheFcmTest() throws Exception {
        fcmService.setToken(1L, "fedGDsuzTfuGs5_lPExj3I:APA91bF5aiAvLGKc25p_EzlbGY4YDXFxRwOQaakC4Wl8wSSl2eBiGleCRuLZpbpzkgFf5drTNjFRScMQznhdcXTEgGoRyGQJaLb28jrz2CMhyDVQfS31ac3mCPo6j-bmoIrC_5vwpDJn");
    }
}
