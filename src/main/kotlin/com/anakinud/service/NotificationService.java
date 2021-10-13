package com.anakinud.service;

import com.anakinud.entity.Impostor;
import com.anakinud.entity.Player;
import com.anakinud.entity.User;

public interface NotificationService {

    void sendNotification(Player player);

    void sendNotification(Impostor impostor);

    void sendEmergencyNotification(User user);
}
