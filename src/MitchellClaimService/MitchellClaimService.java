package MitchellClaimService;


import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Callable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import MitchellClaimORMModels.*;
import MitchellClaimServiceUtils.*;

public class MitchellClaimService {
	
	public MitchellClaimService(String databaseUrl) {
		this.DATABASE_URL = databaseUrl;
	}
	
	/**
	 * 1. set up connection source
	 * 2. set up DAO  
	 */
	public void setUp(){
		try{ 
			connectionSource = new JdbcConnectionSource(DATABASE_URL);
			MitchellClaimBasicInfoDAO = DaoManager.createDao(connectionSource, MitchellClaimBasicInfo.class);
			MitchellClaimLossInfoDAO = DaoManager.createDao(connectionSource, MitchellClaimLossInfo.class);
			MitchellClaimVehicleInfoDAO = DaoManager.createDao(connectionSource, MitchellClaimVehicleInfo.class);
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("An Exception occurs while set up service, Aborting...");
		}finally {
			try {
				if (connectionSource != null){
					connectionSource.close();
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 1. clear connection
	 */
	public void tearDown(){
		try {
			if (connectionSource != null){
				connectionSource.close();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * return the counts of tuples in three table(For test purpose)
	 * @return
	 * @throws SQLException
	 */
	public List<Long> countOf() throws SQLException{
		List<Long> counts = new ArrayList<Long>();
		
		counts.add(MitchellClaimBasicInfoDAO.countOf());
		counts.add(MitchellClaimLossInfoDAO.countOf());
		counts.add(MitchellClaimVehicleInfoDAO.countOf());
		
		return counts;
	}
	
	/** 
	 * Assembler: assemble a VehicleInfoType instance.
	 * @param vehicleInfo_t
	 * @return
	 */
	public static VehicleInfoType toVehicleInfoType(MitchellClaimVehicleInfo vehicleInfo_t){
		VehicleInfoType vehicleInfo = new VehicleInfoType();
		
		vehicleInfo.setDamageDescription(vehicleInfo_t.getDamageDescription());
		vehicleInfo.setEngineDescription(vehicleInfo_t.getEngineDescription());
		vehicleInfo.setExteriorColor(vehicleInfo_t.getExteriorColor());
		vehicleInfo.setLicPlate(vehicleInfo_t.getLicPlate());
		
		Date gcDate = vehicleInfo_t.getLicPlateExpDate();
		GregorianCalendar expDateCalendar = new GregorianCalendar();
		expDateCalendar.setTime(gcDate);
        	XMLGregorianCalendar expDate = null;
        	try {
            		expDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(expDateCalendar);
        	} catch (Exception e) {
            		e.printStackTrace();
        	}
		vehicleInfo.setLicPlateExpDate(expDate);
		
		vehicleInfo.setLicPlateState(vehicleInfo_t.getLicPlateState());
		vehicleInfo.setMakeDescription(vehicleInfo_t.getMakeDescription());
		vehicleInfo.setMileage(vehicleInfo_t.getMileage());
		vehicleInfo.setModelDescription(vehicleInfo_t.getModelDescription());
		vehicleInfo.setModelYear(vehicleInfo_t.getModelYear());
		vehicleInfo.setVin(vehicleInfo_t.getVin());
		
		return vehicleInfo;
	}
	
	/**
	 * Assembler： assemble tuples extracted to a MitchellClaimType instance
	 * @param basicInfo_t
	 * @param lossInfo_t
	 * @param lvehicleInfo_t
	 * @return
	 */
	public static MitchellClaimType toMitchellClaimType(MitchellClaimBasicInfo basicInfo_t, 
							    MitchellClaimLossInfo lossInfo_t,
							    List<MitchellClaimVehicleInfo> lvehicleInfo_t){
		MitchellClaimType mc = new MitchellClaimType();
		
		/** set return data type **/
		mc.setClaimNumber(basicInfo_t.getClaimNumber());
		mc.setClaimantFirstName(basicInfo_t.getClaimantFirstName());
		mc.setClaimantLastName(basicInfo_t.getClaimantLastName());
		mc.setAssignedAdjusterID(basicInfo_t.getAssignedAdjustID());
		mc.setStatus(basicInfo_t.getStatus());
		
		/** set loss date **/
		GregorianCalendar lossDateCalendar = new GregorianCalendar();
		lossDateCalendar.setTime(basicInfo_t.getLossDate());
        	XMLGregorianCalendar lossDate = null;
        	try {
            		lossDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(lossDateCalendar);
        	} catch (Exception e) {
            		e.printStackTrace();
        	}
		mc.setLossDate(lossDate);
		
		/*** set lossInfo ***/
		LossInfoType lossInfo = new LossInfoType();
		lossInfo.setCauseOfLoss(lossInfo_t.getCauseOfLoss());
		lossInfo.setLossDescription(lossInfo_t.getLossDescription());
		
		/** set reported date **/
		GregorianCalendar reportedDateCalendar = new GregorianCalendar();
		reportedDateCalendar.setTime(lossInfo_t.getReportedDate());
        	XMLGregorianCalendar reportedDate = null;
        	try {
            		reportedDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(reportedDateCalendar);
        	} catch (Exception e) {
            		e.printStackTrace();
        	}
		lossInfo.setReportedDate(reportedDate);
		mc.setLossInfo(lossInfo);
		
		/*** set vehicle list ***/
		List<VehicleInfoType> tmp = new ArrayList<VehicleInfoType>();
		VehicleListType vehicleList = new VehicleListType();
		for (MitchellClaimVehicleInfo vehicleInfo_t : lvehicleInfo_t){
			tmp.add(toVehicleInfoType(vehicleInfo_t));
		}
		vehicleList.setVehicleDetails(tmp);
		mc.setVehicles(vehicleList);
		
		return mc;
	}
	
	
	
	
	
	/**
	 * creator: persist N claim to back store, which is just for test purpose
	 * @param XMLStrings
	 */
	public void createMitchellClaimN(String[] XMLStrings){
		
		try{
			// parse input XML to JavaBean instance
			JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller un = context.createUnmarshaller();
			for (String XMLString : XMLStrings){
				
				MitchellClaimType mct = ((JAXBElement<MitchellClaimType>) un.unmarshal(new ByteArrayInputStream(XMLString.getBytes(StandardCharsets.UTF_8)))).getValue();
			
				/*** Create basic claim info tuple ***/
				final MitchellClaimBasicInfo claimBasicInfo_t = new MitchellClaimBasicInfo(
										mct.getClaimNumber(),
										mct.getClaimantFirstName(),
										mct.getClaimantLastName(),
										mct.getStatus(),
										mct.getLossDate(),
										mct.getAssignedAdjusterID()
										); 
				
				
				/*** Create loss info tuple ***/
				LossInfoType lossInfo = mct.getLossInfo();
				final MitchellClaimLossInfo lossInfo_t = new MitchellClaimLossInfo(
									 mct.getClaimNumber(),
									 mct.getLossDate(),
									 lossInfo.getCauseOfLoss(),
									 lossInfo.getReportedDate(),
									 lossInfo.getLossDescription()
									);
				
				
				/*** Create vehicle info tuples ***/
				List<VehicleInfoType> vehicleList = mct.getVehicles().getVehicleDetails();
				final List<MitchellClaimVehicleInfo> l_vehicleInfo_t = new ArrayList<MitchellClaimVehicleInfo>();
				for (VehicleInfoType vehicle : vehicleList){
					l_vehicleInfo_t.add(new MitchellClaimVehicleInfo(
								mct.getClaimNumber(),
								vehicle.getDamageDescription(),
								vehicle.getEngineDescription(),
								vehicle.getExteriorColor(),
								vehicle.getLicPlate(),
								vehicle.getLicPlateExpDate(),
								vehicle.getLicPlateState(),
								vehicle.getMakeDescription(),
								vehicle.getMileage(), 
								vehicle.getModelDescription(),
								vehicle.getModelYear(),
								vehicle.getVin())
							     );
				}
				
				
				TransactionManager.callInTransaction(connectionSource,
					new Callable<Void>() {
						public Void call() throws Exception {
							MitchellClaimBasicInfoDAO.create(claimBasicInfo_t);
							MitchellClaimLossInfoDAO.create(lossInfo_t);
							for (MitchellClaimVehicleInfo vehicleInfo_t : l_vehicleInfo_t){
								MitchellClaimVehicleInfoDAO.create(vehicleInfo_t);
							}
							return null;
							}
					});
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Creator: Persist ONE claim into back store
	 * @param XMLString
	 * @throws SQLException 
	 * @throws IllegalArgumentException 
	 */
	public void createMitchellClaim(String XMLString) throws SQLException, IllegalArgumentException{
		if (XMLString == null){
			throw new IllegalArgumentException();
		}
		
		try{
			// parse input XML to JavaBean instance
			JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
            Unmarshaller un = context.createUnmarshaller();
            MitchellClaimType mct = ((JAXBElement<MitchellClaimType>) un.unmarshal(new ByteArrayInputStream(XMLString.getBytes(StandardCharsets.UTF_8)))).getValue();
			
			/*** Create basic claim info tuple ***/
			final MitchellClaimBasicInfo claimBasicInfo_t = new MitchellClaimBasicInfo(
									mct.getClaimNumber(),
									mct.getClaimantFirstName(),
									mct.getClaimantLastName(),
									mct.getStatus(),
									mct.getLossDate(),
									mct.getAssignedAdjusterID()
									); 
			
			
			/*** Create loss info tuple ***/
			LossInfoType lossInfo = mct.getLossInfo();
			final MitchellClaimLossInfo lossInfo_t = new MitchellClaimLossInfo(
								mct.getClaimNumber(),
								mct.getLossDate(),
								lossInfo.getCauseOfLoss(),
								lossInfo.getReportedDate(),
								lossInfo.getLossDescription()
								);
			
			
			/*** Create vehicle info tuples ***/
			List<VehicleInfoType> vehicleList = mct.getVehicles().getVehicleDetails();
			final List<MitchellClaimVehicleInfo> l_vehicleInfo_t = new ArrayList<MitchellClaimVehicleInfo>();
			for (VehicleInfoType vehicle : vehicleList){
				l_vehicleInfo_t.add(new MitchellClaimVehicleInfo(
							mct.getClaimNumber(),
							vehicle.getDamageDescription(),
							vehicle.getEngineDescription(),
							vehicle.getExteriorColor(),
							vehicle.getLicPlate(),
							vehicle.getLicPlateExpDate(),
							vehicle.getLicPlateState(),
							vehicle.getMakeDescription(),
							vehicle.getMileage(), 
							vehicle.getModelDescription(),
							vehicle.getModelYear(),
							vehicle.getVin())
							);
			}
			
			
			TransactionManager.callInTransaction(connectionSource,
				new Callable<Void>() {
					public Void call() throws Exception {
						MitchellClaimBasicInfoDAO.create(claimBasicInfo_t);
						MitchellClaimLossInfoDAO.create(lossInfo_t);
						for (MitchellClaimVehicleInfo vehicleInfo_t : l_vehicleInfo_t){
							MitchellClaimVehicleInfoDAO.create(vehicleInfo_t);
						}
						return null;
					}
				});
			
		}catch(JAXBException e){
			e.printStackTrace();
			System.out.println("Parse XML string failed, Aborting ... ");
		}
	}
		
	/**
	 * Reader: read a claim claim from back store by claim number
	 * @param ClaimNumber
	 * @return
	 */
	public MitchellClaimType readByClaimNumber (String ClaimNumber){		
		
		MitchellClaimType mc = null;
		/**
		 * to avoid evoke SQLException
		 */
		if (ClaimNumber == null){
			return mc;
		}
		
		try {	
			/* query needed tuples from data bases */
			MitchellClaimBasicInfo basicInfo_t = MitchellClaimBasicInfoDAO.queryForId(ClaimNumber);
			MitchellClaimLossInfo lossInfo_t = MitchellClaimLossInfoDAO.queryForId(ClaimNumber);
			List<MitchellClaimVehicleInfo> l_vehicleInfo_t = MitchellClaimVehicleInfoDAO
							.queryForEq(MitchellClaimVehicleInfo.CLAIM_NUMBER_FIELD_NAME, ClaimNumber);
			
			if (basicInfo_t != null && lossInfo_t != null){
				mc = toMitchellClaimType(basicInfo_t, lossInfo_t, l_vehicleInfo_t);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return mc;
	}
	
	/**
	 * Reader: read a list of claims from back store by loss date
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<MitchellClaimType> readByLossDate(Date startDate, Date endDate){
		List<MitchellClaimType> l_mc = new ArrayList<MitchellClaimType>();
		
		try{
			/* get the claims by start and end date */
			QueryBuilder<MitchellClaimLossInfo, String> lossInfoQueryBuilder = MitchellClaimLossInfoDAO.queryBuilder();
			lossInfoQueryBuilder.where()
					.ge(MitchellClaimLossInfo.LOSS_DATE_FIELD_NAME, startDate)
					.and()
					.le(MitchellClaimLossInfo.LOSS_DATE_FIELD_NAME, endDate);
			List<MitchellClaimLossInfo> l_lossInfo_t = lossInfoQueryBuilder.query();
			
			/* get the rest of the information for each claim */
			for (MitchellClaimLossInfo lossInfo_t : l_lossInfo_t){
				
				String ClaimNumber = lossInfo_t.getClaimNumber();
				
				/* get corresponding MitchellBasicInfo tuples */  
				MitchellClaimBasicInfo basicInfo_t = MitchellClaimBasicInfoDAO.queryForId(ClaimNumber);
				
				/* get corresponding MitchellVehicleInfo tuples */
				List<MitchellClaimVehicleInfo> l_vehicleInfo_t = MitchellClaimVehicleInfoDAO
																.queryForEq(MitchellClaimVehicleInfo.CLAIM_NUMBER_FIELD_NAME, ClaimNumber);
				l_mc.add(toMitchellClaimType(basicInfo_t, lossInfo_t, l_vehicleInfo_t));
			}
			
		}catch (Exception e){
			e.printStackTrace();
		}
		return l_mc;
	}
	
	/**
	 * update a claim in back store
	 * @param XMLString
	 * @throws SQLException
	 */
	public void updateClaim(String XMLString) throws SQLException{
		if (XMLString == null){
			return;
		}
		
		try{
			// parse input XML to JavaBean instance
			JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
            		Unmarshaller un = context.createUnmarshaller();
            		final MitchellClaimType mct = ((JAXBElement<MitchellClaimType>) un.unmarshal(new ByteArrayInputStream(XMLString.getBytes(StandardCharsets.UTF_8)))).getValue();
		
            		/** query for existing claim **/
            		final MitchellClaimBasicInfo basicInfo_t = MitchellClaimBasicInfoDAO.queryForId(mct.getClaimNumber());
            		final MitchellClaimLossInfo lossInfo_t = MitchellClaimLossInfoDAO.queryForId(mct.getClaimNumber());
            		final List<MitchellClaimVehicleInfo> l_vehicleInfo_t = MitchellClaimVehicleInfoDAO
            					.queryForEq(MitchellClaimVehicleInfo.CLAIM_NUMBER_FIELD_NAME, mct.getClaimNumber());
            
            
            		TransactionManager.callInTransaction(connectionSource,
				new Callable<Void>() {
					public Void call() throws Exception {
						basicInfo_t.updateWithParsedClaim(mct);
				            	MitchellClaimBasicInfoDAO.update(basicInfo_t);
				            	lossInfo_t.updateWithParsedClaim(mct);
				            	MitchellClaimLossInfoDAO.update(lossInfo_t);
				            	for (MitchellClaimVehicleInfo vehicleInfo_t : l_vehicleInfo_t){
				            		vehicleInfo_t.updateWithParsedClaim(mct);
				            		MitchellClaimVehicleInfoDAO.update(vehicleInfo_t);
				            	}
						return null;
					}
				});
            
  		          
		}catch (JAXBException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Read a vehicle by specifying claim number and VIN
	 * @param ClaimNumber
	 * @param Vin
	 * @return
	 */
	public VehicleInfoType readVecileInfoByClaimNumberAndVin(String ClaimNumber, String Vin){
		VehicleInfoType vehicleInfo = null;
		
		try{
		    /* get corresponding MitchellVehicleInfo tuples */
			QueryBuilder<MitchellClaimVehicleInfo, Void> mcVehicleInfoQueryBuilder = MitchellClaimVehicleInfoDAO.queryBuilder();
			mcVehicleInfoQueryBuilder.where()
		                         	 .eq(MitchellClaimVehicleInfo.CLAIM_NUMBER_FIELD_NAME, ClaimNumber)
									 .and()
									 .eq(MitchellClaimVehicleInfo.VIN_FIELD_NAME, Vin);
									 
			List<MitchellClaimVehicleInfo> l_vehicleInfo_t = mcVehicleInfoQueryBuilder.query();		
			if (l_vehicleInfo_t.size() != 0){
				vehicleInfo = toVehicleInfoType(l_vehicleInfo_t.get(0));
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return vehicleInfo;
	}
	
	/**
	 * Delete a claim by claim number 
	 * @param ClaimNumber
	 * @throws SQLException 
	 */
	public void deleteByClaimNumber(String ClaimNumber) throws SQLException{
		if (ClaimNumber == null){
			return;
		}
		
		final String cn = ClaimNumber;
		
        TransactionManager.callInTransaction(connectionSource,
				new Callable<Void>() {
					public Void call() throws Exception {
						/** delete from basic info table **/
						MitchellClaimBasicInfoDAO.deleteById(cn);
							
						/** delete from loss info table **/
						MitchellClaimLossInfoDAO.deleteById(cn);
							
						/** delete from vehicle info table **/
						DeleteBuilder<MitchellClaimVehicleInfo, Void> mcVehicleInfoQueryBuilder = MitchellClaimVehicleInfoDAO.deleteBuilder();
						mcVehicleInfoQueryBuilder.where()
							                 .eq(MitchellClaimVehicleInfo.CLAIM_NUMBER_FIELD_NAME, cn);
						mcVehicleInfoQueryBuilder.delete();
						return null;
					}
				});
			

	}
	
	public static void main(String args[]){
	}
	
	private final String DATABASE_URL;
	ConnectionSource connectionSource = null;
	private Dao<MitchellClaimBasicInfo, String> MitchellClaimBasicInfoDAO = null;
	private Dao<MitchellClaimLossInfo, String> MitchellClaimLossInfoDAO = null;
	private Dao<MitchellClaimVehicleInfo, Void> MitchellClaimVehicleInfoDAO = null;
	
}
