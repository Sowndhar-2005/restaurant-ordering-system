import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { Category, PastOrder, CartItem, Customer } from '../models/menu.model'; // Import our models

@Injectable({
  providedIn: 'root',
})
export class MenuService {
  // --- UPDATED: This is the URL of our Java backend ---
  private baseUrl = 'http://localhost:8080/api';

  private http = inject(HttpClient);

  // --- Method to GET the menu from our backend ---
  getMenuData(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.baseUrl}/menu`).pipe(
      retry(1), // Try one more time if it fails
      catchError(this.handleError) // Call our error handler
    );
  }

  // --- Method to GET the order history from our backend ---
  getOrderHistory(): Observable<PastOrder[]> {
    return this.http.get<PastOrder[]>(`${this.baseUrl}/orders`).pipe(
      catchError(this.handleError)
    );
  }

  // --- Method to POST a new order to our backend ---
  placeOrder(orderPayload: { items: CartItem[]; total: number; customer: Customer }): Observable<PastOrder> {
    return this.http.post<PastOrder>(`${this.baseUrl}/orders`, orderPayload).pipe(
      catchError(this.handleError)
    );
  }

  // --- A private method to handle all API errors ---
  private handleError(error: HttpErrorResponse): Observable<never> {
    console.error('An API error occurred:', error);

    let userMessage = 'Something bad happened; please try again later.';
    
    if (error.status === 0) {
      // A client-side or network error
      userMessage = 'Cannot connect to the backend. Is the Java server running?';
    } else if (error.status === 400 && error.error?.error) {
      // A "Bad Request" error from our backend (e.g., "Order must contain items")
      userMessage = error.error.error; // Use the error message from our Java exception handler
    } else {
      // Any other backend error
      userMessage = `Error ${error.status}: ${error.error?.error || 'Server error'}`;
    }

    // Return an observable that emits the user-friendly error message
    return throwError(() => new Error(userMessage));
  }
}