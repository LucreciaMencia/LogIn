"use client";

import { useEffect, useState } from "react";
import styles from "../../login/page.module.css";
import {
  getRoles,
  deleteRole,
} from "@/api/rolesApi";
import RoleForm from "../role-form/RoleForm";
import useHasPermission from "@/hooks/useHasPermission";

export default function RolesPage() {
  const [roles, setRoles] = useState<any[]>([]);
  const [modo, setModo] = useState<"lista" | "crear" | "editar">("lista");
  const [rolSeleccionado, setRolSeleccionado] = useState<any | null>(null);
  const hasPermission = useHasPermission;

  async function cargarRoles() {
    const data = await getRoles();
    setRoles(data);
  }

  useEffect(() => {
    cargarRoles();
  }, []);

  const handleEliminar = async (id: string) => {
    if (confirm("¿Eliminar este rol?")) {
      await deleteRole(id);
      cargarRoles();
    }
  };

  if (modo !== "lista") {
    return (
      <div className={styles.container}>
        <RoleForm
          role={modo === "editar" ? rolSeleccionado : undefined}
          onSuccess={() => {
            setModo("lista");
            cargarRoles();
          }}
          onCancel={() => setModo("lista")}
        />
      </div>
    );
  }

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <h1 className={styles.title}>Gestión de Roles</h1>

        {hasPermission("ADD_ROLE") && (
          <button onClick={() => setModo("crear")}>Agregar Rol</button>
        )}

        <table
          style={{
            width: "100%",
            borderCollapse: "collapse",
            marginTop: 20,
            textAlign: "left",
          }}
        >
          <thead>
            <tr style={{ background: "#f3e1f6" }}>
              <th>Nombre</th>
              <th>Descripción</th>
              <th>Permisos</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {roles.map((rol) => (
              <tr key={rol.id} style={{ borderBottom: "1px solid #ccc" }}>
                <td>{rol.nombre}</td>
                <td>{rol.descripcion}</td>
                <td>{rol.permisos?.join(", ")}</td>
                <td>
                  {hasPermission("EDIT_ROLE") && (
                    <button onClick={() => {
                      setRolSeleccionado(rol);
                      setModo("editar");
                    }}>
                      Editar
                    </button>
                  )}
                  {" "}
                  {hasPermission("DELETE_ROLE") && (
                    <button onClick={() => handleEliminar(rol.id)}>
                      Eliminar
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
