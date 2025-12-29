import {Routes} from '@angular/router';
import {NotificationsComponent} from './notifications/notifications.component';
import {NotifiersComponent} from './notifiers/notifiers.component';
import {ClientsComponent} from './clients/clients.component';
import {ClientDetailComponent} from './clients/client-detail.component';

export const routes: Routes = [
    {path: 'clients', component: ClientsComponent},
    {path: 'clients/:hostname', component: ClientDetailComponent},
    {path: 'notifiers', component: NotifiersComponent},
    {path: 'notifications', component: NotificationsComponent},
    {path: '', redirectTo: '/clients', pathMatch: 'full'},
];
