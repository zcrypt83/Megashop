import { BarChart3, LogIn, PackageSearch, ShoppingCart, Truck, UserRound } from "lucide-react";
import { NavLink, Route, Routes } from "react-router-dom";
import React, { useMemo, useState } from "react";
import Dashboard from "./pages/Dashboard.jsx";
import Login from "./pages/Login.jsx";
import Products from "./pages/Products.jsx";
import TrackOrder from "./pages/TrackOrder.jsx";

export default function App() {
  const [cart, setCart] = useState([]);
  const cartCount = useMemo(() => cart.reduce((sum, item) => sum + item.cantidad, 0), [cart]);

  function addToCart(product) {
    setCart((items) => {
      const exists = items.find((item) => item._id === product._id);
      if (exists) return items.map((item) => item._id === product._id ? { ...item, cantidad: item.cantidad + 1 } : item);
      return [...items, { ...product, cantidad: 1 }];
    });
  }

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <span className="brand-mark">M</span>
          <div>
            <strong>Megashop</strong>
            <small>Delivery commerce</small>
          </div>
        </div>
        <nav className="nav-list">
          <NavLink to="/" end><PackageSearch size={18} /> Productos</NavLink>
          <NavLink to="/tracking"><Truck size={18} /> Seguimiento</NavLink>
          <NavLink to="/dashboard"><BarChart3 size={18} /> Admin</NavLink>
          <NavLink to="/login"><LogIn size={18} /> Acceso</NavLink>
        </nav>
        <div className="cart-summary">
          <ShoppingCart size={18} />
          <span>{cartCount} items</span>
        </div>
      </aside>

      <main className="content">
        <header className="topbar">
          <div>
            <p className="eyebrow">MongoDB + Redis</p>
            <h1>Operacion comercial en tiempo real</h1>
          </div>
          <button className="icon-button" aria-label="Perfil"><UserRound size={20} /></button>
        </header>

        <Routes>
          <Route path="/" element={<Products cart={cart} addToCart={addToCart} setCart={setCart} />} />
          <Route path="/tracking" element={<TrackOrder />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/login" element={<Login />} />
        </Routes>
      </main>
    </div>
  );
}
