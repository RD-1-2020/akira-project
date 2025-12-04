import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MaterialModule } from '../material.module';
import { SyslogMessage } from '../models';
import { SyslogMessageService } from '../syslog-message.service';

@Component({
  selector: 'app-syslog-messages-dialog',
  templateUrl: './syslog-messages-dialog.component.html',
  standalone: true,
  imports: [CommonModule, FormsModule, MaterialModule],
})
export class SyslogMessagesDialogComponent implements OnInit {
  displayedColumns: string[] = ['receivedAt', 'message'];
  dataSource: SyslogMessage[] = [];
  loading = false;
  sourceId: number;
  startDate: Date | null = null;
  startTime: string = '00:00';
  endDate: Date | null = null;
  endTime: string = '23:59';

  constructor(
    public dialogRef: MatDialogRef<SyslogMessagesDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { sourceId: number },
    private syslogMessageService: SyslogMessageService
  ) {
    this.sourceId = data.sourceId;
  }

  ngOnInit(): void {
    this.loadMessages();
  }

  loadMessages(): void {
    this.loading = true;
    let startDateMillis: number | undefined = undefined;
    let endDateMillis: number | undefined = undefined;
    
    if (this.startDate) {
      const startDate = new Date(this.startDate);
      const [hours, minutes] = this.startTime.split(':').map(Number);
      startDate.setHours(hours, minutes, 0, 0);
      startDateMillis = startDate.getTime();
    }
    if (this.endDate) {
      const endDate = new Date(this.endDate);
      const [hours, minutes] = this.endTime.split(':').map(Number);
      endDate.setHours(hours, minutes, 59, 999);
      endDateMillis = endDate.getTime();
    }
    
    this.syslogMessageService.getMessagesBySourceId(
      this.sourceId,
      startDateMillis,
      endDateMillis
    ).subscribe({
      next: (messages) => {
        this.dataSource = messages;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  applyFilter(): void {
    this.loadMessages();
  }

  clearFilter(): void {
    this.startDate = null;
    this.startTime = '00:00';
    this.endDate = null;
    this.endTime = '23:59';
    this.loadMessages();
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleString('ru-RU');
  }
}


