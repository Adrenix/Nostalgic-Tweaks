package mod.adrenix.nostalgic.mixin.duck;

/**
 * Will be injected into the LocalPlayer class.
 *
 * This will prevent the client side swinging animation from playing when dropping an item, but will still send
 * a swing packet to the server so other players will see the swing.
 */

public interface ILocalSwing
{
    void NT$setSwingBlocked(boolean state);
    boolean NT$isSwingBlocked();
}
