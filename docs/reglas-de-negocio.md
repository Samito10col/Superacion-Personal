A. Grupos y roles
RN-01: Cada grupo tiene al menos un administrador. Si el último admin sale, el sistema exige asignar otro antes.
RN-02: Solo un admin puede crear o editar hábitos, invitar o expulsar miembros, anular multas y confirmar pagos.
RN-03: Un usuario puede pertenecer a varios grupos. Sus obligaciones y multas son independientes en cada grupo.
B. Hábitos y periodos
RN-04: Cada hábito tiene una frecuencia (diaria, semanal o mensual) y una meta por periodo (ejemplo: 3 veces por semana).
RN-05: La semana va de lunes a domingo y se usa la zona horaria de Colombia (America/Bogota).
RN-06: Cualquier cambio en un hábito (meta, valor de multa o modo de verificación) rige desde el siguiente periodo. Nunca es retroactivo.
RN-07: Un miembro nuevo empieza a tener obligaciones desde el siguiente periodo completo.
C. Registro de cumplimiento
RN-08: Solo se puede registrar el día actual. Se permite un registro tardío hasta 24 horas después, configurable por grupo.
RN-09: Para hábitos diarios hay máximo un registro por día. Para semanales o mensuales hay máximo uno por día, hasta cumplir la meta.
RN-10: Nadie puede validar su propio registro.
RN-11: Un rechazo exige un motivo escrito.
RN-12: Si nadie valida en 48 horas, el registro se aprueba automáticamente.
RN-13: El usuario puede apelar un rechazo una sola vez, y un admin decide de forma definitiva.
D. Multas
RN-14: El cierre de un periodo se ejecuta después de vencer el plazo de validación (fin del periodo + 48 h). Así no se cobra una multa por un registro que todavía estaba pendiente.
RN-15: Cada hábito tiene un tipo de multa:
fija: si no cumple la meta, paga el valor completo.
por_falta: paga el valor por cada vez que le faltó (meta 3, cumplió 1, paga 2 × valor).
RN-16: Los montos van en pesos colombianos, en valores enteros. El valor queda congelado en la multa al momento de generarse.
RN-17: El usuario puede pedir una exención con justificación (enfermedad, viaje). Si el admin la aprueba, la multa queda en estado anulada.
RN-18: Un usuario puede pausar un hábito avisando con anticipación (por ejemplo, vacaciones). Los periodos pausados no generan multa.
RN-19: Ninguna multa se borra. Solo cambia de estado (pendiente, pagada, anulada) y cada cambio queda en un registro de auditoría.
E. Pagos y salida del grupo
RN-20: Se permiten pagos parciales. La multa pasa a pagada cuando la suma de pagos alcanza el monto.
RN-21: El deudor registra el pago y un admin lo confirma. Hasta entonces no cuenta.
RN-22: Los pagos confirmados alimentan el fondo común del grupo, que es visible para todos los miembros.
RN-23: Quien sale o es expulsado de un grupo conserva sus deudas registradas. El admin puede condonarlas, con motivo.