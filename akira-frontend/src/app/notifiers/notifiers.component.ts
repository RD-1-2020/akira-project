import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {NotifierConfig} from '../models';
import {NotifierConfigService} from '../notifier-config.service';
import {NotifierDialogComponent} from './notifier-dialog.component';
import {CommonModule} from '@angular/common';
import {MaterialModule} from '../material.module';

@Component({
    selector: 'app-notifiers',
    templateUrl: './notifiers.component.html',
    styleUrls: ['./notifiers.component.scss'],
    standalone: true,
    imports: [CommonModule, MaterialModule],
})
export class NotifiersComponent implements OnInit {

    displayedColumns: string[] = ['name', 'type', 'actions'];
    dataSource: NotifierConfig[] = [];

    constructor(
        private dialog: MatDialog,
        private notifierService: NotifierConfigService
    ) {
    }

    ngOnInit(): void {
        this.loadNotifiers();
    }

    loadNotifiers(): void {
        this.notifierService.getAll().subscribe(data => {
            this.dataSource = data;
        });
    }

    openDialog(data?: NotifierConfig): void {
        const dialogRef = this.dialog.open(NotifierDialogComponent, {
            width: '400px',
            data: data ? JSON.parse(JSON.stringify(data)) : {} // Deep copy
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.loadNotifiers();
            }
        });
    }

    delete(id: number): void {
        if (confirm('Are you sure you want to delete this notifier?')) {
            this.notifierService.delete(id).subscribe(() => {
                this.loadNotifiers();
            });
        }
    }
}
