package mod.adrenix.nostalgic.client.gui.overlay;

/**
 * This is a functional interface that provides custom key pressing instructions that will be performed when keyboard
 * input is accepted by an overlay.
 */
public interface OverlayKeyboard
{
    /**
     * Evaluates this predicate on the given argument.
     *
     * @param overlay   The {@link Overlay} instance.
     * @param keyCode   The key code that was pressed.
     * @param scanCode  A key scan code.
     * @param modifiers Key code modifiers.
     * @return Whether this handled the key press event.
     */
    boolean test(Overlay overlay, int keyCode, int scanCode, int modifiers);
}
