package com.nyx.menu
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.*
class FloatingMenuService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var menuView: View
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 100; params.y = 100
        menuView = LayoutInflater.from(this).inflate(R.layout.floating_menu, null)
        
        val spinner = menuView.findViewById<Spinner>(R.id.spinnerTarget)
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, arrayOf("Cabeça", "Pescoço", "Peito (Legit)"))
        val txtFov = menuView.findViewById<TextView>(R.id.txtFov)
        menuView.findViewById<SeekBar>(R.id.seekFov).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, p: Int, f: Boolean) { txtFov.text = "FOV: ${p + 70}" }
            override fun onStartTrackingTouch(s: SeekBar?) {}
            override fun onStopTrackingTouch(s: SeekBar?) {}
        })
        menuView.findViewById<Button>(R.id.btnClose).setOnClickListener { stopSelf() }
        
        menuView.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0; private var initialY = 0; private var initialTouchX = 0f; private var initialTouchY = 0f
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x; initialY = params.y; initialTouchX = event.rawX; initialTouchY = event.rawY; return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(menuView, params); return true
                    }
                }
                return false
            }
        })
        windowManager.addView(menuView, params)
    }
    override fun onDestroy() {
        super.onDestroy()
        if (::menuView.isInitialized) windowManager.removeView(menuView)
    }
}
