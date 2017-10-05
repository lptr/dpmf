
import java.lang.reflect.*;
import java.lang.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;


class myCanvas extends Canvas implements KeyListener {

	String outString;
	LinkedList history;
	int historyPos;
	int linePos;
	String[] regExp;

	myCanvas(Object fuckyou) {
		outString = "DPMF";
		history = new LinkedList();
		history.add(outString);
		historyPos = 0; // history.size() - 1;
		linePos = outString.length();
		setSize(400, 250);
		addKeyListener(this);
// initialize autocomplete		
		regExp = null;
		regExp = addStringArrayItem(regExp,"/startgame %c");
		regExp = addStringArrayItem(regExp,"/highscore");
		regExp = addStringArrayItem(regExp,"/exit");
		regExp = addStringArrayItem(regExp,"/exithighscore");
		regExp = addStringArrayItem(regExp,"/nextlevel");
		regExp = addStringArrayItem(regExp,"/step");
		regExp = addStringArrayItem(regExp,"/exitgame %c");
		regExp = addStringArrayItem(regExp,"/setdirection [up|right|down|left]");
		regExp = addStringArrayItem(regExp,"/putbomb");
		regExp = addStringArrayItem(regExp,"/remark {%c}");
		regExp = addStringArrayItem(regExp,"/info [all|pacman|monster|bomb|crystal|bonus]");
		regExp = addStringArrayItem(regExp,"/show [maze|info]");
		regExp = addStringArrayItem(regExp,"/hack load %c");
		regExp = addStringArrayItem(regExp,"/hack score %n");
		regExp = addStringArrayItem(regExp,"/hack lives %n");
		regExp = addStringArrayItem(regExp,"/hack time %n");
		regExp = addStringArrayItem(regExp,"/hack pacman kill");
		regExp = addStringArrayItem(regExp,"/hack pacman set %n %n [up|right|down|left] %n");
		regExp = addStringArrayItem(regExp,"/hack monster create [clever|dumb] %n %n [up|right|down|left]");
		regExp = addStringArrayItem(regExp,"/hack monster %n [kill|reborn|remove]");
		regExp = addStringArrayItem(regExp,"/hack monster %n set %n %n [up|right|down|left]");
		regExp = addStringArrayItem(regExp,"/hack monster %n [auto|manual]");
		regExp = addStringArrayItem(regExp,"/hack monster %n [up|right|down|left]");
		regExp = addStringArrayItem(regExp,"/hack bomb create %n %n %n");
		regExp = addStringArrayItem(regExp,"/hack bomb %n remove");
		regExp = addStringArrayItem(regExp,"/hack bomb %n set %n %n %n");
		regExp = addStringArrayItem(regExp,"/hack bomb %n [activate|deactivate]");
		regExp = addStringArrayItem(regExp,"/hack bonus [auto|manual]");
		regExp = addStringArrayItem(regExp,"/hack bonus create [time|score|bomb] %n %n %n %n");
		regExp = addStringArrayItem(regExp,"/hack bonus %n remove");
		regExp = addStringArrayItem(regExp,"/hack bonus %n set %n %n %n %n");
		regExp = addStringArrayItem(regExp,"/hack crystal create %n %n");
		regExp = addStringArrayItem(regExp,"/hack crystal %n remove");
		regExp = addStringArrayItem(regExp,"/hack wall [on|off] %n %n [up|right|down|left] [single|both]");
	}

	public void paint(Graphics g) {
		update(g);
	}

	public void update(Graphics g) {
		BufferedImage img = (BufferedImage)createImage(400 ,250);
		Graphics g2 = img.createGraphics();
		g2.clearRect(0,0,400,250);
		for(int i=historyPos-5;i<historyPos;i++)
			if (i>=0 && i < history.size())
				g2.drawString((String)history.get(i),10,110+(i-historyPos)*15-1);
		int fontWidth = (int)g2.getFontMetrics().getStringBounds(outString.substring(0,linePos),g2).getWidth();
		g2.drawString(outString.substring(0,linePos),10,110);
		g2.drawLine(fontWidth+10,110,fontWidth+15,110);
		g2.drawString(outString.substring(linePos),fontWidth + 17,110);
		g2.drawRect(7,97,380,16);
		for(int i=historyPos+1;i<=historyPos+5;i++)
			if (i>=0 && i < history.size())
				g2.drawString((String)history.get(i),10,110+(i-historyPos)*15+1);

		g.drawImage(img,0,0,this);
	}

	private String[] addStringArrayItem(String[] a,String b) {
		if (a==null) {
			String[] temp = new String[1];
			temp[0]=b;
			return temp;
		} else {
			String[] temp = new String[a.length+(b==null?0:1)];
			for (int i=0; i<a.length; i++)
				temp[i]=a[i];
			if (b!=null)
				temp[a.length]=b;
			return temp;
		}
	}

