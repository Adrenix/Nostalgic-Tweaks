package mod.adrenix.nostalgic.neoforge.mixin;

import com.llamalad7.mixinextras.utils.MixinInternals;
import net.neoforged.fml.loading.FMLLoader;
import org.embeddedt.embeddium.impl.taint.mixin.MixinTaintDetector;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ext.IExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

/**
 * Do <b color=red>not</b> class load any mod related classes here. Doing so will cause "applied too early" ASM errors
 * during the mixin application process.
 */
public class MixinEmbeddiumPlugin implements IMixinConfigPlugin
{
    /* Logger */

    private static final Logger LOGGER = LoggerFactory.getLogger("NostalgicTweaks-EmbeddiumSupport");

    /* Constructor */

    /**
     * Embeddium now requires mods to use mod loader events or the Embeddium API. Both of which do not have the
     * capabilities to support what our mod needs to change/add to Embeddium code. If a mixin is broken, then the tweak
     * will fail to function correctly and will indicate to the user that this is a Nostalgic Tweaks problem and not
     * Embeddium. In which case, an update will need to be made by Nostalgic Tweaks. None of our Embeddium mixins are
     * required, and will simply fail-soft if a conflict arises.
     * <p>
     * It is understandable why Embeddium made this change. However, a large majority of our changes are very niche and
     * may not be reasonable to include in the Embeddium API. Such as our changes to the Occlusion Culler to make the
     * old square chunk border tweak work as intended. This workaround is made to lift the Embeddium restriction applied
     * to mods. If the Embeddium API is open to very niche inclusions, then this will be removed in a future update.
     */
    public MixinEmbeddiumPlugin()
    {
        System.setProperty("embeddium.mixinTaintEnforceLevel", "WARN");

        try
        {
            Field instance = MixinTaintDetector.class.getDeclaredField("INSTANCE");

            instance.setAccessible(true);
            MixinInternals.unregisterExtension((IExtension) instance.get(null));

            LOGGER.info("Removed Embeddium mixin taint enforcement. Please report issues to Nostalgic Tweaks before Embeddium.");
        }
        catch (Throwable throwable)
        {
            LOGGER.error("Embeddium broke mixin support. Try an older Embeddium version or a newer Nostalgic Tweaks version.");
        }
    }

    /* Methods */

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLoad(String mixinPackage)
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRefMapperConfig()
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
    {
        return FMLLoader.getLoadingModList().getModFileById("embeddium") != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets)
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getMixins()
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
    {
    }
}
