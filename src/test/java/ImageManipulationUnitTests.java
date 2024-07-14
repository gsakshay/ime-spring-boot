import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import ime.ImeApplication;
import ime.controller.ImageManipulationController;
import ime.model.MIME;
import ime.service.ImageLoader;
import ime.service.ImageManipulationService;
import ime.service.ImageSaver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ImeApplication.class)
@ComponentScan(basePackages = "com.your.package")
public class ImageManipulationUnitTests {

  private MockMvc mockMvc;

  @Mock
  private ImageManipulationService imageService;

  @Mock
  private MIME mimeModel;

  @Mock
  private ImageLoader imageLoader;

  @Mock
  private ImageSaver imageSaver;

  @InjectMocks
  private ImageManipulationController controller;

  @InjectMocks
  private ImageManipulationService realImageService;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  // Controller Tests

  @Test
  public void testLoadImage() throws Exception {
    MockMultipartFile file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());

    when(imageService.loadImage(any(MultipartFile.class))).thenReturn("test-image");

    mockMvc.perform(multipart("/api/load").file(file))
            .andExpect(status().isOk())
            .andExpect(content().string("Image loaded with name: test-image"));

    verify(imageService).loadImage(any(MultipartFile.class));
  }

  @Test
  public void testLoadImageError() throws Exception {
    MockMultipartFile file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());

    when(imageService.loadImage(any(MultipartFile.class))).thenThrow(new IOException("Error loading image"));

    mockMvc.perform(multipart("/api/load").file(file))
            .andExpect(status().isInternalServerError())
            .andExpect(content().string("An IO error occurred: Error loading image"));
  }

  @Test
  public void testSaveImage() throws Exception {
    when(imageService.saveImage(anyString(), anyString())).thenReturn("image data".getBytes());

    mockMvc.perform(get("/api/save")
                    .param("imageName", "test-image")
                    .param("destination", "test-destination"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.IMAGE_PNG))
            .andExpect(content().bytes("image data".getBytes()));

    verify(imageService).saveImage("test-image", "test-destination");
  }

  @Test
  public void testSaveImageError() throws Exception {
    when(imageService.saveImage(anyString(), anyString())).thenThrow(new IOException("Error saving image"));

    mockMvc.perform(get("/api/save")
                    .param("imageName", "test-image")
                    .param("destination", "test-destination"))
            .andExpect(status().isInternalServerError())
            .andExpect(content().string("An IO error occurred: Error saving image"));
  }

  @Test
  public void testBrightenImage() throws Exception {
    when(imageService.brighten(anyString(), anyInt())).thenReturn("brightened-image");

    mockMvc.perform(post("/api/brighten")
                    .param("imageName", "test-image")
                    .param("scale", "10"))
            .andExpect(status().isOk())
            .andExpect(content().string("Image brightened and saved as: brightened-image"));

    verify(imageService).brighten("test-image", 10);
  }

  @Test
  public void testFlipImage() throws Exception {
    when(imageService.flip(anyString(), anyBoolean())).thenReturn("flipped-image");

    mockMvc.perform(post("/api/flip")
                    .param("imageName", "test-image")
                    .param("orientation", "vertical"))
            .andExpect(status().isOk())
            .andExpect(content().string("Image flipped and saved as: flipped-image"));

    verify(imageService).flip("test-image", true);
  }

  @Test
  public void testGreyscaleImage() throws Exception {
    when(imageService.greyscale(anyString(), anyString())).thenReturn("greyscale-image");

    mockMvc.perform(post("/api/greyscale")
                    .param("imageName", "test-image")
                    .param("component", "value-component"))
            .andExpect(status().isOk())
            .andExpect(content().string("Greyscale applied and saved as: greyscale-image"));

    verify(imageService).greyscale("test-image", "value-component");
  }

  @Test
  public void testRgbSplitImage() throws Exception {
    when(imageService.rgbSplit(anyString())).thenReturn(new String[]{"red-image", "green-image", "blue-image"});

    mockMvc.perform(post("/api/rgb-split")
                    .param("imageName", "test-image"))
            .andExpect(status().isOk())
            .andExpect(content().string("RGB split completed. Red: red-image, Green: green-image, Blue: blue-image"));

    verify(imageService).rgbSplit("test-image");
  }

  @Test
  public void testRgbCombineImage() throws Exception {
    when(imageService.rgbCombine(anyString(), anyString(), anyString())).thenReturn("combined-image");

    mockMvc.perform(post("/api/rgb-combine")
                    .param("redImage", "red-image")
                    .param("greenImage", "green-image")
                    .param("blueImage", "blue-image"))
            .andExpect(status().isOk())
            .andExpect(content().string("RGB combine completed and saved as: combined-image"));

    verify(imageService).rgbCombine("red-image", "green-image", "blue-image");
  }

  @Test
  public void testBlurImage() throws Exception {
    when(imageService.blur(anyString())).thenReturn("blurred-image");

    mockMvc.perform(post("/api/blur")
                    .param("imageName", "test-image"))
            .andExpect(status().isOk())
            .andExpect(content().string("Image blurred and saved as: blurred-image"));

    verify(imageService).blur("test-image");
  }

  @Test
  public void testSharpenImage() throws Exception {
    when(imageService.sharpen(anyString())).thenReturn("sharpened-image");

    mockMvc.perform(post("/api/sharpen")
                    .param("imageName", "test-image"))
            .andExpect(status().isOk())
            .andExpect(content().string("Image sharpened and saved as: sharpened-image"));

    verify(imageService).sharpen("test-image");
  }

  @Test
  public void testSepiaImage() throws Exception {
    when(imageService.sepia(anyString())).thenReturn("sepia-image");

    mockMvc.perform(post("/api/sepia")
                    .param("imageName", "test-image"))
            .andExpect(status().isOk())
            .andExpect(content().string("Sepia filter applied and saved as: sepia-image"));

    verify(imageService).sepia("test-image");
  }

  @Test
  public void testDitherImage() throws Exception {
    when(imageService.dither(anyString())).thenReturn("dithered-image");

    mockMvc.perform(post("/api/dither")
                    .param("imageName", "test-image"))
            .andExpect(status().isOk())
            .andExpect(content().string("Image dithered and saved as: dithered-image"));

    verify(imageService).dither("test-image");
  }

  // Service Tests

  @Test
  public void testSaveImageService() throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    when(mimeModel.writeData(anyString())).thenReturn(outputStream);

    byte[] result = realImageService.saveImage("test-image", "test-destination");

    assertNotNull(result);
    verify(imageSaver).save(any(OutputStream.class), eq("test-destination"));
  }

  @Test
  public void testBrightenService() {
    String result = realImageService.brighten("test-image", 10);

    assertEquals("test-image-brightened", result);
    verify(mimeModel).brighten(10, "test-image", "test-image-brightened");
  }

  @Test
  public void testFlipService() {
    String result = realImageService.flip("test-image", true);

    assertEquals("test-image-vertical", result);
    verify(mimeModel).verticalFlip("test-image", "test-image-vertical");

    result = realImageService.flip("test-image", false);

    assertEquals("test-image-horizontal", result);
    verify(mimeModel).horizontalFlip("test-image", "test-image-horizontal");
  }

  @Test
  public void testGreyscaleService() {
    String result = realImageService.greyscale("test-image", "value-component");

    assertEquals("test-image-greyscale-value-component", result);
    verify(mimeModel).valueGreyscale("test-image", "test-image-greyscale-value-component");

    result = realImageService.greyscale("test-image", "luma-component");
    verify(mimeModel).lumaGreyscale("test-image", "test-image-greyscale-luma-component");

    result = realImageService.greyscale("test-image", "intensity-component");
    verify(mimeModel).intensityGreyscale("test-image", "test-image-greyscale-intensity-component");

    result = realImageService.greyscale("test-image", "red-component");
    verify(mimeModel).redGreyscale("test-image", "test-image-greyscale-red-component");

    result = realImageService.greyscale("test-image", "green-component");
    verify(mimeModel).greenGreyscale("test-image", "test-image-greyscale-green-component");

    result = realImageService.greyscale("test-image", "blue-component");
    verify(mimeModel).blueGreyscale("test-image", "test-image-greyscale-blue-component");
  }

  @Test
  public void testRgbSplitService() {
    String[] result = realImageService.rgbSplit("test-image");

    assertArrayEquals(new String[]{"test-image-red", "test-image-green", "test-image-blue"}, result);
    verify(mimeModel).rgbSplit("test-image", "test-image-red", "test-image-green", "test-image-blue");
  }

  @Test
  public void testRgbCombineService() {
    String result = realImageService.rgbCombine("red-image", "green-image", "blue-image");

    assertTrue(result.startsWith("combined-"));
    verify(mimeModel).rgbCombine("red-image", "green-image", "blue-image", result);
  }

  @Test
  public void testBlurService() {
    String result = realImageService.blur("test-image");

    assertEquals("test-image-blurred", result);
    verify(mimeModel).blur("test-image", "test-image-blurred");
  }

  @Test
  public void testSharpenService() {
    String result = realImageService.sharpen("test-image");

    assertEquals("test-image-sharpened", result);
    verify(mimeModel).sharpen("test-image", "test-image-sharpened");
  }

  @Test
  public void testSepiaService() {
    String result = realImageService.sepia("test-image");

    assertEquals("test-image-sepia", result);
    verify(mimeModel).sepia("test-image", "test-image-sepia");
  }

  @Test
  public void testDitherService() {
    String result = realImageService.dither("test-image");

    assertEquals("test-image-dithered", result);
    verify(mimeModel).dither("test-image", "test-image-dithered");
  }
}