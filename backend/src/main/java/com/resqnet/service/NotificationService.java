package com.resqnet.service;

import com.resqnet.entity.Incident;
import com.resqnet.entity.Notification;
import com.resqnet.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public Notification send(String title, String message, String severity, Incident incident) {
        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .severity(severity)
                .incident(incident)
                .readStatus(false)
                .build();
        notification = notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/topic/notifications", notification);
        return notification;
    }

    public java.util.List<Notification> recent() {
        return notificationRepository.findTop50ByOrderByCreatedAtDesc();
    }
}
