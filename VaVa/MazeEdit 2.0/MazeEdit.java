import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

class MyEditBox extends JPanel {

	JTextField text;
	JButton up;
	JButton down;
	
	int value;

	protected void initValue(int value) {
		text.setText((new Integer(value)).toString());
		this.value=value;
	}

	public boolean setValue(String value) {
		if (isValid(value))
			return setValue((new Integer(value)).intValue());
		else 
			return false;
	}

	public boolean setValue(int value) {
		if (isValid(value)) {
			text.setText((new Integer(value)).toString());
			this.value=value;
			newValue();
		}
		return true;
	}

	public int getValue() {
		return value;
	}

	boolean isValid(int value) {
		return value>=0;
	}

	void newValue() {
	}
	
	boolean isValid(String value) {
		Integer temp = new Integer(value);
		if (!value.equals(temp.toString()))
			return false;
		return isValid(temp.intValue());
	}
	
	MyEditBox(String name) {
		this(name,0);
	}

	MyEditBox(String name, int startvalue) {
		setLayout(new GridLayout(1,3));
		add(new JLabel(name));
		add(text = new JTextField());
			text.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent e) {
												if (!setValue(text.getText()))
													setValue(value);
											}
										});
		JPanel Butt = new JPanel();
			Butt.setLayout(new GridLayout(2,1));
			Butt.add(up = new JButton("up"));
				up.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent e) {
												setValue(getValue()+1);
											}
										});
			Butt.add(down = new JButton("dn"));
				down.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent e) {
												setValue(getValue()-1);
											}
										});
		add(Butt);
//		value = startvalue;
		initValue(startvalue);
	}
}

class ME_MenuBar extends JPanel {
	MazeEdit main;
	
	ME_MenuBar(MazeEdit newmain) {
		main = newmain;
		setLayout(new GridLayout(6,1));
		add(new MyEditBox("Time",main.maze.getTime()));
		add(new MyEditBox("SizeX",main.maze.getSizeX()) {
			boolean isValid(int value) {
				return value>=3;
			}
			void newValue() {
				main.maze.setSizeX(value);
			}
		});
		add(new MyEditBox("SizeY",main.maze.getSizeY()) {
			boolean isValid(int value) {
				return value>=3;
			}
			void newValue() {
				main.maze.setSizeY(value);
			}
		});
		add(new MyEditBox("Probab",main.maze.getProbab()));
//		add(new ME_Buttons());
		add(new JPanel());
//		add(new ME_Table());)
		add(new JPanel());
	}
}

class MazeView extends JComponent implements MouseListener, MouseMotionListener {
	MazeEdit main;

	MazeView(MazeEdit main) {
		this.main = main;
		addMouseListener(this);
		addMouseMotionListener(this);
	//	setSize(100,100);
	}

	private final static double FAT = 0.15;
	private final static double ANTIFAT = 1 - FAT;

	final Polygon upwall = new Polygon(	new int[] {	0,
													MazeEdit.CELLSIZE-1,
													new Double(MazeEdit.CELLSIZE*ANTIFAT).intValue(),
													new Double(MazeEdit.CELLSIZE*FAT).intValue()},
										new int[] {	0,
													0,
													new Double(MazeEdit.CELLSIZE*FAT).intValue(),
													new Double(MazeEdit.CELLSIZE*FAT).intValue()},
										4);
	
	final Polygon downwall = new Polygon(	new int[] {	0,
														MazeEdit.CELLSIZE-1,
														new Double(MazeEdit.CELLSIZE*ANTIFAT).intValue(),
														new Double(MazeEdit.CELLSIZE*FAT).intValue()},
											new int[] {	MazeEdit.CELLSIZE-1,
														MazeEdit.CELLSIZE-1,
														new Double(MazeEdit.CELLSIZE*ANTIFAT).intValue(),
														new Double(MazeEdit.CELLSIZE*ANTIFAT).intValue()},
											4);
	
	final Polygon leftwall = new Polygon(	new int[] {	0,
														0,
														new Double(MazeEdit.CELLSIZE*FAT).intValue(),
														new Double(MazeEdit.CELLSIZE*FAT).intValue()},
											new int[] {	0,
														MazeEdit.CELLSIZE-1,
														new Double(MazeEdit.CELLSIZE*ANTIFAT).intValue(),
														new Double(MazeEdit.CELLSIZE*FAT).intValue()},
											4);
	
