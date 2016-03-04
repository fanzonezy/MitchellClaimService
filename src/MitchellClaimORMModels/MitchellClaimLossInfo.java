package MitchellClaimORMModels;

import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import MitchellClaimServiceUtils.CauseOfLossCode;
import MitchellClaimServiceUtils.MitchellClaimType;

@DatabaseTable(tableName = "MitchellClaimLossInfo")
public class MitchellClaimLossInfo {
	
	public static final String CLAIM_NUMBER_FIELD_NAME = "cla:ClaimNumber";
	public static final String LOSS_DATE_FIELD_NAME = "cla:LossDate";
	public static final String CAUSE_OF_LOSS_FIELD_NAME = "cla:CauseOfLoss";
	public static final String REPORTED_DATE_FIELD_NAME = "cla:ReportedDate";
	public static final String LOSS_DESCRIPTION_FIELD_NAME = "cla:LossDescription";
	
	@DatabaseField(columnName = "cla:ClaimNumber", id = true)
	private String ClaimNumber;
	
	@DatabaseField(columnName = "cla:LossDate")
	private Date LossDate;
	
	@DatabaseField(columnName = "cla:CauseOfLoss")
	private CauseOfLossCode CauseOfLoss;
	
	@DatabaseField(columnName = "cla:ReportedDate")
	private Date ReportedDate;
	
	@DatabaseField(columnName = "cla:LossDescription")
	private String LossDescription;
	
	MitchellClaimLossInfo(){
		
	}
	
	public MitchellClaimLossInfo(String ClaimNumber,
							     XMLGregorianCalendar LossDate,
							     CauseOfLossCode CauseOfLoss,
							     XMLGregorianCalendar ReportedDate,
							     String LossDescription){
		this.ClaimNumber = ClaimNumber;
		this.LossDate = LossDate.toGregorianCalendar().getTime();
		this.CauseOfLoss = CauseOfLoss;
		this.ReportedDate = ReportedDate.toGregorianCalendar().getTime();
		this.LossDescription = LossDescription;
	}
	
	public String getClaimNumber(){
		return this.ClaimNumber;
	}
	
	public void setClaimNumber(String ClaimNumber){
		this.ClaimNumber = ClaimNumber;
	}
	
	public Date getLossDate(){
		return this.LossDate;
	}
	
	public void setLossDate(XMLGregorianCalendar LossDate){
		this.LossDate = LossDate.toGregorianCalendar().getTime();
	}
	
	public CauseOfLossCode getCauseOfLoss(){
		return this.CauseOfLoss;
	}
	
	public void getCauseOfLoss(CauseOfLossCode CauseOfLoss){
		this.CauseOfLoss = CauseOfLoss;
	}
	
	public Date getReportedDate(){
		return this.ReportedDate;
	}
	
	public void setReportedDate(XMLGregorianCalendar ReportedDate){
		this.ReportedDate = ReportedDate.toGregorianCalendar().getTime();
	}
	
	public String getLossDescription(){
		return this.LossDescription;
	}
	
	public void setLossDescription(String LossDescription){
		this.LossDescription = LossDescription;
	}
	
	public void updateWithParsedClaim(MitchellClaimType other){
		
		if (other.getClaimNumber() != null){
			ClaimNumber = other.getClaimNumber();
		}
		
		if (other.getLossDate() != null){
			LossDate = other.getLossDate().toGregorianCalendar().getTime();
		}
		
		if (other.getLossInfo()!= null && other.getLossInfo().getCauseOfLoss() != null){
    		CauseOfLoss = other.getLossInfo().getCauseOfLoss();
    	}
    	
    	if (other.getLossInfo()!= null && other.getLossInfo().getLossDescription() != null){
    		LossDescription = other.getLossInfo().getLossDescription();
    	}
    	
    	if (other.getLossInfo()!= null && other.getLossInfo().getReportedDate() != null){
    		ReportedDate = other.getLossInfo().getReportedDate().toGregorianCalendar().getTime();
    	}
 
	}
	
	public void printAllFields(){
		System.out.println(ClaimNumber);
		System.out.println(CauseOfLoss);
		System.out.println(ReportedDate.toString());
		System.out.println(LossDescription);
	}
}
