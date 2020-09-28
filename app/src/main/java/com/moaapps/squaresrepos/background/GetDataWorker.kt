package com.moaapps.squaresrepos.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.moaapps.squaresrepos.R
import com.moaapps.squaresrepos.utils.InitRetrofit
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.random.Random

class GetDataWorker(var context: Context, workerParams:WorkerParameters) : Worker(context, workerParams) {
    companion object{
        private const val TAG = "GetDataWorker"
    }



    override fun doWork(): Result {
        Log.d(TAG, "doWork: ")
        GlobalScope.launch{
            val response = InitRetrofit(context).retrofit.getRepos(1, 10)
            if (response.isSuccessful){
                Result.success()
                val firstTime = context.getSharedPreferences("PRE", Context.MODE_PRIVATE)
                    .getBoolean("f", true)
                if (!firstTime){
                    showNotification()
                }else{
                    context.getSharedPreferences("PRE", Context.MODE_PRIVATE)
                        .edit().putBoolean("f", false).apply()
                }
            }else{
                Result.retry()
            }
        }
        return Result.success()
    }

    private fun showNotification(){
        val channelId = "Notification"
        val notification = NotificationCompat.Builder(context,channelId)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle("Data Updated")
            .setContentText("Repositories Data is updated")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notificationManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(channelId, "Updates",
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(notificationChannel)
            manager
        }else{
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager
        }

        val id = Random.nextInt()
        notificationManager.notify(id, notification.build())
    }
}