	final Polygon rightwall = new Polygon(	new int[] {	MazeEdit.CELLSIZE-1,
														MazeEdit.CELLSIZE-1,
														new Double(MazeEdit.CELLSIZE*ANTIFAT).intValue(),
														new Double(MazeEdit.CELLSIZE*ANTIFAT).intValue()},
											new int[] {	0,
														MazeEdit.CELLSIZE-1,
														new Double(MazeEdit.CELLSIZE*ANTIFAT).intValue(),
														new Double(MazeEdit.CELLSIZE*FAT).intValue()},
											4);
	
	public void mouseClicked(MouseEvent e) {
		int fx = e.getX();
		int fy = e.getY();
		int mx = fx % MazeEdit.CELLSIZE;
		int my = fy % MazeEdit.CELLSIZE;
		int x = fx / MazeEdit.CELLSIZE;
		int y = fy / MazeEdit.CELLSIZE;
		FieldItem temp = main.maze.getFieldItem(x,y);
		if (temp!=null)	{
			System.out.println("("+x+";"+y+")"+"==>"+"("+mx+";"+my+")");
			if (upwall.contains(mx,my))
				temp.setWall(Coordinates.DIR_UP);
			if (downwall.contains(mx,my))
				temp.setWall(Coordinates.DIR_DOWN);
			if (leftwall.contains(mx,my))
				temp.setWall(Coordinates.DIR_LEFT);
			if (rightwall.contains(mx,my))
				temp.setWall(Coordinates.DIR_RIGHT);
			main.mazedraw.repaint();
		}
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}
	public void mouseDragged(MouseEvent e) { 
	}
	public void mouseMoved(MouseEvent e) {
	}

	
	private void drawCell(Graphics g, FieldItem field) {
		g.setColor(Color.white);
		g.fillRect(0,0,MazeEdit.CELLSIZE,MazeEdit.CELLSIZE);
		g.setColor(Color.gray);
		g.drawLine(0,0,0+MazeEdit.CELLSIZE,0);
		g.drawLine(0,0,0,0+MazeEdit.CELLSIZE);
		g.setColor(Color.black);
		if (field.isWall(Coordinates.DIR_UP))
			g.fillPolygon(upwall);
		if (field.isWall(Coordinates.DIR_DOWN))
			g.fillPolygon(downwall);
		if (field.isWall(Coordinates.DIR_LEFT))
			g.fillPolygon(leftwall);
		if (field.isWall(Coordinates.DIR_RIGHT))
			g.fillPolygon(rightwall);
	}
	
	public void paint(Graphics g) {
		BufferedImage img = (BufferedImage)createImage(MazeEdit.CELLSIZE*main.maze.getSizeX(), MazeEdit.CELLSIZE*main.maze.getSizeY());
		Graphics g2 = img.createGraphics();

		for(int i=0; i<main.maze.getSizeX(); i++)
			for(int j=0; j<main.maze.getSizeY(); j++) {
				g2.translate(i*MazeEdit.CELLSIZE,j*MazeEdit.CELLSIZE);
				drawCell(g2,main.maze.getFieldItem(i,j));
				g2.translate(-i*MazeEdit.CELLSIZE,-j*MazeEdit.CELLSIZE);
			}


//		g2.dispose();
		g.drawImage(img,0,0,this);
	}
}

class ME_Maze extends JPanel {
	MazeEdit main;
	MazeView mazeview;
	
	ME_Maze(MazeEdit main) {
		this.main = main;
		setLayout(new BorderLayout());
		add(mazeview = new MazeView(main),BorderLayout.CENTER);
	}
}

class ME_MainWindow extends JFrame {
	MazeEdit main;
	ME_Maze mazepanel;

	ME_MainWindow(MazeEdit main) {
		this.main = main;
		JPanel base = new JPanel();
		base.setLayout(new BorderLayout());

		base.add(new ME_MenuBar(main),BorderLayout.EAST);
		base.add(mazepanel = new ME_Maze(main),BorderLayout.CENTER);

		this.setContentPane(base);
	}
}

class MazeEdit extends Object {

	public final static int CELLSIZE = 32;

	MazeData maze;

	ME_MainWindow main;
	MazeView mazedraw;

	public void validate() {
		main.validate();
	}

	MazeEdit() {
		maze = new MazeData(this);
		main = new ME_MainWindow(this);
		mazedraw = main.mazepanel.mazeview;
		main.setSize(600,400);
		main.show();
	}

	public static void main(String[] args) {
		new MazeEdit();
	}
}