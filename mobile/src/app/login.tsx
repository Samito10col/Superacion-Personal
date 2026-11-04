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

export default function Login() {
  const { iniciarSesion } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [enviando, setEnviando] = useState(false);

  async function entrar() {
    if (!email.trim() || !password) {
      setError('Escribe tu email y tu contraseña');
      return;
    }
    setError(null);
    setEnviando(true);
    try {
      await iniciarSesion(email.trim(), password);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Ocurrió un error inesperado');
    } finally {
      setEnviando(false);
    }
  }

  return (
    <KeyboardAvoidingView style={{ flex: 1 }} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
      <ScrollView contentContainerStyle={e.contenedor} keyboardShouldPersistTaps="handled">
        <View style={e.tarjeta}>
          <Text style={e.titulo}>Superación Personal</Text>
          <Text style={e.subtitulo}>Cumple tus hábitos… o paga la multa 💸</Text>

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
            placeholder="Contraseña"
            secureTextEntry
            value={password}
            onChangeText={setPassword}
            onSubmitEditing={entrar}
          />

          {error && <Text style={e.error}>{error}</Text>}

          <Pressable
            style={({ pressed }) => [e.boton, (pressed || enviando) && e.botonPresionado]}
            onPress={entrar}
            disabled={enviando}
          >
            {enviando ? <ActivityIndicator color="#fff" /> : <Text style={e.textoBoton}>Iniciar sesión</Text>}
          </Pressable>

          <Link href="/registro" style={e.enlace}>
            ¿No tienes cuenta? Créala aquí
          </Link>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}