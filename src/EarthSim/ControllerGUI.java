// GUI.java
package EarthSim;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import common.State;

public class ControllerGUI extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6146431536208036768L;
	
//	private EarthSimEngine engine;
//	private final Publisher publisher;
	private Controller controller;
	
	private HashMap<String, JTextField> inputs = new HashMap<String, JTextField>();
	private HashMap<String, JButton> buttons = new HashMap<String, JButton>();

	public ControllerGUI(boolean ownSimThread, boolean ownPresThread, State initiative, long bufferSize) {
		
		// Remap initiative setting
		InitiativeSetting init2;
		switch(initiative) {
		case PRESENTATION:
			init2 = InitiativeSetting.VIEW;
			break;
		case SIMULATION:
			init2 = InitiativeSetting.MODEL;
			break;
		case MASTER:
			init2 = InitiativeSetting.THIRD_PARTY;
			break;
		default:
			init2 = null;
				
		}
		controller = new Controller(ownSimThread, ownPresThread, init2, (int)bufferSize);
//		// BW - still need to think on the best way to do this. But that's only when
//		// we have the papers done...
//		this.engine = new EarthSimEngine(initiative, ownSimThread, ownPresThread, (int) bufferSize);
//		this.publisher = Publisher.getInstance();

		setupWindow();
		pack();
	}

	private void setupWindow() {
		
		// setup overall app ui
		setTitle("Heated Earth Diffusion Simulation");
		
		setSize(300, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// TODO this blocks close
//		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
//		Runtime.getRuntime().addShutdownHook(new Thread() {
//			
//			@Override
//			public void run() {
//				publisher.send(new CloseMessage());
//				Publisher.unsubscribeAll();
//			}
//		});
		
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setAlwaysOnTop(true);
		
		add(settingsAndControls(), BorderLayout.CENTER);
	}

	private JPanel settingsAndControls() {
		
		JPanel sncPanel = new JPanel();
		sncPanel.setLayout(new BoxLayout(sncPanel, BoxLayout.PAGE_AXIS));
		sncPanel.setAlignmentY(Component.TOP_ALIGNMENT);

		sncPanel.add(settings(), BorderLayout.WEST);
		sncPanel.add(runControls(), BorderLayout.WEST);

		return sncPanel;
	}

	private JPanel settings() {
		
		JPanel settingsPanel = new JPanel();
		settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
		settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.PAGE_AXIS));
		settingsPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		
		settingsPanel.add(inputField("Grid Spacing","2"));
		settingsPanel.add(inputField("Simulation Time Step","50"));
		settingsPanel.add(inputField("Presentation Rate","0.01"));

		return settingsPanel;
	}

	private JPanel runControls() {
		
		JPanel ctrlsPanel = new JPanel(new FlowLayout());
		ctrlsPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

		ctrlsPanel.add(button("Start"));
		ctrlsPanel.add(button("Pause"));
		ctrlsPanel.add(button("Resume"));
		ctrlsPanel.add(button("Stop"));

		buttons.get("Start").setEnabled(true);
		buttons.get("Pause").setEnabled(false);
		buttons.get("Resume").setEnabled(false);
		buttons.get("Stop").setEnabled(false);
		
		return ctrlsPanel;
	}

	private JPanel inputField(String name, String defaultText) {
		
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new FlowLayout());
		inputPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

		JLabel l = new JLabel(name);
		l.setAlignmentX(Component.LEFT_ALIGNMENT);
		inputPanel.add(l);

		JTextField t = new JTextField(defaultText, 10);
		t.setAlignmentX(Component.RIGHT_ALIGNMENT);
		l.setLabelFor(t);
		inputPanel.add(t);

		inputs.put(name, t);
		return inputPanel;
	}

	private JButton button(String name) {
		
		JButton button = new JButton(name);
		button.setActionCommand(name);
		button.addActionListener(this);
		buttons.put(name, button);
		return button;
	}

	public void actionPerformed(ActionEvent e) {
		
		String cmd = e.getActionCommand();
		
		if ("Start".equals(cmd)) {
//			controller.start();
			if (configureEngine()) {
				//do gui stuff to indicate start has occurred.
				buttons.get("Start").setEnabled(false);
				buttons.get("Pause").setEnabled(true);
				buttons.get("Resume").setEnabled(false);
				buttons.get("Stop").setEnabled(true);
			}
//				this.start();
		}
		
		else if ("Pause".equals(cmd)) {
			controller.pause();
			buttons.get("Pause").setEnabled(false);
			buttons.get("Resume").setEnabled(true);
		}
		
		else if ("Resume".equals(cmd)) {
			controller.restart();
			buttons.get("Pause").setEnabled(true);
			buttons.get("Resume").setEnabled(false);
			
		}
		
		else if ("Stop".equals(cmd)) {
			try {
				controller.stop();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
//				e1.printStackTrace();
			}
			buttons.get("Start").setEnabled(true);
			buttons.get("Pause").setEnabled(false);
			buttons.get("Resume").setEnabled(false);
			buttons.get("Stop").setEnabled(false);
		}
	}
	
//	public void start() {
//		publisher.send(new StopMessage());
//		publisher.send(new StartMessage());
//	}
//	
//	public void stop() {
//		publisher.send(new StopMessage());	
//	}
//	
//	public void pause() {
//		publisher.send(new PauseMessage());
//	}
//	
//	public void resume() {
//		publisher.send(new ResumeMessage());
//	}

	private boolean configureEngine() {
		
		try {
			
			final int gs = Integer.parseInt(inputs.get("Grid Spacing").getText());
			final int timeStep = Integer.parseInt(inputs.get("Simulation Time Step").getText());
			final float presentationRate = Float.parseFloat(inputs.get("Presentation Rate").getText());

			controller.start(gs, timeStep, presentationRate);
//			SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				controller.start(gs, timeStep, presentationRate);
//			}
//			});
			
//			controller.start(gs, timeStep, presentationRate);
//			engine.configure(gs, timeStep, presentationRate);
			
			return true;

		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null,
					"Please correct input. All fields need numbers");
		}
		
		return false;
	}
}
