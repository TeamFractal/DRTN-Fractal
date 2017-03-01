package io.github.teamfractal.entity;

import io.github.teamfractal.screens.GameScreen;
import io.github.teamfractal.screens.Overlay;

/**
 * Created by jack on 01/03/2017.
 */
public interface Effect {

    void constructOverlay(final GameScreen gameScreen);

    void executeRunnable();

    Overlay overlay();


}
