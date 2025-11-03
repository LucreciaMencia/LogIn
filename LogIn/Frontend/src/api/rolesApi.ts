const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:3000";

export async function getRoles() {
  const response = await fetch(`${API_URL}/roles`);
  return await response.json();
}

export async function getPermisos() {
  const response = await fetch(`${API_URL}/permisos`);
  return await response.json();
}

export async function createRole(data: any) {
  const response = await fetch(`${API_URL}/roles`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  return await response.json();
}

export async function updateRole(id: string, data: any) {
  const response = await fetch(`${API_URL}/roles/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  return await response.json();
}

export async function deleteRole(id: string) {
  const response = await fetch(`${API_URL}/roles/${id}`, {
    method: "DELETE",
  });
  return await response.json();
}
