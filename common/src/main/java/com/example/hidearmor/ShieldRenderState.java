package com.example.hidearmor;

/**
 * Thread-scoped flag marking that the shield currently being submitted belongs
 * to the local player.
 *
 * Lives outside the mixins because a mixin may not declare non-private static
 * fields; the mixins only read/write it through this class.
 */
public final class ShieldRenderState {

    private static final ThreadLocal<Boolean> SUBMITTING_LOCAL_SHIELD = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private ShieldRenderState() {
    }

    public static void beginShieldSubmit() {
        SUBMITTING_LOCAL_SHIELD.set(Boolean.TRUE);
    }

    public static void endShieldSubmit() {
        SUBMITTING_LOCAL_SHIELD.set(Boolean.FALSE);
    }

    public static boolean isSubmittingLocalShield() {
        return Boolean.TRUE.equals(SUBMITTING_LOCAL_SHIELD.get());
    }
}
