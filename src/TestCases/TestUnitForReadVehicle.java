package TestCases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import MitchellClaimServiceUtils.*;
import MitchellClaimService.*;
import MitchellClaimORMModels.*;

public class TestUnitForReadVehicle {
	private HashMap<String, VehicleInfoType> references;
	private final String DATABASE_URL = "jdbc:sqlite:mem:testCreate";
	
	public TestUnitForReadVehicle(){
		references = null;

	}
	
	@BeforeClass
	public static void setUp(){
		System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
	}
	
	@Before
	public void intilizeDateBaseForTest1(){
		ConnectionSource connectionSource = null;	
		/**
		 *  create 100 random MitchellClaim XML
		 */
		String[] testXMLStrings = TestXMLGenerator.generateTestXMLString(100);
		references = new HashMap<String, VehicleInfoType>();
		
		try{

			/**
			 *  initialize ORMlite data base
			 */
			connectionSource = new JdbcConnectionSource(DATABASE_URL);
			
			TableUtils.dropTable(connectionSource, MitchellClaimBasicInfo.class, true);
			TableUtils.createTableIfNotExists(connectionSource, MitchellClaimBasicInfo.class);
			TableUtils.dropTable(connectionSource, MitchellClaimLossInfo.class, true);
			TableUtils.createTableIfNotExists(connectionSource, MitchellClaimLossInfo.class);
			TableUtils.dropTable(connectionSource, MitchellClaimVehicleInfo.class, true);
			TableUtils.createTableIfNotExists(connectionSource, MitchellClaimVehicleInfo.class);
			
			MitchellClaimService mcService = new MitchellClaimService(DATABASE_URL);
			mcService.setUp();
			
			/**
	         * use createMitchellClaim API
	         */
	        mcService.createMitchellClaimN(testXMLStrings);
			
	        JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
            Unmarshaller un = context.createUnmarshaller();
			for (String XMLString : testXMLStrings){
				
				/**
				 *  create reference MitchellClaimType instance
				 */
				
	            MitchellClaimType mct = ((JAXBElement<MitchellClaimType>) un.unmarshal(new ByteArrayInputStream(XMLString.getBytes(StandardCharsets.UTF_8)))).getValue();
			
	            for (VehicleInfoType vehicle : mct.getVehicles().getVehicleDetails()){
	            	references.put(mct.getClaimNumber()+vehicle.getVin(), vehicle);
	            }
			}
			
			mcService.tearDown();
			
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			try{
				if (connectionSource != null){
					connectionSource.close();
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	
	@Test
	public void testWithNonExisting(){
		
	}
	
	@Test
	public void testWithExisting(){		
		/**
		 *  initialize ORMlite DAO for verifying DB status
		 */
				
		MitchellClaimService mcs = new MitchellClaimService(DATABASE_URL);
		mcs.setUp();
		
		for (String key : references.keySet()){
			VehicleInfoType vif = mcs.readVecileInfoByClaimNumberAndVin(key.substring(0, 32), key.substring(32));
			assertTrue(vif != null);
			assertTrue(vif.equals(references.get(key)));
		}
		mcs.tearDown();
	}
	
	@AfterClass
	public static void tearDown(){
		
	}
}
