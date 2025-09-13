package com.smartgreenhouse.greenhouse.service.impl;

import com.smartgreenhouse.greenhouse.entity.Greenhouse;
import com.smartgreenhouse.greenhouse.entity.User;
import com.smartgreenhouse.greenhouse.enums.WateringSource;
import com.smartgreenhouse.greenhouse.exceptions.ObjectNotFoundException;
import com.smartgreenhouse.greenhouse.repository.GreenhouseRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final EmailService emailService;
    private final GreenhouseRepository greenhouseRepository;

    public NotificationService(EmailService emailService,
                               GreenhouseRepository greenhouseRepository) {
        this.emailService = emailService;
        this.greenhouseRepository = greenhouseRepository;
    }

    public void sendWateringNotification(Long greenhouseId, WateringSource source,
                                         boolean success, Double amount, int attempts,
                                         String errorDetails) {

        Greenhouse greenhouse = greenhouseRepository.findById(greenhouseId)
                .orElseThrow(() -> new ObjectNotFoundException("Greenhouse not found"));

        User user = greenhouse.getUser();

        emailService.sendWateringNotification(
                user.getEmail(),
                user.getName(),
                greenhouse.getId(),
                greenhouse.getName(),
                source,
                success,
                amount,
                attempts,
                errorDetails
        );
    }
}