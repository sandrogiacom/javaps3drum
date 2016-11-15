/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.giacom.javaps3drum;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 *
 * @author sandrogiacom@gmail.com
 */
public class JavaPS3Drum {

	final MainFrame window;
	private ArrayList<Controller> foundControllers;

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		new JavaPS3Drum();
	}

	public JavaPS3Drum() {
		window = new MainFrame();
		// window.setVisible(true);

		foundControllers = new ArrayList<>();
		searchForControllers();

		// If at least one controller was found we start showing controller data
		// on window.
		if (!foundControllers.isEmpty())
			try {
				Runnable[] run = new Runnable[5];
				for (int i = 0; i < 5; i++) {
					run[i] = new Runnable() {
						@Override
						public void run() {
							try {
								startShowingControllerData();
							} catch (InterruptedException ex) {
								Logger.getLogger(JavaPS3Drum.class.getName()).log(Level.SEVERE, null, ex);
							}
						}
					};
					run[i].run();

				}
			} catch (Exception ex) {
				Logger.getLogger(JavaPS3Drum.class.getName()).log(Level.SEVERE, null, ex);
			}
		else
			window.addControllerName("No controller found!");
	}

	/**
	 * Search (and save) for controllers of type Controller.Type.STICK,
	 * Controller.Type.GAMEPAD, Controller.Type.WHEEL and
	 * Controller.Type.FINGERSTICK.
	 */
	private void searchForControllers() {
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

		for (int i = 0; i < controllers.length; i++) {
			Controller controller = controllers[i];

			if (controller.getType() == Controller.Type.STICK || controller.getType() == Controller.Type.GAMEPAD
					|| controller.getType() == Controller.Type.WHEEL
					|| controller.getType() == Controller.Type.FINGERSTICK) {
				// Add new controller to the list of all controllers.
				foundControllers.add(controller);

				// Add new controller to the list on the window.
				window.addControllerName(controller.getName() + " - " + controller.getType().toString() + " type");
			}
		}
	}

	/**
	 * Starts showing controller data on the window.
	 */
	@SuppressWarnings("restriction")
	private void startShowingControllerData() throws InterruptedException {
		System.out.println("Thread: " + Thread.currentThread().getName());
		while (true) {
			// Currently selected controller.
			int selectedControllerIndex = window.getSelectedControllerName();
			Controller controller = foundControllers.get(selectedControllerIndex);

			// Pull controller for current data, and break while loop if
			// controller is disconnected.
			if (!controller.poll()) {
				window.showControllerDisconnected();
				break;
			}

			// Go trough all components of the controller.
			Component[] components = controller.getComponents();
			for (int i = 0; i < components.length; i++) {
				Component component = components[i];
				Component.Identifier componentIdentifier = component.getIdentifier();

				// Buttons
				// if(component.getName().contains("Button")){ // If the
				// language is not english, this won't work.
				if (componentIdentifier.getName().matches("^[0-9]*$")) { 
					// Is button pressed?
					boolean isItPressed = true;
					if (component.getPollData() == 0.0f)
						isItPressed = false;

					// Button index
					String buttonIndex;
					buttonIndex = component.getIdentifier().toString();

					if (isItPressed) {
						System.out.println(buttonIndex);
						try {
							if (Integer.parseInt(buttonIndex) <= 4) {
								window.changeImage(buttonIndex, true);

								System.out.println("play...");

								InputStream in = new FileInputStream(
										getClass().getResource("/sound/" + buttonIndex + ".wav").getFile());
								AudioStream as = new AudioStream(in);
								AudioPlayer.player.start(as);

								Thread.sleep(300);
								window.changeImage(buttonIndex, false);
							}

						} catch (FileNotFoundException ex) {
							Logger.getLogger(JavaPS3Drum.class.getName()).log(Level.SEVERE, null, ex);
						} catch (IOException ex) {
							Logger.getLogger(JavaPS3Drum.class.getName()).log(Level.SEVERE, null, ex);
						}

					}
					continue;
				}

				// Hat switch
				if (componentIdentifier == Component.Identifier.Axis.POV) {
					float hatSwitchPosition = component.getPollData();
					window.setHatSwitch(hatSwitchPosition);

					// We know that this component was hat switch so we can skip
					// to next component.
					continue;
				}
			
			}

		}
	}

	/**
	 * Given value of axis in percentage. Percentages increases from left/top to
	 * right/bottom. If idle (in center) returns 50, if joystick axis is pushed
	 * to the left/top edge returns 0 and if it's pushed to the right/bottom
	 * returns 100.
	 * 
	 * @return value of axis in percentage.
	 */
	public int getAxisValueInPercentage(float axisValue) {
		return (int) (((2 - (1 - axisValue)) * 100) / 2);
	}

}
