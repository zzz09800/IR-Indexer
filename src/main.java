import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

import java.util.HashSet;

/**
 * Created by andrew on 4/21/17.
 */
public class main {
	public static void main(String[] args) {
		/*JobRunner runner = new JobRunner();
		AcerIndexer acerIndexer = new AcerIndexer();
		MiscIndexer miscIndexer = new MiscIndexer();
		Rankers ranker = new Rankers();

		HashSet<String> fileList = runner.getFileList("Acer");

		HashSet<SpecElement> res = new HashSet<SpecElement>();

		for (String iter_string : fileList) {
			System.out.println(iter_string);
			res.addAll(acerIndexer.createIndexFromPage(iter_string));
			//break;
		}

		fileList = runner.getFileList("AMG");
		for (String iter_string : fileList) {
			System.out.println(iter_string);
			res.addAll(miscIndexer.createIndexFromPage(iter_string));
			//break;
		}

		int i = 0;
		for (SpecElement tmp : res) {
			System.out.println(tmp.brand);
			System.out.println(tmp.model);
			System.out.println(tmp.CPU_model);
			System.out.println(tmp.graphic_model);
			System.out.println(tmp.RAM_size + " GB " + tmp.RAM_type);
			System.out.printf("%4.1f", tmp.screen_size);
			System.out.println("\" " + tmp.screen_resolution_x + " x " + tmp.screen_resolution_y);
			System.out.println(tmp.price);
			System.out.println();
			i++;
			ranker.computeProcessorRank(res,tmp);
			ranker.computeGraphicRank(res,tmp);
			ranker.computeMemoryRank(res,tmp);
			ranker.computeResolutionRank(res,tmp);
			ranker.computePriceRank(res,tmp);
		}

		System.out.println(i);*/

		QueryProcessor queryProcessor = new QueryProcessor("Laptops for gaming.");
		queryProcessor.parseQuery();
	}
}
