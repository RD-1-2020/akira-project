import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Client, ClientMessage, Page} from './models';

@Injectable({
  providedIn: 'root'
})
export class ClientService {

  private apiUrl = '/api/clients';

  constructor(private http: HttpClient) { }

  getClients(): Observable<Client[]> {
    return this.http.get<Client[]>(this.apiUrl);
  }

  getClientMessages(hostname: string, page: number, size: number, from?: string, to?: string): Observable<Page<ClientMessage>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'receivedAt,desc');

    if (from) {
      params = params.set('from', from);
    }
    if (to) {
      params = params.set('to', to);
    }

    return this.http.get<Page<ClientMessage>>(`${this.apiUrl}/${hostname}/messages`, { params });
  }

  getClientStats(hostname: string): Observable<{[key: string]: number}> {
    return this.http.get<{[key: string]: number}>(`${this.apiUrl}/${hostname}/stats`);
  }
}

