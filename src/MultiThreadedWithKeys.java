import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultiThreadedWithKeys {
	private static final int	THREADS	= 2;

	public static void main(String[] args) {
		ExecutorService ex;
		BufferedWriter out;
		
		
		
		try {
			out = new BufferedWriter(new FileWriter(new File("data" + File.separator + "summary.txt")));
			ex = Executors.newFixedThreadPool(THREADS);

			for (int n = 3; n < 8; n++) {
				for (int s = n - 1; s <= Math.round(Math.pow(2, n - 1)); s++) {
					for (int w = s+n-1; w <= Math.round(Math.pow(2, n)) - n - 1; w++) {
						ex.execute(new FindBandwidthThread(n, s, w, out));
					}
				}
			}

			try {
				ex.awaitTermination(1, TimeUnit.DAYS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			out.close();
		} catch (IOException e) {

		}
	}

	private static int choose(int n, int i) {
		return factorial(n)/factorial(i)/factorial(n-i);
	}

	private static int factorial(int n) {
		if(n<=1){
			return 1;
		}
		return n * factorial(n-1);
	}
}
