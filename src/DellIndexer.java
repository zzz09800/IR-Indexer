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
		HashSet<SpecElement> res = new HashSet<SpecElement>();
		res.clear();
		String model_name="";
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
			//System.out.println(model_name);

		} catch (Exception e) {
			System.out.println("Exception: " + e);
			e.printStackTrace();
		}

		for(SpecElement tmp:res)
		{
			tmp.model=model_name;
			tmp.brand="Dell";
		}

		return res;
	}

	public String getSeriesIndntifier(String page_path, ArrayList<String> content_array) {
		if(!page_path.contains("business"))
		{
			int i;
			for(i=0;i<content_array.size();i++)
			{
				if(content_array.get(i).startsWith("$"))
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
			}else if(iter_string.trim().startsWith("Hard Dri")) {
				this.setHardDriveParam(tmp,tokens.get(i+2));
			}

		}

		return res;
	}

	private void setHardDriveParam(SpecElement tmp, String s) {
		int i;
		String tester = s.toLowerCase();
		int spiltter=-1;
		for(i=1;i<tester.length();i++)
		{
			if(tester.charAt(i)>='0'&&tester.charAt(i)<='9')
			{
				if((tester.charAt(i-1)>'9'||tester.charAt(i-1)<'0')&&tester.charAt(i-1)!='.')
					spiltter=i;
			}
		}

		if(spiltter==-1)
			tmp.hard_drive_info=s;
		else
			tmp.hard_drive_info=s.substring(0,spiltter);
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
			int spiltter = s_lower.indexOf("nvidia",6);
			if(spiltter!=-1)
				tmp.graphic_model=s.substring(0,spiltter);
			else
				tmp.graphic_model=s;
			return;
		}

		if(s_lower.contains("amd"))
		{
			tmp.graphic_model=s;
			return;
		}
	}

	private void setScreenParam(SpecElement tmp, String s) {
		s=s.toLowerCase().replaceAll("\\("," ").replaceAll("\\)"," ").
				replaceAll("x"," x ").replaceAll("-"," ").replaceAll("”","").
				replaceAll("\"","").replaceAll("\\s+"," ");

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
		s=s.toLowerCase().replaceAll("gb"," gb").replaceAll("mhz"," mhz").
				replaceAll("g"," g").replaceAll("\\s+"," ");
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
		int num_count=0;
		for(i=0;i<s.length();i++)
		{
			if(s.charAt(i)>='0'&&s.charAt(i)<='9'&&num_count==0)
			{
				res=res+s.charAt(i);
				num_count++;
			}
			if(s.charAt(i)=='D'||s.charAt(i)=='R')
				res=res+s.charAt(i);
		}
		return res;
	}


	public HashSet<SpecElement> createIndexFromPage_work(String page_path)
	{
		HashSet<SpecElement> res = new HashSet<SpecElement>();
		res.clear();
		String model_name="";
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

			model_name=this.getSeriesIndntifier_work(page_path,content_array);
			if(!model_name.equals(""))
				res=this.cosntructSpecElement_work(model_name,content_array);
			//System.out.println(model_name);

		} catch (Exception e) {
			System.out.println("Exception: " + e);
			e.printStackTrace();
		}

		for(SpecElement tmp:res)
		{
			tmp.model=model_name.replaceAll("New","").trim();
			tmp.brand="Dell";
			if(tmp.graphic_model.equals(""))
			{
				if(tmp.CPU_model.toLowerCase().contains("intel"))
					tmp.graphic_model="Intel Intergated Graphics";
				if(tmp.CPU_model.toLowerCase().contains("amd"))
					tmp.graphic_model="AMD Intergated Graphics";
			}
		}

		return res;
	}

	private HashSet<SpecElement> cosntructSpecElement_work(String model_name, ArrayList<String> tokens) {
		int i,j,k;

		HashSet<SpecElement> res = new HashSet<SpecElement>();
		SpecElement tmp = new SpecElement();
		Boolean started=false;
		String processor_reserve="";
		String display_reserve="";

		for(i=0;i<tokens.size();i++)
		{
			String iter_string = tokens.get(i).trim();
			if(iter_string.contains(model_name)&&this.hydralisk(iter_string)) {
				if(started==true){
					if(tmp.RAM_size==0&&!processor_reserve.equals("")){
						this.setMemoryParam(tmp, processor_reserve);
					}
					if(tmp.screen_size==0)
					{
						this.guessScreensize(tmp,model_name);
						this.setScreenParam_retry(tmp,display_reserve);
					}
					res.add(tmp);
				}
				tmp = new SpecElement();
				tmp.price=Float.parseFloat(iter_string.split("\\$")[1].replaceAll(",",""));
				started=true;
			}
			else if(iter_string.toLowerCase().equals("processor")&&started==true) {
				tmp.CPU_model=tokens.get(i+1);
				processor_reserve=tokens.get(i+1);
			}
			else if (iter_string.toLowerCase().startsWith("memory")&&started==true){
				this.setMemoryParam(tmp,iter_string.replaceAll("Memory[0-3]"," ").trim());
			}
			else if(iter_string.toLowerCase().startsWith("hard dri")&&started==true){
				tmp.hard_drive_info=iter_string.replaceAll("Hard Drive","").trim();
			}
			else if(iter_string.toLowerCase().startsWith("graphics car")&&started==true) {
				tmp.graphic_model=iter_string.replaceAll("Graphics Card","").trim();
			}
			else if(iter_string.toLowerCase().startsWith("display")&&started==true) {
				this.setScreenParam(tmp,iter_string.replaceAll("Display"," ").replaceAll("\\s+"," ").trim());
				display_reserve=iter_string.replaceAll("Display"," ").replaceAll("\\s+"," ").trim();
			}
		}

		return res;
	}

	private void setScreenParam_retry(SpecElement tmp, String s) {
		s=s.toLowerCase().replaceAll("\\("," ").replaceAll("\\)"," ").
				replaceAll("x"," x ").replaceAll("-"," ").replaceAll("”","").
				replaceAll("\"","").replaceAll("\\s+"," ");

		String[] tokens = s.split(" ");
		int i;

		int set_counter=0;

		for(i=0;i<tokens.length;i++)
		{
			if(set_counter==2)
				return;
			if(StringUtils.isNumeric(tokens[i].replaceAll("\\.",""))) {
				float tester = Float.parseFloat(tokens[i]);
				if(tester>1000)
				{
					tmp.screen_resolution_x=(int)tester;
					set_counter++;
				}
				else if(set_counter==1&&tester>600)
				{
					tmp.screen_resolution_y=(int)tester;
					set_counter++;
				}
			}
		}
	}

	private void guessScreensize(SpecElement tmp, String model_name) {
		String[] tokens = model_name.split(" ");
		int i;
		for(i=0;i<tokens.length;i++)
		{
			if(StringUtils.isNumeric(tokens[i]))
			{
				if(tokens[i].charAt(1)=='2'){
					tmp.screen_size=12.5;
					return;
				}
				if(tokens[i].charAt(1)=='3'){
					tmp.screen_size=13.3;
					return;
				}
				if(tokens[i].charAt(1)=='5'){
					tmp.screen_size=15.6;
					return;
				}
				if(tokens[i].charAt(1)=='7'){
					tmp.screen_size=17.3;
					return;
				}

			}
		}
	}

	private boolean hydralisk(String iter_string) {
		String tester=iter_string.toLowerCase();
		if(tester.contains("save")||tester.contains("limited time")||tester.contains("plus"))
			return false;
		return true;
	}

	private String getSeriesIndntifier_work(String page_path, ArrayList<String> content_array) {
		int i;
		String res;
		if(page_path.contains("inspiron"))
		{
			for(i=0;i<content_array.size();i++)
			{
				String tester = content_array.get(i);
				if(tester.toLowerCase().contains("inspiron")&&tester.contains("$"))
				{
					res=tester.split("\\$")[0].trim();
					return res;
				}
			}
		}

		if(page_path.contains("latitude"))
		{
			for(i=0;i<content_array.size();i++)
			{
				String tester = content_array.get(i);
				if(tester.toLowerCase().contains("latitude")&&tester.contains("$"))
				{
					res=tester.split("\\$")[0].trim();
					return res;
				}
			}
		}

		if(page_path.contains("precision"))
		{
			for(i=0;i<content_array.size();i++)
			{
				String tester = content_array.get(i);
				if(tester.toLowerCase().contains("precision")&&tester.contains("$"))
				{
					res=tester.split("\\$")[0].trim();
					return res;
				}
			}
		}

		return "";
	}
}
