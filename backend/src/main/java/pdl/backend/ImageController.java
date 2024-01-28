package pdl.backend;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class ImageController {

  @Autowired
  private ObjectMapper mapper;

  private final ImageDao imageDao;

  @Autowired
  public ImageController(ImageDao imageDao) {
    this.imageDao = imageDao;
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<?> getImage(@PathVariable("id") long id) {
    Optional<Image> imageOptional = imageDao.retrieve(id); // On recupère l'image associé à l'ID depuis le DAO
    if (imageOptional.isPresent()) { // On check si l'image existe
        Image image = imageOptional.get(); // On recupère l'objet image depuis optional
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image.getData()); // Code 200 avec l'image
    } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Code 404 si pas trouvé
    }
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteImage(@PathVariable("id") long id) {
    Optional<Image> imageOptional = imageDao.retrieve(id);
    if (imageOptional.isPresent()) {
        Image image = imageOptional.get(); // Supprimer l'image à partir du DAO
        imageDao.delete(image);
        return new ResponseEntity<>(HttpStatus.OK); // Renvoie Code 200 si bien supprimé
    } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Code 404 si pas trouvé
    }
  }

  @RequestMapping(value = "/images", method = RequestMethod.POST)
  public ResponseEntity<?> addImage(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
      try {
        if (!file.getContentType().endsWith(".jpg") && !file.getContentType().endsWith(".jpeg")) {
          return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
        byte[] fileContent = file.getBytes(); // Lire le contenu du fichier de la requête multipart
        Image newImage = new Image(file.getOriginalFilename(), fileContent); // Créer une nouvelle image à partir du contenu du fichier
        imageDao.create(newImage);
        return new ResponseEntity<>(HttpStatus.OK); // Code 201 si l'ajout réussi
      } catch (IOException e) {
        e.printStackTrace();
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }

  @RequestMapping(value = "/images", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
  @ResponseBody
  public ArrayNode getImageList() {
    ArrayNode nodes = mapper.createArrayNode(); // Récupérer la liste des images à partir du DAO
    List<Image> imageList = imageDao.retrieveAll();

    for (Image image : imageList) { // Ajouter les détails des images à l'objet ArrayNode
        ObjectNode node = mapper.createObjectNode();
        node.put("id", image.getId());
        node.put("name", image.getName());
        nodes.add(node);
    }

    return nodes;
  }

}
