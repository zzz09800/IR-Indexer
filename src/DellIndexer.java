import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by andrew on 4/26/17.
 */
public class DellIndexer {
	public HashSet<SpecElement> createIndexFromPage(String page_path)
	{
		JobRunner runner = new JobRunner();
		HashSet<SpecElement> res = new HashSet<SpecElement>();
		res.clear();
		String model_name;
		String content="";
		ArrayList<String> content_array = new ArrayList<String>();

		try{
			File page_in = new File(page_path);
			FileReader fileReader = new FileReader(page_in);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			String tmp;
			while((tmp=bufferedReader.readLine())!=null)
			{
				content=content+"\n"+tmp;
				content_array.add(tmp);
			}

			model_name=this.getSeriesIndntifier(page_path,content_array);
			res=this.cosntructSpecElement(content_array);


		} catch (Exception e) {
			System.out.println("Exception: " + e);
			e.printStackTrace();
		}

		return res;
	}

	public String getSeriesIndntifier(String page_path, ArrayList<String> content_array) {
		if(!page_path.contains("business"))
		{
			int i;
			for(i=0;i<content_array.size();i++)
			{
				if(content_array.get(i).startsWith("\\$"))
				{
					return content_array.get(i-1).trim();
				}
			}
		}
		return "123";
	}


	public HashSet<SpecElement> cosntructSpecElement(ArrayList<String> tokens)
	{
		int i,j,k;

		HashSet<SpecElement> res = new HashSet<SpecElement>();
		SpecElement tmp = new SpecElement();


		for(i=0;i<tokens.size();i++)
		{
			String iter_string = tokens.get(i).trim();
			if(iter_string.startsWith("$")) {
				if(!tmp.CPU_model.equals(""))
					res.add(tmp);
				tmp = new SpecElement();
				String[] tester=iter_string.replaceAll(",","").replaceAll("\\$","").replaceAll("\\s+"," ").split(" ");
				tmp.price=Float.parseFloat(this.hunterLing(tester[0]));
			}else if(iter_string.trim().equals("Processor")) {
				tmp.CPU_model=tokens.get(i+2).substring(0,tokens.get(i+2).indexOf(")")+1);
				tmp.CPU_model=tmp.CPU_model.replaceAll("®","").replaceAll("\\?"," ");
			}else if(iter_string.trim().startsWith("Memory")) {
				this.setMemoryParam(tmp,tokens.get(i+2));
			}else if(iter_string.trim().startsWith("Display")) {
				this.setScreenParam(tmp,tokens.get(i+2));
			}else if(iter_string.trim().startsWith("Video Ca")) {
				this.setGraphicParam(tmp,tokens.get(i+2));
			}

		}

		return res;
	}

	private String hunterLing(String s) {
		int i;
		String res="";
		for(i=0;i<s.length();i++)
		{
			if(s.charAt(i)>='0'&&s.charAt(i)<='9')
				res=res+s.charAt(i);
			if(s.charAt(i)=='.')
				res=res+s.charAt(i);
		}
		return res;
	}

	private void setGraphicParam(SpecElement tmp, String s) {
		s=s.replaceAll("\\?"," ").replaceAll("-"," ").replaceAll("®","").
				replaceAll("\\s+"," ");
		String s_lower=s.toLowerCase();
		if(s_lower.contains("intel"))
		{
			tmp.graphic_model=s;
			return;
		}

		if(s_lower.contains("nvidia"))
		{
			int spiltter = s_lower.lastIndexOf("nvidia",6);
			tmp.graphic_model=s.substring(0,spiltter);
			return;
		}
	}

	private void setScreenParam(SpecElement tmp, String s) {
		s=s.toLowerCase().replaceAll("\\("," ").replaceAll("\\)"," ").
				replaceAll("x"," x ").replaceAll("-"," ").replaceAll("\\s+"," ");
		String[] tokens = s.split(" ");
		int i;

		int set_counter=0;

		for(i=0;i<tokens.length;i++)
		{
			if(set_counter==3)
				return;
			if(StringUtils.isNumeric(tokens[i].replaceAll("\\.",""))) {
				float tester = Float.parseFloat(tokens[i]);
				if(tester<=20)
				{
					tmp.screen_size=tester;
					set_counter++;
				}
				else if(set_counter==1)
				{
					tmp.screen_resolution_x=(int)tester;
					set_counter++;
				}else if(set_counter==2)
				{
					tmp.screen_resolution_y=(int)tester;
					set_counter++;
				}
			}
		}
	}

	private void setMemoryParam(SpecElement tmp, String s) {
		s=s.toLowerCase().replaceAll("gb"," gb").replaceAll("mhz"," mhz").replaceAll("\\s+"," ");
		String[] tokens = s.split(" ");
		int i;
		int set_counter=0;
		for(i=0;i<tokens.length;i++)
		{
			if(set_counter==2)
				return;
			if(StringUtils.isNumeric(tokens[i])&&(i==0||!tokens[i-1].contains("x"))) {
				int tester = Integer.parseInt(tokens[i]);
				if(tester<1333){
					tmp.RAM_size=tester;
					set_counter++;
				}
			}
			else if(tokens[i].toLowerCase().contains("ddr")){
				String verifier = tokens[i].toUpperCase();
				tmp.RAM_type=this.mutalisk(verifier);
				set_counter++;
			}
			else {
				continue;
			}
		}
	}

	private String mutalisk(String s) {
		int i;
		String res="";
		for(i=0;i<s.length();i++)
		{
			if(s.charAt(i)>='0'&&s.charAt(i)<='9')
				res=res+s.charAt(i);
			if(s.charAt(i)=='D')
				res=res+s.charAt(i);
		}
		return res;
	}
}
