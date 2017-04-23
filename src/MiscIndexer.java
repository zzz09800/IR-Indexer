import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by andrew on 4/22/17.
 */
public class MiscIndexer {
	public HashSet<SpecElement> createIndexFromPage(String page_path)
	{
		HashSet<SpecElement> res = new HashSet<SpecElement>();
		res.clear();
		String content="";

		try{
			File page_in = new File(page_path);
			FileReader fileReader = new FileReader(page_in);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			String tmp;
			String url=bufferedReader.readLine();
			while((tmp=bufferedReader.readLine())!=null)
			{
				content=content+"\n"+tmp;
			}
			String content_text= Jsoup.parse(content).text();
			res=this.cosntructSpecElement(content_text);

			if(page_path.toLowerCase().contains("asus")){
				for(SpecElement iterElement:res)
					iterElement.brand="ASUS";
			}else if(page_path.toLowerCase().contains("msi")) {
				for(SpecElement iterElement:res)
					iterElement.brand="MSI";
			}else if(page_path.toLowerCase().contains("gigabyte")) {
				for(SpecElement iterElement:res)
					iterElement.brand="Gigabyte";
			}

			//System.out.println(content_text);
		} catch (Exception e) {
			System.out.println("Exception: " + e);
			e.printStackTrace();
		}

		return res;
	}

	public HashSet<SpecElement> cosntructSpecElement(String content_text)
	{
		HashSet<SpecElement> res = new HashSet<SpecElement>();
		//SpecElement constructElement = new SpecElement();
		ArrayList<String> prices = new ArrayList<String>();
		ArrayList<String> models = new ArrayList<String>();
		ArrayList<String> processors = new ArrayList<String>();
		ArrayList<String> graphics = new ArrayList<String>();
		ArrayList<String> display = new ArrayList<String>();
		ArrayList<String> memory = new ArrayList<String>();
		ArrayList<String> hardDrive = new ArrayList<String>();

		int i;
		int prefix_cut_pin=0;
		int end_cut_pin=0;
		for(i=0;i<content_text.length();i++)
		{
			if(content_text.substring(i).startsWith("Price:"))
			{
				i--;
				for(;content_text.charAt(i)==' ';i--);
				for(;content_text.charAt(i)!=' ';i--);
				i++;
				prefix_cut_pin=i;
				break;
			}
		}
		end_cut_pin=content_text.indexOf("Buy",prefix_cut_pin);
		content_text=content_text.substring(prefix_cut_pin,end_cut_pin);

		String[] tokens = content_text.split("Price:");
		for(i=0;i<tokens.length;i++)
		{
			tokens[i]=tokens[i].trim();
			String[] tmp=tokens[i].split(" ");
			if(tokens[i].startsWith("$"))
			{
				prices.add(tmp[0]);
			}
			if(!tokens[i].endsWith("Cash"))
				models.add(tmp[tmp.length-1]);
			if(tokens[i].length()>64) {
				this.extractor(tokens[i],processors,graphics,display,memory,hardDrive);
			}
		}

		for(i=0;i<processors.size();i++)
		{
			SpecElement constructElement = new SpecElement();
			constructElement.model=models.get(i);
			constructElement.price=Float.parseFloat(prices.get(i).replaceAll(" ","").replaceAll("\\$",""));
			constructElement.CPU_model=processors.get(i);
			constructElement.graphic_model=graphics.get(i);
			constructElement.RAM_type=memory.get(i).split(" ")[1];
			constructElement.RAM_size=Integer.parseInt(memory.get(i).split(" ")[0].toLowerCase().replaceAll("gb",""));
			this.setScreenParam(constructElement,display.get(i));
			res.add(constructElement);
		}

		return res;
	}


	public void setScreenParam(SpecElement element, String token) {
		token=token.replaceAll("\"","").replaceAll("\\(","").replaceAll("\\)","");
		token=token.replaceAll("x", " x ").replaceAll(","," ");
		String[] tokens = token.split(" ");
		int i,j=0;
		for(i=0;i<tokens.length;i++)
		{
			if(StringUtils.isNumeric(tokens[i].replace(".","")))
			{
				if(j==0)
					element.screen_size=Float.parseFloat(tokens[i]);
				if(j==1)
					element.screen_resolution_x=Integer.parseInt(tokens[i]);
				if(j==2)
					element.screen_resolution_y=Integer.parseInt(tokens[i]);
				j++;
			}
		}
	}


	public void extractor(String token,ArrayList<String> processors,ArrayList<String> graphics, ArrayList<String> display,ArrayList<String> memory, ArrayList<String> hardDrive) {
		int i;
		int pin_start=0,pin_end=0;
		String res="";

		String[] tmp=token.split(" ");
		for(i=0;i<tmp.length;i++)
		{
			if(tmp[i].equals("Processor"))
			{
				pin_start=i;
			}
			if(tmp[i].equals("Graphics"))
			{
				pin_end=i;
				break;
			}
		}

		for(i=pin_start+1;i<pin_end;i++)
		{
			res=res+tmp[i]+" ";
		}
		processors.add(res);

		pin_start=pin_end;
		res="";
		for(i=pin_end;i<tmp.length;i++)
		{
			if(tmp[i].equals("Display"))
			{
				pin_end=i;
				break;
			}
		}
		for(i=pin_start+1;i<pin_end;i++)
		{
			res=res+tmp[i]+" ";
		}
		graphics.add(res);

		pin_start=pin_end;
		res="";
		for(i=pin_end;i<tmp.length;i++)
		{
			if(tmp[i].equals("Memory"))
			{
				pin_end=i;
				break;
			}
		}
		for(i=pin_start+1;i<pin_end;i++)
		{
			res=res+tmp[i]+" ";
		}
		display.add(res);

		pin_start=pin_end;
		res="";
		for(i=pin_end;i<tmp.length;i++)
		{
			if(tmp[i].equals("Hard"))
			{
				pin_end=i;
				break;
			}
		}
		for(i=pin_start+1;i<pin_end;i++)
		{
			res=res+tmp[i]+" ";
		}
		memory.add(res);

		res="";
		for(i=pin_end;i<tmp.length;i++)
		{
			if(tmp[i].equals("Hard")&&tmp[i+1].equals("Drive"))
			{
				pin_start=i+1;
			}
			if(tmp[i].equals("Optical"))
			{
				pin_end=i;
				break;
			}
		}
		for(i=pin_start+1;i<pin_end;i++)
		{
			res=res+tmp[i]+" ";
		}
		hardDrive.add(res);
	}
}
