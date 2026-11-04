import { StyleSheet, Text, View } from 'react-native';
import { colores } from '../lib/estilos';

// Pantalla temporal para las secciones que aún no construimos
export function Proximamente({ icono, mensaje }: { icono: string; mensaje: string }) {
  return (
    <View style={estilos.contenedor}>
      <Text style={estilos.icono}>{icono}</Text>
      <Text style={estilos.mensaje}>{mensaje}</Text>
      <Text style={estilos.detalle}>Muy pronto…</Text>
    </View>
  );
}

const estilos = StyleSheet.create({
  contenedor: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 24,
    backgroundColor: colores.fondo,
  },
  icono: {
    fontSize: 48,
    marginBottom: 12,
  },
  mensaje: {
    fontSize: 18,
    fontWeight: '600',
    color: colores.texto,
    textAlign: 'center',
  },
  detalle: {
    fontSize: 15,
    color: colores.textoSuave,
    marginTop: 4,
  },
});