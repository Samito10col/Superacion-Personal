import { useState } from 'react';
import {
    ActivityIndicator,
    KeyboardAvoidingView,
    Platform,
    Pressable,
    StyleSheet,
    Text,
    TextInput,
    View,
} from 'react-native';
import { ApiError } from '../lib/api';
import { useAuth } from '../lib/auth';

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
    } catch (e) {
      setError(e instanceof ApiError ? e.message : 'Ocurrió un error inesperado');
    } finally {
      setEnviando(false);
    }
  }

  return (
    <KeyboardAvoidingView
      style={estilos.contenedor}
      behavior={Platform.OS === 'ios' ? 'padding' : undefined}
    >
      <View style={estilos.tarjeta}>
        <Text style={estilos.titulo}>Superación Personal</Text>
        <Text style={estilos.subtitulo}>Cumple tus hábitos… o paga la multa 💸</Text>

        <TextInput
          style={estilos.campo}
          placeholder="Email"
          autoCapitalize="none"
          autoComplete="email"
          keyboardType="email-address"
          value={email}
          onChangeText={setEmail}
        />
        <TextInput
          style={estilos.campo}
          placeholder="Contraseña"
          secureTextEntry
          value={password}
          onChangeText={setPassword}
          onSubmitEditing={entrar}
        />

        {error && <Text style={estilos.error}>{error}</Text>}

        <Pressable
          style={({ pressed }) => [estilos.boton, (pressed || enviando) && estilos.botonPresionado]}
          onPress={entrar}
          disabled={enviando}
        >
          {enviando ? (
            <ActivityIndicator color="#fff" />
          ) : (
            <Text style={estilos.textoBoton}>Iniciar sesión</Text>
          )}
        </Pressable>
      </View>
    </KeyboardAvoidingView>
  );
}

const estilos = StyleSheet.create({
  contenedor: {
    flex: 1,
    justifyContent: 'center',
    padding: 24,
    backgroundColor: '#F2F4F7',
  },
  tarjeta: {
    backgroundColor: '#FFFFFF',
    borderRadius: 16,
    padding: 24,
    gap: 12,
    width: '100%',
    maxWidth: 420,
    alignSelf: 'center',
  },
  titulo: {
    fontSize: 26,
    fontWeight: '700',
    color: '#101828',
    textAlign: 'center',
  },
  subtitulo: {
    fontSize: 15,
    color: '#667085',
    textAlign: 'center',
    marginBottom: 12,
  },
  campo: {
    borderWidth: 1,
    borderColor: '#D0D5DD',
    borderRadius: 10,
    paddingHorizontal: 14,
    paddingVertical: 12,
    fontSize: 16,
    color: '#101828',
  },
  error: {
    color: '#D92D20',
    fontSize: 14,
  },
  boton: {
    backgroundColor: '#2E90FA',
    borderRadius: 10,
    paddingVertical: 14,
    alignItems: 'center',
    marginTop: 4,
  },
  botonPresionado: {
    opacity: 0.7,
  },
  textoBoton: {
    color: '#FFFFFF',
    fontSize: 16,
    fontWeight: '600',
  },
});