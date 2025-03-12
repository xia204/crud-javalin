<template id="empleados-page">
    <app-frame>
        <v-card>
            <v-data-table :headers="empleados.headers" :items="empleados.items" :items-per-page="empleados.itemsPerPage"
                class="elevation-1" dense :search="search">
                <template v-slot:top>
                    <v-toolbar flat>
                        <v-text-field v-model="search" density="comfortable" placeholder="Buscar"
                            prepend-inner-icon="mdi-magnify" style="max-width: 300px;" variant="solo" clearable
                            hide-details></v-text-field>

                        <v-spacer></v-spacer>
                        <v-toolbar-title>Empleados Administrativos</v-toolbar-title>
                        <v-divider class="mx-4" inset vertical></v-divider>
                        <v-spacer></v-spacer>

                        <v-dialog v-model="empleados.dialog" max-width="500px">
                            <template v-slot:activator="{ props }">
                                <v-btn class="mb-2" color="primary" v-bind="props">
                                    Agregar
                                </v-btn>
                            </template>
                            <v-card>
                                <v-card-title>
                                    <span class="text-h5">{{ formTitle }}</span>
                                </v-card-title>
                                <v-card-text>
                                    <v-container>
                                        <v-row>
                                            <v-col cols="12">
                                                <v-text-field v-model="empleados.editedItem.nombre"
                                                    :error-messages="empleados.errors.nombre_usuario"
                                                    label="Nombre"></v-text-field>
                                            </v-col>
                                            <v-col cols="12">
                                                <v-text-field v-model="empleados.editedItem.apellido_paterno"
                                                    :error-messages="empleados.errors.apellido_paterno"
                                                    label="Apellido Paterno"></v-text-field>
                                            </v-col>
                                            <v-col cols="12">
                                                <v-text-field v-model="empleados.editedItem.apellido_materno"
                                                    :error-messages="empleados.errors.apellido_materno"
                                                    label="Apellido Materno"></v-text-field>
                                            </v-col>
                                            <v-col cols="12">
                                                <v-text-field v-model="empleados.editedItem.correo_empresarial"
                                                    :error-messages="empleados.errors.correo_empresarial"
                                                    label="Correo Empresarial"></v-text-field>
                                            </v-col>
                                            <v-col cols="12">
                                                <v-text-field v-model="empleados.editedItem.nombre_usuario"
                                                    :error-messages="empleados.errors.nombre_usuario"
                                                    label="Usuario"></v-text-field>
                                            </v-col>
                                            <v-col cols="12">
                                                <v-text-field v-model="empleados.editedItem.contraseña"
                                                    :type="showPassword ? 'text' : 'password'"
                                                    :error-messages="empleados.errors.contraseña" label="Contraseña"
                                                    append-inner-icon="mdi-eye-off"
                                                    @click:append-inner="showPassword = !showPassword">
                                                </v-text-field>
                                            </v-col>
                                        </v-row>
                                    </v-container>
                                </v-card-text>
                                <v-card-actions>
                                    <v-spacer></v-spacer>
                                    <v-btn color="blue-darken-1" variant="text" @click="close">Cancelar</v-btn>
                                    <v-btn color="blue-darken-1" variant="text" @click="save">Guardar</v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-dialog>

                        <v-dialog v-model="empleados.dialogDelete" max-width="500px">
                            <v-card>
                                <v-card-title class="headline">Eliminar Empleado</v-card-title>
                                <v-card-text>¿Estás seguro de que deseas eliminar a {{ empleados.editedItem.nombre
                                }}</v-card-text>
                                <v-card-actions>
                                    <v-spacer></v-spacer>
                                    <v-btn color="blue-darken-1" variant="text" @click="close">Cancelar</v-btn>
                                    <v-btn color="blue-darken-1" variant="text"
                                        @click="deleteItemConfirm">Eliminar</v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-dialog>
                    </v-toolbar>
                </template>

                <template v-slot:item.actions="{ item }">
                    <v-icon class="me-2" size="small" @click="editItem(item)">mdi-pencil</v-icon>
                    <v-icon size="small" @click="deleteItem(item)">mdi-delete</v-icon>
                </template>
            </v-data-table>
        </v-card>
    </app-frame>
</template>

