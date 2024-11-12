package com.techteam.aqproad.ManageButton

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.TypedValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.techteam.aqproad.R
import java.io.InputStreamReader

class ButtonManagment(context: Context) {

    private val buttonMap: Map<String, List<ButtonRegion>> = readJson(context)

    //##### INICIO FORMATO BOTONES ####
    //stilo para botones
    private val buttonPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val buttonText = Paint().apply {
        color = Color.WHITE
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 6f, context.resources.displayMetrics)
        isAntiAlias = true
    }
    //radio boton
    private val buttonRadio = 11f
    //mov del texto respecto al centro del circulo
    private val movX = 10.5f
    private val movY = 7f

    //#### FIN FORMATO BOTONES ####

    fun draw(canvas: Canvas) {
        // Recorre cada espacio y dibuja sus botones
        for ((_, buttonList) in buttonMap) {
            for (button in buttonList) {
                button.draw(canvas)
            }
        }

        /*buttonRetCrucificcion(canvas)
        buttonEsp1(canvas)
        buttonEsp2(canvas)
        buttonEsp3(canvas)
        //retablo principal
        buttonRePrincipal(canvas)
        //coro
        buttonCoro(canvas)
        //retablo de los fundadores botones
        buttonRetFundadores(canvas)
        buttonEsp5(canvas)
        buttonEsp6(canvas)
        buttonEsp7(canvas)
        buttonCapillaIgnacio(canvas)
        buttonAnteSacristiaAnt(canvas)*/
    }

    fun findButton(x: Float, y: Float): String {
        // Recorre cada espacio y cada botón para encontrar el tocado
        for ((_, buttonList) in buttonMap) {
            for (button in buttonList) {
                if (button.contains(x, y)) {
                    // Devuelve el texto
                    return button.text
                }
            }
        }
        return "none" // Devuelve none si no se encontró ningún botón
    }

    private fun readJson(context: Context): Map<String, List<ButtonRegion>> {
        val inputStream = context.resources.openRawResource(R.raw.cords)
        val reader = InputStreamReader(inputStream)

        val gson = Gson()
        val rawMapType = object : TypeToken<Map<String, List<ButtonData>>>() {}.type
        val rawMap = gson.fromJson<Map<String, List<ButtonData>>>(reader, rawMapType)

        reader.close()

        // Convierte el JSON en un Map<String, List<CircleRegion>>
        val circleMap = mutableMapOf<String, List<ButtonRegion>>()
        for ((spaceKey, circles) in rawMap) {
            val circleRegions = circles.map { circleData ->
                ButtonRegion(
                    centerX = circleData.centerX,
                    centerY = circleData.centerY,
                    text = circleData.text,
                    context = context
                )
            }
            circleMap[spaceKey] = circleRegions
        }

        return circleMap
    }

    /*private fun buttonRetCrucificcion(canvas: Canvas) {
        val centro47 = PointF(432f, 50f)
        canvas.drawCircle(centro47.x, centro47.y, buttonRadio, buttonPaint)
        canvas.drawText("47", centro47.x - movX, centro47.y + movY, buttonText)

        val centro46 = PointF(501f, 50f)
        canvas.drawCircle(centro46.x, centro46.y, buttonRadio, buttonPaint)
        canvas.drawText("46", centro46.x - movX, centro46.y + movY, buttonText)

        val centro45 = PointF(523f, 92f)
        canvas.drawCircle(centro45.x, centro45.y, buttonRadio, buttonPaint)
        canvas.drawText("45", centro45.x - movX, centro45.y + movY, buttonText)

        val centro48 = PointF(410f, 92f)
        canvas.drawCircle(centro48.x, centro48.y, buttonRadio, buttonPaint)
        canvas.drawText("48", centro48.x - movX, centro48.y + movY, buttonText)
    }

    private fun buttonEsp1(canvas: Canvas) {
        val centro42 = PointF(556f, 92f)
        canvas.drawCircle(centro42.x, centro42.y, buttonRadio, buttonPaint)
        canvas.drawText("42", centro42.x - movX, centro42.y + movY, buttonText)

        val centro38 = PointF(632f, 92f)
        canvas.drawCircle(centro38.x, centro38.y, buttonRadio, buttonPaint)
        canvas.drawText("38", centro38.x - movX, centro38.y + movY, buttonText)

        val centro44 = PointF(574f, 167f)
        canvas.drawCircle(centro44.x, centro44.y, buttonRadio, buttonPaint)
        canvas.drawText("44", centro44.x - movX, centro44.y + movY, buttonText)

        val centro43 = PointF(610f, 167f)
        canvas.drawCircle(centro43.x, centro43.y, buttonRadio, buttonPaint)
        canvas.drawText("43", centro43.x - movX, centro43.y + movY, buttonText)

        val centro40 = PointF(574f, 50f)
        canvas.drawCircle(centro40.x, centro40.y, buttonRadio, buttonPaint)
        canvas.drawText("40", centro40.x - movX, centro40.y + movY, buttonText)

        val centro41 = PointF(592f, 50f)
        canvas.drawCircle(centro41.x, centro41.y, buttonRadio, buttonPaint)
        canvas.drawText("40", centro41.x - movX, centro41.y + movY, buttonText)

        val centro39 = PointF(610f, 50f)
        canvas.drawCircle(centro39.x, centro39.y, buttonRadio, buttonPaint)
        canvas.drawText("39", centro39.x - movX, centro39.y + movY, buttonText)
    }

    private fun buttonEsp2(canvas: Canvas) {
        val centro35 = PointF(740f, 92f)
        canvas.drawCircle(centro35.x, centro35.y, buttonRadio, buttonPaint)
        canvas.drawText("35", centro35.x - movX, centro35.y + movY, buttonText)

        val centro36 = PointF(718f, 167f)
        canvas.drawCircle(centro36.x, centro36.y, buttonRadio, buttonPaint)
        canvas.drawText("36", centro36.x - movX, centro36.y + movY, buttonText)

        val centro37 = PointF(682f, 167f)
        canvas.drawCircle(centro37.x, centro37.y, buttonRadio, buttonPaint)
        canvas.drawText("37", centro37.x - movX, centro37.y + movY, buttonText)
    }

    private fun buttonEsp3(canvas: Canvas) {
        val centro31 = PointF(824f, 167f)
        canvas.drawCircle(centro31.x, centro31.y, buttonRadio, buttonPaint)
        canvas.drawText("31", centro31.x - movX, centro31.y + movY, buttonText)

        val centro32 = PointF(792f, 167f)
        canvas.drawCircle(centro32.x, centro32.y, buttonRadio, buttonPaint)
        canvas.drawText("32", centro32.x - movX, centro32.y + movY, buttonText)

        val centro33 = PointF(846f, 92f)
        canvas.drawCircle(centro33.x, centro33.y, buttonRadio, buttonPaint)
        canvas.drawText("33", centro33.x - movX, centro33.y + movY, buttonText)

        val centro34 = PointF(770f, 92f)
        canvas.drawCircle(centro34.x, centro34.y, buttonRadio, buttonPaint)
        canvas.drawText("34", centro34.x - movX, centro34.y + movY, buttonText)
    }

    private fun buttonRePrincipal(canvas: Canvas) {
        val centro1 = PointF(224f, 245f)
        canvas.drawCircle(centro1.x, centro1.y, buttonRadio, buttonPaint)
        canvas.drawText("01", centro1.x - movX, centro1.y + movY, buttonText)

        val centro2 = PointF(352.5f, 175f)
        canvas.drawCircle(centro2.x, centro2.y, buttonRadio, buttonPaint)
        canvas.drawText("02", centro2.x - movX, centro2.y + movY, buttonText)

        val centro3 = PointF(352.5f, 315f)
        canvas.drawCircle(centro3.x, centro3.y, buttonRadio, buttonPaint)
        canvas.drawText("03", centro3.x - movX, centro3.y + movY, buttonText)
    }

    private fun buttonCoro(canvas: Canvas) {
        val centro27 = PointF(950f, 245f)
        canvas.drawCircle(centro27.x, centro27.y, buttonRadio, buttonPaint)
        canvas.drawText("27", centro27.x - movX, centro27.y + movY, buttonText)

        val centro28 = PointF(950f, 218f)
        canvas.drawCircle(centro28.x, centro28.y, buttonRadio, buttonPaint)
        canvas.drawText("28", centro28.x - movX, centro28.y + movY, buttonText)

        val centro29 = PointF(916f, 175f)
        canvas.drawCircle(centro29.x, centro29.y, buttonRadio, buttonPaint)
        canvas.drawText("29", centro29.x - movX, centro29.y + movY, buttonText)

        val centro30 = PointF(876f, 175f)
        canvas.drawCircle(centro30.x, centro30.y, buttonRadio, buttonPaint)
        canvas.drawText("30", centro30.x - movX, centro30.y + movY, buttonText)

        val centro26 = PointF(950f, 272f)
        canvas.drawCircle(centro26.x, centro26.y, buttonRadio, buttonPaint)
        canvas.drawText("26", centro26.x - movX, centro26.y + movY, buttonText)

        val centro24 = PointF(876f, 315f)
        canvas.drawCircle(centro24.x, centro24.y, buttonRadio, buttonPaint)
        canvas.drawText("24", centro24.x - movX, centro24.y + movY, buttonText)

        val centro25 = PointF(916f, 315f)
        canvas.drawCircle(centro25.x, centro25.y, buttonRadio, buttonPaint)
        canvas.drawText("25", centro25.x - movX, centro25.y + movY, buttonText)
    }

    private fun buttonRetFundadores(canvas: Canvas) {
        val centro4 = PointF(410f, 400f)
        canvas.drawCircle(centro4.x, centro4.y, buttonRadio, buttonPaint)
        canvas.drawText("04", centro4.x - movX, centro4.y + movY, buttonText)

        val centro5 = PointF(432f, 442f)
        canvas.drawCircle(centro5.x, centro5.y, buttonRadio, buttonPaint)
        canvas.drawText("05", centro5.x - movX, centro5.y + movY, buttonText)

        val centro6 = PointF(501f, 442f)
        canvas.drawCircle(centro6.x, centro6.y, buttonRadio, buttonPaint)
        canvas.drawText("06", centro6.x - movX, centro6.y + movY, buttonText)

        val centro7 = PointF(523f, 400f)
        canvas.drawCircle(centro7.x, centro7.y, buttonRadio, buttonPaint)
        canvas.drawText("07", centro7.x - movX, centro7.y + movY, buttonText)
    }

    private fun buttonEsp5(canvas: Canvas) {
        val centro8 = PointF(552f, 400f)
        canvas.drawCircle(centro8.x, centro8.y, buttonRadio, buttonPaint)
        canvas.drawText("08", centro8.x - movX, centro8.y + movY, buttonText)

        val centro9 = PointF(574f, 442f)
        canvas.drawCircle(centro9.x, centro9.y, buttonRadio, buttonPaint)
        canvas.drawText("09", centro9.x - movX, centro9.y + movY, buttonText)

        val centro11 = PointF(592f, 442f)
        canvas.drawCircle(centro11.x, centro11.y, buttonRadio, buttonPaint)
        canvas.drawText("011", centro11.x - movX, centro11.y + movY, buttonText)

        val centro10 = PointF(610f, 442f)
        canvas.drawCircle(centro10.x, centro10.y, buttonRadio, buttonPaint)
        canvas.drawText("10", centro10.x - movX, centro10.y + movY, buttonText)

        val centro12 = PointF(632f, 400f)
        canvas.drawCircle(centro12.x, centro12.y, buttonRadio, buttonPaint)
        canvas.drawText("12", centro12.x - movX, centro12.y + movY, buttonText)

        val centro13 = PointF(574f, 324f)
        canvas.drawCircle(centro13.x, centro13.y, buttonRadio, buttonPaint)
        canvas.drawText("13", centro13.x - movX, centro13.y + movY, buttonText)

        val centro14 = PointF(610f, 324f)
        canvas.drawCircle(centro14.x, centro14.y, buttonRadio, buttonPaint)
        canvas.drawText("14", centro14.x - movX, centro14.y + movY, buttonText)
    }

    private fun buttonEsp6(canvas: Canvas) {
        val centro15 = PointF(660f, 400f)
        canvas.drawCircle(centro15.x, centro15.y, buttonRadio, buttonPaint)
        canvas.drawText("15", centro15.x - movX, centro15.y + movY, buttonText)

        val centro16 = PointF(740f, 400f)
        canvas.drawCircle(centro16.x, centro16.y, buttonRadio, buttonPaint)
        canvas.drawText("16", centro16.x - movX, centro16.y + movY, buttonText)

        val centro17 = PointF(682f, 324f)
        canvas.drawCircle(centro17.x, centro17.y, buttonRadio, buttonPaint)
        canvas.drawText("17", centro17.x - movX, centro17.y + movY, buttonText)

        val centro18 = PointF(718f, 324f)
        canvas.drawCircle(centro18.x, centro18.y, buttonRadio, buttonPaint)
        canvas.drawText("18", centro18.x - movX, centro18.y + movY, buttonText)
    }

    private fun buttonEsp7(canvas: Canvas) {
        val centro19 = PointF(770f, 400f)
        canvas.drawCircle(centro19.x, centro19.y, buttonRadio, buttonPaint)
        canvas.drawText("19", centro19.x - movX, centro19.y + movY, buttonText)

        val centro20 = PointF(846f, 389f)
        canvas.drawCircle(centro20.x, centro20.y, buttonRadio, buttonPaint)
        canvas.drawText("20", centro20.x - movX, centro20.y + movY, buttonText)
        val centro21 = PointF(846f, 411f)
        canvas.drawCircle(centro21.x, centro21.y, buttonRadio, buttonPaint)
        canvas.drawText("21", centro21.x - movX, centro21.y + movY, buttonText)

        val centro22 = PointF(792f, 324f)
        canvas.drawCircle(centro22.x, centro22.y, buttonRadio, buttonPaint)
        canvas.drawText("22", centro22.x - movX, centro22.y + movY, buttonText)

        val centro23 = PointF(824f, 324f)
        canvas.drawCircle(centro23.x, centro23.y, buttonRadio, buttonPaint)
        canvas.drawText("23", centro23.x - movX, centro23.y + movY, buttonText)
    }

    private fun buttonCapillaIgnacio(canvas: Canvas) {
        val centro62 = PointF(85.5f, 415f)
        canvas.drawCircle(centro62.x, centro62.y, buttonRadio, buttonPaint)
        canvas.drawText("62", centro62.x - movX, centro62.y + movY, buttonText)

        val centro63 = PointF(163f, 490f)
        canvas.drawCircle(centro63.x, centro63.y, buttonRadio, buttonPaint)
        canvas.drawText("63", centro63.x - movX, centro63.y + movY, buttonText)

        val centro64 = PointF(230f, 415f)
        canvas.drawCircle(centro64.x, centro64.y, buttonRadio, buttonPaint)
        canvas.drawText("64", centro64.x - movX, centro64.y + movY, buttonText)

        val centro65 = PointF(163f, 341f)
        canvas.drawCircle(centro65.x, centro65.y, buttonRadio, buttonPaint)
        canvas.drawText("65", centro65.x - movX, centro65.y + movY, buttonText)

        val centro66 = PointF(200f, 341f)
        canvas.drawCircle(centro66.x, centro66.y, buttonRadio, buttonPaint)
        canvas.drawText("66", centro66.x - movX, centro66.y + movY, buttonText)
    }

    private fun buttonAnteSacristiaAnt(canvas: Canvas) {

        val centro49 = PointF(380f, 341f)
        canvas.drawCircle(centro49.x, centro49.y, buttonRadio, buttonPaint)
        canvas.drawText("49", centro49.x - movX, centro49.y + movY, buttonText)

        val centro50 = PointF(325f, 341f)
        canvas.drawCircle(centro50.x, centro50.y, buttonRadio, buttonPaint)
        canvas.drawText("50", centro50.x - movX, centro50.y + movY, buttonText)

        val centro51 = PointF(260f, 341f)
        canvas.drawCircle(centro51.x, centro51.y, buttonRadio, buttonPaint)
        canvas.drawText("51", centro51.x - movX, centro51.y + movY, buttonText)

        val centro52 = PointF(260f, 378f)
        canvas.drawCircle(centro52.x, centro52.y, buttonRadio, buttonPaint)
        canvas.drawText("52", centro52.x - movX, centro52.y + movY, buttonText)

        val centro53 = PointF(260f, 415f)
        canvas.drawCircle(centro53.x, centro53.y, buttonRadio, buttonPaint)
        canvas.drawText("53", centro53.x - movX, centro53.y + movY, buttonText)

        val centro54 = PointF(260f, 452f)
        canvas.drawCircle(centro54.x, centro54.y, buttonRadio, buttonPaint)
        canvas.drawText("54", centro54.x - movX, centro54.y + movY, buttonText)

        val centro55 = PointF(260f, 489f)
        canvas.drawCircle(centro55.x, centro55.y, buttonRadio, buttonPaint)
        canvas.drawText("55", centro55.x - movX, centro55.y + movY, buttonText)

        val centro56 = PointF(303f, 489f)
        canvas.drawCircle(centro56.x, centro56.y, buttonRadio, buttonPaint)
        canvas.drawText("56", centro56.x - movX, centro56.y + movY, buttonText)

        val centro57 = PointF(325f, 489f)
        canvas.drawCircle(centro57.x, centro57.y, buttonRadio, buttonPaint)
        canvas.drawText("57", centro57.x - movX, centro57.y + movY, buttonText)

        val centro58 = PointF(352.5f, 489f)
        canvas.drawCircle(centro58.x, centro58.y, buttonRadio, buttonPaint)
        canvas.drawText("58", centro58.x - movX, centro58.y + movY, buttonText)

        val centro59 = PointF(380f, 489f)
        canvas.drawCircle(centro59.x, centro59.y, buttonRadio, buttonPaint)
        canvas.drawText("59", centro59.x - movX, centro59.y + movY, buttonText)

        val centro60 = PointF(380f, 463f)
        canvas.drawCircle(centro60.x, centro60.y, buttonRadio, buttonPaint)
        canvas.drawText("60", centro60.x - movX, centro60.y + movY, buttonText)

        val centro61 = PointF(380f, 437f)
        canvas.drawCircle(centro61.x, centro61.y, buttonRadio, buttonPaint)
        canvas.drawText("61", centro61.x - movX, centro61.y + movY, buttonText)
    }*/
}