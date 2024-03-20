package com.example.curved

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.util.AttributeSet
import android.view.DragEvent
import android.view.View.OnDragListener
import kotlin.properties.Delegates


class CurvedTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?= null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    // Define the properties with property delegates
    private var curveDirection: Int = 0

    private var curveRadius: Float = 0f

    private var angle: Float by Delegates.observable(0f) { _, _, _ ->
        // When the angle changes, update the path
        updatePath()
    }
    private var radius: Float by Delegates.observable(0f) { _, _, _ ->
        // When the radius changes, update the path
        updatePath()
    }
    var curvedText: String by Delegates.observable("") { _, _, _ ->
        // When the text changes, update the path
        updatePath()
    }
    private var curvedTextColor: Int by Delegates.observable(Color.BLACK) { _, _, new ->
        // When the text color changes, update the paint
        paint.color = new
    }
    private var curvedTextSize: Float by Delegates.observable(0f) { _, _, new ->
        // When the text size changes, update the paint and the path
        paint.textSize = new
        updatePath()
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var path: Path? = null // Define path as nullable

    // Common initialization block
    init {


        // Get the attribute values
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CurvedTextView)
        val angle = typedArray.getFloat(R.styleable.CurvedTextView_angle, 0f)
        val radius = typedArray.getDimension(R.styleable.CurvedTextView_radius, 0f)
        val curvedText = typedArray.getString(R.styleable.CurvedTextView_text) ?: ""
        val curvedTextColor = typedArray.getColor(R.styleable.CurvedTextView_textColor, Color.BLACK)
        val curvedTextSize = typedArray.getDimension(R.styleable.CurvedTextView_textSize, 0f)
        // Recycle the typed array
        typedArray.recycle()
        // Assign the attribute values to the class properties
        this.angle = angle
        this.radius = radius
        this.curvedText = curvedText
        this.curvedTextColor = curvedTextColor
        this.curvedTextSize = curvedTextSize
        // Create the meandering path
        updatePath()
        // Add the drag and drop feature
        addDragAndDropFeature()
    }

    // Define a method that updates the meandering path
    private fun updatePath() {
        // Create a new path object
        val newPath = Path()
        // Calculate the center coordinates of the path
        val centerX = this.width / 2f
        val centerY = this.height / 2f
        when (curveDirection) {
            0 -> { // top_to_bottom
                // Add a downward curve starting from the top of the road
                newPath.moveTo(centerX, 0f) // Set the starting point of the path
                newPath.quadTo(
                    centerX,
                    centerY,
                    centerX,
                    this.height.toFloat()
                ) // Draw a winding path
            }

            1 -> { // bottom_to_top
                // Add a curve upwards starting from the bottom of the road
                newPath.moveTo(centerX, this.height.toFloat()) // Set the starting point of the path
                newPath.quadTo(centerX, centerY, centerX, 0f) // Draw a winding path
            }
        }

        // Add a circle or curve using the curveRadius property
        if (curveRadius > 0f) {
            // For example, add a circle
            newPath.addCircle(centerX, centerY, curveRadius, Path.Direction.CW)
        }

        if (curvedText.isNotEmpty()) {
            paint.color = curvedTextColor
            paint.textSize = curvedTextSize
        }
        // Assign the new path object to the class property
        // Add a circle to the path with the given radius and angle

        newPath.addCircle(centerX, centerY, radius, Path.Direction.CW)
        newPath.offset(0f, -radius)
        newPath.setLastPoint(centerX, centerY - radius)
        newPath.addArc(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius,
            -90f,
            angle
        )
        // Assign the new path to the class property
        path = newPath
    }

    // Define a method that adds the drag and drop feature
    private fun addDragAndDropFeature() {
        // Create a drag shadow builder object
        val dragShadow = DragShadowBuilder(this)
        // Create a clip data object that represents the dragged data
        val clipData = ClipData.newPlainText("curved text", text)
        // Start the drag and drop operation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.startDragAndDrop(clipData, dragShadow, null, 0)
        }
        // Create a drag listener object that listens to the drag events
        val dragListener = OnDragListener { view, event ->
            when (event.action) {
                // When the drag starts
                DragEvent.ACTION_DRAG_STARTED -> {
                    // Check if the dragged data is compatible
                    if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        // The drag is acceptable
                        true
                    } else {
                        // The drag is unacceptable
                        false
                    }
                }
                // When the drag location changes
                DragEvent.ACTION_DRAG_LOCATION -> {
                    // Update the drag location
                    true
                }
                // When the drag is dropped
                DragEvent.ACTION_DROP -> {
                    // Get the dragged data
                    val data = event.clipData.getItemAt(0).text.toString()
                    // Set the dragged data to the target view
                    if (view is CurvedTextView) {
                        view.text = data
                    }
                    // The drag is successful
                    true
                }
                // When the drag ends
                DragEvent.ACTION_DRAG_ENDED -> {
                    // The drag is over
                    true
                }

                else -> {
                    // Unknown action
                    false
                }
            }
        }
        // Set the drag listener to the class property
        setOnDragListener(dragListener)
    }

    override fun onDraw(canvas: Canvas) {
        // Use the paint object to draw the text on the curved path
        path?.let { canvas.drawTextOnPath(curvedText, this.path!!, 0f, 0f, paint) }
    }
}