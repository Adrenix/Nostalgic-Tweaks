package mod.adrenix.nostalgic.mixin.duck;

/**
 * Camera pitching was removed from living entities in Minecraft 1.13.
 */
public interface CameraPitching
{
    void nt$setCameraPitch(float cameraPitch);

    void nt$setPrevCameraPitch(float prevCameraPitch);

    float nt$getCameraPitch();

    float nt$getPrevCameraPitch();
}
