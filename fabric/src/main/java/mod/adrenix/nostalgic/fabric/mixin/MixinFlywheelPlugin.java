package mod.adrenix.nostalgic.fabric.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Do <b color=red>not</b> class load any mod related classes here. Doing so will cause "applied too early" ASM errors
 * during the mixin application process.
 */
public class MixinFlywheelPlugin implements IMixinConfigPlugin
{
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
        return FabricLoader.getInstance()
            .getModContainer("flywheel")
            .map(flywheel -> flywheel.getMetadata().getVersion().getFriendlyString().startsWith("0.6"))
            .orElse(false);
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
