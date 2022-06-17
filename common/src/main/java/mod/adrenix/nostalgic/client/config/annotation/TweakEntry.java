package mod.adrenix.nostalgic.client.config.annotation;

import mod.adrenix.nostalgic.client.config.gui.widget.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.ConfigReflect;
import mod.adrenix.nostalgic.client.config.reflect.TweakCache;
import mod.adrenix.nostalgic.client.config.reflect.GroupType;
import mod.adrenix.nostalgic.client.config.reflect.StatusType;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.network.chat.TranslatableComponent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;

public abstract class TweakEntry
{
    public abstract static class Gui
    {
        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface NoTooltip {}

        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface EntryStatus
        {
            StatusType status() default StatusType.WAIT;
        }

        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface Ignore {}

        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface IgnoreDisable {}

        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface New {}

        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface Client {}

        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface Server {}

        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface Warning {}

        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface Restart {}

        public enum Position
        {
            TOP,
            BOTTOM
        }

        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface Placement
        {
            Position pos();
            int order();
        }

        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface DisabledInteger
        {
            int disabled();
        }

        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface Sub
        {
            Category group();
        }

        public enum Slider
        {
            SWING_SLIDER,
            CLOUD_SLIDER,
            INTENSITY_SLIDER
        }

        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface SliderType
        {
            Slider slider() default Slider.SWING_SLIDER;
        }
    }

    public abstract static class Run
    {
        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface ReloadChunks {}

        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface ReloadResources {}
    }

    public enum Category
    {
        BLOCK_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_BLOCK),
        INTERFACE_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_GUI),
        ITEM_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_ITEM),
        LIGHTING_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_LIGHTING),
        PARTICLE_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_PARTICLE),
        TITLE_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_TITLE),
        WORLD_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_WORLD);

        private final String langKey;
        private final GroupType groupType;

        Category(GroupType groupType, String langKey)
        {
            this.groupType = groupType;
            this.langKey = langKey;
        }

        private ConfigRowList.CategoryRow getCategory(ConfigRowList list)
        {
            return new ConfigRowList.CategoryRow(list, new TranslatableComponent(this.langKey), () -> {
                ArrayList<ConfigRowList.Row> rows = new ArrayList<>();
                HashMap<String, TweakCache<?>> translated = new HashMap<>();
                HashMap<Integer, TweakCache<?>> bottom = new HashMap<>();
                HashMap<Integer, TweakCache<?>> top = new HashMap<>();

                TweakCache.all().forEach(((key, entry) -> {
                    Gui.Sub sub = ConfigReflect.getAnnotation(entry.getGroup(), entry.getKey(), Gui.Sub.class);
                    Gui.Placement placement = ConfigReflect.getAnnotation(entry.getGroup(), entry.getKey(), Gui.Placement.class);

                    if (sub != null && sub.group() == this && entry.getGroup() == this.groupType)
                    {
                        if (placement == null)
                            translated.put(new TranslatableComponent(entry.getLangKey()).getString(), entry);
                        else
                        {
                            if (entry.getPosition() == Gui.Position.TOP)
                                top.put(entry.getOrder(), entry);
                            else if (entry.getPosition() == Gui.Position.BOTTOM)
                                bottom.put(entry.getOrder(), entry);
                        }
                    }
                }));

                SortedMap<Integer, TweakCache<?>> sortTop = new TreeMap<>(top);
                SortedMap<String, TweakCache<?>> sortMiddle = new TreeMap<>(translated);
                SortedMap<Integer, TweakCache<?>> sortBottom = new TreeMap<>(bottom);

                sortTop.forEach((key, tweak) -> rows.add(list.getRow(tweak.getGroup(), tweak.getKey(), tweak.getCurrent())));
                sortMiddle.forEach((key, tweak) -> rows.add(list.getRow(tweak.getGroup(), tweak.getKey(), tweak.getCurrent())));
                sortBottom.forEach((key, tweak) -> rows.add(list.getRow(tweak.getGroup(), tweak.getKey(), tweak.getCurrent())));

                return rows;
            });
        }

        public static List<ConfigRowList.Row> getCategories(ConfigRowList list, GroupType group)
        {
            List<ConfigRowList.Row> subs = new ArrayList<>();

            EnumSet<Category> categories = EnumSet.allOf(Category.class);
            categories.forEach((category) -> {
                if (category.groupType == group)
                    subs.add(category.getCategory(list).add());
            });

            return subs;
        }
    }
}
