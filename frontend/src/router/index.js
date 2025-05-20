import { createRouter, createWebHistory } from 'vue-router';
import Home from '../components/v1/Home.vue';
import Visual from '../components/v1/Visual.vue';
import ChartView from '../components/v1/ChartView.vue';
import HeatmapView from '../components/v1/HeatmapView.vue'; // ✅ 추가

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
