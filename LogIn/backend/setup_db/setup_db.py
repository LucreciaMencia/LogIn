# setup_db.py
import mysql.connector
from mysql.connector import errorcode

# --- CONFIGURACIÓN DE CONEXIÓN AL SERVIDOR MARIA DB ---
# Usamos las credenciales de un usuario administrador (como root)
# para tener permisos para crear la base de datos.
# **¡Asegúrate de que estas credenciales sean válidas en tu MariaDB!**
DB_HOST = "localhost"
DB_USER = "root"  # Usualmente se usa root para tareas administrativas
DB_PASSWORD = "TU_PASSWORD_ROOT_AQUI" # CAMBIA por tu contraseña real de 'root'
DB_NAME = "db_login"

# --- CONFIGURACIÓN DEL USUARIO DE LA APLICACIÓN (Spring Boot) ---
# Usuario y contraseña que usará Spring Boot (según application.properties)
APP_USER = "lucioamarilla"
APP_PASSWORD = "1234" 

# --- SCRIPT SQL ---
# Define la estructura y el usuario inicial
TABLES = {}
TABLES['users'] = (
    "CREATE TABLE `users` ("
    "  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,"
    "  `email` VARCHAR(255) NOT NULL UNIQUE,"
    "  `password` VARCHAR(255) NOT NULL"
    ") ENGINE=InnoDB")

INSERT_USER = (
    "INSERT INTO `users` (`email`, `password`) VALUES (%s, %s)"
)

def create_database(cursor):
    """Intenta crear la base de datos."""
    try:
        cursor.execute(f"CREATE DATABASE {DB_NAME} DEFAULT CHARACTER SET 'utf8'")
        print(f"✅ Base de datos '{DB_NAME}' creada exitosamente.")
    except mysql.connector.Error as err:
        if err.errno == errorcode.ER_DB_CREATE_EXISTS:
            print(f"⚠️ Base de datos '{DB_NAME}' ya existe.")
        else:
            print(f"❌ Error al crear la base de datos: {err}")
            exit(1)

def setup_database():
    """Conecta al servidor y ejecuta la configuración."""
    try:
        # Conexión al servidor (sin especificar DB)
        cnx = mysql.connector.connect(user=DB_USER, 
                                      password=DB_PASSWORD,
                                      host=DB_HOST)
        cursor = cnx.cursor()

        # 1. Crear la base de datos
        create_database(cursor)

        # 2. Conectarse a la nueva base de datos
        cursor.execute(f"USE {DB_NAME}")
        
        # 3. Crear la tabla de usuarios
        print("\n⏳ Creando tablas...")
        try:
            cursor.execute(TABLES['users'])
            print("✅ Tabla 'users' creada exitosamente.")
        except mysql.connector.Error as err:
            if err.errno == errorcode.ER_TABLE_EXISTS_ERROR:
                print("⚠️ Tabla 'users' ya existe, saltando creación.")
            else:
                print(f"❌ Error al crear la tabla: {err}")
                
        # 4. Asignar/Crear usuario de la aplicación y darle permisos (Opcional, pero recomendado)
        # Nota: En tu caso, tu usuario de Spring Boot es 'lucioamarilla'. 
        # Si este usuario ya existe globalmente, puedes saltar esta parte. 
        # Si no existe, debes crearlo y darle permisos a esta DB.
        try:
            print(f"\n⏳ Dando permisos al usuario '{APP_USER}'...")
            
            # Comando para crear el usuario si no existe y asignarle la contraseña
            create_user_sql = (f"CREATE USER IF NOT EXISTS '{APP_USER}'@'{DB_HOST}' IDENTIFIED BY '{APP_PASSWORD}'")
            cursor.execute(create_user_sql)
            
            # Comando para darle todos los permisos a la DB 'db_login'
            grant_sql = (f"GRANT ALL PRIVILEGES ON {DB_NAME}.* TO '{APP_USER}'@'{DB_HOST}'")
            cursor.execute(grant_sql)
            
            cursor.execute("FLUSH PRIVILEGES")
            print(f"✅ Usuario '{APP_USER}' configurado y con permisos en '{DB_NAME}'.")
            
        except mysql.connector.Error as err:
            print(f"❌ Error al configurar el usuario: {err}")


        # 5. Insertar usuario de prueba
        print("\n⏳ Insertando usuario de prueba...")
        TEST_EMAIL = "test@ejemplo.com"
        TEST_PASS = "password123"
        try:
            # Primero verifica si ya existe para evitar duplicados
            cursor.execute("SELECT COUNT(*) FROM users WHERE email = %s", (TEST_EMAIL,))
            if cursor.fetchone()[0] == 0:
                cursor.execute(INSERT_USER, (TEST_EMAIL, TEST_PASS))
                cnx.commit()
                print(f"✅ Usuario de prueba ({TEST_EMAIL}) insertado. Contraseña: {TEST_PASS}")
            else:
                print(f"⚠️ Usuario de prueba ({TEST_EMAIL}) ya existe, saltando inserción.")
                
        except mysql.connector.Error as err:
            print(f"❌ Error al insertar usuario de prueba: {err}")

        # 6. Cerrar conexión
        cursor.close()
        cnx.close()
        print("\n✨ Configuración de la base de datos completada.")

    except mysql.connector.Error as err:
        if err.errno == errorcode.ER_ACCESS_DENIED_ERROR:
            print("❌ Error de autenticación. Verifica DB_USER y DB_PASSWORD (root) en el script.")
        elif err.errno == errorcode.CR_CONN_HOST_ERROR:
            print("❌ No se pudo conectar a MariaDB. Asegúrate de que el servidor está corriendo en localhost:3306.")
        else:
            print(f"❌ Error desconocido: {err}")

if __name__ == '__main__':
    setup_database()
