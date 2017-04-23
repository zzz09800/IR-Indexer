/**
 * Created by andrew on 4/21/17.
 */
public class SpecElement {
	String brand,model;
	double price;
	String internal_callsign;
	int price_level;

	String CPU_model;
	int CPU_level;  // 0 = unassigned, 1 2 3 4 5 increases

	String RAM_type;
	int RAM_size;   // in GB
	int RAM_level;

	String graphic_model;
	int graphic_level; // 0 = unassigned, 1 2 3 4 5 increases

	String hard_drive_info;

	int screen_resolution_x;
	int screen_resolution_y;
	int screes_resolution_level;
	double screen_size;

	double computed_score;

	public SpecElement()
	{
		this.brand="";
		this.model="";
		this.internal_callsign="";
		this.price=0.00;

		this.CPU_model="";
		this.CPU_level=0;  // 0 = unassigned, 1 2 3 4 5 increases

		this.RAM_type="";
		this.RAM_size=0;   // in GB
		this.RAM_level=0;

		this.graphic_model="";
		this.graphic_level=0; // 0 = unassigned, 1 2 3 4 5 increases

		this.screen_resolution_x=0;
		this.screen_resolution_y=0;
		this.screes_resolution_level=0;
		this.screen_size=0;
	}
}
