import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import ime.ImeApplication;
import ime.model.MIME;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ImeApplication.class)
@AutoConfigureMockMvc
public class ImageManipulationIntegrationTests {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MIME mimeModel;

  @Test
  public void testLoadImageError() throws Exception {
    MockMultipartFile file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());

    doThrow(new IOException("Error loading image")).when(mimeModel).readData(any(InputStream.class), anyString());

    mockMvc.perform(multipart("/api/load").file(file))
            .andExpect(status().isInternalServerError());
  }

  @Test
  public void testSaveImageError() throws Exception {
    when(mimeModel.writeData(anyString())).thenThrow(new IOException("Error saving image"));

    mockMvc.perform(get("/api/save")
                    .param("imageName", "test-image")
                    .param("destination", "test-destination"))
            .andExpect(status().isInternalServerError())
            .andExpect(content().string("An IO error occurred: Error saving image"));
  }

  @Test
  public void testBrightenImage() throws Exception {
    mockMvc.perform(post("/api/brighten")
                    .param("imageName", "test-image")
                    .param("scale", "10"))
            .andExpect(status().isOk())
            .andExpect(content().string("Image brightened and saved as: test-image-brightened"));

    verify(mimeModel).brighten(10, "test-image", "test-image-brightened");
  }

  @Test
  public void testFlipImage() throws Exception {
    mockMvc.perform(post("/api/flip")
                    .param("imageName", "test-image")
                    .param("orientation", "vertical"))
            .andExpect(status().isOk())
            .andExpect(content().string("Image flipped and saved as: test-image-vertical"));

    verify(mimeModel).verticalFlip("test-image", "test-image-vertical");

    mockMvc.perform(post("/api/flip")
                    .param("imageName", "test-image")
                    .param("orientation", "horizontal"))
            .andExpect(status().isOk())
            .andExpect(content().string("Image flipped and saved as: test-image-horizontal"));

    verify(mimeModel).horizontalFlip("test-image", "test-image-horizontal");
  }

  @Test
  public void testGreyscaleImage() throws Exception {
    mockMvc.perform(post("/api/greyscale")
                    .param("imageName", "test-image")
                    .param("component", "value-component"))
            .andExpect(status().isOk())
            .andExpect(content().string("Greyscale applied and saved as: test-image-greyscale-value-component"));

    verify(mimeModel).valueGreyscale("test-image", "test-image-greyscale-value-component");

    // Test other greyscale components
    String[] components = {"luma-component", "intensity-component", "red-component", "green-component", "blue-component"};
    for (String component : components) {
      mockMvc.perform(post("/api/greyscale")
                      .param("imageName", "test-image")
                      .param("component", component))
              .andExpect(status().isOk())
              .andExpect(content().string("Greyscale applied and saved as: test-image-greyscale-" + component));
    }
  }

  @Test
  public void testRgbSplitImage() throws Exception {
    mockMvc.perform(post("/api/rgb-split")
                    .param("imageName", "test-image"))
            .andExpect(status().isOk())
            .andExpect(content().string("RGB split completed. Red: test-image-red, Green: test-image-green, Blue: test-image-blue"));

    verify(mimeModel).rgbSplit("test-image", "test-image-red", "test-image-green", "test-image-blue");
  }

  @Test
  public void testRgbCombineImage() throws Exception {
    mockMvc.perform(post("/api/rgb-combine")
                    .param("redImage", "red-image")
                    .param("greenImage", "green-image")
                    .param("blueImage", "blue-image"))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("RGB combine completed and saved as: combined-")));

    verify(mimeModel).rgbCombine(eq("red-image"), eq("green-image"), eq("blue-image"), anyString());
  }

  @Test
  public void testBlurImage() throws Exception {
    mockMvc.perform(post("/api/blur")
                    .param("imageName", "test-image"))
            .andExpect(status().isOk())
            .andExpect(content().string("Image blurred and saved as: test-image-blurred"));

    verify(mimeModel).blur("test-image", "test-image-blurred");
  }

  @Test
  public void testSharpenImage() throws Exception {
    mockMvc.perform(post("/api/sharpen")
                    .param("imageName", "test-image"))
            .andExpect(status().isOk())
            .andExpect(content().string("Image sharpened and saved as: test-image-sharpened"));

    verify(mimeModel).sharpen("test-image", "test-image-sharpened");
  }

  @Test
  public void testSepiaImage() throws Exception {
    mockMvc.perform(post("/api/sepia")
                    .param("imageName", "test-image"))
            .andExpect(status().isOk())
            .andExpect(content().string("Sepia filter applied and saved as: test-image-sepia"));

    verify(mimeModel).sepia("test-image", "test-image-sepia");
  }

  @Test
  public void testDitherImage() throws Exception {
    mockMvc.perform(post("/api/dither")
                    .param("imageName", "test-image"))
            .andExpect(status().isOk())
            .andExpect(content().string("Image dithered and saved as: test-image-dithered"));

    verify(mimeModel).dither("test-image", "test-image-dithered");
  }

  @Test
  public void testInvalidGreyscaleComponent() throws Exception {
    mockMvc.perform(post("/api/greyscale")
                    .param("imageName", "test-image")
                    .param("component", "invalid-component"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Invalid argument: Invalid greyscale component"));
  }
}