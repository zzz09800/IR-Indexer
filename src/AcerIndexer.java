import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by andrew on 4/21/17.
 */
public class AcerIndexer {
	public HashSet<SpecElement> createIndexFromPage(String page_path)
	{
		JobRunner runner = new JobRunner();
		HashSet<SpecElement> res = new HashSet<SpecElement>();
		res.clear();
		String series_name="";
		String content="";
		ArrayList<String> content_array = new ArrayList<String>();

		try{
			File page_in = new File(page_path);
			FileReader fileReader = new FileReader(page_in);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			String tmp;
			String url=bufferedReader.readLine();
			while((tmp=bufferedReader.readLine())!=null)
			{
				content=content+"\n"+tmp;
				content_array.add(tmp);
			}
			series_name=this.getSeriesIndntifier(url,content_array);
			String productListHTML= Jsoup.parse(content).select("div.product-list").html();
			ArrayList<String> tokens = runner.htmlTextExtractorArray(productListHTML);

			//for(String stp:tokens)
				//System.out.println(stp);
			res=this.cosntructSpecElement(tokens);

		} catch (Exception e) {
			System.out.println("Exception: " + e);
			e.printStackTrace();
		}

		for(SpecElement tmp:res)
		{
			tmp.model=series_name+" "+tmp.model;
			tmp.brand="Acer";
		}

		return res;
	}

	public String getSeriesIndntifier(String url, ArrayList<String> content)
	{
		String[] tokens = url.split("/");
		String url_pot=tokens[tokens.length-1];
		String line_pot="";

		int i;

		for(i=0;i<content.size();i++)
		{
			if(content.get(i).contains(url_pot))
			{
				line_pot=content.get(i);
				break;
			}
		}

		line_pot=new JobRunner().htmlTextExtractor(line_pot);

		return line_pot;
	}

	public HashSet<SpecElement> cosntructSpecElement(ArrayList<String> tokens)
	{
		int i,j,k;
		boolean subflag;

		HashSet<SpecElement> res = new HashSet<SpecElement>();
		SpecElement tmp = new SpecElement();

		subflag=false;
		for(i=0;i<tokens.size();i++)
		{
			String iter_string = tokens.get(i);
			if(iter_string.contains("@")||iter_string.contains("#"))
				continue;
			if(this.isModelType(iter_string)) {
				tmp.model=iter_string;
			}else if(isCPUType(iter_string)){
				tmp.CPU_model=iter_string.replaceAll("®","").replaceAll("\u0099","");
			}else if(this.isScreenType(iter_string)){
				this.setScreenParam(iter_string,tmp);
			}else if(this.isGraphicType(iter_string)){
				tmp.graphic_model=iter_string.replaceAll("®","");
			}else if(this.isMemoryType(iter_string)){
				this.setRAMParam(iter_string,tmp);
			}else if(this.isHardDriveType(iter_string))
			{
				tmp.hard_drive_info=iter_string;
			}else if(this.isPriceType(iter_string)) {
				tmp.price = new Float(iter_string.trim().replaceAll("\\$", "").trim());
				if(tmp.graphic_model.equals("")){
					tmp.graphic_model="Intel HD Graphics";
				}
				res.add(tmp);
				tmp = new SpecElement();
			}
		}

		return res;
	}

	public Boolean isModelType(String token) {
		Pattern pattern = Pattern.compile("[A-Za-z0-9]+-[A-Za-z0-9]+-[A-Za-z0-9]+");
		Matcher m = pattern.matcher(token);
		if(m.matches())
			return true;

		pattern = Pattern.compile("[A-Za-z0-9]+-[A-Za-z0-9]+");
		m = pattern.matcher(token);
		if(m.matches())
			return true;

		return false;
	}


	public Boolean isPriceType(String token) {
		if(token.contains("$"))
			return true;

		return false;
	}

	public Boolean isHardDriveType(String token){
		if(token.contains("SSD")||token.contains("HDD"))
			return true;

		return false;
	}

	public void setRAMParam(String token, SpecElement element){
		token=token.trim();
		String[] tokens = token.split(" ");
		element.RAM_size=new Integer(tokens[0]);
		element.RAM_type=tokens[2];
	}

	public Boolean isMemoryType(String token)
	{
		if(token.contains("DDR")||token.contains("SDRAM"))
			return true;

		return false;
	}

	public Boolean isOSType(String token)
	{
		if(token.contains("Windows")||token.contains("Linux")||token.contains("Ubuntu"))
			return true;
		else
			return false;
	}

	public Boolean isCPUType(String token)
	{
		token=token.toLowerCase();
		if(token.contains("intel") && !token.contains("graphic") && !token.contains("shared"))
			return true;
		if(token.contains("amd")&&!token.contains("radeon"))
			return true;
		if((token.contains("dual")||token.contains("quad"))&&token.contains("core"))
			return true;

		return false;
	}

	public Boolean isScreenType(String token)
	{
		if(token.contains("1600")||token.contains("1366")||token.contains("1920")||token.contains("2560"))
			return true;
		if(token.contains("Full HD")||token.contains("QHD")||token.contains("IPS"))
			return true;

		return false;
	}

	public void setScreenParam(String token, SpecElement element)
	{
		token=token.replaceAll("\"","").replaceAll("\\(","").replaceAll("\\)","");
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

	public Boolean isGraphicType(String token)
	{
		if(token.contains("Graphics")||token.contains("NVIDIA")||token.contains("GeForce")||token.contains("GT")||token.contains("GTX")||token.contains("Radeon"))
			return true;

		return false;
	}
}
