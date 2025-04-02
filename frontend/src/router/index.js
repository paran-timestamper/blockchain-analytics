import { createRouter, createWebHistory } from 'vue-router';
import Home from '../views/Home.vue';
import Visual from '../views/Visual.vue';
import ChartView from '../views/ChartView.vue';
import HeatmapView from '../views/HeatmapView.vue'; // ✅ 추가

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/visual', name: 'Visual', component: Visual },
  { path: '/charts', name: 'Chart', component: ChartView },
  { path: '/heatmap', name: 'Heatmap', component: HeatmapView } // ✅ 추가
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
