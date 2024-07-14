package ime.controller;

import ime.service.ImageManipulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ImageManipulationController {

  private final ImageManipulationService imageService;

  @Autowired
  public ImageManipulationController(ImageManipulationService imageService) {
    this.imageService = imageService;
  }

  @ExceptionHandler(IOException.class)
  public ResponseEntity<String> handleIOException(IOException e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("An IO error occurred: " + e.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Invalid argument: " + e.getMessage());
  }

  @GetMapping("/")
  public ResponseEntity<String> index() {
    Resource resource = new ClassPathResource("static/api-docs.html");

    if (!resource.exists()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body("Documentation file not found");
    }

    try (InputStream inputStream = resource.getInputStream();
         InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
         BufferedReader reader = new BufferedReader(inputStreamReader)) {

      String docContent = reader.lines().collect(Collectors.joining("\n"));
      return ResponseEntity.ok()
              .contentType(MediaType.TEXT_HTML)
              .body(docContent);
    } catch (IOException e) {
      // Log the exception for debugging purposes - To be done better
      // e.printStackTrace(); // Replace with proper logging in production
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("Error reading documentation file: " + e.getMessage());
    }
  }

  @PostMapping("/load")
  public ResponseEntity<String> loadImage(@RequestParam("file") MultipartFile file) throws IOException {
    String imageName = imageService.loadImage(file);
    return ResponseEntity.ok("Image loaded with name: " + imageName);
  }

  @GetMapping("/save")
  public ResponseEntity<byte[]> saveImage(@RequestParam("imageName") String imageName,
                                          @RequestParam("destination") String destination) throws IOException {
    byte[] imageData = imageService.saveImage(imageName, destination);
    return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(imageData);
  }

  @PostMapping("/brighten")
  public ResponseEntity<String> brightenImage(@RequestParam("imageName") String imageName,
                                              @RequestParam("scale") int scale) {
    String resultName = imageService.brighten(imageName, scale);
    return ResponseEntity.ok("Image brightened and saved as: " + resultName);
  }

  @PostMapping("/flip")
  public ResponseEntity<String> flipImage(@RequestParam("imageName") String imageName,
                                          @RequestParam("orientation") String orientation) {
    boolean isVertical = "vertical".equalsIgnoreCase(orientation);
    String resultName = imageService.flip(imageName, isVertical);
    return ResponseEntity.ok("Image flipped and saved as: " + resultName);
  }

  @PostMapping("/greyscale")
  public ResponseEntity<String> greyscaleImage(@RequestParam("imageName") String imageName,
                                               @RequestParam("component") String component) {
    String resultName = imageService.greyscale(imageName, component);
    return ResponseEntity.ok("Greyscale applied and saved as: " + resultName);
  }

  @PostMapping("/rgb-split")
  public ResponseEntity<String> rgbSplitImage(@RequestParam("imageName") String imageName) {
    String[] resultNames = imageService.rgbSplit(imageName);
    return ResponseEntity.ok("RGB split completed. Red: " + resultNames[0] +
            ", Green: " + resultNames[1] + ", Blue: " + resultNames[2]);
  }

  @PostMapping("/rgb-combine")
  public ResponseEntity<String> rgbCombineImage(@RequestParam("redImage") String redImage,
                                                @RequestParam("greenImage") String greenImage,
                                                @RequestParam("blueImage") String blueImage) {
    String resultName = imageService.rgbCombine(redImage, greenImage, blueImage);
    return ResponseEntity.ok("RGB combine completed and saved as: " + resultName);
  }

  @PostMapping("/blur")
  public ResponseEntity<String> blurImage(@RequestParam("imageName") String imageName) {
    String resultName = imageService.blur(imageName);
    return ResponseEntity.ok("Image blurred and saved as: " + resultName);
  }

  @PostMapping("/sharpen")
  public ResponseEntity<String> sharpenImage(@RequestParam("imageName") String imageName) {
    String resultName = imageService.sharpen(imageName);
    return ResponseEntity.ok("Image sharpened and saved as: " + resultName);
  }

  @PostMapping("/sepia")
  public ResponseEntity<String> sepiaImage(@RequestParam("imageName") String imageName) {
    String resultName = imageService.sepia(imageName);
    return ResponseEntity.ok("Sepia filter applied and saved as: " + resultName);
  }

  @PostMapping("/dither")
  public ResponseEntity<String> ditherImage(@RequestParam("imageName") String imageName) {
    String resultName = imageService.dither(imageName);
    return ResponseEntity.ok("Image dithered and saved as: " + resultName);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleException(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("An error occurred: " + e.getMessage());
  }
}