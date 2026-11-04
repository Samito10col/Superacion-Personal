import { Ionicons } from '@expo/vector-icons';
import { Tabs } from 'expo-router';
import { colores } from '../../lib/estilos';

export default function PestanasLayout() {
  return (
    <Tabs
      screenOptions={{
        tabBarActiveTintColor: colores.primario,
        tabBarInactiveTintColor: colores.textoSuave,
        headerTitleStyle: { fontWeight: '700' },
      }}
    >
      <Tabs.Screen
        name="index"
        options={{
          title: 'Hoy',
          tabBarIcon: ({ color, size }) => (
            <Ionicons name="checkmark-circle-outline" size={size} color={color as string} />
          ),
        }}
      />
      <Tabs.Screen
        name="grupos"
        options={{
          title: 'Grupos',
          tabBarIcon: ({ color, size }) => (
            <Ionicons name="people-outline" size={size} color={color as string} />
          ),
        }}
      />
      <Tabs.Screen
        name="multas"
        options={{
          title: 'Multas',
          tabBarIcon: ({ color, size }) => (
            <Ionicons name="cash-outline" size={size} color={color as string} />
          ),
        }}
      />
      <Tabs.Screen
        name="perfil"
        options={{
          title: 'Perfil',
          tabBarIcon: ({ color, size }) => (
            <Ionicons name="person-circle-outline" size={size} color={color as string} />
          ),
        }}
      />
    </Tabs>
  );
}