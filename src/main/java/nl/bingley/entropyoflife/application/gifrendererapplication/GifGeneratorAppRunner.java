package nl.bingley.entropyoflife.application.gifrendererapplication;

import com.aparapi.Range;
import com.madgag.gif.fmsware.AnimatedGifEncoder;
import nl.bingley.entropyoflife.application.Runner;
import nl.bingley.entropyoflife.config.properties.GifGeneratorProperties;
import nl.bingley.entropyoflife.config.properties.UniverseProperties;
import nl.bingley.entropyoflife.kernels.UniverseImageRenderer;
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
    private final Range kernelRange;
    private final UniverseImageRenderer imageRenderer;

    private final BufferedImage bufferedImage;
    private final int[] imageData;

    private final UniverseProperties uniProperties;
    private final GifGeneratorProperties gifProperties;

    public GifGeneratorAppRunner(Universe universe, UniverseKernel universeKernel, UniverseImageRenderer imageRenderer,
                                 UniverseProperties uniProperties, GifGeneratorProperties gifProperties) {
        this.universe = universe;
        this.universeKernel = universeKernel;
        this.imageRenderer = imageRenderer;
        this.uniProperties = uniProperties;
        this.gifProperties = gifProperties;
        int size = gifProperties.getCellSize() * uniProperties.getSize();
        bufferedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        imageData = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
        imageRenderer.render(imageData, size, gifProperties.getCellSize(), 0, 0);
        kernelRange = Range.create2D(uniProperties.getSize(), uniProperties.getSize());
    }

    @Override
    public void run() {
        AnimatedGifEncoder animatedGifEncoder = new AnimatedGifEncoder();
        System.out.println("Starting gif generation");
        String fileName = gifProperties.getSaveLocation() + uniProperties.getRuleset() + "_" +
                new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + ".gif";
        animatedGifEncoder.setRepeat(99);
        animatedGifEncoder.start(fileName);
        animatedGifEncoder.setDelay(1000 / gifProperties.getGenPerSec());
        animatedGifEncoder.addFrame(bufferedImage);
        for (int i = 0; i < gifProperties.getEndGeneration(); i++) {
            updateImage();
            animatedGifEncoder.addFrame(bufferedImage);
            if (i % (gifProperties.getEndGeneration() / 20) == 0) {
                System.out.println("Generating: " + (int) ((float) i / gifProperties.getEndGeneration() * 100) + "%");
            }
        }
        animatedGifEncoder.finish();
        System.out.println("Finished gif generation");
    }

    private void updateImage() {
        universe.incrementGeneration();

        universeKernel.put(universe.energyMatrix);
        universeKernel.execute(kernelRange, 2);
        universeKernel.get(universe.energyMatrix);

        imageRenderer.render(imageData, gifProperties.getCellSize() * uniProperties.getSize(), 8, 0, 0);
    }
}
