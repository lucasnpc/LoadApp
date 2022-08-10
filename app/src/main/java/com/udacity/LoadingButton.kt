package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatButton
import androidx.core.animation.doOnEnd
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
    private var currentAngle = 0
    private val clipRectRight = resources.getDimension(R.dimen.clipRectRight)
    private val clipRectBottom = resources.getDimension(R.dimen.clipRectBottom)
    private val clipRectTop = resources.getDimension(R.dimen.clipRectTop)
    private val clipRectLeft = resources.getDimension(R.dimen.clipRectLeft)
    private val rect: RectF = RectF(clipRectLeft, clipRectTop, clipRectRight, clipRectBottom)

    private val circleX = resources.getDimension(R.dimen.rectInset)
    private val circleY = resources.getDimension(R.dimen.rowInset)

    private val _buttonState = MutableLiveData<ButtonState>(ButtonState.Completed)
    val buttonState: LiveData<ButtonState> = _buttonState

    var buttonDelegateState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Clicked -> {
                startAnimation()
                this.text = context.getString(R.string.button_loading)
            }
            ButtonState.Loading -> {}
            ButtonState.Completed -> {
                this.text = context.getString(R.string.button_name)
                currentAngle = 0
                invalidate()
            }
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
        canvas.translate(circleX, circleY)
        canvas.clipRect(clipRectLeft, clipRectTop, clipRectRight, clipRectBottom)
        paint.color = ContextCompat.getColor(context, R.color.colorAccent)
        canvas.drawArc(
            rect,
            0f,
            currentAngle.toFloat(),
            true,
            paint
        )
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
        valueAnimator = ValueAnimator.ofInt(0, 360).apply {
            duration = ANIMATION_DURATION
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                currentAngle = valueAnimator.animatedValue as Int
                invalidate()
            }
        }
        valueAnimator.start()
        valueAnimator.doOnEnd {
            _buttonState.value = ButtonState.Completed
        }
    }

    private companion object {
        const val ANIMATION_DURATION = 2500L
    }
}