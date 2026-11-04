import { Pressable, StyleSheet, Text, View } from 'react-native';
import { useAuth } from '../lib/auth';

export default function Inicio() {
  const { usuario, cerrarSesion } = useAuth();

  return (
    <View style={estilos.contenedor}>
      <Text style={estilos.saludo}>¡Hola, {usuario?.nombre}! 👋</Text>
      <Text style={estilos.detalle}>Sesión iniciada como {usuario?.email}</Text>

      <Pressable style={estilos.boton} onPress={cerrarSesion}>
        <Text style={estilos.textoBoton}>Cerrar sesión</Text>
      </Pressable>
    </View>
  );
}

const estilos = StyleSheet.create({
  contenedor: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 24,
    gap: 8,
    backgroundColor: '#F2F4F7',
  },
  saludo: {
    fontSize: 24,
    fontWeight: '700',
    color: '#101828',
  },
  detalle: {
    fontSize: 15,
    color: '#667085',
    marginBottom: 16,
  },
  boton: {
    borderWidth: 1,
    borderColor: '#D92D20',
    borderRadius: 10,
    paddingVertical: 12,
    paddingHorizontal: 24,
  },
  textoBoton: {
    color: '#D92D20',
    fontSize: 16,
    fontWeight: '600',
  },
});