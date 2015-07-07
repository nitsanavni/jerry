package me.everything.jerry.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by markkoltnuk on 6/17/15.
 */
public class InitialsFinder {

    private static final String REGEX = "\\b\\w"; // word boundary followed by word letter
    private static final Pattern PATTERN = Pattern.compile(REGEX);
    private static final int MAX_LENGHT = 3;

    public static String initials(String name) {
        StringBuilder sb = new StringBuilder();
        Matcher matcher = PATTERN.matcher(name);
        while (matcher.find() && sb.length() < MAX_LENGHT) {
            sb.append(matcher.group());
        }
        return sb.toString().toUpperCase();
    }

    public static Drawable getInitialsDrawable(String name, int bgColor, int width, int height, float textSize) {
        String initials = InitialsFinder.initials(name);
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        Paint paint = new Paint();

        // background color
        paint.setColor(bgColor);
        paint.setStyle(Paint.Style.FILL);
        c.drawPaint(paint);

        // draw the text itself
        paint.setColor(Color.WHITE);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        int xPos = width / 2;
        int baselineToCenterDistance = (int) ((paint.descent() + paint.ascent()) / 2);
        int yPos = (height / 2) - baselineToCenterDistance;
        c.drawText(initials, xPos, yPos, paint);
        return new BitmapDrawable(b);
    }

    public static Drawable getInitialsDrawable(String name, int color, int w, int h, int s, CircleImageView image) {
        BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
        Bitmap b = drawable.getBitmap();
        Canvas c = new Canvas(b);
        Paint paint = new Paint();

        // background color
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        c.drawPaint(paint);

        // draw the text itself
        paint.setColor(Color.WHITE);
        paint.setTextSize(s);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        int xPos = w / 2;
        int baselineToCenterDistance = (int) ((paint.descent() + paint.ascent()) / 2);
        int yPos = (h / 2) - baselineToCenterDistance;
        String initials = InitialsFinder.initials(name);
        c.drawText(initials, xPos, yPos, paint);
        return new BitmapDrawable(b);
    }
}
