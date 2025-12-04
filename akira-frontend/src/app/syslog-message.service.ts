import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SyslogMessage, SyslogMessageStats } from './models';

@Injectable({ providedIn: 'root' })
export class SyslogMessageService {
    private apiUrl = '/api/syslog-messages';

    constructor(private http: HttpClient) {
    }

    getMessagesBySourceId(sourceId: number, startDate?: number, endDate?: number): Observable<SyslogMessage[]> {
        let params: any = { sourceId: sourceId.toString() };
        if (startDate !== undefined && startDate !== null) {
            params.startDate = startDate.toString();
        }
        if (endDate !== undefined && endDate !== null) {
            params.endDate = endDate.toString();
        }
        return this.http.get<SyslogMessage[]>(this.apiUrl, { params });
    }

    getStats(sourceId: number, startDate: number, endDate: number): Observable<SyslogMessageStats> {
        return this.http.get<SyslogMessageStats>(`${this.apiUrl}/stats`, { 
            params: { 
                sourceId: sourceId.toString(),
                startDate: startDate.toString(),
                endDate: endDate.toString()
            } 
        });
    }
}


