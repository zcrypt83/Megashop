import { LockKeyhole, Mail } from "lucide-react";
import React, { useState } from "react";
import { api } from "../api/client.js";

export default function Login() {
  const [email, setEmail] = useState("admin@megashop.pe");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");

  async function submit(event) {
    event.preventDefault();
    try {
      const session = await api("/auth/login", {
        method: "POST",
        body: JSON.stringify({ email, password })
      });
      localStorage.setItem("megashop_token", session.accessToken);
      setMessage(`Bienvenido, ${session.user.nombre}`);
    } catch (error) {
      setMessage(error.message);
    }
  }

  return (
    <section className="login-layout">
      <form className="login-form" onSubmit={submit}>
        <h2>Acceso seguro</h2>
        <label>
          <Mail size={18} />
          <input value={email} onChange={(event) => setEmail(event.target.value)} type="email" />
        </label>
        <label>
          <LockKeyhole size={18} />
          <input value={password} onChange={(event) => setPassword(event.target.value)} type="password" />
        </label>
        <button className="primary-action">Ingresar</button>
        {message && <p className="form-message">{message}</p>}
      </form>
    </section>
  );
}
