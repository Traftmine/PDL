<script setup lang="ts">
import HelloWorld from './components/HelloWorld.vue'

</script>

<template>
  <div>
    <h1> Image handler </h1>
  </div>
  <HelloWorld msg="Vite + Vue" />
</template>

<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}
.logo:hover {
  filter: drop-shadow(0 0 2em #4048f0aa);
}

</style>
