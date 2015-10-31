package io.kuenzler.dbcrawl.gui;

import io.kuenzler.dbcrawl.control.Crawler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * @author Leonhard Künzler
 * @version 0.1
 * @date 15.10.31 23:00
 */
public class MainGui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7085376948951446720L;
	private JPanel contentPane;
	private JTextField t_start;
	private JTextField t_dest;
	private JLabel lblStart;
	private JLabel lblDestination;
	private JTextArea ta_all;
	private JButton b_check;

	Crawler main;
	private JButton btnNewButton;
	private JTextField t_status;

	/**
	 * Creates test gui
	 * 
	 * @param main
	 *            Crawler for connection
	 */
	public MainGui(Crawler main) {
		setTitle("SBahn Crawl");
		this.main = main;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		t_start = new JTextField();
		t_start.setBounds(98, 11, 326, 27);
		contentPane.add(t_start);
		t_start.setColumns(10);

		t_dest = new JTextField();
		t_dest.setColumns(10);
		t_dest.setBounds(98, 49, 326, 27);
		contentPane.add(t_dest);

		lblStart = new JLabel("Start");
		lblStart.setBounds(10, 17, 46, 14);
		contentPane.add(lblStart);

		lblDestination = new JLabel("Destination");
		lblDestination.setBounds(10, 55, 78, 14);
		contentPane.add(lblDestination);

		ta_all = new JTextArea();
		ta_all.setEditable(false);
		ta_all.setBounds(10, 118, 414, 111);
		contentPane.add(ta_all);

		b_check = new JButton("Check");
		b_check.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				refreshData();
			}
		});
		b_check.setBounds(98, 84, 89, 23);
		contentPane.add(b_check);

		btnNewButton = new JButton("Exit");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(1);
			}
		});
		btnNewButton.setBounds(335, 84, 89, 23);
		contentPane.add(btnNewButton);

		t_status = new JTextField();
		t_status.setEditable(false);
		t_status.setBounds(10, 230, 414, 20);
		contentPane.add(t_status);
		t_status.setColumns(10);

		setVisible(true);
	}

	/**
	 * Set update String to status line
	 * 
	 * @param update
	 *            Update String
	 */
	public void setUpdate(String update) {
		t_status.setText(update.trim());
	}

	/**
	 * triggers data update from crawler and set result to gui
	 */
	public void refreshData() {
		ArrayList<String> data = main.crawlData(t_dest.getText(),
				t_start.getText());
		String info = "";
		for (String x : data) {
			info += x + "\n";
		}
		ta_all.setText(info);
	}
}
