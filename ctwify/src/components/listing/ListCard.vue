<template>
    <v-col cols="3">
    <v-card text="...">
      <template #title>
        <router-link :to="`/main/getList/${list.listId}`">
            <img :src="`http://localhost:8080/images/listings/${list.photo1}`" height="300px" style="cursor: pointer" />
        </router-link>
        <div>title插槽</div>
       </template>
      <template #subtitle
        ><div>{{ list.name }}</div>
      </template>
      <v-card-actions>
        <v-btn>
          <v-icon>mdi-cart-plus</v-icon>
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-col>
</template>
<script setup>
defineProps(["list"])
</script>
<style></style>