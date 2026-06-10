package com.example.dogfinder.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.dogfinder.MainActivity
import com.example.dogfinder.R

// Constantes para identificar la acción del broadcast y el canal de notificaciones
// Se declaran en companion object para poder usarlas también desde el Service.
class DownloadReceiver : BroadcastReceiver() {

    companion object {
        // La "etiqueta" que identifica este broadcast. El Service usa la misma.
        const val ACTION_DOWNLOAD_COMPLETE = "com.example.dogfinder.DOWNLOAD_COMPLETE"

        // Clave del dato extra que viaja en el Intent (la cantidad de fotos descargadas)
        const val EXTRA_COUNT = "extra_count"

        // ID del canal de notificaciones (necesario desde Android 8)
        private const val CHANNEL_ID = "downloads_channel"
        private const val NOTIFICATION_ID = 1001
    }

    // onReceive se ejecuta automáticamente cuando llega un broadcast
    // que coincide con la acción declarada en el AndroidManifest.
    override fun onReceive(context: Context, intent: Intent) {
        // Verificamos que sea nuestro broadcast (por seguridad)
        if (intent.action != ACTION_DOWNLOAD_COMPLETE) return

        // Sacamos la cantidad de descargas del Intent (0 si no vino)
        val count = intent.getIntExtra(EXTRA_COUNT, 0)

        // Mostramos una notificación al usuario
        mostrarNotificacion(context, count)
    }

    private fun mostrarNotificacion(context: Context, count: Int) {
        // 1. Crear el canal de notificaciones (Android 8+ lo requiere)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CHANNEL_ID,
                "Descargas",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones de descargas de favoritos"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }

        // 2. Intent que se ejecuta cuando el usuario toca la notificación.
        // Abre la MainActivity de la app.
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // 3. Construir la notificación
        val notificacion = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done) // ícono del sistema
            .setContentTitle("Descarga completada 🐶")
            .setContentText("Se descargaron $count fotos de tus perros favoritos")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // se cierra al tocarla
            .build()

        // 4. Mostrarla
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notificacion)
    }
}