
import './globals.css';

export const metadata = {
  title: "Sistema Login",
  description: "Login con roles - Next.js"
};

export default function RootLayout({ children }) {
  return (
    <html lang="es">
      <body>{children}</body>
    </html>
  );
}
