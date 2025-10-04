import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {HttpMonitorConfig} from './models';

@Injectable({providedIn: 'root'})
export class HttpMonitorConfigService {
    private apiUrl = '/api/monitors/http';

    constructor(private http: HttpClient) {
    }

    getAll(): Observable<HttpMonitorConfig[]> {
        return this.http.get<HttpMonitorConfig[]>(this.apiUrl);
    }

    getById(id: number): Observable<HttpMonitorConfig> {
        return this.http.get<HttpMonitorConfig>(`${this.apiUrl}/${id}`);
    }

    create(config: HttpMonitorConfig): Observable<HttpMonitorConfig> {
        return this.http.post<HttpMonitorConfig>(this.apiUrl, config);
    }

    update(id: number, config: HttpMonitorConfig): Observable<HttpMonitorConfig> {
        return this.http.put<HttpMonitorConfig>(`${this.apiUrl}/${id}`, config);
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
