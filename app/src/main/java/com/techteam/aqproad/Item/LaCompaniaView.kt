package com.techteam.aqproad.Item

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.techteam.aqproad.ManageButton.ButtonManagment
import com.techteam.aqproad.R

class LaCompaniaView constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    // Dimensiones de desplazamiento
    private val offsetX = 300f
    private val offsetY = 50f
    private val circleRadius = 15f
    private val centerX = (170.6f + 329.136f) / 2 + offsetX
    private val centerY = 169.456f + offsetY - 15f

    // Lista de habitaciones con sus dimensiones
    private val rooms = listOf(
        Room("Sacristía", 170.6f + offsetX, 35f + offsetY, 329.136f + offsetX, 169.456f + offsetY),
        Room("Retablo", 329.136f + offsetX, 35f + offsetY, 499.537f + offsetX, 169.456f + offsetY),
        Room("Espacio 1", 499.537f + offsetX, 35f + offsetY, 607.582f + offsetX, 169.456f + offsetY),
        Room("Espacio 2", 607.582f + offsetX, 35f + offsetY, 715.627f + offsetX, 169.456f + offsetY),
        Room("Espacio 3", 715.627f + offsetX, 35f + offsetY, 823.672f + offsetX, 169.456f + offsetY),
        Room("Espacio 4", 823.672f + offsetX, 35f + offsetY, 924.528f + offsetX, 169.456f + offsetY),
        Room("Espacio 5", 499.537f + offsetX, 325.521f + offsetY, 607.582f + offsetX, 455f + offsetY),
        Room("Espacio 6", 607.582f + offsetX, 325.521f + offsetY, 715.627f + offsetX, 455f + offsetY),
        Room("Espacio 7", 715.627f + offsetX, 325.521f + offsetY, 823.672f + offsetX, 455f + offsetY),
        Room("Espacio 8", 823.672f + offsetX, 325.521f + offsetY, 924.528f + offsetX, 455f + offsetY),
        Room("Capilla de San Ignacio", 30.67f + offsetX, 325.521f + offsetY, 206.89f + offsetX, 501.74f + offsetY),
        Room("Antesacristia antigua", 206.89f + offsetX, 325.521f + offsetY, 356.41f + offsetX, 501.74f + offsetY)
    )

    init {
        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                 rooms.forEach { room ->
                    if (isTouchInsideRect(event.x, event.y, room)) {
                        val fragmentTransaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                        val roomFragment = RoomFragment()

                        val bundle = Bundle()
                        bundle.putParcelable("room", room)

                        roomFragment.arguments = bundle

                        fragmentTransaction.replace(R.id.main_container, roomFragment)
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()

                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }

    private fun isTouchInsideCircle(x: Float, y: Float): Boolean {
        val dx = x - centerX
        val dy = y - centerY
        return dx * dx + dy * dy <= circleRadius * circleRadius
    }

    private fun isTouchInsideRect(x: Float, y: Float, room: Room): Boolean {
        return x >= room.left && x <= room.right && y >= room.top && y <= room.bottom
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Set paint properties
        paint.style = Paint.Style.STROKE
        paint.color = Color.BLACK
        paint.strokeWidth = 2f

        // Dibujar los rectángulos de todas las habitaciones
        rooms.forEach { room ->
            canvas.drawRect(room.left, room.top, room.right, room.bottom, paint)
            paint.textSize = 21f
            canvas.drawText(room.name, room.left + 20f, room.top + 50f, paint) // Ajuste de posición para el texto

        }

        // Pintar el círculo de color #FF7F50
        paint.color = Color.parseColor("#FF7F50")
        canvas.drawCircle(centerX, centerY, circleRadius, paint)

        // Restaurar color negro para otros elementos
        paint.color = Color.BLACK
    }
}
