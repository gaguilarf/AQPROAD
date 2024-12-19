package com.techteam.aqproad.Item

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.techteam.aqproad.ManageButton.ButtonManagment

class RoomView constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    private var room: Room? = null

    init {
        // En el caso de que no se pase un AttributeSet, podemos trabajar con valores por defecto
        attrs?.let {
            // Si necesitas extraer atributos personalizados aquí, puedes hacerlo, por ejemplo:
            val layoutWidth = it.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_width") ?: "match_parent"
            val layoutHeight = it.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height") ?: "match_parent"

            val density = resources.displayMetrics.density

            val widthInPx = if (layoutWidth == "match_parent") {
                // Si es "match_parent", usar el ancho de la vista
                width.toFloat()
            } else {
                // Convertir el valor a píxeles si es un valor en dip
                layoutWidth.replace("dip", "").toFloat() * density
            }

            val heightInPx = if (layoutHeight == "match_parent") {
                // Si es "match_parent", usar la altura de la vista
                height.toFloat()
            } else {
                // Convertir el valor a píxeles si es un valor en dip
                layoutHeight.replace("dip", "").toFloat() * density
            }
        }
    }

    fun setRoom(room: Room) {
        this.room = room
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val roomData = room ?: return

        // Obtener el tamaño en píxeles de la vista
        val layoutWidth = width.toFloat()
        val layoutHeight = height.toFloat()

        // Si las dimensiones de la habitación están en 0 (por alguna razón), no dibujamos nada
        if (roomData.left == 0f || roomData.top == 0f || roomData.right == 0f || roomData.bottom == 0f) {
            return
        }

        // Proporciones de la habitación
        val roomWidthRatio = roomData.right / 1000f
        val roomHeightRatio = roomData.bottom / 1000f

        // Calcular el tamaño proporcional de la habitación
        val roomWidth = layoutWidth * roomWidthRatio
        val roomHeight = layoutHeight * roomHeightRatio

        // Calcular la posición para centrar la habitación
        val left = (layoutWidth - roomWidth) / 2
        val top = (layoutHeight - roomHeight) / 2
        val right = left + roomWidth
        val bottom = top + roomHeight

        // Dibujar el rectángulo de la habitación con las dimensiones proporcionales
        paint.style = Paint.Style.FILL
        paint.color = Color.LTGRAY
        canvas.drawRect(left, top, right, bottom, paint)
    }
}
