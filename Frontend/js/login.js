document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const submitBtn = document.getElementById('submitBtn');
    const messageDiv = document.getElementById('message');

    // 🌐 URL del backend remoto
    const API_URL = "https://alluringly-uncategorical-kristen.ngrok-free.dev/login";

    loginForm.addEventListener('submit', async function(e) {
        e.preventDefault();

        const email = emailInput.value.trim();
        const password = passwordInput.value;

        if (!email || !password) {
            showMessage("Por favor, complete todos los campos", "error");
            return;
        }

        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span class="spinner"></span>Procesando...';

        try {
            // 🔹 Petición al backend
            const response = await fetch(API_URL, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password })
            });

            if (!response.ok) {
                throw new Error(`Error HTTP: ${response.status}`);
            }

            const data = await response.json();

            // 🔹 Ejemplo de respuesta esperada del backend:
            // {
            //   "success": true,
            //   "nombre": "juan",
            //   "rol": "Gerente",
            //   "permisos": ["LECTURA", "EDICIÓN", "APROBACIÓN"]
            // }

            if (data.success) {
                showMessage(`✅ Bienvenido ${data.nombre} (${data.rol})`, "success");
                
                // Guardamos usuario y permisos en localStorage
                localStorage.setItem("usuarioActual", JSON.stringify({
                    nombre: data.nombre || email,
                    rol: data.rol || "Desconocido"
                }));
                localStorage.setItem("permisos", JSON.stringify(data.permisos || []));

                // Redirigir al dashboard
                setTimeout(() => {
                    window.location.href = "dashboard.html";
                }, 1000);
            } else {
                showMessage("❌ Credenciales incorrectas", "error");
            }

        } catch (error) {
            console.error("Error en la petición:", error);
            showMessage("⚠️ No se pudo conectar con el servidor", "error");
        } finally {
            submitBtn.disabled = false;
            submitBtn.innerHTML = "Iniciar Sesión";
        }
    });

    function showMessage(text, type) {
        messageDiv.textContent = text;
        messageDiv.className = `message ${type}`;
        messageDiv.classList.remove("hidden");
        setTimeout(() => messageDiv.classList.add("hidden"), 4000);
    }
});
