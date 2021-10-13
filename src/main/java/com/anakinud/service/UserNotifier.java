package com.anakinud.service;

import com.anakinud.entity.Impostor;
import com.anakinud.entity.Player;
import com.anakinud.entity.User;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UserNotifier {

    private final NotificationService notificationService;

    public UserNotifier(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void sendNotification(Set<User> users) {
        users.forEach(u -> {
            if (u instanceof Player) {
                notificationService.sendNotification((Player)u);
            } else if (u instanceof Impostor) {
                notificationService.sendNotification((Impostor) u);
            }
        });
    }

    public void sendEmergencyNotification(Set<User> users) {
        users.forEach(notificationService::sendEmergencyNotification);
    }
}
