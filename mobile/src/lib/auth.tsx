import { createContext, useContext, useEffect, useState, type ReactNode } from 'react';
import { borrar, guardar, leer } from './almacenamiento';
import { alExpirarSesion, api } from './api';

export type Usuario = {
  id: number;
  nombre: string;
  email: string;
  creadoEn: string;
};

type LoginResponse = {
  token: string;
  tipo: string;
  expiraEn: string;
  usuario: Usuario;
};

type Sesion = {
  usuario: Usuario | null;
  cargando: boolean;
  iniciarSesion: (email: string, password: string) => Promise<void>;
  registrarse: (nombre: string, email: string, password: string) => Promise<void>;
  cerrarSesion: () => Promise<void>;
};

const SesionContext = createContext<Sesion | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [usuario, setUsuario] = useState<Usuario | null>(null);
  const [cargando, setCargando] = useState(true);

  async function cerrarSesion() {
    await borrar('token');
    await borrar('usuario');
    setUsuario(null);
  }

  // Al abrir la app, recupera la sesión guardada
  useEffect(() => {
    alExpirarSesion(cerrarSesion);
    (async () => {
      const token = await leer('token');
      const guardado = await leer('usuario');
      if (token && guardado) {
        setUsuario(JSON.parse(guardado));
      }
      setCargando(false);
    })();
  }, []);

  async function iniciarSesion(email: string, password: string) {
    const respuesta = await api<LoginResponse>('/api/auth/login', {
      method: 'POST',
      body: { email, password },
    });
    await guardar('token', respuesta.token);
    await guardar('usuario', JSON.stringify(respuesta.usuario));
    setUsuario(respuesta.usuario);
  }

  // Crea la cuenta y entra de una vez, sin pedir los datos otra vez
  async function registrarse(nombre: string, email: string, password: string) {
    await api('/api/usuarios', {
      method: 'POST',
      body: { nombre, email, password },
    });
    await iniciarSesion(email, password);
  }

  return (
    <SesionContext.Provider value={{ usuario, cargando, iniciarSesion, registrarse, cerrarSesion }}>
      {children}
    </SesionContext.Provider>
  );
}

export function useAuth(): Sesion {
  const sesion = useContext(SesionContext);
  if (!sesion) {
    throw new Error('useAuth debe usarse dentro de AuthProvider');
  }
  return sesion;
}