import { Stack } from 'expo-router';
import { AuthProvider, useAuth } from '../lib/auth';

function Navegacion() {
  const { usuario, cargando } = useAuth();

  // Mientras revisa si hay una sesión guardada, no muestra nada
  if (cargando) return null;

  return (
    <Stack>
      {/* Solo con sesión iniciada: la app con pestañas */}
      <Stack.Protected guard={!!usuario}>
        <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
      </Stack.Protected>

      {/* Solo sin sesión */}
      <Stack.Protected guard={!usuario}>
        <Stack.Screen name="login" options={{ headerShown: false }} />
        <Stack.Screen name="registro" options={{ title: 'Crear cuenta' }} />
      </Stack.Protected>
    </Stack>
  );
}

export default function RootLayout() {
  return (
    <AuthProvider>
      <Navegacion />
    </AuthProvider>
  );
}