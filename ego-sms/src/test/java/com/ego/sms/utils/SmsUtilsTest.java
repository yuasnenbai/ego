package com.ego.sms.utils;

import com.aliyuncs.exceptions.ClientException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SmsUtilsTest {
    @Autowired
    private SmsUtils smsUtils;
    @Test

    public void testSendSms() throws ClientException {
        smsUtils.sendSms("18224222269","123456");
    }
}
