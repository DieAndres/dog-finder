package com.example.dogfinder.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.example.dogfinder.data.local.AppDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DogDownloadService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "Iniciando descarga de favoritos...", Toast.LENGTH_SHORT).show()
        
        serviceScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val favorites = db.dogDao().getAllFavorites().first()
            
            if (favorites.isEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "No hay favoritos para descargar", Toast.LENGTH_SHORT).show()
                }
                stopSelf()
                return@launch
            }

            favorites.forEachIndexed { index, dog ->
                try {
                    downloadImage(dog.imageUrl, "dog_fav_${index}.jpg")
                    Log.d("DogDownloadService", "Descargado: ${dog.imageUrl}")
                } catch (e: Exception) {
                    Log.e("DogDownloadService", "Error descargando ${dog.imageUrl}: ${e.message}")
                }
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, "Descarga de favoritos completada", Toast.LENGTH_LONG).show()
            }
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun downloadImage(urlStr: String, fileName: String) {
        val url = URL(urlStr)
        val connection = url.openConnection()
        connection.connect()
        
        val input = connection.getInputStream()
        // Guardamos en la carpeta privada de la app para que la descarga sea segura,
        // complicado debido a los cambios de seguridad que puso Google en los últimos años (Android 10 en adelante)
        val file = File(getExternalFilesDir(null), fileName)
        val output = FileOutputStream(file)

        val data = ByteArray(1024)
        var count: Int
        while (input.read(data).also { count = it } != -1) {
            output.write(data, 0, count)
        }

        output.flush()
        output.close()
        input.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
