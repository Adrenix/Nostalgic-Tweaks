package mod.adrenix.nostalgic.neoforge.setup;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.api.NostalgicAttributes;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

@EventBusSubscriber(
    modid = NostalgicTweaks.MOD_ID,
    bus = EventBusSubscriber.Bus.MOD
)
public abstract class CommonSetup
{
    /**
     * Registers our custom entity attributes to vanilla entities.
     *
     * @param event The {@link EntityAttributeModificationEvent} instance.
     */
    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event)
    {
        event.add(EntityType.PLAYER, NostalgicAttributes.MAX_STAMINA);
    }
}
