
package com.medicare_backend.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
public class PatientKafkaPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(PatientKafkaPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    public PatientKafkaPublisher(
            KafkaTemplate<String, String> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void publish(PatientRegisteredEvent event) {

        String patientId = event.patientId().toString();

        kafkaTemplate.send(
                "medicare.patient.registered",
                patientId,
                patientId
        ).whenComplete((result, error) -> {

            if (error != null) {
                log.error("Kafka publication failed", error);
            } else {
                log.info(
                        "Kafka event published for patient ID {}",
                        patientId
                );
            }
        });
    }
}
