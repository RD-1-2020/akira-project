package org.azurecloud.solutions.akira.service.notifier;

import org.azurecloud.solutions.akira.model.entity.NotificationMessage;
import org.azurecloud.solutions.akira.model.entity.NotifierConfig;

public interface AkiraNotifier {

    void send(String message);
}
