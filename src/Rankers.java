import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

/**
 * Created by andrew on 4/22/17.
 */
public class Rankers {
	public int computeProcessorRank(HashSet<SpecElement> results, SpecElement element)
	{
		String verifier_input=element.CPU_model.toLowerCase();
		if(verifier_input.contains("i7")&&(verifier_input.contains("hq")||verifier_input.contains("hk"))){
			return 6;
		}
		if(verifier_input.contains("i7")&&!verifier_input.contains("hq")&&!verifier_input.contains("hk")){
			return 5;
		}
		if(verifier_input.contains("i5")){
			return 4;
		}
		if(verifier_input.contains("i3")){
			return 3;
		}
		if(verifier_input.contains("pentium")||verifier_input.contains("Celeron")) {
			return 2;
		}
		if(verifier_input.contains("amd")) {
			return 2;
		}
		if(!verifier_input.contains("intel")||verifier_input.contains("atom")) {
			return 1;
		}
		return 0;   //unranked.
	}

	public int computeGraphicRank(HashSet<SpecElement> results, SpecElement element)
	{
		String verifier_input=element.CPU_model.toLowerCase();
		if(verifier_input.contains("intel")){
			return 1;
		}
		if(verifier_input.contains("gtx")) {
			return 6;
		}
		if(verifier_input.contains("nvidia")&&verifier_input.contains("quadro")){
			return 5;
		}
		if(verifier_input.contains("nvidia")&&verifier_input.contains("geforce")){
			return 4;
		}
		if(verifier_input.contains("radeon")&&verifier_input.contains("shared")){
			return 3;
		}

		return 0;
	}

	public int computeMemoryRank(HashSet<SpecElement> results, SpecElement element)
	{
		int i=0;
		ArrayList<SpecElement> results_array = new ArrayList<SpecElement>();
		for(SpecElement tmp:results)
		{
			results_array.add(tmp);
		}
		results_array.sort(new Comparator<SpecElement>() {
			@Override
			public int compare(SpecElement o1, SpecElement o2) {
				if(o1.RAM_size>o2.RAM_size)
					return -1;
				if(o1.RAM_size<o2.RAM_size)
					return 1;
				return 0;
			}
		});

		int xl_cut=results_array.get((int)(results.size()*0.15)).RAM_size;
		int l_cut=results_array.get((int)(results.size()*0.45)).RAM_size;
		int m_cut=results_array.get((int)(results.size()*0.75)).RAM_size;

		if(element.RAM_size>=xl_cut)
			return 4;
		if(element.RAM_size>=l_cut)
			return 3;
		if(element.RAM_size>=m_cut)
			return 2;
		if(element.RAM_size>0)
			return 1;

		return 0;
	}

	public int computeResolutionRank(HashSet<SpecElement> results, SpecElement element)
	{
		int i=0;
		ArrayList<SpecElement> results_array = new ArrayList<SpecElement>();
		for(SpecElement tmp:results)
		{
			results_array.add(tmp);
		}
		results_array.sort(new Comparator<SpecElement>() {
			@Override
			public int compare(SpecElement o1, SpecElement o2) {
				if(o1.screen_resolution_x>o2.screen_resolution_x)
					return -1;
				if(o1.screen_resolution_x<o2.screen_resolution_x)
					return 1;
				return 0;
			}
		});

		int xl_cut=results_array.get((int)(results.size()*0.05)).screen_resolution_x;
		int l_cut=results_array.get((int)(results.size()*0.45)).screen_resolution_x;
		int m_cut=results_array.get((int)(results.size()*0.75)).screen_resolution_x;

		if(element.screen_resolution_x>=xl_cut)
			return 4;
		if(element.screen_resolution_x>=l_cut)
			return 3;
		if(element.screen_resolution_x>=m_cut)
			return 2;
		if(element.screen_resolution_x>0)
			return 1;

		return 0;
	}

	public int computePriceRank(HashSet<SpecElement> results, SpecElement element)
	{
		int i=0;
		ArrayList<SpecElement> results_array = new ArrayList<SpecElement>();
		for(SpecElement tmp:results)
		{
			results_array.add(tmp);
		}
		results_array.sort(new Comparator<SpecElement>() {
			@Override
			public int compare(SpecElement o1, SpecElement o2) {
				if(o1.price>o2.price)
					return -1;
				if(o1.price<o2.price)
					return 1;
				return 0;
			}
		});

		double xl_cut=results_array.get((int)(results.size()*0.05)).price;
		double l_cut=results_array.get((int)(results.size()*0.15)).price;
		double m_cut=results_array.get((int)(results.size()*0.50)).price;

		if(element.price>=xl_cut)
			return 4;
		if(element.price>=l_cut)
			return 3;
		if(element.price>=m_cut)
			return 2;
		if(element.price>0)
			return 1;

		return 0;
	}
}
