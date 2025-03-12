<template id="app-frame">
  <v-app id="inspire">

    <v-navigation-drawer v-model="drawer">
      <v-sheet
        class="pa-4"
        color="grey-lighten-4"
      >
        <v-avatar
          :image="userImg"
          class="mb-4"
          color="grey-darken-1"
          size="64"
        ></v-avatar>

        <div>Gerente de Ventas</div>
      </v-sheet>

      <v-divider></v-divider>

      <v-list>
        <v-list-item v-for="menus in menu"
          :prepend-icon="menus.icon"
          :title="menus.text"
          :href="menus.href"
          link
        ></v-list-item>
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
  </v-app>
  <v-footer>La casa de la tortilla, 2025</v-footer>
</template>

<script>
app.component("app-frame", {
  template: "#app-frame",
  data: () => ({
    drawer: null,
    userImg: 'images/avatar.jpg',
    menu: [
        {icon: 'mdi-form-select', text: 'Fomrulario', href: 'form'},
        {icon: 'mdi-account-tie', text: 'Empleados', href: '/empleados'},
        {icon: 'mdi-delete', text: 'Trash', href: '/'},
        {icon: 'mdi-alert-octagon', text: 'Spam', href: "/"},
      ],
  }),
});
</script>