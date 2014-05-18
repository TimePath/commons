package com.timepath.math;

import java.util.logging.Logger;

/**
 * @author TimePath
 */
public class Vector3 {

    public static final  Vector3 ZERO = new Vector3(0, 0, 0);
    private static final Logger  LOG  = Logger.getLogger(Vector3.class.getName());
    private float x, y, z;

    private Vector3(Number x, Number y, Number z) {
        this.x = x.floatValue();
        this.y = y.floatValue();
        this.z = z.floatValue();
    }

    private Vector3() {
    }

    private Vector3(Vector3 v) {
        new Vector3().set(v);
    }

    void set(Vector3 v) {
        x = v.x;
        y = v.y;
        z = v.z;
    }

    public Vector3 add(Vector3 v) {
        return add(v.x, v.y, v.z);
    }

    Vector3 add(float x, float y, float z) {
        return new Vector3(this.x + x, this.y + y, this.z + z);
    }

    float getX() {
        return x;
    }

    public void setX(float f) {
        x = f;
    }

    float getY() {
        return y;
    }

    public void setY(float f) {
        y = f;
    }

    float getZ() {
        return z;
    }

    public void setZ(float f) {
        z = f;
    }

    public Vector3 addLocal(Vector3 v) {
        return addLocal(v.x, v.y, v.z);
    }

    Vector3 addLocal(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public float distance(Vector3 o) {
        return (float) Math.sqrt(distanceSquared(o));
    }

    float distanceSquared(Vector3 o) {
        return new Vector3(o.x - x, o.y - y, o.z - z).magnitudeSquared();
    }

    float magnitudeSquared() {
        return ( x * x ) + ( y * y ) + ( z * z );
    }

    public Vector3 normalize() {
        return new Vector3(x / magnitude(), y / magnitude(), z / magnitude());
    }

    float magnitude() {
        return (float) Math.sqrt(magnitudeSquared());
    }

    public Vector3 mult(float f) {
        return new Vector3(x * f, y * f, z * f);
    }

    public Vector3 transform(int i, int i0, int i1) {
        return new Vector3(x * i, y * i0, z * i1);
    }
}
