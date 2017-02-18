package io.github.teamfractal.util;

import com.badlogic.gdx.Gdx;

public class Fonts {

    /**
     * This class exists to import TrueType fonts into the game and to convert them into BitmapFont objects using
     * the TTFont class that was ported over from DRTN's last project
     */

    public TTFont montserratRegular;
    public TTFont montserratLight;

    /**
     * Constructor that instantiates the internal TTFont objects using the TrueType font files located in the
     * game's source-tree
     */
    public Fonts() {
        montserratRegular = new TTFont(Gdx.files.internal("font/MontserratRegular.ttf"));
        montserratLight = new TTFont(Gdx.files.internal("font/MontserratLight.ttf"));
    }

}
