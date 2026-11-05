
package com.medicare_backend.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PatientKafkaConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(PatientKafkaConsumer.class);

    @KafkaListener(
            topics = "medicare.patient.registered",
            groupId = "medicare-demo"
    )
    public void receive(String patientId) {

        log.info(
                "Kafka consumer received: Patient {} registered",
                patientId
        );
    }
}
