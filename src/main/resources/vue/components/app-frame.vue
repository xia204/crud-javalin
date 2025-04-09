<template id="app-frame">
  <v-app id="inspire">
    <v-navigation-drawer v-model="drawer" width="280">
      <v-sheet class="pa-4" color="grey-lighten-4">

        <v-row class="d-flex align-center justify-center">
          <!-- Avatar -->
          <v-col class="text-center" cols="auto">
            <v-avatar :image="userImg" class="mb-8" color="grey-darken-1" size="64"></v-avatar>
          </v-col>
          
          <!-- Información de nombre y perfil -->
          <v-col class="text-center" cols="auto">
            <v-list-item-content>
              <v-list-item-title class="text-h6">{{ $javalin.state.userInfo.nombreUsuario }}</v-list-item-title>
              <v-list-item-subtitle class="text-lowercase">{{ $javalin.state.userInfo.perfil }}</v-list-item-subtitle>
            </v-list-item-content>
          </v-col>
        </v-row>
      </v-sheet>

      <v-divider></v-divider>

      <v-list>
        <template v-for="item in filteredMenu">
          <v-list-item v-if="!item.submenu" :key="item.text" :prepend-icon="item.icon" :title="item.text"
            :href="item.href"></v-list-item>

          <v-list-group v-else :key="item.text + '-group'" :value="item.text">
            <template v-slot:activator="{ props }">
              <v-list-item v-bind="props" :prepend-icon="item.icon" :title="item.text" />
            </template>

            <!-- Subniveles -->
            <template v-for="sub in item.submenu">
              <v-list-group v-if="sub.submenu" :key="sub.text" :value="sub.text">
                <template v-slot:activator="{ props }">
                  <v-list-item v-bind="props" :prepend-icon="sub.icon" :title="sub.text" />
                </template>

                <v-list-item v-for="final in sub.submenu" :key="final.text" :prepend-icon="final.icon"
                  :title="final.text" :href="final.href" />
              </v-list-group>

              <v-list-item v-else :key="sub.text + '-else'" :prepend-icon="sub.icon" :title="sub.text" :href="sub.href" />
            </template>
          </v-list-group>
        </template>
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
    menu: []
  }),
  computed: {
    // Filtrar el menú según el perfil del usuario
    filteredMenu() {
      const perfil = this.$javalin?.state?.userInfo?.perfil;

      // Filtra los items del menú según el perfil
      return this.menu.filter(item => {
        if (!item.perfiles) return false;  // Si no tiene perfiles definidos, es accesible a todos
        return item.perfiles.includes(perfil);
      });
    }
  },
  created() {

    // Construcción dinámica del menú
    this.menu = [
      // Menú accesible por ADMINISTRADOR o GERENTE
      {
        icon: 'mdi-form-select',
        text: 'Formulario',
        href: '/form',
        perfiles: ['GERENTE']  // Solo accesible para ADMINISTRADOR y GERENTE
      },
      {
        icon: 'mdi-account-tie',
        text: 'Empleados',
        href: '/empleados',
        perfiles: ['ADMINISTRADOR']  // Solo ADMINISTRADOR puede ver este menú
      },
      {
        icon: 'mdi-cash-register',
        text: 'Ventas',
        href: '/ventas',
        perfiles: ['ADMINISTRADOR', 'GERENTE']  // Solo ADMINISTRADOR puede ver este menú
      },
      {
        icon: 'mdi-cash-register',
        text: 'Compras',
        href: '/compras',
        perfiles: ['ADMINISTRADOR', 'GERENTE']  // Solo ADMINISTRADOR puede ver este menú
      },
      // Menú de 'Personas' para todos los perfiles
      {
        icon: 'mdi-account-group',
        text: 'Personas',
        perfiles: ['ADMINISTRADOR'],  // Solo ADMINISTRADOR y GERENTE pueden ver este menú
        submenu: [
          {
            text: 'Clientes',
            icon: 'mdi-account',
            submenu: [
              { icon: 'mdi-human', text: 'Persona', href: '/clientes/persona' },
              { icon: 'mdi-domain', text: 'Empresa', href: '/clientes/empresa' }
            ]
          },
          {
            text: 'Proveedores',
            icon: 'mdi-truck',
            href: '/proveedores',
            perfiles: ['ADMINISTRADOR']  // Solo ADMINISTRADOR puede ver este menú
          }
        ]
      },
      {
        icon: 'mdi-chart-line',
        text: 'Logistica',
        //perfiles: ['ADMINISTRADOR', 'GERENTE'],  // Solo ADMINISTRADOR y GERENTE pueden ver este menú
        submenu: [
          {
            text: 'Entregas',
            icon: 'mdi-truck-fast',
            perfiles: ['GERENTE'],  // Solo ADMINISTRADOR y GERENTE pueden ver este menú
            submenu: [
              { icon: 'mdi-domain', text: 'Rutas', href: '/entregas' }
            ]
          },
          {
            text: 'Embarques',
            icon: 'mdi-truck-delivery',
            href: '/proveedores',
            perfiles: ['ADMINISTRADOR']  // Solo ADMINISTRADOR puede ver este menú
          }
        ]
      },
    ];
  }
});
</script>
