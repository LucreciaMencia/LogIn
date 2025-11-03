"use client";

import styles from "../login/page.module.css";
import { useRouter } from "next/navigation";
import useUser from "@/hooks/useUser";
import useHasPermission from "@/hooks/useHasPermission";

export default function AdminDashboard() {
  const router = useRouter();
  const user = useUser();
  const hasPermission = useHasPermission;

  // Seguridad: si no hay usuario, redirigir al login
  if (!user) {
    if (typeof window !== "undefined") {
      router.push("/login");
    }
    return null;
  }

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <h1 className={styles.title}>Panel de Administración</h1>
        <p style={{ marginBottom: "20px" }}>
          Bienvenido <strong>{user.email}</strong><br />
          Rol actual: <strong>{user.rol}</strong>
        </p>

        {/* Botón para gestionar roles (solo si tiene permiso) */}
        {hasPermission("VIEW_ROLES") && (
          <button
            className={styles.secondaryButton}
            onClick={() => router.push("/admin/roles")}
          >
            Gestión de Roles
          </button>
        )}

        {/* Ejemplo de otra función futura */}
        {hasPermission("VIEW_USERS") && (
          <button
            className={styles.secondaryButton}
            onClick={() => router.push("/admin/usuarios")}
          >
            Gestión de Usuarios
          </button>
        )}

        {/* Botón para cerrar sesión */}
        <button
          style={{ marginTop: "30px" }}
          onClick={() => {
            sessionStorage.removeItem("token");
            router.push("/login");
          }}
        >
          Cerrar sesión
        </button>
      </div>
    </div>
  );
}
