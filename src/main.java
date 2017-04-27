import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by andrew on 4/21/17.
 */
public class main {
	public static void main(String[] args) {
		JobRunner runner = new JobRunner();
		AcerIndexer acerIndexer = new AcerIndexer();
		MiscIndexer miscIndexer = new MiscIndexer();
		DellIndexer dellIndexer = new DellIndexer();
		Rankers ranker = new Rankers();

		HashSet<String> fileList = runner.getFileList("Acer");

		HashSet<SpecElement> res = new HashSet<SpecElement>();

		/*for (String iter_string : fileList) {
			//System.out.println(iter_string);
			res.addAll(acerIndexer.createIndexFromPage(iter_string));
			//break;
		}

		fileList = runner.getFileList("AMG");
		for (String iter_string : fileList) {
			//System.out.println(iter_string);
			res.addAll(miscIndexer.createIndexFromPage(iter_string));
			//break;
		}*/

		fileList = runner.getFileList("Dell");
		for (String iter_string : fileList) {
			//System.out.println(iter_string);
			res.addAll(dellIndexer.createIndexFromPage(iter_string));
			//break;
		}

		int i = 0;
		for (SpecElement tmp : res) {
			tmp.CPU_level=ranker.computeProcessorRank(res,tmp);
			tmp.graphic_level=ranker.computeGraphicRank(res,tmp);
			tmp.RAM_level=ranker.computeMemoryRank(res,tmp);
			tmp.screes_resolution_level=ranker.computeResolutionRank(res,tmp);
			tmp.price_level=ranker.computePriceRank(res,tmp);
		}

		QueryProcessor queryProcessor = new QueryProcessor("laptops with fast processor and under 1200");
		queryProcessor.parseQuery();
		ArrayList<SpecElement> filtered=queryProcessor.filteredSearch(res);

		for(i=0;i<40;i++)
		{
			SpecElement tmp = filtered.get(i);
			System.out.println(tmp.brand);
			System.out.println(tmp.model);
			System.out.println(tmp.CPU_model);
			System.out.println(tmp.graphic_model);
			System.out.println(tmp.RAM_size + " GB " + tmp.RAM_type);
			System.out.printf("%4.1f", tmp.screen_size);
			System.out.println("\" " + tmp.screen_resolution_x + " x " + tmp.screen_resolution_y);
			System.out.println(tmp.price);
			System.out.println();
		}
	}
}
