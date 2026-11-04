import type { Frecuencia, MiHabito } from './tipos';

// 15000 → "$15.000"
export function formatearPesos(valor: number): string {
  return '$' + valor.toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.');
}

// "2026-11-09" → "9 de noviembre"
export function formatearFecha(fechaIso: string): string {
  const [anio, mes, dia] = fechaIso.split('-').map(Number);
  return new Date(anio, mes - 1, dia).toLocaleDateString('es-CO', { day: 'numeric', month: 'long' });
}

const NOMBRE_PERIODO: Record<Frecuencia, string> = {
  DIARIA: 'hoy',
  SEMANAL: 'esta semana',
  MENSUAL: 'este mes',
};

export function textoPeriodo(frecuencia: Frecuencia): string {
  return NOMBRE_PERIODO[frecuencia];
}

export function textoMulta(habito: MiHabito): string {
  const valor = formatearPesos(habito.valorMulta);
  return habito.tipoMulta === 'FIJA'
    ? `Multa de ${valor} si no cumples`
    : `Multa de ${valor} por cada vez que faltes`;
}