
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class GameOfLife extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final int ROWS = 40, COLS = 40;
	private JButton cells[][];
	// use map so can keep track of all cell values and update simultaneously
	private Map<JButton, Color> cellsMap;
	private Container container, options;

	public GameOfLife() {
		setBoardDesign();
		cells = new JButton[COLS][ROWS];
		cellsMap = new HashMap<JButton, Color>();
		createAndFillBoxes();
		createOptionMenu();
		setVisible(true);
	}

	public void setBoardDesign() {
		setSize(800, 600);
		setTitle("Game of Life");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		setLayout(new BorderLayout());
		container = new Container();
		container.setLayout(new GridLayout(COLS, ROWS));
		add(container, BorderLayout.CENTER);
		options = new Container();
		options.setLayout(new GridLayout(6, 0));
		add(options, BorderLayout.WEST);
	}

	private void createOptionMenu() {
		// create timer to be used to start continuous next generation
		ActionListener timeRun = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (cellsMap.containsValue(Color.GREEN)) {
					nextGeneration();
				}
			}
		};
		Timer timer = new Timer(0, timeRun);

		// add button to clears board and create a new board with random cells filled
		JButton newGame = new JButton("NEW");
		ActionListener newGameListen = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				container.removeAll();
				createAndFillBoxes();
				setVisible(true);
			}
		};
		newGame.addActionListener(newGameListen);
		newGame.setAlignmentY(CENTER_ALIGNMENT);
		options.add(newGame);

		// add button to print the next generation
		JButton nextG = new JButton("NEXT");
		ActionListener nextGListen = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (timer.isRunning()) {
					timer.stop();
				}
				nextGeneration();
			}
		};
		nextG.addActionListener(nextGListen);
		nextG.setAlignmentY(CENTER_ALIGNMENT);
		options.add(nextG);

		// add button that will continue to print next generation until stopped
		JButton start = new JButton("START");
		ActionListener startListen = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				timer.start();
			}
		};
		start.addActionListener(startListen);
		options.add(start);

		// add button to stop continuous next generation if currently running
		JButton stop = new JButton("STOP");
		ActionListener stopListen = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				timer.stop();
			}
		};
		stop.addActionListener(stopListen);
		options.add(stop);

		// add button to clear board - set all cells to black/dead
		JButton clear = new JButton("CLEAR");
		ActionListener clearListen = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (timer.isRunning()) {
					timer.stop();
				}
				for (JButton i : cellsMap.keySet()) {
					cellsMap.put(i, Color.BLACK);
				}
				printNextGeneration();
			}
		};
		clear.addActionListener(clearListen);
		options.add(clear);

		// add button that exits program
		JButton exit = new JButton("EXIT");
		ActionListener exitListen = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		};
		exit.addActionListener(exitListen);
		options.add(exit);
	}

	private void createAndFillBoxes() {
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JButton box = (JButton) event.getSource();
				if (box.getBackground().equals(Color.BLACK)) {
					box.setBackground(Color.GREEN);
					cellsMap.put(box, Color.GREEN);
				} else {
					box.setBackground(Color.BLACK);
					cellsMap.put(box, Color.BLACK);
				}

			}
		};

		Random random = new Random();
		for (int i = 0; i < COLS; i++) {
			for (int j = 0; j < ROWS; j++) {
				JButton button = new JButton();
				cells[i][j] = button;
				container.add(button);
				button.addActionListener(listener);

				// set random 30% of cells to green
				int n = random.nextInt(100);
				if (n < 30) {
					button.setBackground(Color.GREEN);
					cellsMap.put(button, Color.GREEN);
				} else {
					button.setBackground(Color.BLACK);
					cellsMap.put(button, Color.BLACK);
				}
			}
		}
	}

	private void nextGeneration() {
		for (int i = 0; i < COLS; i++) {
			for (int j = 0; j < ROWS; j++) {
				// check how many neighbors of current cell are alive
				switch (getNumAliveNeighbors(i, j)) {
				case 0:
				case 1:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
					// if there are 0, 1 or more than 3 neighbors alive, the current cell dies
					cellsMap.put(cells[i][j], Color.BLACK);
					break;
				case 2:
					// if 2 neighbors are alive, the current cell remains the same
					break;
				case 3:
					// if 3 neighbors are alive, the current cell becomes alive
					cellsMap.put(cells[i][j], Color.GREEN);
					break;
				}
			}
		}
		printNextGeneration();
	}

	private int getNumAliveNeighbors(int i, int j) {
		int numAlive = 0;
		// go to each neighbor and check if alive
		for (int k = -1; k < 2; k++) {
			for (int m = -1; m < 2; m++) {
				if (k == 0 && m == 0) {
					// skip center box
				} else if (isAlive(i + k, j + m)) {
					numAlive++;
				}
			}
		}
		return numAlive;
	}

	private boolean isAlive(int i, int j) {
		try {
			return cells[i][j].getBackground().equals(Color.GREEN);
		} catch (Exception e) {
			return false;
		}
	}

	private void printNextGeneration() {
		for (int i = 0; i < COLS; i++) {
			for (int j = 0; j < ROWS; j++) {
				cells[i][j].setBackground(cellsMap.get(cells[i][j]));
			}
		}
	}

	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		new GameOfLife();
	}
}