package com.windhike.annotation.model;

import java.util.List;

public class IntersectPoint {
    public double x;
    public double y;

    public IntersectPoint(double xVal, double yVal) {
        this.x = xVal;
        this.y = yVal;
    }

    public IntersectPoint(IntersectPoint oldPoint) {
        this.x = oldPoint.x;
        this.y = oldPoint.y;
    }

    void move(double xDelta, double yDelta) {
        this.x += xDelta;
        this.y += yDelta;
    }

    double distance(IntersectPoint aPoint) {
        return Math.sqrt(((this.x - aPoint.x) * (this.x - aPoint.x)) + ((this.y - aPoint.y) * (this.y - aPoint.y)));
    }

    public double distanceSqrt(IntersectPoint p) {
        return Math.hypot(this.x - p.x, this.y - p.y);
    }

    public String toString() {
        return new StringBuilder(String.valueOf(Double.toString(this.x))).append(",").append(this.y).toString();
    }

    public IntersectPoint getClosestPoint(List<IntersectPoint> pts) {
        double minDistSoFar = Double.MAX_VALUE;
        IntersectPoint rval = null;
        for (IntersectPoint p : pts) {
            if (p.x != this.x || p.y != this.y) {
                double pDist = distance(p);
                if (pDist < minDistSoFar) {
                    minDistSoFar = pDist;
                    rval = p;
                }
            }
        }
        return rval;
    }

    @Deprecated
    public IntersectPoint getIntersectionPoint(List<IntersectPoint> listPoints) {
        IntersectPoint minPoint = null;
        for (int i = 0; i < listPoints.size(); i++) {
            IntersectPoint point = listPoints.get(i);
            minPoint = listPoints.get((i + 1) % listPoints.size());
            double minDist = Math.hypot(minPoint.x - point.x, minPoint.y - point.y);
            for (int j = 0; j < listPoints.size(); j++) {
                if (i != j) {
                    IntersectPoint testPt = listPoints.get(j);
                    double dist = Math.hypot(point.x - testPt.x, point.y - testPt.y);
                    if (dist < minDist) {
                        minDist = dist;
                        minPoint = testPt;
                    }
                }
            }
        }
        System.out.println("minPoint:" + minPoint.x + "\t" + minPoint.y);
        return minPoint;
    }
}

