<template id="loading-overlay">
    <v-overlay class="d-flex justify-center align-center" persistent :contained="contained">
        <v-progress-circular :color="color" :size="size" :scrim="color" indeterminate></v-progress-circular>
        <slot></slot>
    </v-overlay>
</template>
<script nonce="@addNonce">
app.component('loading-overlay', {
    template: '#loading-overlay',
    props: {
        color: { type: String, default: 'primary' },
        size: { type: String, default: '48' },
        contained: { type: Boolean, default: false }
    }
})
</script>