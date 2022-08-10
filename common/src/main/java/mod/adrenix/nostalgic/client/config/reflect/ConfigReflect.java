package mod.adrenix.nostalgic.client.config.reflect;

import com.mojang.datafixers.util.Pair;
import mod.adrenix.nostalgic.client.config.ClientConfig;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;

import static mod.adrenix.nostalgic.client.config.CommonRegistry.cache;

public abstract class ConfigReflect
{
    private static final ClientConfig DEFAULT_CONFIG = new ClientConfig();
    private static Pair<Class<?>, Object> getGroupClass(GroupType group, ClientConfig config)
    {
        switch (group)
        {
            case ROOT -> { return new Pair<>(ClientConfig.class, config); }
            case SOUND -> { return new Pair<>(ClientConfig.Sound.class, config.sound); }
            case CANDY -> { return new Pair<>(ClientConfig.EyeCandy.class, config.eyeCandy); }
            case ANIMATION -> { return new Pair<>(ClientConfig.Animation.class, config.animation); }
            case SWING -> { return new Pair<>(ClientConfig.Swing.class, config.swing); }
            case GUI -> { return new Pair<>(ClientConfig.Gui.class, config.gui); }
        }

        return new Pair<>(ClientConfig.class, config);
    }

    public static void setConfig(GroupType group, String key, Object value) { setField(group, cache, key, value); }
    public static HashMap<String, Object> getGroup(GroupType group) { return fetchFields(group, cache); }
    public static <T> T getDefault(GroupType group, String key) { return getFieldValue(group, key, DEFAULT_CONFIG); }
    public static <T> T getCurrent(GroupType group, String key) { return getFieldValue(group, key, cache); }
    private static <T> T getFieldValue(GroupType group, String key, ClientConfig config) { return findField(group, config, key); }

    @SuppressWarnings("unchecked")
    private static <T> T findField(GroupType group, ClientConfig config, String key)
    {
        Pair<Class<?>, Object> groupClass = getGroupClass(group, config);
        Class<?> reference = groupClass.getFirst();
        Object instance = groupClass.getSecond();

        for (Field field : reference.getFields())
        {
            try
            {
                if (key.equals(field.getName()))
                    return (T) field.get(instance);
            }
            catch (IllegalArgumentException | IllegalAccessException e) { e.printStackTrace(); }
        }

        return null;
    }

    private static HashMap<String, Object> fetchFields(GroupType group, ClientConfig config)
    {
        Pair<Class<?>, Object> groupClass = getGroupClass(group, config);
        Class<?> reference = groupClass.getFirst();
        Object instance = groupClass.getSecond();

        HashMap<String, Object> fields = new HashMap<>();
        for (Field field : reference.getFields())
        {
            try
            {
                fields.put(field.getName(), field.get(instance));
            }
            catch (IllegalArgumentException | IllegalAccessException e) { e.printStackTrace(); }
        }

        return fields;
    }

    private static void setField(GroupType group, ClientConfig config, String key, Object value)
    {
        Pair<Class<?>, Object> groupClass = getGroupClass(group, config);
        Class<?> reference = groupClass.getFirst();
        Object instance = groupClass.getSecond();

        for (Field field : reference.getFields())
        {
            try
            {
                if (field.getName().equals(key))
                {
                    field.set(instance, value);
                    break;
                }
            }
            catch (IllegalArgumentException | IllegalAccessException e) { e.printStackTrace(); }
        }
    }

    @Nullable
    public static <T extends Annotation> T getAnnotation(GroupType group, String key, Class<T> annotation)
    {
        return getFieldAnnotation(group, key, annotation);
    }

    @Nullable
    private static <T extends Annotation> T getFieldAnnotation(GroupType group, String key, Class<T> annotation)
    {
        Pair<Class<?>, Object> groupClass = getGroupClass(group, ConfigReflect.DEFAULT_CONFIG);
        Class<?> reference = groupClass.getFirst();

        for (Field field : reference.getFields())
            if (field.getName().equals(key))
                return field.getAnnotation(annotation);
        return null;
    }
}
