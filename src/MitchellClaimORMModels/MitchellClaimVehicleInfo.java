package MitchellClaimORMModels;

/*** jaxb libraries ***/
import javax.xml.datatype.XMLGregorianCalendar;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import MitchellClaimServiceUtils.MitchellClaimType;
import MitchellClaimServiceUtils.VehicleInfoType;

@DatabaseTable(tableName = "MitchellClaimVehicleInfo")
public class MitchellClaimVehicleInfo {

	public static final String CLAIM_NUMBER_FIELD_NAME = "cla:ClaimNumber";
	public static final String VIN_FIELD_NAME = "cla:Vin";
	public static final String MODEL_YEAR_NAME_FIELD = "cla:ModelYear";
	public static final String MAKE_DESCRIPTION_FIELD_NAME = "cla:MakeDescription";
	public static final String MODEL_DESCRIPTION_FIELD_NAME = "cla:ModelDescription";
	public static final String EMGINE_DESCRIPTION_FIELD_NAME = "cla:EngineDescription";
	public static final String EXTERIOR_COLOR_FIELD_NAME = "cla:ExteriorColor";
	public static final String LIC_PLATE_FIELD_NAME = "cla:LicPlate";
	public static final String LIC_PLATE_STATE_NAME = "cla:LicPlateState";
	public static final String LIC_PLATE_EXP_DATE_NAME = "cla:LicPlateExpDate";
	public static final String DAMAGE_DESCRIPTION_FIELD_NAME = "cla:DamageDescription";
	public static final String MILEAGE_FIELD_NAME = "cla:Mileage";
	
	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField(columnName = "cla:ClaimNumber", canBeNull = false)
	private String ClaimNumber;

	@DatabaseField(columnName = "cla:Vin", canBeNull = false)
	private String Vin;

	@DatabaseField(columnName = "cla:ModelYear", canBeNull = false)
	private int ModelYear;

	@DatabaseField(columnName = "cla:MakeDescription")
	private String MakeDescription;

	@DatabaseField(columnName = "cla:ModeDescription")
	private String ModelDescription;

	@DatabaseField(columnName = "cla:EngineDescription")
	private String EngineDescription;

	@DatabaseField(columnName = "cla:ExteriorColor")
	private String ExteriorColor;

	@DatabaseField(columnName = "cla:LicPlate")
	private String LicPlate;

	@DatabaseField(columnName = "cla:LicPlateState")
	private String LicPlateState;

	@DatabaseField(columnName = "cla:LicPlateExpDate")
	private Date LicPlateExpDate;

	@DatabaseField(columnName = "cla:DamageDescription")
	private String DamageDescription;

	@DatabaseField(columnName = "cla:Mileage")
	private int Mileage;

	MitchellClaimVehicleInfo() {

	}

	public MitchellClaimVehicleInfo(String ClaimNumber, String DamageDescription, String EngineDescription,
			String ExteriorColor, String LicPlate, XMLGregorianCalendar LicPlateExpDate, String LicPlateState,
			String MakeDescription, int Mileage, String ModelDescription, int ModelYear, String Vin) {

		this.ClaimNumber = ClaimNumber;
		this.DamageDescription = DamageDescription;
		this.EngineDescription = EngineDescription;
		this.ExteriorColor = ExteriorColor;
		this.LicPlate = LicPlate;
		this.LicPlateExpDate = LicPlateExpDate.toGregorianCalendar().getTime();
		this.LicPlateState = LicPlateState;
		this.MakeDescription = MakeDescription;
		this.Mileage = Mileage;
		this.ModelDescription = ModelDescription;
		this.ModelYear = ModelYear;
		this.Vin = Vin;
	}

	public String getClaimNumber() {
		return this.ClaimNumber;
	}

	public void setClaimNumber(String ClaimNumber) {
		this.ClaimNumber = ClaimNumber;
	}

	public int getModelYear() {
		return this.ModelYear;
	}

