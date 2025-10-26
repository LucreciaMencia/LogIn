"use client";

import { useRouter } from "next/navigation";
import styles from "../login/page.module.css";
import useUser from "../../hooks/useUser";

export default function AdminPage() {
  const router = useRouter();
  const user = useUser();

  const logout = () => {
    document.cookie =
      "token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 UTC;";
    sessionStorage.clear();
    router.push("/login");
  };

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <h1 className={styles.title}>Panel de Administrador</h1>

        {user ? (
          <p style={{ marginBottom: "20px" }}>
            Bienvenido: <b>{user.email}</b> ({user.rol})
          </p>
        ) : (
          <p>Cargando usuario...</p>
        )}

        <button className={styles.secondaryButton} onClick={logout}>
          Cerrar Sesión
        </button>
      </div>
    </div>
  );
}
