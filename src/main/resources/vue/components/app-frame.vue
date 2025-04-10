<template id="app-frame">
  <v-app id="inspire">
    <v-navigation-drawer v-model="drawer" width="300">
      <v-sheet class="pa-4" color="grey-lighten-4">
        <v-row class="d-flex align-center justify-center">
          <v-col class="text-center" cols="auto">
            <v-avatar :image="userImg" class="mb-8" color="grey-darken-1" size="64" />
          </v-col>
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
        <template v-for="(item, index) in filteredMenu" :key="index">
          <menu-item :item="item" />
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
app.component("menu-item", {
  props: ["item"],
  template: `
    <v-list-group v-if="item.children && item.children.length" :value="item.text">
      <template v-slot:activator="{ props }">
        <v-list-item v-bind="props" :title="item.text" :prepend-icon="item.icon" />
      </template>
      <menu-item v-for="(child, index) in item.children" :key="index" :item="child" />
    </v-list-group>
    <v-list-item v-else :title="item.text" :href="item.href" :prepend-icon="item.icon" />
  `
});

app.component("app-frame", {
  template: "#app-frame",
  data: () => ({
    drawer: null,
    userImg: 'images/avatar.jpg',
    menu: []
  }),
  computed: {
    filteredMenu() {
      const perfil = this.$javalin?.state?.userInfo?.perfil;
      return this.menu.filter(item => {
        if (!item.perfiles) return true;
        return item.perfiles.includes(perfil);
      });
    }
  },
  created() {
    const modulos = this.$javalin.state.userInfo.modulos;

    const buildMenuTree = (paths) => {
      const tree = [];

      paths.forEach(modulo => {
        const parts = modulo.urlModulo.split('/');
        let current = tree;

        parts.forEach((part, index) => {
          let existing = current.find(item => item.key === part);

          if (!existing) {
            existing = {
              key: part,
              text: index === parts.length - 1 ? modulo.nombreModulo : part.charAt(0).toUpperCase() + part.slice(1),
              href: index === parts.length - 1 ? '/' + modulo.urlModulo : null,
              icon: index === parts.length - 1 ? 'mdi-file-outline' : 'mdi-folder-outline',
              children: []
            };
            current.push(existing);
          }

          if (index < parts.length - 1) {
            if (!existing.children) existing.children = [];
            current = existing.children;
          }
        });
      });

      return tree;
    };

    this.menu = buildMenuTree(modulos);
  }
});
</script>
