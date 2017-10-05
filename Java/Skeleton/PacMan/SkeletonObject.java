package PacMan;

import java.lang.*;
import java.io.*;

class SkeletonObject extends Object {
	static int objNumberCounter = 0;
	static int printBegin = 0;
	int objNumber;

	public SkeletonObject() {
		objNumber = objNumberCounter++;
		System.out.println (margine() + ">>> " + this + " created.");
	}
	
	public void finalize() throws Throwable {
		System.out.println (margine() + "<<< " + this + " has been overwritten.");
	}
	
	public String toString() {
		return getClass().getName() + "[" + objNumber + "]";
	}
	
	String margine() {
		String s = "";
		for (int i=0; i < printBegin; i++)
			s = s + ' ';
		return s;	
	}
	
	public void margineIn() {
		printBegin += 2;
	}

	public void margineOut() {
		printBegin -= 2;
	}

	String Answer() {
		char ch = 0;
		
		String s = "";
		
		while (true) {
			try {
				ch = (char)System.in.read();
			} catch (IOException e) {
			}
			
			if (ch == '\r')
				continue;

			if (ch == '\n')
				return s;
			s = s + ch;
		}
	}
	
	String Ask(String q) {
		System.out.print (margine() + "??? " + q + " ");
		return Answer();
	}
	
	void In(String func, String m) {
		Println("in " + GetName(func) + (m.equals("")?"":(" [" + m + "]")) + ".");
		margineIn();
	}
	
	void Leave(String func, String m) {
		margineOut();
		Println("leave " + GetName(func) + (m.equals("")?"":(" [" + m + "]")) + ".");
	}
	
	void Print(String s) {
		System.out.print(margine() + s);
	}

	void Println(String s) {
		System.out.println(margine() + s);
	}
	
	void Error (String s, Exception e) {
		System.err.println ("\n" + margine() + "!!! Error in " + this + (s.equals("")?"":(", problem: " + s)) + ".");
		e.printStackTrace(System.err);
	}
	
	String GetName (String s) {
		return this + "." + s + "()";
	}
}