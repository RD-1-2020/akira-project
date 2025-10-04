// Create models to match backend entities
export type NotifierType = 'TELEGRAM';
export const NotifierType = {
    TELEGRAM: 'TELEGRAM' as NotifierType
};

// Update models to match backend entities
export interface NotifierConfig {
    id?: number;
    name: string;
    notifier_type: 'TELEGRAM';
}

export interface TelegramNotifierConfig extends NotifierConfig {
    botToken: string;
    chatId: string;
}

export type MonitorType = 'HTTP' | 'SYSLOG';

export type MonitorConfig = HttpMonitorConfig | SyslogMonitorConfig;

export interface HttpMonitorConfig {
    id?: number;
    name: string;
    monitor_type: 'HTTP';
    url: string;
    expectedStatus: number;
}

export interface SyslogMonitorConfig {
    id?: number;
    name: string;
    monitor_type: 'SYSLOG';
    sourceUrl: string;
}

export interface NotificationMessage {
    id: number;
    message: string;
    status: 'CREATED' | 'DELIVERED' | 'FAILED';
    senderId: string;
    createdAt: string; // ISO DateTime string
    updatedAt: string; // ISO DateTime string
}
