import * as SecureStore from 'expo-secure-store';
import { Platform } from 'react-native';

// En el celular usamos almacenamiento cifrado; en la web, el del navegador
const esWeb = Platform.OS === 'web';
const hayNavegador = typeof localStorage !== 'undefined';

export async function guardar(clave: string, valor: string): Promise<void> {
  if (esWeb) {
    if (hayNavegador) localStorage.setItem(clave, valor);
    return;
  }
  await SecureStore.setItemAsync(clave, valor);
}

export async function leer(clave: string): Promise<string | null> {
  if (esWeb) {
    return hayNavegador ? localStorage.getItem(clave) : null;
  }
  return SecureStore.getItemAsync(clave);
}

export async function borrar(clave: string): Promise<void> {
  if (esWeb) {
    if (hayNavegador) localStorage.removeItem(clave);
    return;
  }
  await SecureStore.deleteItemAsync(clave);
}