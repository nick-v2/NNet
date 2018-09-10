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

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;

import java.awt.event.InputEvent;

import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Network of layers.
 *
 * @author Nick Vocaire
 */
final class Network {

    /**
     * name - Name of network. keyPressed - Key pressed for network to learn.
     * type - Type of network.
     */
    private String name, keyPressed, type;

    /**
     * Takes control of keyboard.
     */
    private Robot robot;

    /**
     * inputWidth - Width of input image. inputHeight - Height of input box.
     * regionX - X position of selected desktop region. regionY - Y position of
     * selected desktop region. regionW - Width of selected desktop region.
     * regionH - Height of selected desktop region. keyInt - ?
     */
    private int inputWidth, inputHeight, regionX, regionY, regionW, regionH,
            focusX, focusY, focusW, focusH, keyInt;

    /**
     * timeTrained - time the network has been trained.
     */
    private long timeTrained;

    /**
     * Array of input neurons (read from region).
     */
    private INeuron[] inputNeurons;

    /**
     * Array of hidden layers (process in between input and output neurons).
     */
    private Layer[] hiddenLayers;

    /**
     * Array of output neurons (send keyboard commands).
     */
    private ONeuron[] outputNeurons;

    /**
     * Constructor for making a network with just a name (usually to load a
     * network).
     *
     * @param n name of network to load
     */
    protected Network(final String n) {
        name = n;

        //Creates robot to trap keyboard output
        try {
            robot = new Robot();
        } catch (AWTException ex) {
            System.err.println(ex);
        }
    }

    /**
     * Constructor for making supervised networks with general parameters.
     *
     * @param n the name of the network
     * @param w the width of the input neuron gray scale image
     * @param h the height of the input neuron gray scale image
     * @param ins the input neurons
     * @param lays the hidden layers of the network
     * @param outs the output neurons of the network
     */
    protected Network(final String n, final int w, final int h,
            final INeuron[] ins, final Layer[] lays, final ONeuron[] outs) {
        name = n;
        inputWidth = w;
        inputHeight = h;
        inputNeurons = ins;
        hiddenLayers = lays;
        outputNeurons = outs;
        timeTrained = 0;

        //Creates robot to trap keyboard ouput
        try {
            robot = new Robot();
        } catch (AWTException ex) {
            System.err.println(ex);
        }
    }

    /**
     * Method for setting the name of the network.
     *
     * @param n name of the network
     */
    protected void setNetName(final String n) {
        name = n;
    }

    /**
     * Method for setting the type of network.
     *
     * @param t type to set
     */
    protected void setNetType(final String t) {
        type = t;
    }

    /**
     * Method to set the training time of the network.
     * @param t the time to be set
     */
    protected void setNetTrainTime(final long t) {
        timeTrained = t;
    }

    /**
     * Method for setting the outputs of the network.
     *
     * @param o output neurons
     */
    protected void setOutputs(final ONeuron[] o) {
        outputNeurons = o;
    }

    /**
     * Sets the width of the area the network is looking at.
     *
     * @param w width of region
     */
    protected void setRegionWidth(final int w) {
        regionW = w;
    }

    /**
     * Sets the height of the area the network is looking at.
     *
     * @param h height of region
     */
    protected void setRegionHeight(final int h) {
        regionH = h;
    }

    /**
     * Sets the X coordinate of the area the network is looking at.
     *
     * @param x horizontal position of region
     */
    protected void setRegionX(final int x) {
        regionX = x;
    }

    /**
     * Sets the Y coordinate of the area the network is looking at.
     *
     * @param y vertical position of region
     */
    protected void setRegionY(final int y) {
        regionY = y;
    }

    /**
     * Sets the width of the focused area the network is looking at.
     *
     * @param w width of region
     */
    protected void setFocusWidth(final int w) {
        focusW = w;
    }

    /**
     * Sets the height of the focus area the network is looking at.
     *
     * @param h height of region
     */
    protected void setFocusHeight(final int h) {
        focusH = h;
    }

    /**
     * Sets the X coordinate of the focus area the network is looking at.
     *
     * @param x horizontal position of region
     */
    protected void setFocusX(final int x) {
        focusX = x;
    }

    /**
     * Sets the Y coordinate of the focus area the network is looking at.
     *
     * @param y vertical position of region
     */
    protected void setFocusY(final int y) {
        focusY = y;
    }

    /**
     * Returns the type of network.
     *
     * @return type of network
     */
    protected String getNetType() {
        return type;
    }

