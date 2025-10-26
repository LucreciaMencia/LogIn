
"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

interface Usuario {
  nombre?: string;
  rol?: string;
}

export default function Dashboard() {
  const router = useRouter();
  const [usuario, setUsuario] = useState<Usuario | null>(null);

  useEffect(() => {
    const token = document.cookie
      .split("; ")
      .find((row) => row.startsWith("token="))
      ?.split("=")[1];

    if (!token) {
      router.push("/login");
      return;
    }

    // TEMPORAL: Datos simulados mientras backend manda datos reales
    const rol = sessionStorage.getItem("rol") ?? "USER";
    setUsuario({
      nombre: "Usuario Genérico",
      rol: rol ?? "Sin rol definido"
    });
  }, []);

  const logout = () => {
    document.cookie =
      "token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 UTC;";
    document.cookie =
      "role=; path=/; expires=Thu, 01 Jan 1970 00:00:00 UTC;";
    router.push("/login");
  };

  if (!usuario) return <p>Cargando...</p>;

  return (
    <div style={{ padding: "40px" }}>
      <h1>Bienvenido, {usuario.nombre}</h1>
      <h3>Rol asignado: {usuario.rol}</h3>

      <hr style={{ margin: "20px 0" }} />

      <p>✅ Acceso general al sistema habilitado</p>
      <p>ℹ️ Funcionalidades específicas del rol serán activadas más adelante</p>

      <br />

      <button onClick={logout}>
        Cerrar Sesión
      </button>
    </div>
  );
}
