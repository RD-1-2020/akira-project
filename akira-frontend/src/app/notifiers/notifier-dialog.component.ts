import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { NotifierConfig, TelegramNotifierConfig } from '../models';
import { NotifierConfigService } from '../notifier-config.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MaterialModule } from '../material.module';

@Component({
    selector: 'app-notifier-dialog',
    templateUrl: './notifier-dialog.component.html',
    standalone: true,
    imports: [CommonModule, FormsModule, MaterialModule],
})
export class NotifierDialogComponent {
    notifier: Partial<TelegramNotifierConfig> = {};
    notifierTypes = ['TELEGRAM'];

    constructor(
        public dialogRef: MatDialogRef<NotifierDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: Partial<NotifierConfig>,
        private notifierService: NotifierConfigService
    ) {
        this.notifier = { ...data };
        if (!this.notifier.notifier_type) {
            this.notifier.notifier_type = 'TELEGRAM';
        }
    }

    onNoClick(): void {
        this.dialogRef.close();
    }

    save(): void {
        const action = this.notifier.id
            ? this.notifierService.update(this.notifier.id, this.notifier as NotifierConfig)
            : this.notifierService.create(this.notifier as NotifierConfig);

        action.subscribe(() => {
            this.dialogRef.close(true);
        });
    }
}
