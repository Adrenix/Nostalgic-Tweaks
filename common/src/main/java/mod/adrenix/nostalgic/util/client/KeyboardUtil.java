package mod.adrenix.nostalgic.util.client;

import mod.adrenix.nostalgic.util.common.lang.Translation;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Utility class for the keyboard and the mod's key mappings.
 */
public abstract class KeyboardUtil
{
    /* Key Press Helpers */

    /**
     * @return Checks if the shift, ctrl, or alt key is held down.
     */
    public static boolean isModifierDown()
    {
        return Screen.hasShiftDown() || Screen.hasControlDown() || Screen.hasAltDown();
    }

    /**
     * A key combination for paging right.
     *
     * @param key A pressed key.
     * @return Whether the ctrl or alt key is held down, and the right arrow key is pressed.
     */
    public static boolean isGoingRight(int key)
    {
        return (Screen.hasControlDown() || Screen.hasAltDown()) && key == GLFW.GLFW_KEY_RIGHT;
    }

    /**
     * A key combination for paging left.
     *
     * @param key A pressed key.
     * @return Whether the ctrl or alt key is held down and the left arrow key is pressed.
     */
    public static boolean isGoingLeft(int key)
    {
        return (Screen.hasControlDown() || Screen.hasAltDown()) && key == GLFW.GLFW_KEY_LEFT;
    }

    /**
     * Checks if the given key code is a left arrow key or right arrow key.
     *
     * @param key A pressed key.
     * @return Whether the left or right arrow key is pressed.
     */
    public static boolean isLeftOrRight(int key)
    {
        return key == GLFW.GLFW_KEY_LEFT || key == GLFW.GLFW_KEY_RIGHT;
    }

    /**
     * A key combination for searching.
     *
     * @param key A pressed key.
     * @return Whether the ctrl key is held down and the F key is pressed.
     */
    public static boolean isSearching(int key)
    {
        return Screen.hasControlDown() && key == GLFW.GLFW_KEY_F;
    }

    /**
     * A key combination for saving.
     *
     * @param key A pressed key.
     * @return Whether the ctrl key is held down and the S key is pressed.
     */
    public static boolean isSaving(int key)
    {
        return Screen.hasControlDown() && key == GLFW.GLFW_KEY_S;
    }

    /**
     * A key combination for selecting everything.
     *
     * @param key A pressed key.
     * @return Whether the ctrl key is held down and the A key is pressed.
     */
    public static boolean isSelectAll(int key)
    {
        return Screen.hasControlDown() && key == GLFW.GLFW_KEY_A;
    }

    /**
     * This returns true if the number pad enter key is pressed, the space key is pressed, or the keyboard enter key is
     * pressed. Use the {@code see also} method if the space key is not acceptable.
     *
     * @param key A pressed key.
     * @return Whether the number pad enter key, space key, or keyboard enter key is pressed.
     * @see #isOnlyEnter(int)
     */
    public static boolean isEnter(int key)
    {
        return key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER || key == GLFW.GLFW_KEY_SPACE;
    }

    /**
     * This returns true if the number pad enter key is pressed or the keyboard enter key is pressed. Use the
     * {@code see also} method is the space key is also acceptable.
     *
     * @param key A pressed key.
     * @return Whether the number pad enter key or keyboard enter key is pressed.
     * @see #isEnter(int)
     */
    public static boolean isOnlyEnter(int key)
    {
        return key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER;
    }

    /**
     * Checks if the Tab key is pressed.
     *
     * @param key A pressed key.
     * @return Whether the Tab key was pressed.
     */
    public static boolean isTab(int key)
    {
        return key == GLFW.GLFW_KEY_TAB;
    }

    /**
     * Checks if the Esc key is pressed.
     *
     * @param key A pressed key.
     * @return Whether the Esc key was pressed.
     */
    public static boolean isEsc(int key)
    {
        return key == GLFW.GLFW_KEY_ESCAPE;
    }

    /**
     * Checks if any of the given keys matches the given key code.
     *
     * @param keyCode A key code to check against.
     * @param keys    A varargs list of keys to check.
     * @return Whether any of the given keys matched the given key code.
     */
    public static boolean match(int keyCode, int... keys)
    {
        return Arrays.stream(keys).anyMatch(key -> key == keyCode);
    }

    /**
     * This cache prevents the need to cycle through all the key mappings. Extremely useful for when there are a lot of
     * key mappings.
     */
    private static final Map<String, KeyMapping> MAPPING_CACHE = new HashMap<>();

    /* Key Helpers */

    /**
     * Finds a key mapping based on the given {@code langKey}.
     *
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
     * Finds a key mapping based on the given argument.
     *
     * @param langKey A {@link Translation} instance.
     * @return A key mapping if one was found.
     */
    public static Optional<KeyMapping> find(Translation langKey)
    {
        return find(langKey.langKey());
    }

    /**
     * Checks if the given mapping conflicts with another key mapping.
     *
     * @param mapping A key mapping instance.
     * @return Whether the key mapping conflicts with another key mapping.
     */
    public static boolean isMappingConflict(KeyMapping mapping)
    {
        if (!mapping.isUnbound())
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
}
