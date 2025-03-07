<template>
  <img alt="Vue logo" src="./assets/logo.png">
  <Header></Header>
  <router-view></router-view>
  <Footer></Footer>

</template>

<script>

import Header from './components/PageHeader.vue'
import Footer from './components/PageFooter.vue'
export default {
  name: 'App',
  components: {
    Header,
    Footer,
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
</style>
