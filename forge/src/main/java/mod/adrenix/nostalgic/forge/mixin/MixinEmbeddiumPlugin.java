package mod.adrenix.nostalgic.forge.mixin;

import com.google.common.base.Suppliers;
import net.neoforged.fml.ModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Do <b color=red>not</b> class load any mod related classes here. Doing so will cause "applied too early" ASM errors
 * during the mixin application process.
 */
public class MixinEmbeddiumPlugin implements IMixinConfigPlugin
{
    /* Fields */

    private final Supplier<Boolean> isEmbeddiumPresent = Suppliers.memoize(() -> ModList.get().isLoaded("embeddium"));

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
        if (ModList.get() == null)
            return false;

        return this.isEmbeddiumPresent.get();
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
