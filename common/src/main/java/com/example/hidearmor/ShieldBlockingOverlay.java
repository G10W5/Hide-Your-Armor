package com.example.hidearmor;

/**
 * Tracks when the local player started blocking with a shield,
 * so the hotbar overlay can compute how long they've been blocking.
 */
public final class ShieldBlockingOverlay {

    private ShieldBlockingOverlay() {}

    // -1 means not blocking
    private static long blockingStartMs = -1L;

    /** Call every client tick to update blocking state. */
    public static void tick(boolean isBlocking) {
        if (isBlocking) {
            if (blockingStartMs < 0) {
                blockingStartMs = System.currentTimeMillis();
            }
        } else {
            blockingStartMs = -1L;
        }
    }

    /**
     * Returns a 0..1 progress for the sweep animation (cycles every 1.5 s),
     * or -1 if the player is not blocking.
     */
    public static float getProgress() {
        if (blockingStartMs < 0) return -1f;
        long elapsed = System.currentTimeMillis() - blockingStartMs;
        return (elapsed % 1500L) / 1500f;
    }

    public static boolean isBlocking() {
        return blockingStartMs >= 0;
    }
}
