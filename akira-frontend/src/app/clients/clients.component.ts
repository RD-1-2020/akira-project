import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MaterialModule} from '../material.module';
import {RouterModule} from '@angular/router';
import {ClientService} from '../client.service';
import {Client} from '../models';

@Component({
  selector: 'app-clients',
  standalone: true,
  imports: [CommonModule, MaterialModule, RouterModule],
  templateUrl: './clients.component.html',
  styleUrls: ['./clients.component.scss']
})
export class ClientsComponent implements OnInit {
  clients: Client[] = [];
  displayedColumns: string[] = ['hostname', 'status', 'lastMessageAt', 'actions'];

  constructor(private clientService: ClientService) { }

  ngOnInit(): void {
    this.loadClients();
  }

  loadClients(): void {
    this.clientService.getClients().subscribe(data => {
      this.clients = data;
    });
  }
}