	public void setModelYear(int ModelYear) {
		this.ModelYear = ModelYear;
	}

	public String getMakeDescription() {
		return this.MakeDescription;
	}

	public void setMakeDescription(String MakeDescription) {
		this.MakeDescription = MakeDescription;
	}

	public String getModelDescription() {
		return this.ModelDescription;
	}

	public void setModelDescription(String ModelDescription) {
		this.ModelDescription = ModelDescription;
	}

	public String getEngineDescription() {
		return this.EngineDescription;
	}

	public void setEngineDescription(String EngineDescription) {
		this.EngineDescription = EngineDescription;
	}

	public String getExteriorColor() {
		return this.ExteriorColor;
	}

	public void setExteriorColor(String ExteriorColor) {
		this.ExteriorColor = ExteriorColor;
	}

	public String getLicPlate() {
		return this.LicPlate;
	}

	public void setLicPlate(String LicPlate) {
		this.LicPlate = LicPlate;
	}

	public String getLicPlateState() {
		return this.LicPlateState;
	}

	public void setLicPlateState(String LicPlateState) {
		this.LicPlateState = LicPlateState;
	}

	public Date getLicPlateExpDate() {
		return this.LicPlateExpDate;
	}

	public void setLicPlateExpDate(Date LicPlateExpDate) {
		this.LicPlateExpDate = LicPlateExpDate;
	}

	public String getDamageDescription() {
		return this.DamageDescription;
	}

	public void setDamageDescription(String DamageDescription) {
		this.DamageDescription = DamageDescription;
	}

	public int getMileage() {
		return this.Mileage;
	}

	public void setMileage(int Mileage) {
		this.Mileage = Mileage;
	}

	public String getVin() {
		return this.Vin;
	}

	public void setVin(String Vin) {
		this.Vin = Vin;
	}

	public void updateWithParsedClaim(MitchellClaimType other) {
		if (other.getVehicles() == null){
			return;
		}
		
		for (VehicleInfoType newVehicle : other.getVehicles().getVehicleDetails()) {
	
			if (newVehicle.getVin().equals(Vin)) {
							
				if (newVehicle.getDamageDescription() != null) {
					DamageDescription = newVehicle.getDamageDescription();
				}

				if (newVehicle.getEngineDescription() != null) {
					EngineDescription = newVehicle.getEngineDescription();
				}

				if (newVehicle.getExteriorColor() != null) {
					ExteriorColor = newVehicle.getExteriorColor();
				}

				if (newVehicle.getLicPlate() != null) {
					LicPlate = newVehicle.getLicPlate();
				}

				if (newVehicle.getLicPlateExpDate() != null) {
					LicPlateExpDate = newVehicle.getLicPlateExpDate().toGregorianCalendar().getTime();
				}

				if (newVehicle.getLicPlateState() != null) {
					LicPlateState = newVehicle.getLicPlateState();
				}

				if (newVehicle.getMakeDescription() != null) {
					MakeDescription = newVehicle.getMakeDescription();
				}

				if (newVehicle.getMileage() != null) {
					Mileage = newVehicle.getMileage();
				}

				if (newVehicle.getModelDescription() != null) {
					ModelDescription = newVehicle.getModelDescription();
				}

				if (newVehicle.getModelYear() != null) {
					ModelYear = newVehicle.getModelYear();
				}

				if (newVehicle.getVin() != null) {
					Vin = newVehicle.getVin();
				}
				
				break;
			}
		}
	}

	public void printAllFields() {
		System.out.println(ClaimNumber);
		System.out.println(Vin);
		System.out.println(ExteriorColor);
		System.out.println(EngineDescription);
		System.out.println(DamageDescription);
		System.out.println(LicPlate);
		System.out.println(LicPlateState);
		System.out.println(LicPlateExpDate.toString());
		System.out.println(ModelDescription);
		System.out.println(ModelYear);
		System.out.println(Mileage);
	}
}
