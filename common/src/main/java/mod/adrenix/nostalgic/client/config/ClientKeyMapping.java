package mod.adrenix.nostalgic.client.config;

import com.mojang.blaze3d.platform.InputConstants;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

/**
 * This helper class keeps a record of all key mappings that are used by the mod.
 * These mappings are used by both mod loaders. The instantiation of these mappings are handled by a mod loader.
 */

public abstract class ClientKeyMapping
{
    /**
     * The config key, when pressed, opens the mod's configuration graphical user interface. The screen that appears
     * when this key is pressed is dependent on the default screen that is set by the user. By default, the mod's config
     * homepage is used.
     */
    public static final KeyMapping CONFIG_KEY = new KeyMapping
    (
        LangUtil.Key.OPEN_CONFIG,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_O,
        LangUtil.Key.CATEGORY_NAME
    );

    /**
     * The fog key, when pressed, toggles the game's rendering distance. The value is changed by the classic predefined
     * rendering distances used before the game switched to a slider. The key cycles between the following; far (16),
     * normal (8), short (4), and tiny (2).
     */
    public static final KeyMapping FOG_KEY = new KeyMapping
    (
        LangUtil.Key.TOGGLE_FOG,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_UNKNOWN,
        LangUtil.Key.CATEGORY_NAME
    );
}
