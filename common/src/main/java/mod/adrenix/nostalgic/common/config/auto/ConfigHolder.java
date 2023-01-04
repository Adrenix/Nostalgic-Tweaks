/*
 * This file is from the Cloth Config mod created by shedaniel and has been
 * modified to work with the Nostalgic Tweaks mod created by Adrenix.
 *
 * Copyright (C) 2020 - 2021 shedaniel
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package mod.adrenix.nostalgic.common.config.auto;

import mod.adrenix.nostalgic.common.config.auto.event.ConfigSerializeEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/**
 * Changes made:
 * - Cleaned up class to match codebase styling
 * - Added inferred documentation
 * - Added backup method
 *
 * @param <T> The class type that defines config structure.
 */

@ApiStatus.NonExtendable
public interface ConfigHolder<T extends ConfigData> extends Supplier<T>
{
    /**
     * Shortcut for retrieving the config instance used by this holder.
     * @return A config structure instance.
     */
    @Override
    default T get() { return getConfig(); }

    /**
     * Retrieve the config instance used by this holder.
     * @return A config structure instance.
     */
    T getConfig();

    /**
     * Save the data within the config instance to disk.
     */
    void save();

    /**
     * Load data saved on disk to the current config instance.
     * @return Whether the loading process was successful.
     */
    boolean load();

    /**
     * Create a backup file which will be a verbatim copy of what is currently saved on disk.
     * A runtime exception will be thrown if there is an IO issue.
     */
    void backup();

    /**
     * Register a listener for when the config file is saved.
     * @param save Instructions to perform when the config file is saved.
     */
    void registerSaveListener(ConfigSerializeEvent.Save<T> save);

    /**
     * Register a listener for when the config loads data from disk.
     * @param load Instructions to perform when the config file loads new data.
     */
    void registerLoadListener(ConfigSerializeEvent.Load<T> load);

    /**
     * Resets the config held by this holder to its default values.
     * <br>
     * Does not save the reset config to file, for that use {@link #save()}.
     */
    void resetToDefault();

    /**
     * Sets the config held by this holder.
     * <br>
     * Does not save the set config to file, for that use {@link #save()}.
     */
    void setConfig(T config);
}
