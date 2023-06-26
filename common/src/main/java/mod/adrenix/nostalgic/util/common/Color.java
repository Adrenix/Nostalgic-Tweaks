package mod.adrenix.nostalgic.util.common;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.ChatFormatting;
import net.minecraft.util.Mth;

import java.util.Random;

public class Color
{
    /* Colors */

    @PublicAPI public static final Color TRANSPARENT = new Color(0xFFFFFF, 0.0F);
    @PublicAPI public static final Color WHITE = new Color(0xFFFFFF);
    @PublicAPI public static final Color BLACK = new Color(0x000000);
    @PublicAPI public static final Color RED = new Color(0xFF0000);
    @PublicAPI public static final Color GREEN = new Color(0x00FF00);
    @PublicAPI public static final Color BLUE = new Color(0x0000FF);
    @PublicAPI public static final Color YELLOW = new Color(0xFFFF00);
    @PublicAPI public static final Color CYAN = new Color(0x00FFFF);
    @PublicAPI public static final Color PINK = new Color(0xFF00FF);
    @PublicAPI public static final Color ORANGE = new Color(0xFF8000);
    @PublicAPI public static final Color LIME = new Color(0xBFFF00);
    @PublicAPI public static final Color PURPLE = new Color(0xBF00FF);
    @PublicAPI public static final Color LIGHT_BLUE = new Color(0x00BFFF);
    @PublicAPI public static final Color LIGHT_GRAY = new Color(0xC0C0C0);
    @PublicAPI public static final Color DARK_GRAY = new Color(0x202020);
    @PublicAPI public static final Color GRAY = new Color(0x808080);

    /* Static */

    /**
     * Creates a new color instance based on the given Minecraft chat formatting enumeration. If the given enumeration
     * does not have a color, then white is returned.
     *
     * @param formatting A Minecraft chat formatting enumeration.
     * @return A new color instance using the color integer returned by the formatting enumeration, or white if the
     * enumeration does not have a color.
     */
    @PublicAPI
    public static Color fromFormatting(ChatFormatting formatting)
    {
        return formatting.getColor() == null ? Color.WHITE : new Color(formatting.getColor());
    }

    /**
     * Creates a new random color instance based on random floats produced by the {@link Random} utility. All random
     * colors will be opaque.
     *
     * @return A new random color instance.
     */
    @PublicAPI
    public static Color getRandomColor()
    {
        Random random = new Random();
        float r = random.nextFloat();
        float g = random.nextFloat();
        float b = random.nextFloat();

        return new Color(r, g, b);
    }

    /**
     * Creates a new color instance based on the specified values for the HSB color model. The {@code s} and {@code b}
     * components should be floating-point values between zero and one (numbers in the range 0.0-1.0). The {@code h}
     * component can be any floating-point number. The floor of this number is subtracted from it to create a fraction
     * between 0 and 1. This fractional number is then multiplied by 360 to produce the hue angle in the HSB color
     * model.
     *
     * @param h The hue component.
     * @param s The saturation of the color.
     * @param b The brightness of the color.
     * @return A new color instance with the specific HSB (<i>also known as {@code HSV}</i>).
     */
    @PublicAPI
    public static Color getHSBColor(float h, float s, float b)
    {
        return new Color(HSBtoRGB(h, s, b));
    }

