import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Observable, throwError} from "rxjs";
import {catchError} from "rxjs/operators";

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

    constructor(private snackBar: MatSnackBar) {
    }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(req).pipe(
            catchError((error: HttpErrorResponse) => {
                let errorMessage = 'An unknown error occurred!';
                if (error.error && error.error.message) {
                    // Use the custom AkiraErrorDto message
                    errorMessage = `[${error.error.errorCode}] ${error.error.message}`;
                } else if (error.status === 401 || error.status === 403) {
                    // Redirect to login page for auth errors
                    window.location.href = '/login';
                    errorMessage = 'Authentication error. Please log in again.';
                } else if (error.message) {
                    errorMessage = error.message;
                }

                this.snackBar.open(errorMessage, 'Close', {
                    duration: 5000,
                    panelClass: ['mat-toolbar', 'mat-warn']
                });

                return throwError(() => error);
            })
        );
    }
}
