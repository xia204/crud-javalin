<template id="form-page">
    <v-container>
      <h2>Formulario</h2>
      <form @submit.prevent="submit">
        <v-text-field v-model="name" :counter="10" :error-messages="errors.name" label="Nombre"></v-text-field>
        <v-text-field v-model="phone" :counter="7" :error-messages="errors.phone" label="Celular"></v-text-field>
        <v-text-field v-model="email" :error-messages="errors.email" label="E-mail"></v-text-field>
        <v-select v-model="select" :error-messages="errors.select" :items="items" label="Área"></v-select>
  
        <v-checkbox v-model="checkbox" :error-messages="errors.checkbox" label="Habilitado" value="enabled"></v-checkbox>
        <v-checkbox v-model="checkbox" :error-messages="errors.checkbox" label="Deshabilitado" value="disabled"></v-checkbox>
  
        <v-btn class="me-4" type="submit">Aceptar</v-btn>
        <v-btn @click="handleReset">Limpiar</v-btn>
        <v-btn class="me-4" @click="$parent.navigateTo('/')">Volver</v-btn>
      </form>
    </v-container>
  </template>
  
  <script>
  app.component("form-page", {
    template: "#form-page",
    data() {
      return {
        name: "",
        phone: "",
        email: "",
        select: null,
        checkbox: null,
        items: ["Ventas", "RH", "Logística", "Almacén"],
        errors: {},
      };
    },
    methods: {
      validate() {
        this.errors = {};
        if (this.name.length < 3 || !/^[a-zA-Z]+$/.test(this.name)) this.errors.name = "El nombre debe tener al menos 3 caracteres y solo contener letras.";
        if (!/^[0-9-]{10,}$/.test(this.phone)) this.errors.phone = "El número celular debe tener al menos 10 dígitos.";
        if (!/^[a-z0-9.-]+@[a-z0-9.-]+\.[a-z]+$/i.test(this.email)) this.errors.email = "El correo ingresado no es válido.";
        if (!this.select) this.errors.select = "Selecciona un área.";
        if (!this.checkbox) this.errors.checkbox = "Selecciona el estado.";
        return Object.keys(this.errors).length === 0;
      },
      submit() {
        if (this.validate()) {
          alert(JSON.stringify({ name: this.name, phone: this.phone, email: this.email, select: this.select, checkbox: this.checkbox }, null, 2));
        }
      },
      handleReset() {
        this.name = "";
        this.phone = "";
        this.email = "";
        this.select = null;
        this.checkbox = null;
        this.errors = {};
      },
    },
  });
  </script>
  