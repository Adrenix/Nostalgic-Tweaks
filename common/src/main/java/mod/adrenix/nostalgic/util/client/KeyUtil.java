package mod.adrenix.nostalgic.util.client;

import mod.adrenix.nostalgic.client.config.gui.screen.SettingsScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Utility class for the mod's available key mappings.
 */

public abstract class KeyUtil
{
    /* Key Press Helpers */

    /**
     * @return Checks if the shift, ctrl, or alt key is held down.
     */
    public static boolean isModifierDown() { return Screen.hasShiftDown() || Screen.hasControlDown() || Screen.hasAltDown(); }

    /**
     * A key combination for paging right.
     * @param key A pressed key.
     * @return Whether the ctrl or alt key is held down and the right arrow key is pressed.
     */
    public static boolean isGoingRight(int key) { return (Screen.hasControlDown() || Screen.hasAltDown()) && key == GLFW.GLFW_KEY_RIGHT; }

    /**
     * A key combination for paging left.
     * @param key A pressed key.
     * @return Whether the ctrl or alt key is held down and the left arrow key is pressed.
     */
    public static boolean isGoingLeft(int key) { return (Screen.hasControlDown() || Screen.hasAltDown()) && key == GLFW.GLFW_KEY_LEFT; }

    /**
     * A key combination for searching.
     * @param key A pressed key.
     * @return Whether the ctrl key is held down and the F key is pressed.
     */
    public static boolean isSearching(int key) { return Screen.hasControlDown() && key == GLFW.GLFW_KEY_F; }

    /**
     * A key combination for saving.
     * @param key A pressed key.
     * @return Whether the ctrl key is held down and the S key is pressed.
     */
    public static boolean isSaving(int key) { return Screen.hasControlDown() && key == GLFW.GLFW_KEY_S; }

    /**
     * A key combination for selecting everything.
     * @param key A pressed key.
     * @return Whether the ctrl key is held down and the A key is pressed.
     */
    public static boolean isSelectAll(int key) { return Screen.hasControlDown() && key == GLFW.GLFW_KEY_A; }

    /**
     * This is accepted whether the number pad enter key is pressed or the keyboard enter key is pressed.
     * @param key A pressed key.
     * @return Whether the number pad enter key or keyboard enter key is pressed.
     */
    public static boolean isEnter(int key) { return key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER; }

    /**
     * Checks if the Tab key is pressed.
     * @param key A pressed key.
     * @return Whether the Tab key was pressed.
     */
    public static boolean isTab(int key) { return key == GLFW.GLFW_KEY_TAB; }

    /**
     * Checks if the Esc key is pressed.
     * @param key A pressed key.
     * @return Whether the Esc key was pressed.
     */
    public static boolean isEsc(int key) { return key == GLFW.GLFW_KEY_ESCAPE; }

    /**
     * This cache prevents the need to cycle through all the key mappings.
     * Extremely useful for when there are a lot of key mappings.
     */
    private static final Map<String, KeyMapping> MAPPING_CACHE = new HashMap<>();

    /**
     * Checks if the fog key is currently being held down.
     */
    public static boolean isFogDown = false;

    /* Key Helpers */

    /**
     * Finds a key mapping based on the given <code>langKey</code>
     * @param langKey The key to search for in the game's key mappings.
     * @return A key mapping if one was found.
     */
    public static Optional<KeyMapping> find(String langKey)
    {
        KeyMapping cache = MAPPING_CACHE.get(langKey);

        if (cache != null)
            return Optional.of(cache);

        KeyMapping[] allMappings = Minecraft.getInstance().options.keyMappings;

        for (KeyMapping keyMapping : allMappings)
        {
            if (keyMapping.getName().equals(langKey))
            {
                MAPPING_CACHE.put(langKey, keyMapping);
                return Optional.of(keyMapping);
            }
        }

        return Optional.empty();
    }

    /**
     * Checks if the given mapping conflicts with another key mapping.
     * @param mapping A key mapping instance.
     * @return Whether the key mapping conflicts with another key mapping.
     */
    public static boolean isMappingConflict(KeyMapping mapping)
    {
        if(!mapping.isUnbound())
        {
            KeyMapping[] allMappings = Minecraft.getInstance().options.keyMappings;

            for (KeyMapping keyMapping : allMappings)
            {
                if (keyMapping != mapping && mapping.same(keyMapping))
                    return true;
            }
        }

        return false;
    }

    /**
     * Opens the mod's configuration screen if the configuration key mapping is being held down.
     * @param openConfig The key mapping to check.
     */
    public static void onOpenConfig(KeyMapping openConfig)
    {
        if (openConfig.isDown() && Minecraft.getInstance().screen == null)
            Minecraft.getInstance().setScreen(new SettingsScreen(null, true));
    }

    /**
     * Toggles the game's fog rendering distance if the fog key mapping is being held down.
     * @param toggleFog The key mapping to check.
     */
    public static void onToggleFog(KeyMapping toggleFog)
    {
        boolean isReleased = false;

        if (!isFogDown && toggleFog.isDown())
            isFogDown = true;
        else if (isFogDown && !toggleFog.isDown())
        {
            isFogDown = false;
            isReleased = true;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (isReleased && minecraft.level != null)
        {
            int distance = minecraft.options.renderDistance().get();

            if (distance >= 16)
                minecraft.options.renderDistance().set(8);
            else if (distance >= 8)
                minecraft.options.renderDistance().set(4);
            else if (distance >= 4)
                minecraft.options.renderDistance().set(2);
            else if (distance >= 2)
                minecraft.options.renderDistance().set(16);

            minecraft.levelRenderer.needsUpdate();
            minecraft.options.save();
        }
    }
}