    /**
     * Converts the components of a color, as specified by the HSB model (<i>also known as {@code HSV}</i>), to an
     * equivalent set of values for the default RGB model.
     *
     * <br><br>
     * The {@code saturation} and {@code brightness} components should be floating-point values between zero and one
     * (numbers in the range 0.0-1.0). The {@code hue} component can be any floating-point number. The floor of this
     * number is subtracted from it to create a fraction between 0 and 1. This fractional number is then multiplied by
     * 360 to produce the hue angle in the HSB color model. All arguments will be clamped to the range (0.0F-1.0F).
     *
     * <br><br>
     * The integer that is returned by {@code HSBtoRGB} encodes the value of a color in bits 0-23 of an integer value
     * that is the same format used by the method {@link Color#get()}. This integer can be supplied as an argument to
     * the {@code Color} constructor that takes a single integer argument.
     *
     * @param hue        The normalized hue component of the color (0.0F-1.0F).
     * @param saturation The normalized saturation of the color (0.0F-1.0F).
     * @param brightness The normalized brightness of the color (0.0F-1.0F).
     * @return The RGB value of the color with the indicated hue, saturation, and brightness.
     */
    @PublicAPI
    public static int HSBtoRGB(float hue, float saturation, float brightness)
    {
        hue = Mth.clamp(hue, 0.0F, 1.0F);
        saturation = Mth.clamp(saturation, 0.0F, 1.0F);
        brightness = Mth.clamp(brightness, 0.0F, 1.0F);

        int r = 0;
        int g = 0;
        int b = 0;

        if (saturation == 0)
        {
            int dark = (int) (brightness * 255.0F + 0.5F);
            r = dark;
            g = dark;
            b = dark;
        }
        else
        {
            float h = (hue - (float) Math.floor(hue)) * 6.0F;
            float f = h - (float) Math.floor(h);
            float p = brightness * (1.0F - saturation);
            float q = brightness * (1.0F - saturation * f);
            float t = brightness * (1.0F - (saturation * (1.0F - f)));

            switch ((int) h)
            {
                case 0 ->
                {
                    r = (int) (brightness * 255.0F + 0.5F);
                    g = (int) (t * 255.0F + 0.5F);
                    b = (int) (p * 255.0F + 0.5F);
                }
                case 1 ->
                {
                    r = (int) (q * 255.0F + 0.5F);
                    g = (int) (brightness * 255.0F + 0.5F);
                    b = (int) (p * 255.0F + 0.5F);
                }
                case 2 ->
                {
                    r = (int) (p * 255.0F + 0.5F);
                    g = (int) (brightness * 255.0F + 0.5F);
                    b = (int) (t * 255.0F + 0.5F);
                }
                case 3 ->
                {
                    r = (int) (p * 255.0F + 0.5F);
                    g = (int) (q * 255.0F + 0.5F);
                    b = (int) (brightness * 255.0F + 0.5F);
                }
                case 4 ->
                {
                    r = (int) (t * 255.0F + 0.5F);
                    g = (int) (p * 255.0F + 0.5F);
                    b = (int) (brightness * 255.0F + 0.5F);
                }
                case 5 ->
                {
                    r = (int) (brightness * 255.0F + 0.5F);
                    g = (int) (p * 255.0F + 0.5F);
                    b = (int) (q * 255.0F + 0.5F);
                }
            }
        }

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    /**
     * Converts the components of a color, as specified by the default RGB model, to an equivalent set of values for
     * hue, saturation, and brightness that are the three components of the HSB model (<i>also known as
     * {@code HSV}</i>).
     *
     * <br><br>
     * If the {@code hsbValues} argument is {@code null}, then a new array is allocated to return the result. Otherwise,
     * the method returns the array {@code hsbValues}, with the values put into that array.
     *
     * <br><br>
     * Each argument will be clamped to the RGB integer range of (0-255).
     *
     * @param r         The red component of the color (0-255).
     * @param g         The green component of the color (0-255).
     * @param b         The blue component of the color (0-255).
     * @param hsbValues The array used to return the three HSB values, or {@code null}.
     * @return An array of three elements containing the hue, saturation, and brightness (in that order), of the color
     * with the indicated red, green, and blue components. All floats are normalized (0.0F-1.0F) and the hue component
     * is based on 360 <b>not</b> 255.
     * @see Color#RGBtoHSB(int, int, int)
     */
    @PublicAPI
    public static float[] RGBtoHSB(int r, int g, int b, float[] hsbValues)
    {
        r = Mth.clamp(r, 0, 255);
        g = Mth.clamp(g, 0, 255);
        b = Mth.clamp(b, 0, 255);

        float hue;
        float saturation;
        float brightness;

        if (hsbValues == null)
            hsbValues = new float[3];

        int cMax = Math.max(r, g);

        if (b > cMax)
            cMax = b;

        int cMin = Math.min(r, g);

        if (b < cMin)
            cMin = b;

        brightness = ((float) cMax) / 255.0F;

        if (cMax != 0)
            saturation = ((float) (cMax - cMin)) / ((float) cMax);
        else
            saturation = 0;

        if (saturation == 0)
            hue = 0;
        else
        {
            float cRed = ((float) (cMax - r)) / ((float) (cMax - cMin));
            float cGreen = ((float) (cMax - g)) / ((float) (cMax - cMin));
            float cBlue = ((float) (cMax - b)) / ((float) (cMax - cMin));

            if (r == cMax)
                hue = cBlue - cGreen;
            else if (g == cMax)
                hue = 2.0F + cRed - cBlue;
            else
                hue = 4.0F + cGreen - cRed;

            hue = hue / 6.0F;

            if (hue < 0)
                hue = hue + 1.0F;
        }

        hsbValues[0] = hue;
        hsbValues[1] = saturation;
        hsbValues[2] = brightness;

        return hsbValues;
    }

    /**
     * Converts the components of a color, as specified by the default RGB model, to an equivalent set of values for
     * hue, saturation, and brightness that are the three components of the HSB model (<i>also known as
     * {@code HSV}</i>).
     *
     * @param r The red component of the color.
     * @param g The green component of the color.
     * @param b The blue component of the color.
     * @return An array of three elements containing the hue, saturation, and brightness (in that order), of the color
     * with the indicated red, green, and blue components.
     * @see Color#RGBtoHSB(int, int, int, float[])
     */
    @PublicAPI
    public static float[] RGBtoHSB(int r, int g, int b)
    {
        return RGBtoHSB(r, g, b, null);
    }

    /* Fields */

    /**
     * The color value.
     *
     * @see Color#get()
     */
    private int color;

    /* Constructors */

    /**
     * Creates an sRGB color with the specified red, green, blue, and alpha values in the range (0-255). Any argument
     * that os outside of this range will be clamped.
     *
     * @param r The red component.
     * @param g The green component.
     * @param b The blue component.
     * @param a The alpha component.
     * @see Color#getRed
     * @see Color#getGreen
     * @see Color#getBlue
     * @see Color#getAlpha
     * @see Color#get
     */
    public Color(int r, int g, int b, int a)
    {
        r = Mth.clamp(r, 0, 255);
        g = Mth.clamp(g, 0, 255);
        b = Mth.clamp(b, 0, 255);
        a = Mth.clamp(a, 0, 255);

        this.color = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF));
    }

    /**
     * Creates an opaque sRGB color with the specified red, green, and blue values in the range (0-255). The actual
     * color used in rendering depends on finding the best match given the color space available for a given output
     * device. The alpha component is defaulted to 255. Any argument that is outside the range (0-255) will be clamped.
     *
     * @param r The red component.
     * @param g The green component.
     * @param b The blue component.
     * @see Color#getRed
     * @see Color#getGreen
     * @see Color#getBlue
     * @see Color#get
     */
    public Color(int r, int g, int b)
    {
        this(r, g, b, 255);
    }

    /**
     * Creates an opaque sRGB color with the specified combined RGB value consisting of the red component in bits 16-23,
     * the green component in bits 8-15, and the blue component in bits 0-7. The actual color used in rendering depends
     * on finding the best match given the color space available for a particular output device. The alpha component is
     * defaulted to 255.
     *
     * @param rgb The combined RGB components as an integer.
     * @see Color#getRed
     * @see Color#getGreen
     * @see Color#getBlue
     * @see Color#get
     */
    public Color(int rgb)
    {
        this.color = 0xFF000000 | rgb;
    }

    /**
     * Creates an opaque sRGB color with the specified combined RGB value consisting of the red component in bits 16-23,
     * the green component in bits 8-15, and the blue component in bits 0-7. The actual color used in rendering depends
     * on finding the best match given the color space available for a particular output device.
     *
     * @param rgb   The combined RGB components as an integer.
     * @param alpha A float that is in the range (0.0F-1.0F). This will be clamped if out of range.
     */
    public Color(int rgb, float alpha)
    {
        this((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, (int) (Mth.clamp(alpha, 0.0F, 1.0F) * 255.0F));
    }

    /**
     * Creates an opaque sRGB color with the specified red, green, and blue values in the range (0.0F-1.0F). The alpha
     * component is defaulted to 1.0F. The actual color used in rendering depends on finding the best match given the
     * color space available for a particular output device. Each argument will be clamped if it is out of range.
     *
     * @param r The red component.
     * @param g The green component.
     * @param b The blue component.
     * @see Color#getRed
     * @see Color#getGreen
     * @see Color#getBlue
     * @see Color#get
     */
    public Color(float r, float g, float b)
    {
        this(r, g, b, 1.0F);
    }

    /**
     * Creates an sRGB color with the specified red, green, blue, and alpha values in the range (0.0F-1.0F). The actual
     * color used in rendering depends on finding the best match given the color space available for a particular output
     * device. Each argument will be clamped if it is out of range.
     *
     * @param r The red component.
     * @param g The green component.
     * @param b The blue component.
     * @param a The alpha component.
     * @see Color#getRed
     * @see Color#getGreen
     * @see Color#getBlue
     * @see Color#getAlpha
     * @see Color#get
     */
    public Color(float r, float g, float b, float a)
    {
        this((int) (r * 255.0F), (int) (g * 255.0F), (int) (b * 255.0F), (int) (a * 255.0F));
    }

    /**
     * Copies a color instance and applies the given alpha to it. The given alpha will be clamped if it is outside the
     * range (0.0F-1.0F).
     *
     * @param color A color instance.
     * @param alpha An alpha float that is in the range (0.0F-1.0F).
     */
    public Color(Color color, float alpha)
    {
        this(color.getRed(), color.getGreen(), color.getBlue(), (int) (Mth.clamp(alpha, 0.0F, 1.0F) * 255));
    }

    /**
     * Get a new {@code Color} instance by copying the color from another {@code Color} instance.
     *
     * @param color A {@link Color} instance.
     */
    public Color(Color color)
    {
        this.color = color.get();
    }

    /**
     * Creates an sRGB color with the given {@code int[4]} RGBA array. The actual color used in rendering depends on
     * finding the best match given the color space available for a particular output device.
     *
     * @param rgba An {@code int[4]} array where index 0 represents {@code R} and index 3 represents {@code A}.
     */
    public Color(int[] rgba)
    {
        this(rgba[0], rgba[1], rgba[2], rgba[3]);
    }

    /**
     * Creates an sRGB color with the given hex input string. The actual color used in rendering depends on finding the
     * best match given the color space available for a particular output device. If the given hex input is invalid,
     * then the color will default to white.
     *
     * @param hexInput A hex string that may or may not start with a hash "#" and may or may not include an alpha
     *                 component.
     */
    public Color(String hexInput)
    {
        this(ColorUtil.toHexRGBA(hexInput));
    }

    /* Methods */

    /**
     * Change the color value for this {@code Color} instance. The RGB integer can contain only RGB or can contain RGBA
     * hex values.
     *
     * @param rgb An RGB integer or RGBA integer.
     */
    @PublicAPI
    public void set(int rgb)
    {
        this.color = 0xFF000000 | rgb;
    }

    /**
     * Change the color value for this {@code Color} instance by using the color value from a different {@code Color}
     * instance.
     *
     * @param color The {@code Color} instance to copy the color value from.
     */
    @PublicAPI
    public void set(Color color)
    {
        this.color = color.get();
    }

    /**
     * Change the color value for this {@code Color} instance using a HSB values.
     *
     * @param h The normalized hue component of the color (0.0F-1.0F).
     * @param s The normalized saturation of the color (0.0F-1.0F).
     * @param b The normalized brightness of the color (0.0F-1.0F).
     * @see Color#HSBtoRGB(float, float, float)
     */
    @PublicAPI
    public void set(float h, float s, float b)
    {
        this.color = HSBtoRGB(h, s, b);
    }

    /**
     * Change the alpha value for this {@code Color} instance. The normalized float will be clamped if it is out of
     * range.
     *
     * @param alpha A normalized alpha component (0.0F-1.0F).
     */
    @PublicAPI
    public void setAlpha(float alpha)
    {
        this.color = new Color(this.get(), alpha).get();
    }

    /**
     * @return Whether this {@code Color} instance has an alpha component less than 1.0F (or < 255).
     */
    @PublicAPI
    public boolean isTransparent()
    {
        return this.getAlpha() < 255;
    }

    /**
     * @return Whether this {@code Color} instance is fully opaque.
     */
    @PublicAPI
    public boolean isOpaque()
    {
        return this.getAlpha() == 255;
    }

    /**
     * Returns the RGB value representing the color in the default sRGB color model. Bits 24-31 are alpha, 16-23 are
     * red, 8-15 are green, 0-7 are blue.
     *
     * @return The RGB value of the color in the default sRGB color model.
     * @see Color#getRed
     * @see Color#getGreen
     * @see Color#getBlue
     * @see Color#getAlpha
     */
    public int get()
    {
        return this.color;
    }

    /**
     * Returns the red component in the range 0-255 in the default sRGB space.
     *
     * @return The red component.
     * @see Color#get
     */
    @PublicAPI
    public int getRed()
    {
        return (this.color >> 16) & 0xFF;
    }

    /**
     * Returns the green component in the range 0-255 in the default sRGB space.
     *
     * @return The green component.
     * @see Color#get
     */
    @PublicAPI
    public int getGreen()
    {
        return (this.color >> 8) & 0xFF;
    }

    /**
     * Returns the blue component in the range 0-255 in the default sRGB space.
     *
     * @return The blue component.
     * @see Color#get
     */
    @PublicAPI
    public int getBlue()
    {
        return this.color & 0xFF;
    }

    /**
     * Returns the alpha component in the range 0-255 in the default sRGB space.
     *
     * @return The alpha component.
     * @see Color#get
     */
    @PublicAPI
    public int getAlpha()
    {
        return (this.color >> 24) & 0xFF;
    }

    /**
     * Get an ARGB integer where the alpha component is fully opaque.
     *
     * @return An opaque version of this color.
     */
    @PublicAPI
    public int getOpaque()
    {
        return 0xFF000000 | (this.getRed() << 16) | (this.getGreen() << 8) | this.getBlue();
    }

    /**
     * @return The red component as a normalized percentage (0.0F-1.0F).
     */
    @PublicAPI
    public float getFloatRed()
    {
        return this.getRed() / 255.0F;
    }

    /**
     * @return The red component as a normalized percentage (0.0F-1.0F).
     */
    @PublicAPI
    public float getFloatGreen()
    {
        return this.getGreen() / 255.0F;
    }

    /**
     * @return The red component as a normalized percentage (0.0F-1.0F).
     */
    @PublicAPI
    public float getFloatBlue()
    {
        return this.getBlue() / 255.0F;
    }

    /**
     * @return The red component as a normalized percentage (0.0F-1.0F).
     */
    @PublicAPI
    public float getFloatAlpha()
    {
        return this.getAlpha() / 255.0F;
    }

    /**
     * Returns the hue value of this color as a normalized percent float (0.0F-1.0F). This is a shortcut method for
     * {@code this.getHSBComponents()[0]}.
     *
     * @return A normalized hue value float (0.0F-1.0F).
     */
    @PublicAPI
    public float getHue()
    {
        return this.getHSBComponents()[0];
    }

    /**
     * Returns the saturation value of this color as a normalized percent float (0.0F-1.0F). This is a shortcut method
     * for {@code this.getHSBComponents()[1]}.
     *
     * @return A normalized hue value float (0.0F-1.0F).
     */
    @PublicAPI
    public float getSaturation()
    {
        return this.getHSBComponents()[1];
    }

    /**
     * Returns the brightness value of this color as a normalized percent float (0.0F-1.0F). This is a shortcut method
     * for {@code this.getHSBComponents()[2]}.
     *
     * @return A normalized hue value float (0.0F-1.0F).
     */
    @PublicAPI
    public float getBrightness()
    {
        return this.getHSBComponents()[2];
    }

    /**
     * Returns the RGB value representing the hue of this color. Bits 24-31 are alpha, 16-23 are red, 8-15 are green,
     * 0-7 are blue.
     *
     * @return The RGB value of the hue from this color in the default sRGB color model.
     */
    @PublicAPI
    public int getHueAsRGB()
    {
        return HSBtoRGB(this.getHue(), 1.0F, 1.0F);
    }

    /**
     * Returns a {@code float[3]} array containing this {@code Color}'s HSB values.
     *
     * @return An array of three elements containing the hue, saturation, and brightness (in that order), of the color
     * with the indicated red, green, and blue components.
     */
    @PublicAPI
    public float[] getHSBComponents()
    {
        return RGBtoHSB(this.getRed(), this.getGreen(), this.getBlue());
    }

    /**
     * Returns a {@code float[4]} array containing the color and alpha components of the {@code Color}, as represented
     * in the default sRGB color space.
     *
     * @return The RGBA components in a {@code float[4]} array.
     */
    @PublicAPI
    public float[] getComponents()
    {
        float r = this.getFloatRed();
        float g = this.getFloatGreen();
        float b = this.getFloatBlue();
        float a = this.getFloatAlpha();

        return new float[] { r, g, b, a };
    }

    /**
     * Returns a {@code int[4]} array containing the color and alpha components of the {@code Color}, as represented in
     * the default sRGB color space.
     *
     * @return The RGBA components in a {@code int[4]} array.
     */
    @PublicAPI
    public int[] getIntComponents()
    {
        return new int[] { this.getRed(), this.getGreen(), this.getBlue(), this.getAlpha() };
    }

    /**
     * Returns an {@code int[3 or 4]} array containing the color (<i>and alpha component if the array size is 4</i>), as
     * represented in the default sRGB color space. If the given array does not have a length of 3 or 4, then a new
     * array with RGBA components will be returned.
     *
     * @param array An array of size 3 or 4.
     * @return The RGB(A) components stored in the given array.
     */
    @PublicAPI
    public int[] getIntComponents(int[] array)
    {
        if (array.length != 3 && array.length != 4)
            return getIntComponents();

        array[0] = this.getRed();
        array[1] = this.getGreen();
        array[2] = this.getBlue();

        if (array.length == 4)
            array[3] = this.getAlpha();

        return array;
    }

    /**
     * Creates a new {@code Color} that is a brighter version of this {@code Color}.
     *
     * @return A new {@code Color} object that is a brighter version of this {@code Color} with the same {@code alpha}
     * value.
     */
    @PublicAPI
    public Color brighter()
    {
        return this.brighten(0.3D);
    }

    /**
     * Brighten the color by the given amount.
     *
     * @param amount A normalized float amount [0.0F, 1.0F]. This value is clamped.
     * @return A brightened {@link Color} instance.
     */
    @PublicAPI
    public Color brighten(double amount)
    {
        amount = Mth.clamp(1.0D - amount, 0.0D, 1.0D);

        int r = this.getRed();
        int g = this.getGreen();
        int b = this.getBlue();
        int a = this.getAlpha();
        int i = (int) (1.0D / (1.0D - amount));

        if (r == 0 && g == 0 && b == 0)
            return new Color(i, i, i, a);

        if (r > 0 && r < i)
            r = i;

        if (g > 0 && g < i)
            g = i;

        if (b > 0 && b < i)
            b = i;

        r = Math.min((int) (r / amount), 255);
        g = Math.min((int) (g / amount), 255);
        b = Math.min((int) (b / amount), 255);

        return new Color(r, g, b, a);
    }

    /**
     * Creates a new {@code Color} that is a brighter version of this {@code Color}. If the brightened color is white,
     * then a darker color instance is returned.
     *
     * @return A new {@code Color} object that is a brighter (or darker if brightening results in a white color) with
     * the same {@code alpha} value.
     */
    @PublicAPI
    public Color brighterOrDarker()
    {
        Color brighter = this.brighter();

        if (brighter.equals(Color.WHITE))
            return this.darker();

        return brighter;
    }

    /**
     * Creates a new {@code Color} that is a darker version of this {@code Color}.
     *
     * @return A new {@code Color} object that is a darker version of this {@code Color} with the same {@code alpha}
     * value.
     */
    @PublicAPI
    public Color darker()
    {
        return this.darken(0.3D);
    }

    /**
     * Darken the color by the given amount.
     *
     * @param amount A normalized float amount [0.0F, 1.0F]. This value is clamped.
     * @return A darkened {@link Color} instance.
     */
    @PublicAPI
    public Color darken(double amount)
    {
        amount = Mth.clamp(1.0F - amount, 0.0F, 1.0F);
        int r = Math.max((int) (this.getRed() * amount), 0);
        int g = Math.max((int) (this.getGreen() * amount), 0);
        int b = Math.max((int) (this.getBlue() * amount), 0);

        return new Color(r, g, b, this.getAlpha());
    }

    /**
     * Creates a new {@code Color} that is a darker version of this {@code Color}. If the darkened color is black, then
     * a brighter color instance is returned.
     *
     * @return A new {@code Color} object that is a darker (or brighter if darkening results in a black color) with the
     * same {@code alpha} value.
     */
    @PublicAPI
    public Color darkerOrBrighter()
    {
        Color darker = this.darker();

        if (darker.equals(Color.BLACK))
            return this.brighter();

        return darker;
    }

    /**
     * This is <b>not</b> the same as {@link Color#isTransparent()}, which only checks if the color not <i>fully</i>
     * opaque.
     *
     * @return Whether this color equals a fully transparent color, which is equivalent to {@link Color#TRANSPARENT}.
     */
    @PublicAPI
    public boolean isEmpty()
    {
        return this.equals(Color.TRANSPARENT);
    }

    /**
     * Computes the hash code for this {@code Color}.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode()
    {
        return this.color;
    }

    /**
     * Determines whether another object is equal to this {@code Color}.
     *
     * <br><br>
     * The result is {@code true} if and only if the argument is not {@code null} and is a {@code Color} object that has
     * the same red, green, blue, and alpha values as this object.
     *
     * @param object The object to test for equality with this {@code Color}
     * @return {@code true} if the objects are the same; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object object)
    {
        return object instanceof Color && ((Color) object).get() == this.get();
    }

    /**
     * Returns a string representation of this {@code Color}. This method is intended to be used only for debugging
     * purposes. The content and format of the returned string might vary between implementations. The returned string
     * might be empty but cannot be {@code null}.
     *
     * @return A string representation of this {@code Color}.
     */
    @Override
    public String toString()
    {
        return getClass().getName() + "[r=" + getRed() + ",g=" + getGreen() + ",b=" + getBlue() + "]";
    }
}
