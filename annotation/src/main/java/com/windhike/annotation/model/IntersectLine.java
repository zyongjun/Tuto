package com.windhike.annotation.model;

public class IntersectLine {
    IntersectPoint end;
    IntersectPoint start;

    public IntersectLine(IntersectPoint start, IntersectPoint end) {
        this.start = new IntersectPoint(start);
        this.end = new IntersectPoint(end);
    }

    public IntersectLine(double xStart, double yStart, double xEnd, double yEnd) {
        this.start = new IntersectPoint(xStart, yStart);
        this.end = new IntersectPoint(xEnd, yEnd);
    }

    double length() {
        return this.start.distance(this.end);
    }

    public String toString() {
        return "(" + this.start + "):(" + this.end + ")";
    }

    public IntersectPoint intersects(IntersectLine line1) {
        IntersectPoint localPoint = new IntersectPoint(0.0d, 0.0d);
        double num = ((this.end.y - this.start.y) * (this.start.x - line1.start.x)) - ((this.end.x - this.start.x) * (this.start.y - line1.start.y));
        double denom = ((this.end.y - this.start.y) * (line1.end.x - line1.start.x)) - ((this.end.x - this.start.x) * (line1.end.y - line1.start.y));
        localPoint.x = line1.start.x + (((line1.end.x - line1.start.x) * num) / denom);
        localPoint.y = line1.start.y + (((line1.end.y - line1.start.y) * num) / denom);
        return localPoint;
    }

    public IntersectPoint getIntersect(IntersectLine line1, IntersectLine line2) {
        IntersectPoint X = new IntersectPoint(0.0d, 0.0d);
        double a1 = (line1.end.y - line1.start.y) / (line1.end.x - line1.start.x);
        double b1 = line1.end.y - (line1.end.x * a1);
        double a2 = (line2.end.y - line2.start.y) / (line2.end.x - line2.start.x);
        double x = ((line2.end.y - (line2.end.x * a2)) - b1) / (a1 - a2);
        X = new IntersectPoint(x, (a1 * x) + b1);
        System.out.println("Point X:" + X.toString());
        return X;
    }
}
