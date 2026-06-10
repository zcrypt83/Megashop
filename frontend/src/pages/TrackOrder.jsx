import { CheckCircle2, Clock3, PackageCheck, Truck } from "lucide-react";
import React from "react";

const states = [
  { label: "Confirmado", icon: CheckCircle2, done: true },
  { label: "Preparando", icon: PackageCheck, done: true },
  { label: "En ruta", icon: Truck, done: true },
  { label: "Entregado", icon: Clock3, done: false }
];

export default function TrackOrder() {
  return (
    <section className="tracking-page">
      <div className="tracking-map">
        <div className="route-line" />
        <div className="driver-pin"><Truck size={24} /></div>
      </div>
      <div className="status-list">
        <h2>Pedido MS-10482</h2>
        {states.map(({ label, icon: Icon, done }) => (
          <div className={`status-item ${done ? "done" : ""}`} key={label}>
            <Icon size={20} />
            <span>{label}</span>
          </div>
        ))}
      </div>
    </section>
  );
}
