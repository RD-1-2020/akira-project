import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {HttpMonitorConfig} from '../models';
import {HttpMonitorConfigService} from '../http-monitor-config.service';
import {HttpMonitorDialogComponent} from './http-monitor-dialog.component';
import {CommonModule} from '@angular/common';
import {MaterialModule} from '../material.module';

@Component({
    selector: 'app-http-monitors',
    templateUrl: './http-monitors.component.html',
    standalone: true,
    imports: [CommonModule, MaterialModule],
})
export class HttpMonitorsComponent implements OnInit {

    displayedColumns: string[] = ['name', 'url', 'interval', 'actions'];
    dataSource: HttpMonitorConfig[] = [];

    constructor(
        private dialog: MatDialog,
        private httpMonitorService: HttpMonitorConfigService
    ) {
    }

    ngOnInit(): void {
        this.loadMonitors();
    }

    loadMonitors(): void {
        this.httpMonitorService.getAll().subscribe(data => {
            this.dataSource = data;
        });
    }

    openDialog(data?: HttpMonitorConfig): void {
        const dialogRef = this.dialog.open(HttpMonitorDialogComponent, {
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
            this.httpMonitorService.delete(id).subscribe(() => {
                this.loadMonitors();
            });
        }
    }
}
