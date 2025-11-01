"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import styles from "../login/page.module.css";
import { requestPasswordReset } from "../../api/passwordApi";

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState("");
  const [mensaje, setMensaje] = useState<string | null>(null);
  const [enviando, setEnviando] = useState(false);
  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMensaje(null);

    if (!email) {
      setMensaje("Por favor ingrese su correo electrónico.");
      return;
    }

    // Validación simple de email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      setMensaje("Ingrese un correo electrónico válido.");
      return;
    }

    setEnviando(true);
    const result = await requestPasswordReset(email);
    setEnviando(false);

    setMensaje(result.message);
  };

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <h1 className={styles.title}>Recuperar contraseña</h1>

        <form className={styles.form} onSubmit={handleSubmit}>
          <input
            type="email"
            placeholder="Correo electrónico"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            autoComplete="email"
            required
          />

          <button type="submit" disabled={enviando}>
            {enviando ? "Enviando..." : "Solicitar enlace"}
          </button>
        </form>

        {mensaje && (
          <p style={{ marginTop: 16, color: "#330f21", fontWeight: 500 }}>
            {mensaje}
          </p>
        )}

        <button
          className={styles.secondaryButton}
          style={{ marginTop: 16 }}
          onClick={() => router.push("/login")}
        >
          Volver al Login
        </button>
      </div>
    </div>
  );
}
