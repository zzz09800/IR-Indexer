import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.parser.dvparser.DVModelReranker;
import edu.stanford.nlp.process.Stemmer;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by andrew on 4/23/17.
 */
public class QueryProcessor {
	String inputQuery;
	float target_Processor_range_min;
	float target_Processor_range_max;
	float target_Graphics_range_min;
	float target_Graphics_range_max;
	float target_Memory_rank_range_min;
	float target_Memory_rank_range_max;
	float target_Resolution_range_min;
	float target_Resolution_range_max;
	float target_Price_range_min;
	float target_Price_range_max;
	float taeget_Screes_size_min;
	float taeget_Screes_size_max;

	public QueryProcessor(String query)
	{
		this.inputQuery=query;
		this.target_Processor_range_min=0;
		this.target_Processor_range_max=6;
		this.target_Graphics_range_min=0;
		this.target_Graphics_range_max=6;
		this.target_Memory_rank_range_min=0;
		this.target_Memory_rank_range_max=6;
		this.target_Resolution_range_min=0;
		this.target_Resolution_range_max=6;
		this.target_Price_range_min=0;
		this.target_Price_range_max=99999;
		this.taeget_Screes_size_min=0;
		this.taeget_Screes_size_max=9999;
	}


	public void parseQuery()
	{
		//Built-in purpose-driven matches
		Stemmer stemmer = new Stemmer();
		String stemmedQuery=stemmer.stem(inputQuery).toLowerCase();
		//Rough Estimates
		if(stemmedQuery.contains("game"))
		{
			this.target_Processor_range_min=4;
			this.target_Graphics_range_min=4;
			this.target_Memory_rank_range_min=3;
		}
		if((stemmedQuery.contains("photo")||stemmedQuery.contains("video"))&&stemmedQuery.contains("edit"))
		{
			this.target_Processor_range_min=4;
			this.target_Graphics_range_min=4;
			this.target_Memory_rank_range_min=3;
			this.target_Resolution_range_min=1600;
			this.taeget_Screes_size_min=13;
		}
		if(stemmedQuery.contains("expensive"))
		{
			this.target_Price_range_min=3;
		}
		else if(stemmedQuery.contains("cheap"))
		{
			this.target_Price_range_max=2;
		}

		//Specific Params
		Document doc = new Document(stemmedQuery);
		String[] tmp=stemmedQuery.replaceAll("\\.","").split(" ");

		ArrayList<QueryTokens> queryTokens= new ArrayList<QueryTokens>();
		int i;

		for(i=0;i<tmp.length;i++)
		{
			QueryTokens constructToken = new QueryTokens();
			constructToken.position=i;
			constructToken.content=tmp[i];
			constructToken.posTag=doc.sentence(0).posTag(i);
			queryTokens.add(constructToken);
		}

	}
}
