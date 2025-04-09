<template id="proveedores-page">
    <app-frame>
        <div>
            <h1>Lista de Proveedores</h1>
            <v-data-table :headers="proveedores.headers" :items="proveedores.items" class="elevation-1">
                <template v-slot:proveedores.headers.id="{ column }">
                    {{ column.title.toUpperCase() }}
                </template>
            </v-data-table>
        </div>
    </app-frame>
</template>

<script>
app.component("proveedores-page", {
    template: "#proveedores-page",
    data() {
        return {
            proveedores: {
                headers: [
                    { text: "ID", value: "id" },
                    { text: "Nombre", value: "name" },
                    { text: "Tamaño", value: "size" },
                    { text: "Color", value: "color" },
                    { text: "Precio", value: "price" },
                    { text: "Cantidad", value: "quantity" },
                ],
                items: [
                    {
                        id: 1,
                        name: "T-Shirt",
                        size: "M",
                        color: "Red",
                        price: 19.99,
                        quantity: 10,
                    },
                    {
                        id: 2,
                        name: "Jeans",
                        size: "32",
                        color: "Blue",
                        price: 49.99,
                        quantity: 5,
                    },
                    {
                        id: 3,
                        name: "Sweater",
                        size: "L",
                        color: "Green",
                        price: 29.99,
                        quantity: 7,
                    },
                    {
                        id: 4,
                        name: "Jacket",
                        size: "XL",
                        color: "Black",
                        price: 89.99,
                        quantity: 3,
                    },
                    {
                        id: 5,
                        name: "Socks",
                        size: "One Size",
                        color: "White",
                        price: 9.99,
                        quantity: 20,
                    },
                ],
            },
        };
    },
    mounted() {
        // Aquí puedes agregar lógica que necesites al montar el componente
    },
});
</script>