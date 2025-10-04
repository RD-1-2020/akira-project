import { Routes } from '@angular/router';
import { HttpMonitorsComponent } from './http-monitors/http-monitors.component';
import { SyslogMonitorsComponent } from './syslog-monitors/syslog-monitors.component';
import { NotificationsComponent } from './notifications/notifications.component';
import { NotifiersComponent } from './notifiers/notifiers.component';

export const routes: Routes = [
    {path: 'http-monitors', component: HttpMonitorsComponent},
    {path: 'syslog-monitors', component: SyslogMonitorsComponent},
    {path: 'notifiers', component: NotifiersComponent},
    {path: 'notifications', component: NotificationsComponent},
    {path: '', redirectTo: '/http-monitors', pathMatch: 'full'},
];
