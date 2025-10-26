export function redirigirSegunRol(router: any, rol: string) {
  const rolNormalizado = rol?.toUpperCase?.() ?? "USER";

  switch (rolNormalizado) {
    case "ADMIN":
      router.push("/admin");
      break;
    case "USER":
      router.push("/usuario");
      break;
    case "RESTAURANTE":
      router.push("/crear-comida");
      break;
    default:
      router.push("/dashboard");
  }
}
