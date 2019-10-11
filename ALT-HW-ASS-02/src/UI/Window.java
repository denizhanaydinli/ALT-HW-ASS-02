package UI;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.DebugGraphics;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Window extends JFrame implements MouseListener, MouseMotionListener {
	
	private static final long serialVersionUID = 1L;
	private Canvas canvas;
	// Window title // TODO : display window title if it is undecorated
	private String title = "";
	// Default window width and height
	private int WIDTH = 500;
	private int HEIGHT = 500;
	// Drawable list to render and update
	private ArrayList<Drawable> drawables;
	
	private Canvas.RenderCallback renderCallback;
	
	private Button.OnClickListener buttonOnClickListener;
	private ArrayList<Button> buttons;
	private ArrayList<Track> tracks;

	
	private int EXIT_ID = 0, ADD_TRACK_ID = 1;
	
	private int fps = 0;
	private String fps_displayed = "";

	private InputText minAddressField, maxAddressField;

	/**Initialize window with title, width and height
	 * 
	 * @param title
	 * Window title that will be displayed on screen
	 * @param WIDTH
	 * Width of the window
	 * @param HEIGHT
	 * Height of the window
	 */
	public Window(String title, int WIDTH, int HEIGHT) {
		this.title = title; // set title of window
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
		
		initTools();
		initUI();
	}
	
	public Window(int WIDTH, int HEIGHT) {
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
		
		initTools();
		initUI();
	}
	
	public Window() {
		initTools();
		initUI();
	}
	
	// set window attributes
	private void initUI() {
		super.setContentPane(canvas); // add custom canvas to window
		//super.setResizable(false); // make window not resizeable
		super.setTitle(this.title); // TODO : change title and exit operation
		super.setUndecorated(true);
		
		minAddressField = new InputText(" Min Address");
		minAddressField.setPreferredSize(new Dimension(100, 20));
		maxAddressField = new InputText(" Max Address");
		maxAddressField.setPreferredSize(new Dimension(100, 20));
		
		super.add(minAddressField);
		super.add(maxAddressField);

		pack();
		super.setLocationRelativeTo(null); // center to screen
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		super.addMouseListener(this); // add mouse event listener
		super.addMouseMotionListener(this);
		super.setVisible(true);
		
		super.requestFocusInWindow();
		super.requestFocusInWindow();

		
		EXIT_ID = buttons.size()+1;
		buttons.add(new Button(this.WIDTH - 55, 5, 51, 22).setText("X").setFont(new Font("Verdana", Font.BOLD, 16)).setID(EXIT_ID).setColor(Color.DARK_GRAY, Color.GRAY).setOnClickListener(buttonOnClickListener));

		ADD_TRACK_ID = buttons.size()+1;
		buttons.add(new Button(5, 5, 50, 22).setText("ADD").setFont(new Font("Verdana", Font.BOLD, 16)).setID(ADD_TRACK_ID).setColor(Color.DARK_GRAY, Color.GRAY).setOnClickListener(buttonOnClickListener));
	
	    //for (int i = 0; i < 5; i++) {
		//	for (int j = 0; j < 6; j++) {
				//tracks.add(new Track(7 + i*63, 75 + j * 63, 55, 55).setText("AB001").setFont(new Font("Verdana", Font.BOLD, 12)).setID(0).setColor(new Color(129, 245, 66), Color.GRAY));
		//	}
		//}
		
		for (int i = 0; i < maxXLoc; i++) {
			for (int j = 0; j < maxYLoc; j++) {
				locs.add(new Point(i, j));
			}
		}
	}
	
	// initialize lists and other tools
	private void initTools() {
		buttons = new ArrayList<Button>();
		buttonOnClickListener = new Button.OnClickListener() {
			
			@Override
			public void onClick(int id) {
				if(id == EXIT_ID) {
					System.out.println("EXIT");
					dispatchEvent(new WindowEvent(getFrames()[0], WindowEvent.WINDOW_CLOSING));
				} else if(id == ADD_TRACK_ID) {
					addTrack();
				}
			}
		};
		
		tracks = new ArrayList<Track>();
		
		drawables = new ArrayList<Drawable>();
		renderCallback = new Canvas.RenderCallback() {
			
			@Override
			public void render(Graphics g) {
				fps++;
				renderGraphics(g);
			}
		};
		
		canvas = new Canvas(this.WIDTH, this.HEIGHT);
		canvas.setRenderCallback(renderCallback);
		
		// This is a fps measurement thread, every second it resets to 0
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					fps_displayed = fps + "";
					fps = 0;
					repaint();
					//System.out.println("FPS RESET");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	private ArrayList<Point> locs = new ArrayList<Point>();
	private int maxXLoc = 5, maxYLoc = 6;
	private int placedTrackNumber = 0;

	private void addTrack() {
		int min = minAddressField.getText().length() > 0 ? Integer.parseInt(minAddressField.getText(), 16) : -1; 
		int max = maxAddressField.getText().length() > 0 ? Integer.parseInt(maxAddressField.getText(), 16) : -1;
		//System.out.println(min + " " + max);
		if(min == -1) {
			minAddressField.setText(" Fill");
			return;
		} else if(max == -1) {
			maxAddressField.setText(" Fill");
			return;
		}
		//System.out.println(minAddressField.getText() + " " + maxAddressField.getText());
		//System.out.println(Integer.parseInt(minAddressField.getText(), 16) + " " + Integer.parseInt(maxAddressField.getText(), 16));
		if(locs.size() == 0) {
			System.out.println("no place is available");
			return;
		}
		int ran = new Random().nextInt(locs.size());
		Point pos = locs.get(ran);
		locs.remove(ran);
System.out.println(pos.x + " " + pos.y);
		if(placedTrackNumber + min > max) {
			tracks.add(new Track(7 + pos.x*63, 75 + pos.y * 63, 55, 55).setText("").setFont(new Font("Verdana", Font.BOLD, 12)).setID(0).setColor(new Color(229, 45, 66), Color.GRAY));
		}else {
			tracks.add(new Track(7 + pos.x*63, 75 + pos.y * 63, 55, 55).setText(Integer.toHexString(min + placedTrackNumber).toUpperCase()).setFont(new Font("Verdana", Font.BOLD, 12)).setID(0).setColor(new Color(129, 245, 66), Color.GRAY));
		}
		placedTrackNumber++;
	}
	
	/**Add a drawable to the window draw queue
	 */
	public void addDrawable(Drawable drawable) {
		drawables.add(drawable);
	}
	
	private void renderGraphics(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, this.WIDTH-1, this.HEIGHT-1);
		g.setColor(Color.DARK_GRAY);
		((Graphics2D) g).setStroke(new BasicStroke(5));
		g.drawRect(0, 0, this.WIDTH-1, this.HEIGHT-1);
		
		g.drawString(fps_displayed + " FPS", 10, this.HEIGHT - 10);
		
		g.setColor(new Color(66, 135, 245));
		g.drawString("ALT-HW-ASS-02 _ DENÝZHAN AYDINLI", 100, this.HEIGHT - 10);
		
		for (Button button : buttons) {
			button.render(g);
		}
		
        for (Drawable drawable : drawables) {
			drawable.render(g);
		}
        
        for (Drawable drawable : tracks) {
			drawable.render(g);
		}
	}
	
	private void update(int action, int mouse_x, int mouse_y) {
		// Update screen elements according to action and mouse positions
		for (Drawable drawable : drawables) {
			drawable.tick(action, mouse_x, mouse_y);
		}
		
		for (Button button : buttons) {
			button.tick(action, mouse_x, mouse_y);
		}
		
		for (Track track : tracks) {
			track.tick(action, mouse_x, mouse_y);
		}
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * This will be called when a mouse button is down
	 */
	@Override
	public void mousePressed(MouseEvent event) {
		// System.out.println(event.getX() + " " + event.getY());
		// Update every drawable that is in the list
		update(Drawable.ACTION_PRESS, event.getX(), event.getY());
		// repaint screen with mouse click since we dont need to repaint every so often
		super.repaint();
	}


	/**
	 * This will be called when a mouse button is up
	 */
	@Override
	public void mouseReleased(MouseEvent event) {
		update(Drawable.ACTION_RELEASE, event.getX(), event.getY());
		// repaint screen with mouse click since we dont need to repaint every so often
		super.repaint();
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * This will be called when mouse pointer moved on screen
	 */
	@Override
	public void mouseMoved(MouseEvent event) {
		// Render and update hover colors when moved and collided
		update(Drawable.ACTION_MOVE, event.getX(), event.getY());
		super.repaint();
	}

}
