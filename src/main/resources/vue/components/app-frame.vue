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
        <v-list-item v-for="item in filteredMenu" :key="item.text" :title="item.text" :prepend-icon="item.icon"
          :href="item.href" />
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
    const modulos = this.$javalin?.state?.userInfo?.modulos || [];

    // Mapea los módulos a un formato de menú básico
    this.menu = modulos.map(modulo => ({
      icon: this.getIconForModulo(modulo.nombreModulo), // Obtener el ícono según el nombre del módulo
      text: modulo.nombreModulo,
      href: '/' + modulo.urlModulo,
      perfiles: [this.$javalin.state.userInfo.perfil] // El backend ya te filtró por perfil
    }));
  },
  methods: {
    // Método para asignar íconos de forma dinámica según el nombre del módulo
    getIconForModulo(moduloNombre) {
      switch(moduloNombre.toLowerCase()) {
        case 'empleados-page': return 'mdi-account-group';
        case 'form-page': return 'mdi-file';
        case 'proveedores-page': return 'mdi-truck';
        default: return 'mdi-menu'; // Icono por defecto
      }
    }
  }
});
</script>
