import java.io.*;
public class HighScore
{
	public String line;
	public String ered;
	int s=0;
	int t=0;
	int h=0;
	int g=0;
    char chars[] = new char [16];
	char seged[] = new char [16];
	char seged2[] = new char [16];
	public String nevek[]= new String[10];
	public int eredmeny[]= new int[10];
	Proto prot;
	public HighScore(Proto p)
	{
		prot=p;
	}
	public void ShowHighScore() {
//	prot.vi.highscore.setEditable(false);
		for (int n=0;n<10 ;n++ )
		{
//			prot.vi.highscore.append(nevek[n]+eredmeny[n]+"\n");
		}
	}
	public void CheckHigh(int score,String nev)
	{
		if (score>eredmeny[9])
		{
			EnterHighScore(score,nev);
		}
	}

	public void EnterHighScore(int score,String nev)
	{
		h=9;
		for (int v=9;v>-1 ;v-- )
		{
			if (score>eredmeny[v])
			{
				h=v;
			}
		}
		t=0;
		System.out.println(h);
	
	if (h>0)
	{
		for (int g=8;g>(h-1) ;g--)
		{
	
		eredmeny[g+1]=eredmeny[g];
		nevek[g+1]=nevek[g];
		eredmeny[1]=eredmeny[0];
		nevek[1]=nevek[0];
		}
		eredmeny[h]=score;
		nevek[h]=nev;
	}
if (h==0)
	{
		for (int g=8;g>-1 ;g--)
		{
	
		eredmeny[g+1]=eredmeny[g];
		nevek[g+1]=nevek[g];
		}
		eredmeny[h]=score;
		nevek[h]=nev;
	}

	}

	public void LoadHighScore() {
		try
		{
		BufferedReader high=(new BufferedReader(new FileReader(new File("highscore.txt"))));
		for (int y=0;y<10 ;y++ )
			{
			s=0;
			line=high.readLine();
			chars = line.toCharArray();
			while (chars[s]!='#')
			{
				seged[s]=chars[s];
				s++;
			}
			s++;
			nevek[y]=new String(seged);
			s=17;
			t=0;
			while (chars[s]!='#')
			{
				seged2[t]=chars[s];
				t++;
				s++;
			}
			ered=String.valueOf(seged2,0,t);
			eredmeny[y]= Integer.parseInt(ered);
			}
			high.close();
		}
		catch (IOException e)
		{
		}
	}
public void SaveHighScore()
	{
		try
		{
		BufferedWriter high2=(new BufferedWriter(new FileWriter(new File("highscore.txt"))));	
		for (int y=0;y<10 ;y++ )
		{
		s=0;
					seged=nevek[y].toCharArray();
				high2.write(nevek[y]);
				high2.write('#');
				ered=String.valueOf(eredmeny[y]);
				high2.write(ered);
				high2.write('#');
				high2.write('\n');
			}
		high2.close();
		}
		catch (IOException e)
		{
		}
	}
}
