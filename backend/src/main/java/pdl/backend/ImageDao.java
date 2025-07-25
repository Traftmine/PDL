package pdl.backend;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

@Repository
public class ImageDao implements Dao<Image> {

  private final Map<Long, Image> images = new HashMap<>();

  public ImageDao() {
    // placez une image test.jpg dans le dossier "src/main/resources" du projet
    final ClassPathResource imgFile = new ClassPathResource("test.jpg");
    byte[] fileContent;
    try {
      fileContent = Files.readAllBytes(imgFile.getFile().toPath());
      Image img = new Image("test.jpg", fileContent); // modify here for image name
      images.put(img.getId(), img);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Optional<Image> retrieve(final long id) {
    return Optional.of(images.get(id));
  }

  @Override
  public List<Image> retrieveAll() {

    ArrayList<Image> liste = new ArrayList<>();

    for (long index = 0; index < images.size(); index++) {
      liste.add(images.get(index));
    }
    return liste;
  }

  @Override
  public void create(final Image img) {
    images.put(img.getId(),img);
  }

  @Override
  public void update(final Image img, final String[] params) {
    // Not used
  }

  @Override
  public void delete(final Image img) {
    images.remove(img.getId(),img);
  }
}
