// Create models to match backend entities
export type NotifierType = 'TELEGRAM';
export const NotifierType = {
    TELEGRAM: 'TELEGRAM' as NotifierType
};

export interface NotifierConfig {
    id?: number;
    name: string;
    notifier_type: 'TELEGRAM';
}

export interface TelegramNotifierConfig extends NotifierConfig {
    botToken: string;
    chatId: string;
    notifier_type: 'TELEGRAM';
}

export interface NotificationMessage {
    id: number;
    message: string;
    status: 'CREATED' | 'DELIVERED' | 'FAILED';
    senderId: string;
    createdAt: string; // ISO DateTime string
    updatedAt: string; // ISO DateTime string
}

export type ClientStatus = 'OK' | 'FAILED';

export interface Client {
    hostname: string;
    status: ClientStatus;
    lastMessageAt?: string;
}

export interface ClientMessage {
    id: number;
    message: string;
    clientTimestamp: string;
    receivedAt: string;
}

export interface Page<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
}
