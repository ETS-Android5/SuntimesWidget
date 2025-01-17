/**
    Copyright (C) 2022 Forrest Guice
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

package com.forrestguice.suntimeswidget.alarmclock;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.forrestguice.suntimeswidget.R;
import com.forrestguice.suntimeswidget.SuntimesActivity;
import com.forrestguice.suntimeswidget.SuntimesActivityTestBase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;

@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class AlarmSettingsTest extends SuntimesActivityTestBase
{
    private Context context;

    @Rule
    public ActivityTestRule<SuntimesActivity> activityRule = new ActivityTestRule<>(SuntimesActivity.class);

    @Before
    public void init() {
        context = activityRule.getActivity();
    }

    @Test
    public void test_allPrefKeysUnique()
    {
        Context context = activityRule.getActivity();

        String[] testKeys = new String[] {
                AlarmSettings.PREF_KEY_ALARM_CATEGORY,
                AlarmSettings.PREF_KEY_ALARM_BATTERYOPT,
                AlarmSettings.PREF_KEY_ALARM_NOTIFICATIONS,
                AlarmSettings.PREF_KEY_ALARM_VOLUMES,
                AlarmSettings.PREF_KEY_ALARM_HARDAREBUTTON_ACTION,
                AlarmSettings.PREF_KEY_ALARM_SILENCEAFTER,
                AlarmSettings.PREF_KEY_ALARM_TIMEOUT,
                AlarmSettings.PREF_KEY_ALARM_SNOOZE,
                AlarmSettings.PREF_KEY_ALARM_UPCOMING,
                AlarmSettings.PREF_KEY_ALARM_AUTOENABLE,
                AlarmSettings.PREF_KEY_ALARM_AUTOVIBRATE,
                AlarmSettings.PREF_KEY_ALARM_RINGTONE_URI_ALARM,
                AlarmSettings.PREF_KEY_ALARM_RINGTONE_NAME_ALARM,
                AlarmSettings.PREF_KEY_ALARM_RINGTONE_URI_NOTIFICATION,
                AlarmSettings.PREF_KEY_ALARM_RINGTONE_NAME_NOTIFICATION,
                AlarmSettings.PREF_KEY_ALARM_ALLRINGTONES,
                AlarmSettings.PREF_KEY_ALARM_SHOWLAUNCHER,
                AlarmSettings.PREF_KEY_ALARM_POWEROFFALARMS,
                AlarmSettings.PREF_KEY_ALARM_UPCOMING_ALARMID,
                AlarmSettings.PREF_KEY_ALARM_FADEIN,
                AlarmSettings.PREF_KEY_ALARM_SORT
        };

        Set<String> set = new HashSet<>();
        for (String key : testKeys) {
            if (set.contains(key)) {
                fail("AlarmSettings key is not unique! " + key);
            } else set.add(key);
        }
    }

    @Test
    public void test_getRingtoneName()
    {
        Uri[] test_uri = new Uri[] {
                Uri.parse("content://dne"), Uri.parse("invalid"), Uri.parse(""), null,
                RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM),
                RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION)
        };
        for (Uri uri : test_uri)
        {
            String name = AlarmSettings.getRingtoneName(context, uri);
            assertNotNull(name);
        }
    }

    @Test
    public void test_setDefaultRingtone()
    {
        clearDefaultRingtone();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // test getDefaultRingtoneUri( resolveDefaults : false )
        assertEquals(AlarmSettings.VALUE_RINGTONE_DEFAULT, AlarmSettings.getDefaultRingtoneUri(context, AlarmClockItem.AlarmType.ALARM, false).toString());
        assertEquals(context.getString(R.string.configLabel_tagDefault), AlarmSettings.getDefaultRingtoneName(context, AlarmClockItem.AlarmType.ALARM));
        assertEquals(AlarmSettings.VALUE_RINGTONE_DEFAULT, AlarmSettings.getDefaultRingtoneUri(context, AlarmClockItem.AlarmType.NOTIFICATION, false).toString());
        assertEquals(context.getString(R.string.configLabel_tagDefault), AlarmSettings.getDefaultRingtoneName(context, AlarmClockItem.AlarmType.NOTIFICATION));

        // test setDefaultRingtone (ALARM)
        AlarmSettings.setDefaultRingtone(context, AlarmClockItem.AlarmType.ALARM);
        Uri defaultAlarmUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM);
        assertEquals(defaultAlarmUri.toString(), prefs.getString(AlarmSettings.PREF_KEY_ALARM_RINGTONE_URI_ALARM, "UNSET"));
        assertEquals(defaultAlarmUri.toString(), AlarmSettings.getDefaultRingtoneUri(context, AlarmClockItem.AlarmType.ALARM, false).toString());

        String defaultAlarmRingtoneName = AlarmSettings.getRingtoneName(context, defaultAlarmUri);
        assertEquals(defaultAlarmRingtoneName, prefs.getString(AlarmSettings.PREF_KEY_ALARM_RINGTONE_NAME_ALARM, "UNSET"));
        assertEquals(defaultAlarmRingtoneName, AlarmSettings.getDefaultRingtoneName(context, AlarmClockItem.AlarmType.ALARM));

        // test setDefaultRingtone (NOTIFICATION)
        AlarmSettings.setDefaultRingtone(context, AlarmClockItem.AlarmType.NOTIFICATION);
        Uri defaultNotificationUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION);
        assertEquals(defaultNotificationUri.toString(), prefs.getString(AlarmSettings.PREF_KEY_ALARM_RINGTONE_URI_NOTIFICATION, "UNSET"));
        assertEquals(defaultNotificationUri.toString(), AlarmSettings.getDefaultRingtoneUri(context, AlarmClockItem.AlarmType.NOTIFICATION, false).toString());

        String defaultNotificationRingtoneName = AlarmSettings.getRingtoneName(context, defaultNotificationUri);
        assertEquals(defaultNotificationRingtoneName, prefs.getString(AlarmSettings.PREF_KEY_ALARM_RINGTONE_NAME_NOTIFICATION, "UNSET"));
        assertEquals(defaultNotificationRingtoneName, AlarmSettings.getDefaultRingtoneName(context, AlarmClockItem.AlarmType.NOTIFICATION));

        // test getDefaultRingtoneUri( resolveDefaults : true )
        clearDefaultRingtone();
        assertEquals(defaultAlarmUri.toString(), AlarmSettings.getDefaultRingtoneUri(context, AlarmClockItem.AlarmType.ALARM, true).toString());
        assertEquals(defaultAlarmRingtoneName, AlarmSettings.getDefaultRingtoneName(context, AlarmClockItem.AlarmType.ALARM));
        assertEquals(defaultNotificationUri.toString(), AlarmSettings.getDefaultRingtoneUri(context, AlarmClockItem.AlarmType.NOTIFICATION, true).toString());
        assertEquals(defaultNotificationRingtoneName, AlarmSettings.getDefaultRingtoneName(context, AlarmClockItem.AlarmType.NOTIFICATION));
    }

    private void clearDefaultRingtone()
    {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefs.remove(AlarmSettings.PREF_KEY_ALARM_RINGTONE_URI_ALARM);
        prefs.remove(AlarmSettings.PREF_KEY_ALARM_RINGTONE_NAME_ALARM);
        prefs.remove(AlarmSettings.PREF_KEY_ALARM_RINGTONE_URI_NOTIFICATION);
        prefs.remove(AlarmSettings.PREF_KEY_ALARM_RINGTONE_NAME_NOTIFICATION);
        prefs.apply();
    }

    @Test
    public void test_upcomingAlarmID()
    {
        Long value0 = 10L;
        AlarmSettings.saveUpcomingAlarmId(context, value0);
        assertEquals(value0, AlarmSettings.loadUpcomingAlarmId(context));

        AlarmSettings.saveUpcomingAlarmId(context, null);
        assertNull(AlarmSettings.loadUpcomingAlarmId(context));
    }

    @Test
    public void test_alarmSort()
    {
        AlarmSettings.savePrefAlarmSort(context, AlarmSettings.SORT_BY_ALARMTIME);
        assertEquals(AlarmSettings.SORT_BY_ALARMTIME, AlarmSettings.loadPrefAlarmSort(context));

        AlarmSettings.savePrefAlarmSort(context, AlarmSettings.SORT_BY_CREATION);
        assertEquals(AlarmSettings.SORT_BY_CREATION, AlarmSettings.loadPrefAlarmSort(context));

        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefs.remove(AlarmSettings.PREF_KEY_ALARM_SORT);
        prefs.apply();
        assertEquals(AlarmSettings.PREF_DEF_ALARM_SORT, AlarmSettings.loadPrefAlarmSort(context));
    }

}
