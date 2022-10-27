package mod.adrenix.nostalgic.mixin.widen;

import com.mojang.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@SuppressWarnings("unused")
@Mixin(Matrix4f.class)
public interface Matrix4fAccessor
{
    @Accessor("m00") float NT$getM00();
    @Accessor("m01") float NT$getM01();
    @Accessor("m02") float NT$getM02();
    @Accessor("m03") float NT$getM03();
    @Accessor("m10") float NT$getM10();
    @Accessor("m11") float NT$getM11();
    @Accessor("m12") float NT$getM12();
    @Accessor("m13") float NT$getM13();
    @Accessor("m20") float NT$getM20();
    @Accessor("m21") float NT$getM21();
    @Accessor("m22") float NT$getM22();
    @Accessor("m23") float NT$getM23();
    @Accessor("m30") float NT$getM30();
    @Accessor("m31") float NT$getM31();
    @Accessor("m32") float NT$getM32();
    @Accessor("m33") float NT$getM33();

    @Accessor("m00") void NT$setM00(float value);
    @Accessor("m01") void NT$setM01(float value);
    @Accessor("m02") void NT$setM02(float value);
    @Accessor("m03") void NT$setM03(float value);
    @Accessor("m10") void NT$setM10(float value);
    @Accessor("m11") void NT$setM11(float value);
    @Accessor("m12") void NT$setM12(float value);
    @Accessor("m13") void NT$setM13(float value);
    @Accessor("m20") void NT$setM20(float value);
    @Accessor("m21") void NT$setM21(float value);
    @Accessor("m22") void NT$setM22(float value);
    @Accessor("m23") void NT$setM23(float value);
    @Accessor("m30") void NT$setM30(float value);
    @Accessor("m31") void NT$setM31(float value);
    @Accessor("m32") void NT$setM32(float value);
    @Accessor("m33") void NT$setM33(float value);
}
