import java.lang.*;


/**
 * parsolás tesztelo
 *
 * @author VaVa
 */
public class parsertest extends SkeletonObject {

	public parsertest() {
		String be  = Ask("Irj valamit, parsolom jol!!");
		parser myParser = new parser(be);
		while (!myParser.EOF())
			System.out.println(myParser.getNextToken());
		Ask ("press enter to exit.");
	}

	public static void main (String args[]) {
		new parsertest();
		System.exit(0);
	}
}
