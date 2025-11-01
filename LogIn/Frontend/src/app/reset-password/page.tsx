"use client";

import { useState } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import styles from "../login/page.module.css";
import { resetPassword } from "../../api/passwordApi";

export default function ResetPasswordPage() {
  const searchParams = useSearchParams();
  const token = searchParams.get("token") ?? "";
  const router = useRouter();

  const [password, setPassword] = useState("");
  const [confirmar, setConfirmar] = useState("");
  const [mensaje, setMensaje] = useState<string | null>(null);
  const [enviando, setEnviando] = useState(false);

  const validarPassword = (p: string) => {
    // regla mínima: longitud >= 8 (podés ajustarla)
    return p.length >= 8;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMensaje(null);

    if (!token) {
      setMensaje("Token inválido o ausente.");
      return;
    }

    if (!validarPassword(password)) {
      setMensaje("La contraseña debe tener al menos 8 caracteres.");
      return;
    }

    if (password !== confirmar) {
      setMensaje("Las contraseñas no coinciden.");
      return;
    }

    setEnviando(true);
    const result = await resetPassword(token, password);
    setEnviando(false);

    setMensaje(result.message);

    if (result.success) {
      // Pequeña demora para que usuario lea el mensaje
      setTimeout(() => router.push("/login"), 1800);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <h1 className={styles.title}>Restablecer contraseña</h1>

        <form className={styles.form} onSubmit={handleSubmit}>
          <input
            type="password"
            placeholder="Nueva contraseña"
            autoComplete="new-password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />

          <input
            type="password"
            placeholder="Confirmar contraseña"
            autoComplete="new-password"
            value={confirmar}
            onChange={(e) => setConfirmar(e.target.value)}
            required
          />

          <button type="submit" disabled={enviando}>
            {enviando ? "Restableciendo..." : "Restablecer contraseña"}
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
