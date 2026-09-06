import { useEffect, useState } from "react";
import type { FormEvent } from "react";
import {
  cancelReservation,
  confirmReservation,
  createProduct,
  createReservation,
  getProducts,
  getReservations,
} from "./api";
import type { Product, Reservation } from "./types";
import "./App.css";

function App() {
  const [products, setProducts] = useState<Product[]>([]);
  const [reservations, setReservations] =
    useState<Reservation[]>([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const [productName, setProductName] = useState("");
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState("");
  const [stock, setStock] = useState("");

  const [selectedProductId, setSelectedProductId] =
    useState("");
  const [quantity, setQuantity] = useState("1");

  async function loadData() {
    try {
      setLoading(true);

      const [productData, reservationData] =
        await Promise.all([
          getProducts(),
          getReservations(),
        ]);

      setProducts(productData);
      setReservations(reservationData);

      if (
        !selectedProductId &&
        productData.length > 0
      ) {
        setSelectedProductId(
          String(productData[0].id)
        );
      }
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to load data"
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadData();
  }, []);

  async function handleCreateProduct(
    event: FormEvent
  ) {
    event.preventDefault();
    setError("");
    setMessage("");

    try {
      await createProduct({
        name: productName,
        description,
        price: Number(price),
        stock: Number(stock),
      });

      setProductName("");
      setDescription("");
      setPrice("");
      setStock("");

      setMessage("Product created.");
      await loadData();
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to create product"
      );
    }
  }

  async function handleCreateReservation(
    event: FormEvent
  ) {
    event.preventDefault();
    setError("");
    setMessage("");

    try {
      await createReservation(
        Number(selectedProductId),
        Number(quantity)
      );

      setQuantity("1");
      setMessage("Reservation created.");
      await loadData();
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to create reservation"
      );
    }
  }

  async function handleConfirm(id: number) {
    setError("");
    setMessage("");

    try {
      await confirmReservation(id);
      setMessage("Reservation confirmed.");
      await loadData();
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to confirm reservation"
      );
    }
  }

  async function handleCancel(id: number) {
    setError("");
    setMessage("");

    try {
      await cancelReservation(id);
      setMessage("Reservation cancelled.");
      await loadData();
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to cancel reservation"
      );
    }
  }

  const formatCurrency = (value: number) =>
    new Intl.NumberFormat("id-ID", {
      style: "currency",
      currency: "IDR",
      maximumFractionDigits: 0,
    }).format(value);

  return (
    <main className="app">
      <header className="hero">
        <div>
          <h1>
            Reservation & Inventory Management
          </h1>
          <p className="subtitle">
            Spring Boot, PostgreSQL and React with
            transactional stock management
          </p>
        </div>
      </header>

      {error && (
        <div className="alert error">{error}</div>
      )}

      {message && (
        <div className="alert success">
          {message}
        </div>
      )}

      <section className="grid two-columns">
        <article className="panel">
          <h2>Add Product</h2>

          <form
            className="form"
            onSubmit={handleCreateProduct}
          >
            <input
              required
              placeholder="Product name"
              value={productName}
              onChange={(e) =>
                setProductName(e.target.value)
              }
            />

            <input
              placeholder="Description"
              value={description}
              onChange={(e) =>
                setDescription(e.target.value)
              }
            />

            <input
              required
              min="0"
              type="number"
              placeholder="Price"
              value={price}
              onChange={(e) =>
                setPrice(e.target.value)
              }
            />

            <input
              required
              min="0"
              type="number"
              placeholder="Initial stock"
              value={stock}
              onChange={(e) =>
                setStock(e.target.value)
              }
            />

            <button type="submit">
              Create Product
            </button>
          </form>
        </article>

        <article className="panel">
          <h2>Create Reservation</h2>

          <form
            className="form"
            onSubmit={handleCreateReservation}
          >
            <select
              required
              value={selectedProductId}
              onChange={(e) =>
                setSelectedProductId(
                  e.target.value
                )
              }
            >
              {products.length === 0 && (
                <option value="">
                  No products available
                </option>
              )}

              {products.map((product) => (
                <option
                  key={product.id}
                  value={product.id}
                >
                  {product.name} — stock{" "}
                  {product.stock}
                </option>
              ))}
            </select>

            <input
              required
              min="1"
              type="number"
              value={quantity}
              onChange={(e) =>
                setQuantity(e.target.value)
              }
            />

            <button
              type="submit"
              disabled={!selectedProductId}
            >
              Reserve
            </button>
          </form>
        </article>
      </section>

      <section className="section">
        <div className="section-heading">
          <h2>Products</h2>
          <span>{products.length} products</span>
        </div>

        {loading ? (
          <p>Loading...</p>
        ) : (
          <div className="product-grid">
            {products.map((product) => (
              <article
                className="product-card"
                key={product.id}
              >
                <div>
                  <h3>{product.name}</h3>
                  <p>
                    {product.description ||
                      "No description"}
                  </p>
                </div>

                <div className="product-meta">
                  <strong>
                    {formatCurrency(
                      product.price
                    )}
                  </strong>

                  <span>
                    Stock: {product.stock}
                  </span>
                </div>
              </article>
            ))}
          </div>
        )}
      </section>

      <section className="section">
        <div className="section-heading">
          <h2>Reservations</h2>
          <span>
            {reservations.length} reservations
          </span>
        </div>

        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Product</th>
                <th>Qty</th>
                <th>Status</th>
                <th>Created</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {reservations.map(
                (reservation) => (
                  <tr key={reservation.id}>
                    <td>#{reservation.id}</td>

                    <td>
                      {reservation.productName}
                    </td>

                    <td>
                      {reservation.quantity}
                    </td>

                    <td>
                      <span
                        className={`status ${reservation.status.toLowerCase()}`}
                      >
                        {reservation.status}
                      </span>
                    </td>

                    <td>
                      {new Date(
                        reservation.createdAt
                      ).toLocaleString()}
                    </td>

                    <td className="actions">
                      <button
                        className="small"
                        disabled={
                          reservation.status !==
                          "PENDING"
                        }
                        onClick={() =>
                          handleConfirm(
                            reservation.id
                          )
                        }
                      >
                        Confirm
                      </button>

                      <button
                        className="small secondary"
                        disabled={
                          reservation.status ===
                          "CANCELLED"
                        }
                        onClick={() =>
                          handleCancel(
                            reservation.id
                          )
                        }
                      >
                        Cancel
                      </button>
                    </td>
                  </tr>
                )
              )}

              {reservations.length === 0 && (
                <tr>
                  <td
                    colSpan={6}
                    className="empty"
                  >
                    No reservations yet.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </section>
    </main>
  );
}

export default App;