<script>
app.component("empleados-page", {
    template: "#empleados-page",
    data: () => ({
        search: "",
        showPassword: false,
        empleados: {
            dialog: false,
            dialogDelete: false,
            headers: [
                { title: 'ID', key: 'id' },
                { title: 'Nombre', key: 'nombre' },
                { title: 'Apellido Paterno', key: 'apellido_paterno' },
                { title: 'Apellido Materno', key: 'apellido_materno' },
                { title: 'Correo', key: 'correo_empresarial' },
                { title: 'Usuario', key: 'nombre_usuario' },
                { title: 'Acciones', key: 'actions' },
            ],
            errors: {},
            items: [],
            itemsPerPage: 10,
            editedIndex: -1,
            editedItem: { id: 0, nombre: '', apellido_paterno: '', apellido_materno: '', correo_empresarial: '', nombre_usuario: '', contraseña: '' },
            defaultItem: { id: 0, nombre: '', apellido_paterno: '', apellido_materno: '', correo_empresarial: '', nombre_usuario: '', contraseña: '' },
            listaAreas: []
        },
    }),

    computed: {
        formTitle() {
            return this.empleados.editedIndex === -1 ? 'Agregar Empleado' : 'Editar Empleado'
        },
    },

    created() {
        this.initialize();
    },

    methods: {

        validate() {
            this.empleados.errors = {}; // Limpiar errores
            const item = this.empleados.editedItem; // Acceso rápido a los datos

            if (!item.nombre || item.nombre.trim().length < 3 || !/^[a-zA-Z]+$/.test(item.nombre.trim())) {
                this.empleados.errors.nombre = "El nombre debe tener al menos 3 caracteres y no contener espacios vacíos.";
            }
            if (!item.apellido_paterno || item.apellido_paterno.trim().length < 3 || !/^[a-zA-Z]+$/.test(item.apellido_paterno.trim())) {
                this.empleados.errors.apellido_paterno = "El apellido debe tener al menos 3 caracteres y no contener espacios vacíos.";
            }
            if (!item.apellido_materno || item.apellido_materno.trim().length < 3 || !/^[a-zA-Z]+$/.test(item.apellido_materno.trim())) {
                this.empleados.errors.apellido_materno = "El apellido debe tener al menos 3 caracteres y no contener espacios vacíos.";
            }
            if (!item.nombre_usuario || item.nombre_usuario.trim().length < 3 || !/^[a-zA-Z]+$/.test(item.nombre_usuario.trim())) {
                this.empleados.errors.nombre_usuario = "El nombre de usuario debe tener al menos 3 caracteres y no contener espacios vacíos.";
            }
            if (!item.correo_empresarial || !/^[a-z0-9.-]+@[a-z0-9.-]+\.[a-z]+$/i.test(item.correo_empresarial.trim())) {
                this.empleados.errors.correo_empresarial = "El correo ingresado no es válido.";
            }
            if (!item.contraseña || item.contraseña.trim().length < 6) {
                this.empleados.errors.contraseña = "La contraseña debe tener al menos 6 caracteres y no contener solo espacios.";
            }

            return Object.keys(this.empleados.errors).length === 0;
        },


        initialize() {
            fetch('/api/empleados')
                .then(response => response.json())
                .then(data => {
                    this.empleados.items = data;
                })
                .catch(error => console.error("Error al obtener empleados:", error));
        },

        editItem(item) {
            fetch(`api/empleados/${item.id}`)
                .then(response => response.json())
                .then(data => {
                    console.log(data);
                    this.empleados.editedIndex = this.empleados.items.indexOf(item)
                    this.empleados.editedItem = Object.assign({}, data)
                    this.empleados.dialog = true
                })
                .catch(error => console.error("Error al obtener empleado:", error));
        },

        deleteItem(item) {
            this.empleados.editedIndex = this.empleados.items.indexOf(item);
            this.empleados.editedItem = Object.assign({}, item);
            this.empleados.dialogDelete = true;
        },

        deleteItemConfirm() {
            const id = this.empleados.editedItem.id;
            fetch(`api/empleados/${id}`, { method: 'DELETE' })
                .then(response => {
                    if (!response.ok) {
                        throw new Error("Error al eliminar el empleado");
                    }
                    return response.text();
                })
                .then(data => {
                    if (data.trim() === "success") {
                        this.empleados.items.splice(this.empleados.editedIndex, 1);
                    }
                    this.closeDelete();
                })
                .catch(error => console.error("Error al eliminar empleado:", error));
        },

        close() {
            this.empleados.dialog = false;
            this.$nextTick(() => {
                this.empleados.editedItem = Object.assign({}, this.empleados.defaultItem);
                this.empleados.editedIndex = -1;
            });
        },

        closeDelete() {
            this.empleados.dialogDelete = false
            this.$nextTick(() => {
                this.empleados.editedItem = Object.assign({}, this.empleados.defaultItem)
                this.empleados.editedIndex = -1
            })
        },

        save() {
            if (!this.validate()) return;

            const edicion = this.empleados.editedIndex > -1;
            const empleados = this.empleados.editedItem;
            const metodo = edicion ? 'PATCH' : 'POST';
            const url = edicion ? `api/empleados/${empleados.id}` : "api/empleados";

            fetch(url, {
                method: metodo,
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(empleados)
            })
                .then(response => response.text()) // Primero obtenemos la respuesta como texto
                .then(text => {
                    try {
                        // Intentamos parsear la respuesta como JSON
                        const data = JSON.parse(text);
                        if (data.id) {
                            console.log("Empleado guardado con ID:", data.id);
                            this.initialize(); // Recargar lista de empleados
                            this.close();
                            return;
                        }
                    } catch (e) {
                        // Si falla el JSON.parse, significa que la respuesta fue texto plano
                        if (text.trim() === "success") {
                            console.log("Empleado editado correctamente");
                            this.empleados.items = this.empleados.items.map(item =>
                                item.id === empleados.id ? empleados : item);
                            this.close();
                            return;
                        }
                    }
                    throw new Error("Respuesta inesperada del servidor: " + text);
                })

                .catch(error => console.error("Error al guardar empleado:", error.message));
        },

    },

});
</script>