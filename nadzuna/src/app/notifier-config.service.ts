import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {NotifierConfig} from './models';

@Injectable({providedIn: 'root'})
export class NotifierConfigService {
    private apiUrl = '/api/notifiers';

    constructor(private http: HttpClient) {
    }

    getAll(): Observable<NotifierConfig[]> {
        return this.http.get<NotifierConfig[]>(this.apiUrl);
    }

    create(config: NotifierConfig): Observable<NotifierConfig> {
        return this.http.post<NotifierConfig>(this.apiUrl, config);
    }

    update(id: number, config: NotifierConfig): Observable<NotifierConfig> {
        return this.http.put<NotifierConfig>(`${this.apiUrl}/${id}`, config);
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
