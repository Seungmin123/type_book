package com.muzlive.kitpage.kitpage.service.transfer.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WebhookTransfer {

    @Value("${spring.webhook.slack}")
    private String slackDomain;

    public void sendSlackMessage(String message) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String webhookText = "------------------------------------------------------------------------------------" + "\n"
                    + message;

            HttpPost httpPost = new HttpPost(slackDomain);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("text", webhookText);

            ObjectMapper objectMapper = new ObjectMapper();
            StringEntity entity = new StringEntity(objectMapper.writeValueAsString(params));

            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            client.execute(httpPost);
        } catch (Exception e) {
            log.error(e.getStackTrace().toString());
        }
    }
}
