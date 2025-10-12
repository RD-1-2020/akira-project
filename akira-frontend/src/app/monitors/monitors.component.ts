import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { MaterialModule } from '../material.module';
import { MonitorConfig, HttpMonitorConfig, SyslogMonitorConfig, NotifierConfig } from '../models';
import { MonitorConfigService } from '../monitor-config.service';
import { NotifierConfigService } from '../notifier-config.service';
import { MonitorDialogComponent } from './monitor-dialog.component';

@Component({
  selector: 'app-monitors',
  templateUrl: './monitors.component.html',
  styleUrls: ['./monitors.component.scss'],
  standalone: true,
  imports: [CommonModule, MaterialModule],
})
export class MonitorsComponent implements OnInit {
  displayedColumns: string[] = ['name', 'type', 'status', 'notifier', 'details', 'actions'];
  dataSource: MonitorConfig[] = [];
  notifiers: NotifierConfig[] = [];

  constructor(
    private dialog: MatDialog,
    private monitorService: MonitorConfigService,
    private notifierService: NotifierConfigService
  ) {}

  ngOnInit(): void {
    this.loadMonitors();
    this.loadNotifiers();
  }

  loadMonitors(): void {
    this.monitorService.getAll().subscribe(data => {
      this.dataSource = data;
    });
  }

  loadNotifiers(): void {
    this.notifierService.getAll().subscribe(data => {
      this.notifiers = data;
    });
  }

  getMonitorDetails(monitor: MonitorConfig): string {
    if (monitor.monitor_type === 'HTTP') {
      const httpConfig = monitor as HttpMonitorConfig;
      return `URL: ${httpConfig.url}, Status: ${httpConfig.expectedStatus}`;
    }
    if (monitor.monitor_type === 'SYSLOG') {
      const syslogConfig = monitor as SyslogMonitorConfig;
      return `Source URL: ${syslogConfig.sourceIp}`;
    }
    return '';
  }

  getNotifierName(monitor: MonitorConfig): string {
    return monitor.notifierConfig?.name || 'Not assigned';
  }

  getStatusText(monitor: MonitorConfig): string {
    return monitor.status === 'ACTIVE' ? 'Active' : 'Failed';
  }

  getStatusIcon(monitor: MonitorConfig): string {
    return monitor.status === 'ACTIVE' ? 'check_circle' : 'error';
  }

  getStatusColor(monitor: MonitorConfig): string {
    return monitor.status === 'ACTIVE' ? 'primary' : 'warn';
  }

  openDialog(data?: MonitorConfig): void {
    const dialogRef = this.dialog.open(MonitorDialogComponent, {
      width: '400px',
      data: data ? { ...data } : {},
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadMonitors();
      }
    });
  }

  delete(id: number): void {
    if (confirm('Are you sure you want to delete this monitor?')) {
      this.monitorService.delete(id).subscribe(() => {
        this.loadMonitors();
      });
    }
  }
}
