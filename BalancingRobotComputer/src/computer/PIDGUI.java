package computer;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import se.lth.control.*;

public class PIDGUI {
	private String name;
	private BluetoothMonitor blMon;
	private PIDParameters params;
	private JPanel paramsLabelPanel = new JPanel();
	private JPanel paramsFieldPanel = new JPanel();
	private BoxPanel paramsPanel = new BoxPanel(BoxPanel.HORIZONTAL);
	private DoubleField paramsKField = new DoubleField(6, 3);
	private DoubleField paramsTiField = new DoubleField(6, 3);
	private DoubleField paramsTdField = new DoubleField(6, 3);
	private DoubleField paramsNField = new DoubleField(6, 3);
	private DoubleField paramsTrField = new DoubleField(6, 3);
	private DoubleField paramsBetaField = new DoubleField(6, 3);
	private DoubleField paramsHField = new DoubleField(6, 3);
	private JButton paramsButton = new JButton("Apply");
	private JPanel directionBtnPanel = new JPanel();
	private JButton forwardBtn = new JButton("↑");
	private JButton backwardBtn = new JButton("↓");

	public PIDGUI(BluetoothMonitor blMon2, PIDParameters p, String n) {
		name = n;

		this.blMon = blMon2;
		params = p;
		p.K = 4;
		p.Ti = 0;
		p.Td = 0.2;// 0.085;
		p.N = 10;
		p.Tr = 1;
		p.Beta = 1;
		p.H = 0.01;
		MainFrame.showLoading();
		paramsLabelPanel.setLayout(new GridLayout(0, 1));
		paramsLabelPanel.add(new JLabel("K: "));
		paramsLabelPanel.add(new JLabel("Ti: "));
		paramsLabelPanel.add(new JLabel("Td: "));
		paramsLabelPanel.add(new JLabel("N: "));
		paramsLabelPanel.add(new JLabel("Tr: "));
		paramsLabelPanel.add(new JLabel("Beta: "));
		paramsLabelPanel.add(new JLabel("h: "));

		paramsFieldPanel.setLayout(new GridLayout(0, 1));
		paramsFieldPanel.add(paramsKField);
		paramsFieldPanel.add(paramsTiField);
		paramsFieldPanel.add(paramsTdField);
		paramsFieldPanel.add(paramsNField);
		paramsFieldPanel.add(paramsTrField);
		paramsFieldPanel.add(paramsBetaField);
		paramsFieldPanel.add(paramsHField);
		paramsPanel.add(paramsLabelPanel);
		paramsPanel.addGlue();
		paramsPanel.add(paramsFieldPanel);
		paramsPanel.addFixed(10);
		paramsKField.setValue(p.K);
		paramsTiField.setValue(p.Ti);
		paramsTdField.setValue(p.Td);
		paramsNField.setValue(p.N);
		paramsTrField.setValue(p.Tr);
		paramsBetaField.setValue(p.Beta);
		paramsHField.setValue(p.H);
		paramsKField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				double tempValue = paramsKField.getValue();
				params.K = tempValue;
				paramsButton.setEnabled(true);
			}
		});
		paramsTiField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double tempValue = paramsTiField.getValue();
				if (tempValue < 0.0) {
					paramsTiField.setValue(params.Ti);
				} else {
					params.Ti = tempValue;
					paramsButton.setEnabled(true);
					params.integratorOn = (params.Ti != 0.0);
				}
			}
		});
		paramsTdField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double tempValue = paramsTdField.getValue();
				if (tempValue < 0.0) {
					paramsTdField.setValue(params.Td);
				} else {
					params.Td = tempValue;
					paramsButton.setEnabled(true);
				}
			}
		});
		paramsNField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double tempValue = paramsNField.getValue();
				if (tempValue < 0.0) {
					paramsNField.setValue(params.N);
				} else {
					params.N = tempValue;
					paramsButton.setEnabled(true);
				}
			}
		});
		paramsTrField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double tempValue = paramsTrField.getValue();
				if (tempValue < 0.0) {
					paramsTrField.setValue(params.Tr);
				} else {
					params.Tr = tempValue;
					paramsButton.setEnabled(true);
				}
			}
		});

		paramsBetaField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double tempValue = paramsBetaField.getValue();

				params.Beta = tempValue;
				paramsButton.setEnabled(true);

			}
		});

		paramsHField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double tempValue = paramsHField.getValue();
				if (tempValue < 0.0) {
					paramsHField.setValue(params.H);
				} else {
					params.H = tempValue;
					paramsButton.setEnabled(true);
				}
			}
		});

		forwardBtn.addMouseListener(createMouseListener(
				Sendobject.CMD_SET_DIRECTION, 1));
		backwardBtn.addMouseListener(createMouseListener(
				Sendobject.CMD_SET_DIRECTION, -1));

		BoxPanel paramButtonPanel = new BoxPanel(BoxPanel.VERTICAL);
		paramButtonPanel.setBorder(BorderFactory
				.createTitledBorder("Parameters"));
		paramButtonPanel.addFixed(10);
		paramButtonPanel.add(paramsPanel);
		paramButtonPanel.addFixed(10);
		paramButtonPanel.add(paramsButton);

		directionBtnPanel.setLayout(new GridLayout(1, 2));
		directionBtnPanel.add(forwardBtn);
		directionBtnPanel.add(backwardBtn);

		paramButtonPanel.addFixed(10);
		paramButtonPanel.add(directionBtnPanel);

		paramsButton.setEnabled(false);
		paramsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Sendobject so = new Sendobject(Sendobject.CMD_SET_PARAMETERS,
						new double[] { params.Beta, params.H,
								params.K, params.N,
								params.Td, params.Ti,
								params.Tr });
				// blMon.newSendData(new double[]{l1Value, l2Value, 0});
				blMon.newSendData(so);
				paramsButton.setEnabled(false);
			}
		});
		MainFrame.setPanel(paramButtonPanel, name);
	}

	private MouseListener createMouseListener(final int cmd, final int direction) {
		MouseListener ml = new MouseListener() {
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
				if (isRunning)
					return false;
				isRunning = true;
				return true;
			}

			private void initThread() {
				if (checkAndMark()) {
					new Thread() {
						public void run() {
							do {
								Sendobject so = new Sendobject(cmd, direction);
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
