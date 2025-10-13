# 🚀 Backend: API de Login (Spring Boot + MariaDB)

Este repositorio contiene la API de inicio de sesión desarrollada con Java 21 y Spring Boot 3.5.6.

## 💾 1. Configuración de la Base de Datos (MariaDB)

La aplicación requiere una base de datos llamada `db_login` con la tabla `users`. Hemos proporcionado un script de Python para automatizar esta configuración.

### Requisitos Previos

1.  **MariaDB / MySQL:** Asegúrese de que el servidor esté instalado y corriendo en `localhost:3306`.
2.  **Python 3:** Instalado y accesible.
3.  **Librería de Python:** Instale el conector de MariaDB/MySQL para Python:
    ```bash
    pip install mysql-connector-python
    ```

### Uso del Script de Configuración

El script `setup_db.py` crea la base de datos, la tabla y el usuario de la aplicación.

1.  **Editar Credenciales:**
    Abra `setup_db.py` y modifique la línea `DB_PASSWORD` con la **contraseña del usuario root** de su instalación de MariaDB:
    ```python
    DB_PASSWORD = "TU_PASSWORD_ROOT_AQUI" 
    ```

2.  **Ejecutar el Script:**
    Ejecute el script en su terminal:
    ```bash
    python setup_db.py
    ```
    Si la ejecución es exitosa, verá mensajes de confirmación de que la base de datos y la tabla `users` han sido creadas.

### Credenciales de la Aplicación

El script configura el usuario `lucioamarilla` con contraseña `1234` para la base de datos `db_login`, tal como se especifica en el archivo `application.properties`:

| Propiedad | Valor |
| :--- | :--- |
| `spring.datasource.username` | `lucioamarilla` |
| `spring.datasource.password` | `1234` |
| `spring.datasource.url` | `jdbc:mariadb://localhost:3306/db_login` |

---

## 💻 2. Ejecución del Servidor Spring Boot

Una vez que la base de datos esté lista, puede iniciar la aplicación de Spring Boot.

1.  **Construir y Ejecutar:**
    Abra la terminal en la raíz del proyecto y use el wrapper de Maven:
    ```bash
    ./mvnw spring-boot:run
    ```
2.  **Verificación:**
    El servidor se iniciará en `http://localhost:3000`.

---

## 🎯 3. Prueba del Endpoint de Login

El endpoint de login espera una petición `POST` en la ruta `/login`.

### Petición

* **Método:** `POST`
* **URL:** `http://localhost:3000/login`
* **Body (JSON):**
    ```json
    {
      "email": "test@ejemplo.com", 
      "password": "password123" 
    }
    ```

### Respuestas Esperadas

| Escenario | Status HTTP | Cuerpo de Respuesta |
| :--- | :--- | :--- |
| **Login Exitoso** | `200 OK` | `{"success": true, "message": "Login correcto"}` |
| **Login Fallido** | `200 OK` | `{"success": false, "message": "Usuario o contraseña incorrectos"}` |
