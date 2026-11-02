# Reglas de negocio (v1)

## A. Grupos y roles
- **RN-01:** Cada grupo tiene al menos un administrador. Si el último admin sale, el sistema exige asignar otro antes.
- **RN-02:** Solo un admin puede crear o editar hábitos, invitar o expulsar miembros, anular multas y confirmar pagos.
- **RN-03:** Un usuario puede pertenecer a varios grupos. Sus obligaciones y multas son independientes en cada grupo.

## B. Hábitos y periodos
- **RN-04:** Cada hábito tiene frecuencia (diaria, semanal o mensual) y una meta por periodo (ej.: 3 veces por semana).
- **RN-05:** La semana va de lunes a domingo, zona horaria America/Bogota.
- **RN-06:** Cambios en un hábito (meta, valor de multa, modo de verificación) rigen desde el siguiente periodo. Nunca son retroactivos.
- **RN-07:** Un miembro nuevo tiene obligaciones desde el siguiente periodo completo.

## C. Registro de cumplimiento
- **RN-08:** Solo se registra el día actual; registro tardío hasta 24 h después (configurable por grupo).
- **RN-09:** Hábitos diarios: máximo un registro por día. Semanales o mensuales: máximo uno por día hasta cumplir la meta.
- **RN-10:** Nadie puede validar su propio registro.
- **RN-11:** Un rechazo exige motivo escrito.
- **RN-12:** Si nadie valida en 48 h, el registro se aprueba automáticamente.
- **RN-13:** El usuario puede apelar un rechazo una sola vez; un admin decide de forma definitiva.

## D. Multas
- **RN-14:** El cierre del periodo se ejecuta después de vencer el plazo de validación (fin del periodo + 48 h).
- **RN-15:** Tipo de multa por hábito: `fija` (no cumple la meta → paga el valor completo) o `por_falta` (paga el valor por cada vez faltante).
- **RN-16:** Montos en pesos colombianos, valores enteros. El valor queda congelado al generarse la multa.
- **RN-17:** El usuario puede pedir exención con justificación (enfermedad, viaje). Si el admin la aprueba, la multa queda `anulada`.
- **RN-18:** Un usuario puede pausar un hábito avisando con anticipación. Los periodos pausados no generan multa.
- **RN-19:** Las multas nunca se borran; solo cambian de estado (`pendiente`, `pagada`, `anulada`) y cada cambio queda auditado.

## E. Pagos y salida del grupo
- **RN-20:** Se permiten pagos parciales. La multa pasa a `pagada` cuando la suma de pagos alcanza el monto.
- **RN-21:** El deudor registra el pago y un admin lo confirma. Hasta entonces no cuenta.
- **RN-22:** Los pagos confirmados alimentan el fondo común del grupo, visible para todos los miembros.
- **RN-23:** Quien sale o es expulsado conserva sus deudas registradas. El admin puede condonarlas, con motivo.