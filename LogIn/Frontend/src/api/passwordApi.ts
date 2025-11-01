// src/api/passwordApi.ts
export async function requestPasswordReset(email: string) {
  try {
    const response = await fetch("http://localhost:3000/forgot-password", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email }),
    });

    const data = await response.json();
    return { success: response.ok, message: data.message || "Solicitud enviada" };
  } catch (error) {
    console.error("Error en requestPasswordReset:", error);
    return { success: false, message: "Error de red. Intente más tarde." };
  }
}

export async function resetPassword(token: string, newPassword: string) {
  try {
    const response = await fetch("http://localhost:3000/reset-password", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ token, newPassword }),
    });

    const data = await response.json();
    return { success: response.ok, message: data.message || "Contraseña actualizada" };
  } catch (error) {
    console.error("Error en resetPassword:", error);
    return { success: false, message: "Error de red. Intente más tarde." };
  }
}