	private static String commonPart(String[] a) {
		if (a==null)
			return "";
		else if (a.length==0)
			return "";
		else {
			int len=a[0].length();
			int counter=0;
			for(counter=0; counter<a.length; counter++) {
				if (a[counter].length()<len) len = a[counter].length();
				for (int i=0; i<len; i++)
					if (a[0].charAt(i)!=a[counter].charAt(i)) len=i;
			}
			return a[0].substring(0,len);
		}
	}

	String completeAnswer(String toFind) {
/*		if (toFind.length()==0) {
			if (set.indexOf('|')==-1) return set;
			else return set.substring(0,set.indexOf('|'));
		}
*/
		String[] tempList = null;
		for(int i=0; i<regExp.length; i++) {
			String temp = ourParser.completeString(toFind,regExp[i]);
			if (!temp.equals(""))
				tempList = addStringArrayItem(tempList,temp);
		}
		return toFind + commonPart(tempList);
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getModifiers())
		{
			case InputEvent.CTRL_MASK :
				int newPos = linePos;
				switch (e.getKeyCode()) {
					case KeyEvent.VK_LEFT:
						try {
							while (outString.charAt(newPos-1)==' ')
								newPos--;
							while (outString.charAt(newPos-1)!=' ')
								newPos--;
							linePos = newPos-1;
						} catch (Exception ex) {
							linePos = 0;
						}
						update(getGraphics());
						break;
					case KeyEvent.VK_RIGHT:
						try {
							while (outString.charAt(newPos)==' ')
								newPos++;
							while (outString.charAt(newPos)!=' ')
								newPos++;
							linePos = newPos;
						} catch (Exception ex) {
							linePos = outString.length();
						}
						update(getGraphics());
						break;
				}
				break;		
			default :
				switch (e.getKeyCode()) {
					case KeyEvent.VK_UP :
						if (historyPos>0) {
							if (historyPos == history.size()-1) {
								history.removeLast();
								history.add(outString);
							}
							historyPos--;
							outString = (String)history.get(historyPos);
							linePos = outString.length();
							update(getGraphics());
						}
						break;
					case KeyEvent.VK_DOWN :
						if (historyPos < history.size()-1)
						{
							historyPos++;
							outString = (String)history.get(historyPos);
							linePos = outString.length();
							update(getGraphics());
						}
						break;
					case KeyEvent.VK_LEFT :
						if (linePos > 0) {
							linePos--;
							update(getGraphics());
						}
		
							break;
					case KeyEvent.VK_RIGHT :
						if (linePos < outString.length()) {
							linePos++;
							update(getGraphics());
						}
						break;
					case KeyEvent.VK_DELETE : //delete
						if (linePos<outString.length()) {
							historyPos = history.size()-1;
							outString = outString.substring(0,linePos) + outString.substring(linePos+1);
							update(getGraphics());
						}
						break;
					case KeyEvent.VK_HOME :
						linePos = 0;
						update(getGraphics());
						break;
					case KeyEvent.VK_END :
						linePos = outString.length();
						update(getGraphics());
						break;
				}
		}
	}
	public void keyReleased(KeyEvent e) {
	}
	public void keyTyped(KeyEvent e) {
		char ch = e.getKeyChar();
		historyPos = history.size()-1;
		switch (ch) {
			case '\n' : //enter
				if (!outString.equals(""))
				{
					history.removeLast();
					history.remove(outString);
					history.add(outString);
					System.out.println(outString);
					outString = "";
					linePos=0;
					history.add(outString);
					historyPos = history.size() - 1;
				}
				break;
			case '\b' : //backspace
				if (linePos>0) {
					outString = outString.substring(0,linePos-1) + outString.substring(linePos);
					linePos--;
				}
				break;
			case '\t' : //tab
				String temp = completeAnswer(outString.substring(0,linePos));
				if (!temp.equals("")) {
					outString = temp + outString.substring(linePos);
					linePos = temp.length();
				}
//				System.out.print("TAB        ");
				break;
			default :
				ch = Character.toLowerCase(ch);
				if ((ch>='a' && ch<='z') || (ch>='0' && ch<='9') || ch==' ' || ch=='.' || ch=='/') {
					outString = outString.substring(0,linePos) + ch + outString.substring(linePos);
					linePos++;
				}
//				System.out.print("notspecial ");
		}
		update(getGraphics());
//		System.out.println(e.getKeyCode() + " '" + e.getKeyChar() + "'" + e.isActionKey());
	}
}


public class protoinput extends Object {

	protoinput() {
		Frame f = new Frame("Input Console");
		myCanvas c = new myCanvas(this);
		f.setSize(420, 70);
		f.add(c);
		f.pack();
		f.setVisible(true);
		while (true)
		{
		}
	}
		
	public static void main (String args[]) {
		new protoinput();
//		System.exit(0);
	}
}
/*
/startgame
/highscore
/exit
/exithighscore
/nextlevel
/step
/exitgame
/setdirection [up|right|down|left]
/putbomb
/remark
/info
/show
/hack load
/hack score
/hack lives
/hack time
/hack pacman kill
/hack pacman set
/hack monster create
/hack monster
/hack bomb create
/hack bomb
/hack bonus
/hack bonus create
/hack crystal create
/hack crystal
/hack wall
*/
