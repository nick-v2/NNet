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
 * Input neuron.
 *
 * @author Nick Vocaire
 */
final class INeuron {

    /**
     * Array of connection weights.
     */
    private final double[] weights;

    /**
     * Value of neuron.
     */
    private double value;

    /**
     * Constructor for making Input Neurons based on a low res grey scale image.
     *
     * @param w the weights of the connections to the next neurons
     */
    protected INeuron(final double[] w) {
        weights = w;
    }

    /**
     * Sets the value of the input neuron.
     *
     * @param v the value
     */
    public void setValue(final int v) {
        value = v;
    }

    /**
     * Sets the weight to value a value.
     *
     * @param w weight to set
     * @param aj value to set it to
     */
    protected void setWeight(final int w, final double aj) {
        weights[w] = aj;
    }

    /**
     * Method for getting the double value of the neuron.
     *
     * @return value of neuron
     */
    protected double getValue() {
        return value;
    }

    /**
     * Method for getting a specific weight in the weight array.
     *
     * @param w the weight you want from the array
     * @return the value of the weight
     */
    protected double getWeight(final int w) {
        return weights[w];
    }

    /**
     * Method for getting the size of the weights array.
     *
     * @return length of array
     */
    protected int getWeightSize() {
        return weights.length;
    }

    /**
     * Method for getting weights array of neuron in the form of a String array
     * for saving.
     *
     * @return array of weight in string format
     */
    protected String[] getWeightsString() {
        String[] w = new String[weights.length];

        for (int i = 0; i < weights.length; i++) {
            w[i] = String.valueOf(weights[i]);
        }
        return w;
    }
}
