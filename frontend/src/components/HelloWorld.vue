<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';

const images = ref([]); // Stocke la liste des images récupérées du backend
const selectedImage = ref(null); // Stocke l'ID de l'image sélectionnée

// Utilisation du hook onMounted pour exécuter du code après le montage du composant
onMounted(async () => {
  try {
    // GET pour récupérer la liste des images
    const response = await axios.get('/images');
    // Update avec les données reçues du backend
    images.value = response.data;
  } catch (error) {
    console.error('Erreur lors de la récupération des images:', error);
  }
});

// Fonction pour afficher l'image sélectionnée
function show() {
  if (selectedImage.value !== null) { // Si mage est sélectionnée
    // Objet image correspondant à l'ID sélectionné
    const selectedImageData = images.value.find(img => img.id === selectedImage.value);
    if (selectedImageData) { // Vérifier si l'objet image est trouvé
      const imageUrl = `/${selectedImageData.name}`; // Construire l'URL de l'image à partir du nom
      const imageEl = document.querySelector("img"); // Sélectionner l'élément img dans le DOM
      axios.get(imageUrl, { responseType: "blob" })
        .then((response: AxiosResponse) => {
          // Convertir les données de l'image en URL de données (base64)
          const reader = new window.FileReader();
          reader.readAsDataURL(response.data); 
          reader.onload = function() {
            // Mettre à jour l'attribut src de l'élément img avec l'URL de données de l'image
            const imageDataUrl = (reader.result as string);
            imageEl.setAttribute("src", imageDataUrl);
          };
        })
        .catch(error => {console.error('Erreur lors du chargement de l\'image:', error);
        });
    }
  }
}
</script>

<template>
  <div>
    <h1>{{ msg }}</h1>

    <!-- Affiche ce bloc si la liste des images n'est pas vide -->
    <div class="card" v-if="images.length > 0">
      <!-- Sélectionnez une image -->
      <label for="imageSelect">Sélectionnez une image :</label>
      <select id="imageSelect" v-model="selectedImage">
        <!-- Option par défaut -->
        <option :key="null" :value="null">Aucune</option>
        <!-- Options dans la liste déroulante -->
        <option v-for="image in images" :key="image.id" :value="image.id">{{ image.name }}</option>
      </select>

      <!-- Affiche le nom de l'image sélectionnée ou un message si aucune image n'est sélectionnée -->
      <div>
        <h2>L'image sélectionnée :</h2>
        <p v-if="selectedImage !== null">
          Vous avez sélectionné : {{ images.find(img => img.id === selectedImage)?.name }}
          <button @click="show">
            Afficher
          </button>
        </p>
        <p v-else>
          Aucune image sélectionnée.
        </p>
      </div>
    </div>

    <!-- Affiche ce bloc si la liste des images est vide -->
    <div class="card" v-else>
      <p>Aucune image disponible.</p>
    </div>
  </div>
</template>