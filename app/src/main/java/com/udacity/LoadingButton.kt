package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var valueAnimator = ValueAnimator()
    private val paint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        strokeWidth = resources.getDimension(R.dimen.stroke_width)
    }
    private val rect: RectF = RectF(0f, 0f, 0f, 0f)
    private var currentAngle = 0
    private val clipRectRight = resources.getDimension(R.dimen.clipRectRight)
    private val clipRectBottom = resources.getDimension(R.dimen.clipRectBottom)
    private val clipRectTop = resources.getDimension(R.dimen.clipRectTop)
    private val clipRectLeft = resources.getDimension(R.dimen.clipRectLeft)

    private val _buttonState = MutableLiveData<ButtonState>(ButtonState.Completed)
    val buttonState: LiveData<ButtonState> = _buttonState

    var buttonDelegateState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                startAnimation()
            }
            ButtonState.Loading -> {}
            ButtonState.Completed -> {}
        }
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            setBackgroundColor(getColor(R.styleable.LoadingButton_backgroundColor, 0))
        }
        isClickable = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = ContextCompat.getColor(context, R.color.colorAccent)
        canvas.drawArc(rect, 100f, currentAngle.toFloat(), false, paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(w.toFloat() -100f, 0f, w.toFloat() - 200f, h.toFloat())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun performClick(): Boolean {
        _buttonState.value = ButtonState.Clicked
        return super.performClick()
    }

    private fun startAnimation() {
        valueAnimator.cancel()
        valueAnimator = ValueAnimator.ofInt(0, -360).apply {
            duration = 2000
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                currentAngle = valueAnimator.animatedValue as Int
                invalidate()
            }
        }
        valueAnimator.start()
    }
}