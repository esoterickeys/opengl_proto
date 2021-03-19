package audio;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

import java.io.IOException;

public class Test {

    public static void main(String[] args) throws IOException, InterruptedException {
        AudioMaster am = new AudioMaster();
        am.init();
        am.setListenerData(0, 0, 0);
        AL10.alDistanceModel(AL11.AL_INVERSE_DISTANCE_CLAMPED);

        int buffer = am.loadSound("bounce");
        Source source = new Source();
        source.setLooping(true);
        source.play(buffer);

        float xPos = 8;
        source.setPosition(xPos, 0, 0);

        char c = ' ';

        while (c != 'q') {
            xPos -= 0.03f;
            source.setPosition(xPos, 0, 0);
            System.out.println(xPos);
            Thread.sleep(10);
        }

        source.delete();
        am.cleanUp();

        System.exit(0);
    }
}