    /**
     * Method to get the training time of the network.
     * @return the time the network has trained
     */
    protected long getNetTrainTime() {
        return timeTrained;
    }

    /**
     * Returns the width of the area the network is looking at.
     *
     * @return region width
     */
    protected int getRegionWidth() {
        return regionW;
    }

    /**
     * Returns the height of the area the network is looking at.
     *
     * @return region height
     */
    protected int getRegionHeight() {
        return regionH;
    }

    /**
     * Returns the X coordinate of the area the network is looking at.
     *
     * @return horizontal position of region
     */
    protected int getRegionX() {
        return regionX;
    }

    /**
     * Returns the Y coordinate of the area the network is looking at.
     *
     * @return vertical position of region
     */
    protected int getRegionY() {
        return regionY;
    }
    
    /**
     * Returns the width of the focus area the network is looking at.
     *
     * @return region width
     */
    protected int getFocusWidth() {
        return focusW;
    }

    /**
     * Returns the height of the focus area the network is looking at.
     *
     * @return region height
     */
    protected int getFocusHeight() {
        return focusH;
    }

    /**
     * Returns the X coordinate of the focus area the network is looking at.
     *
     * @return horizontal position of region
     */
    protected int getFocusX() {
        return focusX;
    }

    /**
     * Returns the Y coordinate of the focus area the network is looking at.
     *
     * @return vertical position of region
     */
    protected int getFocusY() {
        return focusY;
    }

    /**
     * Returns the name of the network.
     *
     * @return the name of the network
     */
    protected String getNetName() {
        return name;
    }

    /**
     * Returns the size of the array of output neurons.
     *
     * @return size of array of output neurons
     */
    protected int getOutputSize() {
        return outputNeurons.length;
    }

    /**
     * Returns the output neuron requested.
     *
     * @param output the neuron to get
     * @return the output neuron
     */
    protected ONeuron getOutputNeuron(final int output) {
        return outputNeurons[output];
    }

    /**
     * Returns the size of the array of hidden layers.
     *
     * @return size of array of layers
     */
    protected int getLayerSize() {
        return hiddenLayers.length;
    }

    /**
     * Returns the layer requested.
     *
     * @param layer the layer to get
     * @return the layer
     */
    protected Layer getLayer(final int layer) {
        return hiddenLayers[layer];
    }

    /**
     * Returns the size of the array of input neurons.
     *
     * @return size of array of input neurons
     */
    protected int getInputSize() {
        return inputNeurons.length;
    }

    /**
     * Returns the input neuron requested.
     *
     * @param input the neuron to get
     * @return the input neuron
     */
    protected INeuron getInputNeuron(final int input) {
        return inputNeurons[input];
    }

    /**
     * Returns the width of the input neuron gray scale image.
     *
     * @return the gray scale image width
     */
    protected int getInputResWidth() {
        return inputWidth;
    }

    /**
     * Returns the height of the input neuron gray scale image.
     *
     * @return the gray scale image height
     */
    protected int getInputResHeight() {
        return inputHeight;
    }

    /**
     * Returns the value of the input neuron.
     *
     * @param n the neuron to get the value of
     * @return input neuron value
     */
    protected int getINeuronValue(final int n) {
        return (int) inputNeurons[n].getValue();
    }

    /**
     * Method for getting the current key pressed when the network is running.
     *
     * @return pressed key
     */
    protected String getPressedKey() {
        return keyPressed;
    }

