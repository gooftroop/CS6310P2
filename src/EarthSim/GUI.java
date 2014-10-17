// GUI.java
package EarthSim;

import common.State;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;

import java.awt.FlowLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;

import javax.swing.JOptionPane;

import messaging.Publisher;
import messaging.events.CloseMessage;
import messaging.events.PauseMessage;
import messaging.events.ResumeMessage;
import messaging.events.StartMessage;
import messaging.events.StopMessage;

import java.util.HashMap;

public class GUI extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6146431536208036768L;
	
	private JPanel contents;
	private EarthSimEngine engine;
	private final Publisher publisher;
	
	private HashMap<String, JTextField> outputs = new HashMap<String, JTextField>();
	private HashMap<String, JTextField> inputs = new HashMap<String, JTextField>();

	public GUI(boolean ownSimThread, boolean ownPresThread, State initiative, long bufferSize) {
		
		// BW - still need to think on the best way to do this. But that's only when
		// we have the papers done...
		this.engine = new EarthSimEngine(initiative, ownSimThread, ownPresThread, (int) bufferSize);
		this.publisher = Publisher.getInstance();
		
		// TODO this blocks close
//		Runtime.getRuntime().addShutdownHook(new Thread() {
//			
//			@Override
//			public void run() {
//				publisher.send(new CloseMessage());
//				Publisher.unsubscribeAll();
//			}
//		});

		setupWindow();
		pack();
	}

	private void setupWindow() {
		
		// setup overall app ui
		setTitle("Heated Earth Diffusion Simulation");
		
		setSize(300, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		add(settingsAndControls(), BorderLayout.CENTER);
	}

//	private JPanel contentsPanel() {
//		
//		// setup primary window contents panel
//		JPanel contents = new JPanel(new BorderLayout());
//		contents.setBorder(new EmptyBorder(new Insets(20, 20, 20, 20)));
//		contents.setAlignmentY(Component.TOP_ALIGNMENT);
//
//		contents.add(settingsNControls(), BorderLayout.WEST);
//		contents.add(presentation(), BorderLayout.CENTER);
//		// contents.add(feedback(),BorderLayout.SOUTH);
//
//		this.contents = contents;
//		return contents;
//	}

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
		
		// settingsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		// settingsPanel.add(prompt("Set initial conditions"));
		
		settingsPanel.add(inputField("Grid Spacing"));
		settingsPanel.add(inputField("Simulation Time Step"));
		settingsPanel.add(inputField("Presentation Rate"));

		return settingsPanel;
	}

	private JPanel runControls() {
		
		JPanel ctrlsPanel = new JPanel(new FlowLayout());
		ctrlsPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

		ctrlsPanel.add(button("Start"));
		ctrlsPanel.add(button("Pause"));
		ctrlsPanel.add(button("Resume"));
		ctrlsPanel.add(button("Stop"));

		return ctrlsPanel;
	}

//	private JPanel presentation() {
//		
//		JPanel pres = new JPanel();
//		pres.setBorder(BorderFactory.createLineBorder(Color.black));
//		return pres;
//	}

//	private JPanel feedback() {
//		
//		JPanel fbPanel = new JPanel();
//		fbPanel.setLayout(new BoxLayout(fbPanel, BoxLayout.PAGE_AXIS));
//		fbPanel.add(displayField("Rotational Position"));
//		fbPanel.add(displayField("Time"));
//		fbPanel.add(displayField("Grid Spacing"));
//		fbPanel.add(displayField("Simulation Time Step"));
//
//		return fbPanel;
//	}

	private JPanel inputField(String name) {
		
		JPanel inputPanel = new JPanel();
		// inputPanel.setBorder(BorderFactory.createTitledBorder("blah"));
		// inputPanel.setLayout(new BoxLayout(inputPanel,BoxLayout.LINE_AXIS));
		inputPanel.setLayout(new FlowLayout());
		inputPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

		JLabel l = new JLabel(name);
		l.setAlignmentX(Component.LEFT_ALIGNMENT);
		inputPanel.add(l);

		// inputPanel.add(Box.createHorizontalGlue());
		JTextField t = new JTextField("", 10);
		t.setAlignmentX(Component.RIGHT_ALIGNMENT);
		l.setLabelFor(t);
		inputPanel.add(t);

		inputs.put(name, t);
		return inputPanel;
	}

//	private JPanel displayField(String name) {
//		
//		JPanel outputPanel = new JPanel();
//		// outputPanel.setBorder(BorderFactory.createTitledBorder("blah"));
//		// outputPanel.setLayout(new
//		// BoxLayout(outputPanel,BoxLayout.LINE_AXIS));
//		outputPanel.setLayout(new FlowLayout());
//		outputPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
//
//		JLabel l = new JLabel(name);
//		l.setAlignmentX(Component.LEFT_ALIGNMENT);
//		outputPanel.add(l);
//
//		// outputPanel.add(Box.createHorizontalGlue());
//		JTextField t = new JTextField("", 10);
//		t.setAlignmentX(Component.RIGHT_ALIGNMENT);
//		t.setEditable(false);
//		l.setLabelFor(t);
//		outputPanel.add(t);
//
//		outputs.put(name, t);
//		return outputPanel;
//	}

	private JButton button(String name) {
		
		JButton button = new JButton(name);
		button.setActionCommand(name);
		button.addActionListener(this);
		return button;
	}

	public void actionPerformed(ActionEvent e) {
		
		String cmd = e.getActionCommand();
		
		if ("Start".equals(cmd)) {
			if (configureEngine())
				this.start();
		}
		
		if ("Pause".equals(cmd))
			this.pause();
		
		if ("Resume".equals(cmd))
			this.resume();
		
		if ("Stop".equals(cmd)) 
			this.stop();
	}
	
	public void start() {
		
		publisher.send(new StartMessage());
	}
	
	public void stop() {
		publisher.send(new StopMessage());	
	}
	
	public void pause() {
		publisher.send(new PauseMessage());
	}
	
	public void resume() {
		publisher.send(new ResumeMessage());
	}

	private boolean configureEngine() {
		
		try {
			
			int gs = Integer.parseInt(inputs.get("Grid Spacing").getText());
			int timeStep = Integer.parseInt(inputs.get("Simulation Time Step").getText());
			long presentationRate = Long.parseLong(inputs.get("Presentation Rate").getText());

			engine.configure(gs, timeStep, presentationRate);
			return true;

		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null,
					"Please correct input. All fields need numbers");
		}
		
		return false;
	}
}
