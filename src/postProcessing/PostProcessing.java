package postProcessing;

import bloom.BrightFilter;
import bloom.CombineFilter;
import gaussianBlur.HorizontalBlur;
import gaussianBlur.VerticalBlur;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import models.RawModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;

public class PostProcessing {

    private static final float[] POSITIONS = {-1, 1, -1, -1, 1, 1, 1, -1};
    private static RawModel quad;
    private static ContrastChanger contrastChanger;
    private static HorizontalBlur hBlur;
    private static VerticalBlur vBlur;
    private static BrightFilter brightFilter;
    private static CombineFilter combineFilter;

    public static void init(Loader loader) {
        quad = loader.loadToVAO(POSITIONS, 2);
        contrastChanger = new ContrastChanger();
        hBlur = new HorizontalBlur(DisplayManager.getWidth() / 5, DisplayManager.getHeight() / 5);
        vBlur = new VerticalBlur(DisplayManager.getWidth() / 5, DisplayManager.getHeight() / 5);

        brightFilter = new BrightFilter(DisplayManager.getWidth() / 2, DisplayManager.getHeight() / 2);
        combineFilter = new CombineFilter();
    }

    public static void doPostProcessing(int colourTexture, int brightTexture) {
        start();

        //brightFilter.render(colourTexture);
        hBlur.render(brightTexture);
        vBlur.render(hBlur.getOutputTexture());

        combineFilter.render(colourTexture, vBlur.getOutputTexture());
        //contrastChanger.render(combineFilter.getOutputTexture());

        end();
    }

    public static void cleanUp() {
        contrastChanger.cleanUp();
        hBlur.cleanUp();
        vBlur.cleanUp();
        brightFilter.cleanUp();
        combineFilter.cleanUp();
    }

    private static void start() {
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    private static void end() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }


}
