package com.ksw.simple_calender;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static java.lang.Math.min;

public class  SimpleCalenderView extends View {
    Paint m_paint;
    float m_width;
    float m_height;
    float m_weekHeight;

    static final int WEEK = 7;
    static final float EVENT_GAP = 30.0f;

    public SimpleCalenderView(Context context) {
        super(context);
    }

    public SimpleCalenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleCalenderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (m_paint == null) {
            m_paint = new Paint();
        }

        m_width = getWidth();
        m_height = getHeight();
        m_weekHeight = getHeight() / 20;

        drawCalenderBackLine(canvas);
        drawWeekText(canvas);
        drawCalenderAttr(canvas);
    }

    private void drawWeekText(Canvas canvas) {
        final float testTextSize = m_weekHeight / 2;
        m_paint.setTextAlign(Paint.Align.CENTER);
        m_paint.setTextSize(testTextSize);
        float dayWidth = m_width / 7;
        float tempX = dayWidth / 2;

        ArrayList<String> weekStr = new ArrayList<String> (Arrays.asList(
               "일", "월", "화", "수", "목", "금", "토"
        ));

        m_paint.setColor(Color.RED);
        canvas.drawText(weekStr.get(0), tempX,  m_weekHeight * 9 / 10, m_paint);

        m_paint.setColor(Color.BLACK);
        for (int i = 1; i < 7; ++i)
        {
            tempX+= dayWidth;
            canvas.drawText(weekStr.get(i), tempX, m_weekHeight * 9 / 10, m_paint);
        }
    }

    private void drawCalenderAttr(Canvas canvas) {
        Date today = new Date();

        int year = today.getYear();
        int month = today.getMonth();
        int date = today.getDate();

        Date myDate = new Date(year, month, 1);
        int day = myDate.getDay();
        myDate.setDate(32);
        int last =  32- myDate.getDate();
        int num = 1;

        for (int i = 0; i < 5 ; ++i) {
            for (int j = 0; j < 7; ++j) {
                if (i == 0 && j < day) {
                    continue;
                }

                if (num == date) {
                    drawMarkNumber(canvas, i, j);
                }

                if (num <= last) {
                    drawDayNumber(canvas, i, j, num);
                    num++;
                }
            }
        }

        drawSchedule(canvas, year, month, day, last);
    }

    private void drawSchedule(Canvas canvas, int year, int month, int day, int last) {
        DateEventManager mngr = DateEventManager.getInstance();

        // week
        int startWeek = 1;
        int endWeek = WEEK - day;
        for (int i= 0; i < 5; ++i) {
            DateAttr sWeek = new DateAttr(year + 1900, month + 1, startWeek, 0, 0);
            DateAttr eWeek = new DateAttr(year + 1900, month + 1, endWeek, 23, 59);

            ArrayList<DateEvent> ret = mngr.rangeCutEvent(sWeek.getDateTime(), eWeek.getDateTime());

            for (DateEvent e : ret) {
                int startEventWeek;
                int endEventWeek;

                if (i == 0) {
                    startEventWeek = day + e.getStart().getDay();
                    endEventWeek = day + e.getEnd().getDay();
                }
                else{
                    startEventWeek = e.getStart().getDay() - startWeek;
                    endEventWeek = e.getEnd().getDay() - startWeek;
                }

                drawScheduleLine(canvas, i, startEventWeek, endEventWeek);
            }

            startWeek = endWeek + 1;
            if (startWeek > last) break;
            endWeek += 7;
            endWeek = min(endWeek, last);
        }

        //
    }

    private void drawScheduleLine(Canvas canvas, int i, int startEventWeek, int endEventWeek) {
        float dayWidth = m_width / 7;
        float dayHeight = (m_height - m_weekHeight) / 5;

        float tempStartX = dayWidth * startEventWeek;
        float tempEndX = dayWidth * (endEventWeek + 1);
        float tempY = dayHeight * i + EVENT_GAP;

        m_paint.setColor(Color.BLUE);
        RectF rect = new RectF(tempStartX, tempY + m_weekHeight, tempEndX, tempY + m_weekHeight + EVENT_GAP);
        canvas.drawRoundRect(rect, 10, 10, m_paint);

    }

    private void drawMarkNumber(Canvas canvas, int y, int x) {
        m_paint.setColor(Color.GREEN);
        m_paint.setAlpha(100);
        float dayWidth = m_width / 7;
        float dayHeight = (m_height - m_weekHeight) / 5;
        final float circleSize = 24f;
        float tempX = dayWidth / 2 + dayWidth * x;
        float tempY = circleSize / 2 + m_weekHeight + dayHeight * y;

        canvas.drawCircle( tempX,  tempY, circleSize, m_paint);
    }

    private void drawDayNumber(Canvas canvas, int y, int x, int num) {
        if (x == 0) {
            m_paint.setColor(Color.RED);
        }
        else {
            m_paint.setColor(Color.BLACK);
        }

        final float testTextSize = 24f;
        m_paint.setTextAlign(Paint.Align.CENTER);
        m_paint.setTextSize(testTextSize);
        float dayWidth = m_width / 7;
        float dayHeight = (m_height - m_weekHeight) / 5;
        float tempX = dayWidth * x;
        float tempY = dayHeight * y;

        canvas.drawText(num +"", tempX + dayWidth / 2, tempY + m_weekHeight + testTextSize, m_paint);
    }

    private void drawCalenderBackLine(Canvas canvas) {
        m_paint.setColor(Color.GRAY);
        float dayWidth = m_width / 7;
        float dayHeight = (m_height - m_weekHeight) / 5;

        float tempX = dayWidth;
        float tempY = m_weekHeight;

        for (int i = 0; i < 6; ++i)
        {
            canvas.drawLine(tempX, 0, tempX, m_height,  m_paint);
            tempX += dayWidth;
        }

        for (int i = 0; i < 5; ++i)
        {
            canvas.drawLine(0, tempY, m_width, tempY,  m_paint);
            tempY += dayHeight;
        }
    }
}
