package top.ilov.mcmods.tc.utils;

import org.joml.Vector2i;

public final class ScrollWheelHandler {

    private double accumulatedScrollX;
    private double accumulatedScrollY;

    public Vector2i onMouseScroll(double scrollDeltaX, double scrollDeltaY) {

        if (accumulatedScrollX != 0.0D
                && Math.signum(scrollDeltaX) != Math.signum(accumulatedScrollX)) {
            accumulatedScrollX = 0.0D;
        }

        if (accumulatedScrollY != 0.0D
                && Math.signum(scrollDeltaY) != Math.signum(accumulatedScrollY)) {
            accumulatedScrollY = 0.0D;
        }

        accumulatedScrollX += scrollDeltaX;
        accumulatedScrollY += scrollDeltaY;

        int scrollX = (int) accumulatedScrollX;
        int scrollY = (int) accumulatedScrollY;
        if (scrollX == 0 && scrollY == 0) {
            return new Vector2i();
        }

        accumulatedScrollX -= scrollX;
        accumulatedScrollY -= scrollY;

        return new Vector2i(scrollX, scrollY);

    }
}