<template>
  <div class="md-editor">
    <Editor
      :value="modelValue"
      :plugins="plugins"
      @change="$emit('update:modelValue', $event)"
    />
  </div>
</template>

<script setup lang="ts">
import { Editor } from '@bytemd/vue-next'
import gfm from '@bytemd/plugin-gfm'
import highlight from '@bytemd/plugin-highlight'
import 'bytemd/dist/index.css'
import 'highlight.js/styles/github.css'

defineProps<{ modelValue: string }>()
defineEmits<{ 'update:modelValue': [value: string] }>()

const plugins = [gfm(), highlight()]
</script>

<style>
/* ByteMD 编辑器高度充满父容器 */
.md-editor {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.md-editor .bytemd {
  height: 100% !important;
  border: none;
  border-radius: 0;
}

.md-editor .bytemd-toolbar {
  border-top: none;
  border-left: none;
  border-right: none;
}
</style>
