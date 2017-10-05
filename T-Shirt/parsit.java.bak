import java.lang.*;
import java.io.*;

public class parsit {
	public static void main (String[] argv) throws Exception {
		String s = "";
		StreamTokenizer st = new StreamTokenizer(new BufferedReader(new InputStreamReader(System.in)));
		st.resetSyntax();
		st.slashSlashComments(true);
		st.slashStarComments(true);
		st.eolIsSignificant(false);
		st.wordChars(33, 46);
		st.wordChars(48, 127);
		st.whitespaceChars(0, 32);
		while (st.nextToken() != st.TT_EOF) {
			s += st.sval + " ";
		}

		while (s.length() > 132) {
			System.out.println (s.substring(0, 132));
			s = s.substring (132);
		}
		System.out.println (s);
	}

}