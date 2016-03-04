package TestCases;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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


public class TestUnitForDelete {

	private final String DATABASE_URL = "jdbc:sqlite:mem:testCreate";
	private HashMap<String, Integer> cn2nVehicle;
	
	
	public TestUnitForDelete(){
		cn2nVehicle = null;
	}
	
	@BeforeClass
	public static void setUp(){
		System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
	}
	

	public void prepare(int nTuples){
		ConnectionSource connectionSource = null;	
		/**
		 *  create n random MitchellClaim XML String to test createMitchellClaim API
		 */
		String[] testXMLStrings = TestXMLGenerator.generateTestXMLString(nTuples);
		cn2nVehicle = new HashMap<String, Integer>();
		MitchellClaimService mcs = new MitchellClaimService(DATABASE_URL);
		mcs.setUp();
		
		try{

			/**
			 *  initialize ORMlite DAO for writing test data
			 */
			connectionSource = new JdbcConnectionSource(DATABASE_URL);
			
			TableUtils.dropTable(connectionSource, MitchellClaimBasicInfo.class, true);
			TableUtils.createTableIfNotExists(connectionSource, MitchellClaimBasicInfo.class);
			TableUtils.dropTable(connectionSource, MitchellClaimLossInfo.class, true);
			TableUtils.createTableIfNotExists(connectionSource, MitchellClaimLossInfo.class);
			TableUtils.dropTable(connectionSource, MitchellClaimVehicleInfo.class, true);
			TableUtils.createTableIfNotExists(connectionSource, MitchellClaimVehicleInfo.class);
			
			/**
	         * use createMitchellClaim API
	         */
	        mcs.createMitchellClaimN(testXMLStrings);
			
			for (String XMLString : testXMLStrings){
				
				/**
				 *  create reference MitchellClaimType instance
				 */
				JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
	            Unmarshaller un = context.createUnmarshaller();
	            MitchellClaimType mct = ((JAXBElement<MitchellClaimType>) un.unmarshal(new ByteArrayInputStream(XMLString.getBytes(StandardCharsets.UTF_8)))).getValue();
		
	            cn2nVehicle.put(mct.getClaimNumber(), mct.getVehicles().getVehicleDetails().size());   
			}
			
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			mcs.tearDown();
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
	public void testDelete1(){		

		/**
		 *  prepare test environment
		 */
		prepare(1);
		
		MitchellClaimService mcs = new MitchellClaimService(DATABASE_URL);
		mcs.setUp();
		
		for (String key : cn2nVehicle.keySet()){
			
			try {
				List<Long> before = mcs.countOf();
				mcs.deleteByClaimNumber(key);
				List<Long> after = mcs.countOf();
				assertNull(mcs.readByClaimNumber(key));
				assertTrue(before.get(0) == after.get(0)+1);
				assertTrue(before.get(1) == after.get(1)+1);
				assertTrue(before.get(2) == after.get(2)+cn2nVehicle.get(key));
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		mcs.tearDown();
	}
	
	
	@Test
	public void testDelete2(){
		/**
		 * prepare test environment
		 */
		prepare(100);
		
		MitchellClaimService mcs = new MitchellClaimService(DATABASE_URL);
		mcs.setUp();
		
		for (String key : cn2nVehicle.keySet()){
			
			try {
				List<Long> before = mcs.countOf();
				mcs.deleteByClaimNumber(key);
				List<Long> after = mcs.countOf();
				assertNull(mcs.readByClaimNumber(key));
				assertTrue(before.get(0) == after.get(0)+1);
				assertTrue(before.get(1) == after.get(1)+1);
				assertTrue(before.get(2) == after.get(2)+cn2nVehicle.get(key));
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		mcs.tearDown();
		
	}
	
	@AfterClass
	public static void tearDown(){
		
	}
}
