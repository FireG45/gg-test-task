package ru.borisov.consumer_service.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

@Component
public class KafkaConsumer {

    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                    .withZone(ZoneId.systemDefault());

    @KafkaListener(id = "${kafka.config.consumer.group-id}", topics = "${kafka.topic}")
    public void listenReportMessages(ConsumerRecord<String, String> record) {
        String timestamp = formatter.format(Instant.ofEpochMilli(record.timestamp()));
        log.info(String.format("\n[%s]\n%s", timestamp, record.value()));
    }
}
