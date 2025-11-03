import useUser from "./useUser";

/**
 * Hook para verificar si el usuario tiene permiso
 * según su rol actual. Los permisos están definidos
 * localmente mientras el backend no los devuelva.
 */
const permisosPorRol: Record<string, string[]> = {
  "Administrador": ["ADD_ROLE", "EDIT_ROLE", "DELETE_ROLE", "VIEW_ROLES"],
  "Gerente": ["VIEW_ROLES"],
  "Supervisor": ["VIEW_ROLES"],
  "Personal": [],
};

export default function useHasPermission(permission: string): boolean {
  const user = useUser();

  // Si no hay usuario autenticado, no tiene permisos
  if (!user || !user.rol) return false;

  // Buscar permisos según su rol
  const permisos = permisosPorRol[user.rol] || [];

  // Verificar si el permiso solicitado está permitido
  return permisos.includes(permission);
}
