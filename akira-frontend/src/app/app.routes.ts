import { Routes } from '@angular/router';
import { NotificationsComponent } from './notifications/notifications.component';
import { NotifiersComponent } from './notifiers/notifiers.component';
import { MonitorsComponent } from './monitors/monitors.component';

export const routes: Routes = [
    {path: 'monitors', component: MonitorsComponent},
    {path: 'notifiers', component: NotifiersComponent},
    {path: 'notifications', component: NotificationsComponent},
    {path: '', redirectTo: '/monitors', pathMatch: 'full'},
];
