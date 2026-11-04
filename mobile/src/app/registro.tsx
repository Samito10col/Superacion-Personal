import { Link } from 'expo-router';
import { useState } from 'react';
import {
    ActivityIndicator,
    KeyboardAvoidingView,
    Platform,
    Pressable,
    ScrollView,
    Text,
    TextInput,
    View,
} from 'react-native';
import { ApiError } from '../lib/api';
import { useAuth } from '../lib/auth';
import { estilosFormulario as e } from '../lib/estilos';

export default function Registro() {
  const { registrarse } = useAuth();
  const [nombre, setNombre] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmacion, setConfirmacion] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [enviando, setEnviando] = useState(false);

  // Revisiones rápidas antes de molestar al servidor
  function validar(): string | null {
    if (!nombre.trim()) return 'Escribe tu nombre';
    if (!email.trim()) return 'Escribe tu email';
    if (password.length < 8) return 'La contraseña debe tener al menos 8 caracteres';
    if (password !== confirmacion) return 'Las contraseñas no coinciden';
    return null;
  }

  async function crearCuenta() {
    const problema = validar();
    if (problema) {
      setError(problema);
      return;
    }
    setError(null);
    setEnviando(true);
    try {
      await registrarse(nombre.trim(), email.trim(), password);
    } catch (err) {
      if (err instanceof ApiError) {
        // Si el backend indica qué campo falló, mostramos ese mensaje
        const primerError = err.errores ? Object.values(err.errores)[0] : null;
        setError(primerError ?? err.message);
      } else {
        setError('Ocurrió un error inesperado');
      }
    } finally {
      setEnviando(false);
    }
  }

  return (
    <KeyboardAvoidingView style={{ flex: 1 }} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
      <ScrollView contentContainerStyle={e.contenedor} keyboardShouldPersistTaps="handled">
        <View style={e.tarjeta}>
          <Text style={e.titulo}>Crea tu cuenta</Text>
          <Text style={e.subtitulo}>Únete a tu grupo y empieza a cumplir</Text>

          <TextInput
            style={e.campo}
            placeholder="Nombre"
            autoComplete="name"
            value={nombre}
            onChangeText={setNombre}
          />
          <TextInput
            style={e.campo}
            placeholder="Email"
            autoCapitalize="none"
            autoComplete="email"
            keyboardType="email-address"
            value={email}
            onChangeText={setEmail}
          />
          <TextInput
            style={e.campo}
            placeholder="Contraseña (mínimo 8 caracteres)"
            secureTextEntry
            value={password}
            onChangeText={setPassword}
          />
          <TextInput
            style={e.campo}
            placeholder="Repite la contraseña"
            secureTextEntry
            value={confirmacion}
            onChangeText={setConfirmacion}
            onSubmitEditing={crearCuenta}
          />

          {error && <Text style={e.error}>{error}</Text>}

          <Pressable
            style={({ pressed }) => [e.boton, (pressed || enviando) && e.botonPresionado]}
            onPress={crearCuenta}
            disabled={enviando}
          >
            {enviando ? <ActivityIndicator color="#fff" /> : <Text style={e.textoBoton}>Crear cuenta</Text>}
          </Pressable>

          <Link href="/login" style={e.enlace}>
            ¿Ya tienes cuenta? Inicia sesión
          </Link>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}