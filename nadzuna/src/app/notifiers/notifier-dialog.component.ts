import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {NotifierConfig, NotifierType, TelegramNotifierConfig} from '../models';
import {NotifierConfigService} from '../notifier-config.service';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {MaterialModule} from '../material.module';

@Component({
    selector: 'app-notifier-dialog',
    templateUrl: './notifier-dialog.component.html',
    standalone: true,
    imports: [CommonModule, FormsModule, MaterialModule],
})
export class NotifierDialogComponent {
    isEdit: boolean;
    notifierTypes = Object.values(NotifierType);

    get telegramData(): TelegramNotifierConfig {
        return this.data as TelegramNotifierConfig;
    }

    constructor(
        public dialogRef: MatDialogRef<NotifierDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: NotifierConfig,
        private notifierService: NotifierConfigService
    ) {
        this.isEdit = !!this.data.id;
    }

    onNoClick(): void {
        this.dialogRef.close();
    }

    save(): void {
        const operation = this.isEdit
            ? this.notifierService.update(this.data.id, this.data)
            : this.notifierService.create(this.data);

        operation.subscribe(() => {
            this.dialogRef.close(true);
        });
    }
}
