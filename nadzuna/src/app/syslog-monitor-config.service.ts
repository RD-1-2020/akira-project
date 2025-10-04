import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {SyslogMonitorConfig} from './models';

@Injectable({providedIn: 'root'})
export class SyslogMonitorConfigService {
    private apiUrl = '/api/monitors/syslog';

    constructor(private http: HttpClient) {
    }

    getAll(): Observable<SyslogMonitorConfig[]> {
        return this.http.get<SyslogMonitorConfig[]>(this.apiUrl);
    }

    getById(id: number): Observable<SyslogMonitorConfig> {
        return this.http.get<SyslogMonitorConfig>(`${this.apiUrl}/${id}`);
    }

    create(config: SyslogMonitorConfig): Observable<SyslogMonitorConfig> {
        return this.http.post<SyslogMonitorConfig>(this.apiUrl, config);
    }

    update(id: number, config: SyslogMonitorConfig): Observable<SyslogMonitorConfig> {
        return this.http.put<SyslogMonitorConfig>(`${this.apiUrl}/${id}`, config);
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
