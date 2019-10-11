package UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

public class Track extends Drawable{
	
	protected int id = 0;
	
	private String text = "";
	private int text_x = 0, text_y = 0;
	
	private Font font;
	
	private Color defaultColor = Color.GRAY;
	private Color hoverColor = Color.DARK_GRAY;
	
	private boolean hovered = false;
	/**
	 * @param x
	 * x position relative to screen in pixels
	 * @param y
	 * y position relative to screen in pixels
	 * @param width
	 * width of the button in pixels
	 * @param height
	 * height of the button in pixels in pixels
	 */
	public Track(int x, int y, int width, int height) {
		super(x, y, width, height);
	}
	
	/**Set the text that will be displayed on top of the button
	 * 
	 * @param text
	 * Displayed text on screen
	 * @return
	 * Itself
	 */
	public Track setText(String text) {
		this.text = text;
		return this;
	}

	/**Set the font of the displayed text
	 * 
	 * @param font
	 * Font of the displayed text
	 * @return
	 */
	public Track setFont(Font font) {
		this.font = font;
		return this;
	}
	
	/**Set the default and hover colors of the button
	 * 
	 * @param color
	 * Default color when not hovered with mouse
	 * @param hoverColor
	 * Default color when hovered over the button
	 * @return
	 */
	public Track setColor(Color color, Color hoverColor) {
		this.defaultColor = color;
		this.hoverColor = hoverColor;
		return this;
	}
	
	/**Set the visibility of the button and child elements
	 * @param state
	 * If true button will be displayed
	 * If false button will not be displayed
	 */
	public Track setVisibility(boolean state) {
		this.visible = state;
		this.hovered = false;
		return this;
	}
	
	/**ID is required for order in the button hierarchy
	 * @param state
	 * ID of the button
	 */
	public Track setID(int id) {
		this.id = id;
		return this;
	}
	
	/**Get the ID of the button
	 * 
	 * @return
	 * Id of the button
	 */
	public int getId() {
		return id;
	}
	
	/**Add button info when hovered over with a mouse
	 * 
	 * @param hint
	 * Display text of the hint
	 */
	private void centerText(Graphics g) {
		Rectangle2D bounds = g.getFontMetrics().getStringBounds(text, g);
		
		this.text_x = super.pos_x + (super.width - (int) bounds.getWidth()) / 2;
		this.text_y = super.pos_y + (int) bounds.getHeight();
	}
	
	@Override
	public void render(Graphics g) {
		super.render(g);
		if(!super.visible) {
			return;
		}
		if(hovered) {
			g.setColor(hoverColor);
		}else {
			g.setColor(defaultColor);
		}
        g.fillOval(super.pos_x, super.pos_y + super.vert_anim_padding, super.width, super.height);
        
        g.setFont(this.font);
		g.setColor(Color.DARK_GRAY);
		
		
		centerText(g);
		g.drawString(text, text_x, text_y + super.vert_anim_padding + 15);
	}
	
	@Override
	public void tick(int action, int mouse_x, int mouse_y) {
		if(!visible) {
			return;
		}
		super.collide = false;
		if((mouse_x > super.pos_x && mouse_x < super.pos_x + super.width) && (mouse_y > super.pos_y && mouse_y < super.pos_y + super.height)) {
			super.collide = true;
			super.vert_anim_padding = -2;
		}
		if(super.collide) {
			switch (action) {
			case Drawable.ACTION_MOVE:
				hovered = true;
				break;

			case Drawable.ACTION_PRESS:
				hovered = false;
				break;
			case Drawable.ACTION_RELEASE:
				hovered = true;
				break;
			}
		}else {
			hovered = false;
		}
	}
}
