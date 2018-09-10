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

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.imageio.ImageIO;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * A program for creating neural networks.
 *
 * @author Nick Vocaire
 * @since 6/5/18
 * TODO: Make it so user can select activation function Fix how
 * the brain layers are displayed to the screen(Sqrt) Create a recording
 * function and test it only learning when a key is pressed
 */
public final class NeuralNetGUI {

    private static final int MENUWIDTH = 400;
    private static final int MENUHEIGHT = 400;
    private static final int TRAINWIDTH = 300;
    private static final int TRAINHEIGHT = 250;
    //Scale of the sceenshot when selecting a region
    private static final int REGIONSCALE = 2;

    private static String[] networkNames, keyNames;
    private static int selectedHiddenLayer = 0; //For displayed brain

    private static JFrame menu, brainMap, gray, region, trainer;
    private static JTextField resWidth, resHeight, layers, outputs, neurons,
            name, updates, learnRate;
    private static JLabel resWidthText, resHeightText, layersText, outputsText,
            outputNumText, keyText, neuronText, nameText, networkName,
            nameError, trainerText, updatesText, keyPressed, learnRateText,
            hiddenLayerDisplayed, timeTrained;
    private static JButton newNet, loadNet, brain, image, createNetwork,
            pickOutputs, setNumOutputs, setAllOutputs, pickRegion, setRegion,
            hiddenLayerRight, hiddenLayerLeft, regionInc, regionMin, regionLeft,
            regionRight, regionUp, regionDown, openTrainer, startTrainer,
            stopTrainer, play, stopPlaying;
    private static ButtonGroup networkTypes, regionSelection;
    private static JRadioButton supervised, reinforcement, mRegion, fRegion;
    private static JComboBox networkList, outputsList, keyList;
    private static JPanel mPanel, iPanel, rPanel, tPanel, bPanel;
    private static File[] networkFiles;
    private static BufferedImage nNetPic, screenshot;
    private static Field[] keyEventFields;
    private static Network loadedNetwork;
    private static Trainer networkTrainer;
    private static Robot robot;

    private static ONeuron[] outputNeurons; //For making new networks

    /**
     * Loads menu.
     *
     * @param args the command line arguments
     * @throws java.io.IOException Image Not Found
     */
    public static void main(final String[] args) throws IOException {
        NeuralNetGUI neuralNetGUI = new NeuralNetGUI();
    }

    /**
     * Initializes GUI.
     *
     * @throws java.io.IOException Image Not Found
     */
    private NeuralNetGUI() throws IOException {
        searchNetworks();
        loadKeyNames();
        loadButtons();
        loadPanels();
        loadFrames();

        try {
            robot = new Robot(); //Load the robot
        } catch (AWTException ex) {
            System.err.println(ex);
        }

        menu.setVisible(true);
    }

    /**
     * Loads key names.
     */
    private static void loadKeyNames() {
        Field[] fields = java.awt.event.KeyEvent.class.getDeclaredFields();
        keyEventFields = new Field[fields.length - 9];
        keyNames = new String[keyEventFields.length - 1];

        int staticCount = 0;
        for (int i = 0; i < keyEventFields.length; i++) {
            //If a static variable
            if (Modifier.isStatic(fields[i].getModifiers())) {
                keyEventFields[staticCount] = fields[i];
                keyNames[staticCount] = fields[i].getName();
                staticCount++;
            }
        }

    }

    /**
     * Looks for all networks to load in networks folder.
     */
    private static void searchNetworks() {
        File networkFolder = new File("networks");
        networkFiles = networkFolder.listFiles();
        networkNames = new String[networkFiles.length];
        //Puts the file names in a String array
        for (int i = 0; i < networkFiles.length; i++) {
            if (networkFiles[i].isFile()) {
                networkNames[i] = networkFiles[i].getName().substring(0,
                        networkFiles[i].getName().lastIndexOf("."));
            }
        }
    }

