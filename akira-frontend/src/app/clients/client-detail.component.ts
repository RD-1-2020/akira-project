import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MaterialModule} from '../material.module';
import {ActivatedRoute} from '@angular/router';
import {ClientService} from '../client.service';
import {Client, ClientMessage} from '../models';
import {PageEvent} from '@angular/material/paginator';
import {FormControl, ReactiveFormsModule} from '@angular/forms';

@Component({
  selector: 'app-client-detail',
  standalone: true,
  imports: [CommonModule, MaterialModule, ReactiveFormsModule],
  templateUrl: './client-detail.component.html',
  styleUrls: ['./client-detail.component.scss']
})
export class ClientDetailComponent implements OnInit {
  hostname: string = '';
  client: Client | undefined;
  messages: ClientMessage[] = [];
  totalMessages = 0;
  pageSize = 20;
  pageIndex = 0;
  displayedColumns: string[] = ['id', 'message', 'clientTimestamp', 'receivedAt'];
  
  stats: {hour: number, count: number}[] = [];
  maxCount = 0;

  dateControl = new FormControl<Date | null>(null);

  constructor(
    private route: ActivatedRoute,
    private clientService: ClientService
  ) {}

  ngOnInit(): void {
    this.hostname = this.route.snapshot.paramMap.get('hostname') || '';
    this.loadClientInfo();
    this.loadMessages();
    this.loadStats();

    this.dateControl.valueChanges.subscribe(() => {
        this.pageIndex = 0;
        this.loadMessages();
    });
  }

  loadClientInfo(): void {
    this.clientService.getClients().subscribe(clients => {
      this.client = clients.find(c => c.hostname === this.hostname);
    });
  }

  loadMessages(event?: PageEvent): void {
    if (event) {
      this.pageIndex = event.pageIndex;
      this.pageSize = event.pageSize;
    }

    let fromStr: string | undefined;
    let toStr: string | undefined;

    if (this.dateControl.value) {
        const d = this.dateControl.value;
        const start = new Date(d);
        start.setHours(0,0,0,0);
        const end = new Date(d);
        end.setHours(23,59,59,999);
        fromStr = start.toISOString();
        toStr = end.toISOString();
    }

    this.clientService.getClientMessages(this.hostname, this.pageIndex, this.pageSize, fromStr, toStr)
      .subscribe(page => {
        this.messages = page.content;
        this.totalMessages = page.totalElements;
      });
  }

  loadStats(): void {
    this.clientService.getClientStats(this.hostname).subscribe(data => {
      this.stats = Object.entries(data).map(([k, v]) => ({hour: parseInt(k), count: v}));
      this.maxCount = Math.max(...this.stats.map(s => s.count), 1); // Avoid div by zero
    });
  }
  
  getBarHeight(count: number): string {
      const percentage = (count / this.maxCount) * 100;
      return `${percentage}%`;
  }
}

