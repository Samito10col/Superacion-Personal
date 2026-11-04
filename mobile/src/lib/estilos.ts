import { StyleSheet } from 'react-native';

// Colores de la app en un solo lugar
export const colores = {
  fondo: '#F2F4F7',
  tarjeta: '#FFFFFF',
  texto: '#101828',
  textoSuave: '#667085',
  borde: '#D0D5DD',
  primario: '#2E90FA',
  error: '#D92D20',
};

// Estilos compartidos por las pantallas de login y registro
export const estilosFormulario = StyleSheet.create({
  contenedor: {
    flexGrow: 1,
    justifyContent: 'center',
    padding: 24,
    backgroundColor: colores.fondo,
  },
  tarjeta: {
    backgroundColor: colores.tarjeta,
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
    color: colores.texto,
    textAlign: 'center',
  },
  subtitulo: {
    fontSize: 15,
    color: colores.textoSuave,
    textAlign: 'center',
    marginBottom: 12,
  },
  campo: {
    borderWidth: 1,
    borderColor: colores.borde,
    borderRadius: 10,
    paddingHorizontal: 14,
    paddingVertical: 12,
    fontSize: 16,
    color: colores.texto,
  },
  error: {
    color: colores.error,
    fontSize: 14,
  },
  boton: {
    backgroundColor: colores.primario,
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
  enlace: {
    color: colores.primario,
    fontSize: 15,
    textAlign: 'center',
    marginTop: 8,
  },
});