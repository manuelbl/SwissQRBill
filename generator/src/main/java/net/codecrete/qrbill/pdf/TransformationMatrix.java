//
// Swiss QR Bill Generator
// Copyright (c) 2022 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.pdf;

/**
 * 3-by-3 matrix for affine geometric transformation.
 */
public class TransformationMatrix {
    // matrix elements in row-major order
    private final double[] elements;

    /**
     * Creates a new identity matrix instance.
     */
    public TransformationMatrix() {
        // identity matrix
        elements = new double[]{1, 0, 0, 1, 0, 0};
    }

    /**
     * Matrix elements in row-major order.
     * <p>
     * As the elements of the third column are always [ 0, 0, 1 ],
     * only the first two columns are returned, i.e. 6 elements.
     * </p>
     */
    public double[] getElements() {
        return elements;
    }

    /**
     * Applies a translation to the matrix (prepend).
     *
     * @param dx horizontal translation
     * @param dy vertical translation
     */
    public void translate(double dx, double dy) {
        // check for simple case without scaling or rotation
        if (elements[0] == 1 && elements[1] == 0 && elements[2] == 0 && elements[3] == 1) {
            elements[4] += dx;
            elements[5] += dy;
        } else {
            elements[4] += elements[0] * dx + elements[2] * dy;
            elements[5] += elements[1] * dx + elements[3] * dy;
        }
    }

    /**
     * Applies a scaling relative to the origin (prepend).
     *
     * @param sx horizontal scaling
     * @param sy vertical scaling
     */
    public void scale(double sx, double sy) {
        if (sx == 1 && sy == 1)
            return;

        elements[0] *= sx;
        elements[1] *= sx;
        elements[2] *= sy;
        elements[3] *= sy;
    }

    /**
     * Applies a rotation about the origin (prepend).
     *
     * @param angle rotation angle (in radians)
     */
    public void rotate(double angle) {
        if (angle == 0)
            return;

        double c = Math.cos(angle);
        double s = Math.sin(angle);
        double e0 = elements[0];
        double e1 = elements[1];
        double e2 = elements[2];
        double e3 = elements[3];
        elements[0] = e0 * c + e2 * s;
        elements[1] = e1 * c + e3 * s;
        elements[2] = -e0 * s + e2 * c;
        elements[3] = -e1 * s + e3 * c;
    }
}
