<template>
  <div>
    <div class="heatmap-page">
      <div class="controls">
        <input type="date" v-model="selectedDate" @change="fetchHeatmapData" />
      </div>
      <div class="heatmap-container">
        <div class="section-wrapper">
          <div class="section-title">Daily Heatmap</div>
          <div ref="dailyHeatmapContainer" class="heatmap-box"></div>
        </div>
        <div class="section-wrapper">
          <div class="section-title">Monthly Heatmap</div>
          <div ref="monthlyHeatmapContainer" class="heatmap-box"></div>
        </div>
      </div>
    </div>

    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content large">
        <h3>{{ modalTitle }}</h3>
        <div class="modal-heatmaps">
          <div class="section-wrapper">
            <div class="section-title">Daily Heatmap</div>
            <div ref="modalDaily" class="heatmap-box"></div>
          </div>
          <div class="section-wrapper">
            <div class="section-title">Monthly Heatmap</div>
            <div ref="modalMonthly" class="heatmap-box"></div>
          </div>
        </div>
        <button class="close-btn" @click="closeModal">✕</button>
      </div>
    </div>
  </div>
</template>

<script>
import * as d3 from "d3";
export default {
  name: "HeatmapView",
  props: { address: { type: String, required: true } },
  data() {
    return {
      selectedDate: new Date().toISOString().substr(0, 10),
      heatmapDataDaily: [],
      heatmapDataMonthly: [],
      maxValDaily: 1,
      maxValMonthly: 1,
      showModal: false,
      modalDate: null
    };
  },
  computed: {
    modalTitle() {
      return `${this.modalDate} Heatmap`;
    }
  },
  methods: {
    closeModal() {
      this.showModal = false;
      this.modalDate = null;
      d3.select(this.$refs.modalDaily).select('svg').remove();
      d3.select(this.$refs.modalMonthly).select('svg').remove();
    },
    async fetchHeatmapData() {
      if (!this.selectedDate) return;
      const dateOnly = this.selectedDate;
      const monthOnly = dateOnly.slice(0, 7);

      // Daily
      const resD = await fetch('/api/heatmap/daily', {
        method: 'POST', headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ fromAddress: this.address, date: dateOnly })
      });
      const rD = await resD.json();
      const ranges = ["00-04","04-08","08-12","12-16","16-20","20-24"];
      const blocks = rD.timeBlocks?.length ? rD.timeBlocks : ranges.map(r=>({range:r,txCount:0}));
      this.heatmapDataDaily = blocks.map(b=>({ range:b.range, txCount:+b.txCount }));
      this.maxValDaily = d3.max(this.heatmapDataDaily, d=>d.txCount) || 1;

      // Monthly
      const resM = await fetch('/api/heatmap/monthly', {
        method: 'POST', headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ fromAddress: this.address, date: monthOnly })
      });
      let rM = await resM.json();
      if (!Array.isArray(rM)) rM = [rM];
      this.heatmapDataMonthly = rM.map(d=>({ day:d.day, txCount:+d.txCount }));
      this.maxValMonthly = d3.max(this.heatmapDataMonthly, d=>d.txCount) || 1;

      this.$nextTick(() => {
        this.drawDailyHeatmap(this.$refs.dailyHeatmapContainer);
        this.drawMonthlyHeatmap(this.$refs.monthlyHeatmapContainer);
      });
    },
    handleCellClick(dateStr) {
      this.modalDate = dateStr;
      this.showModal = true;
      this.$nextTick(() => {
        this.drawDailyHeatmap(this.$refs.modalDaily);
        this.drawMonthlyHeatmap(this.$refs.modalMonthly);
      });
    },
    drawDailyHeatmap(container) {
      const margin = { top:40, right:40, bottom:30, left:60 };
      const w = 80*6 + margin.left + margin.right;
      const h = 40 + margin.top + margin.bottom;
      d3.select(container).select('svg').remove();
      const svg = d3.select(container).append('svg').attr('width',w).attr('height',h);
      const scale = d3.scaleSequential(d3.interpolateReds).domain([0,this.maxValDaily]);
      svg.selectAll('rect').data(this.heatmapDataDaily.map((d,i)=>({...d,index:i})))
          .enter().append('rect')
          .attr('x',d=>margin.left + d.index*80)
          .attr('y',margin.top)
          .attr('width',80).attr('height',40)
          .attr('fill',d=>scale(d.txCount))
          .attr('stroke','#333')
          .on('click',(e,d)=>{
            const [y,m,day] = this.selectedDate.split('-');
            const dt = `${y}-${m}-${day}T${String(d.index*4).padStart(2,'0')}:00:00`;
            this.handleCellClick(dt);
          });
      svg.selectAll('text.label').data(this.heatmapDataDaily.map(d=>d.range))
          .enter().append('text')
          .attr('class','label')
          .attr('x',(_,i)=>margin.left + i*80 + 40)
          .attr('y',margin.top-10)
          .attr('text-anchor','middle')
          .attr('fill','white')
          .text(d=>d);
    },
    drawMonthlyHeatmap(container) {
      const margin = { top:80, right:40, bottom:40, left:80 };
      const cols = 7, rows = Math.ceil((this.heatmapDataMonthly.length+new Date(...this.selectedDate.split('-').map(Number),1).getDay())/7);
      const w = 80*cols + margin.left + margin.right;
      const h = 40*rows + margin.top + margin.bottom;
      d3.select(container).select('svg').remove();
      const svg = d3.select(container).append('svg').attr('width',w).attr('height',h);
      const scale = d3.scaleSequential(d3.interpolateReds).domain([0,this.maxValMonthly]);
      const [y,m] = this.selectedDate.split('-').map(Number);
      const first = new Date(y,m-1,1).getDay();
      const days = new Date(y,m,0).getDate();
      const cells=[];
      for(let i=0;i<first;i++) cells.push({day:'',txCount:0,dateStr:null});
      for(let i=1;i<=days;i++){
        const dayStr = `${i<10?'0':''}${i}일`;
        const match = this.heatmapDataMonthly.find(d=>d.day===dayStr)||{day:dayStr,txCount:0};
        const dateStr = `${y}-${String(m).padStart(2,'0')}-${String(i).padStart(2,'0')}`;
        cells.push({day:match.day,txCount:match.txCount,dateStr});
      }
      while(cells.length % 7) cells.push({day:'',txCount:0,dateStr:null});
      const weekdays=['일','월','화','수','목','금','토'];
      svg.selectAll('text.week').data(weekdays)
          .enter().append('text')
          .attr('x',(_,i)=>margin.left + i*80 + 40)
          .attr('y',margin.top-30)
          .attr('text-anchor','middle')
          .attr('fill','white')
          .attr('font-weight','bold')
          .text(d=>d);
      svg.selectAll('rect.cell').data(cells)
          .enter().append('rect')
          .attr('class','cell')
          .attr('x',(_,i)=>margin.left + (i%7)*80)
          .attr('y',(_,i)=>margin.top + Math.floor(i/7)*40)
          .attr('width',80).attr('height',40)
          .attr('fill',d=>scale(d.txCount))
          .attr('stroke','#333')
          .on('click',(e,d)=>{ if(d.dateStr) this.handleCellClick(d.dateStr); });
      svg.selectAll('text.num').data(cells)
          .enter().append('text')
          .attr('class','num')
          .attr('x',(_,i)=>margin.left + (i%7)*80 + 4)
          .attr('y',(_,i)=>margin.top + Math.floor(i/7)*40 + 14)
          .attr('fill','white')
          .attr('font-size','12px')
          .text(d=>d.day.replace('일',''));
    }
  },
  mounted() {
    this.fetchHeatmapData();
  }
};
</script>

