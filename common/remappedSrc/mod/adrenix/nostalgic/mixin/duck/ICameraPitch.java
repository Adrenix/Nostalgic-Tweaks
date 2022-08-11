package mod.adrenix.nostalgic.mixin.duck;

/**
 * Camera pitching was removed from living entities in Minecraft 1.13.
 */

public interface ICameraPitch
{
    void NT$setCameraPitch(float cameraPitch);
    void NT$setPrevCameraPitch(float prevCameraPitch);

    float NT$getCameraPitch();
    float NT$getPrevCameraPitch();
}
