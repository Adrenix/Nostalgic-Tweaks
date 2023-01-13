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

package mod.adrenix.nostalgic.common.config.auto.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Changes made:
 * - Removed unused fields/methods/classes
 * - Added inferred documentation
 *
 * Attach this to your config POJO.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Config
{
    /**
     * This is the name of the config file.
     * It will be used when saving data to the disk in a mod loader's config folder.
     *
     * @return The name of the config this annotation is attached to.
     */
    String name();
}