<style scoped>
input[type="date"] { background:#1e1e1e; color:#fff; border:1px solid #444; border-radius:6px; padding:6px 10px; }
.heatmap-page { padding:24px; color:#fff; }
.controls { margin-bottom:16px; display:flex; gap:10px; align-items:center; }
.heatmap-container { display:flex; flex-direction:column; }
svg { background:#1e1e1e; border-radius:8px; }
.cell:hover { stroke:yellow; stroke-width:2px; cursor:pointer; }
.section-title { text-align:center; font-weight:bold; font-size:18px; margin-bottom:8px; }
.heatmap-box { display:block; overflow-x:auto; border-radius:8px; padding:12px; }
.section-wrapper { border:1px solid #444; border-radius:8px; padding:16px; margin-bottom:16px; background:#1e1e1e; }
.modal-overlay { position:fixed; inset:0; background:rgba(0,0,0,0.6); display:flex; justify-content:center; align-items:center; z-index:1000; }
.modal-content { background:#1e1e1e; padding:24px; border-radius:8px; color:#fff; position:relative; max-width:90%; max-height:90%; overflow:auto; }
.modal-content.large { width:80vw; }
.modal-heatmaps { display:flex; gap:16px; flex-wrap:wrap; }
.close-btn { position:absolute; top:12px; right:12px; background:transparent; border:none; font-size:24px; color:#fff; cursor:pointer; }
</style>
