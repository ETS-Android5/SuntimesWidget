/**
    Copyright (C) 2020 Forrest Guice
    This file is part of SuntimesWidget.

    SuntimesWidget is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SuntimesWidget is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SuntimesWidget.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.forrestguice.suntimeswidget.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

import com.forrestguice.suntimeswidget.R;

@SuppressWarnings("Convert2Diamond")
public class SolarEventIcons
{
    @SuppressLint("ResourceType")
    public static int getIconResID(Context context, SolarEvents event)
    {
        switch (event)
        {
            case MORNING_ASTRONOMICAL: case MORNING_NAUTICAL: case MORNING_BLUE8: case MORNING_CIVIL:
            case MORNING_BLUE4: case SUNRISE: case MORNING_GOLDEN: case MOONRISE:
                return R.drawable.svg_sunrise;

            case EVENING_GOLDEN: case SUNSET: case EVENING_BLUE4: case EVENING_CIVIL: case EVENING_BLUE8:
            case EVENING_NAUTICAL: case EVENING_ASTRONOMICAL: case MOONSET:
                return R.drawable.svg_sunset;

            case FIRSTQUARTER: return R.drawable.svg_moon_q1;
            case THIRDQUARTER: return R.drawable.svg_moon_q3;

            case NOON: return getResID(context, R.attr.sunnoonIcon, R.drawable.ic_noon_large);
            case NEWMOON: return getResID(context, R.attr.moonPhaseIcon0, R.drawable.ic_moon_new);
            case FULLMOON: return getResID(context, R.attr.moonPhaseIcon2, R.drawable.ic_moon_full);

            case EQUINOX_SPRING: case SOLSTICE_SUMMER: case EQUINOX_AUTUMNAL: case SOLSTICE_WINTER:
                return R.drawable.svg_season;

            default: return 0;
        }
    }

    public static float[] getIconScale(SolarEvents event) {
        return new float[] {1f, 1f};
    }

    @SuppressLint("ResourceType")
    public static Integer getIconTint(Context context, SolarEvents event)
    {
        switch (event)
        {
            case MORNING_ASTRONOMICAL: case MORNING_NAUTICAL:
            case MORNING_BLUE8: case MORNING_CIVIL:
            case MORNING_BLUE4: case SUNRISE: case MORNING_GOLDEN:
                return getColor(context, R.attr.sunriseColor, R.color.sunIcon_color_rising_dark);

            case EVENING_GOLDEN: case SUNSET:
            case EVENING_BLUE4: case EVENING_CIVIL:
            case EVENING_BLUE8: case EVENING_NAUTICAL:
            case EVENING_ASTRONOMICAL:
                return getColor(context, R.attr.sunsetColor, R.color.sunIcon_color_setting_dark);

            case EQUINOX_SPRING: return getColor(context, R.attr.springColor, R.color.springColor_dark);
            case SOLSTICE_SUMMER: return getColor(context, R.attr.summerColor, R.color.summerColor_dark);
            case EQUINOX_AUTUMNAL: return getColor(context, R.attr.fallColor, R.color.fallColor_dark);
            case SOLSTICE_WINTER: return getColor(context, R.attr.winterColor, R.color.winterColor_dark);

            case MOONRISE: case FIRSTQUARTER: return getColor(context, R.attr.moonriseColor, R.color.moonIcon_color_rising_dark);
            case MOONSET: case THIRDQUARTER: return getColor(context, R.attr.moonsetColor, R.color.moonIcon_color_setting_dark);

            default: return null;
        }
    }

    public static int getIconDrawablePadding(Context context, @NonNull SolarEvents event)
    {
        switch (event)
        {
            case FIRSTQUARTER: case THIRDQUARTER:
            case FULLMOON: case NEWMOON: case NOON:
                return (int)context.getResources().getDimension(R.dimen.eventIcon_margin1);
            default:
                return (int)context.getResources().getDimension(R.dimen.eventIcon_margin);
        }
    }

    public static int getIconDrawableInset(Context context, @NonNull SolarEvents event)
    {
        switch (event)
        {
            case FULLMOON: case NEWMOON: case NOON:
                return (int)context.getResources().getDimension(R.dimen.eventIcon_margin1);
            default:
                return 0;
        }
    }

    public static Drawable getIconDrawable(Context context, @NonNull SolarEvents event)
    {
        return getIconDrawable(context, event, -1, -1);
    }
    public static Drawable getIconDrawable(Context context, @NonNull SolarEvents event, int width, int height)
    {
        Drawable eventIcon = ContextCompat.getDrawable(context, SolarEventIcons.getIconResID(context, event)).mutate();
        Integer tintColor = SolarEventIcons.getIconTint(context, event);
        if (tintColor != null)
        {
            if (Build.VERSION.SDK_INT >= 21) {
                DrawableCompat.setTint(eventIcon, tintColor);
                DrawableCompat.setTintMode(eventIcon, PorterDuff.Mode.SRC_IN);
            } else {
                eventIcon.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
            }
        }

        int inset = getIconDrawableInset(context, event);
        if (inset > 0) {
            eventIcon = new InsetDrawable(eventIcon, inset, inset, inset, inset);
        }

        float[] scale = getIconScale(event);
        if (width > 0 && height > 0 && scale[0] > 0 && scale[1] > 0) {
            eventIcon.setBounds(0, 0, (int)(scale[0] * width), (int)(scale[1] * height));
        }

        return eventIcon;
    }

    public static int getResID(Context context, int attr, int defResID)
    {
        int[] attrs = {attr};
        TypedArray a = context.obtainStyledAttributes(attrs);
        int resID = a.getResourceId(0, defResID);
        a.recycle();
        return resID;
    }

    public static int getColor(Context context, int attr, int defColor)
    {
        int[] attrs = {attr};
        TypedArray a = context.obtainStyledAttributes(attrs);
        int color = ContextCompat.getColor(context, a.getResourceId(0, defColor));
        a.recycle();
        return color;
    }

}
