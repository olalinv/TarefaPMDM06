package com.robottitto.gpmdm06;

import com.badlogic.gdx.Game;
import com.robottitto.gpmdm06.screen.Screen;

public class PMDM06 extends Game {

    @Override
    public void create() {
        setScreen(new Screen());
    }

}
