package MitchellClaimORMModels;

import java.util.Date;

/*** jaxb libraries ***/
import javax.xml.datatype.XMLGregorianCalendar;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import MitchellClaimServiceUtils.MitchellClaimType;
import MitchellClaimServiceUtils.StatusCode;

@DatabaseTable(tableName = "MitchellClaimBasicInfo")
public class MitchellClaimBasicInfo {
	
	public static final String CLAIM_NUMBER_FIELD_NAME = "cla:ClaimNumber";
	public static final String CLAIMANT_FIRSTNAME_FIELD_NAME = "cla:ClaimantFirstName";
	public static final String CLAIMANT_LASTNAME_FIELD_NAME = "cla:ClaimantLastName";
	public static final String STATUS_FIELD_NAME = "cla:Status";
	public static final String LOSS_DATE_FIELD_NAME = "cla:LossDate";
	public static final String ASSIGNED_ADJUESTER_ID_FIELD_NAME = "cla:AssignedAdjusterID";
	
	@DatabaseField(columnName = "cla:ClaimNumber", id = true)
	private String ClaimNumber;
	
	@DatabaseField(columnName = "cla:ClaimantFirstName")
	private String ClaimantFirstName;
	
	@DatabaseField(columnName = "cla:ClaimantLastName")
	private String ClaimantLastName;
	
	@DatabaseField(columnName = "cla:Status")
	private StatusCode Status;
	
	@DatabaseField(columnName = "cla:LossDate")
	private Date LossDate;
	
	@DatabaseField(columnName = "cla:AssignedAdjusterID")
	private long AssignedAdjusterID;
	
	
	MitchellClaimBasicInfo(){
		
	}
	
	public MitchellClaimBasicInfo(String ClaimNumber,
								  String ClaimantFirstName,
								  String ClaimantLastName,
								  StatusCode Status,
								  XMLGregorianCalendar LossDate,
								  long AssignedAdjusterID){
		
        this.ClaimNumber = ClaimNumber;
        this.ClaimantFirstName = ClaimantFirstName;
        this.ClaimantLastName = ClaimantLastName;
        this.Status = Status;
        this.LossDate = LossDate.toGregorianCalendar().getTime();
        this.AssignedAdjusterID = AssignedAdjusterID;          
	}
	
	public String getClaimNumber(){
		return this.ClaimNumber;
	}
	
	public void setClaimNumber(String ClaimNumber){
		this.ClaimNumber = ClaimNumber;
	}
	
	public String getClaimantFirstName(){
		return this.ClaimantFirstName;
	}
	
	public void setClaimantFirstName(String ClaimantFirstName){
		this.ClaimantFirstName = ClaimantFirstName;
	}
	
	public String getClaimantLastName(){
		return this.ClaimantLastName;
	}
	
	public void setClaimantLastName(String ClaimantLastName){
		this.ClaimantLastName = ClaimantLastName;
	}
	
	public StatusCode getStatus(){
		return this.Status;
	}
	
	public void setStatus(StatusCode Status){
		this.Status = Status;
	}
	
	public Date getLossDate(){
		return this.LossDate;
	}
	
	public void setLossDate(XMLGregorianCalendar LossDate){
		this.LossDate = LossDate.toGregorianCalendar().getTime();
	}
	
	public long getAssignedAdjustID(){
		return this.AssignedAdjusterID;
	}
	
	public void setAssignedAdjusterID(long AssignedAdjusterID){
		this.AssignedAdjusterID = AssignedAdjusterID;
	}
	
	public void updateWithParsedClaim(MitchellClaimType mcType){
		if (mcType.getAssignedAdjusterID() != null){
    		AssignedAdjusterID = mcType.getAssignedAdjusterID();
    	}
    	
    	if (mcType.getClaimantFirstName() != null){
    		ClaimantFirstName = mcType.getClaimantFirstName();
    	}
    	
    	if (mcType.getClaimantLastName() != null){
    		ClaimantLastName = mcType.getClaimantLastName();
    	}
    	
    	if (mcType.getClaimNumber() != null){
    		ClaimNumber = mcType.getClaimNumber();
    	}
    	
    	if (mcType.getLossDate() != null){
    		LossDate =  mcType.getLossDate().toGregorianCalendar().getTime();
    	}
    	   	
    	if (mcType.getStatus() != null){
    		Status = mcType.getStatus();
    	}
    	
	}
	
	public void printAllFields(){
		System.out.println("ClaimNumber: " + ClaimNumber);
		System.out.println("ClaimantFirstName: " + ClaimantFirstName);
		System.out.println("ClaimantLastName: " + ClaimantLastName);
		System.out.println("Status: " + Status);
		System.out.println("AssignedAdjusterID: " + AssignedAdjusterID);
		System.out.println("LossDate: " + LossDate.toString());
	}
}

