import com.sun.org.apache.xpath.internal.operations.Bool;
import edu.stanford.nlp.process.Stemmer;
import edu.stanford.nlp.simple.Document;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

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
	float target_Screes_size_min;
	float target_Screes_size_max;
	int target_Price_sort_seq;
	float price_limit_min;
	float price_limit_max;
	boolean price_limit_effective=false;

	boolean special_graphic=false;
	boolean price_range_advised=false;
	boolean screen_size_range_advised=false;
	boolean scrren_size_limit_effective=false;

	String stemmedQuery;
	ArrayList<QueryTokens> queryTokens;

	public QueryProcessor(String query)
	{
		this.inputQuery=query.toLowerCase().replaceAll("\\.","").replaceAll("  "," ");
		this.target_Processor_range_min=0;
		this.target_Processor_range_max=999;
		this.target_Graphics_range_min=0;
		this.target_Graphics_range_max=999;
		this.target_Memory_rank_range_min=0;
		this.target_Memory_rank_range_max=999;
		this.target_Resolution_range_min=0;
		this.target_Resolution_range_max=999;

		this.target_Price_range_min=0;      //in rank
		this.target_Price_range_max=99999;

		this.target_Screes_size_min =0;
		this.target_Screes_size_max =9999;
		this.target_Price_sort_seq=1; //going upwards
		this.price_limit_min=0;
		this.price_limit_max=9999;

		this.stemmedQuery="";
		this.queryTokens = new ArrayList<QueryTokens>();
	}

	public void clearFilters()
	{
		this.target_Processor_range_min=0;
		this.target_Processor_range_max=999;
		this.target_Graphics_range_min=0;
		this.target_Graphics_range_max=999;
		this.target_Memory_rank_range_min=0;
		this.target_Memory_rank_range_max=999;
		this.target_Resolution_range_min=0;
		this.target_Resolution_range_max=999;

		this.target_Price_range_min=0;      //in rank
		this.target_Price_range_max=99999;

		this.target_Screes_size_min =0;
		this.target_Screes_size_max =9999;
		this.target_Price_sort_seq=1; //going upwards
		this.price_limit_min=0;
		this.price_limit_max=9999;

		screen_size_range_advised=false;
		scrren_size_limit_effective=false;

	}

	public void parseQuery()
	{
		//Built-in purpose-driven matches
		Stemmer stemmer = new Stemmer();
		this.stemmedQuery=stemmer.stem(inputQuery);
		//Rough Estimates
		if(stemmedQuery.contains("gam"))
		{
			this.target_Processor_range_min=4;
			this.target_Graphics_range_min=4;
			this.target_Memory_rank_range_min=3;
			this.special_graphic=false;
		}
		if(stemmedQuery.contains("video")&&stemmedQuery.contains("edit"))
		{
			this.target_Processor_range_min=4;
			this.target_Graphics_range_min=4;
			this.target_Memory_rank_range_min=3;
			this.target_Resolution_range_min=1920;
			this.target_Screes_size_min=14;
			this.special_graphic=true;
		}

		if(stemmedQuery.contains("photo")&&stemmedQuery.contains("edit"))
		{
			this.target_Processor_range_min=3;
			this.target_Graphics_range_min=3;
			this.target_Memory_rank_range_min=2;
			this.target_Resolution_range_min=1600;
			this.target_Screes_size_min=13;
			this.special_graphic=true;
		}

		if(stemmedQuery.contains("stud"))
		{
			this.target_Processor_range_min=2;
			this.target_Memory_rank_range_min=1;
			this.target_Screes_size_min=13;
			this.target_Screes_size_max=15;
			this.target_Price_range_max=2;
			this.price_range_advised=true;
			this.screen_size_range_advised=true;
			this.scrren_size_limit_effective=false;
		}

		//Specific Params
		Document doc = new Document(stemmedQuery);

		int i;

		for(i=0;i<doc.sentence(0).lemmas().size();i++)
		{
			QueryTokens constructToken = new QueryTokens();
			constructToken.position=i;
			constructToken.content=doc.sentence(0).lemma(i);
			constructToken.posTag=doc.sentence(0).posTag(i);
			this.queryTokens.add(constructToken);
		}

		JobRunner runner = new JobRunner();
		int rootNodeIndex = runner.wordLocate(this.queryTokens,"laptop");

		if(rootNodeIndex==-1){
			clearFilters();
			return;
		}

		HashSet<String> prefixAdjs = runner.extractJJs(this.queryTokens,rootNodeIndex);
		if(prefixAdjs.contains("expensive"))
		{
			this.target_Price_range_min=3;
			this.target_Price_sort_seq=1;
			this.price_range_advised=true;
		}
		else if(prefixAdjs.contains("cheap"))
		{
			this.target_Price_range_max=2;
			this.target_Price_sort_seq=-1;
			this.price_range_advised=true;
		}

		if(prefixAdjs.contains("fast"))
		{
			this.target_Processor_range_min=4;
			this.target_Memory_rank_range_min=2;
		}

		if(prefixAdjs.contains("small")){
			this.scrren_size_limit_effective=true;
			this.target_Screes_size_max=14;
		}else if(prefixAdjs.contains("large"))
		{
			this.scrren_size_limit_effective=true;
			this.target_Price_range_min=15;
		}


		//Extract Spec requirements
		int processorNodeIndex = runner.wordLocate(this.queryTokens,"cpu");
		if(processorNodeIndex==-1)
			processorNodeIndex = runner.wordLocate(this.queryTokens,"processor");
		if(processorNodeIndex!=-1){
			HashSet<String> processorAdjs = runner.extractJJs(this.queryTokens,processorNodeIndex);
			if(processorAdjs.contains("fast"))
			{
				this.target_Processor_range_min=4;
			}else if(processorAdjs.contains("slow"))
			{
				this.target_Processor_range_max=3;
				this.target_Price_range_max=3;
				this.price_range_advised=true;
			}else if(processorAdjs.contains("i7")){
				this.target_Processor_range_min=5;
			}else if(processorAdjs.contains("i5")){
				this.target_Processor_range_min=(float)4.0;
				this.target_Processor_range_max=(float)4.5;
			}else if(processorAdjs.contains("i3")){
				this.target_Processor_range_min=(float)3.0;
				this.target_Processor_range_max=(float)3.0;
			}
		}

		int memoryNodeIndex = runner.wordLocate(this.queryTokens,"memory");
		if(memoryNodeIndex==-1)
			memoryNodeIndex = runner.wordLocate(this.queryTokens,"ram");
		if(memoryNodeIndex!=-1) {
			HashSet<String> memoryAdjs = runner.extractJJs(this.queryTokens, memoryNodeIndex);
			if (memoryAdjs.contains("large")) {
				this.target_Processor_range_min = 3;
			} else if (memoryAdjs.contains("small")) {
				this.target_Processor_range_max = 2;
			}
		}

		int graphicNodeIndex = runner.wordLocate(this.queryTokens,"graphic");
		if(graphicNodeIndex==-1)
			graphicNodeIndex = runner.wordLocate(this.queryTokens,"gpu");
		if(graphicNodeIndex!=-1) {
			HashSet<String> graphicAdjs = runner.extractJJs(this.queryTokens, graphicNodeIndex);
			if (graphicAdjs.contains("fast")) {
				this.target_Graphics_range_min=3;
			}
		}

		int resolutionNodeIndex = runner.wordLocate(this.queryTokens,"resolution");
		if(resolutionNodeIndex!=-1) {
			HashSet<String> resolutionAdjs = runner.extractJJs(this.queryTokens, memoryNodeIndex);
			if (resolutionAdjs.contains("high")) {
				this.target_Resolution_range_min = 3;
			} else if (resolutionAdjs.contains("low")) {
				this.target_Resolution_range_min = 3;
			}
		}

		/*int screenSizeNodeIndex = runner.wordLocate(this.queryTokens,"screen");
		if(screenSizeNodeIndex!=-1) {
			HashSet<String> screenSizeNodeIndexAdjs = runner.extractJJs(this.queryTokens, screenSizeNodeIndex);

		}*/

		this.price_limit_effective=this.setPriceFilter();
	}

	public Boolean setPriceFilter()
	{
		int i;
		Boolean setfalg=false;
		for(i=0;i<this.queryTokens.size()-1;i++)
		{
			if(this.queryTokens.get(i).content.equals("higher")&&(this.queryTokens.get(i+1).posTag.equals("CD")||this.queryTokens.get(i+2).posTag.equals("CD")))
			{
				this.price_limit_min=Float.parseFloat(this.queryTokens.get(i+2).content);
				setfalg=true;
				return setfalg;
			}
			if(this.queryTokens.get(i).content.equals("above")&&this.queryTokens.get(i+1).posTag.equals("CD"))
			{
				this.price_limit_min=Float.parseFloat(this.queryTokens.get(i+1).content);
				setfalg=true;
				return setfalg;
			}

			if(this.queryTokens.get(i).content.equals("under")&&this.queryTokens.get(i+1).posTag.equals("CD"))
			{
				this.price_limit_max=Float.parseFloat(this.queryTokens.get(i+1).content);
				setfalg=true;
				return setfalg;
			}
			if(this.queryTokens.get(i).content.equals("below")&&this.queryTokens.get(i+1).posTag.equals("CD"))
			{
				this.price_limit_max=Float.parseFloat(this.queryTokens.get(i+1).content);
				setfalg=true;
				return setfalg;
			}

			if(this.queryTokens.get(i).content.equals("between")&&this.queryTokens.get(i+1).posTag.equals("CD")&&this.queryTokens.get(i+2).posTag.equals("IN")&&this.queryTokens.get(i+3).posTag.equals("CD"))
			{
				this.price_limit_max=Float.parseFloat(this.queryTokens.get(i+2).content);
				this.price_limit_min=Float.parseFloat(this.queryTokens.get(i+1).content);
				setfalg=true;
				return setfalg;
			}

			if(this.queryTokens.get(i).content.equals("around")&&this.queryTokens.get(i+1).posTag.equals("CD"))
			{
				this.price_limit_min=Float.parseFloat(this.queryTokens.get(i+1).content)-150;
				this.price_limit_max=Float.parseFloat(this.queryTokens.get(i+1).content)+150;
				setfalg=true;
				return setfalg;
			}

		}

		return setfalg;
	}

	public ArrayList<SpecElement> filteredSearch(HashSet<SpecElement> res)
	{
		ArrayList<SpecElement> searchResults = new ArrayList<SpecElement>();

		if(this.price_limit_effective){
			this.target_Price_range_min=0;
			this.target_Price_range_max=9999;
		}

		int score;
		for (SpecElement tmp : res)
		{
			//if(tmp.model.equals("Latitude 7280"))
			//	System.out.println("Here we go");
			tmp.computed_score=0;
			if(tmp.CPU_level>=this.target_Processor_range_min&&tmp.CPU_level<=this.target_Processor_range_max)
			{
				//if(this.target_Processor_range_max==999&&this.target_Processor_range_min!=0)    //only lower limit
				if(this.target_Processor_range_max==999) //no upper limit
					tmp.computed_score=tmp.computed_score+(tmp.CPU_level-this.target_Processor_range_min)*0.7;
				else if(this.target_Price_range_min==0&&this.target_Processor_range_max!=999)
					tmp.computed_score=tmp.computed_score+(this.target_Processor_range_max-tmp.CPU_level)*3.0;
				else
					tmp.computed_score=tmp.computed_score+4;
			}
			else
				tmp.computed_score=tmp.computed_score-2.0;

			if(tmp.RAM_level>=this.target_Memory_rank_range_min&&tmp.RAM_level<=this.target_Memory_rank_range_max)
			{
				//if(this.target_Memory_rank_range_max==999&&this.target_Memory_rank_range_min!=0)    //only lower limit
				if(this.target_Memory_rank_range_max==999)  //no upper limit
					tmp.computed_score=tmp.computed_score+(tmp.RAM_level-this.target_Memory_rank_range_min)*0.4;
				else
					tmp.computed_score=tmp.computed_score+1;
			}
			else
				tmp.computed_score=tmp.computed_score-2.0;

			if(tmp.graphic_level>=this.target_Graphics_range_min&&tmp.graphic_level<=this.target_Graphics_range_max)
			{
				//if(this.target_Graphics_range_max==999&&this.target_Graphics_range_min!=0)    //only lower limit
				if(this.target_Graphics_range_max==999)  //no upper limit
				{
					if(this.special_graphic==false&&tmp.special_use==true)
						tmp.computed_score=tmp.computed_score-2;
					else if(this.special_graphic==true&&tmp.special_use==true)
						tmp.computed_score=tmp.computed_score+4;
					else
						tmp.computed_score=tmp.computed_score+(tmp.graphic_level-this.target_Graphics_range_min)*0.7;
				}
				else
					tmp.computed_score=tmp.computed_score+1;
			}
			else
				tmp.computed_score=tmp.computed_score-2.0;

			if(this.screen_size_range_advised){
				if(tmp.screen_size>=this.target_Screes_size_min&&tmp.screen_size<=this.target_Screes_size_max)
					tmp.computed_score=tmp.computed_score+1;
				else
					tmp.computed_score=tmp.computed_score-2.0;
			}

			if(tmp.screes_resolution_level>=this.target_Resolution_range_min&&tmp.screes_resolution_level<=this.target_Resolution_range_max)
				tmp.computed_score=tmp.computed_score+1;
			else
				tmp.computed_score=tmp.computed_score-2.0;
			if(price_range_advised&&!this.price_limit_effective){
				if(tmp.price_level>=this.target_Price_range_min&&tmp.price_level<=this.target_Price_range_max) {
					tmp.computed_score=tmp.computed_score+4.0;
				}
				else
					tmp.computed_score=tmp.computed_score-Math.max(tmp.price_level-this.target_Price_range_max,this.target_Price_range_min-tmp.price_level)*4.5;
			}
			/*else
				tmp.computed_score=tmp.computed_score-6.5;
			else
				tmp.computed_score=tmp.computed_score-1.5;*/

			if(this.price_limit_effective&&!this.scrren_size_limit_effective)
			{
				if(tmp.price>=this.price_limit_min&&tmp.price<=this.price_limit_max)
				{
					searchResults.add(tmp);
				}
			}
			else if(!this.price_limit_effective&&this.scrren_size_limit_effective)
			{
				if(tmp.screen_size>=this.target_Screes_size_min&&tmp.screen_size<=this.target_Screes_size_max)
				{
					searchResults.add(tmp);
				}
			}
			else if(this.price_limit_effective&&this.scrren_size_limit_effective)
			{
				if(tmp.screen_size>=this.target_Screes_size_min&&tmp.screen_size<=this.target_Screes_size_max&&tmp.price>=this.price_limit_min&&tmp.price<=this.price_limit_max)
				{
					searchResults.add(tmp);
				}
			}
			else if(tmp.computed_score>=0)
			{
				searchResults.add(tmp);
			}
		}

		searchResults.sort(new Comparator<SpecElement>() {
			@Override
			public int compare(SpecElement o1, SpecElement o2) {
				if(o1.computed_score>o2.computed_score)
					return -1;
				if(o1.computed_score<o2.computed_score)
					return 1;
				return 0;
			}
		});

		return searchResults;
	}
}
