import { Package, Receipt, TrendingUp, Users } from "lucide-react";
import React, { useEffect, useState } from "react";
import { api } from "../api/client.js";

const fallback = { clientes: 100, productos: 200, pedidos: 500, ventas: 84590, hotProducts: [] };

export default function Dashboard() {
  const [summary, setSummary] = useState(fallback);

  useEffect(() => {
    api("/dashboard/resumen").then(setSummary).catch(() => setSummary(fallback));
  }, []);

  const cards = [
    { label: "Clientes", value: summary.clientes, icon: Users },
    { label: "Productos", value: summary.productos, icon: Package },
    { label: "Pedidos", value: summary.pedidos, icon: Receipt },
    { label: "Ventas", value: `S/ ${Number(summary.ventas).toLocaleString("es-PE")}`, icon: TrendingUp }
  ];

  return (
    <section className="dashboard">
      <div className="metric-grid">
        {cards.map(({ label, value, icon: Icon }) => (
          <article className="metric-card" key={label}>
            <Icon size={22} />
            <span>{label}</span>
            <strong>{value}</strong>
          </article>
        ))}
      </div>
      <div className="ops-grid">
        <div className="ops-panel">
          <h2>Ventas por canal</h2>
          <div className="bar-list">
            <span style={{ "--size": "86%" }}>App movil</span>
            <span style={{ "--size": "64%" }}>Web</span>
            <span style={{ "--size": "42%" }}>Marketplaces</span>
          </div>
        </div>
        <div className="ops-panel">
          <h2>Cola de entregas</h2>
          {["Pendiente", "Preparando", "En ruta", "Entregado"].map((state, index) => (
            <div className="delivery-row" key={state}>
              <span>{state}</span>
              <strong>{[38, 24, 17, 421][index]}</strong>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
