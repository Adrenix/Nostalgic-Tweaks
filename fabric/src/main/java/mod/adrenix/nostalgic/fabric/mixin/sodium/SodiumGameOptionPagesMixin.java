package mod.adrenix.nostalgic.fabric.mixin.sodium;

import me.jellysquid.mods.sodium.client.gui.SodiumGameOptionPages;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpact;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpl;
import me.jellysquid.mods.sodium.client.gui.options.control.CyclingControl;
import me.jellysquid.mods.sodium.client.gui.options.storage.MinecraftOptionsStorage;
import net.minecraft.client.CloudStatus;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SodiumGameOptionPages.class)
public abstract class SodiumGameOptionPagesMixin
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
            .setName(Component.literal("§e[Nostalgic Tweaks]§r Clouds"))
            .setTooltip(Component.literal("Change how clouds are rendered."))
            .setControl(option -> new CyclingControl<>(option, CloudStatus.class, new Component[] {
                Component.translatable(CloudStatus.OFF.getKey()),
                Component.translatable(CloudStatus.FAST.getKey()),
                Component.translatable(CloudStatus.FANCY.getKey())
            }))
            .setBinding((options, value) -> options.cloudStatus().set(value), options -> options.cloudStatus().get())
            .setImpact(OptionImpact.LOW);
    }
}
