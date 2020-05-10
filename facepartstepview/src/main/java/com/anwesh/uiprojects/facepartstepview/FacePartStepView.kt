package com.anwesh.uiprojects.facepartstepview

/**
 * Created by anweshmishra on 11/05/20.
 */

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Color
import android.graphics.Canvas

val nodes : Int = 5
val balls : Int = 2
val sizeFactor : Float = 2.5f
val strokeFactor : Float = 90f
val delay : Long = 20
val colors : Array<String> = arrayOf("#4CAF50", "#F44336", "#009688", "#FF9800", "#673AB7")
val backColor : Int = Color.parseColor("#BDBDBD")
val eyeSizeFactor : Float = 9f
val eyeOffsetFactor : Float = 8f
val lipSizeFactor = 6f
val lipOffsetFactor = 4f
val parts : Int = 3
val scGap : Float = 0.02f / parts

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()
