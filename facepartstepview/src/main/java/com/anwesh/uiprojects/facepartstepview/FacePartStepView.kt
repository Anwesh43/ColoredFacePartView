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

fun Canvas.drawFaceShape(scale : Float, size : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    val sf0 : Float = sf.divideScale(0, parts)
    paint.style = Paint.Style.STROKE
    drawArc(RectF(-size, -size, size, size), 0f, 360f * sf0, false, paint)
}

fun Canvas.drawFaceEye(i : Int, scale : Float, size : Float, paint : Paint) {
    val sf2 : Float = scale.sinify().divideScale(2, parts)
    val sf2i : Float = sf2.divideScale(i, 2)
    paint.style = Paint.Style.FILL
    val x : Float = (1 - 2 * i) * (size / eyeOffsetFactor) * sf2
    val y : Float = -(size / eyeOffsetFactor)
    val r : Float = size / eyeSizeFactor
    drawCircle(x, y, r * sf2i, paint)
}

fun Canvas.drawFaceLip(scale : Float, size : Float, paint : Paint) {
    val sf1 : Float = scale.sinify().divideScale(1, parts)
    val y : Float = size / lipOffsetFactor
    val lipSize : Float = size / lipSizeFactor
    drawLine(-(lipSize) * sf1, y, lipSize * sf1, y, paint)
}

fun Canvas.drawFacePartStep(scale : Float, size : Float, paint : Paint) {
    drawFaceShape(scale, size, paint)
    for (j in 0..1) {
        drawFaceEye(j, scale, size, paint)
    }
    drawFaceLip(scale, size, paint)
}

fun Canvas.drawFPSNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val size : Float = Math.min(w, h) / sizeFactor
    paint.color = Color.parseColor(colors[i])
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    translate(w / 2, h / 2)
    drawFacePartStep(scale, size, paint)
    restore()
}

class FacePartStepView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var prevScale : Float = 0f, var dir : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class FPSNode(var i : Int, val state : State = State()) {

        private var next : FPSNode? = null
        private var prev : FPSNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = FPSNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawFPSNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : FPSNode {
            var curr : FPSNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class FacePartStep(var i : Int) {

        private var curr : FPSNode = FPSNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }
}