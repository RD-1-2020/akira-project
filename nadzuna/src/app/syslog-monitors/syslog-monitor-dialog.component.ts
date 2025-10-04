import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {NotifierConfig, SyslogMonitorConfig} from '../models';
import {SyslogMonitorConfigService} from '../syslog-monitor-config.service';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {MaterialModule} from '../material.module';
import {NotifierConfigService} from '../notifier-config.service';

@Component({
    selector: 'app-syslog-monitor-dialog',
    templateUrl: './syslog-monitor-dialog.component.html',
    standalone: true,
    imports: [CommonModule, FormsModule, MaterialModule],
})
export class SyslogMonitorDialogComponent implements OnInit {
    isEdit: boolean;
    notifiers: NotifierConfig[] = [];

    constructor(
        public dialogRef: MatDialogRef<SyslogMonitorDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: SyslogMonitorConfig,
        private syslogMonitorService: SyslogMonitorConfigService,
        private notifierService: NotifierConfigService
    ) {
        this.isEdit = !!this.data.id;
    }

    ngOnInit(): void {
        this.notifierService.getAll().subscribe(data => {
            this.notifiers = data;
        });
    }

    onNoClick(): void {
        this.dialogRef.close();
    }

    save(): void {
        const operation = this.isEdit
            ? this.syslogMonitorService.update(this.data.id, this.data)
            : this.syslogMonitorService.create(this.data);

        operation.subscribe(() => {
            this.dialogRef.close(true);
        });
    }

    compareNotifiers(o1: NotifierConfig, o2: NotifierConfig): boolean {
        return o1 && o2 ? o1.id === o2.id : o1 === o2;
    }
}
