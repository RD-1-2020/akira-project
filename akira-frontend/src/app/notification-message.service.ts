import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {NotificationMessage} from './models';

@Injectable({providedIn: 'root'})
export class NotificationMessageService {
    private apiUrl = '/api/notifiers/messages';

    constructor(private http: HttpClient) {
    }

    getAll(): Observable<NotificationMessage[]> {
        return this.http.get<NotificationMessage[]>(this.apiUrl);
    }

    getById(id: number): Observable<NotificationMessage> {
        return this.http.get<NotificationMessage>(`${this.apiUrl}/${id}`);
    }

    retry(id: number): Observable<NotificationMessage> {
        return this.http.post<NotificationMessage>(`${this.apiUrl}/${id}/retry`, {});
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
