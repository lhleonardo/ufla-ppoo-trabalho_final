package br.ufla.simulator.simulation.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.ufla.simulator.actors.Actor;
import br.ufla.simulator.influencers.seasons.Season;
import br.ufla.simulator.simulation.Field;
import br.ufla.simulator.simulation.FieldStats;

/**
 * A graphical view of the simulation grid. The view displays a colored
 * rectangle for each location representing its contents. It uses a default
 * background color. Colors for each type of species can be defined using the
 * setColor method.
 * 
 * @author David J. Barnes and Michael Kolling
 * @version 2002-04-23
 */
@SuppressWarnings("serial")
public class SimulatorView extends JFrame {
	// Colors used for empty locations .
	private static final Color EMPTY_COLOR = Color.white;

	// Color used for objects that have no defined color.
	private static final Color UNKNOWN_COLOR = Color.gray;

	private final String STEP_PREFIX = "Step: ";
	private final String POPULATION_PREFIX = "Population: ";
	private JLabel stepLabel, population;
	private FieldView fieldView;

	// A map for storing colors for participants in the simulation
	private HashMap<Class<? extends Actor>, Color> colors;
	// A statistics object computing and storing simulation information
	private FieldStats stats;

	/**
	 * Create a view of the given width and height.
	 * 
	 * @param height - height of screen and dimension of the field
	 * @param width  - width of screen and dimension of the field
	 */
	public SimulatorView(int height, int width) {
		stats = new FieldStats();
		colors = new HashMap<>();

		setTitle("Fox and Rabbit Simulation");
		stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
		population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);

		setLocation(100, 50);

		fieldView = new FieldView(height, width);

		Container contents = getContentPane();
		contents.add(stepLabel, BorderLayout.NORTH);
		contents.add(fieldView, BorderLayout.CENTER);
		contents.add(population, BorderLayout.SOUTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);

	}

	/**
	 * Define a color to be used for a given class of animal.
	 */
	private void setColor(Class<? extends Actor> actorClass, Color color) {
		colors.put(actorClass, color);
	}

	/**
	 * Define a color to be used for a given class of animal.
	 */
	private Color getColor(Class<? extends Actor> actorClass) {
		Color col = (Color) colors.get(actorClass);
		if (col == null) {
			// no color defined for this class
			return UNKNOWN_COLOR;
		} else {
			return col;
		}
	}

	/**
	 * Show the current status of the field.
	 * 
	 * @param step          Which iteration step it is.
	 * @param stats         Status of the field to be represented.
	 * @param currentSeason - season who applied in the simulation
	 */
	public void showStatus(int step, Field field, Season currentSeason) {
		if (!isVisible())
			setVisible(true);

		stepLabel.setText(STEP_PREFIX + step);

		stats.reset();
		fieldView.preparePaint();

		for (int row = 0; row < field.getDepth(); row++) {
			for (int col = 0; col < field.getWidth(); col++) {
				Actor animal = field.getActorAt(row, col);
				if (animal != null) {
					if (!this.colors.containsKey(animal.getClass())) {
						this.setColor(animal.getClass(), animal.getColorRepresentation());
					}
					stats.incrementCount(animal.getClass());
					fieldView.drawMark(col, row, getColor(animal.getClass()));
				} else {
					fieldView.drawMark(col, row, EMPTY_COLOR);
				}
			}
		}
		stats.countFinished();
		stats.setCurrentSeason(currentSeason);

		population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
		fieldView.repaint();
	}

	/**
	 * Determine whether the simulation should continue to run.
	 * 
	 * @param field - Representation of the field simulation
	 * @return true If there is more than one species alive.
	 */
	public boolean isViable(Field field) {
		return stats.isViable(field);
	}

	/**
	 * Provide a graphical view of a rectangular field. This is a nested class (a
	 * class defined inside a class) which defines a custom component for the user
	 * interface. This component displays the field. This is rather advanced GUI
	 * stuff - you can ignore this for your project if you like.
	 */
	private class FieldView extends JPanel {
		private final int GRID_VIEW_SCALING_FACTOR = 6;

		private int gridWidth, gridHeight;
		private int xScale, yScale;
		Dimension size;
		private Graphics g;
		private Image fieldImage;

		/**
		 * Create a new FieldView component.
		 * 
		 * @param height - dimension of the component
		 * @param width  - dimension of the component
		 */
		public FieldView(int height, int width) {
			gridHeight = height;
			gridWidth = width;
			size = new Dimension(0, 0);
		}

		/**
		 * Tell the GUI manager how big we would like to be.
		 * 
		 * @return dimension configurated
		 */
		public Dimension getPreferredSize() {
			return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR, gridHeight * GRID_VIEW_SCALING_FACTOR);
		}

		/**
		 * Prepare for a new round of painting. Since the component may be resized,
		 * compute the scaling factor again.
		 */
		public void preparePaint() {
			if (!size.equals(getSize())) { // if the size has changed...
				size = getSize();
				fieldImage = fieldView.createImage(size.width, size.height);
				g = fieldImage.getGraphics();

				xScale = size.width / gridWidth;
				if (xScale < 1) {
					xScale = GRID_VIEW_SCALING_FACTOR;
				}
				yScale = size.height / gridHeight;
				if (yScale < 1) {
					yScale = GRID_VIEW_SCALING_FACTOR;
				}
			}
		}

		/**
		 * Paint on grid location on this field in a given color.
		 * 
		 * @param x     - position in screen
		 * @param y     - position in screen
		 * @param color - drawable color
		 */
		public void drawMark(int x, int y, Color color) {
			g.setColor(color);
			g.fillRect(x * xScale, y * yScale, xScale - 1, yScale - 1);
		}

		/**
		 * The field view component needs to be redisplayed. Copy the internal image to
		 * screen.
		 * 
		 * @param g - object that draw the image
		 */
		public void paintComponent(Graphics g) {
			if (fieldImage != null) {
				g.drawImage(fieldImage, 0, 0, null);
			}
		}
	}

}