    /**
     * Method for moving the mouse to the region.
     */
    protected void moveMouse() {
        //Move mouse to region
        robot.mouseMove(regionX + regionW / 2, regionY + regionH / 2);

        //Press mouse to activate region
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

        try {
            Thread.sleep(20);
        } catch (InterruptedException ex) {
            System.err.println(ex);
        }

        //release mouse after activating region
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    /**
     * Method for setting the key of the network randomly based on output
     * probabilities.
     */
    protected void setKeyDice() {
        int mostActivated = 0;
        double[] chance = new double[outputNeurons.length];

        //Fill chance array
        for (int o = 0; o < outputNeurons.length; o++) {
            //Chance is based on the probability of a output
            chance[o] = Math.random() * outputNeurons[o].getValue();
        }

        //Take a guess based on the probability of each output
        for (int o = 1; o < outputNeurons.length; o++) {
            if (chance[o] > chance[mostActivated]) {
                mostActivated = o;
            }
        }

        keyPressed = outputNeurons[mostActivated].getKeyName();
        keyInt = outputNeurons[mostActivated].getKeyInt();
    }

    /**
     * Method for setting the key of the network based on highest probabilities.
     */
    protected void setKeyProb() {
        int mostActivated = 0;

        //Pick the highest activated output
        for (int o = 1; o < outputNeurons.length; o++) {
            if (outputNeurons[o].getValue()
                    > outputNeurons[mostActivated].getValue()) {
                mostActivated = o;
            }
        }

        keyPressed = outputNeurons[mostActivated].getKeyName();
        keyInt = outputNeurons[mostActivated].getKeyInt();
    }

    /**
     * Method for when the network is running and picking the outputs to play.
     */
    protected void play() {
        int prevKey = keyInt;
        setKeyProb();

        if (prevKey != 0) {
            robot.keyRelease(prevKey);
        }
        robot.keyPress(keyInt);
    }

    /**
     * Method for seeing what the output neurons values are.
     */
    protected void printOutputs() {
        for (ONeuron outputNeuron : outputNeurons) {
            System.out.println(outputNeuron.getKeyName() + ": "
                    + outputNeuron.getValue());
        }
        System.out.println("\n");
    }

    /**
     * Converts String array of numbers to double array.
     *
     * @param s incoming string array
     * @return array of double
     */
    private double[] convertStringArray(final String[] s) {
        double[] doubles = new double[s.length];
        for (int i = 0; i < s.length; i++) {
            doubles[i] = Double.parseDouble(s[i]);
        }
        return doubles;
    }

    /**
     * Converts current frame of region into values for the input neurons.
     */
    protected void convertRegion() {
        int focusXInRegion = 0, focusYInRegion = 0, fColumn = 0, fRow = 0;
        BufferedImage area = robot.createScreenCapture(
                new Rectangle(regionX, regionY, regionW, regionH));
        if (focusH>0) {
            focusXInRegion = focusX - regionX;
            focusYInRegion = focusY - regionY;
        }

        //The dimensions of a single pixel for the gray scale image
        int miniH = regionH / inputHeight;
        int miniW = regionW / inputWidth;
        int nCount = 0;

        //for each row of input image pixels
        for (int r = 0; r < inputHeight; r++) {

            //for each column of input image pixels
            for (int c = 0; c < inputWidth; c++) {

                //Total value of all pixels in a mini-square after
                //being converted to gray
                int value = 0;

                //If the converted region is within the focus region
                if ((focusH > 0) && (c * miniW) > focusXInRegion - focusW / 2
                        && (r * miniH) > focusYInRegion - focusH / 2
                        && (c * miniW) < focusXInRegion + focusW + focusW / 2
                        && (r * miniH) < focusYInRegion + focusH + focusH / 2) {

                    //iterates through what will be a single pixel of input
                    //of the focus region
                    for (int rp = 0; rp < miniH / 2; rp++) {
                        for (int cp = 0; cp < miniW / 2; cp++) {

                            //Gets color at pixel
                            Color color = new Color(area.getRGB(focusXInRegion
                                    + cp + (fColumn * miniW / 2), focusYInRegion
                                            + rp + (fRow * miniH / 2)));

                            //Converts it to grayscale
                            int grayColor = (color.getRed() + color.getBlue()
                                    + color.getGreen()) / 3;

                            //Adds to value to be averaged later
                            value += grayColor;
                        }
                    }
                    //Sets the value of the input neuron to the average value
                    //of all converted pixels in the mini-box dictated by
                    //the resolution
                    inputNeurons[nCount].setValue(
                            value / ((miniW * miniH) / 4));
                    nCount++;
                    fColumn++;

                } else {

                    //iterates through what will be a single pixel of input
                    for (int rp = 0; rp < miniH; rp++) {
                        for (int cp = 0; cp < miniW; cp++) {

                            //Gets color at pixel
                            Color color = new Color(area.getRGB(cp
                                    + (c * miniW), rp + (r * miniH)));

                            //Converts it to grayscale
                            int grayColor = (color.getRed() + color.getBlue()
                                    + color.getGreen()) / 3;

                            //Adds to value to be averaged later
                            value += grayColor;
                        }
                    }
                    //Sets the value of the input neuron to the average value
                    //of all converted pixels in the mini-box dictated by
                    //the resolution
                    inputNeurons[nCount].setValue(
                            value / (miniW * miniH));
                    nCount++;
                }
            }

            //If the input pixel row you are calculating is in the focused
            //region
            if ((focusH > 0) && (r * miniH) > focusYInRegion - focusH / 2
                    && (r * miniH) < focusYInRegion + focusH + focusH / 2) {
                        fRow++;
            }
            fColumn = 0;
        }
    }

    /**
     * Propagates forward through the network to set values for output neurons.
     */
    protected void calculate() {
        //For each neuron in the first hidden layer
        for (int n = 0; n < hiddenLayers[0].getSize(); n++) {
            double value = 0;

            for (INeuron inputNeuron : inputNeurons) {

                //Multiply by the weight from each input neuron corresponding
                //to the neuron and add together
                value += inputNeuron.getValue() / 255
                        * inputNeuron.getWeight(n);
            }
            //Sets neuron value
            hiddenLayers[0].setNeuronValue(n, sigmoid(value
                    + hiddenLayers[0].getNeuronBias(n)));
        }

        for (int lay = 1; lay < hiddenLayers.length; lay++) {

            //For each neuron in the next layer
            for (int n = 0; n < hiddenLayers[lay].getSize(); n++) {
                double value = 0;

                //Multiply by the weight from each prev neuron corresponding
                //to the neuron and add
                for (int pn = 0; pn < hiddenLayers[lay - 1].getSize(); pn++) {
                    value += hiddenLayers[lay - 1].getNeuronValue(pn)
                            * hiddenLayers[lay - 1].getNeuronWeight(pn, n);
                }
                //Sets neuron value
                hiddenLayers[lay].setNeuronValue(n, sigmoid(value
                        + hiddenLayers[lay].getNeuronBias(n)));
            }
        }

        //For each output neuron
        for (int out = 0; out < outputNeurons.length; out++) {
            double value = 0;
            //Multiply by the weight from each prev neuron corresponding
            //to the neuron and add
            for (int n = 0; n < hiddenLayers[hiddenLayers.length - 1].getSize();
                    n++) {
                value += hiddenLayers[hiddenLayers.length - 1].getNeuronValue(n)
                        * hiddenLayers[hiddenLayers.length - 1].getNeuronWeight(
                                n, out);
            }
            //Sets output neuron value
            outputNeurons[out].setValue(sigmoid(value
                    + outputNeurons[out].getBias()));
        }
    }

    /**
     * Applies ReLU function to values to keep between 0 and 1.
     *
     * @param v value
     * @return squished value
     */
    private double sigmoid(final double v) {
        /**
         * TODO: Optimize this by making a table of values and picking one
         * closest.
         */

        return 1 / (1 + Math.exp(-v));
    }

    /**
     * Initializes the network with random values for all weights and biases.
     * Requires that the output Neurons are set.
     *
     * @param wid the width in pixels of the grey scale image
     * @param heig the height in pixels of the grey scale image
     * @param lay the # of hidden layers in the network
     * @param neu the # of neurons in the network
     */
    protected void loadRandom(final int wid, final int heig, final int lay,
            final int neu) {
        inputWidth = wid;
        inputHeight = heig;
        regionW = wid;
        regionH = heig;

        hiddenLayers = new Layer[lay]; //Make Layers
        //Loop through all Layers
        for (int i = 0; i < hiddenLayers.length; i++) {
            Neuron[] n = new Neuron[neu]; //Make a neuron array for each Layer

            for (int j = 0; j < n.length; j++) { //Loop through all Neurons

                //If its not the last layer make a weights array based on
                //how many neurons are in each layer
                if (i != hiddenLayers.length - 1) {
                    double[] weights = new double[neu];

                    //Loop through all weights
                    for (int w = 0; w < weights.length; w++) {

                        //Set each weight randomly
                        weights[w] = Math.random() * 2 - 1;
                    }

                    //Create each Neuron with its own weights to the next layer
                    //and random bias.
                    //Makes bias range larger because it is adjusted
                    //more drastically
                    n[j] = new Neuron(Math.random() * 2 - 1, weights);

                } else { //if last hidden layer
                    //Same stuff as above but a diffrent abount of connection
                    //to next layer(output layer)
                    double[] weights = new double[outputNeurons.length];

                    for (int w = 0; w < weights.length; w++) {
                        weights[w] = Math.random() * 2 - 1;
                    }
                    n[j] = new Neuron(Math.random() * 2 - 1, weights);
                }
            }
            hiddenLayers[i] = new Layer(n);
        }

        //Multiplied width and height to get rectangle area
        inputNeurons = new INeuron[wid * heig];

        //Loop through input neurons
        for (int i = 0; i < inputNeurons.length; i++) {

            //Create weights for each neuron to the next layer
            double[] weights = new double[hiddenLayers[0].getSize()];

            for (int w = 0; w < weights.length; w++) {
                weights[w] = Math.random() * 2 - 1;
            }

            //Set all input Neurons to have a base value of 0
            inputNeurons[i] = new INeuron(weights);
        }

        try {
            save(); //Save network
        } catch (IOException ex) {
        }
    }

    /**
     * Method for saving the network to a CSV file.
     *
     * @throws IOException File Not Found
     */
    protected void save() throws IOException {
        //Creates file if its not there or saves over it
        try (
                FileWriter fWriter = new FileWriter("networks/" + name
                        + ".csv"); CSVWriter writer = new CSVWriter(fWriter)) {

            String[] row = {String.valueOf(inputWidth),
                String.valueOf(inputHeight), type, String.valueOf(timeTrained),
                    String.valueOf(regionW), String.valueOf(regionH),
                        String.valueOf(regionX), String.valueOf(regionY),
                            String.valueOf(focusW), String.valueOf(focusH),
                                String.valueOf(focusX), String.valueOf(focusY)};
            writer.writeNext(row, false);

            row = new String[]{String.valueOf(inputNeurons.length)};
            writer.writeNext(row, false);

            for (INeuron inputNeuron : inputNeurons) {
                //Save all input neurons
                writer.writeNext(inputNeuron.getWeightsString(), false);
            }

            row = new String[]{String.valueOf(hiddenLayers.length)};
            writer.writeNext(row, false);

            for (Layer hiddenLayer : hiddenLayers) {
                //Save all Neurons
                //Temp Neuron array for each layer
                Neuron[] tempNeurons = hiddenLayer.getNeurons();
                row = new String[]{String.valueOf(tempNeurons.length)};
                writer.writeNext(row, false);
                for (int j = 0; j < hiddenLayer.getSize(); j++) {
                    row[0] = String.valueOf(tempNeurons[j].getBias());
                    writer.writeNext(row, false);
                    writer.writeNext(tempNeurons[j].getWeightsString(), false);
                }
            }

            row = new String[]{String.valueOf(outputNeurons.length)};
            writer.writeNext(row, false);

            for (ONeuron outputNeuron : outputNeurons) {
                //Save all Outputs
                row = new String[]{outputNeuron.getKeyName(),
                    String.valueOf(outputNeuron.getBias())};
                writer.writeNext(row, false);
            }

        }
    }

    /**
     * Method for loading network from a CSV file.
     * @throws IOException File Not Found
     */
    protected void load() throws IOException {
        try {
            FileReader fReader = new FileReader("networks/" + name + ".csv");
            CSVReader reader = new CSVReader(fReader);

            String[] row = reader.readNext();
            inputWidth = Integer.parseInt(row[0]);
            inputHeight = Integer.parseInt(row[1]);
            regionW = Integer.parseInt(row[4]);
            regionH = Integer.parseInt(row[5]);
            regionX = Integer.parseInt(row[6]);
            regionY = Integer.parseInt(row[7]);
            focusW = Integer.parseInt(row[8]);
            focusH = Integer.parseInt(row[9]);
            focusX = Integer.parseInt(row[10]);
            focusY = Integer.parseInt(row[11]);
            timeTrained = Integer.parseInt(row[3]);
            type = row[2];

            row = reader.readNext();
            inputNeurons = new INeuron[Integer.parseInt(row[0])];

            //load Input neurons
            for (int i = 0; i < inputNeurons.length; i++) {
                inputNeurons[i] = new INeuron(convertStringArray(
                        reader.readNext()));
            }

            row = reader.readNext();
            hiddenLayers = new Layer[Integer.parseInt(row[0])];

            for (int i = 0; i < hiddenLayers.length; i++) { //load layers
                row = reader.readNext();
                Neuron[] tempNeurons = new Neuron[Integer.parseInt(row[0])];

                for (int j = 0; j < tempNeurons.length; j++) {
                    row = reader.readNext();
                    tempNeurons[j] = new Neuron(Double.parseDouble(row[0]),
                            convertStringArray(reader.readNext()));
                }
                hiddenLayers[i] = new Layer(tempNeurons);
            }

            row = reader.readNext();
            outputNeurons = new ONeuron[Integer.parseInt(row[0])];
            //load output neurons
            for (int i = 0; i < outputNeurons.length; i++) {
                row = reader.readNext();

                try {
                    outputNeurons[i] = new ONeuron(
                            java.awt.event.KeyEvent.class.getField(row[0]),
                            Double.parseDouble(row[1]));
                } catch (NoSuchFieldException | SecurityException ex) {
                    System.err.println(ex);
                }
            }

        } catch (FileNotFoundException ex) {
            System.err.println("That is not a network\n" + ex);
        }
    }
}
