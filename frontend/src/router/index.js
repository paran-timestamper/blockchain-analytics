import { createRouter, createWebHistory } from 'vue-router';
import Home from '../components/Home.vue';
import Visual from '../components/Visual.vue';
import ChartView from '../components/ChartView.vue';
import HeatmapView from '../components/HeatmapView.vue'; // ✅ 추가

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
