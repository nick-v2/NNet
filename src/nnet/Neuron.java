/*
 * Copyright (C) 2018 Nick Vocaire
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nnet;

/**
 *
 * @author Nick Vocaire
 */
final class Neuron {

    /**
     * Array of neuron weights.
     */
    private final double[] weights;

    /**
     * bias - bias of neuron value calculation .
     * value - value of neuron.
     * errorDelta - how far off value is based on cost function.
     */
    private double bias, value, errorDelta;

    /**
     * Constructor for making hidden neurons.
     *
     * @param w the weights of the neural connections to the next layer (or
     * output)
     * @param b the bias of the neuron
     */
    protected Neuron(final double b, final double[] w) {
        weights = w;
        bias = b;
    }

    /**
     * Sets the value of the neuron.
     *
     * @param v value to set
     */
    protected void setValue(final double v) {
        value = v;
    }

    /**
     * Method for setting the error of the neuron.
     *
     * @param e the error to set
     */
    protected void setError(final double e) {
        errorDelta = e;
    }

    /**
     * Method for setting the weight of a neuron.
     *
     * @param w weight to set
     * @param aj value to set it too
     */
    protected void setWeight(final int w, final double aj) {
        weights[w] = aj;
    }

    /**
     * Method for setting the bias of the neuron.
     *
     * @param b bias to set
     */
    protected void setBias(final double b) {
        bias = b;
    }

    /**
     * Method for getting value of neuron.
     *
     * @return value
     */
    protected double getValue() {
        return value;
    }

    /**
     * Method for getting weight of neuron.
     *
     * @param w the weight to get
     * @return the weight
     */
    protected double getWeight(final int w) {
        return weights[w];
    }

    /**
     * Method for getting Neuron bias.
     *
     * @return the bias
     */
    protected double getBias() {
        return bias;
    }

    /**
     * Method for getting the error of the neuron.
     *
     * @return the error
     */
    protected double getError() {
        return errorDelta;
    }

    /**
     * Method for getting how many weights the neuron has.
     *
     * @return weight array size
     */
    protected int getWeightSize() {
        return weights.length;
    }

    /**
     * Method for getting weights array of neuron in the form of a String array.
     *
     * @return array of neuron weights in string format
     */
    protected String[] getWeightsString() {
        String[] w = new String[weights.length];

        for (int i = 0; i < weights.length; i++) {
            w[i] = String.valueOf(weights[i]);
        }
        return w;
    }
}
