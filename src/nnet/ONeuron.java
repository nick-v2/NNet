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

import java.lang.reflect.Field;

/**
 * Output neuron.
 *
 * @author Nick Vocaire
 */
final class ONeuron {

    /**
     * Output key.
     */
    private Field key;

    /**
     * value - value of output neuron.
     * bias - bias of output neuron.
     * errorDelta - how far off value is based on cost function.
     */
    private double value, bias, errorDelta;

    /**
     * Constructor for making output neurons with keyboard key 'k'.
     *
     * @param k the key for the output
     * @param b bias of output neuron
     */
    protected ONeuron(final Field k, final double b) {
        key = k;
        bias = b;
    }

    /**
     * Sets the key for this output neuron.
     *
     * @param k the key to set for the output
     */
    protected void setKey(final Field k) {
        key = k;
    }

    /**
     * Method for setting the value of the neuron.
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
     * Method for setting the bias of the neuron.
     *
     * @param b bias to set
     */
    protected void setBias(final double b) {
        bias = b;
    }

    /**
     * Method for getting the neurons bias.
     *
     * @return bias
     */
    protected double getBias() {
        return bias;
    }

    /**
     * Method for getting the value of the neuron.
     *
     * @return value
     */
    protected double getValue() {
        return value;
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
     * Gets the key for this output neuron.
     *
     * @return key int value
     */
    protected int getKeyInt() {
        try {
            return key.getInt(null);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
        }
        return 0;
    }

    /**
     * Gets the key name for this output neuron.
     *
     * @return key name
     */
    protected String getKeyName() {
        return key.getName();
    }

}
