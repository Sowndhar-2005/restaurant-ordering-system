// This is the TypeScript equivalent of our Java MenuItem model
export interface MenuItem {
  id: string;
  name: string;
  description: string;
  price: number;
  imageUrl: string;
}

// This is the TypeScript equivalent of our Java Category model
export interface Category {
  name: string;
  items: MenuItem[];
}

// This model is for the items in our shopping cart
export interface CartItem extends MenuItem {
  quantity: number;
}

// This is the TypeScript equivalent of our Java Customer model
export interface Customer {
  name: string;
  email: string;
  phoneNumber: string;
}

// This is the TypeScript equivalent of our Java Order model
// We use 'PastOrder' to avoid confusion with any other 'Order' types
export interface PastOrder {
  id: number;
  date: string;
  items: CartItem[];
  total: number;
  customer: Customer;
}