    /**
     * When creating new network, checks to see if name has been taken.
     *
     * @param cName the name to check
     * @return false if the check found a duplicate, true if there is no
     * duplicate
     */
    private static boolean checkName(final String cName) {
        for (String n : networkNames) {
            if (cName.equals(n)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Loads all frames, applies settings, and adds panels.
     *
     * @throws IOException Image Not Found
     */
    private static void loadFrames() throws IOException {
        menu = new JFrame("Neural Network Maker");
        brainMap = new JFrame("Neural Network Map");
        gray = new JFrame("Neural Network Gray Scale");
        region = new JFrame("Neural Network Region");
        trainer = new JFrame("U: 0");

        menu.setResizable(false);
        brainMap.setResizable(false);
        gray.setResizable(false);
        region.setResizable(false);
        trainer.setResizable(false);

        menu.setBackground(new Color(30, 170, 255));

        menu.add(mPanel);
        gray.add(iPanel);
        region.add(rPanel);
        trainer.add(tPanel);
        brainMap.add(bPanel);

        region.addMouseListener(new Mouse());

        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        menu.setSize(MENUWIDTH, MENUHEIGHT + 29); //29 pixel border on JFrame
        region.setSize(2 + (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth()
                / REGIONSCALE), 40 + (int) (Toolkit.getDefaultToolkit()
                        .getScreenSize().getHeight()
                / REGIONSCALE));
        trainer.setSize(TRAINWIDTH, TRAINHEIGHT);

        nNetPic = ImageIO.read(new File("img/NNet.png"));
    }

    /**
     * Sets the gray scale image window size based on resolution.
     */
    private static void loadGrayScale() {
        int width = loadedNetwork.getInputResWidth();
        int height = loadedNetwork.getInputResHeight();

        if (width <= 32) {
            width = width * 10;
        }
        if (height <= 32) {
            height = height * 10;
        }

        if (width <= 64 && width > 32) {
            width = width * 6;
        }
        if (height <= 64 && height > 32) {
            height = height * 6;
        }

        if (width <= 192 && width > 64) {
            width = width * 2;
        }
        if (height <= 192 && height > 64) {
            height = height * 2;
        }

        gray.setSize(height + 29, width + 6);
        brainMap.setSize(height + 29, width + 6);
    }

    /**
     * Loads all buttons.
     */
    private static void loadButtons() {

        loadJButtons();

        loadJComboBoxes();

        networkTypes = new ButtonGroup(); //ButtonGroups (RadioButtons)
        regionSelection = new ButtonGroup();

        loadJRadioButtons();

        loadJTextFields();

        loadJLabels();

        setBounds();
    }

    /**
     * Load JTextFields.
     */
    private static void loadJTextFields() {
        resWidth = new JTextField("32");
        resHeight = new JTextField("32");
        layers = new JTextField("2");
        outputs = new JTextField();
        neurons = new JTextField("16");
        name = new JTextField("Bestfriend");
        updates = new JTextField("10");
        learnRate = new JTextField(".1");
    }

    /**
     * Load JRadioButtons.
     */
    private static void loadJRadioButtons() {
        supervised = new JRadioButton("Supervised");
        supervised.setSelected(true);
        supervised.setActionCommand("supervised");
        networkTypes.add(supervised);

        reinforcement = new JRadioButton("Reinforcement");
        reinforcement.setActionCommand("reinforcement");
        networkTypes.add(reinforcement);

        mRegion = new JRadioButton("Main Region");
        mRegion.setSelected(true);
        mRegion.setActionCommand("mainR");
        mRegion.addActionListener(new ButtonHandler());
        regionSelection.add(mRegion);

        fRegion = new JRadioButton("Focused Region");
        fRegion.setActionCommand("focusR");
        fRegion.addActionListener(new ButtonHandler());
        regionSelection.add(fRegion);
    }

    /**
     * Load JComboBoxes.
     */
    private static void loadJComboBoxes() {
        networkList = new JComboBox(networkNames);

        keyList = new JComboBox(keyNames);
        keyList.setActionCommand("keyChanged");
        keyList.addActionListener(new ButtonHandler());

        outputsList = new JComboBox();
        outputsList.setActionCommand("outputNumChanged");
        outputsList.addActionListener(new ButtonHandler());
    }

    /**
     * Set bounds.
     */
    private static void setBounds() {
        newNet.setBounds(40, 225, 120, 40);
        loadNet.setBounds(225, 240, 120, 30);
        brain.setBounds(150, 205, 100, 20);
        networkName.setBounds(155, 150, 120, 20);
        image.setBounds(130, 230, 140, 20);
        networkList.setBounds(210, 215, 150, 20);
        supervised.setBounds(43, 160, 100, 20);
        reinforcement.setBounds(40, 185, 110, 20);
        name.setBounds(40, 230, 100, 20);
        nameText.setBounds(40, 210, 100, 20);
        resWidth.setBounds(225, 165, 110, 20);
        layers.setBounds(225, 245, 105, 20);
        resWidthText.setBounds(225, 145, 120, 20);
        layersText.setBounds(225, 225, 100, 20);
        createNetwork.setBounds(150, 360, 100, 30);
        pickOutputs.setBounds(135, 325, 130, 30);
        setNumOutputs.setBounds(210, 170, 130, 30);
        setAllOutputs.setBounds(140, 325, 120, 30);
        outputs.setBounds(60, 180, 100, 20);
        outputsText.setBounds(60, 160, 100, 20);
        outputsList.setBounds(60, 230, 120, 20);
        outputNumText.setBounds(60, 210, 100, 20);
        keyText.setBounds(220, 210, 100, 20);
        keyList.setBounds(220, 230, 120, 20);
        neurons.setBounds(225, 285, 100, 20);
        neuronText.setBounds(225, 265, 110, 20);
        resHeight.setBounds(225, 205, 120, 20);
        resHeightText.setBounds(225, 185, 100, 20);
        nameError.setBounds(30, 250, 140, 20);
        pickRegion.setBounds(140, 180, 120, 20);
        trainerText.setBounds(77, 10, 150, 40);
        openTrainer.setBounds(150, 255, 100, 20);
        updatesText.setBounds(100, 50, 100, 20);
        updates.setBounds(100, 75, 100, 20);
        startTrainer.setBounds(25, 170, 120, 30);
        stopTrainer.setBounds(90, 140, 120, 30);
        play.setBounds(155, 170, 120, 30);
        stopPlaying.setBounds(85, 140, 120, 30);
        keyPressed.setBounds(50, 45, 250, 30);
        timeTrained.setBounds(50, 80, 250, 30);
        learnRate.setBounds(100, 125, 100, 20);
        learnRateText.setBounds(100, 100, 100, 20);
    }

    /**
     * Load JLabels.
     */
    private static void loadJLabels() {
        resWidthText = new JLabel("Width Resolution");
        resHeightText = new JLabel("Height Resolution");
        layersText = new JLabel("Hidden Layers");
        outputsText = new JLabel("Outputs");
        outputNumText = new JLabel("Outputs Number");
        keyText = new JLabel("Key");
        neuronText = new JLabel("Neurons per Layer");
        nameText = new JLabel("Name");
        nameError = new JLabel("ERROR: NAME TAKEN");
        updatesText = new JLabel("Updates/Secound");
        learnRateText = new JLabel("Learning Rate");

        timeTrained = new JLabel();
        timeTrained.setFont(new Font("Dialog", Font.BOLD, 18));

        hiddenLayerDisplayed = new JLabel("Layer 0");
        hiddenLayerDisplayed.setFont(new Font("Dialog", Font.BOLD, 18));

        trainerText = new JLabel("Train/Play");
        trainerText.setFont(new Font("Dialog", Font.BOLD, 30));

        networkName = new JLabel();
        networkName.setFont(new Font("Dialog", Font.BOLD, 18));

        keyPressed = new JLabel();
        keyPressed.setFont(new Font("Dialog", Font.BOLD, 18));
    }

    /**
     * Load JButtons.
     */
    private static void loadJButtons() {
        newNet = new JButton("New Network");
        newNet.addActionListener(new ButtonHandler());

        loadNet = new JButton("Load Network");
        loadNet.addActionListener(new ButtonHandler());

        brain = new JButton("Show Brain");
        brain.addActionListener(new ButtonHandler());

        image = new JButton("Show Gray Image");
        image.addActionListener(new ButtonHandler());

        createNetwork = new JButton("Create");
        createNetwork.addActionListener(new ButtonHandler());

        pickOutputs = new JButton("Pick Outputs");
        pickOutputs.addActionListener(new ButtonHandler());

        setNumOutputs = new JButton("Set Num Outputs");
        setNumOutputs.addActionListener(new ButtonHandler());

        setAllOutputs = new JButton("Set All Outputs");
        setAllOutputs.addActionListener(new ButtonHandler());

        pickRegion = new JButton("Select Region");
        pickRegion.addActionListener(new ButtonHandler());

        openTrainer = new JButton("Train/Play");
        openTrainer.addActionListener(new ButtonHandler());

        startTrainer = new JButton("Train");
        startTrainer.addActionListener(new ButtonHandler());

        stopTrainer = new JButton("Stop Training");
        stopTrainer.addActionListener(new ButtonHandler());

        play = new JButton("Play");
        play.addActionListener(new ButtonHandler());

        stopPlaying = new JButton("Stop Playing");
        stopPlaying.addActionListener(new ButtonHandler());

        regionInc = new JButton("+");
        regionMin = new JButton("-");
        regionUp = new JButton("^^");
        regionDown = new JButton("VV");
        regionLeft = new JButton("<<");
        regionRight = new JButton(">>");

        regionInc.addActionListener(new ButtonHandler());
        regionMin.addActionListener(new ButtonHandler());
        regionUp.addActionListener(new ButtonHandler());
        regionDown.addActionListener(new ButtonHandler());
        regionLeft.addActionListener(new ButtonHandler());
        regionRight.addActionListener(new ButtonHandler());

        hiddenLayerLeft = new JButton("<");
        hiddenLayerRight = new JButton(">");

        hiddenLayerLeft.addActionListener(new ButtonHandler());
        hiddenLayerRight.addActionListener(new ButtonHandler());

        setRegion = new JButton("Set Region");
        setRegion.addActionListener(new ButtonHandler());
        setRegion.setFont(new Font("Dialog", Font.BOLD, 15));
    }

    /**
     * Loads all panels and adds buttons.
     */
    private static void loadPanels() {
        mPanel = new MenuPanel();
        mPanel.setLayout(null);
        mPanel.add(newNet);
        mPanel.add(loadNet);
        mPanel.add(networkList);

        iPanel = new ImagePanel();

        bPanel = new BrainPanel();
        bPanel.add(hiddenLayerLeft);
        bPanel.add(hiddenLayerDisplayed);
        bPanel.add(hiddenLayerRight);

        rPanel = new RegionPanel();
        rPanel.add(mRegion);
        rPanel.add(fRegion);
        rPanel.add(setRegion);
        rPanel.add(regionInc);
        rPanel.add(regionMin);
        rPanel.add(regionUp);
        rPanel.add(regionDown);
        rPanel.add(regionLeft);
        rPanel.add(regionRight);

        tPanel = new JPanel();
        tPanel.setBackground(new Color(30, 170, 255));
        tPanel.setLayout(null);
        tPanel.add(trainerText);
        tPanel.add(updates);
        tPanel.add(updatesText);
        tPanel.add(learnRate);
        tPanel.add(learnRateText);
        tPanel.add(startTrainer);
        tPanel.add(play);
    }

    /**
     * Changes menu to the output selector.
     */
    private static void outputsMenu() {
        pickOutputs.setFont(new Font("Dialog", Font.BOLD, 12));
        mPanel.removeAll();
        mPanel.add(outputs);
        mPanel.add(outputsText);
        mPanel.add(setAllOutputs);
        mPanel.add(setNumOutputs);
        mPanel.add(outputsList);
        mPanel.add(keyList);
        mPanel.add(keyText);
        mPanel.add(outputNumText);
        menu.revalidate();
        menu.repaint();
    }

    /**
     * Changes menu to the new network setting selector.
     */
    private static void newNetworkMenu() {
        mPanel.removeAll();
        mPanel.add(supervised);
        mPanel.add(reinforcement);
        mPanel.add(resWidth);
        mPanel.add(layers);
        mPanel.add(resWidthText);
        mPanel.add(layersText);
        mPanel.add(createNetwork);
        mPanel.add(pickOutputs);
        mPanel.add(neurons);
        mPanel.add(neuronText);
        mPanel.add(name);
        mPanel.add(nameText);
        mPanel.add(resHeight);
        mPanel.add(resHeightText);
        menu.revalidate();
        menu.repaint();
    }

    /**
     * Changes menu to the loaded network menu.
     */
    private static void loadedNetworkMenu() {
        networkName.setText(loadedNetwork.getNetName());
        loadGrayScale();

        mPanel.removeAll();
        mPanel.add(networkName);
        mPanel.add(pickRegion);
        menu.revalidate();
        menu.repaint();
    }

    /**
     * Changes the trainer to the default menu.
     */
    private static void trainerMenu() {
        tPanel.removeAll();
        tPanel.add(startTrainer);
        tPanel.add(updates);
        tPanel.add(updatesText);
        tPanel.add(trainerText);
        tPanel.add(learnRate);
        tPanel.add(learnRateText);
        tPanel.add(play);
        trainer.revalidate();
        trainer.repaint();
    }

    /**
     * Converts a buffered image into a gray scale version.
     *
     * @param im the image to convert
     * @return the converted image
     */
    private static BufferedImage makeGray(final BufferedImage im) {
        BufferedImage result = new BufferedImage(im.getWidth(), im.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = result.getGraphics();
        g.drawImage(im, 0, 0, null); //draws image onto gray buffered image
        g.dispose();
        return result;
    }

    /**
     * An action listener to deal with button presses.
     */
    private static class ButtonHandler implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent e) {
            String cmd = e.getActionCommand();

            switch (cmd) {
                default:
                    break;
                case "Load Network":
                    loadNetwork();
                    break;

                case "Create": //Create a new network
                    createNetwork();
                    break;

                case "Select Region":
                    selectRegion();
                    break;

                case "Set Region":
                    setRegion();
                    break;

                case "Play":
                    play();
                    break;

                case "Stop Playing":
                    networkTrainer.stopTraining();

                    trainerMenu();
                    break;

                case "Train":
                    train();
                    break;

                case "Stop Training":
                    networkTrainer.stopTraining();

                    try {
                        loadedNetwork.save();
                    } catch (IOException ex) {
                        System.err.println(ex);
                    }

                    trainerMenu();
                    break;

                case "Train/Play":
                    trainer.setVisible(true);
                    break;

                case "Show Brain":
                    brainMap.setVisible(true);
                    break;

                case "Show Gray Image":
                    gray.setVisible(true);
                    break;

                case "keyChanged":
                    outputNeurons[Integer.parseInt(outputsList.getSelectedItem()
                            .toString()) - 1].setKey(keyEventFields[keyList.
                                    getSelectedIndex()]);
                    break;

                case "outputNumChanged":
                    keyList.setSelectedItem(outputNeurons[Integer.parseInt(
                            outputsList.getSelectedItem().toString()) - 1]
                            .getKeyName());
                    break;

                case "Set Num Outputs":
                    setNumOutputs();
                    break;

                case "Set All Outputs":
                case "New Network":
                    newNetworkMenu();
                    break;

                case "Pick Outputs":
                    outputsMenu();
                    break;

                case "focusR":
                    if (loadedNetwork.getFocusHeight() == 0) {
                        loadedNetwork.setFocusHeight(
                                loadedNetwork.getInputResHeight());
                        loadedNetwork.setFocusWidth(
                                loadedNetwork.getInputResWidth());
                    }
                case "mainR":
                    region.repaint();
                    break;

                case ">": //Hidden Layer Select Buttons
                    if (selectedHiddenLayer < loadedNetwork
                            .getLayerSize() - 1) {
                        selectedHiddenLayer++;
                    }

                    hiddenLayerDisplayed.setText("Layer "
                            + selectedHiddenLayer);
                    brainMap.repaint();
                    break;
                case "<":
                    if (selectedHiddenLayer > 0) {
                        selectedHiddenLayer--;
                    }

                    hiddenLayerDisplayed.setText("Layer "
                            + selectedHiddenLayer);
                    brainMap.repaint();
                    break;

                case "+": //Region Buttons
                    if (mRegion.isSelected()) {
                        loadedNetwork.setRegionHeight(
                                loadedNetwork.getRegionHeight()
                                + loadedNetwork.getInputResHeight());
                        loadedNetwork.setRegionWidth(
                                loadedNetwork.getRegionWidth()
                                + loadedNetwork.getInputResWidth());
                    } else {
                        loadedNetwork.setFocusHeight(
                                loadedNetwork.getFocusHeight()
                                + loadedNetwork.getInputResHeight());
                        loadedNetwork.setFocusWidth(
                                loadedNetwork.getFocusWidth()
                                + loadedNetwork.getInputResWidth());
                    }
                    region.repaint();
                    break;
                case "-":
                    if (mRegion.isSelected()) {
                        if (loadedNetwork.getRegionHeight()
                                > loadedNetwork.getInputResHeight()) {
                            loadedNetwork.setRegionHeight(
                                    loadedNetwork.getRegionHeight()
                                    - loadedNetwork.getInputResHeight());
                            loadedNetwork.setRegionWidth(
                                    loadedNetwork.getRegionWidth()
                                    - loadedNetwork.getInputResWidth());
                        }
                    } else {
                        if (loadedNetwork.getFocusHeight() > 0) {
                            loadedNetwork.setFocusHeight(
                                    loadedNetwork.getFocusHeight()
                                    - loadedNetwork.getInputResHeight());
                            loadedNetwork.setFocusWidth(
                                    loadedNetwork.getFocusWidth()
                                    - loadedNetwork.getInputResWidth());
                        }
                    }
                    region.repaint();
                    break;
                case "^^":
                    if (mRegion.isSelected()) {
                        loadedNetwork.setRegionY(loadedNetwork.getRegionY()
                                - 1 * REGIONSCALE);
                    } else {
                        loadedNetwork.setFocusY(loadedNetwork.getFocusY()
                                - 1 * REGIONSCALE);
                    }
                    region.repaint();
                    break;
                case "VV":
                    if (mRegion.isSelected()) {
                        loadedNetwork.setRegionY(loadedNetwork.getRegionY()
                                + 1 * REGIONSCALE);
                    } else {
                        loadedNetwork.setFocusY(loadedNetwork.getFocusY()
                                + 1 * REGIONSCALE);
                    }
                    region.repaint();
                    break;
                case "<<":
                    if (mRegion.isSelected()) {
                        loadedNetwork.setRegionX(loadedNetwork.getRegionX()
                                - 1 * REGIONSCALE);
                    } else {
                        loadedNetwork.setFocusX(loadedNetwork.getFocusX()
                                - 1 * REGIONSCALE);
                    }
                    region.repaint();
                    break;
                case ">>":
                    if (mRegion.isSelected()) {
                        loadedNetwork.setRegionX(loadedNetwork.getRegionX()
                                + 1 * REGIONSCALE);
                    } else {
                        loadedNetwork.setFocusX(loadedNetwork.getFocusX()
                                + 1 * REGIONSCALE);
                    }
                    region.repaint();
                    break;
            }

        }

        /**
         * Load saved network.
         */
        private static void loadNetwork() {
            try {
                loadedNetwork = new Network(networkList.
                        getSelectedItem().toString());
                loadedNetwork.load();

                loadedNetworkMenu();
            } catch (IOException ex) {
            }
        }

        /**
         * Select input region.
         */
        private static void selectRegion() {
            screenshot = makeGray(robot.createScreenCapture(
                    new Rectangle(Toolkit.getDefaultToolkit().
                            getScreenSize())));

            region.setVisible(true);
        }

        /**
         * Train network.
         */
        private static void train() {
            JFrame[] trainFrames = new JFrame[3];
            trainFrames[0] = trainer;
            trainFrames[1] = gray;
            trainFrames[2] = brainMap;

            networkTrainer = new Trainer(loadedNetwork, trainFrames,
                    keyPressed, timeTrained, Integer.parseInt(updates.getText()),
                    Double.parseDouble(learnRate.getText()));
            networkTrainer.start();

            tPanel.removeAll();
            tPanel.add(stopTrainer);
            tPanel.add(keyPressed);
            tPanel.add(timeTrained);
            trainer.revalidate();
            trainer.repaint();
        }

        /**
         * Have network attempt to play.
         */
        private static void play() {
            JFrame[] playFrames = new JFrame[3];
            playFrames[0] = trainer;
            playFrames[1] = gray;
            playFrames[2] = brainMap;

            networkTrainer = new Trainer(loadedNetwork, playFrames,
                keyPressed, timeTrained, Integer.parseInt(updates.getText()),
                    Double.parseDouble(learnRate.getText()));
            networkTrainer.setMode(1);
            networkTrainer.start();

            tPanel.removeAll();
            tPanel.add(stopPlaying);
            tPanel.add(keyPressed);
            tPanel.add(timeTrained);
            trainer.revalidate();
            trainer.repaint();
        }

        /**
         * Set number of outputs.
         */
        private static void setNumOutputs() {
            if (!outputs.getText().equals("")) {
                outputNeurons = new ONeuron[Integer.parseInt(
                        outputs.getText())];
                
                for (int i = 0; i < Integer.parseInt(outputs.getText()); i++) {
                    
                    //Sets all output neurons to default in JComboBox for keys
                    outputNeurons[i] = new ONeuron(keyEventFields[0],
                            Math.random() * 11 - 5);
                    //If its not in the comboBox
                    if (i > outputsList.getItemCount() - 1) {
                        outputsList.addItem(i + 1);
                    }

                }
                //temp integer for combobox length
                int comboBoxItems = outputsList.getItemCount();

                //To remove items if the combobox is too big
                for (int i = 0; i < comboBoxItems; i++) {
                    if (i >= Integer.parseInt(outputs.getText())) {
                        outputsList.removeItem(i + 1);
                    }
                }
                keyList.setSelectedItem(keyNames[0]);
                outputsList.setSelectedItem(1);
            }
        }

        /**
         * Set input region.
         */
        private static void setRegion() {
            if (loadedNetwork.getRegionWidth() > 0
                    //If they set a region
                    && loadedNetwork.getRegionHeight() > 0) {
                region.setVisible(false);
                
                //Allows for region window to close before converting
                //original region
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                }

                loadedNetwork.convertRegion();
                mPanel.add(brain);
                mPanel.add(image);
                mPanel.add(openTrainer);
                menu.revalidate();
                menu.repaint();
                gray.repaint();
            }
        }

        /**
         * Create new network.
         */
        private static void createNetwork() {
            if (outputNeurons == null) { //If outputs are not chosen
                pickOutputs.setFont(new Font("Dialog", Font.BOLD, 14));
            } else {
                if (checkName(name.getText())) { //If name is not a dupliate
                    //Make network with the name given
                    loadedNetwork = new Network(name.getText());
                    //Send the network its output neurons
                    loadedNetwork.setOutputs(outputNeurons);
                    loadedNetwork.setNetType(networkTypes.getSelection()
                            .getActionCommand());

                    int resW = Integer.parseInt(resWidth.getText());
                    int resH = Integer.parseInt(resHeight.getText());
                    int lay = Integer.parseInt(layers.getText());
                    int neu = Integer.parseInt(neurons.getText());

                    loadedNetwork.loadRandom(resW, resH, lay, neu);

                    loadedNetworkMenu();
                } else {
                    mPanel.add(nameError);
                    menu.repaint();
                }
            }
        }
    }

    /**
     * A JPanel for the grey scale image.
     */
    private static class ImagePanel extends JPanel {

        @Override
        public void paintComponent(Graphics g) {
            int resW = loadedNetwork.getInputResWidth();
            int resH = loadedNetwork.getInputResHeight();
            int n = 0; //Current neuron

            for (int i = 0; i < resH; i++) {
                //Prints all inputNeurons to the screen
                for (int j = 0; j < resW; j++) {
                    int color = loadedNetwork.getINeuronValue(n);

                    g.setColor(new Color(color, color, color));
                    g.fillRect(j * ((gray.getWidth() - 6) / resW),
                            i * ((gray.getHeight() - 29) / resH),
                            (gray.getWidth() - 6) / resW,
                            (gray.getHeight() - 29) / resH);

                    n++;
                }
            }
        }
    }

    /**
     * A JPanel for the brain images.
     */
    private static class BrainPanel extends JPanel {

        @Override
        public void paintComponent(final Graphics g) {
            Layer currentLayer = loadedNetwork.getLayer(selectedHiddenLayer);
            int resW = (int) Math.sqrt(currentLayer.getSize());
            int resH = (int) Math.sqrt(currentLayer.getSize());
            int n = 0; //Current neuron

            for (int i = 0; i < resH; i++) {
                //Prints all neurons in the layer to the screen
                for (int j = 0; j < resW; j++) {
                    int color = (int) (currentLayer.getNeuronValue(n) * 255);

                    g.setColor(new Color(color, color, color));
                    g.fillRect(j * ((brainMap.getWidth() - 6) / resW),
                            i * ((brainMap.getHeight() - 29) / resH),
                            (brainMap.getWidth() - 6) / resW,
                            (brainMap.getHeight() - 29) / resH);

                    n++;
                }
            }
        }
    }

    /**
     * A JPanel for the region selector.
     */
    private static class RegionPanel extends JPanel {

        @Override
        public void paintComponent(final Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));

            g2.drawImage(screenshot, 0, 40, screenshot.getWidth() / REGIONSCALE,
                    screenshot.getHeight() / REGIONSCALE, null);

            g2.setColor(Color.RED);
            g2.drawRect(loadedNetwork.getRegionX() / REGIONSCALE,
                    40 + loadedNetwork.getRegionY() / REGIONSCALE,
                    loadedNetwork.getRegionWidth() / REGIONSCALE,
                    loadedNetwork.getRegionHeight() / REGIONSCALE);
            if (loadedNetwork.getFocusHeight() > 0) {
                g2.setColor(Color.ORANGE);
                g2.drawRect(loadedNetwork.getFocusX() / REGIONSCALE,
                        40 + loadedNetwork.getFocusY() / REGIONSCALE,
                        loadedNetwork.getFocusWidth() / REGIONSCALE,
                        loadedNetwork.getFocusHeight() / REGIONSCALE);
            }

            g2.setColor(new Color(30, 170, 255));
            g2.fillRect(0,0,screenshot.getWidth() / REGIONSCALE,40);
        }
    }

    /**
     * A JPanel for the menu.
     */
    private static class MenuPanel extends JPanel {

        @Override
        public void paintComponent(final Graphics g) {
            g.drawImage(nNetPic, -5, -10, null);
        }
    }

    /**
     * Mouse control handler.
     */
    private static class Mouse implements MouseListener {

        @Override
        public void mouseClicked(final MouseEvent me) {
            //unused
        }

        @Override
        public void mousePressed(final MouseEvent me) {
            if (mRegion.isSelected()) {
                loadedNetwork.setRegionX((me.getX() - 2) * REGIONSCALE);
                loadedNetwork.setRegionY((me.getY() - 70) * REGIONSCALE);
            } else {
                loadedNetwork.setFocusX((me.getX() - 2) * REGIONSCALE);
                loadedNetwork.setFocusY((me.getY() - 70) * REGIONSCALE);
            }

            region.repaint();
        }

        @Override
        public void mouseReleased(final MouseEvent me) {
            //unused
        }

        @Override
        public void mouseEntered(final MouseEvent me) {
            //unused
        }

        @Override
        public void mouseExited(final MouseEvent me) {
            //unused
        }

    }
}
