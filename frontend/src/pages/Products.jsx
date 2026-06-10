import { Filter, Minus, Plus, Search, Trash2 } from "lucide-react";
import React, { useEffect, useMemo, useState } from "react";
import { api, demoProducts } from "../api/client.js";

export default function Products({ cart, addToCart, setCart }) {
  const [products, setProducts] = useState(demoProducts);
  const [query, setQuery] = useState("");
  const [category, setCategory] = useState("todos");

  useEffect(() => {
    api("/productos").then(setProducts).catch(() => setProducts(demoProducts));
  }, []);

  const filtered = useMemo(() => products.filter((product) => {
    const matchesText = product.nombre.toLowerCase().includes(query.toLowerCase());
    const matchesCategory = category === "todos" || product.categoriaId === category;
    return matchesText && matchesCategory;
  }), [products, query, category]);

  const total = cart.reduce((sum, item) => sum + item.precio * item.cantidad, 0);

  return (
    <div className="workspace-grid">
      <section className="catalog-panel">
        <div className="toolbar">
          <label className="search-box">
            <Search size={18} />
            <input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Buscar productos" />
          </label>
          <label className="select-box">
            <Filter size={18} />
            <select value={category} onChange={(event) => setCategory(event.target.value)}>
              <option value="todos">Todos</option>
              <option value="tecnologia">Tecnologia</option>
              <option value="hogar">Hogar</option>
              <option value="moda">Moda</option>
            </select>
          </label>
        </div>

        <div className="product-grid">
          {filtered.map((product) => (
            <article className="product-card" key={product._id}>
              <img src={product.imagenUrl} alt={product.nombre} />
              <div className="product-body">
                <span className="category-chip">{product.categoriaId}</span>
                <h2>{product.nombre}</h2>
                <p>{product.descripcion}</p>
                <div className="product-actions">
                  <strong>S/ {product.precio.toFixed(2)}</strong>
                  <button onClick={() => addToCart(product)}><Plus size={18} /> Agregar</button>
                </div>
              </div>
            </article>
          ))}
        </div>
      </section>

      <aside className="checkout-panel">
        <h2>Checkout</h2>
        <div className="cart-items">
          {cart.map((item) => (
            <div className="cart-line" key={item._id}>
              <div>
                <strong>{item.nombre}</strong>
                <span>S/ {item.precio.toFixed(2)} x {item.cantidad}</span>
              </div>
              <div className="qty-tools">
                <button aria-label="Reducir" onClick={() => setCart(cart.map((cartItem) => cartItem._id === item._id ? { ...cartItem, cantidad: Math.max(1, cartItem.cantidad - 1) } : cartItem))}><Minus size={16} /></button>
                <button aria-label="Eliminar" onClick={() => setCart(cart.filter((cartItem) => cartItem._id !== item._id))}><Trash2 size={16} /></button>
              </div>
            </div>
          ))}
        </div>
        <div className="total-box">
          <span>Total</span>
          <strong>S/ {total.toFixed(2)}</strong>
        </div>
        <button className="primary-action">Confirmar pedido</button>
      </aside>
    </div>
  );
}
