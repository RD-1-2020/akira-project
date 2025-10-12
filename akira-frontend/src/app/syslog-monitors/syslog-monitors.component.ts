import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {SyslogMonitorConfig} from '../models';
import {SyslogMonitorConfigService} from '../syslog-monitor-config.service';
import {SyslogMonitorDialogComponent} from './syslog-monitor-dialog.component';
import {CommonModule} from '@angular/common';
import {MaterialModule} from '../material.module';

@Component({
    selector: 'app-syslog-monitors',
    templateUrl: './syslog-monitors.component.html',
    standalone: true,
    imports: [CommonModule, MaterialModule],
})
export class SyslogMonitorsComponent implements OnInit {

    displayedColumns: string[] = ['name', 'sourceUrl', 'lastMessageAt', 'actions'];
    dataSource: SyslogMonitorConfig[] = [];

    constructor(
        private dialog: MatDialog,
        private syslogMonitorService: SyslogMonitorConfigService
    ) {
    }

    ngOnInit(): void {
        this.loadMonitors();
    }

    loadMonitors(): void {
        this.syslogMonitorService.getAll().subscribe(data => {
            this.dataSource = data;
        });
    }

    openDialog(data?: SyslogMonitorConfig): void {
        const dialogRef = this.dialog.open(SyslogMonitorDialogComponent, {
            width: '400px',
            data: data ? {...data} : {}
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.loadMonitors();
            }
        });
    }

    delete(id: number): void {
        if (confirm('Are you sure you want to delete this monitor?')) {
            this.syslogMonitorService.delete(id).subscribe(() => {
                this.loadMonitors();
            });
        }
    }
}
