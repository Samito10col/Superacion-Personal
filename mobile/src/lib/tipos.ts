// La forma de los datos que envía el backend

export type Frecuencia = 'DIARIA' | 'SEMANAL' | 'MENSUAL';
export type TipoMulta = 'FIJA' | 'POR_FALTA';
export type ModoVerificacion = 'AUTODECLARADO' | 'EVIDENCIA' | 'VALIDACION_PAR' | 'EVIDENCIA_Y_VALIDACION';

export type MiHabito = {
  habitoId: number;
  nombre: string;
  grupoId: number;
  grupoNombre: string;
  frecuencia: Frecuencia;
  metaPorPeriodo: number;
  tipoMulta: TipoMulta;
  valorMulta: number;
  modoVerificacion: ModoVerificacion;
  vigenteDesde: string;        // "2026-11-09"
  periodoActualInicio: string;
  periodoActualFin: string;
  cumplidosEnPeriodo: number;
  registradoHoy: boolean;
};