
"use client";
export function redirigirSegunRol(router: any, rol: string) {
  const rolNormalizado = rol?.toUpperCase?.() ?? "USER";

  switch (rolNormalizado) {
    case "ADMIN":
      router.push("/admin");
      break;
    case "USER":
      router.push("/usuario");
      break;
    case "RESTAURANTE":
      router.push("/crear-comida");
      break;
    default:
      router.push("/dashboard");
  }
}

import styles from "./page.module.css";
import { useState } from "react";
import { useRouter } from "next/navigation";

export default function Login() {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = async (e) => {
    e.preventDefault();

    const response = await fetch("http://localhost:3000/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password })
    });

    const data = await response.json();

// ✅ Aquí va el manejo del token — NO en el return
    if (data.success && data.token) {
    // Cookie para Middleware
      document.cookie = `token=${data.token}; path=/;`;

      // SessionStorage para UI del Frontend
      sessionStorage.setItem("token", data.token);
      sessionStorage.setItem("rol", data.rol ?? "USER");
     sessionStorage.setItem("refreshToken", data.refreshToken ?? "");

  // Redirigir según rol real del backend
      redirigirSegunRol(router, data.rol);

      return;
    }

  alert("Credenciales incorrectas");
  };

return (
  <div className={styles.container}>
    <div className={styles.card}>
      <h1 className={styles.title}>Iniciar Sesión</h1>

      <form className={styles.form} onSubmit={handleLogin}>
        <input
          type="email"
          placeholder="Correo electrónico"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />

        <input
          type="password"
          placeholder="Contraseña"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />

        <button type="submit">
          Ingresar
        </button>
        <button
          type="button"
          onClick={() => router.push("/register")}
          className={styles.secondaryButton}
        >
          Crear cuenta
          </button>
      </form>

    </div>
  </div>
  
);

}
