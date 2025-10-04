import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MaterialModule } from '../material.module';
import { MonitorConfig } from '../models';
import { MonitorConfigService } from '../monitor-config.service';

@Component({
  selector: 'app-monitor-dialog',
  templateUrl: './monitor-dialog.component.html',
  standalone: true,
  imports: [CommonModule, FormsModule, MaterialModule],
})
export class MonitorDialogComponent {
  monitor: Partial<MonitorConfig> = {};
  monitorTypes = ['HTTP', 'SYSLOG'];

  constructor(
    public dialogRef: MatDialogRef<MonitorDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Partial<MonitorConfig>,
    private monitorService: MonitorConfigService
  ) {
    this.monitor = { ...data };
    if (!this.monitor.monitor_type) {
      this.monitor.monitor_type = 'HTTP';
    }
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  save(): void {
    const action = this.monitor.id
      ? this.monitorService.update(this.monitor.id, this.monitor as MonitorConfig)
      : this.monitorService.create(this.monitor as MonitorConfig);

    action.subscribe(() => {
      this.dialogRef.close(true);
    });
  }
}
