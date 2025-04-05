<template id="app-frame">
  <v-app id="inspire">
    <v-navigation-drawer v-model="drawer" width="280">
      <v-sheet class="pa-4" color="grey-lighten-4">
        <v-avatar :image="userImg" class="mb-8" color="grey-darken-1" size="64"></v-avatar>
        <div>Gerente de Ventas</div>
      </v-sheet>

      <v-divider></v-divider>

      <v-list v-model:opened="open">
        <!-- Nivel 1: Opciones principales -->
        <v-list-item prepend-icon="mdi-form-select" title="Formulario" to="/form"></v-list-item>
        <v-list-item prepend-icon="mdi-account-tie" title="Empleados" to="/empleados"></v-list-item>

        <!-- Nivel 1: Personas (con submenús) -->
        <v-list-group value="Personas">
          <template v-slot:activator="{ props }">
            <v-list-item v-bind="props" prepend-icon="mdi-account-group" title="Personas"></v-list-item>
          </template>

          <!-- Nivel 2: Clientes -->
          <v-list-group value="Clientes">
            <template v-slot:activator="{ props }">
              <v-list-item v-bind="props" prepend-icon="mdi-account" title="Clientes"></v-list-item>
            </template>

            <!-- Nivel 3: Persona y Empresa -->
            <v-list-item prepend-icon="mdi-human" title="Persona" to="/clientes/persona"></v-list-item>
            <v-list-item prepend-icon="mdi-domain" title="Empresa" to="/clientes/empresa"></v-list-item>
          </v-list-group>

          <!-- Nivel 2: Proveedores -->
          <v-list-item prepend-icon="mdi-truck" title="Proveedores" to="/proveedores"></v-list-item>
        </v-list-group>
      </v-list>
    </v-navigation-drawer>

    <v-app-bar>
      <v-app-bar-nav-icon @click="drawer = !drawer"></v-app-bar-nav-icon>
      <v-app-bar-title>La Casa De La Tortilla</v-app-bar-title>
    </v-app-bar>

    <v-main>
      <v-container fluid>
        <slot></slot>
      </v-container>
    </v-main>

    <v-footer>La casa de la tortilla, 2025</v-footer>
  </v-app>
</template>

<script>
app.component("app-frame", {
  template: "#app-frame",
  data: () => ({
    drawer: null,
    userImg: 'images/avatar.jpg',
    open: [],
    menu: [
      { icon: 'mdi-form-select', text: 'Formulario', href: '/form' },
      { icon: 'mdi-account-tie', text: 'Empleados', href: '/empleados' },
      {
        icon: 'mdi-account-group',
        text: 'Personas',
        submenu: [
          {
            text: 'Clientes',
            icon: 'mdi-account',
            submenu: [
              { icon: 'mdi-human', text: 'Persona', href: '/clientes/persona' },
              { icon: 'mdi-domain', text: 'Empresa', href: '/clientes/empresa' }
            ]
          },
          { icon: 'mdi-truck', text: 'Proveedores', href: '/proveedores' }
        ]
      }
    ]
  }),
});
</script>
