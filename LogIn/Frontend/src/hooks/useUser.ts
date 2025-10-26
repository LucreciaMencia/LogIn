"use client";

import { jwtDecode } from "jwt-decode";

interface TokenData {
  email: string;
  rol: string;
  exp: number;
}

export default function useUser() {
  if (typeof window === "undefined") return null;

  const token = sessionStorage.getItem("token");
  if (!token) return null;

  try {
    const data: TokenData = jwtDecode(token);
    return data;
  } catch (error) {
    console.error("Error decodificando el token", error);
    return null;
  }
}
