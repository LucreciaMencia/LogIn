document.addEventListener('DOMContentLoaded', function() {
    const usuario = JSON.parse(localStorage.getItem("usuarioActual"));
    const permisos = JSON.parse(localStorage.getItem("permisos"));
    const titulo = document.getElementById("tituloDashboard");
    const accionesContainer = document.getElementById("accionesContainer");
    const logoutBtn = document.getElementById("logoutBtn");

    if (!usuario) {
        window.location.href = "index.html";
        return;
    }

    titulo.textContent = `Bienvenido, ${usuario.nombre} (${usuario.rol})`;

    const accionesDisponibles = {
        "LECTURA": "📖 Ver Documentos",
        "EDICIÓN": "✏️ Editar Informes",
        "APROBACIÓN": "✅ Aprobar Solicitudes",
        "DECISIÓN": "⚖️ Tomar Decisiones Estratégicas",
        "CONTROL": "🧭 Supervisar Personal",
        "GESTIÓN_TOTAL": "🛠️ Panel de Administración Total"
    };

    permisos.forEach(permiso => {
        const btn = document.createElement("button");
        btn.className = "btn accion-btn";
        btn.textContent = accionesDisponibles[permiso] || permiso;
        accionesContainer.appendChild(btn);
    });

    logoutBtn.addEventListener('click', () => {
        localStorage.clear();
        window.location.href = "index.html";
    });
});
