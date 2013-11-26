package computer;

import javax.swing.*;

import se.lth.control.DoubleField;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GUI {
	private String name;
	private BluetoothMonitor blMon;
	private Random rand = new Random();

	public GUI(String name, BluetoothMonitor blMon) {
		this.name = name;
		this.blMon = blMon;
	}

	public void initializeGUI() {
		JFrame frame = new JFrame(name);
		JPanel pane = new JPanel();
		JButton button = new JButton("Send");
		JButton quitbutton = new JButton("QUIT");
		
		JButton forwardBtn = new JButton("↑");
		JButton backwardBtn = new JButton("↓");
		
		JLabel kP = new JLabel("kP: ");
		JLabel kD = new JLabel("kD: ");
		final DoubleField paramsl1 = new DoubleField(6, 3);
		final DoubleField paramsl2 = new DoubleField(6, 3);
		paramsl1.setValue(50.0);
		paramsl2.setValue(4.0);

		
		pane.setLayout(new GridLayout(5, 2));
		pane.add(kP);
		pane.add(kD);
		pane.add(paramsl1);
		pane.add(paramsl2);
		pane.add(button);
		pane.add(quitbutton);
		pane.add(new JLabel(" "));
		pane.add(new JLabel(" "));
		pane.add(forwardBtn);
		pane.add(backwardBtn);
		
		paramsl1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double tempValue = paramsl1.getValue();
			}
		});
		
		paramsl2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double tempValue = paramsl2.getValue();
			}
		});

		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				float l1Value = (float) paramsl1.getValue();
				float l2Value = (float) paramsl2.getValue();
				Sendobject so = new Sendobject(
						Sendobject.CMD_SET_PARAMETERS, new float[] {
								l1Value, l2Value });
				// blMon.newSendData(new double[]{l1Value, l2Value, 0});
				blMon.newSendData(so);
				//label.setText("" + r);
			}
		});
		quitbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(Thread.currentThread().getPriority());
				System.exit(0);
			}
		});
		
		forwardBtn.addMouseListener(createMouseListener(Sendobject.CMD_SET_DIRECTION, 1));
		
		
		backwardBtn.addMouseListener(createMouseListener(Sendobject.CMD_SET_DIRECTION, -1));
			
		frame.getContentPane().add(pane, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}
	
	private MouseListener createMouseListener(final int cmd, final int direction){
		MouseListener ml = new MouseListener(){
			boolean mouseDown = false;

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
			        mouseDown = true;
			        initThread();
			    }
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
			        mouseDown = false;
			    }
				
			}
			private boolean isRunning = false;
			private synchronized boolean checkAndMark() {
			    if (isRunning) return false;
			    isRunning = true;
			    return true;
			}
			private void initThread() {
			    if (checkAndMark()) {
			        new Thread() {
			            public void run() {
			                do {
			                    Sendobject so = new Sendobject(
			    						cmd, direction);
			    				blMon.newSendData(so);
			                    try {
									sleep(100);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			                } while (mouseDown);
			                isRunning = false;
			            }
			        }.start();
			    }
			}
			
		};
		return ml;
	}
}
