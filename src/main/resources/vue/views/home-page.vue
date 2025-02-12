<template id="home-page">
    <app-frame>
        <v-card :disabled="loading" :loading="loading" class="mx-auto my-12" max-width="374">
            <template v-slot:loader="{ isActive }">
                <v-progress-linear :active="isActive" color="deep-purple" height="4" indeterminate></v-progress-linear>
            </template>

            <v-img height="250" src="https://cdn7.kiwilimon.com/brightcove/8214/640x640/8214.jpg.webp" cover></v-img>

            <v-card-item>
                <v-card-title>Tortilla de maiz amarillo</v-card-title>

                <v-card-subtitle>
                    <span class="me-1">Sucursal Norte</span>

                    <v-icon color="error" icon="mdi-fire-circle" size="small"></v-icon>
                </v-card-subtitle>
            </v-card-item>

            <v-card-text>
                <div class="my-4 text-subtitle-1">
                    $20 • Kilo de tortilla 
                </div>

                <div>Pide desde el este sitio web y llegara hasta la comodidad de tu casa
                </div>
            </v-card-text>

            <v-divider class="mx-4 mb-1"></v-divider>

            <v-card-title>Ordena aqui</v-card-title>

            <div class="px-4 mb-2">
                <v-chip-group v-model="selection" selected-class="bg-deep-purple-lighten-2">
                    <v-chip>5kg</v-chip>

                    <v-chip>10kg</v-chip>

                    <v-chip>15kg</v-chip>

                    <v-chip>20kg</v-chip>
                </v-chip-group>
            </div>

            <v-card-actions>
                <v-btn color="deep-purple-lighten-2" text="Ordenar" block border @click="reserve"></v-btn>
            </v-card-actions>
        </v-card>
    </app-frame>
</template>

<script>
app.component("home-page", {
    template: "#home-page",
    data: () => ({
        loading: false,
        selection: 1,
    }),

    methods: {
        reserve() {
            this.loading = true

            setTimeout(() => (this.loading = false), 2000)
        },
    },
})
</script>
