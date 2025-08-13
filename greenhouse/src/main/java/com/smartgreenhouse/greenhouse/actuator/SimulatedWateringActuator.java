package com.smartgreenhouse.greenhouse.actuator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class SimulatedWateringActuator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulatedWateringActuator.class);

    private final Random random = new Random();

    public boolean activateWatering(Long greenhouseId, Double amount) {
        try {
            LOGGER.info("💧 WATERING STARTED | Greenhouse {} | Amount: {}L", greenhouseId, amount);
            simulatePhysicalDelay();
            logSuccess(greenhouseId, amount);
            return true;
        } catch (InterruptedException e) {
            logFailure(greenhouseId, e);
            return false;
        }
    }

    private void logSuccess(Long greenhouseId, Double amount) {
        LOGGER.info("✅ WATERING COMPLETED | Greenhouse {} | Amount: {}L", greenhouseId, amount);
    }

    private void logFailure(Long greenhouseId, InterruptedException e) {
        LOGGER.error("❌ WATERING FAILED | Greenhouse {}", greenhouseId, e);
    }

    private void simulatePhysicalDelay() throws InterruptedException {
        Thread.sleep(3000 + random.nextInt(2000));
    }
}
