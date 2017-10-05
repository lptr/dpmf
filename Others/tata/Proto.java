//Source file: C:/jdk1.3/bin/Proto.java
import java.io.*;   // BufferedReader, InputStreamReader
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Proto
{
	public Viewer vi;
   public int StartX=4,StartY=4;
   public Game_Menu theGame_Menu;
   public int dir=0;
   public int scrn[] = new int[400];
   public int time=0;
   public byte[] inchar={0};

   public void Init()
{
try
	{
	BufferedReader proj=(new BufferedReader(new FileReader(new File("input.txt"))));	
	if (theGame_Menu==null)
	{
	theGame_Menu = new Game_Menu();
	}
	System.out.println(theGame_Menu);
	theGame_Menu.Start_game(this, proj);
	theGame_Menu.theGame.theLabyrinth.Load (proj);
	theGame_Menu.theGame.theLabyrinth.Szalak();
	proj.close();
	}
catch (IOException e)
	{
			System.out.println("Input file hiba!");
			System.exit(0);
			}
  }


 

   public static void main(String[] args)
   {
	Proto theSkeleton = new Proto(args[0], args[1]);

   }

   
   public Proto(String inf, String outf) 
   {
			Viewer viewer= new Viewer(this);
			vi=viewer;
			viewer.start();
			Init();
  }

   public static void p(String s) 
   {
   } 
	
   public static void q(String s) 
   {
	System.out.println(s);
   } 

   public static void r(char ch) 
   {
	System.out.print(ch);
   } 

	public int Get_Time()
	{
	return time;
	}
	public void Set_Time(int t)
	{
	time= t;
	}
}
	