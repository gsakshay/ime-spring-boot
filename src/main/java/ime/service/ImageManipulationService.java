package ime.service;

import ime.model.MIME;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

@Service
public class ImageManipulationService {

  private final MIME mimeModel;
  private final ImageLoader imageLoader;
  private final ImageSaver imageSaver;

  @Autowired
  public ImageManipulationService(MIME mimeModel, ImageSaver imageSaver, ImageLoader imageLoader) {
    this.mimeModel = mimeModel;
    this.imageLoader = imageLoader;
    this.imageSaver = imageSaver;
  }

  private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
    File file = new File(System.getProperty("java.io.tmpdir") + "/" + multipartFile.getOriginalFilename());
    try (FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(multipartFile.getBytes());
    }
    return file;
  }

  public String loadImage(MultipartFile multipartFile) throws IOException {
    String imageName = UUID.randomUUID().toString();
    // Convert MultipartFile to File
    File file = convertMultipartFileToFile(multipartFile);
    try (InputStream inputStream = imageLoader.load(file)) {
      mimeModel.readData(inputStream, imageName);
    }
    return imageName;
  }

  public byte[] saveImage(String imageName, String destination) throws IOException {
    try (OutputStream outputStream = mimeModel.writeData(imageName)) {
      imageSaver.save(outputStream, destination);
      return ((java.io.ByteArrayOutputStream) outputStream).toByteArray();
    }
  }

  public String brighten(String imageName, int scale) {
    String resultName = imageName + "-brightened";
    mimeModel.brighten(scale, imageName, resultName);
    return resultName;
  }

  public String flip(String imageName, boolean isVertical) {
    String resultName = imageName + (isVertical ? "-vertical" : "-horizontal");
    if (isVertical) {
      mimeModel.verticalFlip(imageName, resultName);
    } else {
      mimeModel.horizontalFlip(imageName, resultName);
    }
    return resultName;
  }

  public String greyscale(String imageName, String component) {
    String resultName = imageName + "-greyscale-" + component;
    switch (component) {
      case "value-component":
        mimeModel.valueGreyscale(imageName, resultName);
        break;
      case "luma-component":
        mimeModel.lumaGreyscale(imageName, resultName);
        break;
      case "intensity-component":
        mimeModel.intensityGreyscale(imageName, resultName);
        break;
      case "red-component":
        mimeModel.redGreyscale(imageName, resultName);
        break;
      case "green-component":
        mimeModel.greenGreyscale(imageName, resultName);
        break;
      case "blue-component":
        mimeModel.blueGreyscale(imageName, resultName);
        break;
      default:
        throw new IllegalArgumentException("Invalid greyscale component");
    }
    return resultName;
  }

  public String[] rgbSplit(String imageName) {
    String redName = imageName + "-red";
    String greenName = imageName + "-green";
    String blueName = imageName + "-blue";
    mimeModel.rgbSplit(imageName, redName, greenName, blueName);
    return new String[]{redName, greenName, blueName};
  }

  public String rgbCombine(String redImage, String greenImage, String blueImage) {
    String resultName = "combined-" + UUID.randomUUID().toString();
    mimeModel.rgbCombine(redImage, greenImage, blueImage, resultName);
    return resultName;
  }

  public String blur(String imageName) {
    String resultName = imageName + "-blurred";
    mimeModel.blur(imageName, resultName);
    return resultName;
  }

  public String sharpen(String imageName) {
    String resultName = imageName + "-sharpened";
    mimeModel.sharpen(imageName, resultName);
    return resultName;
  }

  public String sepia(String imageName) {
    String resultName = imageName + "-sepia";
    mimeModel.sepia(imageName, resultName);
    return resultName;
  }

  public String dither(String imageName) {
    String resultName = imageName + "-dithered";
    mimeModel.dither(imageName, resultName);
    return resultName;
  }
}