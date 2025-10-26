"use client";

import { useRouter } from "next/navigation";
import styles from "../login/page.module.css";

export default function CrearComidaPage() {
  const router = useRouter();

  const logout = () => {
    document.cookie =
      "token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 UTC;";
    sessionStorage.clear();
    router.push("/login");
  };

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <h1 className={styles.title}>Gestión de Restaurante</h1>
        <p style={{ marginBottom: "20px" }}>
          Bienvenido, creador de Menús
        </p>

        <button className={styles.secondaryButton} onClick={logout}>
          Cerrar Sesión
        </button>
      </div>
    </div>
  );
}
