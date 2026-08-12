package mod.adrenix.nostalgic.api;

import mod.adrenix.nostalgic.util.common.LocateResource;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("2.0.0")
public final class NostalgicAttributes
{
    // @formatter:off
    @ApiStatus.AvailableSince("2.0.0")
    public static final Holder<Attribute> MAX_STAMINA = register("player.max_stamina", new RangedAttribute("attribute.nostalgic_tweaks.max_stamina", 20.0D, 2.0D, 1000.0D).setSyncable(true));

    // @formatter:on

    /**
     * Register an attribute.
     *
     * @param name      The identifier key for the attribute.
     * @param attribute The {@link Attribute} instance to register.
     * @return A {@link Holder} that contains the {@link Attribute}.
     */
    private static Holder<Attribute> register(@SuppressWarnings("SameParameterValue") String name, Attribute attribute)
    {
        return Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, LocateResource.mod(name), attribute);
    }

    // no-op
    private NostalgicAttributes()
    {
    }
}
