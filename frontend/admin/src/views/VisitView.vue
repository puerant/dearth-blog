<template>
  <div class="visit-page">
    <!-- 统计卡片 -->
    <el-row :gutter="16">
      <el-col :span="6" v-for="card in statCards" :key="card.label">
        <div class="stat-card">
          <div class="stat-value">{{ card.value }}</div>
          <div class="stat-label">{{ card.label }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 趋势图 + 排行 -->
    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="16">
        <div class="panel">
          <div class="panel-header">
            <span>访问趋势</span>
            <el-radio-group v-model="days" size="small" @change="loadTrend">
              <el-radio-button :value="7">近 7 天</el-radio-button>
              <el-radio-button :value="30">近 30 天</el-radio-button>
              <el-radio-button :value="90">近 90 天</el-radio-button>
            </el-radio-group>
          </div>
          <v-chart :option="trendOption" style="height: 300px" autoresize />
        </div>
      </el-col>
      <el-col :span="8">
        <div class="panel">
          <div class="panel-header">文章浏览排行 TOP 10</div>
          <div class="rank-list">
            <div v-for="(item, index) in rankList" :key="item.id" class="rank-item">
              <span class="rank-no" :class="{ top3: index < 3 }">{{ index + 1 }}</span>
              <span class="rank-title" :title="item.title">{{ item.title }}</span>
              <span class="rank-count">{{ item.viewCount }}</span>
            </div>
            <el-empty v-if="!rankList.length" description="暂无数据" :image-size="60" />
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { use } from 'echarts/core'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import VChart from 'vue-echarts'
import { getVisitSummaryApi, getVisitTrendApi, getArticleRankApi } from '../api/visit'
import type { VisitSummaryVO, VisitTrendVO, ArticleRankVO } from '../types'

use([LineChart, GridComponent, TooltipComponent, LegendComponent, CanvasRenderer])

const summary = ref<VisitSummaryVO>({ totalPv: 0, totalUv: 0, todayPv: 0, todayUv: 0 })
const trendData = ref<VisitTrendVO[]>([])
const rankList = ref<ArticleRankVO[]>([])
const days = ref(30)

const statCards = computed(() => [
  { label: '累计 PV', value: summary.value.totalPv },
  { label: '今日 PV', value: summary.value.todayPv },
  { label: '累计 UV', value: summary.value.totalUv },
  { label: '今日 UV', value: summary.value.todayUv },
])

const trendOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  legend: { data: ['PV', 'UV'], bottom: 0 },
  grid: { top: 20, bottom: 36, left: 40, right: 20 },
  xAxis: { type: 'category', data: trendData.value.map((d) => d.date), axisLabel: { fontSize: 11 } },
  yAxis: { type: 'value', minInterval: 1 },
  series: [
    { name: 'PV', type: 'line', data: trendData.value.map((d) => d.pv), smooth: true, itemStyle: { color: '#409eff' }, areaStyle: { opacity: 0.1 } },
    { name: 'UV', type: 'line', data: trendData.value.map((d) => d.uv), smooth: true, itemStyle: { color: '#67c23a' }, areaStyle: { opacity: 0.1 } },
  ],
}))

async function loadTrend() {
  trendData.value = await getVisitTrendApi(days.value).catch(() => [])
}

onMounted(async () => {
  const [s, rank] = await Promise.allSettled([getVisitSummaryApi(), getArticleRankApi(10)])
  if (s.status === 'fulfilled') summary.value = s.value
  if (rank.status === 'fulfilled') rankList.value = rank.value
  loadTrend()
})
</script>

<style scoped>
.visit-page { display: flex; flex-direction: column; gap: 0; }
.stat-card { background: #fff; border-radius: 10px; padding: 20px; text-align: center; box-shadow: 0 1px 4px rgba(0,0,0,.04); }
.stat-value { font-size: 28px; font-weight: 700; color: #1a1a1a; }
.stat-label { font-size: 13px; color: #909399; margin-top: 4px; }
.panel { background: #fff; border-radius: 10px; padding: 20px; box-shadow: 0 1px 4px rgba(0,0,0,.04); height: 380px; display: flex; flex-direction: column; }
.panel-header { font-size: 14px; font-weight: 600; color: #333; margin-bottom: 16px; display: flex; justify-content: space-between; align-items: center; flex-shrink: 0; }
.rank-list { flex: 1; overflow-y: auto; display: flex; flex-direction: column; gap: 10px; }
.rank-item { display: flex; align-items: center; gap: 10px; }
.rank-no { width: 22px; height: 22px; border-radius: 50%; background: #f0f2f5; display: flex; align-items: center; justify-content: center; font-size: 12px; font-weight: 600; color: #909399; flex-shrink: 0; }
.rank-no.top3 { background: #409eff; color: #fff; }
.rank-title { flex: 1; font-size: 13px; color: #333; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.rank-count { font-size: 13px; color: #909399; flex-shrink: 0; }
</style>
