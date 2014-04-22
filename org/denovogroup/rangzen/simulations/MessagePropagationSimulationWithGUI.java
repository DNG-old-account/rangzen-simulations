package org.denovogroup.rangzen.simulations;

import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.display.Console;
import sim.engine.SimState;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.util.gui.SimpleColorMap;

import java.awt.*;
import javax.swing.*;

public class MessagePropagationSimulationWithGUI extends GUIState {
    public Display2D display;
    public JFrame displayFrame;
    private ContinuousPortrayal2D spacePortrayal = new ContinuousPortrayal2D();

    public MessagePropagationSimulationWithGUI() { 
      super(new MessagePropagationSimulation(System.currentTimeMillis()));
    }
    
    public MessagePropagationSimulationWithGUI(SimState state) { 
      super(state);
     }

    public void setupPortrayals() {
      // What to display.
      spacePortrayal.setField(((MessagePropagationSimulation) state).space);
      spacePortrayal.setPortrayalForAll(new OvalPortrayal2D(2));

      display.reset();    // reschedule the displayer
      display.setBackdrop(Color.white);
      display.repaint();  // redraw the display
    }

    public void start() {
      super.start();      
      setupPortrayals();  // set up our portrayals
    }
    public void load(SimState state) {
      super.load(state);      
      setupPortrayals();  // set up our portrayals for the new SimState model
    }

    public void init(Controller c) {
      super.init(c);

      MessagePropagationSimulation sim = (MessagePropagationSimulation) state;
      display = new Display2D(sim.width, sim.height, this);
      display.setClipping(false);
      displayFrame = display.createFrame();
      c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
      displayFrame.setVisible(true);
      display.attach(spacePortrayal, "Space");  // attach the portrayals

      // specify the backdrop color  -- what gets painted behind the displays
      // display.setBackdrop(Color.black);
    }

    public static void main(String[] args) {
      // new MessagePropagationSimulationWithGUI().createController();
      MessagePropagationSimulationWithGUI mpsGUI = new MessagePropagationSimulationWithGUI();
      Console c = new Console(mpsGUI);
      c.setVisible(true);
    }

    

    public static String getName() { 
      return "Message Propagation Simulation"; 
    }
    
    public static Object getInfo() {
      return "Simulation of Message Propagation in Rangzen."; 
    }



}
