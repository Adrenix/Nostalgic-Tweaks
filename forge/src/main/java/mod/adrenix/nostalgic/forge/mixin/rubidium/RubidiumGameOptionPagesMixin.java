package mod.adrenix.nostalgic.forge.mixin.rubidium;

import me.jellysquid.mods.sodium.client.gui.SodiumGameOptionPages;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpact;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpl;
import me.jellysquid.mods.sodium.client.gui.options.control.CyclingControl;
import me.jellysquid.mods.sodium.client.gui.options.storage.MinecraftOptionsStorage;
import mod.adrenix.nostalgic.util.common.ComponentBackport;
import net.minecraft.client.CloudStatus;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SodiumGameOptionPages.class)
public abstract class RubidiumGameOptionPagesMixin
{
    /* Shadows */

    @Shadow @Final private static MinecraftOptionsStorage vanillaOpts;

    /* Injections */

    /**
     * Replaces the control option for cloud rendering.
     */
    @Redirect(
        remap = false,
        method = "quality",
        at = @At(
            ordinal = 1,
            value = "INVOKE",
            target = "setImpact(Lme/jellysquid/mods/sodium/client/gui/options/OptionImpact;)Lme/jellysquid/mods/sodium/client/gui/options/OptionImpl$Builder;"
        )
    )
    private static OptionImpl.Builder<?, ?> NT$onCloudsImpl(OptionImpl.Builder<?, ?> instance, OptionImpact optionImpact)
    {
        return OptionImpl.createBuilder(CloudStatus.class, vanillaOpts)
            .setName(ComponentBackport.literal("§e[Nostalgic Tweaks]§r Clouds"))
            .setTooltip(ComponentBackport.literal("Change how clouds are rendered."))
            .setControl(option -> new CyclingControl<>(option, CloudStatus.class, new Component[] {
                ComponentBackport.translatable(CloudStatus.OFF.getKey()),
                ComponentBackport.translatable(CloudStatus.FAST.getKey()),
                ComponentBackport.translatable(CloudStatus.FANCY.getKey())
            }))
            .setBinding((options, value) -> options.renderClouds = value, options -> options.renderClouds)
            .setImpact(OptionImpact.LOW);
    }
}
