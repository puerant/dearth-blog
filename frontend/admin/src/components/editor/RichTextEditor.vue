<template>
  <div class="rich-editor">
    <Toolbar :editor="editorRef" :defaultConfig="toolbarConfig" mode="default" />
    <Editor
      v-model="content"
      :defaultConfig="editorConfig"
      mode="default"
      style="flex: 1; overflow-y: hidden"
      @onCreated="handleCreated"
      @onChange="handleChange"
    />
  </div>
</template>

<script setup lang="ts">
import { shallowRef } from 'vue'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import type { IDomEditor, IEditorConfig } from '@wangeditor/editor'
import '@wangeditor/editor/dist/css/style.css'

const props = defineProps<{ modelValue: string }>()
const emit = defineEmits<{ 'update:modelValue': [value: string] }>()

const editorRef = shallowRef<IDomEditor>()
const content = ref(props.modelValue)

watch(() => props.modelValue, (val) => {
  if (editorRef.value && val !== editorRef.value.getHtml()) {
    editorRef.value.setHtml(val)
  }
})

const toolbarConfig = {}
const editorConfig: Partial<IEditorConfig> = {
  placeholder: '请输入正文...',
}

function handleCreated(editor: IDomEditor) {
  editorRef.value = editor
}

function handleChange(editor: IDomEditor) {
  emit('update:modelValue', editor.getHtml())
}

onBeforeUnmount(() => {
  editorRef.value?.destroy()
})
</script>

<style scoped>
.rich-editor {
  height: 100%;
  display: flex;
  flex-direction: column;
  border: none;
}
</style>
