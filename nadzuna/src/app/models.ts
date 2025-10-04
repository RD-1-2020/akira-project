// Create models to match backend entities
export type NotifierType = 'TELEGRAM';
export const NotifierType = {
    TELEGRAM: 'TELEGRAM' as NotifierType
};

// Update models to match backend entities
export interface HttpMonitorConfig {
    id: number;
    name: string;
    url: string;
    checkInterval: string; // e.g., "PT60S"
    timeout: string;       // e.g., "PT5S"
    notifierConfig: NotifierConfig;
    ignoreTlsErrors: boolean;
}

export interface SyslogMonitorConfig {
    id: number;
    name: string;
    sourceUrl: string;
    noMessageInterval: string; // e.g., "PT1H"
    lastMessageAt: string;     // ISO DateTime string
    notifierConfig?: NotifierConfig;
}

export interface NotifierConfig {
    id: number;
    name: string;
    notifier_type: NotifierType;
}

export interface TelegramNotifierConfig extends NotifierConfig {
    botToken: string;
    chatId: string;
}

export interface NotificationMessage {
    id: number;
    message: string;
    status: 'CREATED' | 'DELIVERED' | 'FAILED';
    senderId: string;
    createdAt: string; // ISO DateTime string
    updatedAt: string; // ISO DateTime string
}
