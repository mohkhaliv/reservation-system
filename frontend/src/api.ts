import type {
  CreateProductInput,
  Product,
  Reservation,
} from "./types";

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ??
  "http://localhost:8080";

async function request<T>(
  url: string,
  options?: RequestInit
): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${url}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...options?.headers,
    },
  });

  if (!response.ok) {
    let message = "Request failed";

    try {
      const data = await response.json();
      message = data.error ?? message;
    } catch {
      // Ignore non-JSON error body
    }

    throw new Error(message);
  }

  return response.json();
}

export function getProducts(): Promise<Product[]> {
  return request<Product[]>("/api/products");
}

export function createProduct(
  product: CreateProductInput
): Promise<Product> {
  return request<Product>("/api/products", {
    method: "POST",
    body: JSON.stringify(product),
  });
}

export function getReservations(): Promise<Reservation[]> {
  return request<Reservation[]>("/api/reservations");
}

export function createReservation(
  productId: number,
  quantity: number
): Promise<Reservation> {
  return request<Reservation>("/api/reservations", {
    method: "POST",
    body: JSON.stringify({
      productId,
      quantity,
    }),
  });
}

export function confirmReservation(
  id: number
): Promise<Reservation> {
  return request<Reservation>(
    `/api/reservations/${id}/confirm`,
    {
      method: "PATCH",
    }
  );
}

export function cancelReservation(
  id: number
): Promise<Reservation> {
  return request<Reservation>(
    `/api/reservations/${id}/cancel`,
    {
      method: "PATCH",
    }
  );
}