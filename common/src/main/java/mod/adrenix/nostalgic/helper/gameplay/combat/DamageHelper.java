package mod.adrenix.nostalgic.helper.gameplay.combat;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This utility class is used by both the client and server.
 */
public abstract class DamageHelper
{
    /**
     * A cache map that connects an item to its old damage value item attribute.
     */
    private static final Map<String, ItemAttributeModifiers> DAMAGE_MODIFIERS = new ConcurrentHashMap<>();

    /**
     * Get the attack damage amount for a tiered item.
     *
     * @param tieredItem The {@link TieredItem} instance.
     * @return The attack damage amount.
     */
    private static float getAttackDamage(TieredItem tieredItem)
    {
        return switch (tieredItem)
        {
            case SwordItem sword -> sword.getTier().getAttackDamageBonus() + 4.0F;
            case AxeItem axe -> axe.getTier().getAttackDamageBonus() + 3.0F;
            case PickaxeItem pickaxe -> pickaxe.getTier().getAttackDamageBonus() + 2.0F;
            case ShovelItem shovel -> shovel.getTier().getAttackDamageBonus() + 1.0F;
            default -> tieredItem.getTier().getAttackDamageBonus();
        };
    }

    /**
     * Check if the given tiered item has an old value.
     *
     * @param tieredItem The {@link TieredItem} to check.
     * @return Whether the given tiered item has an old damage value.
     */
    public static boolean isApplicable(TieredItem tieredItem)
    {
        return switch (tieredItem)
        {
            case SwordItem ignored -> true;
            case AxeItem ignored -> true;
            case PickaxeItem ignored -> true;
            case ShovelItem ignored -> true;
            case HoeItem ignored -> true;
            default -> false;
        };
    }

    /**
     * Get the old damage value based on the provided tiered item.
     *
     * @param tieredItem The {@link TieredItem} instance.
     * @param attributes The {@link ItemAttributeModifiers} instance.
     * @return An old damage value, if it is a vaild item.
     */
    public static ItemAttributeModifiers get(final TieredItem tieredItem, final ItemAttributeModifiers attributes)
    {
        return DAMAGE_MODIFIERS.computeIfAbsent(tieredItem.getDescriptionId(), itemClass -> {
            ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

            attributes.modifiers().forEach(entry -> {
                if (entry.attribute() != Attributes.ATTACK_DAMAGE)
                    builder.add(entry.attribute(), entry.modifier(), entry.slot());
                else
                {
                    if (tieredItem instanceof DiggerItem diggerItem)
                        builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_UUID, "Tool modifier", getAttackDamage(diggerItem), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
                    else if (tieredItem instanceof SwordItem swordItem)
                        builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", getAttackDamage(swordItem), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
                    else
                        builder.add(entry.attribute(), entry.modifier(), entry.slot());
                }
            });

            return builder.build();
        });
    }
}
