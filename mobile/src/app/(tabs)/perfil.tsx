import { Pressable, StyleSheet, Text, View } from 'react-native';
import { useAuth } from '../../lib/auth';
import { colores } from '../../lib/estilos';

export default function Perfil() {
  const { usuario, cerrarSesion } = useAuth();
  if (!usuario) return null;

  const inicial = usuario.nombre.charAt(0).toUpperCase();
  const miembroDesde = new Date(usuario.creadoEn).toLocaleDateString('es-CO', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  });

  return (
    <View style={estilos.contenedor}>
      <View style={estilos.tarjeta}>
        <View style={estilos.avatar}>
          <Text style={estilos.inicial}>{inicial}</Text>
        </View>
        <Text style={estilos.nombre}>{usuario.nombre}</Text>
        <Text style={estilos.email}>{usuario.email}</Text>
        <Text style={estilos.desde}>Miembro desde el {miembroDesde}</Text>
      </View>

      <Pressable
        style={({ pressed }) => [estilos.boton, pressed && { opacity: 0.7 }]}
        onPress={cerrarSesion}
      >
        <Text style={estilos.textoBoton}>Cerrar sesión</Text>
      </Pressable>
    </View>
  );
}

const estilos = StyleSheet.create({
  contenedor: {
    flex: 1,
    padding: 24,
    gap: 16,
    backgroundColor: colores.fondo,
  },
  tarjeta: {
    backgroundColor: colores.tarjeta,
    borderRadius: 16,
    padding: 24,
    alignItems: 'center',
    gap: 4,
  },
  avatar: {
    width: 80,
    height: 80,
    borderRadius: 40,
    backgroundColor: colores.primario,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 12,
  },
  inicial: {
    color: '#FFFFFF',
    fontSize: 36,
    fontWeight: '700',
  },
  nombre: {
    fontSize: 22,
    fontWeight: '700',
    color: colores.texto,
  },
  email: {
    fontSize: 15,
    color: colores.textoSuave,
  },
  desde: {
    fontSize: 13,
    color: colores.textoSuave,
    marginTop: 8,
  },
  boton: {
    borderWidth: 1,
    borderColor: colores.error,
    borderRadius: 10,
    paddingVertical: 14,
    alignItems: 'center',
  },
  textoBoton: {
    color: colores.error,
    fontSize: 16,
    fontWeight: '600',
  },
});