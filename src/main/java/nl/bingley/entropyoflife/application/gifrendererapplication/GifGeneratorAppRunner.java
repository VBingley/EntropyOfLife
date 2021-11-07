package nl.bingley.entropyoflife.application.gifrendererapplication;

import com.aparapi.Range;
import com.madgag.gif.fmsware.AnimatedGifEncoder;
import nl.bingley.entropyoflife.application.Runner;
import nl.bingley.entropyoflife.config.properties.GifGeneratorProperties;
import nl.bingley.entropyoflife.config.properties.UniverseProperties;
import nl.bingley.entropyoflife.kernels.RendererKernel;
import nl.bingley.entropyoflife.kernels.UniverseKernel;
import nl.bingley.entropyoflife.models.Universe;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class GifGeneratorAppRunner implements Runner {

    private final Universe universe;
    private final UniverseKernel universeKernel;
    private final Range universeKernelRange;

    private final RendererKernel rendererKernel;
    private final Range renderKernelRange;
    private final BufferedImage bufferedImage;
    private final int[] imageData;

    private final UniverseProperties uniProperties;
    private final GifGeneratorProperties gifProperties;

    public GifGeneratorAppRunner(Universe universe, UniverseKernel universeKernel, RendererKernel rendererKernel,
                                 UniverseProperties uniProperties, GifGeneratorProperties gifProperties) {
        this.universe = universe;
        this.universeKernel = universeKernel;
        universeKernelRange = Range.create2D(uniProperties.getSize(), uniProperties.getSize());

        this.uniProperties = uniProperties;
        this.gifProperties = gifProperties;

        int imageSize = gifProperties.getCellSize() * uniProperties.getSize();
        bufferedImage = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB);
        imageData = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
        this.rendererKernel = rendererKernel;
        this.rendererKernel.setImageDataSize(imageData, imageSize);
        this.rendererKernel.setCellSize(gifProperties.getCellSize());
        this.rendererKernel.setTranslateX(0);
        this.rendererKernel.setTranslateY(0);
        renderKernelRange = Range.create(imageData.length);
        renderImage();
    }

    @Override
    public void run() {
        System.out.println("Starting gif generation");

        AnimatedGifEncoder animatedGifEncoder = new AnimatedGifEncoder();
        String fileName = gifProperties.getSaveLocation() + uniProperties.getRuleset() + "_" +
                new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + ".gif";
        animatedGifEncoder.setRepeat(99);
        animatedGifEncoder.start(fileName);
        animatedGifEncoder.setDelay(1000 / gifProperties.getGenPerSec());
        animatedGifEncoder.addFrame(bufferedImage);

        for (int i = 0; i < gifProperties.getEndGeneration(); i++) {
            incrementGeneration();
            renderImage();
            animatedGifEncoder.addFrame(bufferedImage);
            if (i % (gifProperties.getEndGeneration() / 20) == 0) {
                System.out.println("Generating: " + (int) ((float) i / gifProperties.getEndGeneration() * 100) + "%");
            }
        }
        animatedGifEncoder.finish();

        System.out.println("Finished gif generation");
    }

    private void incrementGeneration() {
        universe.incrementGeneration();

        universeKernel.put(universe.energyMatrix);
        universeKernel.execute(universeKernelRange, 2);
        universeKernel.get(universe.energyMatrix);
    }

    private void renderImage() {
        rendererKernel.put(universe.energyMatrix);
        rendererKernel.execute(renderKernelRange);
        rendererKernel.get(imageData);
    }
}
