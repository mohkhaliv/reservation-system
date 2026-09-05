export interface Product {
  id: number;
  name: string;
  description: string | null;
  price: number;
  stock: number;
}

export type ReservationStatus =
  | "PENDING"
  | "CONFIRMED"
  | "CANCELLED";

export interface Reservation {
  id: number;
  productId: number;
  productName: string;
  quantity: number;
  status: ReservationStatus;
  createdAt: string;
}

export interface CreateProductInput {
  name: string;
  description: string;
  price: number;
  stock: number;
}