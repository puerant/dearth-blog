<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import type { ProjectVO } from '../types'

const props = defineProps<{ projects: ProjectVO[] }>()
const emit = defineEmits<{ (e: 'select', project: ProjectVO): void }>()

const SNAP_SPEED = 0.09
const SCROLL_FACTOR = 0.22
const CARD_W = 41
const CARD_H = 55

const wrapRef = ref<HTMLElement | null>(null)
const ringRef = ref<HTMLElement | null>(null)
const centerRef = ref<HTMLElement | null>(null)

let currentAngle = 0
let targetAngle = 0
let rafId: number | null = null
let isHovering = false

const step = computed(() => props.projects.length ? 360 / props.projects.length : 360)
const activeIdx = ref(0)

function getMetrics() {
  if (!wrapRef.value) return { W: 660, RADIUS: 310 }
  const W = wrapRef.value.clientWidth
  return { W, RADIUS: W * 0.47 }
}

function layout(angle: number) {
  if (!ringRef.value) return
  const items = Array.from(ringRef.value.querySelectorAll<HTMLElement>('.clock-item'))
  const { W, RADIUS } = getMetrics()
  const cx = W / 2, cy = W / 2
  items.forEach((item, i) => {
    const deg = i * step.value + angle
    const rad = deg * Math.PI / 180
    const x = cx + Math.sin(rad) * RADIUS - CARD_W / 2
    const y = cy - Math.cos(rad) * RADIUS - CARD_H / 2
    item.style.transform = `translate(${x}px, ${y}px)`
  })
}

function getActiveIdx(angle: number) {
  let best = 0, bestDist = Infinity
  props.projects.forEach((_, i) => {
    const deg = ((i * step.value + angle) % 360 + 360) % 360
    const dist = Math.min(deg, 360 - deg)
    if (dist < bestDist) { bestDist = dist; best = i }
  })
  return best
}

function tick() {
  const diff = targetAngle - currentAngle
  currentAngle += diff * SNAP_SPEED
  layout(currentAngle)
  const idx = getActiveIdx(currentAngle)
  if (idx !== activeIdx.value) {
    activeIdx.value = idx
    emit('select', props.projects[idx])
    // toggle active class
    if (ringRef.value) {
      ringRef.value.querySelectorAll('.clock-item').forEach((el, i) => {
        el.classList.toggle('active', i === idx)
      })
    }
  }
  rafId = Math.abs(diff) > 0.05 ? requestAnimationFrame(tick) : null
}

function startTick() {
  if (!rafId) rafId = requestAnimationFrame(tick)
}

function onWheel(e: WheelEvent) {
  if (!isHovering) return
  e.preventDefault()
  targetAngle += e.deltaY * SCROLL_FACTOR
  startTick()
}

function onCenterClick() {
  targetAngle += step.value
  startTick()
}

onMounted(() => {
  if (!props.projects.length) return
  layout(0)
  emit('select', props.projects[0])
  if (wrapRef.value) {
    wrapRef.value.addEventListener('wheel', onWheel, { passive: false })
  }
  // Mark first item active
  if (ringRef.value) {
    ringRef.value.querySelector('.clock-item')?.classList.add('active')
  }
})

onUnmounted(() => {
  if (rafId) cancelAnimationFrame(rafId)
  if (wrapRef.value) wrapRef.value.removeEventListener('wheel', onWheel)
})
</script>

<template>
  <div
    class="clock-wrap"
    ref="wrapRef"
    @mouseenter="isHovering = true"
    @mouseleave="isHovering = false"
  >
    <svg class="clock-orbit" viewBox="0 0 400 400" xmlns="http://www.w3.org/2000/svg">
      <circle cx="200" cy="200" r="184" fill="none" stroke="rgba(255,255,255,0.045)" stroke-width="1" stroke-dasharray="3 7"/>
    </svg>

    <div class="clock-ring" ref="ringRef">
      <div
        v-for="(p, i) in projects"
        :key="p.id"
        class="clock-item"
        :data-idx="i"
        @click="$router.push ? null : (window.location.href = `/projects/${p.projectNo}`)"
      >
        <div class="clock-card" @click.stop="$emit('select', p); (window as any).location.href = `/projects/${p.projectNo}`">
          <div class="cc-img" :style="`background: linear-gradient(145deg, hsl(${p.hue},55%,10%), hsl(${p.hue},30%,5%));`">
            <span class="cc-bg-text" :style="`color: hsl(${p.hue},45%,18%);`">{{ p.shortName }}</span>
            <span class="cc-id">{{ p.projectNo }}</span>
          </div>
          <div class="cc-label" :style="`border-top-color: hsl(${p.hue},30%,16%);`">{{ p.name }}</div>
        </div>
      </div>
    </div>

    <div
      class="clock-center"
      ref="centerRef"
      @click="onCenterClick"
    >
      <div class="clock-logo">
        <span class="clock-logo-text">PROJECT</span>
        <sup class="clock-logo-sup">TM</sup>
      </div>
    </div>
  </div>
</template>
