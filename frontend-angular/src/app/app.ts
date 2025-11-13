import { Component, ChangeDetectionStrategy, signal, computed, WritableSignal, Signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Category, MenuItem, CartItem, PastOrder, Customer } from './models/menu.model';
import { MenuService } from './services/menu.service';

// Define a type for our pop-up notifications
interface Notification {
  id: number;
  message: string;
  icon: 'cart' | 'check' | 'error';
}
@Component({
 selector: 'app-root',
  standalone: true,
  // Make sure to import CommonModule, RouterOutlet, and FormsModule
  imports: [CommonModule, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class App {
  menu: WritableSignal<Category[]> = signal([]);
  cart: WritableSignal<CartItem[]> = signal([]);
  orderHistory: WritableSignal<PastOrder[]> = signal([]);
  selectedCategory: WritableSignal<string> = signal('');
  searchTerm = signal('');
  isLoading = signal(true);
  error = signal<string | null>(null);

  // UI state
  isMobileCartVisible = signal(false);
 isSidebarVisible = signal(true);
  isCategoryDropdownOpen = signal(false);
  notifications: WritableSignal<Notification[]> = signal([]);

  private nextNotificationId = 0;
  
  // Inject our MenuService
  private menuService = inject(MenuService);

  // --- COMPONENT INITIALIZATION ---
  constructor() {
    this.loadOrderHistory(); // Load history from backend
    this.loadMenu();         // Load menu from backend
  }

  // --- COMPUTED SIGNALS ---
  // These are read-only signals that recalculate when their dependencies change

  // Calculates the items to display based on search or category
  filteredMenu: Signal<MenuItem[]> = computed(() => {
    if (this.isLoading()) return [];

    const term = this.searchTerm().toLowerCase().trim();
    if (term) {
      // If there's a search term, search all items
      const allItems = this.menu().flatMap(category => category.items);
      return allItems.filter(item =>
        item.name.toLowerCase().includes(term) ||
        item.description.toLowerCase().includes(term)
      );
    }
    
    // Otherwise, show items from the selected category
    const category = this.menu().find(c => c.name === this.selectedCategory());
    return category ? category.items : [];
  });

  // Calculates the total number of items in the cart
  cartCount: Signal<number> = computed(() => {
    return this.cart().reduce((total, item) => total + item.quantity, 0);
  });
  
  // Calculates the total price of the cart
  cartTotal: Signal<number> = computed(() => {
    return this.cart().reduce((total, item) => total + item.price * item.quantity, 0);
  });

  // --- DATA LOADING METHODS ---

  private loadMenu(): void {
    this.isLoading.set(true);
    this.error.set(null);
    this.menuService.getMenuData().subscribe({
      next: (menuData) => {
        const validMenuData = menuData.filter(category => category && category.items.length > 0);
        this.menu.set(validMenuData);
        // Set the first category as the default
        if (validMenuData.length > 0) {
          this.selectedCategory.set(validMenuData[0].name);
        }
        this.isLoading.set(false);
      },
      error: (err: Error) => {
        console.error('Failed to load menu', err);
        this.error.set(err.message || 'An unknown error occurred while loading the menu.');
        this.isLoading.set(false);
      }
    });
  }

  private loadOrderHistory(): void {
    this.menuService.getOrderHistory().subscribe({
      next: (history) => {
        this.orderHistory.set(history);
      },
      error: (err: Error) => {
        console.error('Failed to load order history', err);
        this.addNotification(err.message || 'Could not load order history.', 'error');
      }
    });
  }

  // --- USER ACTION METHODS ---

  retryLoadMenu(): void {
    this.loadMenu();
  }

  selectCategory(categoryName: string): void {
    this.selectedCategory.set(categoryName);
    this.isCategoryDropdownOpen.set(false); // Close dropdown on selection
    this.searchTerm.set(''); // Clear search term
  }

  addToCart(itemToAdd: MenuItem): void {
    this.cart.update(currentCart => {
      const existingItem = currentCart.find(item => item.id === itemToAdd.id);
      if (existingItem) {
        // If item exists, just increase quantity
        return currentCart.map(item =>
          item.id === itemToAdd.id ? { ...item, quantity: item.quantity + 1 } : item
        );
      }
      // Otherwise, add new item to cart
      return [...currentCart, { ...itemToAdd, quantity: 1 }];
    });
    this.addNotification(`'${itemToAdd.name}' added to cart`, 'cart');
  }

  updateQuantity(itemId: string, change: number): void {
    this.cart.update(currentCart => {
      const item = currentCart.find(i => i.id === itemId);
      if (!item) return currentCart;

      const newQuantity = item.quantity + change;
      if (newQuantity <= 0) {
        // Remove item if quantity is zero or less
        return currentCart.filter(i => i.id !== itemId);
      }
      // Otherwise, update quantity
      return currentCart.map(i => i.id === itemId ? { ...i, quantity: newQuantity } : i);
    });
  }
  
  placeOrder(): void {
    const currentCart = this.cart();
    if (currentCart.length === 0) return;

    // Create a hardcoded customer to satisfy the backend model
    const guestCustomer: Customer = {
      name: "Guest User",
      email: "guest@example.com",
      phoneNumber: "N/A"
    };

    const newOrderPayload = {
      items: currentCart,
      total: this.cartTotal() * 1.1, // Example: Total includes 10% tax
      customer: guestCustomer
    };

    // Call the service to POST the order
    this.menuService.placeOrder(newOrderPayload).subscribe({
      next: (savedOrder) => {
        // Add the newly saved order to the top of the history
        this.orderHistory.update(history => [savedOrder, ...history]);
        this.addNotification('Your order has been successfully placed!', 'check');
        this.cart.set([]); // Clear the cart
        this.closeMobileCart();
        this.isSidebarVisible.set(false);
      },
      error: (err: Error) => {
        console.error('Failed to place order', err);
        this.addNotification(err.message, 'error');
      }
    });
  }

  // --- UI HELPER METHODS ---

  toggleSidebar(): void {
    this.isSidebarVisible.update(v => !v);
  }

  toggleCategoryDropdown(): void {
    this.isCategoryDropdownOpen.update(v => !v);
  }

  openMobileCart(): void {
    this.isMobileCartVisible.set(true);
  }

  closeMobileCart(): void {
    this.isMobileCartVisible.set(false);
  }

  // Formats the date string from our backend
  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleString(undefined, { 
      year: 'numeric', 
      month: 'short', 
      day: 'numeric', 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  }

  // Manages pop-up notifications
  addNotification(message: string, icon: 'cart' | 'check' | 'error'): void {
    const id = this.nextNotificationId++;
    const newNotification: Notification = { id, message, icon };
    this.notifications.update(current => [...current, newNotification]);

    // Remove notification after 3 seconds
    setTimeout(() => {
      this.notifications.update(current => current.filter(n => n.id !== id));
    }, 3000);
  }
}