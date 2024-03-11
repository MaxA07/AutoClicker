package com.example.autoclicker

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast

class FloatingViewService : Service() {


    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: Button
    private var initialX: Int = 100
    private var initialY: Int = 100
    private var initialTouchX: Float = 100.toFloat()
    private var initialTouchY: Float = 110.toFloat()
    var isMoving = false

    private fun checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${applicationContext.packageName}")
            )
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        checkOverlayPermission()
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()

        // Создание плавающей кнопки
        floatingView = Button(this)
        floatingView.text = "Click"
        floatingView.setBackgroundColor(Color.GREEN)
        floatingView.x = 0F
        floatingView.x = 0F
        floatingView.isClickable = true
        //floatingView.setBackgroundResource(R.drawable.transparent_background)

        // Настройка параметров для плавающей view
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        // Установка начальных координат
        params.gravity = Gravity.CENTER or Gravity.CENTER
        params.x = 110
        params.y = 100

        // Добавление плавающей view в WindowManager
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(floatingView, params)

//        floatingView.setOnClickListener{
//            Toast.makeText(this, "sadsa", Toast.LENGTH_SHORT)
//                .show()
//        }

        // Добавление обработчиков для перемещения плавающей view
        floatingView.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Сохраняем начальные позиции
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isMoving = false
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    isMoving = true
                    // Вычисляем смещение
                    val dx = event.rawX - initialTouchX
                    val dy = event.rawY - initialTouchY

                    // Обновляем параметры позиции
                    params.x = (initialX + dx).toInt()
                    params.y = (initialY + dy).toInt()

                    // Обновляем расположение плавающей view
                    windowManager.updateViewLayout(floatingView, params)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (!isMoving) {
                        // Обработка клика здесь
                        Log.d("tag", "Button clicked")
                        Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show()
                    }
                    false // Возвращаем false, чтобы событие не потреблялось полностью

                }

                else -> false
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // Удаление плавающей view при завершении службы
        if (::floatingView.isInitialized) {
            windowManager.removeView(floatingView)
        }
    }
}