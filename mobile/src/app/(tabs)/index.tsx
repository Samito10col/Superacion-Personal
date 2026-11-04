import { useFocusEffect } from 'expo-router';
import { useCallback, useState } from 'react';
import {
  ActivityIndicator,
  FlatList,
  Pressable,
  RefreshControl,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { api, ApiError } from '../../lib/api';
import { colores } from '../../lib/estilos';
import { formatearFecha, textoMulta, textoPeriodo } from '../../lib/formato';
import type { MiHabito } from '../../lib/tipos';

export default function Hoy() {
  const [habitos, setHabitos] = useState<MiHabito[]>([]);
  const [cargando, setCargando] = useState(true);
  const [refrescando, setRefrescando] = useState(false);
  const [registrando, setRegistrando] = useState<number | null>(null);
  const [aviso, setAviso] = useState<{ texto: string; esError: boolean } | null>(null);

  const cargar = useCallback(async () => {
    try {
      setHabitos(await api<MiHabito[]>('/api/habitos/mios'));
    } catch (e) {
      setAviso({ texto: e instanceof ApiError ? e.message : 'No se pudieron cargar tus hábitos', esError: true });
    } finally {
      setCargando(false);
      setRefrescando(false);
    }
  }, []);

  // Recarga cada vez que entras a esta pestaña
  useFocusEffect(
    useCallback(() => {
      cargar();
    }, [cargar]),
  );

  async function registrar(habito: MiHabito) {
    setRegistrando(habito.habitoId);
    setAviso(null);
    try {
      const formulario = new FormData();
      formulario.append('nota', '');
      await api(`/api/habitos/${habito.habitoId}/registros`, { method: 'POST', body: formulario });

      const texto = habito.modoVerificacion === 'VALIDACION_PAR'
        ? `¡Bien! "${habito.nombre}" quedó registrado. Un compañero debe validarlo.`
        : `¡Bien! "${habito.nombre}" quedó registrado. 💪`;
      setAviso({ texto, esError: false });
      await cargar();
    } catch (e) {
      setAviso({ texto: e instanceof ApiError ? e.message : 'No se pudo registrar', esError: true });
    } finally {
      setRegistrando(null);
    }
  }

  if (cargando) {
    return (
      <View style={estilos.centrado}>
        <ActivityIndicator size="large" color={colores.primario} />
      </View>
    );
  }

  return (
    <FlatList
      style={{ backgroundColor: colores.fondo }}
      contentContainerStyle={estilos.lista}
      data={habitos}
      keyExtractor={(h) => String(h.habitoId)}
      refreshControl={
        <RefreshControl
          refreshing={refrescando}
          onRefresh={() => {
            setRefrescando(true);
            cargar();
          }}
        />
      }
            ListHeaderComponent={
        aviso ? (
          <Text style={[estilos.aviso, aviso.esError ? estilos.avisoError : estilos.avisoExito]}>
            {aviso.texto}
          </Text>
         ) : undefined
      }
      ListEmptyComponent={
        <View style={estilos.vacio}>
          <Text style={estilos.vacioIcono}>🌱</Text>
          <Text style={estilos.vacioTitulo}>Aún no tienes hábitos</Text>
          <Text style={estilos.vacioTexto}>
            Pídele al administrador de tu grupo que te agregue, o crea tu propio grupo.
          </Text>
        </View>
      }
      renderItem={({ item }) => (
        <TarjetaHabito
          habito={item}
          registrando={registrando === item.habitoId}
          onRegistrar={() => registrar(item)}
        />
      )}
    />
  );
}

function TarjetaHabito({
  habito,
  registrando,
  onRegistrar,
}: {
  habito: MiHabito;
  registrando: boolean;
  onRegistrar: () => void;
}) {
  const meta = habito.metaPorPeriodo;
  const cumplidos = Math.min(habito.cumplidosEnPeriodo, meta);
  const porcentaje = (cumplidos / meta) * 100;
  const metaCumplida = habito.cumplidosEnPeriodo >= meta;
  const aunNoEmpieza = habito.vigenteDesde > habito.periodoActualFin;

  let textoBoton = '¡Lo hice!';
  let deshabilitado = registrando;
  if (habito.registradoHoy) {
    textoBoton = '✓ Registrado hoy';
    deshabilitado = true;
  } else if (metaCumplida) {
    textoBoton = '🎉 Meta cumplida';
    deshabilitado = true;
  }

  return (
    <View style={estilos.tarjeta}>
      <Text style={estilos.nombre}>{habito.nombre}</Text>
      <Text style={estilos.grupo}>{habito.grupoNombre}</Text>

      <View style={estilos.filaProgreso}>
        <View style={estilos.barra}>
          <View style={[estilos.relleno, { width: `${porcentaje}%` }, metaCumplida && estilos.rellenoCompleto]} />
        </View>
        <Text style={estilos.progreso}>
          {cumplidos} de {meta} {textoPeriodo(habito.frecuencia)}
        </Text>
      </View>

      <Text style={estilos.detalle}>{textoMulta(habito)}</Text>
      {habito.modoVerificacion === 'VALIDACION_PAR' && (
        <Text style={estilos.detalle}>👀 Un compañero debe validarlo</Text>
      )}
      {aunNoEmpieza && (
        <Text style={estilos.nota}>
          Empieza a contar el {formatearFecha(habito.vigenteDesde)}. Puedes ir practicando.
        </Text>
      )}

      <Pressable
        style={({ pressed }) => [
          estilos.boton,
          deshabilitado && estilos.botonInactivo,
          pressed && !deshabilitado && { opacity: 0.7 },
        ]}
        onPress={onRegistrar}
        disabled={deshabilitado}
      >
        {registrando ? (
          <ActivityIndicator color="#fff" />
        ) : (
          <Text style={[estilos.textoBoton, deshabilitado && estilos.textoBotonInactivo]}>{textoBoton}</Text>
        )}
      </Pressable>
    </View>
  );
}

const estilos = StyleSheet.create({
  centrado: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: colores.fondo,
  },
  lista: {
    padding: 16,
    gap: 12,
    flexGrow: 1,
  },
  aviso: {
    padding: 12,
    borderRadius: 10,
    fontSize: 15,
    overflow: 'hidden',
  },
  avisoExito: {
    backgroundColor: '#ECFDF3',
    color: '#067647',
  },
  avisoError: {
    backgroundColor: '#FEF3F2',
    color: colores.error,
  },
  tarjeta: {
    backgroundColor: colores.tarjeta,
    borderRadius: 16,
    padding: 16,
    gap: 6,
  },
  nombre: {
    fontSize: 18,
    fontWeight: '700',
    color: colores.texto,
  },
  grupo: {
    fontSize: 14,
    color: colores.textoSuave,
    marginBottom: 6,
  },
  filaProgreso: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
  },
  barra: {
    flex: 1,
    height: 8,
    borderRadius: 4,
    backgroundColor: '#EAECF0',
    overflow: 'hidden',
  },
  relleno: {
    height: '100%',
    backgroundColor: colores.primario,
  },
  rellenoCompleto: {
    backgroundColor: '#17B26A',
  },
  progreso: {
    fontSize: 14,
    fontWeight: '600',
    color: colores.texto,
  },
  detalle: {
    fontSize: 14,
    color: colores.textoSuave,
  },
  nota: {
    fontSize: 13,
    color: '#B54708',
    backgroundColor: '#FFFAEB',
    padding: 8,
    borderRadius: 8,
    overflow: 'hidden',
  },
  boton: {
    backgroundColor: colores.primario,
    borderRadius: 10,
    paddingVertical: 12,
    alignItems: 'center',
    marginTop: 8,
  },
  botonInactivo: {
    backgroundColor: '#F2F4F7',
  },
  textoBoton: {
    color: '#FFFFFF',
    fontSize: 16,
    fontWeight: '600',
  },
  textoBotonInactivo: {
    color: colores.textoSuave,
  },
  vacio: {
    alignItems: 'center',
    paddingTop: 80,
    paddingHorizontal: 24,
    gap: 6,
  },
  vacioIcono: {
    fontSize: 48,
  },
  vacioTitulo: {
    fontSize: 18,
    fontWeight: '700',
    color: colores.texto,
  },
  vacioTexto: {
    fontSize: 15,
    color: colores.textoSuave,
    textAlign: 'center',
  },
});