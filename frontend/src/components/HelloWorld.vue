<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios, { AxiosResponse } from 'axios';

const images = ref([]); // Stores the list of images retrieved from the backend
const selectedImage = ref(null); // Stores the ID of the selected image
const downloadedImageUrl = ref(null); // Stores the downloaded image URL

// Use the onMounted hook to execute code after the component is mounted
onMounted(async () => {
  try {
    // GET to retrieve the list of images
    const response = await axios.get('/images');
    // Update with the data received from the backend
    images.value = response.data;
  } catch (error) {
    console.error('Error fetching images:', error);
  }
});

// Function to show the selected image
function show() {
  downloadImage();
}

// Function to download the selected image
async function downloadImage() {
  if (selectedImage.value !== null) {
    try {
      // GET to download the selected image
      const response = await axios.get(`/images/${selectedImage.value}`, { responseType: 'blob' });

      // Convert the blob to a data URL
      const reader = new window.FileReader();
      reader.readAsDataURL(response.data);
      reader.onload = function () {
        const imageDataUrl = reader.result as string;
        downloadedImageUrl.value = imageDataUrl;
      };
    } catch (error) {
      console.error('Error downloading image:', error);
    }
  }
}
</script>

<template>
  <div>
    <h1>{{ msg }}</h1>

    <!-- Display this block if the list of images is not empty -->
    <div class="card" v-if="images.length > 0">
      <!-- Select an image -->
      <label for="imageSelect">Select an image:</label>
      <select id="imageSelect" v-model="selectedImage">
        <!-- Default option -->
        <option :key="null" :value="null">None</option>
        <!-- Options in the dropdown list -->
        <option v-for="image in images" :key="image.id" :value="image.id">{{ image.name }}</option>
      </select>

      <!-- Display the name of the selected image or a message if no image is selected -->
      <div>
        <h2>Selected image:</h2>
        <p v-if="selectedImage !== null">
          You selected: {{ images.find(img => img.id === selectedImage)?.name }}
          <button @click="show">
            Show
          </button>
        </p>
        <p v-else>
          No image selected.
        </p>
      </div>

      <!-- Display the downloaded image if available -->
      <img v-if="downloadedImageUrl" :src="downloadedImageUrl" alt="Downloaded Image" class="resizable-image"/>
    </div>

    <!-- Display this block if the list of images is empty -->
    <div class="card" v-else>
      <p>No images available.</p>
    </div>
  </div>
</template>

<style>
.resizable-image {
  max-width: 20%;  /* Set maximum width */
  height: auto;     /* Automatically adjust height to maintain aspect ratio */
  display: block;   /* Remove any default inline styling */
  margin: 0 auto;   /* Center the image horizontally */
}
</style>
