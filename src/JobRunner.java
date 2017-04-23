import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by andrew on 4/21/17.
 */
public class JobRunner {
	public HashSet<String> getFileList(String path)
	{
		HashSet<String> res = new HashSet<String>();
		res.clear();

		File target_dir = new File(path);
		if(target_dir.isFile()) {
			return res;
		}

		File[] files = target_dir.listFiles();
		for(File iter_file:files)
		{
			res.add(iter_file.getAbsolutePath());
		}

		return res;
	}

	public HashSet<String> hrefExtractor(String content)
	{
		int i,j;
		int pin_start, pin_end;
		HashSet<String> hrefs = new HashSet<String>();

		i=0;
		while(i<content.length()-4)
		{
			if(content.substring(i,i+4).equals("href")){
				j=i;
				while(content.charAt(j)!='\"') {
					j++;
				}
				j++;
				pin_start=j;
				while(content.charAt(j)!='\"'&&content.charAt(j)!='?'&&content.charAt(j)!='#') {
					j++;
				}
				pin_end=j;

				hrefs.add(content.substring(pin_start,pin_end));
				j++;
				i=j;
				continue;
			}
			i++;
		}

		hrefs.remove("");
		hrefs.remove("&");

		return hrefs;
	}

	public String htmlTextExtractor(String line)
	{
		int i=0;
		String res="";
		int pin_start,pin_end;
		pin_start=pin_end=0;

		while(true){
			if(line.charAt(i)=='>'&&endVerifier(line,i)) {
				pin_start=i+1;
				break;
			}
			else {
				i++;
				if(i==line.length()-1)
					break;
			}
		}
		i=i+2;
		while(i<line.length())
		{
			if(line.charAt(i)=='<')
			{
				pin_end=i;
				break;
			}
			i++;
		}

		return line.substring(pin_start,pin_end);
	}

	public ArrayList<String> htmlTextExtractorArray(String content)
	{
		ArrayList<String> res = new ArrayList<String>();
		int i=0;
		int pin_start=0;
		int pin_end=0;
		int sub_started_flag=0;

		content=content.replaceAll("\n","").replaceAll("\\t+","");
		for(i=0;i<content.length()-1;i++)
		{
			if(content.charAt(i)=='>'&&endVerifier(content,i)) {
				pin_start=i+1;
				sub_started_flag=1;
			}
			if(sub_started_flag==1&&content.charAt(i)=='<')
			{
				pin_end=i;
				sub_started_flag=0;
				res.add(content.substring(pin_start,pin_end));
			}
		}

		return res;
	}

	public Boolean endVerifier(String content, int index)
	{
		char c=content.charAt(index+1);
		if(c=='\n'||c=='\0')
			return false;

		if(c!=' '){
			if(c>='a'&&c<='z')
				return true;
			if(c>='A'&&c<='Z')
				return true;
			if(c>='0'&&c<='9')
				return true;
		}
		else {
			while(content.charAt(index+1)==' '){
				index++;
			}
			if(content.charAt(index+1)!='<')
				return true;
		}

		return false;
	}

	public int wordLocate(ArrayList<QueryTokens> queryTokens, String word)
	{
		int res=-1;
		int i;
		for(i=0;i<queryTokens.size();i++)
		{
			if(queryTokens.get(i).content.equals(word))
				return i;
		}

		return res;
	}

	public HashSet<String> extractJJs(ArrayList<QueryTokens> queryTokens, int index)
	{
		int i;
		HashSet<String> res = new HashSet<String>();

		if(index==0)
			return res;

		for(i=index-1;!queryTokens.get(i).posTag.startsWith("NN")&&i>=0;i--)
		{
			if(queryTokens.get(i).posTag.startsWith("JJ"))
				res.add(queryTokens.get(i).content);
		}

		return res;
	}
}
