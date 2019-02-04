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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/**
 * Trainer for network.
 *
 * @author Nick Vocaire
 */
class Trainer extends Thread {

    final int DEFAULT_UPDATES = 15;
    final int DEFAULT_PORT = 22333;
    final double DEFAULT_LEARNING_RATE = .5;
    final long DEFAULT_SAVE_TIME = 30000;

    Network loadedNetwork;
    ServerSocket serverSocket;
    Socket program;
    BufferedReader programInput;
    JFrame[] frames;
    JLabel keyPressedLabel, timeTrainedLabel;
    String keyPressed;
    int updates, mode; //0 = training, 1 = playing (DEFAULT SET TO 0)
    double learningRate, reward;
    long lastSave;
    boolean running, mouseMoved; //mouseMoved for moving the mouse to region when trainer starts

    /**
     * Basic constructor for making a network trainer.
     *
     * @param net the network to train
     */
    protected Trainer(final Network net) {
        loadedNetwork = net;
        keyPressed = "None";
        updates = DEFAULT_UPDATES;
        learningRate = DEFAULT_LEARNING_RATE;
        mode = 0;
        reward = 1;
        mouseMoved = false;
        running = true;

        if (loadedNetwork.getNetType().equals("reinforcement")) {
            try {
                serverSocket = new ServerSocket(DEFAULT_PORT);
            } catch (IOException ex) {
            }
        }

        // Get the logger for "org.jnativehook" and set the level to off.
        Logger logger = Logger.getLogger(
                GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
    }

    /**
     * Basic constructor for making a network trainer.
     *
     * @param net the network to train
     * @param f the jFrames you want to refresh
     * @param key the JLabel for key pressed you want to change and refresh
     * @param time the JLabel for time you want to update and refresh
     * @param u how many times per sec you want the network to train
     * @param l learning rate
     */
    protected Trainer(final Network net, final JFrame[] f, final JLabel key,
            final JLabel time, final int u, final double l) {
        loadedNetwork = net;
        frames = f;
        updates = u;
        keyPressedLabel = key;
        timeTrainedLabel = time;
        keyPressed = "NONE";
        mode = 0;
        reward = 1;
        mouseMoved = false;
        running = true;
        learningRate = l;

        if (loadedNetwork.getNetType().equals("reinforcement")) {
            try {
                serverSocket = new ServerSocket(DEFAULT_PORT);
            } catch (IOException ex) {
            }
        }

        // Get the logger for "org.jnativehook" and set the level to off.
        Logger logger = Logger.getLogger(
                GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
    }

    /**
     * Method for setting the mode of the trainer.
     *
     * @param m mode to set
     */
    protected void setMode(final int m) {
        mode = m;
    }

    /**
     * The code to be run when the thread is started, calls the update method
     * the amount of times specified per sec.
     */
    @Override
    public void run() {
        //Training reinforcement
        if (loadedNetwork.getNetType().equals("reinforcement") && mode == 0) {
            try {
                keyPressedLabel.setText("Connecting...");
                repaintFrames();
                program = serverSocket.accept();
                programInput = new BufferedReader(
                        new InputStreamReader(program.getInputStream()));
            } catch (IOException ex) {
                System.err.println(ex);
            }

            lastSave = System.currentTimeMillis();

            //Waits for input then trains network so it updates at the
            //same speed as the program
            while (running) {
                try {
                    reward = Double.parseDouble(programInput.readLine());
                } catch (IOException ex) {
                    System.err.println(ex);
                }

                update();

                if (System.currentTimeMillis()
                        >= lastSave + DEFAULT_SAVE_TIME) {
                    try {
                        loadedNetwork.save();
                        lastSave = System.currentTimeMillis();
                        loadedNetwork.setNetTrainTime(
                                loadedNetwork.getNetTrainTime()
                                + DEFAULT_SAVE_TIME / 1000);
                    } catch (IOException ex) {
                        System.err.println(ex);
                    }
                }

            }

        } else { //Supervised or playing mode
            try {
                //Hooks into native libraries of the OS in order to get key
                //presses when outside of the JFrame
                GlobalScreen.registerNativeHook();
                GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
            } catch (NativeHookException ex) {
            }

            int fps = 0;
            long lastLoopTime = System.nanoTime();
            long fpsTimer = 0;
            //Optimal time between each loop
            final long OPTIMAL_TIME = 1000000000 / updates;

            while (running) {
                long now = System.nanoTime();

                //How much time it took the loop to cycle, includes sleeping
                long loopTime = now - lastLoopTime;
                lastLoopTime = now; //Last time loop took place
                fpsTimer += loopTime;
                fps++;

                //If its been 1 sec, shows the frames in that secound
                if (fpsTimer >= 1000000000) {
                    if (frames != null) {
                        frames[0].setTitle("U: " + fps);
                    }

                    //Increase the network train time by 1 secound
                    if (mode == 0) {
                        loadedNetwork.setNetTrainTime(
                                loadedNetwork.getNetTrainTime() + 1);
                    }
                    fpsTimer = 0;
                    fps = 0;
                }

                //Update, pass in the delta loop to change calculations based
                //on lag if necessary
                update();

                now = System.nanoTime();
                //Similar to loop time but does not include the sleep,

                long updateTime = now - lastLoopTime;
                //only the update method call and the small code before

                long timeToSleep = (OPTIMAL_TIME - updateTime) / 1000000;
                //Takes how long the update took, subtracts it from the optimal
                //time and divides by 1000000 to get how long to sleep in
                //millisecounds (depending on lag, changes loop speed.
                //More lag, smaller sleep). Because of percision lost from
                //dividing a long, fps is usually over what is set

                //If update is taking super long(timeToSleep is negetive)
                //dont sleep
                if (timeToSleep > 0) {
                    try {
                        Thread.sleep(timeToSleep);
                    } catch (InterruptedException i) {
                        System.err.println("Loop could not sleep");
                    }
                }
            }
        }
    }

    /**
     * Method for stopping the trainer.
     */
    protected void stopTraining() {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException ex) {
        }

        running = false;
        mode = 0;
        mouseMoved = false;

        if (loadedNetwork.getNetType().equals("reinforcement")) {
            try {
                serverSocket.close();
                if (program != null) {
                    program.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    /**
     * Method for repainting frames.
     */
    protected void repaintFrames() {
        for (int i = 1; i < frames.length; i++) { //Repaint all frames
            frames[i].repaint();
        }
    }

    /**
     * Method for displaying the time trained
     */
    protected void displayTime() {
        long time = loadedNetwork.getNetTrainTime();
        long hour = time / 3600;
        time = time % 3600;
        long min = time / 60;
        time = time % 60;
        timeTrainedLabel.setText("H:" + hour + " M:" + min + " S:" + time);
    }

    /**
     * The code to be run 'updates' amount of times per seconds.
     */
    private void update() {
        if (mode == 1) { //Playing
            if (!mouseMoved) {
                loadedNetwork.moveMouse();
                mouseMoved = true;
            }
            //Converts region and stores in input neurons
            loadedNetwork.convertRegion();

            //Propgates input through network setting neuron values
            loadedNetwork.calculate();
            loadedNetwork.play();
            keyPressedLabel.setText(loadedNetwork.getPressedKey());
            loadedNetwork.printOutputs();

            displayTime();

        } else { //Training
            //Converts region and stores in input neurons
            loadedNetwork.convertRegion();

            //Propgates input through network setting neuron values
            loadedNetwork.calculate();

            if (loadedNetwork.getNetType().equals("reinforcement")) {

                //Move the mouse to the game if it hasent moved yet
                if (!mouseMoved) {
                    loadedNetwork.moveMouse();
                    mouseMoved = true;
                }
                loadedNetwork.play();
                keyPressed = loadedNetwork.getPressedKey();
                keyPressedLabel.setText(keyPressed + " : " + reward);
            } else {
                keyPressedLabel.setText(keyPressed);
            }

            displayTime();

            //Only train network if keys are being pressed
            if (!keyPressed.equals("NONE")) {
                backpropagate();
            }

            loadedNetwork.printOutputs();
        }

        repaintFrames();
    }

    /**
     * Method for back-propagating through the network to find quantitative
     * error for each neuron then adjust weights.
     */
    private void backpropagate() {

        //Loop through all output neruons
        for (int o = 0; o < loadedNetwork.getOutputSize(); o++) {
            ONeuron output = loadedNetwork.getOutputNeuron(o);

            if (output.getKeyName().equals(keyPressed)) {
                output.setError(reward * (1.0 - output.getValue())
                        * sigmoidDer(output.getValue()));
            } else {
                output.setError((0.0 - output.getValue())
                        * sigmoidDer(output.getValue()));
            }

            output.setBias(output.getBias() + learningRate * output.getError());
        }

        //Loop through all layers reversed
        for (int l = loadedNetwork.getLayerSize() - 1; l >= 0; l--) {
            Layer layer = loadedNetwork.getLayer(l);

            //Loop through all neurons in the layer
            for (int n = 0; n < layer.getSize(); n++) {
                double totalError = 0.0; //For adding error

                //If the layer is connected to output layer
                if (l == loadedNetwork.getLayerSize() - 1) {
                    for (int o = 0; o < loadedNetwork.getOutputSize(); o++) {
                        ONeuron output = loadedNetwork.getOutputNeuron(o);

                        totalError += output.getError()
                                * layer.getNeuronWeight(n, o)
                                * sigmoidDer(layer.getNeuronValue(n));
                    }
                } else { //If layer is not layer closest to output layer
                    Layer prevLayer = loadedNetwork.getLayer(l + 1);
                    for (int o = 0; o < prevLayer.getSize(); o++) {
                        totalError += prevLayer.getNeuronError(n)
                                * layer.getNeuronWeight(n, o)
                                * sigmoidDer(layer.getNeuronValue(n));
                    }
                }

                //Setting neuron error then neuron bias
                layer.setNeuronError(n, totalError);
                layer.setNeuronBias(n, layer.getNeuronBias(n)
                        + learningRate * layer.getNeuronError(n));

                //Loop through all weights of neuron
                for (int w = 0; w < layer.getNeuronWeightSize(n); w++) {

                    //If the layer is connected to output layer
                    if (l == loadedNetwork.getLayerSize() - 1) {
                        ONeuron output = loadedNetwork.getOutputNeuron(w);

                        double adjustment = layer.getNeuronWeight(n, w)
                                + learningRate * output.getError()
                                * layer.getNeuronValue(n);
                        layer.setNeuronWeight(n, w, adjustment);
                    } else { //If layer is not layer closest to output layer
                        Layer prevLayer = loadedNetwork.getLayer(l + 1);

                        double adjustment = layer.getNeuronWeight(n, w)
                                + learningRate * prevLayer.getNeuronError(w)
                                * layer.getNeuronValue(n);
                        layer.setNeuronWeight(n, w, adjustment);
                    }
                }
            }
        }

        Layer firstLayer = loadedNetwork.getLayer(0);

        //Loop through input neurons
        for (int i = 0; i < loadedNetwork.getInputSize(); i++) {
            INeuron input = loadedNetwork.getInputNeuron(i);

            for (int w = 0; w < input.getWeightSize(); w++) {
                double adjustment = input.getWeight(w) + learningRate
                        * firstLayer.getNeuronError(w) * input.getValue();
                input.setWeight(w, adjustment);
            }
        }
    }

    /**
     * Method for computing the derivative of the sigmoid based on the value of
     * a neuron.
     *
     * @param value the value of the neuron
     * @return the slope of the neuron value
     */
    private double sigmoidDer(final double value) {
        return value * (1.0 - value);
    }

    /**
     * Class for getting key presses globally.
     */
    private class GlobalKeyListener implements NativeKeyListener {

        @Override
        public void nativeKeyTyped(final NativeKeyEvent k) {
        }

        @Override
        public void nativeKeyPressed(final NativeKeyEvent k) {
            String globalKey = "VK_" + NativeKeyEvent.getKeyText(
                    k.getKeyCode()).toUpperCase();

            for (int o = 0; o < loadedNetwork.getOutputSize(); o++) {
                if (globalKey.equals(
                        loadedNetwork.getOutputNeuron(o).getKeyName())) {
                    keyPressed = globalKey;
                }
            }
        }

        @Override
        public void nativeKeyReleased(final NativeKeyEvent k) {
            keyPressed = "NONE";
        }

    }
}
