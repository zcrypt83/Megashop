const API_URL = import.meta.env.VITE_API_URL || "http://localhost:4000/api/v1";

export async function api(path, options = {}) {
  const token = localStorage.getItem("megashop_token");
  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options.headers
    }
  });
  const payload = await response.json();
  if (!response.ok) throw new Error(payload.error?.message || "Error de API");
  return payload.data;
}

export const demoProducts = [
  {
    _id: "demo-1",
    nombre: "Audifonos Wireless Pro",
    descripcion: "Audio inmersivo, cancelacion de ruido y bateria de larga duracion.",
    categoriaId: "tecnologia",
    precio: 189.9,
    stock: 34,
    imagenUrl: "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=80"
  },
  {
    _id: "demo-2",
    nombre: "Kit Smart Home",
    descripcion: "Sensores, focos inteligentes y control central para automatizar el hogar.",
    categoriaId: "hogar",
    precio: 259.5,
    stock: 18,
    imagenUrl: "https://images.unsplash.com/photo-1558002038-1055907df827?auto=format&fit=crop&w=900&q=80"
  },
  {
    _id: "demo-3",
    nombre: "Mochila Urbana Antirrobo",
    descripcion: "Compartimentos seguros, puerto USB y material impermeable.",
    categoriaId: "moda",
    precio: 119.0,
    stock: 52,
    imagenUrl: "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=900&q=80"
  }
];
