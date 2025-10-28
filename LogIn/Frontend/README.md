Se movio el frontend al framework Next.js
Para ejecutar el login se debe abrir  la carpeta en VisualEstudio o similar y ejecutar el comando "npm install", luejo ejecutar "npm run dev -- -p 8000".
Se debe tener instalado Node.js 20.9 o superior https://nodejs.org/es
por ahora se ejecuta en este enlace: http://localhost:8000/register

# ✅ README — Sistema de Login con Roles (Next.js + JWT)

```md
# 🚀 Sistema de Login con Roles — Frontend (Next.js)

Este proyecto forma parte del sistema de autenticación con control de acceso basado en **roles**.  
Permite a los usuarios **iniciar sesión** y **registrarse**, recibiendo un **JWT** desde el backend para proteger rutas privadas dentro del sitio.

---

## ✅ Tecnologías utilizadas

| Tecnología | Uso |
|----------|-----|
| Next.js 14 (App Router) | Frontend con Server/Client Components |
| CSS Modules | Estilos aislados por página |
| JWT Decode | Decodificar token sin consultas extra |
| React Hooks | Manejo de estado y sesión |
| Fetch API | Comunicación con backend |
| SessionStorage + Cookies | Almacenamiento seguro del token |

---

## 📂 Estructura del Proyecto

```

src/
├─ app/
│   ├─ login/               # Página de inicio de sesión
│   ├─ register/            # Página de registro de usuarios
│   ├─ admin/               # Vista protegida para ADMIN
│   ├─ usuario/             # Vista protegida para USER
│   ├─ crear-comida/        # Vista protegida para RESTAURANTE
│   ├─ layout.tsx
│   └─ page.tsx
├─ hooks/
│   └─ useUser.ts           # Decodifica datos del JWT
└─ middleware.ts             # Protección de rutas privadas

````

---

## 🛠️ Instalación

> ⚠️ Se requiere **Node.js 18+**  
> Para instalar dependencias ejecutar:

```sh
npm install
````

---

## ✅ Ejecución en desarrollo

```sh
npm run dev
```

Luego ingresar en el navegador:

👉 [http://localhost:3000/](http://localhost:3000/)

---

## 🔐 Autenticación & JWT

📌 Cuando el usuario inicia sesión exitosamente:

* El backend devuelve:

  * ✅ `token` (JWT)
  * ✅ `rol` del usuario
* Se guarda en sessionStorage y cookie
* El middleware protege rutas privadas leyendo el token

📌 El hook `useUser()` decodifica el token:

```ts
{
  "email": "test@ejemplo.com",
  "rol": "ADMIN",
  "exp": 1730089962
}
```

---

## 🔁 Flujo de Login

1️⃣ Usuario ingresa email + contraseña
2️⃣ Se envía POST a:

```
/login
```

Ejemplo de request:

```json
{
  "email": "test@ejemplo.com",
  "password": "password123"
}
```

3️⃣ Si el login es exitoso:

* Se guarda el **token**
* Se redirige según el rol:

| Rol         | Redirección             |
| ----------- | ----------------------- |
| ADMIN       | `/admin`                |
| USER        | `/usuario`              |
| RESTAURANTE | `/crear-comida`         |
| Otros       | `/dashboard` (fallback) |

---

## 🛑 Protección de Rutas

Archivo:
📌 `middleware.ts`

Bloquea acceso a páginas privadas si:

* No hay token
* El token es inválido o expiró (**pendiente** implementar expiración)

---

## 🧱 Scripts útiles

| Comando         | Descripción                 |
| --------------- | --------------------------- |
| `npm run dev`   | Ejecutar entorno desarrollo |
| `npm run build` | Generar build producción    |
| `npm start`     | Ejecutar build generado     |

---

## ✅ Pendientes futuros

| Prioridad | Funcionalidad                                  |
| :-------: | ---------------------------------------------- |
|  🔥 Alta  | Expiración automática del token                |
|  🔥 Alta  | Toasts para éxito/error en login/registro      |
|  ✅ Media  | Vincular datos reales de usuario desde backend |
|  ✅ Media  | UI de dashboard según permisos                 |
|  🟡 Baja  | Alias `@` configurado correctamente            |




