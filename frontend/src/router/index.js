import { createRouter, createWebHistory } from 'vue-router';
import App from '../App.vue';
import Home from '../views/Home.vue';
import Visual from '../views/Visual.vue';

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/visual',
    name: 'Visual',
    component: Visual
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
