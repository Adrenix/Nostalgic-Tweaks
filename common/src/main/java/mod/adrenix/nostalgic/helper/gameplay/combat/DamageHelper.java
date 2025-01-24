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
     * Get the attack damage bonus amount for a tiered item.
     *
     * @param itemStack The {@link ItemStack} instance.
     * @return The attack damage bonus amount.
     */
    private static float getDamageBonus(ItemStack itemStack)
    {
        if (itemStack.isValidRepairItem(Items.OAK_PLANKS.getDefaultInstance()))
            return ToolMaterial.WOOD.attackDamageBonus();
        else if (itemStack.isValidRepairItem(Items.COBBLESTONE.getDefaultInstance()))
            return ToolMaterial.STONE.attackDamageBonus();
        else if (itemStack.isValidRepairItem(Items.IRON_INGOT.getDefaultInstance()))
            return ToolMaterial.IRON.attackDamageBonus();
        else if (itemStack.isValidRepairItem(Items.GOLD_INGOT.getDefaultInstance()))
            return ToolMaterial.GOLD.attackDamageBonus();
        else if (itemStack.isValidRepairItem(Items.DIAMOND.getDefaultInstance()))
            return ToolMaterial.DIAMOND.attackDamageBonus();
        else if (itemStack.isValidRepairItem(Items.NETHERITE_INGOT.getDefaultInstance()))
            return ToolMaterial.NETHERITE.attackDamageBonus();

        return 0.0F;
    }

    /**
     * Get the attack damage amount for a tiered item.
     *
     * @param itemStack The {@link ItemStack} instance.
     * @return The attack damage amount.
     */
    private static float getAttackDamage(ItemStack itemStack)
    {
        return switch (itemStack.getItem())
        {
            case SwordItem ignored -> getDamageBonus(itemStack) + 4.0F;
            case AxeItem ignored -> getDamageBonus(itemStack) + 3.0F;
            case PickaxeItem ignored -> getDamageBonus(itemStack) + 2.0F;
            case ShovelItem ignored -> getDamageBonus(itemStack) + 1.0F;
            default -> 0.0F;
        };
    }

    /**
     * Check if the given tiered item has an old value.
     *
     * @param item The {@link Item} instance to check.
     * @return Whether the given tiered item has an old damage value.
     */
    public static boolean isApplicable(Item item)
    {
        return switch (item)
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
     * @param itemStack  The {@link Item} instance.
     * @param attributes The {@link ItemAttributeModifiers} instance.
     * @return An old damage value, if it is a valid item.
     */
    public static ItemAttributeModifiers get(final ItemStack itemStack, final ItemAttributeModifiers attributes)
    {
        Item item = itemStack.getItem();

        return DAMAGE_MODIFIERS.computeIfAbsent(item.getDescriptionId(), itemClass -> {
            ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

            attributes.modifiers().forEach(entry -> {
                if (entry.attribute() != Attributes.ATTACK_DAMAGE)
                    builder.add(entry.attribute(), entry.modifier(), entry.slot());
                else
                {
                    if (item instanceof DiggerItem || item instanceof SwordItem)
                        builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, getAttackDamage(itemStack), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
                    else
                        builder.add(entry.attribute(), entry.modifier(), entry.slot());
                }
            });

            return builder.build();
        });
    }
}
