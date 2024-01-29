import axios from 'axios';

const baseURL = '/images';  // Update with your actual backend URL

export async function getImages() {
  try {
    const response = await axios.get(baseURL);
    return response.data;
  } catch (error) {
    console.error('Error fetching images:', error);
    throw error;
  }
}

// -------------------------- IMAGE DOWNLOAD -------------------------- //

// Function to download the selected image
export async function downloadImage(imageId) {
  try {
    // GET to download the selected image
    const response = await axios.get(`${baseURL}/${imageId}`, { responseType: 'blob' });
    const imageDataUrl = await new Promise((resolve) => {
        // Convert the blob to a data URL
        const reader = new FileReader();
        reader.readAsDataURL(response.data);
        reader.onload = function () {
            resolve(reader.result);
        };
    });
    return imageDataUrl as string;
  } catch (error) {
    console.error('Error downloading image:', error);
    throw error;
  }
}

// -------------------------- IMAGE UPLOAD -------------------------- //

export async function uploadImage(file: File) {
    try {
      const formData = new FormData();
      formData.append('file', file);
      await axios.post(baseURL, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
    } catch (error) {
      console.error('Error uploading image:', error);
      throw error;
    }
  }

// -------------------------- IMAGE DELETE -------------------------- //

export async function deleteImage(imageId) {
  try {
    await axios.delete(`/images/${imageId}`);
  } catch (error) {
    console.error('Error deleting image:', error);
    throw error;
  }
}
