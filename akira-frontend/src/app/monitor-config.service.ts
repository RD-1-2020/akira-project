import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MonitorConfig } from './models';

@Injectable({ providedIn: 'root' })
export class MonitorConfigService {
    private apiUrl = '/api/monitors';

    constructor(private http: HttpClient) {
    }

    getAll(): Observable<MonitorConfig[]> {
        return this.http.get<MonitorConfig[]>(this.apiUrl);
    }

    create(config: MonitorConfig): Observable<MonitorConfig> {
        return this.http.post<MonitorConfig>(this.apiUrl, config);
    }

    update(id: number, config: MonitorConfig): Observable<MonitorConfig> {
        return this.http.put<MonitorConfig>(`${this.apiUrl}/${id}`, config);
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
