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
 * Singles layer of neurons.
 *
 * @author Nick Vocaire
 */
final class Layer {

    /**
     * Number of neurons in layer.
     */
    private final int size;

    /**
     * Array of layer's neurons.
     */
    private final Neuron[] neurons;

    /**
     * Constructor for making layers in a network based on specified neurons.
     *
     * @param n the neuron array of the layer
     */
    protected Layer(final Neuron[] n) {
        neurons = n;
        size = n.length;
    }

    /**
     * Method for setting a specific neuron's value.
     *
     * @param n the neuron whose value to set
     * @param v the value to set is as
     */
    protected void setNeuronValue(final int n, final double v) {
        neurons[n].setValue(v);
    }

    /**
     * Method for setting a specific neuron's error.
     *
     * @param n the neuron to set
     * @param e the value to set it as
     */
    public void setNeuronError(final int n, final double e) {
        neurons[n].setError(e);
    }

    /**
     * Method for setting the weight of a specific neuron.
     *
     * @param n neuron to set weight of
     * @param w weight to set
     * @param aj value to set it as
     */
    protected void setNeuronWeight(final int n, final int w, final double aj) {
        neurons[n].setWeight(w, aj);
    }

    /**
     * Method for setting a specific neuron's bias.
     *
     * @param n neuron to set bias of
     * @param b bias to set
     */
    protected void setNeuronBias(final int n, final double b) {
        neurons[n].setBias(b);
    }

    /**
     * Method for getting value of neuron.
     *
     * @param n neuron to get value of
     * @return value of neuron
     */
    protected double getNeuronValue(final int n) {
        return neurons[n].getValue();
    }

    /**
     * Method for getting a neuron's weight.
     *
     * @param n neuron to check
     * @param w weight to get
     * @return weight
     */
    protected double getNeuronWeight(final int n, final int w) {
        return neurons[n].getWeight(w);
    }

    /**
     * Method for getting a neuron's bias.
     *
     * @param n neuron to check
     * @return bias to get
     */
    protected double getNeuronBias(final int n) {
        return neurons[n].getBias();
    }

    /**
     * Method for getting a neuron's error.
     *
     * @param n neuron to check
     * @return error to get
     */
    protected double getNeuronError(final int n) {
        return neurons[n].getError();
    }

    /**
     * Method to return the size of the Layer.
     *
     * @return size of layer
     */
    protected int getSize() {
        return size;
    }

    /**
     * Method to return the size of the weights array for a neuron.
     *
     * @param n neuron to check
     * @return size of weights array of neuron
     */
    protected int getNeuronWeightSize(final int n) {
        return neurons[n].getWeightSize();
    }

    /**
     * Method to return array of neurons in layer.
     *
     * @return array of neurons
     */
    protected Neuron[] getNeurons() {
        return neurons;
    }
}
