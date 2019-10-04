package com.sgc.graphslibrary.diagram;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.sgc.graphslibrary.R;
import com.sgc.graphslibrary.model.PieChartData;

import java.util.ArrayList;

import static com.sgc.graphslibrary.Maths.AngleMath.getAngleOfSectorCenter;
import static com.sgc.graphslibrary.Maths.AngleMath.getCompress;

public class PieChart extends View {

    //
    // Constructors
    //

    public PieChart(Context context) {
        super(context);
    }

    public PieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAttribute(context, attrs);
    }

    public PieChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadAttribute(context, attrs);
    }

    //
    // Constructors
    //

    /**
     *   load and set xml attribute
      */
    protected void loadAttribute(Context context, AttributeSet attrs) {
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.PieChart);

        String attributeStr = getAttribute(arr, R.styleable.PieChart_startAngle);

        if (attributeStr != null) {
            startAngle = Integer.parseInt(getAttribute(arr, R.styleable.PieChart_startAngle));
        }
        attributeStr = getAttribute(arr, R.styleable.PieChart_descriptionColor);
        if (attributeStr != null) {
            descriptionColor = Color.parseColor(attributeStr);
        }
        attributeStr = getAttribute(arr, R.styleable.PieChart_distanceDescription);
        if (attributeStr != null) {
            distanceDescriptionSectorFactor = Float.parseFloat(attributeStr);
        }

        arr.recycle();  // Do this when done.
    }

    /**
     * get xml attribute
     * @param arr R.styleable attribute
     * @param attr context.obtainStyledAttributes
     * @return string value attribute
     */
    protected String getAttribute(TypedArray arr, int attr) {
        CharSequence startAngleArr = arr.getString(attr);
        String valueStr = null;
        if (startAngleArr != null) {
            valueStr = startAngleArr.toString();
        }
        return valueStr;
    }

    /**
     *  coordinates of the beginning and end of the pie chart
     */
    protected RectF circle = new RectF();

    protected int width;
    protected int height;

    /**
     * sector description text color
     */
    protected int descriptionColor = Color.BLACK;

    /*
    * angle rotation pie chart
     */
    protected float startAngle = 0;

    /*
    * Remoteness factor of the description of sectors of the chart.
    * Recommend value 1.2 - 1.7
     */
    protected float distanceDescriptionSectorFactor = 1.5f;

    /**
     *data to build pie chart
     */
    protected ArrayList<PieChartData> data;

    /**
     * @return  sector description text color
     */
    public int getDescriptionColor() {
        return descriptionColor;
    }

    /**
     * @return angle rotation pie chart
     */
    public float getStartAngle() {
        return startAngle;
    }

    /**
     * @return Remoteness factor of the description of sectors of the chart.
     */
    public float getDistanceDescriptionSectorFactor() {
        return distanceDescriptionSectorFactor;
    }

    /**
     * @return data to build pie chart
     */
    public ArrayList<PieChartData> getData() {
        return data;
    }

    /**
     * @param descriptionColor sector description text color
     */
    public void setDescriptionColor(int descriptionColor) {
        this.descriptionColor = descriptionColor;
        super.invalidate();
    }

    /**
     * @param startAngle angle rotation pie chart
     */
    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
        super.invalidate();
    }

    /**
     * @param distanceDescriptionSectorFactor Remoteness factor of the description of sectors of the chart.Recommend value : 1.2 - 1.7
     */
    public void setDistanceDescriptionSectorFactor(float distanceDescriptionSectorFactor) {
        this.distanceDescriptionSectorFactor = distanceDescriptionSectorFactor;
        super.invalidate();
    }

    /**
     * @param data data to build pie chart
     */
    public void setData(ArrayList<PieChartData> data) {
        this.data = data;
        super.invalidate();
    }

    /**
     *  save width and height upon change width and height
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.width = w;
        this.height = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * draw pie chart
     */
    @Override
    protected void onDraw(Canvas canvas) {
        // check data available
        checkDataNull();
        //calculate pie chart coordinates
        createCircleRectF();
        //draw pie chart
        drawCircle(canvas);
        //draw description of each sector
        drawText(canvas);
    }

    /**
     * check data available
     */
    protected void checkDataNull() {
        if (data == null)
            throw new RuntimeException("Most likely you did not transfer data. Call a method setData() +  \n + and pass data to it to build a chart");
    }

    /**
     * calculate pie chart coordinates
     */
    protected void createCircleRectF() {
        int diameter = getDiameter();
        circle.set(0F, 0F, diameter, diameter);
    }

    /**
     * draw pie chart
     * @param canvas canvas on which need to draw pie chart
     */
    protected void drawCircle(Canvas canvas) {
        float compress = getCompress(data);
        float sweepAngle;

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        for (int i = 0; i < data.size(); i++) {
            paint.setColor(data.get(i).getColorDedicatedSpace());
            sweepAngle = data.get(i).getPercentageSpace() * compress;
            canvas.drawArc(circle, startAngle, sweepAngle, true, paint);
            startAngle += sweepAngle;
        }
    }

    /**
     * draw description of each sector
     * @param canvas canvas on which need to draw description of each sector
     */
    protected void drawText(Canvas canvas) {
        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(40);
        paint.setColor(descriptionColor);
        int diameter = getDiameter();

        for (int i = 0; i < data.size(); i++) {
            float angle = getAngleOfSectorCenter(i,startAngle,data);
            float cosY = (float) Math.cos(Math.toRadians(angle));
            float sinX = (float) Math.sin(Math.toRadians(angle));
            canvas.drawText(data.get(i).getText(),
                    getSectorDescriptionPosition(sinX, diameter),
                    getSectorDescriptionPosition(-cosY, diameter), paint);
        }
    }


    /**
     * @param value cos Y or sin X
     * @param diameter diameter pie chart
     * @return offset x or y
     */
    private float getSectorDescriptionPosition(float value, int diameter) {
        return ((diameter + value * (diameter / 2f) * distanceDescriptionSectorFactor) / 2);
    }

    /**
     * @return Diameter pie chart
     */
    protected int getDiameter() {
        if (width >= height)
            return height;
        else
            return width;
    }

}