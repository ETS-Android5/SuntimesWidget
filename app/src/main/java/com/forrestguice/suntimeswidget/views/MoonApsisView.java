/**
    Copyright (C) 2019 Forrest Guice
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
package com.forrestguice.suntimeswidget.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.forrestguice.suntimeswidget.R;
import com.forrestguice.suntimeswidget.SuntimesUtils;
import com.forrestguice.suntimeswidget.calculator.SuntimesMoonData;
import com.forrestguice.suntimeswidget.calculator.SuntimesMoonData0;
import com.forrestguice.suntimeswidget.calculator.core.SuntimesCalculator;
import com.forrestguice.suntimeswidget.settings.WidgetSettings;
import com.forrestguice.suntimeswidget.themes.SuntimesTheme;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

@SuppressWarnings("Convert2Diamond")
public class MoonApsisView extends LinearLayout
{
    private static SuntimesUtils utils = new SuntimesUtils();

    private LinearLayout content;
    private RecyclerView card_view;
    private MoonApsisAdapter card_adapter;
    private LinearLayoutManager card_layout;

    public MoonApsisView(Context context)
    {
        super(context);
        init(context, null);
    }

    public MoonApsisView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        SuntimesUtils.initDisplayStrings(context);
        LayoutInflater.from(context).inflate(R.layout.layout_view_moonapsis, this, true);
        content = (LinearLayout)findViewById(R.id.moonapsis_layout);

        card_layout = new LinearLayoutManager(context);
        card_layout.setOrientation(LinearLayoutManager.HORIZONTAL);

        card_view = (RecyclerView)findViewById(R.id.moonapsis_card);
        card_view.setHasFixedSize(true);
        card_view.setItemViewCacheSize(7);
        card_view.setLayoutManager(card_layout);

        card_adapter = new MoonApsisAdapter(context);
        card_adapter.setItemWidth(Resources.getSystem().getDisplayMetrics().widthPixels / 3);  // initial width; 3 to screen; reassigned later in onSizeChanged

        card_view.setAdapter(card_adapter);
        card_view.scrollToPosition(MoonApsisAdapter.CENTER_POSITION);

        initTheme(context);
        if (isInEditMode()) {
            updateViews(context, null);
        }
    }

    @Override
    public void onSizeChanged( int w, int h, int oldWidth, int oldHeight )
    {
        super.onSizeChanged(w, h, oldWidth, oldHeight);
        if (card_adapter != null) {
            int margin = 8;
            card_adapter.setItemWidth((w - (margin * 2)) / 2);   // 2 to view
        }
    }

    public void initTheme(Context context)
    {
        /**int[] colorAttrs = { android.R.attr.textColorPrimary };
        TypedArray typedArray = context.obtainStyledAttributes(colorAttrs);
        int def = R.color.transparent;
        int timeColor = ContextCompat.getColor(context, typedArray.getResourceId(0, def));
        typedArray.recycle();

        for (MoonApsisField field : f) {
            field.themeView(timeColor);
        }*/
    }

    public void themeViews(Context context, SuntimesTheme theme) {
        card_adapter.applyTheme(context, theme);
    }

    public void updateViews( Context context, SuntimesMoonData data )
    {
        // EMPTY
    }

    public boolean isRising() {
        return card_adapter.isRising();
    }

    public void setOnClickListener( OnClickListener listener )
    {
        content.setOnClickListener(listener);
    }

    public void setOnLongClickListener( OnLongClickListener listener)
    {
        content.setOnLongClickListener(listener);
    }

    /**
     * MoonApsisAdapter
     */
    public static class MoonApsisAdapter extends RecyclerView.Adapter<MoonApsisField>
    {
        public static final int MAX_POSITIONS = 200;
        public static final int CENTER_POSITION = 100;

        private WeakReference<Context> contextRef;
        private HashMap<Integer, SuntimesMoonData0> data = new HashMap<>();
        private boolean isRising = false;

        private int colorNote, colorTitle, colorTime, colorText, colorDisabled, colorMoonrise, colorMoonset;

        public MoonApsisAdapter(Context context)
        {
            contextRef = new WeakReference<>(context);
            initData(context);
            initTheme(context);
        }

        private int itemWidth = -1;
        public void setItemWidth( int pixels ) {
            itemWidth = pixels;
            notifyDataSetChanged();
        }

        @Override
        public MoonApsisField onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layout = LayoutInflater.from(parent.getContext());
            View view = layout.inflate(R.layout.layout_view_moonapsis1, parent, false);
            return new MoonApsisField(view);
        }

        @Override
        public void onViewRecycled(MoonApsisField holder)
        {
            if (holder.position >= 0 && (holder.position < CENTER_POSITION - 1 || holder.position > CENTER_POSITION + 2)) {
                data.remove(holder.position);
                Log.d("DEBUG", "remove data " + holder.position);
            }
            holder.position = RecyclerView.NO_POSITION;
        }

        @Override
        public void onBindViewHolder(MoonApsisField holder, int position)
        {
            Context context = contextRef.get();
            if (context == null) {
                Log.e("MoonApsisAdapter", "null context!");
                return;
            }

            if (itemWidth > 0) {
                holder.resizeField(itemWidth);
            }

            SuntimesMoonData0 moon = initData(context, position);

            int rawOffset = (position - CENTER_POSITION);
            boolean isAgo = rawOffset < 0;
            int offset = rawOffset % 2;

            holder.isRising = (isRising ? (offset == 0) : (offset != 0));
            themeViews(context, holder, isAgo);
            holder.bindDataToPosition(context, moon, holder.isRising, position);
        }

        protected void initData( Context context ) {
            SuntimesMoonData0 moon = initData(context, CENTER_POSITION);
            Pair<Calendar, SuntimesCalculator.MoonPosition> perigee = moon.getMoonPerigee();
            Pair<Calendar, SuntimesCalculator.MoonPosition> apogee = moon.getMoonApogee();
            isRising = (perigee.first != null && !perigee.first.before(apogee.first));
        }

        protected SuntimesMoonData0 initData( Context context, int position )
        {
            int offset = (position - CENTER_POSITION) % 2;
            int firstPosition = position;
            if (offset > 0) {
                firstPosition = position - (offset);
            } else if (offset < 0) {
                firstPosition = position - (2 + (offset));
            }
            Log.d("DEBUG", "position: + " + position + ", firstPosition: " + firstPosition + ", isRising: " + isRising);

            SuntimesMoonData0 moon = data.get(firstPosition);
            if (moon == null)
            {
                moon = createData(context, firstPosition);
                for (int i=0; i<2; i++) {
                    data.put(firstPosition + i, moon);
                }
            }
            return moon;
        }

        protected SuntimesMoonData0 createData( Context context, int position )
        {
            SuntimesMoonData moon = new SuntimesMoonData(context, 0, "moon");
            if (position != CENTER_POSITION)
            {
                SuntimesMoonData0 moon0 = initData(context, CENTER_POSITION);
                Pair<Calendar, SuntimesCalculator.MoonPosition> perigee = moon0.getMoonPerigee();
                Pair<Calendar, SuntimesCalculator.MoonPosition> apogee = moon0.getMoonApogee();
                if (perigee.first != null && apogee.first != null)
                {
                    Calendar date = Calendar.getInstance(moon.timezone());
                    date.setTimeInMillis(isRising ? apogee.first.getTimeInMillis() : perigee.first.getTimeInMillis());

                    int rawOffset = position - CENTER_POSITION;
                    date.add(Calendar.HOUR, (int)((rawOffset / 2d) * 27.56d * 24d));
                    date.add(Calendar.DATE, -1);
                    moon.setTodayIs(date);
                }
            }
            moon.calculate();
            return moon;
        }

        @Override
        public int getItemCount() {
            return MAX_POSITIONS;
        }

        @SuppressLint("ResourceType")
        protected void initTheme(Context context)
        {
            int[] colorAttrs = { android.R.attr.textColorPrimary, android.R.attr.textColorSecondary, R.attr.text_disabledColor, R.attr.moonriseColor, R.attr.moonsetColor };
            TypedArray typedArray = context.obtainStyledAttributes(colorAttrs);
            int def = R.color.transparent;
            colorNote = colorTitle = colorTime = ContextCompat.getColor(context, typedArray.getResourceId(0, def));
            colorText = ContextCompat.getColor(context, typedArray.getResourceId(1, def));
            colorDisabled = ContextCompat.getColor(context, typedArray.getResourceId(2, def));
            colorMoonrise = ContextCompat.getColor(context, typedArray.getResourceId(3, def));
            colorMoonset = ContextCompat.getColor(context, typedArray.getResourceId(4, def));
            typedArray.recycle();
        }

        protected void applyTheme(Context context, SuntimesTheme theme)
        {
            colorNote = theme.getTimeColor();
            colorTitle = theme.getTitleColor();
            colorTime = theme.getTimeColor();
            colorText = theme.getTextColor();
            colorMoonrise = theme.getMoonriseTextColor();
            colorMoonset = theme.getMoonsetTextColor();
        }

        protected void themeViews(Context context, @NonNull MoonApsisField holder, boolean isAgo)
        {
            holder.timeColor = colorNote;
            MoonApsisField.disabledColor = colorDisabled;

            int titleColor = isAgo ? colorDisabled : colorTitle;
            int timeColor = isAgo ? colorDisabled : colorTime;
            int textColor = isAgo ? colorDisabled : colorText;
            int moonriseColor = isAgo ? colorDisabled : colorMoonrise;
            int moonsetColor = isAgo ? colorDisabled : colorMoonset;
            holder.themeView(titleColor, textColor, timeColor, moonriseColor, moonsetColor);
        }

        public boolean isRising() {
            return isRising;
        }
    }

    /**
     * MoonApsisField
     */
    public static class MoonApsisField extends RecyclerView.ViewHolder
    {
        public int position = RecyclerView.NO_POSITION;
        public View layout;
        public TextView labelView;
        public TextView timeView;
        public TextView positionView;
        public TextView noteView;
        public boolean isRising = true;

        public int timeColor = Color.WHITE;
        public static int disabledColor = Color.GRAY;

        public MoonApsisField(View view)
        {
            super(view);
            layout = view.findViewById(R.id.moonapsis_layout);
            labelView = (TextView)view.findViewById(R.id.moonapsis_label);
            timeView = (TextView)view.findViewById(R.id.moonapsis_date);
            positionView = (TextView)view.findViewById(R.id.moonapsis_distance);
            noteView = (TextView)view.findViewById(R.id.moonapsis_note);
        }

        public void bindDataToPosition(Context context, SuntimesMoonData0 data, boolean isRising, int position)
        {
            this.position = position;
            this.isRising = isRising;

            boolean showTime = WidgetSettings.loadShowTimeDatePref(context, 0);
            boolean showSeconds = WidgetSettings.loadShowSecondsPref(context, 0);
            boolean showWeeks = WidgetSettings.loadShowWeeksPref(context, 0);
            boolean showHours = WidgetSettings.loadShowHoursPref(context, 0);
            WidgetSettings.LengthUnit units = WidgetSettings.loadLengthUnitsPref(context, 0);

            if (data != null && data.isCalculated())
            {
                Pair<Calendar, SuntimesCalculator.MoonPosition> event = isRising ? data.getMoonApogee() : data.getMoonPerigee();
                updateField(context, event, showTime, showWeeks, showHours, showSeconds, units);

            } else {
                updateField(context, null, showTime, showWeeks, showHours, showSeconds, units);
            }
        }

        public void themeView(int titleColor, int textColor, int timeColor, int moonriseColor, int moonsetColor)
        {
            this.timeColor = timeColor;
            timeView.setTextColor(timeColor);
            positionView.setTextColor(isRising ? moonriseColor : moonsetColor);
            noteView.setTextColor(textColor);
            labelView.setTextColor(titleColor);
        }

        public void updateField(Context context, Pair<Calendar,SuntimesCalculator.MoonPosition> apsis, boolean showTime, boolean showWeeks, boolean showHours, boolean showSeconds, WidgetSettings.LengthUnit units)
        {
            if (apsis != null)
            {
                labelView.setText(context.getString(isRising ? R.string.label_apogee : R.string.label_perigee));
                timeView.setText(utils.calendarDateTimeDisplayString(context, apsis.first, showTime, showSeconds).getValue());
                noteView.setText(createApsisNote(context, apsis.first, showWeeks, showHours, timeColor));
                positionView.setText(SuntimesUtils.formatAsDistance(context, apsis.second.distance, units, 2, true).toString());

                timeView.setVisibility(View.VISIBLE);
                noteView.setVisibility(View.VISIBLE);
                positionView.setVisibility(View.VISIBLE);
                labelView.setVisibility(View.VISIBLE);

            } else {
                timeView.setVisibility(View.GONE);
                noteView.setVisibility(View.GONE);
                positionView.setVisibility(View.GONE);
                labelView.setVisibility(View.GONE);
            }
        }

        public void resizeField(int pixels) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) layout.getLayoutParams();
            params.width = pixels;
            layout.setLayoutParams( params );
        }

        private CharSequence createApsisNote(Context context, Calendar dateTime, boolean showWeeks, boolean showHours, int noteColor)
        {
            Calendar now = Calendar.getInstance();
            String noteText = (dateTime == null ? "" : utils.timeDeltaDisplayString(now.getTime(), dateTime.getTime(), showWeeks, showHours).toString());
            String noteString = now.after(dateTime) ? context.getString(R.string.ago, noteText) : context.getString(R.string.hence, noteText);
            return SuntimesUtils.createBoldColorSpan(null, noteString, noteText, noteColor);
        }

    }

}
