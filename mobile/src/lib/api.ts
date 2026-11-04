import { leer } from './almacenamiento';

export const API_URL = process.env.EXPO_PUBLIC_API_URL ?? 'http://localhost:8081';

// Error con el mensaje en español que envía el backend
export class ApiError extends Error {
  status: number;
  errores?: Record<string, string>;

  constructor(status: number, mensaje: string, errores?: Record<string, string>) {
    super(mensaje);
    this.status = status;
    this.errores = errores;
  }
}

// Si el token vence o es inválido, la sesión se cierra sola
let alExpirar: (() => void) | null = null;
export function alExpirarSesion(funcion: () => void) {
  alExpirar = funcion;
}

type Opciones = {
  method?: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
  body?: unknown;
};

export async function api<T>(ruta: string, opciones: Opciones = {}): Promise<T> {
  const token = await leer('token');
  const esFormulario = typeof FormData !== 'undefined' && opciones.body instanceof FormData;

  let cuerpo: BodyInit | undefined;
  if (opciones.body !== undefined) {
    cuerpo = esFormulario ? (opciones.body as FormData) : JSON.stringify(opciones.body);
  }

  let respuesta: Response;
  try {
    respuesta = await fetch(`${API_URL}${ruta}`, {
      method: opciones.method ?? 'GET',
      headers: {
        // Con formularios, el celular pone el tipo de contenido por su cuenta
        ...(esFormulario ? {} : { 'Content-Type': 'application/json' }),
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      body: cuerpo,
    });
  } catch {
    throw new ApiError(0, 'No se pudo conectar con el servidor. Revisa tu conexión.');
  }

  const texto = await respuesta.text();
  const datos = texto ? JSON.parse(texto) : undefined;

  if (!respuesta.ok) {
    if (respuesta.status === 401 && token && alExpirar) {
      alExpirar();
    }
    throw new ApiError(respuesta.status, datos?.detail ?? 'Ocurrió un error inesperado', datos?.errores);
  }

  return datos as T;
}