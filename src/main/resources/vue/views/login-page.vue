<template id="login-page">
   <v-app fluid>
      <div class="signin-overlay background pa-5">
         <v-card class="signin-card">
            <v-toolbar color="primary">
               <v-avatar class="ml-5">
                  <v-img src="images/avatar.jpg"></v-img>
               </v-avatar>
               <v-card-title>Login View</v-card-title>
            </v-toolbar>
            <v-card-text class="mt-3">
               <v-row>
                  <v-col>
                     <v-text-field v-model="credentials.nombreUsuario" autofocus rounded label="ID Usuario"
                        variant="outlined" density="compact" prepend-inner-icon="mdi-account-tie" color="primary">
                     </v-text-field>
                     <v-text-field v-model="credentials.contraseña" rounded variant="outlined" density="compact"
                        label="Contraseña" :type="showPassword ? 'text' : 'password'"
                        prepend-inner-icon="mdi-account-key"
                        :append-inner-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                        @click:append-inner="showPassword = !showPassword" color="primary">
                     </v-text-field>
                     <v-text-field v-model="credentials.perfil" rounded variant="outlined" label="Rol"
                        prepend-inner-icon="mdi-account-cog" density="compact" @keyup.enter="signIn" color="primary">
                     </v-text-field>
                  </v-col>
               </v-row>
               <v-divider class="mb-5"></v-divider>
               <v-row>
                  <v-col>
                     <v-btn block class="px-5 rounded-pill" variant="tonal" prepend-icon="mdi-close-outline"
                        color="secondary" @click="reset">Cancelar</v-btn>
                  </v-col>
                  <v-col>
                     <v-btn block class="rounded-pill" variant="tonal" prepend-icon="mdi-check-outline"
                        color="secondary" @click="signIn">Ingresar</v-btn>
                  </v-col>
               </v-row>
            </v-card-text>
         </v-card>
      </div>
   </v-app>
</template>

<script>
app.component("login-page", {
   template: "#login-page",
   data: () => ({
      showPassword: false,
      credentials: { nombreUsuario: "", contraseña: "", perfil: "" },
      credentialsDefault: { nombreUsuario: "", contraseña: "", perfil: "" },
   }),

   methods: {
      showError(titulo, mensaje) {
         alert(`${titulo}: ${mensaje}`);
      },

      signIn() {
         if (!this.credentials.nombreUsuario || !this.credentials.contraseña || !this.credentials.perfil) {
            this.showError('Acceso', 'Por favor completa todos los campos');
            return;
         }

         fetch('api/login', {
            method: 'POST',
            headers: {
               'Content-Type': 'application/json'
            },
            body: JSON.stringify(this.credentials)
         })
            .then(async response => {
               if (!response.ok) {
                  const errorText = await response.text();
                  throw new Error(errorText || 'Credenciales incorrectas');
               }
               window.location = '/';
            })
            .catch(error => this.showError('Acceso', error.message || error));
      },

      reset() {
         Object.assign(this.credentials, this.credentialsDefault);
      }
   }
})
</script>

<style scoped>
.signin-overlay {
   position: fixed;
   top: 0;
   left: 0;
   width: 100%;
   height: 100%;
}

.background {
   /*background-image: url('images/login-v3.jpg');*/
   background-repeat: no-repeat;
   background-size: cover;
   background-position: center;
   background-size: auto 100%;
   background-color: #e8eaf6;
   background-blend-mode: multiply;
}

.v-card.signin-card {
   max-width: 380px;
   margin: 150px auto;
   padding: 4px;
   box-shadow: 0 2px 20px rgba(0, 0, 0, .7);
}

.signin-card h1 {
   font-weight: 400;
   color: #444;
   font-size: 28px;
   text-align: center;
}
</style>