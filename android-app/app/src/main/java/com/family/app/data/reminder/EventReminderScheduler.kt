package com.family.app.data.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.family.app.ui.notification.EventReminderReceiver
import java.time.LocalDateTime
import java.time.ZoneId

class EventReminderScheduler(private val context: Context) {
    fun schedule(
        eventId: Long,
        eventTitle: String,
        eventTime: LocalDateTime,
        minutesBefore: Int
    ) {
        val triggerAtMillis = eventTime
            .minusMinutes(minutesBefore.toLong())
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val finalTrigger = if (triggerAtMillis <= System.currentTimeMillis()) {
            System.currentTimeMillis() + 5_000
        } else {
            triggerAtMillis
        }

        val intent = Intent(context, EventReminderReceiver::class.java).apply {
            putExtra(EventReminderReceiver.EXTRA_EVENT_ID, eventId)
            putExtra(EventReminderReceiver.EXTRA_EVENT_TITLE, eventTitle)
            putExtra(EventReminderReceiver.EXTRA_EVENT_TIME, eventTime.toString())
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            finalTrigger,
            pendingIntent
        )
    }
}
