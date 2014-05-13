package com.timepath.math;

import java.util.Random;
import java.util.logging.Logger;

/**
 * @author TimePath
 */
public class DiceRoll {

    private static final Random r   = new Random();
    private static final Logger LOG = Logger.getLogger(DiceRoll.class.getName());

    private DiceRoll() {
    }

    public static int roll(int d) {
        return r.nextInt(d);
    }
}
