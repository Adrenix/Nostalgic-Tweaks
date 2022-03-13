package mod.adrenix.nostalgic.client.config.annotation;

import mod.adrenix.nostalgic.client.config.gui.widget.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.ConfigReflect;
import mod.adrenix.nostalgic.client.config.reflect.EntryCache;
import mod.adrenix.nostalgic.client.config.reflect.GroupType;
import mod.adrenix.nostalgic.client.config.reflect.StatusType;
import mod.adrenix.nostalgic.util.NostalgicLang;
import net.minecraft.network.chat.TranslatableComponent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;

public abstract class NostalgicEntry
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
        public @interface Reload {}

        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface Restart {}

        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface Sub
        {
            Category group();
        }

        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        public @interface SliderType
        {
            Slider slider() default Slider.SWING_SLIDER;
        }

        public enum Slider
        {
            SWING_SLIDER,
            INTENSITY_SLIDER
        }
    }

    public enum Category
    {
        INTERFACE_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_GUI),
        ITEM_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_ITEM),
        PARTICLE_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_PARTICLE),
        WORLD_CANDY(GroupType.CANDY, NostalgicLang.Gui.CANDY_CATEGORY_WORLD);

        private final String langKey;
        private final GroupType groupType;

        Category(GroupType groupType, String langKey)
        {
            this.groupType = groupType;
            this.langKey = langKey;
        }

        public ConfigRowList.CategoryRow getCategory(ConfigRowList list)
        {
            return new ConfigRowList.CategoryRow(list, new TranslatableComponent(this.langKey), () -> {
                ArrayList<ConfigRowList.Row> rows = new ArrayList<>();
                HashMap<String, EntryCache<?>> translated = new HashMap<>();

                EntryCache.all().forEach(((key, entry) -> {
                    Gui.Sub sub = ConfigReflect.getAnnotation(entry.getGroup(), entry.getEntryKey(), Gui.Sub.class);
                    if (sub != null && sub.group() == this && entry.getGroup() == this.groupType)
                        translated.put(new TranslatableComponent(entry.getLangKey()).getString(), entry);
                }));

                SortedMap<String, EntryCache<?>> sorted = new TreeMap<>(translated);
                sorted.forEach((key, entry) -> rows.add(list.getRow(entry.getGroup(), entry.getEntryKey(), entry.getCurrent())));

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
