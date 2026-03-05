/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;

public class CaptchaImageGenerator {
    private static final double TWO_PI = Math.PI * 2;
    private static SecureRandom random = new SecureRandom();
    private Color backgroundColor = Color.BLACK;
    private int numOfBackgroudPolygons = 8;
    private int maxBackgroundPolygonRadius = 8;
    private int minBackgroundPolygonColor = 30;
    private int maxBackgroundPolygonColor = 90;
    private int maxBackgroudPolygonTiltingAngle = 10;
    private int minPolygonSides = 3;
    private int maxPolygonSides = 6;
    private String fontName = "Arial";
    private int fontStyle = 1;
    private int minFontColor = 220;
    private int maxFontColor = 255;
    private int maxFontTiltingAngle = 18;
    private double minFontSizeRatio = 0.6;

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getNumOfBackgroudPolygons() {
        return this.numOfBackgroudPolygons;
    }

    public void setNumOfBackgroudPolygons(int numOfBackgroudPolygons) {
        this.numOfBackgroudPolygons = numOfBackgroudPolygons;
    }

    public int getMaxBackgroundPolygonRadius() {
        return this.maxBackgroundPolygonRadius;
    }

    public void setMaxBackgroundPolygonRadius(int maxBackgroundPolygonRadius) {
        this.maxBackgroundPolygonRadius = maxBackgroundPolygonRadius;
    }

    public int getMinBackgroundPolygonColor() {
        return this.minBackgroundPolygonColor;
    }

    public void setMinBackgroundPolygonColor(int minBackgroundPolygonColor) {
        this.minBackgroundPolygonColor = minBackgroundPolygonColor;
    }

    public int getMaxBackgroundPolygonColor() {
        return this.maxBackgroundPolygonColor;
    }

    public void setMaxBackgroundPolygonColor(int maxBackgroundPolygonColor) {
        this.maxBackgroundPolygonColor = maxBackgroundPolygonColor;
    }

    public int getMaxBackgroudPolygonTiltingAngle() {
        return this.maxBackgroudPolygonTiltingAngle;
    }

    public void setMaxBackgroudPolygonTiltingAngle(int maxBackgroudPolygonTiltingAngle) {
        this.maxBackgroudPolygonTiltingAngle = maxBackgroudPolygonTiltingAngle;
    }

    public int getMinPolygonSides() {
        return this.minPolygonSides;
    }

    public void setMinPolygonSides(int minPolygonSides) {
        this.minPolygonSides = minPolygonSides;
    }

    public int getMaxPolygonSides() {
        return this.maxPolygonSides;
    }

    public void setMaxPolygonSides(int maxPolygonSides) {
        this.maxPolygonSides = maxPolygonSides;
    }

    public String getFontName() {
        return this.fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public int getFontStyle() {
        return this.fontStyle;
    }

    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
    }

    public int getMinFontColor() {
        return this.minFontColor;
    }

    public void setMinFontColor(int minFontColor) {
        this.minFontColor = minFontColor;
    }

    public int getMaxFontColor() {
        return this.maxFontColor;
    }

    public void setMaxFontColor(int maxFontColor) {
        this.maxFontColor = maxFontColor;
    }

    public int getMaxFontTiltingAngle() {
        return this.maxFontTiltingAngle;
    }

    public void setMaxFontTiltingAngle(int maxFontTiltingAngle) {
        this.maxFontTiltingAngle = maxFontTiltingAngle;
    }

    public double getMinFontSizeRatio() {
        return this.minFontSizeRatio;
    }

    public void setMinFontSizeRatio(double minFontSizeRatio) {
        this.minFontSizeRatio = minFontSizeRatio;
    }

    public BufferedImage newImage(int width, int height, String word) {
        BufferedImage image = new BufferedImage(width, height, 1);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(this.backgroundColor);
        g.fillRect(0, 0, width, height);
        for (int i = 0; i < this.numOfBackgroudPolygons; ++i) {
            double angle = Math.toRadians(random.nextInt(this.maxBackgroudPolygonTiltingAngle * 2 + 1) - this.maxBackgroudPolygonTiltingAngle);
            g.rotate(angle);
            g.setColor(this.generateRandomColor(this.minBackgroundPolygonColor, this.maxBackgroundPolygonColor));
            g.fillPolygon(this.generateRandomPolygon(width, height, this.maxBackgroundPolygonRadius));
            g.rotate(-angle);
        }
        int horizontalBlock = width / word.length();
        int maxFontSize = Math.min(horizontalBlock, height);
        int minFontSize = (int)((double)maxFontSize * this.minFontSizeRatio);
        for (int i = 0; i < word.length(); ++i) {
            double angle = Math.toRadians(random.nextInt(this.maxFontTiltingAngle * 2 + 1) - this.maxFontTiltingAngle);
            int fontSize = random.nextInt(maxFontSize - minFontSize + 1) + minFontSize;
            int x = random.nextInt(horizontalBlock - fontSize + 1);
            int y = random.nextInt(height - fontSize + 1) + fontSize;
            g.setFont(new Font(this.fontName, this.fontStyle, fontSize));
            g.setColor(this.generateRandomColor(this.minFontColor, this.maxFontColor));
            g.rotate(angle);
            g.drawString(word.substring(i, i + 1), x, y);
            g.rotate(-angle);
            g.translate(horizontalBlock, 0);
        }
        g.dispose();
        return image;
    }

    private Color generateRandomColor(int minColor, int maxColor) {
        int range = maxColor - minColor + 1;
        return new Color(random.nextInt(range) + minColor, random.nextInt(range) + minColor, random.nextInt(range) + minColor);
    }

    private Polygon generateRandomPolygon(int boundX, int boundY, int maxRadius) {
        int sides = random.nextInt(this.maxPolygonSides - this.minPolygonSides + 1) + this.minPolygonSides;
        int centreX = random.nextInt(boundX);
        int centreY = random.nextInt(boundY);
        return this.generatePolygon(centreX, centreY, maxRadius, sides);
    }

    private Polygon generatePolygon(int centreX, int centreY, int radius, int sides) {
        Polygon polygon = new Polygon();
        double theta = Math.PI * 2 / (double)sides;
        for (int i = 0; i < sides; ++i) {
            polygon.addPoint((int)((double)centreX + (double)radius * Math.cos((double)i * theta)), (int)((double)centreY + (double)radius * Math.sin((double)i * theta)));
        }
        return polygon;
    }
}

