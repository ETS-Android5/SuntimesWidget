/**
   Copyright (C) 2014-2018 Forrest Guice
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

package com.forrestguice.suntimeswidget.layouts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

import com.forrestguice.suntimeswidget.R;
import com.forrestguice.suntimeswidget.SuntimesUtils;
import com.forrestguice.suntimeswidget.calculator.SuntimesCalculator;
import com.forrestguice.suntimeswidget.calculator.SuntimesRiseSetData;
import com.forrestguice.suntimeswidget.calculator.SuntimesRiseSetDataset;
import com.forrestguice.suntimeswidget.settings.WidgetSettings;
import com.forrestguice.suntimeswidget.themes.SuntimesTheme;

/**
 * A 1x1 layout that displays azimuth and elevation.
 */
public class SunExtLayout_1x1_0 extends SunExtLayout
{
    public SunExtLayout_1x1_0()
    {
        super();
    }

    public SunExtLayout_1x1_0(int layoutID )
    {
        this.layoutID = layoutID;
    }

    @Override
    public void initLayoutID()
    {
        this.layoutID = R.layout.layout_widget_1x1_5;
    }

    @Override
    public void prepareForUpdate(SuntimesRiseSetDataset dataset)
    {
        dataset.dataActual.initCalculator();  // init calculator only; skipping full calculate()
    }

    @Override
    public void updateViews(Context context, int appWidgetId, RemoteViews views, SuntimesRiseSetDataset dataset)
    {
        super.updateViews(context, appWidgetId, views, dataset);
        SuntimesCalculator calculator = dataset.dataActual.calculator();
        SuntimesCalculator.SunPosition sunPosition = calculator.getSunPosition(dataset.now());

        String azimuthString = utils.formatAsDirection(sunPosition.azimuth, 2);
        CharSequence azimuth = (boldTime ? SuntimesUtils.createBoldColorSpan(azimuthString, azimuthString, highlightColor)
                                         : SuntimesUtils.createColorSpan(azimuthString, azimuthString, highlightColor));
        views.setTextViewText(R.id.info_sun_azimuth_current, azimuth);

        String elevationString = utils.formatAsDegrees(sunPosition.elevation, 2);
        CharSequence elevation = (boldTime ? SuntimesUtils.createBoldColorSpan(elevationString, elevationString, highlightColor)
                                           : SuntimesUtils.createColorSpan(elevationString, elevationString, highlightColor));
        views.setTextViewText(R.id.info_sun_elevation_current, elevation);

        boolean showLabels = WidgetSettings.loadShowLabelsPref(context, appWidgetId);
        int visibility = (showLabels ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.info_sun_azimuth_current_label, visibility);
        views.setViewVisibility(R.id.info_sun_elevation_current_label, visibility);
    }

    private int highlightColor = Color.WHITE;
    private boolean boldTime = false;

    @Override
    public void themeViews(Context context, RemoteViews views, SuntimesTheme theme)
    {
        super.themeViews(context, views, theme);
        highlightColor = theme.getTimeColor();
        boldTime = theme.getTimeBold();

        int textColor = theme.getTextColor();
        views.setTextColor(R.id.info_sun_azimuth_current_label, textColor);
        views.setTextColor(R.id.info_sun_elevation_current_label, textColor);
        views.setTextColor(R.id.info_sun_azimuth_current, textColor);
        views.setTextColor(R.id.info_sun_elevation_current, textColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            float textSize = theme.getTextSizeSp();
            views.setTextViewTextSize(R.id.info_sun_azimuth_current_label, TypedValue.COMPLEX_UNIT_SP, textSize);
            views.setTextViewTextSize(R.id.info_sun_elevation_current_label, TypedValue.COMPLEX_UNIT_SP, textSize);

            float timeSize = theme.getTimeSizeSp();
            views.setTextViewTextSize(R.id.info_sun_azimuth_current, TypedValue.COMPLEX_UNIT_SP, timeSize);
            views.setTextViewTextSize(R.id.info_sun_elevation_current, TypedValue.COMPLEX_UNIT_SP, timeSize);
        }
    }
}
