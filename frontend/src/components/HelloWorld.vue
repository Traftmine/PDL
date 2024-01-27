<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import axios, { AxiosResponse } from 'axios';

const images = ref([]); // Stores the list of images retrieved from the backend
const selectedImage = ref(null); // Stores the ID of the selected image
const downloadedImageUrl = ref(null); // Stores the downloaded image URL
const showGallery = ref(false); // Flag to determine whether to show the gallery
const fileInputRef = ref(null);

// -------------------------- FUNCTIONS -------------------------- //

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

// Watch for changes in selectedImage
watch(selectedImage, (newValue) => {
  if (newValue === null) { hideImage();} });

// -------------------------- IMAGE GALLERY -------------------------- //

// Function to show the selected image
function toggleGallery() {
  showGallery.value = !showGallery.value;
}

// -------------------------- IMAGE DOWNLOAD -------------------------- //

// Function to show the selected image
function showImage() {
  if(downloadedImageUrl.value == null) {
    downloadImage();}
  else { hideImage(); }
}

// Function to show the selected image
function hideImage() {
  downloadedImageUrl.value = null;
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

// -------------------------- IMAGE UPLOAD -------------------------- //

function handleFileUpload(event) {
  // Update the file input value
  fileInputRef.value = event.target.files[0];
}

function sub() {
  submitFile();
}
async function submitFile() {
  try {
    const formData = new FormData();
    formData.append('image', fileInputRef.value);

    await axios.post('/images', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    // Refresh the images after successful upload
    const response = await axios.get('/images');
    images.value = response.data;
    // Reset the file input and hide the upload form
    fileInputRef.value = null;
    showUploadForm();
  } catch (error) {
    console.error('Error uploading image:', error);
  }
}

// -------------------------- IMAGE UPLOAD FORM -------------------------- //

// Function to show the form to upload images
function showUploadForm() {
  const uploadForm = document.getElementById('uploadForm');
  // Toggle the display of the upload form
  if (uploadForm.style.display === 'none') {
    uploadForm.style.display = 'block';
  } else {
    uploadForm.style.display = 'none';
  }
}

// -------------------------- DELETE IMAGE -------------------------- //

function deleteImage() {
  if (confirm('Are you sure you want to delete this image?')) {
    axios.delete(`/images/${selectedImage.value}`)
      .then(() => {
        // Remove the deleted image from the images array
        images.value = images.value.filter(image => image.id !== selectedImage.value);
        console.log('Image deleted successfully.');
      })
      .catch(error => {
        console.error('Error deleting image:', error);
      });
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
        <p v-if="selectedImage !== null">
          You selected: {{ images.find(img => img.id === selectedImage)?.name }}
          <div class="buttonImgSelected">
            <button class="show-button" @click="showImage"> Show </button>
            <button @click="deleteImage"> Delete </button>
        </div>
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

    <!-- Display "Show Galerie" button -->
    <div>
      <button class="galerie" @click="toggleGallery"> Show Galerie </button>
    </div>

    <!-- Display the gallery if showGallery is true -->
    <div v-if="showGallery">
      <h2>Image Gallery</h2>
      <div v-for="image in images" :key="image.id">
        <!-- Display images in the gallery -->
        <img
          :src="`/images/${image.id}`"
          alt="Gallery Image"
          class="resizable-image"
        />
      </div>
    </div>

    <!-- Display "Upload" button -->
    <div>
      <button class="upload" @click="showUploadForm"> Upload </button>
    </div>

    <div id="uploadForm" style="display: none;">
      <div class="container">
        <div>
          <p>
            <input type="file" ref="fileInput" @change="handleFileUpload( $event )"/>
            <button @click="sub"> Submit </button>
          </p>
        </div>
      </div>
    </div>

  </div>
</template>


<style>

/* GALERY and IMAGES */

.resizable-image {
  max-width: 20%;
  height: auto;
  display: block;
  margin: 10px auto;
}

.galerie {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 5px;
  overflow-x: auto;
}

.galerie .resizable-image {
  max-width: 100%;
  height: auto;
  display: block;
  margin: 10px auto;
}
/* UPLOAD */

.upload {
  position: absolute;
  top: 50px;
  left: 10px;
  padding: 5px;
  overflow-x: auto;
}

/* BUTTONS */

.buttonImgSelected{
  margin-top: 10px;
}

.show-button{
  margin-right: 10px;
}

</style>
