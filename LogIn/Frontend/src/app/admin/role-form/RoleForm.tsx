"use client";

import { useEffect, useState } from "react";
import styles from "../../login/page.module.css";
import { getPermisos, createRole, updateRole } from "@/api/rolesApi";

interface RoleFormProps {
  role?: any; // si está presente, es edición
  onSuccess: () => void;
  onCancel: () => void;
}

export default function RoleForm({ role, onSuccess, onCancel }: RoleFormProps) {
  const [nombre, setNombre] = useState(role?.nombre || "");
  const [descripcion, setDescripcion] = useState(role?.descripcion || "");
  const [permisos, setPermisos] = useState<string[]>(role?.permisos || []);
  const [todosPermisos, setTodosPermisos] = useState<any[]>([]);
  const [mensaje, setMensaje] = useState("");

  useEffect(() => {
    async function cargarPermisos() {
      const data = await getPermisos();
      setTodosPermisos(data);
    }
    cargarPermisos();
  }, []);

  const togglePermiso = (perm: string) => {
    setPermisos((prev) =>
      prev.includes(perm) ? prev.filter((p) => p !== perm) : [...prev, perm]
    );
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!nombre.trim()) {
      setMensaje("El nombre del rol es obligatorio");
      return;
    }

    const data = { nombre, descripcion, permisos };

    const result = role
      ? await updateRole(role.id, data)
      : await createRole(data);

    setMensaje(result.message || "Operación completada");
    if (result.success) {
      setTimeout(onSuccess, 1000);
    }
  };

  return (
    <div className={styles.card}>
      <h1 className={styles.title}>
        {role ? "Editar Rol" : "Crear Rol"}
      </h1>

      <form className={styles.form} onSubmit={handleSubmit}>
        <input
          type="text"
          placeholder="Nombre del rol"
          value={nombre}
          onChange={(e) => setNombre(e.target.value)}
        />

        <input
          type="text"
          placeholder="Descripción"
          value={descripcion}
          onChange={(e) => setDescripcion(e.target.value)}
        />

        <div style={{ textAlign: "left", marginTop: "10px" }}>
          <p style={{ fontWeight: 600 }}>Permisos:</p>
          {todosPermisos.map((perm) => (
            <label key={perm.id} style={{ display: "block" }}>
              <input
                type="checkbox"
                checked={permisos.includes(perm.nombre)}
                onChange={() => togglePermiso(perm.nombre)}
              />
              {perm.nombre}
            </label>
          ))}
        </div>

        <button type="submit">
          {role ? "Guardar Cambios" : "Crear Rol"}
        </button>

        {mensaje && (
          <p style={{ marginTop: 10, color: "#330f21" }}>{mensaje}</p>
        )}

        <button
          type="button"
          className={styles.secondaryButton}
          onClick={onCancel}
        >
          Cancelar
        </button>
      </form>
    </div>
  );
}
