import {Component, OnInit} from '@angular/core';
import {NotificationMessage} from '../models';
import {NotificationMessageService} from '../notification-message.service';
import {CommonModule} from '@angular/common';
import {MaterialModule} from '../material.module';

@Component({
    selector: 'app-notifications',
    templateUrl: './notifications.component.html',
    styleUrls: ['./notifications.component.scss'],
    standalone: true,
    imports: [CommonModule, MaterialModule],
})
export class NotificationsComponent implements OnInit {

    displayedColumns: string[] = ['status', 'message', 'timestamp', 'actions'];
    dataSource: NotificationMessage[] = [];

    constructor(private notificationService: NotificationMessageService) {
    }

    ngOnInit(): void {
        this.loadMessages();
    }

    loadMessages(): void {
        this.notificationService.getAll().subscribe(data => {
            this.dataSource = data.sort((a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime());
        });
    }

    retry(id: number): void {
        this.notificationService.retry(id).subscribe(() => {
            this.loadMessages();
        });
    }

    delete(id: number): void {
        if (confirm('Are you sure you want to delete this notification?')) {
            this.notificationService.delete(id).subscribe(() => {
                this.loadMessages();
            });
        }
    }

    getStatusIcon(status: 'CREATED' | 'DELIVERED' | 'FAILED'): string {
        switch (status) {
            case 'DELIVERED':
                return 'check_circle';
            case 'FAILED':
                return 'error';
            case 'CREATED':
                return 'schedule';
        }
    }
}
