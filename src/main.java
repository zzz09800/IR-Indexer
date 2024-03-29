import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

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

		for (String iter_string : fileList) {
			//System.out.println(iter_string);
			res.addAll(acerIndexer.createIndexFromPage(iter_string));
			//break;
		}

		fileList = runner.getFileList("AMG");
		for (String iter_string : fileList) {
			//System.out.println(iter_string);
			res.addAll(miscIndexer.createIndexFromPage(iter_string));
			//break;
		}

		fileList = runner.getFileList("Dell");
		for (String iter_string : fileList) {
			//System.out.println(iter_string);
			res.addAll(dellIndexer.createIndexFromPage(iter_string));
			//break;
		}

		fileList = runner.getFileList("Dell_work");
		for (String iter_string : fileList) {
			//System.out.println(iter_string);
			//if(iter_string.contains("inspiron-15-5566"))
			res.addAll(dellIndexer.createIndexFromPage_work(iter_string));
			//break;
		}

		int i = 0;
		for (SpecElement tmp : res) {
			//if(tmp.model.equals("Latitude 7280")){
				tmp.CPU_level=ranker.computeProcessorRank(res,tmp);
				tmp.graphic_level=ranker.computeGraphicRank(res,tmp);
				tmp.RAM_level=ranker.computeMemoryRank(res,tmp);
				tmp.screes_resolution_level=ranker.computeResolutionRank(res,tmp);
				tmp.price_level=ranker.computePriceRank(res,tmp);
			//}
		}
		/*for (SpecElement tmp : res) {
			System.out.println(tmp.brand);
			System.out.println(tmp.model);
			System.out.println(tmp.CPU_model);
			System.out.println(tmp.graphic_model);
			System.out.println(tmp.RAM_size + " GB " + tmp.RAM_type);
			System.out.printf("%4.1f", tmp.screen_size);
			System.out.println("\" " + tmp.screen_resolution_x + " x " + tmp.screen_resolution_y);
			System.out.println(tmp.hard_drive_info);
			System.out.println(tmp.price);
			System.out.println();
			i++;
		}
		System.out.println(i);*/

		Scanner clin = new Scanner(System.in);

		while(true){
			System.out.print("Query Input:");
			String inputQuery = clin.nextLine();
			QueryProcessor queryProcessor = new QueryProcessor(inputQuery);
			queryProcessor.parseQuery();
			ArrayList<SpecElement> filtered=queryProcessor.filteredSearch(res);

			for(i=0;i<10;i++)
			{
				SpecElement tmp = filtered.get(i);
				System.out.println(tmp.brand);
				System.out.println(tmp.model);
				System.out.println(tmp.CPU_model);
				System.out.println(tmp.graphic_model);
				System.out.println(tmp.RAM_size + " GB " + tmp.RAM_type);
				System.out.printf("%4.1f", tmp.screen_size);
				System.out.println("\" " + tmp.screen_resolution_x + " x " + tmp.screen_resolution_y);
				if(tmp.hard_drive_info!=null)
					System.out.println(tmp.hard_drive_info);
				System.out.println(tmp.price);
				System.out.println();
			}
		}
	}
}
