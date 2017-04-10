package com.par.projectaugmentedreality;

/**
 * Created by Maick on 4/4/2017.
 */
import com.vuforia.State;

public interface AppRendererControl {

    // This method has to be implemented by the Renderer class which handles the content rendering
    // of the sample, this one is called from SampleAppRendering class for each view inside a loop
    void renderFrame(State state, float[] projectionMatrix);

}
